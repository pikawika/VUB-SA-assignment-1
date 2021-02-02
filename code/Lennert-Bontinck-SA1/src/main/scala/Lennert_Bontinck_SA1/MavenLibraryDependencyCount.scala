package Lennert_Bontinck_SA1

/** Case class that represents a Maven library dependency count */
case class MavenLibraryDependencyCount(library: String = "dummyName", compile: Int = 0, provided: Int = 0, runtime: Int = 0, test: Int = 0) {
  def addDependency(newCount: MavenLibraryDependencyCount): MavenLibraryDependencyCount =
    copy(library = newCount.library,
      compile = compile + newCount.compile,
      provided = provided + newCount.provided,
      runtime = runtime + newCount.runtime,
      test = test + newCount.test
    )
}


