
<p align="center">
<img alt="bedrockR" src="https://raw.githubusercontent.com/xFN10x/bedrockR/refs/heads/master/src/main/resources/ui/BrandingFullWShadow.png" height=""/>
<br/>
<img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/xFN10x/bedrockR/gradle.yml"/>
<img alt="Hackatime" src="https://hackatime-badge.hackclub.com/U0923KXMGUR/bedrockR"/>
<img alt="Dependabot" src="https://img.shields.io/badge/dependabot-025E8C?logo=dependabot&amp;logoColor=white"/>
<img alt="Java" src="https://img.shields.io/badge/java-%23ED8B00.svg?logo=openjdk&amp;logoColor=white"/>
<a href="https://jitpack.io/#xFN10x/bedrockR"><img alt="JitPack" src="https://jitpack.io/v/xFN10x/bedrockR.svg"/></a>
<a href="https://hackclub.com/"><img alt="HackClub" height="20" src="https://assets.hackclub.com/flag-standalone.svg"/></a>

</p>
<p align="center">
A Minecraft Bedrock GUI-Based Addon maker, for Windows, Linux, and Mobile.<br/><br/>
<a href="https://www.youtube.com/channel/UCnEJG4HgBw91uNFuaZ2axyA">bedrockR YouTube Channel</a> | <a href="https://bedrockr.xplate.dev">bedrockR Website</a>
</p>

## Why?

Minecraft Bedrock Addons can be confusing, tedious, and annoying sometimes. bedrockR is what I'm hoping can eliminate
those challenges with a useful user interface, and regular updates. This project is heavily inspired by MCreator, and
how it doesn't have the best Bedrock modding in it.

> [!IMPORTANT]
> bedrockR is **still in alpha**, things can & will probably change and feedback is greatly appreciated!

**(A bedrockR tutorial is [on the wiki](https://github.com/xFN10x/bedrockR/wiki))**

## Installing

Since bedrockR is made in Java, it is available for any computer that can run Java 25.

If you are on Debian or Windows, you can download one of the installers, or portable version in
the [releases section](https://github.com/xFN10x/bedrockR/releases).

Or, for an even simpler download, go to [the bedrockR Website](https://bedrockr.xplate.dev/download) to download the
latest version

If there are no builds included in

### Using the JAR

If none of these builds are for your platform, you can use the universal JAR file, which

## Source Code

bedrockR is made with Java 25, and developed with [OpenJDK](https://openjdk.org/). If you don't know which OpenJDK
distro to use, use [Microsoft's](https://learn.microsoft.com/en-ca/java/openjdk/download#openjdk-25) (it's what I use.).

bedrockR (versions a1.0 - a1.5) were made in VSCode with the built-in Java extension, and it uses Gradle (with kotlin
DSL) for dependency management.

> [!IMPORTANT]
> bedrockR Mobile is a _seperate program,_ read its source code instructions
on [its repository](https://github.com/xFN10x/bedrockR-Mobile)

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

- Run the command: `./gradlew shadowJar`

- Now you should be able to find the JAR in `build/builtJars`

#### Distribution copies

The following commands can be used to make certain builds of bedrockR.

`./gradlew jpackage` - Make an installer based on your OS.

`./gradlew jpackagePORTABLE` - Make a portable copy of bedrockR for your platform.

Now check `build/builtDist`, and you should see your platform's distribution. (NOTE: THIS DIRECTORY CAN ONLY HOLD ONE
DISTRO AT A TIME)

#### Libraries

Since a2.0, bedrockR's codebase was reworked to be fully cross-platform. With the following command, you can make a JAR
with only the backend code. This means that you can port bedrockR to any device that can run Java 25.

Use `./gradlew mavenJar` to make this jar. It will be found in `build/mavenLibs`.

**This isn't the recommended way to use this jar. You should use the Maven library from Jitpack instead.**

> _bedrockR is not assocaited with Mojang AB, or Microsoft Corp._ <br/>
> _"Minecraft" and the Minecraft Logo are trademarks of Mojang AB._
>
>***bedrockR © Copyright 2025-26 xFN10x (Damien K.)***
