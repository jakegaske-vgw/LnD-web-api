plugins {
    standardKotlinJvmModule()
    application
}

application {
    mainClass.set("co.vgw.lnd.wallet.app.ApplicationKt")
}

dependencies {
    implementation(project(":http"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.jetty)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.contentNegotiation)
}

tasks.distTar {
    archiveFileName.set("app-bundle.${archiveExtension.get()}")
}

repositories {
    mavenCentral()
}