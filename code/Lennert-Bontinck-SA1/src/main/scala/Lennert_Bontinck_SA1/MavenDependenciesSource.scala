package Lennert_Bontinck_SA1


import akka.stream.IOResult
import akka.stream.scaladsl.{Source}

import scala.concurrent.Future

object MavenDependenciesSource {
  /** Source received from using the Extracted File To MavenDependencies Composed Flow. */
  val source: Source[MavenDependency, Future[IOResult]] = ExtractedFileSource.sourceExtractedFile
    .via(ListOfMavenDependencies.composedFlowExtractedFileToMavenDependencies)
}
