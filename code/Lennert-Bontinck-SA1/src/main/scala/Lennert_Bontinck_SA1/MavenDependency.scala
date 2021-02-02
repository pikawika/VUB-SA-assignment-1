package Lennert_Bontinck_SA1

/** Case class that represents a Maven dependency */
case class MavenDependency(library: String,
                           dependency: String,
                           dependency_type: String)

/** Companion object for the MavenDependency class that includes alternative constructors */
object MavenDependency {
  /** Alternative constructor for the MavenDependency class.
   *  This can instantiate an object using a map of strings as input. */
  def apply(inputDependency: Map[String, String]): MavenDependency = {
    MavenDependency(inputDependency("library"),
      inputDependency("dependency"),
      inputDependency("dependency_type"))
  }
}


