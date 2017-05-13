# UML Parser using Java

A UML Parser which converts given Java Source Code into
  1. UML Class Diagrams
  2. UML Sequence Diagrams

***
### Instructions

#### Requirements :
- Internet connectivity ( For YUML APIs)
- Java JDK version 1.8

The program runs as below:

Program accepts input in the form of a .zip folder containing Java source code files or a simple folder.
The program will check if the extenstion of the folder is "zip". and if it is, the program will uncompress it,
and then perform next duties, otherwie the program will simply scan the files inside the folder for "java" extension.

Hence, one need not worry about the zipped input or regular folder input, as long as the provided path is correct, the program should run accurately. 


1. Keyword string ("class", "seq"):

2. Path:
  - must be enclosed in double quotes.
  - Fully qualified path of the folder which contains all the .java source files. 


3. Name of output image file
  - One word string
  - Extension will be assigned as ".png" automatically.

Example:-

In order to generate a UML Class diagram from Java source code, run below command:

```
java -jar umlparser.jar class "\Users\akshaymishra\workspace\tests\uml-parser-test-1.zip" output
```

*creates output.png in the same folder as the input folder*


For the generation of the sequence diagram, additional 2 arguments are required:

4. NName of the class inside which the method resides that for which sequence diagram is to be generated
  * Ex: BuildOrder

5. Name of the actual method for which the sequence diagram needs to be generated
  - Do not include parenthesis after the method name
  - Ex - getOrder


In order to generate a UML Class diagram from Java source code, run below command:

```
java -jar umlparser.jar seq "\Users\akshaymishra\workspace\tests\uml-parser-test-1.zip" Main main output
```

*creates output.png in the same folder as the input folder*

### Details of libraries and tools used



Libraries used:

Parser: https://github.com/javaparser/javaparser
For parsing the java code, javaparser library was used.

Javaparser uses the mechanism of 'CompilationUnit' to parse the code for its classes, fields and the methods.


UML Generation Tool: https://yuml.me/diagram/plain/class/draw/

It's a free tool for the generation of UML diagrams and we are making an HTTP request through Java code. 

We provide the 'Abstract Syntax Tree' (AST) as the grammar for the generation of the diagram in the request.
We then parse it's response into an image file., which contains the UML representation of the source code.


Sequence Generator: PlantUML

We are using PlantUML for the generation of the sequence diagram.


