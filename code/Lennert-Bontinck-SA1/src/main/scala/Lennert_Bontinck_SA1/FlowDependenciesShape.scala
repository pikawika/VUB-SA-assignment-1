package Lennert_Bontinck_SA1

import akka.NotUsed
import akka.stream.{FlowShape, Graph, OverflowStrategy}
import akka.stream.scaladsl.{Balance, Broadcast, Flow, GraphDSL, Merge}
import GraphDSL.Implicits._

object FlowDependenciesShape {
  val flowDependencies: Graph[FlowShape[MavenDependency, MavenDependency], NotUsed] = Flow.fromGraph(
    GraphDSL.create() {implicit builder =>

      val balancer = builder.add(Balance[MavenDependency](2))
      val merger = builder.add(Merge[MavenDependency](2))

      balancer.out(0) ~> FlowDependenciesCounterShape.flowDependenciesCounter.async ~> merger.in(0)
      balancer.out(1) ~> FlowDependenciesCounterShape.flowDependenciesCounter.async ~> merger.in(1)

      FlowShape(balancer.in, merger.out)
    }
  )
}
