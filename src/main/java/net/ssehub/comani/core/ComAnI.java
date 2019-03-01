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

import java.io.File;
import java.util.Date;
import java.util.Properties;

import net.ssehub.comani.analysis.AnalysisManager;
import net.ssehub.comani.core.Logger.MessageType;
import net.ssehub.comani.data.CommitQueue;
import net.ssehub.comani.extraction.ExtractionManager;
import net.ssehub.comani.utility.InfrastructureUtilities;

/**
 * The main class of this tool for preparing and starting the internal processes.
 * 
 * @author Christian Kroeher
 *
 */
public class ComAnI {
    
    /**
     * The identifier if this class, e.g. for printing messages.
     */
    private static final String ID = "ComAnI";
    
    /**
     * The {@link Logger} for printing messages.
     */
    private static Logger logger;
    
    /**
     * The {@link Setup} for reading and providing the properties and the user-defined values of the properties files.
     */
    private static Setup setup;

    /**
     * Represents the starting point of the internal processes and the overall tool.
     * 
     * @param args the set of arguments defined by the user
     */
    public static void main(String[] args) {
        int exitStatus = -1;
        logger = Logger.getInstance();
        try {
            /* 
             * Retrieving a setup instance based on the given arguments includes setting the log-level and printing the
             * infrastructure start message to the console, if the log-level allows that
             */
            setup = Setup.getInstance(args);
            Properties coreProperties = setup.getCoreProperties();

            // Determine and save the current time in milliseconds for calculating the execution duration below 
            long startTimeMillis = System.currentTimeMillis();
                       
            // Run the tool
            if (run(coreProperties)) {
                exitStatus = 0;
            }
            
            // Determine end date and time and display them along with the duration of the overall process execution
            long durationMillis = System.currentTimeMillis() - startTimeMillis;
            int durationSeconds = (int) ((durationMillis / 1000) % 60);
            int durationMinutes = (int) ((durationMillis / 1000) / 60);
            Date date = new Date();
            logger.logInfrastructureTermination(date, durationMinutes, durationSeconds);
        } catch (SetupException e) {
            logger.log(ID, "Creating internal setup failed", e.getMessage(), MessageType.ERROR);
            showUsage();
        }
        logger.log(ID, "Exiting with status " + exitStatus, null, MessageType.DEBUG);
        System.exit(exitStatus);
    }
    
    /**
     * Executes the core processes.
     * 
     * @param coreProperties the core properties as defined in the passed properties file
     * @return <code>true</code> if this run was successful; <code>false</code> otherwise
     */
    private static boolean run(Properties coreProperties) {
        boolean runSuccessful = false;
        String pluginsDirectoryPath = coreProperties.getProperty(Setup.PROPERTY_CORE_PLUGINS_DIR);
        // Check for existence of plug-ins directory is done in setup already
        File pluginsDirectory = new File(pluginsDirectoryPath);
        InfrastructureUtilities.getInstance().setPluginsDirectory(pluginsDirectory);
        // Check for existence and validity of max. number of elements is done in setup already
        int maxCommits = (int) coreProperties.get(Setup.PROPERTY_CORE_COMMIT_QUEUE_MAX_ELEMENTS);
        CommitQueue commitQueue = new CommitQueue(maxCommits);
        ExtractionManager extractionManager = 
                new ExtractionManager(coreProperties.getProperty(Setup.PROPERTY_CORE_OS),
                        coreProperties.getProperty(Setup.PROPERTY_CORE_VERSION_CONTROL_SYSTEM),
                        setup.getExtractionProperties(), commitQueue, setup.getCommitString());                
        AnalysisManager analysisManager = 
                new AnalysisManager(coreProperties.getProperty(Setup.PROPERTY_CORE_OS),
                        coreProperties.getProperty(Setup.PROPERTY_CORE_VERSION_CONTROL_SYSTEM),
                        setup.getAnalysisProperties(), commitQueue);
        if (extractionManager.isReady() && analysisManager.isReady()) {                    
            extractionManager.start();
            analysisManager.start();
            try {
                extractionManager.join();
                analysisManager.join();
                if (extractionManager.noErrors() && analysisManager.noErrors()) {
                    runSuccessful = true;
                }
            } catch (InterruptedException e) {
                logger.logException(ID, "Extraction or analysis interrupted", e);
            }
        }
        return runSuccessful;
    }
    
    /**
     * Prints a message to the console describing the usage of this tool.
     */
    private static void showUsage() {
        System.out.println("\n\n##################################################################");
        System.out.println("#                                                                #");
        System.out.println("#              [Com]mit [An]alysis [I]nfrastructure              #");
        System.out.println("#                                                                #");
        System.out.println("##################################################################\n");
        System.out.println("Usage: java -jar ComAnI.jar [FILE_PATH] [-i]\n");
        System.out.println("  [FILE_PATH]  The path to the properties file defining the setup");
        System.out.println("               of the infrastructure.");
        System.out.println("               MANDATORY\n");
        System.out.println("  -i           Enables the interactive mode, in which a single");
        System.out.println("               commit is read directly from the console. The end");
        System.out.println("               of the commit has to be indicated by a line");
        System.out.println("               containing \"!q!\" only.");
        System.out.println("               OPTIONAL\n");
        System.out.println("Example: java -jar ComAnI.jar C:\\test.properties -i");
    }
}
