package Lennert_Bontinck_SA1

// Required imports
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{RunnableGraph, Keep}

import scala.concurrent.{ExecutionContextExecutor, Future}

/** This is the main app that can be run to execute the code for the first assignment. */
object Main extends App {

  // --------------------------------------------------------------------------------------
  // | Setup Actor System
  // --------------------------------------------------------------------------------------

  // "Default" setup from WPOs, meaning 1 dispatcher per actor and same ActorMaterializer.
  implicit val actorSystem: ActorSystem = ActorSystem("Lennert-Bontinck-SA1-ActorSystem")
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()



  // --------------------------------------------------------------------------------------
  // | Make main Runnable Graph of project
  // --------------------------------------------------------------------------------------

  /** Runnable Graph using the Maven Dependencies object list as source per requirement of the assignment. */
  val runnableGraph: RunnableGraph[Future[IOResult]] =
    MavenDependenciesSource.source
      // Create sub streams by grouping on library name
      //    Max amount of sub streams is Int.MAX per requirement of the assignment.
      .groupBy(maxSubstreams = Int.MaxValue, _.library)

      // Push the sub streams through the FlowDependenciesShape Flow Shape.
      .via(FlowDependenciesShapeParallel.flowMavenDependencyToMavenDependencyCountParallel)

      // Merge the sub streams back to a regular (singular) stream
      .mergeSubstreams

      // Convert to ByteString for saving
      .via(StringToByteEncoder.flowStringToByteString)

      // Save to save sink
      // NOTE:  There were some issues with using "to" instead of "toMat".
      //        This was resolved after communication with the TA's.
      .toMat(Sinks.saveSink)(Keep.right)



  // --------------------------------------------------------------------------------------
  // | Execute main Runnable Graph of project
  // --------------------------------------------------------------------------------------

  // Run graph and terminate on completion
  runnableGraph.run().onComplete(_ => actorSystem.terminate())
}
