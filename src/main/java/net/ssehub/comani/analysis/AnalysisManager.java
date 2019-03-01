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

import net.ssehub.comani.core.AbstractManager;
import net.ssehub.comani.core.Logger;
import net.ssehub.comani.core.Logger.MessageType;
import net.ssehub.comani.data.CommitQueue;
import net.ssehub.comani.utility.InfrastructureUtilities;

/**
 * This class manages the entire commit analysis process.
 * 
 * @author Christian Kroeher
 *
 */
public class AnalysisManager extends AbstractManager {
    
    /**
     * The string representation of the properties' key identifying the fully-qualified class name of the analyzer to
     * use.
     */
    public static final String PROPERTY_ANALYSIS_ANALYZER = "analysis.analyzer";
    
    /**
     * The string representation of the properties' key identifying the path to the directory to which the analysis
     * results shall be saved.
     */
    public static final String PROPERTY_ANALYSIS_OUTPUT = "analysis.output";
    
    /**
     * The identifier if this class, e.g. for printing messages.
     */
    private static final String ID = "AnalysisManager";
    
    /**
     * The commit analyzer for analyzing extracted commits.
     */
    private AbstractCommitAnalyzer commitAnalyzer;
    
    /**
     * Constructs the analysis manager.
     * 
     * @param operatingSystem the name of the operating system on which the tool is currently executed
     * @param versionControlSystem the name of the version control system from which the commits are extracted
     * @param analysisProperties the properties of the properties file defining the analysis process and the
     *        configuration of the analyzer in use; all properties, which start with the prefix "<tt>analysis.</tt>"
     * @param commitQueue the {@link CommitQueue} for requesting extracted commits
     */
    public AnalysisManager(String operatingSystem, String versionControlSystem, Properties analysisProperties,
            CommitQueue commitQueue) {
        super(operatingSystem, versionControlSystem, analysisProperties, commitQueue);
        this.commitAnalyzer = null;
        plugInClassName = properties.getProperty(PROPERTY_ANALYSIS_ANALYZER);
        outputDirectory = properties.getProperty(PROPERTY_ANALYSIS_OUTPUT);
        logger.log(ID, this.getClass().getName() + " created", null, MessageType.DEBUG);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setup() {
        boolean setupSuccessful = false;
        commitAnalyzer = InfrastructureUtilities.getInstance().instantiateAnalyzer(plugInClassName, properties,
                commitQueue);
        if (commitAnalyzer != null) {
            logger.log(ID, "Commit analyzer \"" + plugInClassName + "\" instantiated", null,
                    Logger.MessageType.INFO);
            setupSuccessful = true;
        }
        return setupSuccessful;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean go() {
        if (!commitQueue.isOpen()) {
            // Wait until ExtractionManager opens the commit queue
            logger.log(ID, "Waiting for commit queue to be open", null, MessageType.INFO);
            while (!commitQueue.isOpen()) {
                logger.log(ID, "Still waiting for commit queue to be open", null, MessageType.DEBUG);                
            }
        }
        logger.log(ID, "Analysis started", null, MessageType.INFO);
        boolean analysisSuccessful = commitAnalyzer.analyze();
        logger.log(ID, "Analysis finished", null, MessageType.INFO);
        return analysisSuccessful;
    }
}
