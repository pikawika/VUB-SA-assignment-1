package Lennert_Bontinck_SA1

import java.nio.charset.StandardCharsets

import akka.NotUsed
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.Flow
import akka.util.ByteString

object ListOfMavenDependencies {
  /** Flow to convert raw Bytestring to a list of Bytestrings, using CSV Parser. */
  private val flowCsvParsing: Flow[ByteString, List[ByteString], NotUsed] = CsvParsing.lineScanner()

  /** Flow to convert a CSV parsed list of Bytestrings to a map of strings containing: library, dependency and type. */
  private val flowCsvMapper: Flow[List[ByteString], Map[String, String], NotUsed] =
    CsvToMap.withHeadersAsStrings(StandardCharsets.UTF_8, "library", "dependency", "dependency_type")

  /** Flow to convert a map of strings containing: library, dependency and type to a MavenDependency object. */
  private val flowMappedCsvToMavenDependency: Flow[Map[String, String], MavenDependency, NotUsed] = Flow[Map[String, String]]
    .filter(tempMap => {
      // Don't allow records which have empty values
      val filteredEmptyValues = tempMap.values.filter(_.nonEmpty)
      tempMap.size == filteredEmptyValues.size
    })
    .map(dependency => {
      // Create objects from the record using the alternative constructor from the companion object of MavenDependency
      MavenDependency(dependency)
    })

  /** Composed Flow that executes flowCsvParsing ~> flowCsvMapper ~> flowMappedCsvToMavenDependency. */
  val composedFlowExtractedFileToMavenDependencies: Flow[ByteString, MavenDependency, NotUsed] = flowCsvParsing
    .via(flowCsvMapper)
    .via(flowMappedCsvToMavenDependency)
}
