## Maven files
All maven-parent-poms are inside this directory. They're published in a [temporary maven repository](https://github.com/ssedevelopment/comani-mvn-repo) until the real maven repository is set up. 
 

### Structure

ComAnI implements a inheterance structure:
```
comani-project (parent)
├── comani-analyses (parent)
│   └── comani-analyses-variabilitychange
├── comani-extractors (parent)
│   ├── comani-extractor-git
│   └── comani-extractor-svn
└── comani-structure
```

*Be aware. The maven artifact names does not match with the repository names.*

- comani-project: Defines global settings for CommitAnalysisInfrastructure like the java version or encoding to use. 
- comani-extractors: Defines the dependency to comani-structure and all offical child modules (git/svn). 
- comani-analyses: Defines the dependency to comani-structure and all offical child modules.

### Deploy 

See https://github.com/ssedevelopment/comani-mvn-repo for published pom files. 

If you modify a project file you should publish them into the maven repository. 

```
mvn -f <project-xml-file> deploy
rsync -av target/mvn-repo/ [...]/pathToMavenRepository/comani-mvn-repo
git --work-tree=/pathToMavenRepository/comani-mvn-repo/ add -A
```
Then commit and push the changes. 


### Build

In order to build a full release you can just build the "comani-project". 
A flat file structure is required which means, all other project are side by side in the same directory as ComAnI/comani-structure. 

```
├── ComAnI
│   └── maven
├── VariabilityChangeAnalyzer 
├── GitCommitExtractor
└── SvnCommitExtractor
```

Then run a maven task like `package` on comani-project.

```
mvn -f comani-project package
```


