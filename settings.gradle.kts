

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = "credit-cards-manager"
include("domain")
include("application")
include("infrastructure")
include("bootstrap")