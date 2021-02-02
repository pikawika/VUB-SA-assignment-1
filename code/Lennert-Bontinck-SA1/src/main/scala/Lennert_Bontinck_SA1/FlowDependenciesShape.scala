package Lennert_Bontinck_SA1

import akka.NotUsed
import akka.stream.{FlowShape, Graph}
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge}
import GraphDSL.Implicits._

object FlowDependenciesShape {
  val flowDependencies: Graph[FlowShape[MavenDependency, String], NotUsed] = Flow.fromGraph(
    GraphDSL.create() { implicit builder =>

      val balancer = builder.add(Balance[MavenDependency](2))
      val merger = builder.add(Merge[MavenLibraryDependencyCount](2))

      // Convert stream of Maven Library Dependency Count objects to single Maven Library Dependency Count object
      val formDependencyCountString: Flow[MavenLibraryDependencyCount, String, NotUsed] = Flow[MavenLibraryDependencyCount]
        // Make single object that adds all of the previously received classes
        .fold(MavenLibraryDependencyCount())((crt_object, input_object) => crt_object.addDependency(input_object))
        // Filter out objects that don't have any value
        .filter(depCount => depCount.test != 0 || depCount.runtime != 0 || depCount.provided != 0 || depCount.compile != 0)
        .map(dependencyCount => {
          dependencyCount.library + " -> " +
            "Compile: " + dependencyCount.compile + " " +
            "Provided: " + dependencyCount.provided + " " +
            "Runtime: " + dependencyCount.runtime + " " +
            "Test: " + dependencyCount.test + "."
        })

      val toDependencyCountString = builder.add(formDependencyCountString)

      balancer ~> FlowDependenciesCounterShape.flowDependenciesCounter.async ~> merger
      balancer ~> FlowDependenciesCounterShape.flowDependenciesCounter.async ~> merger ~> toDependencyCountString

      FlowShape(balancer.in, toDependencyCountString.out)
    }
  )
}
