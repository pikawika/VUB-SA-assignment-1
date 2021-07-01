package Lennert_Bontinck_SA1

// Required imports

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, RunnableGraph, Sink}
import akka.util.ByteString
import java.nio.file.StandardOpenOption._

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

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

  // --------------------- START Sinks ---------------------
  /** Sink that saves its input, override existing file. */
  val saveSink: Sink[ByteString, Future[IOResult]] =
    FileIO.toPath(Paths.get("src/main/resources/result/Lennert-Bontinck-SA1-output.txt"), Set(WRITE, TRUNCATE_EXISTING, CREATE))

  // --------------------- END Sinks ---------------------

  // --------------------- START runnable graph ---------------------
  /** Runnable Graph using the Maven Dependencies object list as source per requirement of the assignment. */
    //change Future to Done if using dummy sink, to IOResult if using save sink
  val runnableGraph: RunnableGraph[Future[IOResult]] =
    MavenDependenciesSource.source
      // Create substreams grouped which contain all records for a library.
      //    Max substreams = Int.MAX per requirement of the assignment.
      .groupBy(maxSubstreams = Int.MaxValue, _.library)

      // Push the substreams to the flow dependencies Flow Shape.
      .via(FlowDependenciesShape.flowDependencies)

      // Merge the substreams back to a regular stream
      .mergeSubstreams

      // Save output
      .via(StringToByteEncoder.flowStringToByteString)
      .to(saveSink)

  //Using the following, the stream will not terminate but file will save OK
  //runnableGraph.run()

  //using the following, the stream will terminate but the file will not save
  //.onComplete(_ => actorSystem.terminate())

  //Waiting 10 seconds will make the file saved ?
  val materialized = runnableGraph.run()

  materialized.onComplete {
    case Success(_) =>
      Thread.sleep(5000)
      actorSystem.terminate()
    case Failure(e) =>
      println(s"Failure: ${e.getMessage}")
      actorSystem.terminate()
  }

  // --------------------- END runnable graph ---------------------
}
