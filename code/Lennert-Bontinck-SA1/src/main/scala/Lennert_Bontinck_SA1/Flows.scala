package Lennert_Bontinck_SA1

// Required imports
import akka.NotUsed
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.Flow
import akka.util.ByteString

import java.nio.charset.StandardCharsets

/** Object having the required re-usable flows for the assignment.
 * - flowMultipleMavenDependencyCountsToSingle
 * - flowMavenDependencySourceToCollectedMavenDependencyCount
 * - composedFlowExtractedFileToMavenDependencies */
object Flows {

  // --------------------------------------------------------------------------------------
  // | Flow to convert multiple MavenDependencyCount objects to singular object
  // --------------------------------------------------------------------------------------
  // This flow will combine all incoming MavenDependencyCount object(s) to a singular one.

  /** Flow that combines/merges MavenDependencyCount (MDC) objects.
   * NOTE: Assumes input MavenDependencyCount object(s) are of same library (equal name) */
  val flowMultipleMavenDependencyCountsToSingle: Flow[MavenDependencyCount, MavenDependencyCount, NotUsed] =
    Flow[MavenDependencyCount]
      // Filter out objects that don't have any value
      .filter(mevanDC => mevanDC.test != 0 || mevanDC.runtime != 0 || mevanDC.provided != 0 || mevanDC.compile != 0)

      // Make single MavenDependencyCount (MDC) object.
      //    Does this by Merging "existing" to the "new" MavenDependencyCount objects.
      //    Initially the object is a empty MDC which has a dummy name (hence we we need to merge old with new).
      .fold(MavenDependencyCount())((existingMDC, newMDC) => existingMDC.mergeMavenDependencyCount(newMDC))





  // --------------------------------------------------------------------------------------
  // | Flow to convert MavenDependency object(s) to MavenDependencyCount object(s)
  // --------------------------------------------------------------------------------------
  // This flow corresponds to the process from source to collected dependencies
  //    In the assignment figure.
  //    This is provided as a extracted flow for reuse between Main and MainDisplay.

  /** Flow that goes from MavenDependency object(s) from source to the collected dependencies
   * in the form of MavenDependencyCount object(s). */
  val flowMavenDependencySourceToCollectedMavenDependencyCount: Flow[MavenDependency, MavenDependencyCount, NotUsed] =
    Flow[MavenDependency]
      // Create sub streams by grouping on library name
      //    Max amount of sub streams is Int.MAX per requirement of the assignment.
      .groupBy(maxSubstreams = Int.MaxValue, _.library)

      // Push the sub streams through the FlowDependenciesShape Flow Shape.
      .via(FlowShapes.flowMavenDependencyToMavenDependencyCountParallel)

      // Merge the sub streams back to a regular (singular) stream
      .mergeSubstreams





  // --------------------------------------------------------------------------------------
  // | Composed Flow to convert ByteString (BS) object(s) to MavenDependency object(s)
  // --------------------------------------------------------------------------------------
  // This flow corresponds to the process of instantiating classes from the extracted file
  //    in the assignment figure.


  // -------------------- Make flow from ByteString (BS) object(s) to List(s) of BS objects using CSV --------------------

  /** Flow to convert ByteString object(s) to list(s) of ByteString objects
   * by using CSV Parser on the input ByteString object. */
  private val flowByteStringToCsvParsedListOfByteStrings: Flow[ByteString, List[ByteString], NotUsed] = CsvParsing.lineScanner()


  // -------------------- Make flow from List(s) of ByteString objects to Map(s) of Strings --------------------

  /** Flow to Convert CSV parsed list(s) of ByteString objects to Map(s) of Strings with "headers" for readability */
  private val flowCsvParsedListOfByteStringsToMapOfStrings: Flow[List[ByteString], Map[String, String], NotUsed] =
    CsvToMap.withHeadersAsStrings(StandardCharsets.UTF_8, "library", "dependency", "dependency_type")


  // -------------------- Make flow from Map(s) of Strings to MavenDependency object(s) --------------------

  /** Flow to convert Map(s) of Strings with headers to MavenDependency object(s). */
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


  // -------------------- Make composed flow from ByteString (BS) object(s) to MavenDependency object(s) --------------------

  /** Composed Flow to convert an input of ByteString object(s) to MavenDependency object(s).
   * Consists of three subflows:
   * flowByteStringToCsvParsedListOfByteStrings ~> flowCsvParsedListOfByteStringsToMapOfStrings ~> flowMapOfStringsToMavenDependency. */
  val composedFlowExtractedFileToMavenDependencies: Flow[ByteString, MavenDependency, NotUsed] = flowByteStringToCsvParsedListOfByteStrings
    .via(flowCsvParsedListOfByteStringsToMapOfStrings)
    .via(flowMapOfStringsToMavenDependency)





  // --------------------------------------------------------------------------------------
  // | Flow to convert MavenDependencyCount object(s) to textual ByteString representation
  // --------------------------------------------------------------------------------------
  // This flow will convert MavenDependencyCount object(s) to a
  //    textual ByteString representation in the form:
  //        NameOfLibrary --> Compile: # Provided: # Runtime: # Test: #

  /** Flow that converts MavenDependencyCount object(s) to a textual ByteString representation. */
  val flowMavenDependencyCountToTextualByteString: Flow[MavenDependencyCount, ByteString, NotUsed] =
    Flow[MavenDependencyCount].map(libraryCount => {
      ByteString(s"${libraryCount.library} --> Compile: ${libraryCount.compile} Provided: ${libraryCount.provided} Runtime: ${libraryCount.runtime} Test: ${libraryCount.test}\n".getBytes("UTF-8"))
    })
}