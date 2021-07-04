package Lennert_Bontinck_SA1

/** Case class that represents a Maven library dependency count.
 * - library: the name of the library - String
 * - compile: the count of compile dependencies the library has - Int
 * - provided: the count of provided dependencies the library has - Int
 * - runtime: the count of runtime dependencies the library has - Int
 * - test: the count of test dependencies the library has - Int */
case class MavenLibraryDependencyCount(library: String = "dummyName",
                                       compile: Int = 0,
                                       provided: Int = 0,
                                       runtime: Int = 0,
                                       test: Int = 0) {

  /** Function to add a MavenDependency to the MavenLibraryDependencyCount object */
  def addDependency(newCount: MavenLibraryDependencyCount): MavenLibraryDependencyCount =
    copy(library = newCount.library,
      compile = compile + newCount.compile,
      provided = provided + newCount.provided,
      runtime = runtime + newCount.runtime,
      test = test + newCount.test
    )
}


