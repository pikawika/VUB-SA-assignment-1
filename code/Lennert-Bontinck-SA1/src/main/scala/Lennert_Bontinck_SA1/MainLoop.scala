package Lennert_Bontinck_SA1

// Required imports

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}

import scala.concurrent.{ExecutionContextExecutor, Future}

/** This is the main loop used to execute the code for the first assignment. */
object MainLoop extends App {
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

  // --------------------- START Global variables ---------------------
  /** Dummy sink that prints its input. */
  val dummySink: Sink[String, Future[Done]] = Sink.foreach(println)

  // --------------------- END Global variables ---------------------


  // --------------------- START runnable graph ---------------------
  /** Runnable Graph using the Maven Dependencies object list as source per requirement of the assignment. */
  val runnableGraph: RunnableGraph[Future[Done]] = {
    // Get source of Maven Dependencies (already converted to objects)
    MavenDependenciesSource.source
      // Create substreams grouped which contain all records for a library.
      //    Max substreams = Int.MAX per requirement of the assignment.
      .groupBy(maxSubstreams = Int.MaxValue, _.library)
      // Push the substreams to the flow dependencies Flow Shape.
      .via(FlowDependenciesShape.flowDependencies)
      // Merge the substreams back to a regular stream
      .mergeSubstreams
      .toMat(dummySink)(Keep.right)
  }

  runnableGraph.run().foreach(_ => actorSystem.terminate())

  // --------------------- END runnable graph ---------------------
}
