# ComAnI

## Work in Progress: more descriptions and guidance will follow!

### Introduction
The Commit Analysis Infrastructure (ComAnI) is an open and configurable infrastructure for the extraction and analysis of commits from software repositories. For both tasks, individual plug-ins realize different extraction and analysis capabilities, which rely on the same data model provided by the infrastructure. Hence, any combination of extraction and analysis plug-ins is possible. For example, we could first conduct an analysis for a software hosted in a Git repository and later conduct the same analysis for a different software hosted by SVN (assuming that the analysis is able to cope with the artifacts and their technologies of the new software under analysis). Another example is to use the same commit extractor, e.g., supporting the commit extraction from Git repositories, for different analyses. The definition of a particular ComAnI instance consists of a set of configuration parameters saved in a configuration file, which the infrastructure reads at start-up. Hence, there is no implementation effort needed. The infrastructure automatically performs its internal setup, loads and starts the desired plug-ins.

![ComAnI Overview](/guide/inserts/comani_overview.png "ComAnI Overview")

For more information, please have a look at the [ComAnI Guide](/guide/ComAnI_Guide.pdf). Note that also this guide is currently incomplete and will be updated in future to cover all aspects of the infrastructure.
