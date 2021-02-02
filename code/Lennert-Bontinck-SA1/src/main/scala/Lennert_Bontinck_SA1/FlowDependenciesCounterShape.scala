package Lennert_Bontinck_SA1

import akka.NotUsed
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge}
import akka.stream.{FlowShape, Graph, OverflowStrategy}

object FlowDependenciesCounterShape {
  val flowDependenciesCounter: Graph[FlowShape[MavenDependency, MavenDependency], NotUsed] = Flow.fromGraph(
    GraphDSL.create() {implicit builder =>
      // Broadcaster and merger
      val broadcast = builder.add(Broadcast[MavenDependency](4))
      val merge = builder.add(Merge[MavenDependency](4))

      // Filters to filter (determine) which type of dependency is inputted
      val flowComputeCompileDependency: Flow[MavenDependency, MavenDependency, NotUsed] =
        Flow[MavenDependency].filter(dependency => dependency.dependency_type.toLowerCase == "compile")

      val flowComputeProvidedDependency: Flow[MavenDependency, MavenDependency, NotUsed] =
        Flow[MavenDependency].filter(dependency => dependency.dependency_type.toLowerCase == "provided")

      val flowComputeRuntimeDependency: Flow[MavenDependency, MavenDependency, NotUsed] =
        Flow[MavenDependency].filter(dependency => dependency.dependency_type.toLowerCase == "runtime")

      val flowComputeTestDependency: Flow[MavenDependency, MavenDependency, NotUsed] =
        Flow[MavenDependency].filter(dependency => dependency.dependency_type.toLowerCase == "test")

      // Create pipeline
      broadcast ~> flowComputeCompileDependency ~> merge
      broadcast ~> flowComputeProvidedDependency ~> merge
      broadcast ~> flowComputeRuntimeDependency ~> merge
      broadcast ~> flowComputeTestDependency ~> merge


      // Custom flow shape
      FlowShape(broadcast.in, merge.out)
    }
  )
}
