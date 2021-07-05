package Lennert_Bontinck_SA1

// Required imports
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{Keep, RunnableGraph}

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
    Sources.sourceOfMavenDependency

      // Make use of flowMavenDependencySourceToCollectedMavenDependencyCount to go from
      // MavenDependency source to collected dependencies.
      .via(Flows.flowMavenDependencySourceToCollectedMavenDependencyCount)

      // Materialize to save sink that stores textual representation of dependency count
      .toMat(Sinks.textualMavenDependencyCountSaveSink)(Keep.right)



  // --------------------------------------------------------------------------------------
  // | Execute main Runnable Graph of project
  // --------------------------------------------------------------------------------------

  // Run graph and terminate on completion
  runnableGraph.run().onComplete(_ => actorSystem.terminate())
}
