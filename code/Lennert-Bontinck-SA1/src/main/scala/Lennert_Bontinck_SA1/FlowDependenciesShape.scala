package Lennert_Bontinck_SA1

// Required imports
import akka.NotUsed
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge}
import akka.stream.{FlowShape, Graph}

/** Object containing the custom flow shape responsible for counting the number of dependencies. */
object FlowDependenciesShape {
  /** Custom flow shape that takes MavenDependency object(s) as input,
   * counts it's dependencies and returns MavenDependencyCount object(s) as output. */
  val flowMavenDependencyToMavenDependencyCount: Graph[FlowShape[MavenDependency, MavenDependencyCount], NotUsed] = Flow.fromGraph(
    GraphDSL.create() { implicit builder =>

      // --------------------------------------------------------
      // | Configure broadcast and merger
      // --------------------------------------------------------

      val broadcast = builder.add(Broadcast[MavenDependency](4))
      val merger = builder.add(Merge[MavenDependencyCount](4))



      // --------------------------------------------------------
      // | Filter dependency type
      // --------------------------------------------------------

      /** Flow responsible for making MavenDependencyCount (MDC) object(s) for
       * input MavenDependency (MD) object(s) that are "compile" type. */
      val flowComputeCompileDependency: Flow[MavenDependency, MavenDependencyCount, NotUsed] =
        Flow[MavenDependency]
          // Only keep MD that is "compile" type
          .filter(MD => MD.dependency_type.toLowerCase == "compile")
          // Create MDC object for the singular MD, these will be merged later on
          .map(MD => {
            MavenDependencyCount(library = MD.library, compile = 1)
          })


      /** Flow responsible for making MavenDependencyCount (MDC) object(s) for
       * input MavenDependency (MD) object(s) that are "provided" type. */
      val flowComputeProvidedDependency: Flow[MavenDependency, MavenDependencyCount, NotUsed] =
        Flow[MavenDependency]
          // Only keep MD that is "provided" type
          .filter(MD => MD.dependency_type.toLowerCase == "provided")
          // Create MDC object for the singular MD, these will be merged later on
          .map(MD => {
            MavenDependencyCount(library = MD.library, provided = 1)
          })


      /** Flow responsible for making MavenDependencyCount (MDC) object(s) for
       * input MavenDependency (MD) object(s) that are "runtime" type. */
      val flowComputeRuntimeDependency: Flow[MavenDependency, MavenDependencyCount, NotUsed] =
        Flow[MavenDependency]
          // Only keep MD that is "provided" type
          .filter(MD => MD.dependency_type.toLowerCase == "runtime")
          // Create MDC object for the singular MD, these will be merged later on
          .map(MD => {
            MavenDependencyCount(library = MD.library, runtime = 1)
          })


      /** Flow responsible for making MavenDependencyCount (MDC) object(s) for
       * input MavenDependency (MD) object(s) that are "test" type. */
      val flowComputeTestDependency: Flow[MavenDependency, MavenDependencyCount, NotUsed] =
        Flow[MavenDependency]
          // Only keep MD that is "provided" type
          .filter(MD => MD.dependency_type.toLowerCase == "test")
          // Create MDC object for the singular MD, these will be merged later on
          .map(MD => {
            MavenDependencyCount(library = MD.library, test = 1)
          })



      // --------------------------------------------------------
      // | Create single MavenDependencyCount
      // --------------------------------------------------------

      /** Provides the flowMultipleMavenDependencyCountsToSingle to the builder. */
      val toSingleMavenDependencyCount = builder.add(Flows.flowMultipleMavenDependencyCountsToSingle)



      // --------------------------------------------------------
      // | Create pipeline
      // --------------------------------------------------------

      broadcast ~> flowComputeCompileDependency ~> merger
      broadcast ~> flowComputeProvidedDependency ~> merger
      broadcast ~> flowComputeRuntimeDependency ~> merger
      broadcast ~> flowComputeTestDependency ~> merger ~> toSingleMavenDependencyCount



      // --------------------------------------------------------
      // | Make flow shape
      // --------------------------------------------------------

      FlowShape(broadcast.in, toSingleMavenDependencyCount.out)
    }
  )
}
