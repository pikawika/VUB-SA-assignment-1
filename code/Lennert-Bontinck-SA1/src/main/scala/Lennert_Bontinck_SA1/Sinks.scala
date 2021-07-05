package Lennert_Bontinck_SA1

// Required imports
import akka.Done
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Sink}
import akka.util.ByteString

import java.nio.file.Paths
import java.nio.file.StandardOpenOption.{CREATE, TRUNCATE_EXISTING, WRITE}
import scala.concurrent.Future

/** Object having the required sinks for the assignment. */
object Sinks {
  /** Sink that saves input ByteString object(s) to disk */
  val textualMavenDependencyCountSaveSink: Sink[ByteString, Future[IOResult]] =
    FileIO.toPath(Paths.get("src/main/resources/result/Lennert-Bontinck-SA1-output.txt"), Set(WRITE, TRUNCATE_EXISTING, CREATE))

  /** Sink that displays input MavenDependencyCount object(s) to terminal */
  val textualMavenDependencyCountDisplaySink: Sink[MavenDependencyCount, Future[Done]] =
    Sink.foreach(println)
}
