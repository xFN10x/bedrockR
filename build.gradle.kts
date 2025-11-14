plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    java
    id("org.panteleyev.jpackageplugin") version "1.7.3"
    id("com.gradleup.shadow") version "9.2.2"
    id("org.openjfx.javafxplugin") version "0.1.0"
}


repositories {
    mavenCentral()
}

//https://stackoverflow.com/questions/46419817/how-to-add-new-sourceset-with-gradle-kotlin-dsl
sourceSets["main"].resources {
    srcDir("src/main/java")
}

javafx {
    version = "23"
    modules("javafx.controls", "javafx.swing", "javafx.web")
}

dependencies {
    implementation(libs.guava)

    //gson
    implementation("com.google.code.gson:gson:2.13.1")
    //flat laf
    implementation("com.formdev:flatlaf:3.6.2")

    implementation("com.gradleup.shadow:com.gradleup.shadow.gradle.plugin:9.2.2")

    implementation("commons-io:commons-io:2.21.0")

    //update to the new nullable... for some reason (idk why it wont work when updaing guava)
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")

    //3d stuff
    implementation("org.jmonkeyengine:jme3-desktop:3.9.0-alpha2")
    implementation("org.jmonkeyengine:jme3-lwjgl3:3.9.0-alpha2")
    implementation("org.jmonkeyengine:jme3-core:3.9.0-alpha2")

    // https://mvnrepository.com/artifact/org.codehaus.plexus/plexus-utils
    implementation("org.codehaus.plexus:plexus-utils:4.0.2")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "fn10.bedrockr.Launcher"
}

val version = "a1.3.1"
val winver = "0.4.1"

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

