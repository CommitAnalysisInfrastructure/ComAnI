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
package net.ssehub.comani.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.ssehub.comani.core.Logger;
import net.ssehub.comani.core.Logger.MessageType;

/**
 * This class provides utility methods for executing commands as external {@link Process} and returning corresponding
 * {@link ExecutionResult}s.
 * 
 * @author Christian Kroeher
 *
 */
public class ProcessUtilities {

    /**
     * The identifier if this class, e.g. for printing messages.
     */
    private static final String ID = "ProcessUtilities";
    
    /**
     * Singleton instance of this class.
     */
    private static ProcessUtilities instance = null;
    
    /**
     * The {@link Logger} for printing messages.
     */
    private Logger logger = Logger.getInstance();
    
    /**
     * Constructs new {@link ProcessUtilities}.
     */
    private ProcessUtilities() {}
    
    /**
     * Returns the single instance of the {@link ProcessUtilities}.
     * 
     * @return the single instance of the {@link ProcessUtilities}
     */
    public static synchronized ProcessUtilities getInstance() {
        if (instance == null) {
            instance = new ProcessUtilities();
        }
        return instance;
    }
    
    /**
     * This class provides the results of the execution of a command via 
     * {@link ProcessUtilities#executeCommand(String[], File)}. These results subsume the <b>command</b> and the 
     * <b>working directory</b> used for the execution of the subprocess, the <b>standard output</b> and
     * <b>error output</b> data provided by this subprocess, the <b>possible exception</b> thrown during the execution
     * of the subprocess, and the <b>exit value</b> of the subprocess.
     * 
     * @author Christian Kroeher
     *
     */
    public class ExecutionResult {
        
        /**
         * The command that was executed by {@link ProcessUtilities#executeCommand(String[], File)} and provided this
         * result.
         */
        private String command;
        
        /**
         * The absolute path of the working directory of the subprocess executed by 
         * {@link ProcessUtilities#executeCommand(String[], File)}, or <code>null</code> if no particular working
         * directory was given. This means that the subprocess was executed in the directory in which the tool is
         * executed.
         */
        private String workingDirectory;
        
        /**
         * The data of the standard output stream of the process, which executed the {@link #command}. The default value
         * is <code>null</code>.
         */
        private String standardOutput;
        
        /**
         * The data of the standard error stream of the process, which executed the {@link #command}. The default value
         * is <code>null</code>.
         */
        private String errorOutput;
        
        /**
         * The {@link Throwable}, which might have been caught during the execution of the process for the
         * {@link #command}. The default value is <code>null</code>.
         */
        private Throwable executionException;
        
        /**
         * The exit value of the process, which executed the {@link #command}, as provided by 
         * {@link Process#waitFor()}. The default value is <i>-1</i> indicating an abnormal termination of the process.
         */
        private int processExitValue;
        
        /**
         * Constructs a new instance of this {@link ExecutionResult}.
         * 
         * @param command the command that was executed by {@link ProcessUtilities#executeCommand(String[], File)} and
         *        provided this result
         * @param workingDirectory the absolute path of the working directory of the subprocess executed by 
         *        {@link ProcessUtilities#executeCommand(String[], File)}, or <code>null</code> if no particular working
         *        directory was given.
         */
        private ExecutionResult(String command, String workingDirectory) {
            this.command = command;
            this.workingDirectory = workingDirectory;
            standardOutput = null;
            errorOutput = null;
            executionException = null;
            processExitValue = -1;
        }
        
        /**
         * Sets the given string as the {@link #standardOutput} data of this result.
         * 
         * @param standardOutputData the standard output data of the process, which executed the {@link #command}
         */
        private void setStandardOutputData(String standardOutputData) {
            standardOutput = standardOutputData;
        }
        
        /**
         * Sets the given string as the {@link #errorOutput} data of this result.
         * 
         * @param errorOutputData the error output data of the process, which executed the {@link #command}
         */
        private void setErrorOutputData(String errorOutputData) {
            errorOutput = errorOutputData;
        }
        
