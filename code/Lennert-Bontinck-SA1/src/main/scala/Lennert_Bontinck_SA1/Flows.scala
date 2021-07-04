package Lennert_Bontinck_SA1

// Required imports
import akka.NotUsed
import akka.stream.scaladsl.Flow

/** Object having the required re-usable flows for the assignment. */
object Flows {
  // --------------------------------------------------------------------------------------
  // | flowMultipleMavenDependencyCountsToSingle
  // --------------------------------------------------------------------------------------

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
  // | flowMavenDependencyToMavenDependencyCount
  // --------------------------------------------------------------------------------------

  /** Flow that goes from MavenDependency object(s) to collected dependencies in the form of MavenDependencyCount object(s). */
  val flowMavenDependencyToMavenDependencyCount: Flow[MavenDependency, MavenDependencyCount, NotUsed] =
    Flow[MavenDependency]
      // Create sub streams by grouping on library name
      //    Max amount of sub streams is Int.MAX per requirement of the assignment.
      .groupBy(maxSubstreams = Int.MaxValue, _.library)

      // Push the sub streams through the FlowDependenciesShape Flow Shape.
      .via(FlowDependenciesShapeParallel.flowMavenDependencyToMavenDependencyCountParallel)

      // Merge the sub streams back to a regular (singular) stream
      .mergeSubstreams
}