package Lennert_Bontinck_SA1

// Required imports
import akka.NotUsed
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge}
import akka.stream.{FlowShape, Graph}

/** Object containing the custom flow shape responsible for counting the number of dependencies. */
object FlowDependenciesShape {
  /** Custom flow shape that takes MavenDependency object(s) as input,
   * counts it's dependencies and returns MavenLibraryDependencyCount object(s) as output. */
  val flowMavenDependencyToMavenDependencyCount: Graph[FlowShape[MavenDependency, MavenDependencyCount], NotUsed] = Flow.fromGraph(
    GraphDSL.create() { implicit builder =>

      // Broadcaster and merger
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
      // Convert stream of Maven Library Dependency Count objects to single Maven Library Dependency Count object
      val formSingleMavenLibraryDependencyCount: Flow[MavenDependencyCount, MavenDependencyCount, NotUsed] = Flow[MavenDependencyCount]
        // Make single object that adds all of the previously received classes
        .fold(MavenDependencyCount())((crt_object, input_object) => crt_object.mergeMavenDependencyCount(input_object))
        // Filter out objects that don't have any value
        .filter(depCount => depCount.test != 0 || depCount.runtime != 0 || depCount.provided != 0 || depCount.compile != 0)

      val toSingleCountingObject = builder.add(formSingleMavenLibraryDependencyCount)
      // --------------------- END create single ---------------------


      // --------------------- START pipeline ---------------------
      broadcast ~> flowComputeCompileDependency ~> merge
      broadcast ~> flowComputeProvidedDependency ~> merge
      broadcast ~> flowComputeRuntimeDependency ~> merge
      broadcast ~> flowComputeTestDependency ~> merge ~> toSingleCountingObject

      // Custom flow shape
      FlowShape(broadcast.in, toSingleCountingObject.out)
      // --------------------- END pipeline ---------------------
    }
  )
}
