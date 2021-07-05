package Lennert_Bontinck_SA1

// Required imports
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Source}
import akka.util.ByteString

import java.nio.file.{Path, Paths}
import scala.concurrent.Future

/** Object having the required sources for the assignment. */
object Sources {

  // --------------------------------------------------------
  // | "Raw" ByteString Source
  // --------------------------------------------------------
  // This code provides the given file as a ByteString source.
  //    It also allows the user to specify to use
  //    a shorter file for testing purposes.

  /** Path of the folder containing the extracted file from the exercise. */
  private val pathExtractedFileFolder: String = "src/main/resources"

  /** Path of the original extracted file. */
  private var pathExtractedFile: Path = Paths.get(s"$pathExtractedFileFolder/maven_dependencies.txt")

  /** If wanted, one can use a shortened version of the given file by opting for it here.
   * This can be handy for faster testing runs */
  // when set to true, the shortened file is used instead of the provided file
  private val useShortenedVersion = false
  if (useShortenedVersion) {
    pathExtractedFile = Paths.get(s"$pathExtractedFileFolder/maven_dependencies_first_1000.txt")
  }

  /** Make a source producing ByteString object(s) from the extracted assignment file available. */
  val sourceOfByteString: Source[ByteString, Future[IOResult]] = FileIO.fromPath(pathExtractedFile)



  // --------------------------------------------------------
  // | Source of MavenDependency object(s)
  // --------------------------------------------------------
  // This code provides the given file as a source
  //    providing MavenDependency object(s).

  /** Source received from flowing the sourceOfByteStrings source through the MavenDependencies Composed Flow.
   * This source produces MavenDependency object(s). */
  val sourceOfMavenDependency: Source[MavenDependency, Future[IOResult]] =
    sourceOfByteString.via(Flows.composedFlowExtractedFileToMavenDependencies)

}