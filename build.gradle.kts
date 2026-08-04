plugins {
    application
    java
    `java-library`
    `maven-publish`
    
    id("com.gradleup.shadow") version "9.6.1"
    id("org.panteleyev.jpackageplugin") version "2.1.0"
}

repositories {
    mavenCentral()
}

val strVersion = "a3.0"
val winver = "0.9.0"

dependencies {
    implementation("com.google.guava:guava:33.6.0-jre")

    api("com.google.code.gson:gson:2.14.0")
    implementation("com.formdev:flatlaf:3.7.2")

    implementation("commons-io:commons-io:2.22.0")
    implementation("org.apache.commons:commons-lang3:3.20.0")

    //update to the new nullable... for some reason (idk why it wont work when updaing guava)
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
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

tasks.jpackage {
    dependsOn("shadowJar")
    input.set(project.layout.buildDirectory.dir("builtJars"))
    destination.set(project.layout.buildDirectory.dir("builtDist"))
    appVersion.set(winver)

    appName.set("bedrockR")
    vendor.set("_FN10_")
    mainJar.set("bedrockR-$strVersion.jar")
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
    description = "Creates a release that is portable. For example, and EXE in a ZIP for windows."
    dependsOn("shadowJar")
    input.set(project.layout.buildDirectory.dir("builtJars"))
    destination.set(project.layout.buildDirectory.dir("builtDist"))
    appVersion.set(winver)

    appName.set("bedrockR")
    vendor.set("_FN10_")
    mainJar.set("bedrockR-$strVersion.jar")
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
  archiveVersion = strVersion
  destinationDirectory = layout.buildDirectory.dir("builtJars")
  archiveClassifier = ""
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    exclude("pdn/**")
}

val mavenJar = tasks.register<Jar>("mavenJar") {
    description = "Create a jar with only cross-platform code."
    destinationDirectory = layout.buildDirectory.dir("mavenLibs")

    from(sourceSets.main.get().output)

    exclude("fn10/bedrockr/Launcher**")
    exclude("fn10/bedrockr/ui/**")
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "bedrockr"
            groupId = "dev.xplate"
            version = strVersion
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
                        url = "https://github.com/xFN10x/bedrockR/tree/master?tab=GPL-3.0-1-ov-file"
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