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

import java.util.Date;

/**
 * This class is used to print different types of information to the console.
 * 
 * @author Christian Kroeher
 *
 */
public class Logger {
    
    /**
     * The identifier of this class, e.g., for printing messages.
     */
    private static final String ID = "Logger";
    
    /**
     * The square bracket open character used to start a message type or origin definition in messages.
     */
    private static final char SQUARE_BRACKET_OPEN = '[';
   
    /**
     * The square bracket close character used to end a message type or origin definition in messages.
     */
    private static final char SQUARE_BRACKET_CLOSE = ']';
    
    /**
     * The pipe character used to separate message type and origin in printed messages. This character is further used
     * as part of the {@link #TEXT_LINE_PREFIX} to align messages and descriptions to a particular message type and
     * origin.
     */
    private static final char MESSAGE_TYPE_SEPARATOR = '|';
    
    /**
     * The string used as prefix for each message line and as part of description lines.
     */
    private static final String TEXT_LINE_PREFIX = "   " + MESSAGE_TYPE_SEPARATOR + " ";
    
    /**
     * The string used as special prefix for the first description line.
     */
    private static final String DESCRIPTION_FIRST_LINE_PREFIX = TEXT_LINE_PREFIX + "-> ";
    
    /**
     * The string used as special prefix for each description line after the first one.
     */
    private static final String DESCRIPTION_NEXT_LINE_PREFIX = TEXT_LINE_PREFIX + "   ";

    /**
     * The maximum number of characters in a single line of a console's message. 
     */
    private static final int MAX_LINE_LENGTH = 100;

    /**
     * Singleton instance of this class.
     */
    private static Logger instance = new Logger();

    /**
     * This enumeration defines the different types of messages. This class differentiates:
     * <ul>
     * <li>INFO: Information about current status of the tool (always displayed)</li>
     * <li>WARNING: Information about problems that do not influence the overall execution of the tool (always
     *       displayed)</li>
     * <li>ERROR: Information about problems that force stopping the overall execution of the tool (always
     *       displayed)</li>
     * <li>DEBUG: Information about individual process steps used for debugging (only displayed if debug is
     *       enabled)</li>
     * </ul>
     * 
     * @author Christian Kroeher
     *
     */
    public enum MessageType { INFO, WARNING, ERROR, DEBUG };
    
    /**
     * This enumeration defines the different log levels. This class differentiates:
     * <ul>
     * <li>0 - SILENT: No information is logged, hence, there will be no messages at all except for initial setup
     *             errors</li>
     * <li>1 - STANDARD: Basic information, warnings, and errors are logged and displayed on the console</li>
     * <li>2 - DEBUG: Similar to STANDARD, but logs and displays additional debug information</li>
     * </ul>
     * 
     * @author Christian Kroeher
     *
     */
    public enum LogLevel { SILENT, STANDARD, DEBUG };
    
    /**
     * The current {@link LogLevel} if this logger.
     */
    private LogLevel logLevel;

    /**
     * Construct a new {@link Logger}.
     */
    private Logger() {
        // Default log level is standard
        logLevel = LogLevel.STANDARD;
    }

    /**
     * Return the single instance of the {@link Logger}.
     * 
     * @return the single instance of the {@link Logger}
     */
    public static synchronized Logger getInstance() {
        return instance;
    }
    
    /**
     * Sets the new {@link LogLevel} for this logger based on the given log level number to increase or reduce the
     * amount of information logged and displayed on the console. For the current mapping of log level and associated
     * numbers see {@link LogLevel}.
     * 
     * @param newLogLevel the new log level number as a string
     */
    void setLogLevel(String newLogLevel) {
        if (newLogLevel != null && !newLogLevel.isEmpty()) {
            try {
                int logLevelNumber = Integer.parseInt(newLogLevel);
                switch(logLevelNumber) {
                case 0:
                    logLevel = LogLevel.SILENT;
                    break;
                case 2:
                    logLevel = LogLevel.DEBUG;
                    break;
                default:
                    logLevel = LogLevel.STANDARD;
                    break;
                }
            } catch (NumberFormatException e) {
                log(ID, "The desired log level is not specified as a correct number", "Standard log level will be used",
                        MessageType.WARNING);
            }
        }
    }
    
    /**
     * Prints a message indicating the start of the entire infrastructure along with the current date and time to the
     * console.
     * 
     * @param currentDate the current {@link Date} at which the infrastructure is starting
     */
    void logInfrastructureStart(Date currentDate) {
        log("ComAnI", "Infrastructure started", currentDate.toString(), MessageType.INFO);
    }
    
    /**
     * Prints a message indicating the termination of the entire infrastructure along with the current date and time as
     * well as the duration of execution to the console.
     * 
     * @param currentDate the current {@link Date} at which the infrastructure is terminating
     * @param durationMinutes a non-negative value indicating the minutes of execution
     * @param durationSeconds a non-negative value between 0 and 59 indicating the seconds of execution
     */
    void logInfrastructureTermination(Date currentDate, int durationMinutes, int durationSeconds) {
        log("ComAnI", "Infrastructure terminated",
                currentDate.toString() + " - Duration: " + durationMinutes + " min. and " + durationSeconds + " sec.",
                MessageType.INFO);
    }

