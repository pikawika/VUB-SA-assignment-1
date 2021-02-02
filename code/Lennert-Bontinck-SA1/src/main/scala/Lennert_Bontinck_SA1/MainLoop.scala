package Lennert_Bontinck_SA1

// Required imports
import akka.{Done}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer}
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink}

import scala.concurrent.{ExecutionContextExecutor, Future}

/** This is the main loop used to execute the code for the first assignment. */
object MainLoop extends App {
  // Needed implicit values to work with AKKA streams and runnabla graphs.
  // "Default" setup, meaning 1 dispatcher per actor and same ActorMaterializer from WPOs.
  implicit val actorSystem: ActorSystem = ActorSystem("Lennert-Bontinck-SA1-ActorSystem")
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()

  // HELP
  // For unzip see session 5 -> Q3 (GZ, not actual ZIP!)
  // Source[Out, Materializer]
  // Flow[In, Out, Materializer]
  // Sink[In, Materializer]

  // --------------------- START Global variables ---------------------
  /** Dummy sink that prints its input. */
  val dummySink: Sink[MavenDependency, Future[Done]] = Sink.foreach((x:MavenDependency) => println("single input: " + x))

  // --------------------- END Global variables ---------------------



  // --------------------- START runnable graph ---------------------
  /** Runnable Graph using the Maven Dependencies object list as source per requirement of the assignment.  */
  val runnableGraph: RunnableGraph[Future[Done]] = MavenDependenciesSource.source
    .toMat(dummySink)(Keep.right)

  runnableGraph.run().foreach(_ => actorSystem.terminate())

  // --------------------- END runnable graph ---------------------
}
