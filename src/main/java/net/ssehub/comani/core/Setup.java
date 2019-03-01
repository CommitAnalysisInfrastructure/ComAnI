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
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Scanner;

import net.ssehub.comani.core.Logger.MessageType;

/**
 * This class reads the configuration file and provides the user-defined properties for other components of the tool.
 * 
 * @author Christian Kroeher
 *
 */
public class Setup {
    
    /**
     * The string representation of the property's key identifying the operating system this tool is currently running
     * on. This is automatically detected by this class.
     */
    public static final String PROPERTY_CORE_OS = "core.os";
    
    /**
     * The string representation of the property's key identifying the available processors of the machine this tool is
     * currently running on. This is automatically detected by this class.
     */
    public static final String PROPERTY_CORE_THREADS = "core.threads";
    
    /**
     * The string representation of the property's key identifying the user-defined path to the directory, in which the
     * extractor and analyzer plug-ins are located.
     */
    public static final String PROPERTY_CORE_PLUGINS_DIR = "core.plugins_dir";
    
    /**
     * The string representation of the property's key identifying the user-defined name of target version control
     * system, e.g., from which the commits will be extracted.
     */
    public static final String PROPERTY_CORE_VERSION_CONTROL_SYSTEM = "core.version_control_system";
    
    /**
     * The string representation of the property's key identifying the user-defined log level. This is an optional
     * property, hence, if not specified, no further actions are required.
     */
    public static final String PROPERTY_CORE_LOG_LEVEL = "core.log_level";
    
    /**
     * The string representation of the property's key identifying the user-defined maximum number of elements (commits)
     * in the {@link net.ssehub.comani.data.CommitQueue}. This is an optional property, hence, if not specified, no
     * further actions are required.
     */
    public static final String PROPERTY_CORE_COMMIT_QUEUE_MAX_ELEMENTS = "core.commit_queue.max_elements";
    
    /**
     * The string representation of the property's key identifying the user-defined extractor main class name.
     */
    public static final String PROPERTY_EXTRACTION_CLASS = "extraction.extractor";
    
    /**
     * The string representation of the property's key identifying the user-defined repository directory.
     */
    public static final String PROPERTY_EXTRACTION_INPUT = "extraction.input";
    
    /**
     * The string representation of the property's key identifying the user-defined commit list file.
     */
    public static final String PROPERTY_EXTRACTION_COMMIT_LIST = "extraction.commit_list";
    
    /**
     * The string representation of the property's key identifying the user-defined directory for caching extracted
     * commits. The definition of this property enables the caching feature for the extraction.
     */
    public static final String PROPERTY_EXTRACTION_CACHING = "extraction.cache";
    
    /**
     * The string representation of the property's key identifying the user-defined directory for reusing cached
     * commits. The definition of this property enables the caching (reuse) feature for the extraction.
     */
    public static final String PROPERTY_EXTRACTION_REUSE = "extraction.reuse";
    
    /**
     * The string representation of the property's key identifying the user-defined analysis main class name.
     */
    public static final String PROPERTY_ANALYSIS_CLASS = "analysis.analyzer";
    
    /**
     * The string representation of the property's key identifying the user-defined analysis result directory.
     */
    public static final String PROPERTY_ANALYSIS_OUTPUT = "analysis.output";
    
    /**
     * The identifier if this class, e.g. for printing messages.
     */
    private static final String ID = "Setup";
    
    /**
     * The singleton instance of this class.
     */
    private static Setup instance;
    
    /**
     * The {@link Logger} for printing messages.
     */
    private Logger logger;

    /**
     * The core properties for setting up this tool, e.g., the operating system, the number of available processors, or
     * the path to the directory, in which the extractor and analyzer plug-ins are located. In general, this properties
     * object contains all properties defined in the properties file, which start with the prefix "<tt>core.</tt>".
     * 
     */
    private Properties coreProperties;
    
    /**
     * The properties determining the extraction process, e.g., the fully-qualified class name of the extractor to use,
     * or the path to the directory, in which extracted commits shall be saved. In general, this properties object
     * contains all properties defined in the properties file, which start with the prefix "<tt>extraction.</tt>".
     * <br><br> 
     * <b>Note</b> that these properties are exclusive to the extraction process and the extractor in use. For example,
     * an analysis may not be able to access these properties.
     */
    private Properties extractionProperties;
    
