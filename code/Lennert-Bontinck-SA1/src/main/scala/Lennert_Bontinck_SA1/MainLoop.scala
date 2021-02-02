package Lennert_Bontinck_SA1

// Required imports

import java.nio.charset.StandardCharsets
import java.nio.file.{Path, Paths}

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Flow, Keep, RunnableGraph, Sink, Source}
import akka.util.ByteString
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}

import scala.concurrent.{ExecutionContextExecutor, Future}

/** This is the main loop used to execute the code for the first assignment. */
object MainLoop extends App {
  // Needed implicit values to work with AKKA streams and runnabla graphs.
  // "Default" setup, meaning 1 dispatcher per actor and same ActorMaterializer from WPOs.
  implicit val actorSystem: ActorSystem = ActorSystem("Lennert-Bontinck-SA1-ActorSystem")
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()

  // TODO
  // For unzip see session 5 -> Q3 (GZ, not actual ZIP!)

  // Source[Out, Materializer]
  // Flow[In, Out, Materializer]
  // Sink[In, Materializer]


  // ---------------------START file to list of objects---------------------
  val pathExtractedFileFolder: String = "src/main/resources"
  val pathExtractedFile: Path = Paths.get(s"$pathExtractedFileFolder/maven_dependencies_first_1000.txt")

  /** Make raw ByteString source from input file */
  val sourceExtractedFile: Source[ByteString, Future[IOResult]] = FileIO.fromPath(pathExtractedFile)

  /** Flow to convert raw Bytestring to a list of Bytestrings, using CSV Parser */
  val flowCsvParsing: Flow[ByteString, List[ByteString], NotUsed] = CsvParsing.lineScanner()

  /** Flow to convert a CSV parsed list of Bytestrings to a map of strings containing: library, dependency and type */
  val flowCsvMapper: Flow[List[ByteString], Map[String, String], NotUsed] =
    CsvToMap.withHeadersAsStrings(StandardCharsets.UTF_8, "library", "dependency", "dependency_type")

  /** Flow to convert a map of strings containing: library, dependency and type to a MavenDependency object */
  val flowMappedCsvToMavenDependency: Flow[Map[String, String], MavenDependency, NotUsed] = Flow[Map[String, String]].
    map(dependency => {
      MavenDependency(dependency)
    })

  /** Dummy sink that prints the input */
  val dummySink: Sink[MavenDependency, Future[Done]] = Sink.foreach((x:MavenDependency) => println("single input: " + x))

  val runnableGraph: RunnableGraph[Future[Done]] = sourceExtractedFile
    .via(flowCsvParsing)
    .via(flowCsvMapper)
    .via(flowMappedCsvToMavenDependency)
    .toMat(dummySink)(Keep.right)

  runnableGraph.run().foreach(_ => actorSystem.terminate())

}
