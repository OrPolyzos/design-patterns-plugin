# Design Patterns _(Plugin for IntelliJ IDEA)_
This plugin is meant to provide on the fly implementation of various design patterns.
<br/>
You can download it directly from IntelliJ IDEA or from https://plugins.jetbrains.com/plugin/10856-design-patterns-plugin

## Currently supported design patterns
* __Behavioral__
  * Strategy
* __Creational__
  * Builder (Inner)
  * Factory
  * Singleton
* __Structural__

## How to use
Assuming you have already installed the plugin, you can use it just by right-clicking inside a .java file while your mouse is inside a code block that defines a class. This will bring up the editor menu and the first option will be Design Patterns, where you can choose which one should be implemented.<br/>
![Demonstration screenshot](/../screenshots/Demonstration.png?raw=true)<br/>
![Demo gif](/../screenshots/BuilderDemo.gif?raw=true)

## Getting started
In order to avoid some common importing errors (when it comes to intellij plugin development), please follow the steps listed below.<br/>
* Open the project using IntelliJ IDEA (Community or Ultimate)
* In `Project Structure` -> `Project Settings` -> `Project` -> `Project SDK` create a new `IntellJ Platform Plugin SDK`</br>
  * When asked for directory, use your IntelliJ IDEA installation directory.
  * Note that you will need IntelliJ IDEA Community in order to be able to properly debug the SDK's core code. If no debug is required though, Ultimate will work just fine as well.
* In `Project Language Level` choose `8 - Lambdas, type annotations etc` 
* In `Project compiler output` choose the out folder (for example `C:\Users\design-patterns\out`
* Use `gradlew runIde` to run the plugin

## Release History
* <strong>2.0.2</strong>
    * Changes (narrows down) support for IntelliJ IDEA versions since build _172.*_ until build _191.*_
    * Fixes typo with the title of the Builder
    * Major code refactoring (extracted lib package for base classes in order to be moved in a separate library at some point)
* <strong>2.0.1</strong>
    * Fixes bug that was causing _NullPointerException_ when clicking on non .java files
    * Adds support for IntelliJ IDEA versions since build _145.20_ until build _191.*_
    * Code refactoring
* <strong>2.0.0</strong>
    * Fixes bug that was causing ActionDuplicationException during IntelliJ IDEA launch
    * Migrates plugin's build tool from DevKit to Gradle
    * Changes Singleton DP and Builder DP to use resource templates instead of String literals
    * Adds support for IntelliJ IDEA versions since build _145.20_ until build _183.*_
* <strong>1.1.0</strong>
    * Updates Builder DP, so that it can now handle mandatory fields (if the user wants to)
    * Fixes bug in Builder DP, that would produce multiple constructors when ran again
    * Fixes bug in Strategy/Factory DP, that would cause error, when running it in a class without package statement
    * Fixes bug in Factory DP, that would produce multiple constructors when ran again
    * Fixes bug in Factory DP, that would not always find all implementors of an interface
* <strong>1.0.1</strong>
    * Adds Factory DP 
    * Fixes bug with package declaration in Strategy DP
    * Fixes bug with modifiers of fields and methods in Singleton DP
* <strong>1.0.0</strong>
    * Adds Builder DP
    * Adds Singleton DP
    * Adds Strategy DP

## Authors
* **Orestes Polyzos** - *Initial work* - [OrPolyzos](https://github.com/OrPolyzos)