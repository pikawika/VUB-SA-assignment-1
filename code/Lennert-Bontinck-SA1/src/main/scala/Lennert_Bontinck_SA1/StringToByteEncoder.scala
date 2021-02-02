package Lennert_Bontinck_SA1

import akka.NotUsed
import akka.stream.scaladsl.Flow
import akka.util.ByteString

object StringToByteEncoder {
  val flowStringToByteString: Flow[MavenLibraryDependencyCount, ByteString, NotUsed] = Flow[MavenLibraryDependencyCount].map(libraryCount => {
    ByteString(s"${libraryCount.library} --> Compile: ${libraryCount.compile} Provided: ${libraryCount.provided} Runtime: ${libraryCount.runtime} Test: ${libraryCount.test}\n".getBytes("UTF-8"))
  })
}
