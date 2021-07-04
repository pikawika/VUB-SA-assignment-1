package Lennert_Bontinck_SA1
// ok

// Required imports
import java.nio.file.{Path, Paths}

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString

import scala.concurrent.Future

/** Object having the extracted assignment file available as a source producing ByteString object(s) under sourceOfByteStrings.
 * NOTE: file is manually extracted as per the assignment: "The file is manually extracted". */
object ExtractedFileSource {
  /** Path of the folder containing the extracted file from the exercise. */
  private val pathExtractedFileFolder: String = "src/main/resources"

  /** Path of the original extracted file. */
  private var pathExtractedFile: Path = Paths.get(s"$pathExtractedFileFolder/maven_dependencies.txt")

  /** If wanted, one can use a shortened version of the given file by opting for it here.
   * This can be handy for faster testing runs */
  // when set to true, the shortened file is used instead of the provided file
  private val useShortenedVersion = true
  if(useShortenedVersion) {
    pathExtractedFile = Paths.get(s"$pathExtractedFileFolder/maven_dependencies_first_1000.txt")
  }

  /** Make a source producing ByteString object(s) from the extracted assignment file available. */
  val sourceOfByteStrings: Source[ByteString, Future[IOResult]] = FileIO.fromPath(pathExtractedFile)
}
