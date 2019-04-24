## Maven files
All maven-parent-poms are inside this directory. They're published in a [temporary maven repository](https://github.com/ssedevelopment/comani-mvn-repo) until the real maven repository is set up. 
 

### Structure

ComAnI implements a inheterance structure:
```
comani-project (project parent - no submodules defined)
├── comani-extractir-svn
├── comani-extractor-git
└── comani-structure
```
- comani-project: Defines global settings for CommitAnalysisInfrastructure like the java version or encoding to use. 
- comani-extractor-git: Standalone project for extracting Git commits. See [GitCommitExtractor](https://github.com/CommitAnalysisInfrastructure/GitCommitExtractor)
- comani-extractor-svn: Standalone project for extracting SVN commits. See [SvnCommitExtractor](https://github.com/CommitAnalysisInfrastructure/SvnCommitExtractor)

*Be aware. The maven artifact names does not match with the repository names.*


### Deploy 

See https://github.com/ssedevelopment/comani-mvn-repo for published pom files. 

If you modify a project file, you should publish them into the maven repository. 

```
mvn -f <project-xml-file> deploy
rsync -av target/mvn-repo/ [...]/pathToMavenRepository/comani-mvn-repo
git --work-tree=/pathToMavenRepository/comani-mvn-repo/ add -A
```
Then commit and push the changes. 


### Build

In order to build a full release you can just build the "comani-project". 
A flat file structure is required which means, all other project are side by side in the same directory as ComAnI/comani-structure. There is an additional project `comani-build` which builds all modules. It is an additional layer only for build purposes.

Directory structure:
```
├── ComAnI
│   └── maven
├── VariabilityChangeAnalyzer 
├── GitCommitExtractor
└── SvnCommitExtractor
```

Then run a maven task like `package` on comani-build.

```
mvn -f comani-build.pom package
```
