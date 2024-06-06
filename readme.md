Java Profiler
=============
Java Profiler is a tool for profiling JVM applications. It is a pure Java application that runs on all platforms that support Java. 

Coco/R (https://ssw.jku.at/Research/Projects/Coco/) is used to generate the parser for the profiler.  
The parser is used in the Locator class where all the needed Metadata and code insert points about the classes and methods is stored.

Java Profiler analyses the source code of the application to be profiled and inserts code to measure the time spent in each method. 
The instrumented code is compiled and run.

If methods of the instrumented code are called within another method, the time spent in the called method is
is not included in the time spent in the calling method.

The profiler generates a report in HTML format that shows the time spent in each method.

Usage:

```java
java -jar JavaProfiler-1.0.jar [-v] [-s <srcDir>] [-o <outputDir>] <mainfile> [passArgs]
```
Where:  
`-v` - verbose mode  
`-o <outputDir>` - output directory  
`-s <srcDir>` - source directory, only needed when the program has multiple classes   
`<mainfile>` - java mainfile,   
`[passArgs]` - arguments to pass to the instrumented application

Examples:
```java
java -jar JavaProfiler-1.0.jar c:\Example\Package1\Main.java
c:\Example
    |---Package1
    |     |---Main.java
    |---.profile
          |---Package1
          |     |---Main.java
          |---Measurement
          |     |---_M.java
          |---classes
               |---Package1
               |       |---Main.class
               |---Measurement
               |       |---_M.class
               |---report.html

```

```java
java -jar JavaProfiler-1.0.jar -s c:\Example\SomeProgram c:\Example\Package1\Main.java

c:\Example
    |---SomeProgram  
    |     |---Package1
    |     |     |---Main.java
    |     |---OtherPackage
    |     |     |---OtherClass.java
    |---.profile
          |---Package1
          |     |---Main.java
          |---OtherPackage
          |     |---OtherClass.java
          |---Measurement
          |     |---_M.java
          |---classes
               |---Package1
               |       |---Main.class
               |---OtherPackage
               |       |---OtherClass.class
               |---Measurement
               |       |---_M.class
               |---report.html
```
When no output directory is specified, the output is written in the subdirectory `.profile` of the source directory,
otherwise, the output is written in the subdirectory `.profile` specified directory.

    

Coco/R
======
https://ssw.jku.at/Research/Projects/Coco/   
Coco/R is a compiler generator, which takes an attributed grammar of a source language and generates a scanner and a parser for this language.   

The attributed grammar for the Java Profiler is located in `coco/Java.atg`, besides the needed files for the scanner and parser generation.
A gradle task `runCoco` is registered to generate the scanner and parser and replaces them in the package `cc.hofstadler`.