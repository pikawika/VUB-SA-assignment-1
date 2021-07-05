package Lennert_Bontinck_SA1

// Required imports
import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, RunnableGraph}
import akka.stream.ActorMaterializer
import scala.concurrent.{ExecutionContextExecutor, Future}

/** This is an alternative app that can be run to execute the code for the first assignment but display results rather than print them. */
object MainDisplay extends App {

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
  //change Future to Done if using dummy sink, to IOResult if using save sink
  val runnableGraph: RunnableGraph[Future[Done]] =
  Sources.sourceOfMavenDependency

    // Make use of flowMavenDependencyToMavenDependencyCount to go from
    // MavenDependency source to collected dependencies.
    .via(Flows.flowMavenDependencySourceToCollectedMavenDependencyCount)

    // Display textual statistics of dependencies
    .alsoToMat(Sinks.textualMavenDependencyStatisticsDisplaySink)(Keep.right)

    // Display textual representation of dependency count
    .toMat(Sinks.textualMavenDependencyCountDisplaySink)(Keep.right)



  // --------------------------------------------------------------------------------------
  // | Execute main Runnable Graph of project
  // --------------------------------------------------------------------------------------

  // Run graph and terminate on completion
  runnableGraph.run().onComplete(_ => actorSystem.terminate())
}