    /**
     * The properties determining the analysis process, e.g., the fully-qualified class name of the analyzer to use,
     * or the path to the directory, in which analysis results shall be saved. In general, this properties object
     * contains all properties defined in the properties file, which start with the prefix "<tt>analysis.</tt>".
     * <br><br> 
     * <b>Note</b> that these properties are exclusive to the analysis process and the analyzer in use. For example,
     * an extractor may not be able to access these properties.
     */
    private Properties analysisProperties;
    
    /**
     * The content of a single commit from the command line, which shall be extracted to an internal
     * {@link commits.Commit} for analysis. The value is set by parsing the set of arguments defined by the user.
     */
    private String commitString = null;

    /**
     * Constructs the singleton instance of this class.
     * 
     * @param args the arguments as provided to {@link ComAnI#main(String[])}
     */
    private Setup(String[] args) {
        logger = Logger.getInstance();
    }
    
    /**
     * Initializes and returns the singleton instance of this class.
     * 
     * @param args the arguments as provided to {@link ComAnI#main(String[])} 
     * @return the singleton instance of this class; never <code>null</code>.
     * @throws SetupException if preparing the {@link Setup} fails due to missing or false information
     */
    static Setup getInstance(String[] args) throws SetupException {
        if (instance == null) {
            instance = new Setup(args);
            instance.init(args);
        }
        return instance;
    }
    
    /**
     * Returns the singleton instance of this class.<br><br>
     * 
     * <b>Important:</b> the method {@link #getInstance(String[])} for initializing the instance has to be called at
     * least once before this method is called.
     * 
     * @return the singleton instance of this class; never <code>null</code>.
     * @throws SetupException if {@link #getInstance(String[])} was not called before
     */
    static Setup getInstance() throws SetupException {
        if (instance == null) {
            throw new SetupException("No setup available due to missing initialization");
        }
        return instance;
    }
    
    /**
     * Initializes this instance based on the given arguments.
     * 
     * @param args the arguments as provided to {@link ComAnI#main(String[])}
     * @throws SetupException if initializing this instance fails due to missing or false information
     */
    private void init(String[] args) throws SetupException {
        // TODO add a Java version check which ensures that it is supported by ComAnI
        if (args.length == 1) {
            // A single argument can only be the properties file for setting up this instance
            createProperties(args[0]);
        } else if (args.length == 2) {
            // First argument has to be the properties file, the second "-i" for interactive mode
            createProperties(args[0]);
            if (args[1].equals("-i")) {                    
                readInput();
                if (commitString == null || commitString.isEmpty()) {
                    throw new SetupException("Input in interactive mode was empty");
                }
            } else {
                throw new SetupException("Argument \"" + args[1] + "\" is not supported");
            }
        } else {
            throw new SetupException("Wrong number of arguments");
        }
    }
    
    /**
     * Reads all text from {@link System#in} until a line equals "!q!". This method is used in interactive mode where a
     * single commit is passed directly as a stream.
     */
    private void readInput() {
        Scanner inputScanner = new Scanner(System.in);
        StringBuilder commitStringBuilder = new StringBuilder();
        while (inputScanner.hasNextLine()) {
            String commitLine = inputScanner.nextLine();
            if (commitLine.equals("!q!")) {
                break;
            }
            commitStringBuilder.append(commitLine + "\n");
        }
        commitString = commitStringBuilder.toString();
        inputScanner.close();
    }
    
