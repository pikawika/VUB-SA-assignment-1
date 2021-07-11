# Assignment 1 of Software Architecture course

This is the GitHub repository for the first assignment of the Software Architecture course @ VUB 2020-2021. This assignment was made in the second examination period.

## Table of contents

> - [Student info](#student-info)
> - [Important files and folders](#important-files-and-folders)
> - [Notes on the code](#notes-on-the-code)
> - [Running the code](#running-the-code)
> - [Validated output](#validated-output)

## Student info
- **Name**: Bontinck Lennert
- **Email**: lennert.bontinck@vub.be
- **StudentID**: 568702
- **Affiliation**: VUB - Master Computer Science: AI

## Important files and folders
- [Assignment PDF](assignment.pdf)
- [Report containing explanation on solution of the assignment](Lennert-Bontinck-SA1.pdf)
- [Written code folder](code/)

## Notes on the code

- The code has been developed on MacOS Big Sur by using the IntelliJ IDEA 2021.1.3 Ultimate Edition and the "Scala" plugin by JetBrains.
   - It was validated to work on a Windows 10 machine as well using the same software.
- For this project the following version of base software are used (same as WPOs)
   - JRE and JDK 1.8.291
   - sbt 1.3.13
- The sbt build wil provide the following:
   - akka-stream 2.5.32
   - akka-actor 2.5.32
   - akka-stream-alpakka-csv 2.0.2

## Running the code

- Open the ```build.sbt``` file available under ```code\Lennert-Bontinck-SA1\build.sbt``` with the the IntelliJ IDEA.
- Select ```Open as Project``` and select ```Trust Project```.
- The IntelliJ IDEA should build the ```build.sbt``` file providing the Akka dependencies. If all base software was installed with the same versions as used for this assignment, it should provide the correct SDKs as well.
- In the ```Project``` pane navigate to ```src\main\scala\Lennert_Bontinck_SA1``` and:
   - Rightclick on ```MainDisplay``` and choose ```Run 'MainDisplay'``` to start the application which executes all code and prints the output.
   - Rightclick on ```Main``` and choose ```Run 'Main'``` to start the application which executes all code and saves the output as required by the assignment.
- The saved output is available as ```Lennert-Bontinck-SA1-dependencies.txt``` and ```Lennert-Bontinck-SA1-statistics.txt``` under ```code\Lennert-Bontinck-SA1\src\main\resources\result```.

## Validated output

Listed below is a selection of the output available under ```Lennert-Bontinck-SA1-dependencies.txt```. These are equal to the samples given!

```
br.com.objectos:assertion:0.1.0 --> Compile: 9 Provided: 0 Runtime: 0 Test: 4

br.com.objectos:assertion:0.1.1 --> Compile: 9 Provided: 0 Runtime: 0 Test: 0

ai.grakn:grakn-test-profiles:1.1.0 --> Compile: 1 Provided: 0 Runtime: 0 Test: 0

br.com.objectos:assertion:0.1.2 --> Compile: 9 Provided: 0 Runtime: 0 Test: 0
```

Listed below is the output available under ```Lennert-Bontinck-SA1-statistics.txt```. These are not equal to the sample given, however, they are more logical then the sample given and most likely correct.

```
Considered minimum number of dependencies: 2 
Compile: 30207
Provided: 2 
Runtime: 1568
Test: 2 
```