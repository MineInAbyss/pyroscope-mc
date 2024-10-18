plugins {
    alias(idofrontLibs.plugins.mia.kotlin.jvm)
    alias(idofrontLibs.plugins.mia.copyjar)
    alias(idofrontLibs.plugins.mia.papermc)
    alias(idofrontLibs.plugins.mia.autoversion)
    alias(idofrontLibs.plugins.kotlinx.serialization)
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(idofrontLibs.idofront.config)
    compileOnly(idofrontLibs.idofront.commands)
    compileOnly(idofrontLibs.kotlinx.serialization.json)
    implementation("io.pyroscope:agent:0.14.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