    /**
     * Prints the given information to the console. This includes formating the given message and description texts via
     * {@link #formatLogText(String, String)}.
     * 
     * @param origin the name of the class calling this method; should never be <code>null</code>
     * @param message the message to be displayed; should never be <code>null</code>
     * @param description optional description; can be <code>null</code> 
     * @param type the {@link MessageType} of this message; should never be <code>null</code>
     */
    public synchronized void log(String origin, String message, String description, MessageType type) {
        if (logLevel != LogLevel.SILENT) {            
            if (logLevel == LogLevel.DEBUG || (logLevel != LogLevel.DEBUG && type != MessageType.DEBUG)) {
                // First line is always: [MESSAGE_TYPE] [ORIGIN]
                System.out.println(toLogString(type) + MESSAGE_TYPE_SEPARATOR + SQUARE_BRACKET_OPEN + origin 
                        + SQUARE_BRACKET_CLOSE);
                // Next lines are the message and optional description
                System.out.println(formatLogText(TEXT_LINE_PREFIX, message));
                if (description != null && !description.isEmpty()) {                    
                    System.out.println(formatLogText(DESCRIPTION_FIRST_LINE_PREFIX, description));
                }
            }
        }
    }
    
    /**
     * Prints the given {@link Throwable} and its cause(s) as an error message to the console.
     * 
     * @param origin the name of the class calling this method; should never be <code>null</code>
     * @param message a custom error message displayed before the throwable message and its cause(s); can be
     *        <code>null</code>
     * @param throwable the exception to be displayed; can be <code>null</code>
     */
    public synchronized void logException(String origin, String message, Throwable throwable) {
        if (logLevel != LogLevel.SILENT) {
            /*
             * Log an exception as a usual log call with the following parameter values:
             *      origin = the origin provided to this method
             *      message = the message provided to this method or a standard message created by this method
             *      description = the optional throwable message and cause
             *      type = MessageType.Error
             */
            // If no message is available, print this standard text to indicate that this is not a "normal" error 
            if (message == null || message.isEmpty()) {
                message = "An exception was thrown";
            }
            log(origin, message, toLogString(throwable), MessageType.ERROR);
        }
    }
    
    /**
     * Formats the given text such that each line starts with the correct prefix and fits the {@link #MAX_LINE_LENGTH}.
     * 
     * @param textPrefix the initial prefix with which each line of the given text should start. In case of the 
     *        {@link #DESCRIPTION_FIRST_LINE_PREFIX} this method will automatically switch to the
     *        {@link #DESCRIPTION_NEXT_LINE_PREFIX} if the text has to be split into multiple lines; should never be 
     *        <code>null</code>
     * @param text the text to be formatted, which means adding the given prefix and optionally splitting it into
     *        multiple lines (each starting with the correct prefix); can be <code>null</code>, which forces this
     *        method to use a standard text
     * @return the formatted text in which each line starts with the correct prefix and fits the maximum line length;
     *         never <code>null</code>
     */
    private String formatLogText(String textPrefix, String text) {
        String formattedText = null;
        if (text == null || text.isEmpty()) {
            formattedText = textPrefix + "No further information provided";
        } else {
            String[] textLines = text.split("\n");
            // As the text to format is not null there is at least one line available
            formattedText = mergeAndSplit(textPrefix, textLines[0]);
            if (textLines.length > 1) {
                // The text to format consists of multiple lines, hence, split each line if needed
                StringBuilder formattedTextBuilder = new StringBuilder();
                // Add already formatted first text line
                formattedTextBuilder.append(formattedText);
                int textLinesCounter = 1;
                // Add additional lines by adding a new line and formating the text line
                if (textPrefix.equals(DESCRIPTION_FIRST_LINE_PREFIX)) {
                    // If the description of a log has multiple lines, only the first should contain the arrow
                    textPrefix = DESCRIPTION_NEXT_LINE_PREFIX;
                }
                while (textLinesCounter < textLines.length) {
                    formattedTextBuilder.append("\n");
                    formattedTextBuilder.append(mergeAndSplit(textPrefix, textLines[textLinesCounter]));
                    textLinesCounter++;
                }
                formattedText = formattedTextBuilder.toString();
            }
        }
        return formattedText;
    }
    
