/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.ssehub.comani.extraction;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Properties;

import net.ssehub.comani.core.AbstractManager;
import net.ssehub.comani.core.Logger;
import net.ssehub.comani.core.Logger.MessageType;
import net.ssehub.comani.core.Setup;
import net.ssehub.comani.data.Commit;
import net.ssehub.comani.data.CommitQueue;
import net.ssehub.comani.data.CommitQueue.QueueState;
import net.ssehub.comani.data.CommitSerializer;
import net.ssehub.comani.utility.FileUtilities;
import net.ssehub.comani.utility.InfrastructureUtilities;

/**
 * This class manages the entire commit extraction process.
 * 
 * @author Christian Kroeher
 *
 */
public class ExtractionManager extends AbstractManager {
    
    /**
     * The identifier of this class, e.g., for printing messages.
     */
    private static final String ID = "ExtractionManager";
    
    /**
     * The commit extractor for extracting commits from a specified repository.
     */
    private AbstractCommitExtractor commitExtractor;
    
    /**
     * The content of a single commit, which shall be extracted to an internal {@link commits.Commit} for analysis.
     */
    private String commitString;
    
    /**
     * The directory, which contains previously extracted commits for reuse. Its value is set by the {@link #setup()}
     * method depending on the optional, user-defined value of the {@link Setup#PROPERTY_EXTRACTION_REUSE} property.
     * Hence, the value may be <code>null</code>, if that property is not defined, meaning that there are no commits
     * to reuse.
     */
    private File reusableCommitsDirectory;
    
    /**
     * The directory for saving extracted commits. Its value is set by the {@link #setup()} method depending on the
     * optional, user-defined value of the {@link Setup#PROPERTY_EXTRACTION_CACHING} property. Hence, the value may be
     * <code>null</code>, if that property is not defined, meaning that no commits shall be saved for reuse.
     */
    private File commitsCacheDirectory;
    
    /**
     * Constructs the extraction manager.
     * 
     * @param operatingSystem the name of the operating system on which the tool is currently executed
     * @param versionControlSystem the name of the version control system from which the commits shall be extracted
     * @param extractionProperties the properties of the properties file defining the extraction process and the
     *        configuration of the extractor in use; all properties, which start with the prefix "<tt>extraction.</tt>"
     * @param commitQueue the {@link CommitQueue} for sending extracted commits to an analyzer
     * @param commit the content of a single commit, which shall be extracted to an internal 
     *        {@link net.ssehub.comani.data.Commit} for analysis; can be <code>null</code>
     */
    public ExtractionManager(String operatingSystem, String versionControlSystem, Properties extractionProperties,
            CommitQueue commitQueue, String commit) {
        super(operatingSystem, versionControlSystem, extractionProperties, commitQueue);
        this.commitExtractor = null;
        this.commitString = commit;
        reusableCommitsDirectory = null;
        commitsCacheDirectory = null;
        plugInClassName = properties.getProperty(Setup.PROPERTY_EXTRACTION_CLASS);
        logger.log(ID, this.getClass().getName() + " created", null, MessageType.DEBUG);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean setup() {
        boolean setupSuccessful = false;
        // Is caching enabled, which should reuse already extracted commits?
        String reusableCommitsDirectoryString = properties.getProperty(Setup.PROPERTY_EXTRACTION_REUSE);
        if (reusableCommitsDirectoryString != null) {
            // If the property is available, Setup already checked existence and content of the directory!
            reusableCommitsDirectory = new File(reusableCommitsDirectoryString);
            setupSuccessful = true;
            logger.log(ID, "Reusing commits from \"" + reusableCommitsDirectory.getAbsolutePath() + "\"",
                    "No commit extractor instantiated", Logger.MessageType.INFO);
        } else {
            // No reusable commits available, hence, we need the extractor
            commitExtractor = InfrastructureUtilities.getInstance().instantiateExtractor(plugInClassName, properties,
                    commitQueue);
            if (commitExtractor != null) {
                setupSuccessful = true;
                logger.log(ID, "Commit extractor \"" + plugInClassName + "\" instantiated", null,
                        Logger.MessageType.INFO);
                // Is caching enabled, which should save commits extracted by instantiated commit extractor?
                String commitsCacheDirectoryString = properties.getProperty(Setup.PROPERTY_EXTRACTION_CACHING);
                if (commitsCacheDirectoryString != null) {
                    // If the property is available, Setup already checked existence and content of the directory!
                    commitsCacheDirectory = new File(commitsCacheDirectoryString);
                    logger.log(ID, "Saving extracted commits to \"" + commitsCacheDirectory.getAbsolutePath() + "\"",
                            null, Logger.MessageType.INFO);
                }
            }
        }
        return setupSuccessful;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean go() {
        boolean success = false;
        commitQueue.setState(QueueState.OPEN);
        if (reusableCommitsDirectory != null) {
            logger.log(ID, "Reuse started", null, MessageType.INFO);
            success = reuseCachedCommits();
            logger.log(ID, "Reuse finished", null, MessageType.INFO);
        } else {
            logger.log(ID, "Extraction started", null, MessageType.INFO);
            success = executeCommitExtractor();
            logger.log(ID, "Extraction finished", null, MessageType.INFO);
        }
        commitQueue.setState(QueueState.CLOSED);
        return success;
    }
    
    /**
     * Passes the commit objects created by the {@link CommitSerializer} based on the CDMS-files in the given cache
     * directory to the {@link CommitQueue} for analysis. This method does not use a commit extractor.
     * 
     * @return <code>true</code> if the deserialization of each CDMS-file in the given directory and passing the
     *         resulting objects to the commit queue was successful; <code>false</code> if parsing of at least one
     *         CDMS-file failed
     */
    private boolean reuseCachedCommits() {
        boolean reuseSuccessful = true;
        String[] commitCdmsFileNames = reusableCommitsDirectory.list(new FilenameFilter() {
            
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".cdms");
            }
        }); 
        // No further checks, e.g., for the number of available XML-files needed; done by Setup before
        logger.log(ID, "Reusing " + commitCdmsFileNames.length + " commits", null, MessageType.INFO);
        File commitCdmsFile;
        Commit cachedCommit;
        for (int i = 0; i < commitCdmsFileNames.length; i++) {
            commitCdmsFile = new File(reusableCommitsDirectory, commitCdmsFileNames[i]);
            cachedCommit = new Commit();
            if (CommitSerializer.deserialize(cachedCommit, commitCdmsFile)) {
                while (!commitQueue.addCommit(cachedCommit)) {
                    logger.log(ID, "Waiting to add commit to queue", null, MessageType.DEBUG);
                }
            } else {
                reuseSuccessful = false;
                logger.log(ID, "Creating commit object based on CDMS-file \"" 
                        + commitCdmsFileNames[i] + "\" failed",
                        "Skipping this commit (file)", MessageType.ERROR);
            }
        }
        return reuseSuccessful;
    }
    
