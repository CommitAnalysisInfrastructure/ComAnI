# ComAnI
The Commit Analysis Infrastructure (ComAnI) is an open and configurable infrastructure for the extraction and analysis of commits from software repositories. For both tasks, individual plug-ins realize different extraction and analysis capabilities, which rely on the same data model provided by the infrastructure. Hence, any combination of extraction and analysis plug-ins is possible.
![ComAnI Overview](/guide/inserts/comani_overview.png "ComAnI Overview")
ComAnI enables conducting an analysis for a software hosted in a Git repository first and later conducting the same analysis for a different software hosted by SVN (assuming that the analysis is able to cope with the artifacts and their technologies of the new software under analysis). Another example is to use the same commit extractor, e.g., supporting the commit extraction from Git repositories, for different analyses. The definition of a particular ComAnI instance consists of a set of configuration parameters saved in a configuration file, which the infrastructure reads at start-up. Hence, there is no implementation effort needed. The infrastructure automatically performs its internal setup, loads and starts the desired plug-ins.

The [ComAnI Guide](/guide/ComAnI_Guide.pdf) provides more information about the capabilities of the infrastructure. Note that this guide is currently incomplete and will be updated in future to cover all aspects of the infrastructure.

## Installation
Download the [ComAnI.jar](/release/ComAnI.jar) file from the release directory and save it to a directory of your choice on your machine.

*Requirements:*
- Java 8 or equivalent has to be installed (more recent versions will be supported in future)

## Execution
The infrastructure can be executed as a typical Java Jar-file, which expects the following parameters:
- Mandatory: A configuration (properties) file, which defines the particular ComAnI instance and its setup. The available configuration options and their valid valued are described in the ComAnI Guide.
- Optional: the “-i” option can be used to perform a single commit extraction. It has to be followed by the commit information, which is terminated by a last line containing the string “!q!”.

Examples:
```Properties
java -jar ComAnI.jar /path/to/configuration-file.properties
```
or
```Properties
java -jar ComAnI.jar /path/to/configuration-file.properties –i <CommitContent>\n!q!
```
