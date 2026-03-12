// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("org.sonarqube") version "5.1.0.4882"
}

sonar {
    properties {
        property("sonar.projectKey", "Steven01231_SOEN345-Winter2026")
        property("sonar.organization", "steven01231")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sources", "app/src/main")
        property("sonar.tests", "app/src/test,app/src/androidTest")
        property("sonar.coverage.jacoco.xmlReportPaths", "app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        property("sonar.exclusions", "**/R.class,**/R\$*.class,**/BuildConfig.*,**/Manifest*.*")
    }
}