    /**
     * Executes the specified and instantiated {@link #commitExtractor} to extract commits. If
     * {@link #commitsCacheDirectory} is not <code>null</code>, this method will inform the {@link CommitQueue} to save
     * the commits received by the {@link #commitExtractor} to the given cache directory.
     * 
     * @return <code>true</code> if extraction was successful; <code>false</code> otherwise
     */
    private boolean executeCommitExtractor() {
        // If the cache directory is defined by the user, tell the queue to save each commit provided by the extractor
        if (commitsCacheDirectory != null) {
            commitQueue.enableCommitCaching(commitsCacheDirectory);
        }

        boolean extractionSuccessful = false;
        // If commit string is empty already checked in setup
        if (commitString != null) {
            // Only parse this single commit
            extractionSuccessful = commitExtractor.extract(commitString);
        } else {
            // Extraction from repository (availability of property checked in setup)
            File repositoryDirectory = new File(properties.getProperty(Setup.PROPERTY_EXTRACTION_INPUT));
            // Optional commit list file (if available, all further checks already done in setup)
            String commitListFilePath = properties.getProperty(Setup.PROPERTY_EXTRACTION_COMMIT_LIST);
            if (commitListFilePath != null) {                
                File commitListFile = new File(commitListFilePath);
                List<String> commitList = FileUtilities.getInstance().readFile(commitListFile);
                if (commitList != null && !commitList.isEmpty()) {          
                    // Extract only those commits from repository, which are defined in the file
                    extractionSuccessful = commitExtractor.extract(repositoryDirectory, commitList);
                } else {
                    logger.log(ID, "Commit list file does not contain commit numbers", "Check content of \"" 
                            + commitListFile.getAbsolutePath() + "\"", MessageType.ERROR);
                }
            } else {
                // Extract all commits from repository
                extractionSuccessful = commitExtractor.extract(repositoryDirectory);
            }
        }
        return extractionSuccessful;
    }
}
