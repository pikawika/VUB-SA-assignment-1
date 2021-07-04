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
   * counts it's dependencies and returns MavenDependencyCount object(s) as output. */
  val flowMavenDependencyToMavenDependencyCountParallel: Graph[FlowShape[MavenDependency, MavenDependencyCount], NotUsed] =
    Flow.fromGraph(GraphDSL.create() { implicit builder =>

      // The shape has a balanced approach to send groups of dependencies of the same library to the two pipelines.
      val balancer = builder.add(Balance[MavenDependency](2))
      val merger = builder.add(Merge[MavenDependencyCount](2))

      // Provide the flowMultipleMavenDependencyCountsToSingle to the builder
      //    Provided in Flows object for easy reuse
      val toSingleMavenDependencyCount = builder.add(Flows.flowMultipleMavenDependencyCountsToSingle)

      // Specify the custom flow shape
      balancer ~> FlowDependenciesShape.flowMavenDependencyToMavenDependencyCount.async ~> merger
      balancer ~> FlowDependenciesShape.flowMavenDependencyToMavenDependencyCount.async ~> merger ~> toSingleMavenDependencyCount

      // The final flow shape that returns the single resulting MavenDependencyCount per input
      FlowShape(balancer.in, toSingleMavenDependencyCount.out)
    })
}
