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

      // Broadcaster and merger, we make 4 pipelines, 1 per dependency as illustrated in the figure of the assignment.
      val broadcast = builder.add(Broadcast[MavenDependency](4))
      val merge = builder.add(Merge[MavenDependencyCount](4))


      // --------------------- START filter dependency type ---------------------
      // Filters to filter (determine) which type of dependency is inputted
      val flowComputeCompileDependency: Flow[MavenDependency, MavenDependencyCount, NotUsed] =
      Flow[MavenDependency].filter(dependency => dependency.dependency_type.toLowerCase == "compile")
        .map(dependency => {
          // Create objects from the record using the alternative constructor
          //    from the companion object of MavenDependency
          MavenDependencyCount(dependency.library, 1, 0, 0, 0)
        })

      val flowComputeProvidedDependency: Flow[MavenDependency, MavenDependencyCount, NotUsed] =
        Flow[MavenDependency].filter(dependency => dependency.dependency_type.toLowerCase == "provided")
          .map(dependency => {
            // Create objects from the record using the alternative constructor
            //    from the companion object of MavenDependency
            MavenDependencyCount(dependency.library, 0, 1, 0, 0)
          })

      val flowComputeRuntimeDependency: Flow[MavenDependency, MavenDependencyCount, NotUsed] =
        Flow[MavenDependency].filter(dependency => dependency.dependency_type.toLowerCase == "runtime")
          .map(dependency => {
            // Create objects from the record using the alternative constructor
            //    from the companion object of MavenDependency
            MavenDependencyCount(dependency.library, 0, 0, 1, 0)
          })

      val flowComputeTestDependency: Flow[MavenDependency, MavenDependencyCount, NotUsed] =
        Flow[MavenDependency].filter(dependency => dependency.dependency_type.toLowerCase == "test")
          .map(dependency => {
            // Create objects from the record using the alternative constructor
            //    from the companion object of MavenDependency
            MavenDependencyCount(dependency.library, 0, 0, 0, 1)
          })
      // --------------------- END filter dependency type ---------------------


      // --------------------- START create single ---------------------
      // Provide the flowMultipleMavenDependencyCountsToSingle to the builder
      //    Provided in Flows object for easy reuse
      val toSingleMavenDependencyCount = builder.add(Flows.flowMultipleMavenDependencyCountsToSingle)
      // --------------------- END create single ---------------------


      // --------------------- START pipeline ---------------------
      broadcast ~> flowComputeCompileDependency ~> merge
      broadcast ~> flowComputeProvidedDependency ~> merge
      broadcast ~> flowComputeRuntimeDependency ~> merge
      broadcast ~> flowComputeTestDependency ~> merge ~> toSingleMavenDependencyCount

      // Custom flow shape
      FlowShape(broadcast.in, toSingleMavenDependencyCount.out)
      // --------------------- END pipeline ---------------------
    }
  )
}
