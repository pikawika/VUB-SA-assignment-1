package Lennert_Bontinck_SA1

// Required imports

import akka.Done
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink}

import java.nio.file.Paths
import java.nio.file.StandardOpenOption.{CREATE, TRUNCATE_EXISTING, WRITE}
import scala.concurrent.Future

/** Object having the required sinks for the assignment.
 * - textualMavenDependencyCountSaveSink
 * - textualMavenDependencyCountDisplaySink */
object Sinks {

  // --------------------------------------------------------
  // | Textual MavenDependencyCount object(s) save sink
  // --------------------------------------------------------
  // This sink will encode MavenDependencyCount object(s)
  // to the required textual ByteString representation
  // and store it to the disk.

  /** Sink that saves input MavenDependencyCount object(s) to disk in a textual manner */
  val textualMavenDependencyCountSaveSink: Sink[MavenDependencyCount, Future[IOResult]] =
    Flow[MavenDependencyCount]
      .via(Flows.flowMavenDependencyCountToTextualByteString)
      .toMat(FileIO.toPath(Paths.get("src/main/resources/result/Lennert-Bontinck-SA1-dependencies.txt"), Set(WRITE, TRUNCATE_EXISTING, CREATE)))(Keep.right)





  // --------------------------------------------------------
  // | Textual MavenDependencyCount object(s) save sink
  // --------------------------------------------------------
  // This sink will save MavenDependencyStatistics in the required
  //    textual representation.

  /** Sink that displays input MavenDependencyCount object(s) to terminal in a textual manner */
  val textualMavenDependencyStatisticsSaveSink: Sink[MavenDependencyCount, Future[IOResult]] =
    Flow[MavenDependencyCount]
      .via(Flows.flowMavenDependencyCountToMavenDependencyStatistics)
      .via(Flows.flowMavenDependencyStatisticsToTextualByteString)
      .toMat(FileIO.toPath(Paths.get("src/main/resources/result/Lennert-Bontinck-SA1-statistics.txt"), Set(WRITE, TRUNCATE_EXISTING, CREATE)))(Keep.right)





  // --------------------------------------------------------
  // | Textual MavenDependencyCount object(s) display sink
  // --------------------------------------------------------
  // This sink will display MavenDependencyCount in the required
  //    textual representation.

  /** Sink that displays input MavenDependencyCount object(s) to terminal in a textual manner */
  val textualMavenDependencyCountDisplaySink: Sink[MavenDependencyCount, Future[Done]] =
    Sink.foreach(MDC => println(s"${MDC.library} --> Compile: ${MDC.compile} Provided: ${MDC.provided} Runtime: ${MDC.runtime} Test: ${MDC.test}"))





  // --------------------------------------------------------
  // | Textual MavenDependencyCount object(s) display sink
  // --------------------------------------------------------
  // This sink will display MavenDependencyStatistics in the required
  //    textual representation.

  /** Sink that displays input MavenDependencyCount object(s) to terminal in a textual manner */
  val textualMavenDependencyStatisticsDisplaySink: Sink[MavenDependencyCount, Future[Done]] =
    Flow[MavenDependencyCount]
      .via(Flows.flowMavenDependencyCountToMavenDependencyStatistics)
      .toMat(Sink.foreach(MDS =>
        println(s"Considered minimum number of dependencies: ${MDS.minimumDependencies} \nCompile: ${MDS.compile}\nProvided: ${MDS.provided} \nRuntime: ${MDS.runtime}\nTest: ${MDS.test} ")))(Keep.right)
}
