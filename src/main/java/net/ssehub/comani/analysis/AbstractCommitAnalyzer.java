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
package net.ssehub.comani.analysis;

import java.util.Properties;

import net.ssehub.comani.core.Logger;
import net.ssehub.comani.core.Setup;
import net.ssehub.comani.data.IAnalysisQueue;

/**
 * This abstract class defines the methods to be implemented by and the common elements, like the global {@link Logger},
 * for each specific commit analyzer.
 *  
 * @author Christian Kroeher
 *
 */
public abstract class AbstractCommitAnalyzer {
    
    /**
     * The global logger for printing messages.
     */
    protected Logger logger;
    
    /**
     * The properties determining the analysis process and the analyzer itself. In general, this properties object
     * contains all properties defined in the properties file, which start with the prefix "<tt>analysis.</tt>".
     */
    protected Properties analysisProperties;
    
    /**
     * The commit queue from which extracted commit can be obtained. The same queue is used by an extractor for
     * extracting the commits.
     */
    protected IAnalysisQueue commitQueue;
    
    /**
     * Constructs a new instance of this analyzer.
     * 
     * @param analysisProperties the properties of the properties file defining the analysis process and the
     *        configuration of the analyzer in use; all properties, which start with the prefix "<tt>analysis.</tt>"
     * @param commitQueue the {@link IAnalysisQueue} for transferring commits from an extractor to an analyzer
     * @throws AnalysisSetupException if the analyzer is not supporting the current operating or version control
     *         system
     */
    public AbstractCommitAnalyzer(Properties analysisProperties, IAnalysisQueue commitQueue)
            throws AnalysisSetupException {
        logger = Logger.getInstance();
        this.analysisProperties = analysisProperties;
        this.commitQueue = commitQueue;
        // Check for existence of OS and VCS properties already done in setup
        String propertyValue = analysisProperties.getProperty(Setup.PROPERTY_CORE_OS);
        if (!this.operatingSystemSupported(propertyValue)) {
            throw new AnalysisSetupException("Operating system \"" + propertyValue 
                    + "\" not supported");
        }
        propertyValue = analysisProperties.getProperty(Setup.PROPERTY_CORE_VERSION_CONTROL_SYSTEM);
        if (!this.versionControlSystemSupported(propertyValue)) {
            throw new AnalysisSetupException("Version control system \"" + propertyValue + "\" not supported");
        }
    }

    /**
     * Starts this commit analyzer.
     * 
     * @return <code>true</code> if analyzing the commits was successful; <code>false</code> otherwise
     */
    public abstract boolean analyze();
    
    /**
     * Checks if the given operating system is supported by this commit analyzer.
     * 
     * @param operatingSystem the name of the operating system as provided by
     *        <code>System.getProperty("os.name")</code>; is never <code>null</code> nor empty
     * @return <code>true</code> if <code>operatingSystem</code> is supported by this commit analyzer;
     *         <code>false</code> otherwise
     */
    public abstract boolean operatingSystemSupported(String operatingSystem);
    
    /**
     * Checks if the version control system specified in the configuration file of the tool is supported by this commit
     * analyzer.
     * 
     * @param versionControlSystem the name of the version control system as defined by the user in the tools
     *        configuration file; is never <code>null</code> nor empty
     * @return <code>true</code> if <code>versionControlSystem</code> is supported by this commit analyzer;
     *         <code>false</code> otherwise
     */
    public abstract boolean versionControlSystemSupported(String versionControlSystem);
}
