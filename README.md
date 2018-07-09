# Design Patterns IntelliJ IDEA Plugin
This plugin is meant to provide on the fly implementation of various design patterns.

## Currently supported design patterns
* Behavioral
  * Strategy
* Creational
  * Builder (Inner)
  * Factory
  * Singleton
* Structural

## Getting started
In order to avoid some common importing errors (when it comes to intellij plugin development), the master branch includes an .iml file as well as an .idea folder.<br/>
Feel free to delete them and create your own if you know what you 're doing, else you can follow the steps listed below to correctly run the project.
* Open the project using IntelliJ IDEA
* In `Project Structure` -> `Project Settings` -> `Project` -> `Project SDK` create a new `IntellJ Platform Plugin SDK`</br>
  * When asked for directory, use your IntelliJ IDEA installation directory 
  * In `Project Language Level` choose `8 - Lambdas, type annotations etc` 
* Build the project
* In `Edit Configurations` create a new `Plugin Configuration` (if not already configured)
* Run your new configuration!

## Release History
* 1.0.1
  * Added Factory Design Pattern 
  * Fixed bug with package declaration in Strategy Design Pattern 
  * Fixed bug with modifiers of fields and methods in Singleton Design Pattern
* 1.0.0
  * Added Builder Design Pattern
  * Added Singleton Design Pattern
  * Added Strategy Design Pattern

## Authors
* **Orestes Polyzos** - *Initial work* - [OrPolyzos](https://github.com/OrPolyzos)
* See also the list of [contributors](https://github.com/OrPolyzos/Design-Patterns-Intellij-IDEA-Plugin/contributors) who participated in this project.