        /**
         * Sets the given {@link Throwable} as the {@link #executionException}, which was caught during the execution of
         * the {@link #command}.
         * 
         * @param exception the exception that was caught during the execution of the {@link #command}
         */
        private void setExecutionException(Throwable exception) {
            executionException = exception;
        }
        
        /**
         * Sets the given integer as the {@link #processExitValue} of the process, which executed the {@link #command}.
         * 
         * @param exitValue the exit value of the process, which executed the {@link #command}, as provided by 
         *        {@link Process#waitFor()}.
         */
        private void setProcessExitValue(int exitValue) {
            processExitValue = exitValue;
        }
        
        /**
         * Returns the {@link #command} executed by {@link ProcessUtilities#executeCommand(String[], File)}, which
         * provided this result.
         * 
         * @return the string representing the executed command
         */
        public String getCommand() {
            return command;
        }
        
        /**
         * Returns the {@link #workingDirectory} used by {@link ProcessUtilities#executeCommand(String[], File)} to
         * executed the {@link #command}.
         * 
         * @return the absolute path to the working directory of the subprocess or <code>null</code> if no particular
         *         working directory was given.
         */
        public String getWorkingDirectory() {
            return workingDirectory;
        }
        
        /**
         * Returns the {@link #standardOutput} data of the process, which executed the {@link #command}.
         * 
         * @return the string representing the standard output data provided by the process or <code>null</code>, if no
         *         such data was available
         */
        public String getStandardOutputData() {
            return standardOutput;
        }
        
        /**
         * Returns the {@link #errorOutput} data of the process, which executed the {@link #command}.
         * 
         * @return the string representing the error output data provided by the process or <code>null</code>, if no
         *         such data was available
         */
        public String getErrorOutputData() {
            return errorOutput;
        }
        
        /**
         * Return the {@link #executionException}, which might have been caught during the execution of the process for
         * the {@link #command}.
         * 
         * @return the {@link Throwable} representing an exception during the execution of the process or
         *         <code>null</code>, if no exception was thrown
         */
        public Throwable getExecutionException() {
            return executionException;
        }
        
        /**
         * Returns the {@link #processExitValue}, which indicates whether the process for executing the {@link #command}
         * terminated normally or not.
         * 
         * @return an integer value as provided by {@link Process#waitFor()}
         */
        public int getProcessExitValue() {
            return processExitValue;
        }
        
        /**
         * Checks whether the execution of the {@link #command} was successful (<code>true</code>) or not
         * (<code>false</code>). This check will only return <code>true</code> if the {@link #processExitValue} is
         * <i>0</i> and there were no exceptions thrown.
         * 
         * @return <code>true</code> if the process executing the command has exit value <i>0</i> and no exceptions were
         *         thrown; <code>false</code> otherwise
         */
        public boolean executionSuccessful() {
            return processExitValue == 0 && executionException == null;
        }
    }
    
