package Lennert_Bontinck_SA1

/** Case class that represents a Maven dependency statistics (MDS).
 * - minimumDependencies: minimum dependency amount to be included in statistic - Int
 * - compile: the count of compile dependencies the library has - Int
 * - provided: the count of provided dependencies the library has - Int
 * - runtime: the count of runtime dependencies the library has - Int
 * - test: the count of test dependencies the library has - Int */
case class MavenDependencyStatistics(minimumDependencies: Int = 2,
                                     compile: Int = 0,
                                     provided: Int = 0,
                                     runtime: Int = 0,
                                     test: Int = 0) {

  /** Function to combine/merge a "new" Maven dependency statistics (MDS) to the current object */
  def addMavenDependencyCountToStatistic(newMDC: MavenDependencyCount): MavenDependencyStatistics =
    copy(minimumDependencies = minimumDependencies,
      compile = if (newMDC.compile < minimumDependencies) compile else compile + 1,
      provided = if (newMDC.provided < minimumDependencies) provided else provided + 1,
      runtime = if (newMDC.runtime < minimumDependencies) runtime else runtime + 1,
      test = if (newMDC.test < minimumDependencies) test else test + 1
    )
}