    /**
     * Reads the content of the properties file defined by the path denoted by the given string and creates the internal
     * properties based on that content. This includes checks whether the defined properties, their values, and their
     * combination are valid.
     * 
     * @param propertiesFilePath the string denoting the path to the properties file, which defines the setup of the
     *        tool
     * @throws SetupException if properties are missing, not containing correct information, or their combination is
     *         not valid
     */
    private void createProperties(String propertiesFilePath) throws SetupException {
        File propertiesFile = new File(propertiesFilePath);
        if (propertiesFile.exists()) {
            if (propertiesFile.isFile()) {
                InputStream inStream = null;
                try {
                    inStream = new FileInputStream(propertiesFile);
                    Properties loadedProperties = new Properties();
                    loadedProperties.load(inStream);
                    // Categorize properties
                    Enumeration<Object> propertyKeys = loadedProperties.keys();
                    coreProperties = new Properties();
                    extractionProperties = new Properties();
                    analysisProperties = new Properties();
                    while (propertyKeys.hasMoreElements()) {
                        String key = (String) propertyKeys.nextElement();
                        String value = loadedProperties.getProperty(key);
                        if (key.startsWith("core.")) {
                            coreProperties.put(key, value.trim());
                        } else if (key.startsWith("extraction.")) {
                            extractionProperties.put(key, value.trim());
                        } else if (key.startsWith("analysis")) {
                            analysisProperties.put(key, value.trim());
                        }
                    }
                    coreProperties.put(PROPERTY_CORE_OS, System.getProperty("os.name"));
                    coreProperties.put(PROPERTY_CORE_THREADS, Runtime.getRuntime().availableProcessors());
                    // Include OS and version control system in extraction properties for support checks
                    extractionProperties.put(PROPERTY_CORE_OS, System.getProperty("os.name"));
                    extractionProperties.put(PROPERTY_CORE_VERSION_CONTROL_SYSTEM,
                            coreProperties.get(PROPERTY_CORE_VERSION_CONTROL_SYSTEM));
                    // Include OS and version control system in analysis properties for support checks
                    analysisProperties.put(PROPERTY_CORE_OS, System.getProperty("os.name"));
                    analysisProperties.put(PROPERTY_CORE_VERSION_CONTROL_SYSTEM,
                            coreProperties.get(PROPERTY_CORE_VERSION_CONTROL_SYSTEM));
                } catch (IOException e) {
                    throw new SetupException("Reading the properties file failed", e);
                } finally {
                    if (inStream != null) {
                        try {
                            inStream.close();
                        } catch (IOException e) {
                            throw new SetupException("Closing the input stream for reading the properties file failed",
                                    e);
                        }
                    }
                }
            } else {
                throw new SetupException("The specified properties file is not a file");
            }
        } else {
            throw new SetupException("The specified properties file does not exist");
        }
        // Check properties for correctness
        checkProperties();
    }
    
    /**
     * Checks whether the created properties, their values, and their combination are valid.
     * 
     * @throws SetupException if the created properties, their values, and their combination are not valid
     */
    private void checkProperties() throws SetupException {
        checkCoreProperties();
        checkExtractorProperty();
        checkExtractionInputProperty();
        checkExtractionCommitListProperty();
        checkExtractionCachingProperty();
        checkExtractionReuseProperty();
        checkAnalysisProperties();
    }
    
    /**
     * Checks whether the {@link #coreProperties}, their values, and their combination are valid.
     * 
     * @throws SetupException if core properties, their values, or their combination are not valid
     */
    private void checkCoreProperties() throws SetupException {
        // Check if operating system is available
        if (coreProperties.get(PROPERTY_CORE_OS) == null) {
            throw new SetupException("The operating system could not be determined");
        }
        // Check if plug-ins directory is valid
        checkCorePluginsDirectory();
        // Check if log-level is valid; if not, use default level
        String logLevelString = coreProperties.getProperty(PROPERTY_CORE_LOG_LEVEL);
        if (logLevelString != null) {
            try {
                int logLevel = Integer.parseInt(logLevelString);
                if (logLevel < 0 || logLevel > 2) {
                    // No exception needed, use standard log-level
                    logger.setLogLevel("1");
                    coreProperties.put(PROPERTY_CORE_LOG_LEVEL, 1);
                    logger.log(ID, "Log-level not supported", "Using standard log-level", MessageType.WARNING);
                } else {
                    // Set the (valid) user-defined log-level
                    logger.setLogLevel(logLevelString);
                }
            } catch (NumberFormatException e) {
                throw new SetupException("Log-level is not a number");
            }
        } else {
            // No exception needed, use standard log-level
            logger.setLogLevel("1");
            coreProperties.put(PROPERTY_CORE_LOG_LEVEL, 1);
            logger.log(ID, "Log-level not specified", "Using standard log-level", MessageType.WARNING);
        }
        // Check if maximum number of commit queue elements is valid
        String maxCommitQueueElementsString = coreProperties.getProperty(PROPERTY_CORE_COMMIT_QUEUE_MAX_ELEMENTS);
        if (maxCommitQueueElementsString != null) {
            try {
                int maxCommitQueueElements = Integer.parseInt(maxCommitQueueElementsString);
                if (maxCommitQueueElements < 1 || maxCommitQueueElements > 100000) {
                    throw new SetupException("Maximum number of commit queue elements out of range: " 
                        + maxCommitQueueElements + " - should be between 1 and 100000");
                }
            } catch (NumberFormatException e) {
                throw new SetupException("Maximum number of commit queue elements is not a number");
            }
        } else {
            // No exception needed, use default number (10)
            coreProperties.put(PROPERTY_CORE_COMMIT_QUEUE_MAX_ELEMENTS, 10);
            logger.log(ID, "Maximum number of commit queue elements not specified", "Using default maximum number: 10",
                    MessageType.WARNING);
        }    
        // As the log-level is now correctly set, print the infrastructure start message
        logger.logInfrastructureStart(new Date());
    }
    
