package Lennert_Bontinck_SA1
// ok

// Required imports

import akka.NotUsed
import akka.stream.{FlowShape, Graph}
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge}
import GraphDSL.Implicits._

/** Object containing the custom flow shape responsible for counting the number of dependencies in a parallel way. */
object FlowDependenciesShapeParallel {
  /** Custom flow shape that takes MavenDependency object(s) as input,
   * counts it's dependencies and returns MavenLibraryDependencyCount object(s) as output. */
  val flowMavenDependencyToMavenDependencyCountParallel: Graph[FlowShape[MavenDependency, MavenDependencyCount], NotUsed] =
    Flow.fromGraph(GraphDSL.create() { implicit builder =>

      // The shape has a balanced approach to send groups of dependencies of the same library to the two pipelines.
      val balancer = builder.add(Balance[MavenDependency](2))
      val merger = builder.add(Merge[MavenDependencyCount](2))

      // After merging the pipelines, the resulting MavenDependencyCount (MDC) object should be the combination of both pipelines
      //    NOTE: Assumes input MavenDependencyCount object(s) are of same library (equal name)
      val flowMultipleMavenDependencyCountsToSingle: Flow[MavenDependencyCount, MavenDependencyCount, NotUsed] =
      Flow[MavenDependencyCount]
        // Make single MavenLibraryDependencyCount object.
        //    Does this by Merging "existing" and "new" MavenDependencyCount objects.
        .fold(MavenDependencyCount())((existingMDC, newMDC) => existingMDC.mergeMavenDependencyCount(newMDC))

      // Provide the previously made flowMultipleMavenDependencyCountsToSingle to the builder
      val toSingleMavenDependencyCount = builder.add(flowMultipleMavenDependencyCountsToSingle)

      // Specify the custom flow shape
      balancer ~> FlowDependenciesShape.flowMavenDependencyToMavenDependencyCount.async ~> merger
      balancer ~> FlowDependenciesShape.flowMavenDependencyToMavenDependencyCount.async ~> merger ~> toSingleMavenDependencyCount

      // The final flow shape that returns the single resulting MavenDependencyCount per input
      FlowShape(balancer.in, toSingleMavenDependencyCount.out)
    })
}
