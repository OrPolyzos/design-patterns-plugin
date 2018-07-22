# Design Patterns IntelliJ IDEA Plugin
This plugin is meant to provide on the fly implementation of various design patterns. <br/>
You can download it directly from IntelliJ IDEA or from https://plugins.jetbrains.com/plugin/10856-design-patterns-plugin

## Currently supported design patterns
* Behavioral
  * Strategy
* Creational
  * Builder (Inner)
  * Factory
  * Singleton
* Structural

## How to use
Assuming you have already installed the plugin, you can use it just by right-clicking inside a .java file while your mouse is inside a code block that defines a class. This will bring up the editor menu and the first option will be Design Patterns, where you can choose which one should be implemented.<br/>
![Demonstration screenshot](/../screenshots/Demonstration.png?raw=true)<br/>
![Demo gif](/../screenshots/BuilderDemo.gif?raw=true)

## Getting started
In order to avoid some common importing errors (when it comes to intellij plugin development), please follow the steps listed below.<br/>
* Open the project using IntelliJ IDEA
  * When asked choose an `.idea` based project
* In `Project Structure` -> `Project Settings` -> `Project` -> `Project SDK` create a new `IntellJ Platform Plugin SDK`</br>
  * When asked for directory, use your IntelliJ IDEA installation directory 
* In `Project Language Level` choose `8 - Lambdas, type annotations etc` 
* In `Project compiler output` choose the out folder (for example `C:\Users\Design-Patterns-Intellij-IDEA-Plugin\out`
* Edit the generated .iml file with the following content so that IntelliJ understands the project is a plugin module
```
<?xml version="1.0" encoding="UTF-8"?>
<module type="PLUGIN_MODULE" version="4">
  <component name="DevKit.ModuleBuildProperties" url="file://$MODULE_DIR$/resources/META-INF/plugin.xml" />
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/src" isTestSource="false" />
      <sourceFolder url="file://$MODULE_DIR$/resources" type="java-resource" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
  </component>
</module>
```
* Build the project
* In `Edit Configurations` create a new `Plugin Configuration` (if not already configured)
* Run your new configuration!

## Release History
* <strong>1.1.0</strong>
  * Updated Builder DP, so that it can now handle mandatory fields (if the user wants to).
  * Fixed bug in Builder DP, that would produce multiple constructors when ran again.
  * Fixed bug in Strategy/Factory DP, that would cause error, when running it in a class without package statement.
  * Fixed bug in Factory DP, that would produce multiple constructors when ran again.
  * Fixed bug in Factory DP, that would not always find all implementors of an interface.  
* <strong>1.0.1</strong>
  * Added Factory Design Pattern 
  * Fixed bug with package declaration in Strategy Design Pattern 
  * Fixed bug with modifiers of fields and methods in Singleton Design Pattern
* <strong>1.0.0</strong>
  * Added Builder Design Pattern
  * Added Singleton Design Pattern
  * Added Strategy Design Pattern

## Authors
* **Orestes Polyzos** - *Initial work* - [OrPolyzos](https://github.com/OrPolyzos)
* See also the list of [contributors](https://github.com/OrPolyzos/Design-Patterns-Intellij-IDEA-Plugin/contributors) who participated in this project.
