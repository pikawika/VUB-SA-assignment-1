package Lennert_Bontinck_SA1

// Required imports
import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, RunnableGraph}
import akka.stream.ActorMaterializer
import scala.concurrent.{ExecutionContextExecutor, Future}

/** This is an alternative app that can be run to execute the code for the first assignment but display results rather than print them. */
object MainDisplay extends App {
  // Needed implicit values to work with AKKA streams and runnabla graphs.
  // "Default" setup, meaning 1 dispatcher per actor and same ActorMaterializer from WPOs.
  implicit val actorSystem: ActorSystem = ActorSystem("Lennert-Bontinck-SA1-ActorSystem")
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()

  // --------------------- START runnable graph ---------------------
  /** Runnable Graph using the Maven Dependencies object list as source per requirement of the assignment. */
  //change Future to Done if using dummy sink, to IOResult if using save sink
  val runnableGraph: RunnableGraph[Future[Done]] =
  MavenDependenciesSource.source
    // Create substreams grouped which contain all records for a library.
    //    Max substreams = Int.MAX per requirement of the assignment.
    .groupBy(maxSubstreams = Int.MaxValue, _.library)

    // Push the substreams to the flow dependencies Flow Shape.
    .via(FlowDependenciesShapeParallel.flowMavenDependencyToMavenDependencyCountParallel)

    // Merge the substreams back to a regular stream
    .mergeSubstreams

    // Display output
    .toMat(Sinks.displaySink)(Keep.right)

  runnableGraph.run().onComplete(_ => actorSystem.terminate())

  // --------------------- END runnable graph ---------------------
}
