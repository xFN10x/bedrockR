# ![bedrockR](https://raw.githubusercontent.com/xFN10x/bedrockR/refs/heads/master/app/src/main/resources/ui/BrandingFullWShadow.png)

![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/xFN10x/bedrockR/gradle.yml)
![Hackatime](https://hackatime-badge.hackclub.com/U0923KXMGUR/bedrockR)

A Minecraft Bedrock GUI-Based Addon maker, for Windows, Linux, and Chromebook.

## Why?

Minecraft bedrock Addons are experimental, and very hard to create compared to Java mods. With this tool, you will be able to create addons, that fully showcase MCPEs addon capibilitys, and unlike other tools, like MCreator, This will be updated along side Minecraft, and it will let you *live* test your mods, using Minecrafts built-in tools.

## Source Code

This app was made with Visual Studio Code, and it is fully setup to be used with it.

bedrockR is made with Java 21, and it is tested, and known to work with [OpenJDK](https://openjdk.org/). If you don't know which OpenJDK distro to use, use [Microsoft's](https://learn.microsoft.com/en-ca/java/openjdk/download#openjdk-21).

### Debugging/Contributing

It is reccomened to use Visual Studio code to make edits. Here are the extension I reccomend:

- [Debugger for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-debug)
- [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
- [Gradle for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-gradle)
- [Project Manager for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-dependency), and
- [Language Support for Java](https://marketplace.visualstudio.com/items?itemName=redhat.java)

If you can, use this for building when convenient.

### Building

#### JAR

Building a JAR can be done with one command.

(Make sure you are cd'ed into the source code directory)

- First, run

  ```powershell
    ./gradlew (powershell)
    gradlew (cmd prompt)
  ```

- Then, run the last command,

  ```powershell
  ./gradlew shadowJar (powershell)
    gradlew shadowJar (cmd prompt)
  ```

- Now you should be able to find the JAR in `app/build/builtJars`

#### MSI/DEB

This is probably what you want to do when building, as the following commands make an installer depending on what platform you are on.

(Make sure you execute these in the project directory)

- Run
  
  ```powershell
    ./gradlew build jpackage (powershell/terminal)
    gradlew build jpackage (cmd prompt)
  ```
Now check `app/build/builtDist`, and you should see your platform's distribution.
