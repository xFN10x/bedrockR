plugins {
    application
    java
    `java-library`
    `maven-publish`
    id("org.panteleyev.jpackageplugin") version "1.7.3"
    id("com.gradleup.shadow") version "9.3.0"
    //id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

//https://stackoverflow.com/questions/46419817/how-to-add-new-sourceset-with-gradle-kotlin-dsl
sourceSets["main"].resources {
    srcDir("src/main/java")
}

/*javafx {
    version = "25"
    modules("javafx.controls", "javafx.swing", "javafx.web")
}*/

dependencies {
    implementation(libs.guava)

    //gson
    implementation("com.google.code.gson:gson:2.13.1")
    //flat laf
    implementation("com.formdev:flatlaf:3.7")

    implementation("commons-io:commons-io:2.21.0")
    implementation("org.apache.commons:commons-lang3:3.20.0")

    //update to the new nullable... for some reason (idk why it wont work when updaing guava)
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")

    //3d stuff
    implementation("org.jmonkeyengine:jme3-lwjgl3:3.9.0-stable")

    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainClass = "fn10.bedrockr.Launcher"
}

val version = "a2.0"
val winver = "0.7.0"

tasks.test {
    failOnNoDiscoveredTests = false
}

tasks.jpackage {
    dependsOn("shadowJar")
    input.set(project.layout.buildDirectory.dir("builtJars"))
    destination.set(project.layout.buildDirectory.dir("builtDist"))
    appVersion.set(winver)

    appName.set("bedrockR")
    vendor.set("_FN10_")
    mainJar.set("bedrockR-$version.jar")
    mainClass.set("fn10.bedrockr.Launcher")

    fileAssociations = project.files("fileAsso.properties")

    windows {
        type.set(org.panteleyev.jpackage.ImageType.MSI)
        //delegate.winConsole = true
        winMenu = true
        winShortcutPrompt = true
        winPerUserInstall = true
        winDirChooser = true
        icon = layout.projectDirectory.file("iconWin.ico")
    }

    linux {
        icon = layout.projectDirectory.file("iconLinux.png")
        type.set(org.panteleyev.jpackage.ImageType.DEB)
        linuxPackageName = "bedrockr"
        linuxShortcut = true
    }
}


tasks.register<org.panteleyev.jpackage.JPackageTask>("jpackagePORTABLE") {
    dependsOn("shadowJar")
    input.set(project.layout.buildDirectory.dir("builtJars"))
    destination.set(project.layout.buildDirectory.dir("builtDist"))
    appVersion.set(winver)

    appName.set("bedrockR")
    vendor.set("_FN10_")
    mainJar.set("bedrockR-$version.jar")
    mainClass.set("fn10.bedrockr.Launcher")

    windows {
        icon = layout.projectDirectory.file("iconWin.ico")
    }

    linux {
        icon = layout.projectDirectory.file("iconLinux.png")
    }

    type = org.panteleyev.jpackage.ImageType.APP_IMAGE
}


tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
  archiveBaseName = "bedrockR"
  archiveVersion = version
  destinationDirectory = layout.buildDirectory.dir("builtJars")
  archiveClassifier = ""
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val mavenJar by tasks.registering(Jar::class) {
    destinationDirectory = layout.buildDirectory.dir("mavenLibs")

    from(sourceSets.main.get().output)

    exclude("fn10/bedrockr/Launcher.class")
    exclude("fn10/bedrockr/Launcher${'$'}1.class")
    exclude("fn10/bedrockr/Launcher${'$'}2.class")
    exclude("fn10/bedrockr/Launcher${'$'}3.class")
    exclude("fn10/bedrockr/windows/**")
    exclude("fn10/bedrockr/rendering/**")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "bedrockr"
            groupId = "dev.xplate"
            version = version
            artifact(tasks.named<Jar>("mavenJar"))
            artifact(tasks.named<Jar>("sourcesJar"))
            artifact(tasks.named<Jar>("javadocJar"))
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name = "bedrockR"
                description = "The libraries for bedrockR, an addon maker for minecraft bedrock."
                url = "https://bedrockr.xplate.dev"
                licenses {
                    license {
                        name = "GNU GENERAL PUBLIC LICENSE v3"
                        url = "https://github.com/xFN10x/bedrockR/tree/a2.0?tab=License-1-ov-file#"
                    }
                }
                developers {
                    developer {
                        id = "fn10"
                        name = "xFN10x"
                    }
                }
            }
        }
    }
}