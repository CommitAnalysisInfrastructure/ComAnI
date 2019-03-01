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
import java.util.List;
import java.util.Properties;

import net.ssehub.comani.core.Logger;
import net.ssehub.comani.core.Setup;
import net.ssehub.comani.data.IExtractionQueue;

/**
 * This abstract class defines the methods to be implemented by and the common elements, like the global {@link Logger},
 * for each specific commit extractor.
 *  
 * @author Christian Kroeher
 *
 */
public abstract class AbstractCommitExtractor {

    /**
     * The global logger for printing messages.
     */
    protected Logger logger;
    
    /**
     * The properties determining the extraction process, e.g., the fully-qualified class name of the extractor to use,
     * or the path to the directory, in which extracted commits shall be saved. In general, this properties object
     * contains all properties defined in the properties file, which start with the prefix "<tt>extraction.</tt>".
     */
    protected Properties extractionProperties;

    /**
     * The commit queue to which each extracted commit is send. The same queue is used by an analysis for analyzing the
     * extracted commits.
     */
    protected IExtractionQueue commitQueue;
    
    /**
     * Constructs a new instance of this extractor.
     * 
     * @param extractionProperties the properties of the properties file defining the extraction process and the
     *        configuration of the extractor in use; all properties, which start with the prefix "<tt>extraction.</tt>"
     *        as well as the properties defining the operating system and the version control system
     * @param commitQueue the {@link IExtractionQueue} for transferring commits from an extractor to an analyzer
     * @throws ExtractionSetupException if the extractor is not supporting the current operating or version control
     *         system
     */
    public AbstractCommitExtractor(Properties extractionProperties, IExtractionQueue commitQueue)
            throws ExtractionSetupException {
        logger = Logger.getInstance();
        if (extractionProperties == null) {
            throw new ExtractionSetupException("Missing extraction properties");
        }
        if (commitQueue == null) {
            throw new ExtractionSetupException("Missing commit queue");
        }
        this.extractionProperties = extractionProperties;
        this.commitQueue = commitQueue;
        // Check for existence of OS and VCS properties already done in setup
        String propertyValue = extractionProperties.getProperty(Setup.PROPERTY_CORE_OS);
        if (!this.operatingSystemSupported(propertyValue)) {
            throw new ExtractionSetupException("Operating system \"" + propertyValue 
                    + "\" not supported");
        }
        propertyValue = extractionProperties.getProperty(Setup.PROPERTY_CORE_VERSION_CONTROL_SYSTEM);
        if (!this.versionControlSystemSupported(propertyValue)) {
            throw new ExtractionSetupException("Version control system \"" + propertyValue + "\" not supported");
        }
    }
    
    /**
     * Starts this commit extractor for extracting all commits from the given repository.
     * 
     * @param repository the {@link File} representing the repository directory; never <code>null</code> and always a
     *                   directory
     * @return <code>true</code> if extracting the commits was successful; <code>false</code> otherwise
     */
    public abstract boolean extract(File repository);
    
    /**
     * Starts this commit extractor for extracting all commits of the given commit list from the given repository.
     * 
     * @param repository the {@link File} representing the repository directory; never <code>null</code> and always a
     *                   directory
     * @param commitList the {@link List} of commits (commit numbers), which have to be extracted from the given
     *        repository; never <code>null</code> nor <i>empty</i>
     * @return <code>true</code> if extracting the commits was successful; <code>false</code> otherwise
     */
    public abstract boolean extract(File repository, List<String> commitList);
    
    /**
     * Starts this commit extractor to parse the given string to a {@link net.ssehub.comani.data.Commit} object.
     * 
     * @param commit the string representation of a single commit; never <code>null</code> nor <i>empty</i>
     * @return <code>true</code> if parsing the given commit string to a commit object was successful;
     *         <code>false</code> otherwise
     */
    public abstract boolean extract(String commit);
    
    /**
     * Checks if the given operating system is supported by this commit extractor.
     * 
     * @param operatingSystem the name of the operating system as provided by
     *        <code>System.getProperty("os.name")</code>; is never <code>null</code> nor empty
     * @return <code>true</code> if <code>operatingSystem</code> is supported by this commit extractor;
     *         <code>false</code> otherwise
     */
    public abstract boolean operatingSystemSupported(String operatingSystem);
    
    /**
     * Checks if the version control system specified in the configuration file of the tool is supported by this commit
     * extractor.
     * 
     * @param versionControlSystem the name of the version control system as defined by the user in the tools
     *        configuration file; is never <code>null</code> nor empty
     * @return <code>true</code> if <code>versionControlSystem</code> is supported by this commit extractor;
     *         <code>false</code> otherwise
     */
    public abstract boolean versionControlSystemSupported(String versionControlSystem);
    
}