    /**
     * Checks if plug-ins directory is valid and contains Jar-files.
     * 
     * @throws SetupException if check fails
     */
    private void checkCorePluginsDirectory() throws SetupException {
        String pluginsDirectoryPath = coreProperties.getProperty(PROPERTY_CORE_PLUGINS_DIR);
        if (pluginsDirectoryPath != null && !pluginsDirectoryPath.isEmpty()) {
            File pluginsDirectory = new File(pluginsDirectoryPath);
            if (pluginsDirectory.exists()) {
                if (pluginsDirectory.isDirectory()) {
                    File[] jarFiles = pluginsDirectory.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            boolean acceptFile = false;
                            if (name.toLowerCase().endsWith(".jar")) {
                                acceptFile = true;
                            }
                            return acceptFile;
                        }
                    });
                    if (jarFiles == null) {
                        throw new SetupException("I/O error occured while checking the plug-ins directory");
                    } else if (jarFiles.length == 0) {
                        throw new SetupException("The plug-ins directory does not contain any Jar-files");
                    }
                } else {
                    throw new SetupException("The plug-ins directory path does not denote a directory");
                }
            } else {
                throw new SetupException("The plug-ins directory does not exist");
            }
        } else {
            throw new SetupException("The plug-ins directory is not specified");
        }
    }
    
    /**
     * Checks if specific extractor is specified.
     * 
     * @throws SetupException if check fails
     */
    private void checkExtractorProperty() throws SetupException {
        if (extractionProperties.getProperty(PROPERTY_EXTRACTION_CLASS) == null) {
            throw new SetupException("Extractor main class name not specified");
        }
    }
    
    /**
     * Checks if repository directory is valid only if a commit is not directly passed in interactive mode.
     * 
     * @throws SetupException if check fails
     */
    private void checkExtractionInputProperty() throws SetupException {
        if (commitString == null) {
            String repositoryDirectoryPath = extractionProperties.getProperty(PROPERTY_EXTRACTION_INPUT);
            if (repositoryDirectoryPath == null) {
                // Neither repository nor commit specified, hence, nothing to extract
                throw new SetupException("No input for extraction specified");
            } else {
                // Check if repository exists
                File repositoryDirectory = new File(repositoryDirectoryPath);
                if (repositoryDirectory.exists() && repositoryDirectory.isDirectory()) {
                    if (repositoryDirectory.list().length == 0) {
                        throw new SetupException("The input directory for extraction is empty");
                    }
                } else {
                    throw new SetupException("The input directory for extraction does not exist or the path does not"
                            + " denote a directory");
                }
            }   
        }
    }
    
    /**
     * Checks if a commit-list file is specified and if it is valid.
     * 
     * @throws SetupException if check fails
     */
    private void checkExtractionCommitListProperty() throws SetupException {
        String commitListFilePath = extractionProperties.getProperty(PROPERTY_EXTRACTION_COMMIT_LIST);
        if (commitListFilePath != null) {
            File commitListFile = new File(commitListFilePath);
            if (commitListFile.exists()) {
                if (!commitListFile.isFile()) {
                    throw new SetupException("The commit list file path does not denote a file");
                }
            } else {
                throw new SetupException("The commit list file does not exist");
            }
        } // This property is optional, hence, no exception if not specified
    }
    
    /**
     * Checks if caching is enabled and a valid directory for caching extracted commits is specified.
     * 
     * @throws SetupException if check fails
     */
    private void checkExtractionCachingProperty() throws SetupException {
        String cacheDirectoryPath = extractionProperties.getProperty(PROPERTY_EXTRACTION_CACHING);
        if (cacheDirectoryPath != null) {
            File cacheDirectory = new File(cacheDirectoryPath);
            if (!cacheDirectory.exists()) {
                // Create the directory if not already present
                if (!cacheDirectory.mkdirs()) {
                    throw new SetupException("Creating the directory for caching extracted commits failed");
                }
            }
            if (!cacheDirectory.isDirectory()) {
                throw new SetupException("The path of the directory for caching extracted commits does not"
                        + " denote a directory");
            } else {
                // The directory exists and is a directory; delete all content if not empty
                File[] cacheDirectoryFiles = cacheDirectory.listFiles();
                if (cacheDirectoryFiles.length > 0) {
                    logger.log(ID, "The directory for caching extracted commits is not empty",
                            "Deleting content of directory \"" + cacheDirectory.getAbsolutePath() + "\"",
                            MessageType.WARNING);
                    deleteAll(cacheDirectoryFiles);
                }
            }
        } // This property is optional, hence, no exception if not specified
    }
    
    /**
     * Checks if caching is enabled and a valid directory for reusing previously extracted commits is specified.
     * 
     * @throws SetupException if check fails
     */
    private void checkExtractionReuseProperty() throws SetupException { 
        String reuseDirectoryPath = extractionProperties.getProperty(PROPERTY_EXTRACTION_REUSE);
        if (reuseDirectoryPath != null) {
            File reuseDirectory = new File(reuseDirectoryPath);
            if (!reuseDirectory.exists()) {
                throw new SetupException("The cache directory for reusing extracted commits does not exist");
            }
            if (!reuseDirectory.isDirectory()) {
                throw new SetupException("The path of the cache directory for reusing extracted commits "
                        + "does not denote a directory");
            } else {
                // The directory exists and is a directory; check if at least one XML-file is available
                File[] reuseDirectoryFiles = reuseDirectory.listFiles();
                if (reuseDirectoryFiles.length == 0) {
                    throw new SetupException("The cache directory for reusing extracted commits is empty; "
                            + "nothing to reuse");
                }
                boolean cdmsFileFound = false;
                int reuseDirectoryFilesCounter = 0;
                while (!cdmsFileFound && reuseDirectoryFilesCounter < reuseDirectoryFiles.length) {
                    cdmsFileFound = reuseDirectoryFiles[reuseDirectoryFilesCounter].getName().endsWith(".cdms");
                    reuseDirectoryFilesCounter++;
                }
                if (!cdmsFileFound) {
                    throw new SetupException("The cache directory for reusing extracted commits does not contain"
                            + " any CDMS-file");
                }
            }            
        } // This property is optional, hence, no exception if not specified
    }
    
    /**
     * Checks whether the {@link #analysisProperties}, their values, and their combination are valid.
     * 
     * @throws SetupException if analysis properties, their values, or their combination are not valid
     */
    private void checkAnalysisProperties() throws SetupException {
        // Check if specific analysis is specified
        String analyzerClassName = analysisProperties.getProperty(PROPERTY_ANALYSIS_CLASS);
        if (analyzerClassName == null) {
            throw new SetupException("Analysis main class name not specified");
        } else if (analyzerClassName.endsWith(".")) {
            throw new SetupException("Invalid analysis main class name as it ends with a \".\"");
        }
        // Check if analysis result directory is valid
        String analysisResultsDirectoryPath = analysisProperties.getProperty(PROPERTY_ANALYSIS_OUTPUT);
        if (analysisResultsDirectoryPath == null) {
            throw new SetupException("No output directory for analysis results specified");
        } else {
            File analysisResultsDirectory = new File(analysisResultsDirectoryPath);
            if (analysisResultsDirectory.exists()) {
                if (analysisResultsDirectory.isDirectory()) {
                    /*
                     * In order to avoid unintended overriding of previously created analysis results, we simply create
                     * a new sub-directory for the current analysis.
                     */
                    String analysisResultsSubDirectoryPath = createAnalysisResultsSubDirectory(analysisResultsDirectory,
                            analyzerClassName);
                    if (analysisResultsSubDirectoryPath == null) {
                        throw new SetupException("Could not create output sub-directory for this analysis");
                    } else {                        
                        /*
                         * Update the analysis output directory path in the analysis properties such that the particular
                         * analyzer uses the correct directory for saving results
                         */
                        analysisProperties.put(PROPERTY_ANALYSIS_OUTPUT, analysisResultsSubDirectoryPath);
                        logger.log(ID, "Output sub-directory for analysis results created",
                                "Saving analysis results to \"" + analysisResultsSubDirectoryPath + "\"",
                                MessageType.INFO);
                    }
                } else {
                    throw new SetupException("The output directory path for analysis results does not denote a"
                            + " directory");
                }
            } else {
                throw new SetupException("The output directory for analysis results does not exist");
            }
        }
    }
    
    /**
     * Creates a new sub-directory in the given analysis results directory. The name of this sub-directory consists of
     * the following elements: <ANALYZER_SIMPLE_NAME>-"Results"_<YYYY>-<MM>-<DD>_<HH>_<MM>_<SS>
     * 
     * @param analysisResultsDirectory the user-defined analysis results directory as specified by the value of the 
     *        {@link #PROPERTY_ANALYSIS_OUTPUT} property
     * @param analyzerClassName the user-defined, fully qualified main class name of the analyzer as specified by the
     *        value of the {@link #PROPERTY_ANALYSIS_CLASS} property
     * @return a string denoting the absolute path of the new sub-directory or <code>null</code>, if creating the
     *         directory failed
     */
    private String createAnalysisResultsSubDirectory(File analysisResultsDirectory, String analyzerClassName) {
        String analysisResultsSubDirectoryPath = null;
        // Get the simply analyzer class name, which is the part of the full class name after the last "."
        String analyzerSimpleClassName = analyzerClassName.substring(analyzerClassName.lastIndexOf(".") + 1);
        // Get current date and time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // Create the full sub-directory name
        String analysisResultsSubDirectoryName = analyzerSimpleClassName + "-Results_" 
                + calendar.get(Calendar.YEAR) + "-" 
                + calendar.get(Calendar.MONTH) + "-" 
                + calendar.get(Calendar.DAY_OF_MONTH) + "_" 
                + calendar.get(Calendar.HOUR_OF_DAY) + "-" 
                + calendar.get(Calendar.MINUTE) + "-" 
                + calendar.get(Calendar.SECOND);
        // Create the sub-directory
        File analysisResultsSubDirectory = new File(analysisResultsDirectory, analysisResultsSubDirectoryName);
        if (analysisResultsSubDirectory.mkdir()) {
            analysisResultsSubDirectoryPath = analysisResultsSubDirectory.getAbsolutePath();
        }
        return analysisResultsSubDirectoryPath;
    }
    
    /**
     * Deletes all files in the given set of files, .e.g., to clean a directory. This deletion includes all
     * sub-directories and their files as well.
     * 
     * @param files the set of files to be deleted
     * @throws SetupException if deleting a file or sub-directory fails
     */
    private void deleteAll(File[] files) throws SetupException {
        File nestedFile;
        for (int i = 0; i < files.length; i++) {
            nestedFile = files[i];
            if (nestedFile.isDirectory()) {
                deleteAll(nestedFile.listFiles());
            }
            if (!nestedFile.delete()) {
                throw new SetupException("Deleting file \"" + nestedFile.getAbsolutePath() + "\" failed");
            }
        }
    }
    
    /**
     * Returns the {@link #coreProperties} as defined in the properties file.
     * 
     * @return the core properties for setting up this tools; never <code>null</code> but may be empty
     */
    Properties getCoreProperties() {
        return coreProperties;
    }
    
    /**
     * Returns the {@link #extractionProperties} as defined in the properties file.
     * 
     * @return the extraction properties for setting up the extraction process and the extractor in use;
     *         never <code>null</code> but may be empty
     */
    Properties getExtractionProperties() {
        return extractionProperties;
    }
    
    /**
     * Returns the {@link #analysisProperties} as defined in the properties file.
     * 
     * @return the analysis properties for setting up the analysis process and the analyzer in use;
     *         never <code>null</code> but may be empty
     */
    Properties getAnalysisProperties() {
        return analysisProperties;
    }
    
    /**
     * Returns the {@link #commitString} retrieved in interactive mode.
     * 
     * @return the string containing a particular commit or <code>null</code> if the tool is not used in
     *         interactive mode
     */
    String getCommitString() {
        return commitString;
    }
}
