package Lennert_Bontinck_SA1

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

      // --------------------------------------------------------
      // | Configure balancer and merger
      // --------------------------------------------------------

      val balancer = builder.add(Balance[MavenDependency](2))
      val merger = builder.add(Merge[MavenDependencyCount](2))



      // --------------------------------------------------------
      // | Create single MavenDependencyCount
      // --------------------------------------------------------

      /** Provides the flowMultipleMavenDependencyCountsToSingle to the builder. */
      val toSingleMavenDependencyCount = builder.add(Flows.flowMultipleMavenDependencyCountsToSingle)



      // --------------------------------------------------------
      // | Create pipeline
      // --------------------------------------------------------

      // Uses flowMavenDependencyToMavenDependencyCount from FlowDependenciesShape to do the actual counting
      balancer ~> FlowDependenciesShape.flowMavenDependencyToMavenDependencyCount.async ~> merger
      balancer ~> FlowDependenciesShape.flowMavenDependencyToMavenDependencyCount.async ~> merger ~> toSingleMavenDependencyCount



      // --------------------------------------------------------
      // | Make flow shape
      // --------------------------------------------------------

      FlowShape(balancer.in, toSingleMavenDependencyCount.out)
    })
}
