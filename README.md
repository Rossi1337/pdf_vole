![Project Logo](https://github.com/Rossi1337/pdf_vole/blob/master/misc/logo.png "Logo")
# pdf_vole
PDF debugger and structure analysis tool

Introduction:
-------------
PDF Vole is a small tool to debug and analyze PDF files. You can open a 
PDF file and analyze it's internal structure and content. 
I started this project to build a tool as I had to debug and analyze PDF files 
that did not render correctly or were broken in some way. This tool also 
helped me a lot in understanding how things work inside of PDF files. 
So if you need a tool to debug PDF files or if you are just interested in 
learning how things work in the PDF file specification then this tool may be 
helpful to you.

Building:
------
Use the supplied Ant task to build the application.

Usage:
------
To start the tool you need Java 1.6
Launch the tool with 

**java -jar pdfvole.jar**

(Note that the jar file name may change from release to release)
In windows you can double click the jar file in explorer if 
Java is registered correctly to .jar files.

If you get OutOfMemory exceptions when loading big pdf files, increase
the VM memory by adding -Xmx512m to the java command: 

**java -Xmx512m -jar pdfvole-20090112beta.jar**  

License:
--------
PDf Vole is distributed under BSD license. See LICENSE for details.

Have fun

	- Bernd Rosstauscher