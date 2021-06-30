package Lennert_Bontinck_SA1

// Required imports
import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink}
import akka.stream.ActorMaterializer
import scala.concurrent.{ExecutionContextExecutor, Future}

/** This is the main loop used to execute the code for the first assignment. */
object MainLoopDisplay extends App {
  // Needed implicit values to work with AKKA streams and runnabla graphs.
  // "Default" setup, meaning 1 dispatcher per actor and same ActorMaterializer from WPOs.
  implicit val actorSystem: ActorSystem = ActorSystem("Lennert-Bontinck-SA1-ActorSystem")
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()

  // --------------------- START Help ---------------------
  // For unzip see session 5 -> Q3 (GZ, not actual ZIP!)
  // Source[Out, Materializer]
  // Flow[In, Out, Materializer]
  // Sink[In, Materializer]
  // --------------------- END Help ---------------------

  // --------------------- START Sinks ---------------------
  /** Dummy sink that prints its input. */
  val dummySink: Sink[MavenLibraryDependencyCount, Future[Done]] = Sink.foreach(println)

  // --------------------- END Sinks ---------------------

  // --------------------- START runnable graph ---------------------
  /** Runnable Graph using the Maven Dependencies object list as source per requirement of the assignment. */
    //change Future to Done if using dummy sink, to IOResult if using save sink
  val runnableGraph: RunnableGraph[Future[Done]] =
    MavenDependenciesSource.source
      // Create substreams grouped which contain all records for a library.
      //    Max substreams = Int.MAX per requirement of the assignment.
      .groupBy(maxSubstreams = Int.MaxValue, _.library)

      // Push the substreams to the flow dependencies Flow Shape.
      .via(FlowDependenciesShape.flowDependencies)

      // Merge the substreams back to a regular stream
      .mergeSubstreams

      // Display output
      .toMat(dummySink)(Keep.right)

  runnableGraph.run().onComplete(_ => actorSystem.terminate())

  // --------------------- END runnable graph ---------------------
}
