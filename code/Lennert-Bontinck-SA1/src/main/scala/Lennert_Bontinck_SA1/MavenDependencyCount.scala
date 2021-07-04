package Lennert_Bontinck_SA1
//ok

/** Case class that represents a Maven dependency count (MDC).
 * - library: the name of the library - String
 * - compile: the count of compile dependencies the library has - Int
 * - provided: the count of provided dependencies the library has - Int
 * - runtime: the count of runtime dependencies the library has - Int
 * - test: the count of test dependencies the library has - Int */
case class MavenDependencyCount(library: String = "Dummy name",
                                compile: Int = 0,
                                provided: Int = 0,
                                runtime: Int = 0,
                                test: Int = 0) {

  /** Function to combine/merge a "new" MavenDependencyCount (MDC) to the current object */
  def mergeMavenDependencyCount(newMDC: MavenDependencyCount): MavenDependencyCount =
    copy(library = newMDC.library,
      compile = compile + newMDC.compile,
      provided = provided + newMDC.provided,
      runtime = runtime + newMDC.runtime,
      test = test + newMDC.test
    )
}


