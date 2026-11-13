import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.commons.io.FileUtils
import org.codehaus.plexus.archiver.util.DefaultFileSet
import org.codehaus.plexus.archiver.zip.ZipArchiver
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.DeflaterOutputStream

plugins {
    application
    java
    `java-library`
    `maven-publish`

    id("com.gradleup.shadow") version "9.6.1"
    id("org.graalvm.buildtools.native") version "1.1.18"
}


repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

val strVersion = "a3.0PRE2"
val winVer = "0.9.3"
val numVer = 10

val installerVer = "1.0"
val updaterName = "brupdater"

sourceSets {
    create("updater")
    create("bootstrap")
    create("installer")
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.codehaus.plexus:plexus-archiver:4.14.0")
    }
}

dependencies {
    implementation("org.codehaus.plexus:plexus-archiver:4.14.0")
    "installerImplementation"("org.codehaus.plexus:plexus-archiver:4.14.0")
    "installerImplementation"("com.google.guava:guava:33.8.0-jre")

    "installerImplementation"("com.google.code.gson:gson:2.15.0")
    "installerImplementation"("com.formdev:flatlaf:3.8")
    
    implementation("com.google.guava:guava:33.8.0-jre")

    api("com.google.code.gson:gson:2.15.0")
    implementation("com.formdev:flatlaf:3.8")

    implementation("commons-io:commons-io:2.22.0")
    api("org.apache.commons:commons-lang3:3.21.0")

    //update to the new nullable... for some reason (idk why it wont work when updaing guava)
    compileOnly("jakarta.annotation:jakarta.annotation-api:3.0.0")
    compileOnly("org.jetbrains:annotations:26.1.0")
    implementation("com.github.Querz:NBT:6.1")
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    sourceSets {
        main {
            mainClass = "fn10.bedrockr.Launcher"
        }
        named("updater") {
            mainClass = "fn10.bedrockr.updater.Updater"
        }
        named("bootstrap") {
            mainClass = "fn10.bedrockr.bootstrap.Bootstrap"
        }
        named("installer") {
            mainClass = "fn10.bedrockr.installer.Installer"
            compileClasspath += sourceSets.main.get().output
        }

    }
}

graalvmNative {
    //toolchainDetection.set(true)
    binaries {
        create("updaterDev") {
            imageName.set(updaterName)
            mainClass.set("fn10.bedrockr.updater.Main")
            verbose.set(true)
            sharedLibrary.set(false)
            quickBuild.set(true)
            buildArgs.add("--emit build-report")

            // Combine the compile and runtime classpaths/classes for this source set
            classpath.from(
                sourceSets.named("updater").get().output
            )
        }
        create("updaterRelease") {
            imageName.set(updaterName)
            mainClass.set("fn10.bedrockr.updater.Main")
            verbose.set(true)
            sharedLibrary.set(false)
            buildArgs.addAll("--emit build-report", "-Os")

            // Combine the compile and runtime classpaths/classes for this source set
            classpath.from(
                sourceSets.named("updater").get().output
            )
        }
    }
}

tasks.register<DefaultTask>("makeInstallerResources") {
    dependsOn("shadowJar")
    doLast {
        val jdkLoc = System.getProperty("java.home");
        logger.quiet("Creating installer resources, with jdk at: ${jdkLoc}")
        val json = """
        {
            "version": "$strVersion",
            "winVer": "$winVer",
            "jdkVer": "${System.getProperty("java.version")}",
            "installerVer": "$installerVer",
            "numVer": "$numVer"
        }
        """.trimIndent()

        FileUtils.write(
            File("${layout.buildDirectory.get()}/resources/installer/data.json"), json,
            Charsets.UTF_8
        )

        // Now zip the jdk.
        val jdkArchiver = ZipArchiver()
        jdkArchiver.destFile = File("${layout.buildDirectory.get()}/resources/installer/jdk.zip")
        jdkArchiver.addFileSet(DefaultFileSet.fileSet(File(jdkLoc)))
        jdkArchiver.createArchive()

        val bedrockRJarOutput = DeflaterOutputStream(
            FileUtils.newOutputStream(
                File("${layout.buildDirectory.get()}/resources/installer/bedrockRZiped.dat"),
                false
            )
        )
        val bedrockRInput =
            Files.newInputStream(Paths.get("${layout.buildDirectory.get()}/builtJars/bedrockR-$strVersion.jar"))
        bedrockRJarOutput.write(bedrockRInput.readAllBytes())
    }
}
tasks.register<ShadowJar>("makeInstallerJar") {
    dependsOn("makeInstallerResources")
    description = "Create a jar for the installer"

    archiveBaseName = "bedrockR-Installer"
    archiveVersion = strVersion
    destinationDirectory = layout.buildDirectory.dir("installerJars")
    archiveClassifier = ""

    from(sourceSets.named("installer").get().output)
}

/*
tasks.register<Sync>("JpackagePrepare") {
    description = "Prepares the input for JPackage. (not portable)"
    dependsOn("shadowJar", "nativeUpdaterReleaseCompile")

    into(layout.buildDirectory.dir("jpackageInclude"))
    from(layout.buildDirectory.file("native/nativeUpdaterReleaseCompile/${updaterName}.exe"))
    from(tasks.named("shadowJar"))
}

tasks.jpackage {
    dependsOn("JpackagePrepare")
    
    //Add updater to path with jar for input.
    val buildDir = project.layout.buildDirectory
    val inputDir = buildDir.dir("jpackageInclude");
    input.set(inputDir)
    destination.set(buildDir.dir("builtDist"))
    appVersion.set(winver)

    appName.set("bedrockR")
    vendor.set("_FN10_")
    mainJar.set("bedrockR-$strVersion.jar")
    mainClass.set("fn10.bedrockr.Launcher")

    fileAssociations = project.fileTree("fileAsso")

    windows {
        type.set(org.panteleyev.jpackage.ImageType.MSI)
        //delegate.winConsole = true
        winMenu = true
        winShortcutPrompt = true
        winPerUserInstall = true
        winDirChooser = true
        winHelpUrl = "https://github.com/xFN10x/bedrockR"
        icon = layout.projectDirectory.file("iconWin.ico")
    }

    linux {
        icon = layout.projectDirectory.file("iconLinux.png")
        type.set(org.panteleyev.jpackage.ImageType.DEB)
        linuxPackageName = "bedrockr"
        linuxShortcut = true
        linuxAppCategory = "Development"
        linuxMenuGroup = "bedrockR"
    }
    
    //why would you want this on mac? you dont even GET mc bedrock
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
*/

tasks.named<ShadowJar>("shadowJar") {
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