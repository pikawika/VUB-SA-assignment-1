package Lennert_Bontinck_SA1

import java.nio.file.{Path, Paths}

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString

import scala.concurrent.Future

object ExtractedFile {
  /** Path of the folder containing the extracted file. */
  private val pathExtractedFileFolder: String = "src/main/resources"

  /** Path of the extracted file. */
  private val pathExtractedFile: Path = Paths.get(s"$pathExtractedFileFolder/maven_dependencies_first_1000.txt")

  /** Make raw ByteString source from input file. */
  val sourceExtractedFile: Source[ByteString, Future[IOResult]] = FileIO.fromPath(pathExtractedFile)
}
