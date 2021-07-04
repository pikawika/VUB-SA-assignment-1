package Lennert_Bontinck_SA1
// ok

// Required imports
import akka.stream.IOResult
import akka.stream.scaladsl.Source

import scala.concurrent.Future

/** Object having the extracted assignment file available as a source producing MavenDependency object(s) under source. */
object MavenDependenciesSource {
  /** Source received from flowing the sourceOfByteStrings source from ExtractedFileSource through the MavenDependencies Composed Flow.
   * This source produces MavenDependency object(s). */
  val source: Source[MavenDependency, Future[IOResult]] = ExtractedFileSource.sourceOfByteStrings
    .via(ListOfMavenDependencies.composedFlowExtractedFileToMavenDependencies)
}
