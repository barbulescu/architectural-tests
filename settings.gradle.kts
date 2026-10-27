rootProject.name = "architectural-tests"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include("java-arch-tests", "kotlin-arch-tests")
