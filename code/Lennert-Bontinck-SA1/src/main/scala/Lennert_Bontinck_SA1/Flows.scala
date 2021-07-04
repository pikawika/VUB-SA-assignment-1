package Lennert_Bontinck_SA1
// ok

// Required imports
import akka.NotUsed
import akka.stream.scaladsl.Flow

/** Object having the required simple general flows for the assignment. */
object Flows {
  /** Flow that combines/merges MavenDependencyCount (MDC) objects.
   * NOTE: Assumes input MavenDependencyCount object(s) are of same library (equal name) */
  val flowMultipleMavenDependencyCountsToSingle: Flow[MavenDependencyCount, MavenDependencyCount, NotUsed] =
    Flow[MavenDependencyCount]
      // Make single MavenDependencyCount (MDC) object.
      //    Does this by Merging "existing" and "new" MavenDependencyCount objects.
      //    Initially the object is a empty MDC.
      .fold(MavenDependencyCount())((existingMDC, newMDC) => existingMDC.mergeMavenDependencyCount(newMDC))
}
