package Lennert_Bontinck_SA1

/** Case class that represents a Maven dependency */
case class MavenLibraryDependencyCount(library: String,
                                       compile: Int,
                                       provided: Int,
                                       runtime: Int,
                                       test: Int)




