# ![bedrockR](https://raw.githubusercontent.com/xFN10x/bedrockR/refs/heads/master/src/main/resources/ui/BrandingFullWShadow.png)

![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/xFN10x/bedrockR/gradle.yml)
![Hackatime](https://hackatime-badge.hackclub.com/U0923KXMGUR/bedrockR)
![Dependabot](https://img.shields.io/badge/dependabot-025E8C?logo=dependabot&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?logo=openjdk&logoColor=white)

A Minecraft Bedrock GUI-Based Addon maker, for Windows, Linux, and Chromebook.

## Why?

Minecraft Bedrock Addons can be confusing, tedious, and annoying sometimes. bedrockR is what I'm hoping can eliminate those challenges with a useful user interface, and regular updates. This project is heavliy inspired by MCreator, and how it doesn't have the best Bedrock modding in it. 

**bedrockR is still in alpha, things can change and feedback is greatly appreciated!**

**(A bedrockR tutorial is [here](https://github.com/xFN10x/bedrockR/wiki))**

#### Tip: Using the Jar

Using the Release Jar, can be useful if there isnt a build for your platform, or for if you just want to try out bedrockR. If you already have a JDK installed, make sure it is Java 21, most downloads will just lead straight to java 8.

Go [here](https://learn.microsoft.com/en-ca/java/openjdk/download#openjdk-21) for a JDK I reccomend.

## Source Code

This app was made with Visual Studio Code, and it is fully setup to be used with it.

bedrockR is made with Java 21, and it is tested and known to work with [OpenJDK](https://openjdk.org/). If you don't know which OpenJDK distro to use, use [Microsoft's](https://learn.microsoft.com/en-ca/java/openjdk/download#openjdk-21).

### Debugging/Contributing

It is recommended to use Visual Studio Code to make edits. Here are the extensions I recommend:

- [Debugger for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-debug)
- [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
- [Gradle for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-gradle)
- [Project Manager for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-dependency), and
- [Language Support for Java](https://marketplace.visualstudio.com/items?itemName=redhat.java)

These extensions are useful if you are adding a lot of new things.

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

The following commands can be used to make certain builds of bedrockR. (You can only run an OS' build command on the target os. e.g., you can only run `jpackageWIN` on Windows.)

`./gradlew jpackage` - Make an installer based on your OS.

`./gradlew jpackagePORTABLE` - Make a portable copy of bedrockR for your platform.

Now check `build/builtDist`, and you should see your platform's distribution. (NOTE: THIS DIRECTORY CAN ONLY HOLD ONE DISTRO AT A TIME)
