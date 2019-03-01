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
package net.ssehub.comani.core;

import java.util.Properties;

import net.ssehub.comani.core.Logger.MessageType;
import net.ssehub.comani.data.CommitQueue;

/**
 * This abstract class defines common methods and elements of the specific commit extraction and analysis managers.
 *  
 * @author Christian Kroeher
 *
 */
public abstract class AbstractManager extends Thread {

    /**
     * The logger for displaying messages on the console.
     */
    protected Logger logger;
    
    /**
     * The name of the operating system on which the tool is currently executed.
     */
    protected String operatingSystem;
    
    /**
     * The name of the version control system from which the commits are extracted.
     */
    protected String versionControlSystem;
    
    /**
     * The fully-qualified class name of the desired extractor or analyzer (plug-in). The default value is
     * <code>null</code>, which should be changed by the specific managers based on their respective properties.
     */
    protected String plugInClassName;
    
    /**
     * The properties determining the extraction or analysis process, e.g., the fully-qualified class name of the
     * respective plug-ins to use, or the path to the directory, in which extracted commits and analysis results
     * shall be saved. This properties object contains all properties defined in the properties file, which start
     * with the prefix:
     * <ul>
     * <li> "<tt>extraction.</tt>" for extraction properties and
     * <li> "<tt>analysis.</tt>" for analysis properties
     * </ul>
     * Extraction properties will not be available for analyses and vice-versa.
     */
    protected Properties properties;

    /**
     * The {@link CommitQueue} for transferring commits from an extractor to an analyzer.
     */
    protected CommitQueue commitQueue;
    
    /**
     * The string defining the output directory in which extracted commits or analysis results will be saved. The
     * default value is <code>null</code>, which should avoid saving commits or results to a file. This is changed in
     * accordance to the respective properties if they are defined.
     */
    protected String outputDirectory;
    
    /**
     * The flag for indicating whether this manager is ready to {@link #run()} (<code>true</code>) or not
     * (<code>false</code>). If its values is <code>false</code> {@link #run()} will do nothing. Execute
     * {@link #isReady} to change the flag value.
     */
    protected boolean isReady;
    
    /**
     * The flag for indicating whether errors occurred during extraction or analysis (<code>false</code>) or not
     * (<code>true</code>).
     */
    private boolean noErrors;
    
    /**
     * Constructs an abstract manager.
     * 
     * @param operatingSystem the name of the operating system on which the tool is currently executed
     * @param versionControlSystem the name of the version control system from which the commits are extracted
     * @param properties the properties of the properties file determining the particular process and the configuration
     *        of the specific plug-in to use
     * @param commitQueue the {@link CommitQueue} for transferring commits from an extractor to an analyzer
     */
    protected AbstractManager(String operatingSystem, String versionControlSystem, Properties properties,
            CommitQueue commitQueue) {
        logger = Logger.getInstance();
        this.operatingSystem = operatingSystem;
        this.versionControlSystem = versionControlSystem;
        this.properties = properties;
        this.commitQueue = commitQueue;
        plugInClassName = null;
        outputDirectory = null;
        isReady = false;
        noErrors = false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (isReady) {            
            noErrors = go();
        }
    }
    
    /**
     * Returns whether errors occurred during extraction or analysis (<code>false</code>) or not
     * (<code>true</code>).
     * @return <code>true</code> if no errors occurred; <code>false</code> otherwise
     */
    public boolean noErrors() {
        logger.log("AbstractManager", "No errors during execution: " + noErrors, null, MessageType.DEBUG);
        return noErrors;
    }
    
    /**
     * Returns whether this manager is ready to {@link #run()} (<code>true</code>) or not
     * (<code>false</code>). If the values is <code>false</code> {@link #run()} will do nothing. Executing
     * this method will change the value if setting up and configuring the manager was successful.
     * 
     * @return <code>true</code> if this manager is ready for execution; <code>false</code> otherwise
     */
    protected boolean isReady() {
        if (!isReady) {
            isReady = setup();
        }
        return isReady;
    }
    
    /**
     * Performs the necessary setup and configuration of this manager before it is ready to {@link #run()}.
     * 
     * @return <code>true</code> this manager is ready for execution; <code>false</code> otherwise
     */
    protected abstract boolean setup();
    
    /**
     * Starts the extraction or analysis process using the properties given during construction of this object.
     * Note that method is only called if {@link #setup()} returned <code>true</code>.
     * 
     * @return <code>true</code> if the extraction or analysis process was successful; <code>false</code> otherwise
     */
    protected abstract boolean go();
    
}
