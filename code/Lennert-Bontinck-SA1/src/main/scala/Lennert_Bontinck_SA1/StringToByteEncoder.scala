package Lennert_Bontinck_SA1

// Required imports
import akka.NotUsed
import akka.stream.scaladsl.Flow
import akka.util.ByteString

object StringToByteEncoder {
  val flowStringToByteString: Flow[MavenDependencyCount, ByteString, NotUsed] = Flow[MavenDependencyCount].map(libraryCount => {
    ByteString(s"${libraryCount.library} --> Compile: ${libraryCount.compile} Provided: ${libraryCount.provided} Runtime: ${libraryCount.runtime} Test: ${libraryCount.test}\n".getBytes("UTF-8"))
  })
}
