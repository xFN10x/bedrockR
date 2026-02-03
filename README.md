# ![bedrockR](https://raw.githubusercontent.com/xFN10x/bedrockR/refs/heads/master/src/main/resources/ui/BrandingFullWShadow.png)

![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/xFN10x/bedrockR/gradle.yml)
![Hackatime](https://hackatime-badge.hackclub.com/U0923KXMGUR/bedrockR)
![Dependabot](https://img.shields.io/badge/dependabot-025E8C?logo=dependabot&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?logo=openjdk&logoColor=white)
[![JitPack](https://jitpack.io/v/xFN10x/bedrockR.svg)](https://jitpack.io/#xFN10x/bedrockR)

<sub>_Mobile_</sub>

[![Android CI](https://github.com/xFN10x/bedrockR-Mobile/actions/workflows/android.yml/badge.svg)](https://github.com/xFN10x/bedrockR-Mobile/actions/workflows/android.yml)
![Hackatime](https://hackatime-badge.hackclub.com/U0923KXMGUR/bedrockRMobile
) ![Dependabot](https://img.shields.io/badge/dependabot-025E8C?logo=dependabot&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?logo=openjdk&logoColor=white)

A Minecraft Bedrock GUI-Based Addon maker, for Windows, Linux, and Mobile.

[bedrockR Youtube Channel](https://www.youtube.com/channel/UCnEJG4HgBw91uNFuaZ2axyA) | [bedrockR Website](https://bedrockr.xplate.dev)

(Mobile repository is here: <https://github.com/xFN10x/bedrockR-Mobile>, but most of the info is on this repo.)

## Why?

Minecraft Bedrock Addons can be confusing, tedious, and annoying sometimes. bedrockR is what I'm hoping can eliminate those challenges with a useful user interface, and regular updates. This project is heavily inspired by MCreator, and how it doesn't have the best Bedrock modding in it.

**bedrockR is still in alpha, things can change and feedback is greatly appreciated!**

**(A bedrockR tutorial is [on the wiki](https://github.com/xFN10x/bedrockR/wiki))**

## Installing

Since bedrockR is made in Java, it is available for any computer that can run Java 25. If you want an easy installation, you can download one of the installers, or portable version in the [releases section](https://github.com/xFN10x/bedrockR/releases).

Or, for an even simpler download, go to [the bedrockR Website](https://bedrockr.xplate.dev/download) to download the latest version

### Mobile (WIP)

Since a2.0, bedrockR is now available for Android, and it can be downloaded on [the bedrockR Website](https://bedrockr.xplate.dev/download), or on the [bedrockR Mobile Repository's release page.](https://github.com/xFN10x/bedrockR-Mobile/releases)

bedrockR Mobile is a separate repository from bedrockR, and its source code is available on the [bedrockR Mobile Repo](https://github.com/xFN10x/bedrockR-Mobile)

bedrockR Mobile is planned to be released on the Google Play Store at some point.

### Using the JAR (Other Platforms)

If none of these builds are for your platform, you can use the universal JAR file, which you can download along side the other version.

## Source Code

bedrockR is made with Java 25, and it is tested and known to work with [OpenJDK](https://openjdk.org/). If you don't know which OpenJDK distro to use, use [Microsoft's](https://learn.microsoft.com/en-ca/java/openjdk/download#openjdk-25) (it's what I use for testing).

bedrockR was made in VSCode with the built in Java extension, and it uses Gradle (with kotlin DSL) for dependency management.

> [!IMPORTANT]
> bedrockR Mobile is a seperate program, read its source code instructions on [its repository](https://github.com/xFN10x/bedrockR-Mobile)

### Maven

_(since a2.0)_
You can use bedrockR's addon creation code in your own projects! This code can be used for:

- Porting bedrockR to other platforms
- Creating a CLI interface
- and more!

[![Jitpack](https://jitpack.io/v/xFN10x/bedrockR.svg)](https://jitpack.io/#xFN10x/bedrockR)

Click the badge above to go to the Jitpack page where it will tell you on how to use bedrockR on your project.

> [!WARNING]
> bedrockR versions released before a2.0 will not work on Jitpack, and are highly discouraged to be used as libraries

### Building

#### JAR

Building a JAR can be done with one command.

(Make sure you are cd'd into the source code directory)

- Run the command;

  ```powershell
  ./gradlew shadowJar (powershell)
    gradlew shadowJar (cmd prompt)
  ```

- Now you should be able to find the JAR in `app/build/builtJars`

#### Distribution copies

The following commands can be used to make certain builds of bedrockR.

`./gradlew jpackage` - Make an installer based on your OS.

`./gradlew jpackagePORTABLE` - Make a portable copy of bedrockR for your platform.

Now check `build/builtDist`, and you should see your platform's distribution. (NOTE: THIS DIRECTORY CAN ONLY HOLD ONE DISTRO AT A TIME)

#### Libraries

Since, a2.0, bedrockR is now made to be used in other programs as well. With the following command, you can make a JAR that doesn't include and UI related stuff. This means that you can port bedrockR to any device that can run Java 25.

Use `./gradlew mavenJar` to make this jar. It will be found in `build/mavenLibs`.

**This isn't the recommended way to use this jar. You should use the Maven library from Jitpack instead.**
