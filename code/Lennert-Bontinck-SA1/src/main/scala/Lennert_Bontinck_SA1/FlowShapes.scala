package Lennert_Bontinck_SA1

// Required imports
import akka.NotUsed
import akka.stream.{FlowShape, Graph}
import akka.stream.scaladsl.{Balance, Broadcast, Flow, GraphDSL, Merge}
import GraphDSL.Implicits._

/** Object having the required Flow Shapes for the assignment.
 * - flowMavenDependencyToMavenDependencyCount
 * - flowMavenDependencyToMavenDependencyCountParallel */
object FlowShapes {

  // --------------------------------------------------------
  // | Singular Dependency flow/pipeline for counting dependencies
  // --------------------------------------------------------
  // This Flow Shape will convert the input MavenDependency object(s)
  //    to MavenDependencyCount object(s) in simple pipeline manner.
  //    It corresponds with a pipeline within the Flow
  //    Dependencies box in the assignment figure.

  /** Custom flow shape that takes MavenDependency object(s) as input,
   * counts it's dependencies and returns MavenDependencyCount object(s) as output. */
  val flowMavenDependencyToMavenDependencyCount: Graph[FlowShape[MavenDependency, MavenDependencyCount], NotUsed] = Flow.fromGraph(
    GraphDSL.create() { implicit builder =>

      // -------------------- Configure broadcast and merger --------------------

      val broadcast = builder.add(Broadcast[MavenDependency](4))
      val merger = builder.add(Merge[MavenDependencyCount](4))


      // -------------------- Filter dependency type --------------------

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


      // -------------------- Create single MavenDependencyCount --------------------

      /** Provides the flowMultipleMavenDependencyCountsToSingle to the builder. */
      val toSingleMavenDependencyCount = builder.add(Flows.flowMultipleMavenDependencyCountsToSingle)


      // -------------------- Create pipeline --------------------

      broadcast ~> flowComputeCompileDependency ~> merger
      broadcast ~> flowComputeProvidedDependency ~> merger
      broadcast ~> flowComputeRuntimeDependency ~> merger
      broadcast ~> flowComputeTestDependency ~> merger ~> toSingleMavenDependencyCount


      // -------------------- Make flow shape --------------------

      FlowShape(broadcast.in, toSingleMavenDependencyCount.out)
    })





  // --------------------------------------------------------
  // | Parallel Dependency flow for counting dependencies
  // --------------------------------------------------------
  // This Flow Shape will convert the input MavenDependency object(s)
  //    to MavenDependencyCount object(s) in a parallel manner.
  //    It corresponds with the "Flow Dependencies" box of
  //    the assignment figure.

  /** Custom flow shape that takes MavenDependency object(s) as input,
   * counts it's dependencies and returns MavenDependencyCount object(s) as output. */
  val flowMavenDependencyToMavenDependencyCountParallel: Graph[FlowShape[MavenDependency, MavenDependencyCount], NotUsed] =
    Flow.fromGraph(GraphDSL.create() { implicit builder =>

      // -------------------- Configure balancer and merger --------------------

      val balancer = builder.add(Balance[MavenDependency](2))
      val merger = builder.add(Merge[MavenDependencyCount](2))


      // -------------------- Create single MavenDependencyCount --------------------

      /** Provides the flowMultipleMavenDependencyCountsToSingle to the builder. */
      val toSingleMavenDependencyCount = builder.add(Flows.flowMultipleMavenDependencyCountsToSingle)


      // -------------------- Create pipeline --------------------

      // Uses flowMavenDependencyToMavenDependencyCount from FlowDependenciesShape to do the actual counting
      balancer ~> flowMavenDependencyToMavenDependencyCount.async ~> merger
      balancer ~> flowMavenDependencyToMavenDependencyCount.async ~> merger ~> toSingleMavenDependencyCount


      // -------------------- Make flow shape --------------------

      FlowShape(balancer.in, toSingleMavenDependencyCount.out)
    })
}