    /**
     * Executes the given command as a {@link Process} in the current {@link Runtime}.
     * 
     * @param command the command that shall be executed; should never be <i>empty</i> or <code>null</code> itself as
     *        well as any of its elements
     * @param workingDirectory the working directory of the process created by this method for executing the given
     *        command; can be <code>null</code> if the process should use the directory in which the tool is executed
     * @return the {@link ExecutionResult} of the process executing the given command; never <code>null</code>
     */
    public synchronized ExecutionResult executeCommand(String[] command, File workingDirectory) {
        String commandString = getCommandString(command);
        ExecutionResult executionResult;
        if (workingDirectory != null) {
            executionResult = new ExecutionResult(commandString, workingDirectory.getAbsolutePath());
        } else {
            executionResult = new ExecutionResult(commandString, null);
        }
        Process process = null;
        InputStream inputStream = null;
        InputStream errorStream = null;
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder = processBuilder.directory(workingDirectory);
        try {
            process = processBuilder.start();
            // Read the standard output and save it to the result
            inputStream = process.getInputStream();
            executionResult.setStandardOutputData(readStream(inputStream));
            // Read the error output and save it to the result
            errorStream = process.getErrorStream();
            executionResult.setErrorOutputData(readStream(errorStream));
            // Get the actual process exit value and save it to the result
            executionResult.setProcessExitValue(process.waitFor());
        } catch (NullPointerException e) {
            logger.logException(ID, "An element of the command list of command \"" + commandString + "\" is null", e);
        } catch (IndexOutOfBoundsException e) {
            logger.logException(ID, "The command to execute is an empty list", e);
        } catch (SecurityException e) {
            logger.logException(ID, "Existing security manager detected an issue", e);
        } catch (IOException e) {
            logger.logException(ID, "An I/O error occured during the execution of command \"" + commandString + "\"",
                    e);
        } catch (InterruptedException e) {
            executionResult.setExecutionException(e);
            logger.logException(ID, "Waiting for execution of command \"" + commandString + "\" interrupted", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    executionResult.setExecutionException(e);
                    logger.logException(ID, 
                            "Closing the input stream for reading the standard output of a process failed", e);
                }
            }
            if (errorStream != null) {
                try {
                    errorStream.close();
                } catch (IOException e) {
                    executionResult.setExecutionException(e);
                    logger.logException(ID,
                            "Closing the input stream for reading the error output of a process failed", e);
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return executionResult;
    }
    
    /**
     * Reads the data from the given {@link InputStream} and returns it as a {@link String}.
     * 
     * @param stream the stream from which the data shall be read
     * @return the String-representation of the stream's data
     */
    public synchronized String readStream(InputStream stream) {
        String streamData = null;
        if (stream != null) {            
            StringBuffer outputBuffer = new StringBuffer();
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    outputBuffer.append(line + "\n");
                }
                streamData = outputBuffer.toString();
            } catch (IOException e) {
                logger.logException(ID, "Reading stream data failed", e);
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    logger.log(ID, "Closing the buffered reader for reading an input stream failed", e.toString(),
                            MessageType.WARNING);
                }
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    logger.log(ID, "Closing the given input stream failed", e.toString(), MessageType.WARNING);
                }
            }
        }
        return streamData;
    }
    
    /**
     * Returns an extended array by appending the given element at the end of the given array. This method is, in
     * particular, useful, if a command has to be executed via {@link #executeCommand(String[], File)}, which consists
     * of multiple parts that have to be combined dynamically, e.g., at runtime.
     *  
     * @param command the array to extend
     * @param commandElement the element to be added at the end of array
     * @return a new array, which consists of the given array extended by the given element
     */
    public synchronized String[] extendCommand(String[] command, String commandElement) {
        String[] extendedCommand = new String[command.length + 1];
        for (int i = 0; i < command.length; i++) {
            extendedCommand[i] = command[i];
        }
        extendedCommand[command.length] = commandElement;
        return extendedCommand;
    }
    
    /**
     * Returns a single string consisting of the elements of the given array in ascending order of their index separated
     * by a single whitespace. This method is, in particular, useful, if a command executed via 
     * {@link #executeCommand(String[], File)} has to be logged or printed.
     * 
     * @param command the array providing the elements for concatenation of a single string
     * @return a single string consisting of the elements of the given array separated by whitespaces or
     *         <code>null</code>, if the array is <code>null</code> or <i>empty</i>
     */
    public synchronized String getCommandString(String[] command) {
        String commandString = null;
        if (command != null && command.length > 0) {
            StringBuilder commandStringBuilder = new StringBuilder();
            commandStringBuilder.append(command[0]);
            for (int i = 1; i < command.length; i++) {
                commandStringBuilder.append(" ");
                commandStringBuilder.append(command[i]);
            }
            commandString = commandStringBuilder.toString();
        }
        return commandString;
    }
}