    /**
     * Merges the given text with the given prefix and splits the merged string if needed to fit the 
     * {@link #MAX_LINE_LENGTH}.
     * 
     * @param textPrefix the initial prefix with which each line of the given text should start. In case of the 
     *        {@link #DESCRIPTION_FIRST_LINE_PREFIX} this method will automatically switch to the
     *        {@link #DESCRIPTION_NEXT_LINE_PREFIX} if the text has to be split into multiple lines; should never be 
     *        <code>null</code>
     * @param text the string to be merged with the correct prefix and split into multiple lines to fit the maximum line
     *        length
     * @return a single string potentially containing multiple lines, which start with the correct prefix and do not
     *         exceed the maximum line length; never <code>null</code>
     */
    private String mergeAndSplit(String textPrefix, String text) {
        String splittedText = textPrefix + text;
        if (splittedText.length() > MAX_LINE_LENGTH) {
            StringBuilder splittedTextBuilder = new StringBuilder();
            String textPart;
            int charCounter = MAX_LINE_LENGTH;
            boolean splitIndexFound = false;
            while (!splitIndexFound && charCounter >= 0) {
                if (isSplitCharacter(splittedText.charAt(charCounter))) {
                    splitIndexFound = true;
                    // Add the part before the split character as individual text line
                    textPart = splittedText.substring(0, charCounter);
                    splittedTextBuilder.append(textPart);
                    splittedTextBuilder.append("\n");
                    // Split the part starting with the split character again if needed
                    if (textPrefix.equals(DESCRIPTION_FIRST_LINE_PREFIX)) {
                        // If the description of a log has multiple lines, only the first should contain the arrow
                        textPrefix = DESCRIPTION_NEXT_LINE_PREFIX;
                    }
                    textPart = mergeAndSplit(textPrefix, splittedText.substring(charCounter).trim());
                    // Add the part starting with the split character as individual line(s)
                    splittedTextBuilder.append(textPart);
                }
                charCounter--;
            }
            splittedText = splittedTextBuilder.toString();
        }
        return splittedText;
    }
    
    /**
     * Checks if the given character can be used to split a string around it. This is the case, if it matches one of the
     * following characters:
     * <ul>
     * <li>" " (whitespace)</li>
     * <li>"," (comma)</li>
     * <li>"." (dot)</li>
     * <li>";" (semicolon)</li>
     * <li>":" (colon)</li>
     * <li>"!" (exclamation mark)</li>
     * <li>"?" (question mark)</li>
     * <li>"-" (hyphen)</li>
     * <li>"_" (underscore)</li>
     * <li>"\" (backslash)</li>
     * <li>"/" (slash)</li>
     * </ul>
     * @param character the character to check
     * @return <code>true</code> if the given character matches one of the list above; <code>false</code> otherwise
     */
    private boolean isSplitCharacter(char character) {
        boolean isSplitCharacter = false;
        switch(character) {
        case ' ':
            isSplitCharacter = true;
            break;
        case ',':
            isSplitCharacter = true;
            break;
        case '.':
            isSplitCharacter = true;
            break;
        case ';':
            isSplitCharacter = true;
            break;
        case ':':
            isSplitCharacter = true;
            break;
        case '!':
            isSplitCharacter = true;
            break;
        case '?':
            isSplitCharacter = true;
            break;
        case '-':
            isSplitCharacter = true;
            break;
        case '_':
            isSplitCharacter = true;
            break;
        case '\\':
            isSplitCharacter = true;
            break;
        case '/':
            isSplitCharacter = true;
            break;
        default:
            isSplitCharacter = false;
            break;
        }
        return isSplitCharacter;
    }
    
    /**
     * Returns a proper string representation of the given {@link MessageType} including the surrounding brackets.
     *  
     * @param type the {@link MessageType} to be converted into a string ready for printing
     * @return a string representation of the given {@link MessageType} including the surrounding brackets
     */
    
    /**
     * Returns a proper string representation of the given {@link Throwable} including its message and the possible
     * cause(s). The message of the given throwable as well as each cause are separated into individual lines.
     * 
     * @param throwable the throwable to be converted into a string containing all available exception messages
     * @return a string representation of all available exception messages or <code>null</code> if the given throwable
     *         is <code>null</code>
     */
    private String toLogString(Throwable throwable) {
        String logString = null;
        if (throwable != null) {
            String throwableInfo = throwable.getMessage();
            if (throwableInfo != null) {
                logString = throwable.getClass().getSimpleName() + ": " + throwableInfo;
            }
            throwableInfo = toLogString(throwable.getCause());
            if (throwableInfo != null) {
                logString = logString + "\nCaused by " + throwableInfo;
            }
        }
        return logString;
    }
    
    /**
     * Returns a proper string representation of the given {@link MessageType} including the surrounding brackets.
     *  
     * @param type the {@link MessageType} to be converted into a string ready for printing
     * @return a string representation of the given {@link MessageType} including the surrounding brackets
     */
    private String toLogString(MessageType type) {
        String typeString = SQUARE_BRACKET_OPEN + "?" + SQUARE_BRACKET_CLOSE;
        if (type != null) {            
            switch (type) {
            case INFO:
                typeString = SQUARE_BRACKET_OPEN + "I" + SQUARE_BRACKET_CLOSE;
                break;
            case WARNING:
                typeString = SQUARE_BRACKET_OPEN + "W" + SQUARE_BRACKET_CLOSE;
                break;
            case ERROR:
                typeString = SQUARE_BRACKET_OPEN + "E" + SQUARE_BRACKET_CLOSE;
                break;
            case DEBUG:
                typeString = SQUARE_BRACKET_OPEN + "D" + SQUARE_BRACKET_CLOSE;
                break;
            default:
                // Should never be reacher
                typeString = SQUARE_BRACKET_OPEN + "?" + SQUARE_BRACKET_CLOSE;
                break;
            }
        }
        return typeString;
    }
}
