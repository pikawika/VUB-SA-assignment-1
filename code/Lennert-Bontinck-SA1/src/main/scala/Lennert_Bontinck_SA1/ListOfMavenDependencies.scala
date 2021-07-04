package Lennert_Bontinck_SA1
//ok

// Required imports
import java.nio.charset.StandardCharsets

import akka.NotUsed
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.Flow
import akka.util.ByteString

/** Object having the composed flow required to convert an input of ByteString object(s) to MavenDependency object(s)
 * available under composedFlowExtractedFileToMavenDependencies. */
object ListOfMavenDependencies {
  /** Flow to convert ByteString object(s) to list(s) of ByteString objects
   * by using CSV Parser on the input ByteString object. */
  private val flowByteStringToCsvParsedListOfByteStrings: Flow[ByteString, List[ByteString], NotUsed] = CsvParsing.lineScanner()

  /** Flow to Convert CSV parsed list(s) of ByteString objects to map(s) of Strings with "headers" for readability */
  private val flowCsvParsedListOfByteStringsToMapOfStrings: Flow[List[ByteString], Map[String, String], NotUsed] =
    CsvToMap.withHeadersAsStrings(StandardCharsets.UTF_8, "library", "dependency", "dependency_type")

  /** Flow to convert map(s) of Strings with headers to MavenDependency object(s). */
  private val flowMapOfStringsToMavenDependency: Flow[Map[String, String], MavenDependency, NotUsed] = Flow[Map[String, String]]
    .filter(tempMap => {
      // Don't allow records which have empty values
      val filteredEmptyValues = tempMap.values.filter(_.nonEmpty)
      tempMap.size == filteredEmptyValues.size
    })
    .map(dependency => {
      // Create objects from the record using the alternative constructor
      //    from the companion object of MavenDependency
      MavenDependency(dependency)
    })

  /** Composed Flow to convert an input of ByteString object(s) to MavenDependency object(s).
   * Consists of three subflows:
   * flowByteStringToCsvParsedListOfByteStrings ~> flowCsvParsedListOfByteStringsToMapOfStrings ~> flowMapOfStringsToMavenDependency. */
  val composedFlowExtractedFileToMavenDependencies: Flow[ByteString, MavenDependency, NotUsed] = flowByteStringToCsvParsedListOfByteStrings
    .via(flowCsvParsedListOfByteStringsToMapOfStrings)
    .via(flowMapOfStringsToMavenDependency)
}