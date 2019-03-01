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
package net.ssehub.comani.data;

import java.io.File;
import java.util.List;

import net.ssehub.comani.core.Logger;
import net.ssehub.comani.core.Logger.MessageType;
import net.ssehub.comani.utility.FileUtilities;

/**
 * This class provides capabilities to save individual {@link Commit} objects as CDMS-files and to read such files for
 * creating {@link Commit} objects based on their CDMS-elements. CDMS stands for <b>C</b>omAnI <b>D</b>ata <b>M</b>odel
 * <b>S</b>erialization, which is a built-in format for caching extracted commits.
 * 
 * @author Christian Kroeher
 *
 */
public class CommitSerializer {
    
    /**
     * The identifier of this class, e.g., for printing messages.
     */
    private static final String ID = "CommitSerializer";
    
    /**
     * The character opening the definition of a CDMS-element.
     */
    private static final char CDMS_ELEMENT_OPEN_CHARACTER = '[';
    
    /**
     * The character closing the definition of a CDMS-element.
     */
    private static final char CDMS_ELEMENT_CLOSE_CHARACTER = ']';
    
    /**
     * The character opening an attribute list for a CDMS-element.
     */
    private static final char CDMS_ELEMENT_ATTRIBUTES_OPEN_CHARACTER = '(';
    
    /**
     * The character closing an attribute list for a CDMS-element.
     */
    private static final char CDMS_ELEMENT_ATTRIBUTES_CLOSE_CHARACTER = ')';
    
    /**
     * The prefix of each CDMS-start element, which is followed by an identifier defining the particular
     * CDMS-element. 
     */
    private static final String CDMS_ELEMENT_START_PREFIX = CDMS_ELEMENT_OPEN_CHARACTER + "CDMS::Start::";
    
    /**
     * The prefix of each CDMS-end element, which is followed by an identifier defining the particular
     * CDMS-element. 
     */
    private static final String CDMS_ELEMENT_END_PREFIX = CDMS_ELEMENT_OPEN_CHARACTER + "CDMS::End::";

    /**
     * The identifier for defining commit start and end elements.
     */
    private static final String CDMS_COMMIT_ELEMENT_IDENTIFIER = "Commit";
    
    /**
     * The string defining the start of a commit element. This string is the constant part of such an element. The
     * variable attributes (the id and the date of the commit) following this string will be calculated at runtime
     * in {@link #createCommitFileContent(Commit)}. This method also adds the closing characters to yield a valid commit
     * start element.
     */
    private static final String CDMS_COMMIT_ELEMENT_START = CDMS_ELEMENT_START_PREFIX + CDMS_COMMIT_ELEMENT_IDENTIFIER 
            + CDMS_ELEMENT_ATTRIBUTES_OPEN_CHARACTER;
    
    /**
     * The string defining the end of a commit element. This string is the constant part of such an element. The
     * variable attributes (the id and the date of the commit) following this string will be calculated at runtime
     * in {@link #createCommitFileContent(Commit)}. This method also adds the closing characters to yield a valid commit
     * end element.
     */
    private static final String CDMS_COMMIT_ELEMENT_END = CDMS_ELEMENT_END_PREFIX + CDMS_COMMIT_ELEMENT_IDENTIFIER 
            + CDMS_ELEMENT_ATTRIBUTES_OPEN_CHARACTER;
    
    /**
     * The identifier for defining commit header start and end elements.
     */
    private static final String CDMS_COMMIT_HEADER_ELEMENT_IDENTIFIER = "CommitHeader";
    
    /**
     * The string defining the start of a commit header element.
     */
    private static final String CDMS_COMMIT_HEADER_ELEMENT_START = CDMS_ELEMENT_START_PREFIX 
            + CDMS_COMMIT_HEADER_ELEMENT_IDENTIFIER + CDMS_ELEMENT_CLOSE_CHARACTER;
    
    /**
     * The string defining the start of a commit header element.
     */
    private static final String CDMS_COMMIT_HEADER_ELEMENT_END = CDMS_ELEMENT_END_PREFIX 
            + CDMS_COMMIT_HEADER_ELEMENT_IDENTIFIER + CDMS_ELEMENT_CLOSE_CHARACTER;

    /**
     * The identifier for defining changed artifact start and end elements.
     */
    private static final String CDMS_CHANGED_ARTIFACT_ELEMENT_IDENTIFIER = "ChangedArtifact";
    
    /**
     * The string defining the start of a changed artifact element. This string is the constant part of such an element.
     * The variable attributes (the path and name of the changed artifact) following this string will be 
     * calculated at runtime in {@link #createChangedArtifactElement(ChangedArtifact)}. This method also adds the
     * closing characters to yield a valid changed artifact start element.
     */
    private static final String CDMS_CHANGED_ARTIFACT_ELEMENT_START = CDMS_ELEMENT_START_PREFIX 
            + CDMS_CHANGED_ARTIFACT_ELEMENT_IDENTIFIER + CDMS_ELEMENT_ATTRIBUTES_OPEN_CHARACTER;
    
    /**
     * The string defining the end of a changed artifact element. This string is the constant part of such an element.
     * The variable attributes (the path and name of the changed artifact) following this string will be 
     * calculated at runtime in {@link #createChangedArtifactElement(ChangedArtifact)}. This method also adds the
     * closing characters to yield a valid changed artifact end element.
     */
    private static final String CDMS_CHANGED_ARTIFACT_ELEMENT_END = CDMS_ELEMENT_END_PREFIX 
            + CDMS_CHANGED_ARTIFACT_ELEMENT_IDENTIFIER + CDMS_ELEMENT_ATTRIBUTES_OPEN_CHARACTER;

    /**
     * The identifier for defining changed artifact's diff header start and end elements.
     */
    private static final String CDMS_CHANGED_ARTIFACT_DIFF_HEADER_ELEMENT_IDENTIFIER = "DiffHeader";
    
    /**
     * The string defining the start of a changed artifact's diff header element.
     */
    private static final String CDMS_CHANGED_ARTIFACT_DIFF_HEADER_START = CDMS_ELEMENT_START_PREFIX 
            + CDMS_CHANGED_ARTIFACT_DIFF_HEADER_ELEMENT_IDENTIFIER + CDMS_ELEMENT_CLOSE_CHARACTER;
    
    /**
     * The string defining the end of a changed artifact's diff header element.
     */
    private static final String CDMS_CHANGED_ARTIFACT_DIFF_HEADER_END_PATTERN = CDMS_ELEMENT_END_PREFIX 
            + CDMS_CHANGED_ARTIFACT_DIFF_HEADER_ELEMENT_IDENTIFIER + CDMS_ELEMENT_CLOSE_CHARACTER;

    /**
     * The identifier for defining changed artifact's content start and end elements.
     */
    private static final String CDMS_CHANGED_ARTIFACT_CONTENT_ELEMENT_IDENTIFIER = "Content";
    
    /**
     * The string defining the start of a changed artifact's content element.
     */
    private static final String CDMS_CHANGED_ARTIFACT_CONTENT_START_PATTERN = CDMS_ELEMENT_START_PREFIX 
            + CDMS_CHANGED_ARTIFACT_CONTENT_ELEMENT_IDENTIFIER + CDMS_ELEMENT_CLOSE_CHARACTER;
    
    /**
     * The string defining the end of a changed artifact's content element.
     */
    private static final String CDMS_CHANGED_ARTIFACT_CONTENT_END_PATTERN = CDMS_ELEMENT_END_PREFIX 
            + CDMS_CHANGED_ARTIFACT_CONTENT_ELEMENT_IDENTIFIER + CDMS_ELEMENT_CLOSE_CHARACTER;
    
    /**
     * The prefix of each CDMS-file, which this serializer creates to save a particular {@link Commit}.
     */
    private static final String COMMIT_FILE_PREFIX = "Commit_";
    
    /**
     * The postfix of each CDMS-file, which this serializer creates to save a particular {@link Commit}.
     */
    private static final String COMMIT_FILE_POSTFIX = ".cdms";
    
    /**
     * The {@link FileUtilities} used to write and read serialized commits.
     */
    private static FileUtilities fileUtilities = FileUtilities.getInstance();
    
    /**
     * Saves the given {@link Commit} as CDMS-file to the given directory.
     * 
     * @param commit the commit to save; should never be <code>null</code>
     * @param targetDirectory the directory for saving the CDMS-file to; should always exist and be empty
     * @return <code>true</code> if saving the commit was successful; <code>false</code> otherwise
     */
    public static boolean serialize(Commit commit, File targetDirectory) {
        boolean serializationSuccessful = false;
        String commitFileName = COMMIT_FILE_PREFIX + commit.getId() + COMMIT_FILE_POSTFIX;
        String commitFileContent = createCommitFileContent(commit);
        if (commitFileContent != null && !commitFileContent.isEmpty()) {
            serializationSuccessful = fileUtilities.writeFile(targetDirectory.getAbsolutePath(), commitFileName,
                    commitFileContent, false);
        } else {
            Logger.getInstance().log(ID, "Saving commit \"" + commit.getId() + "\" failed", 
                    "No serialized commit string created", MessageType.WARNING);
        }
        return serializationSuccessful;
    }
    
    /**
     * Creates the full content of a CDMS-file as a single string based on the information provided by the given 
     * {@link Commit}.
     * 
     * @param commit the commit providing the input for creating the CDMS-file content string
     * @return a non-empty string representing the content of a CDMS-file or <code>null</code>, if creating that string
     *         failed
     */
    private static String createCommitFileContent(Commit commit) {
        String commitFileContent = null;
        
        String commitId = commit.getId();
        String commitDate = commit.getDate();
        String[] commitHeader = commit.getCommitHeader();
        // Commit header may be empty, which should result only in an empty commit header element
        if (!commitId.isEmpty() && !commitDate.isEmpty()) {
            StringBuilder commitFileContentBuilder = new StringBuilder();
            String commitElementAttributes = commitId + "," + commitDate;
            commitFileContentBuilder.append(CDMS_COMMIT_ELEMENT_START + commitElementAttributes 
                    + CDMS_ELEMENT_ATTRIBUTES_CLOSE_CHARACTER + CDMS_ELEMENT_CLOSE_CHARACTER + "\n\n");
            commitFileContentBuilder.append(createCommitHeaderElement(commitHeader));
            commitFileContentBuilder.append(createChangedArtifactElements(commit.getChangedArtifacts()));
            commitFileContentBuilder.append(CDMS_COMMIT_ELEMENT_END + commitElementAttributes 
                    + CDMS_ELEMENT_ATTRIBUTES_CLOSE_CHARACTER + CDMS_ELEMENT_CLOSE_CHARACTER);
            commitFileContent = commitFileContentBuilder.toString();
        } else {
            Logger.getInstance().log(ID, "Creating CDMS-file content for commit \"" + commit.getId() + "\" failed", 
                    "No commit id (\"" + commitId + "\") or date (\"" + commitDate + "\") available",
                    MessageType.WARNING);
        }
        
        return commitFileContent;
    }
    
    /**
     * Creates the commit header element based on the given set of strings. This set of strings should be the same as
     * calling {@link Commit#getCommitHeader()} on the commit to be saved to a CDMS-file.
     * 
     * @param commitHeader the set of strings representing the content of a commit header; should never be
     *        <code>null</code>
     * @return a string representing the complete commit header element for writing CDMS-files; never <code>null</code>,
     *         but may be <i>empty</i>
     */
    private static String createCommitHeaderElement(String[] commitHeader) {
        StringBuilder commitHeaderElementBuilder = new StringBuilder();
        commitHeaderElementBuilder.append(CDMS_COMMIT_HEADER_ELEMENT_START + "\n");
        for (int i = 0; i < commitHeader.length; i++) {
            commitHeaderElementBuilder.append(commitHeader[i] + "\n");
        }
        commitHeaderElementBuilder.append(CDMS_COMMIT_HEADER_ELEMENT_END + "\n\n");
        return commitHeaderElementBuilder.toString();
    }
    
    /**
     * Creates the full set of changed artifact elements as a single string based on the given list of
     * {@link ChangedArtifact}s. This list of changed artifacts should be the same as calling 
     * {@link Commit#getChangedArtifacts()} on the commit to be saved to a CDMS-file.
     * 
     * @param changedArtifacts the list of artifacts changed by the commit to be saved to a CDMS-file; should never be
     *        <code>null</code>
     * @return the string representing all changed artifact elements for a CDMS-file; never <code>null</code>, but may
     *         be <i>empty</i>, if the given list of changed artifacts is empty
     */
    private static String createChangedArtifactElements(List<ChangedArtifact> changedArtifacts) {
        StringBuilder changedArtifactElementsBuilder = new StringBuilder();
        for (ChangedArtifact changedArtifact : changedArtifacts) {
            changedArtifactElementsBuilder.append(createChangedArtifactElement(changedArtifact) + "\n");
        }
        return changedArtifactElementsBuilder.toString();
    }
    
    /**
     * Creates a changed artifact element based on the given {@link ChangedArtifact}.
     * 
     * @param changedArtifact the changed artifact providing the input for creating a changed artifact element; should
     *        never be <code>null</code>
     * @return a non-empty string representing the changed artifact element for a CDMS-file; never <code>null</code>
     */
    private static String createChangedArtifactElement(ChangedArtifact changedArtifact) {
        StringBuilder changedArtifactElementBuilder = new StringBuilder();
        
        String changedArtifactElementAttributes = changedArtifact.getArtifactPath() + "," 
                + changedArtifact.getArtifactName();
        changedArtifactElementBuilder.append(CDMS_CHANGED_ARTIFACT_ELEMENT_START + changedArtifactElementAttributes 
                + CDMS_ELEMENT_ATTRIBUTES_CLOSE_CHARACTER + CDMS_ELEMENT_CLOSE_CHARACTER + "\n");
        changedArtifactElementBuilder.append(createChangedArtifactDiffHeaderElement(changedArtifact.getDiffHeader()));
        changedArtifactElementBuilder.append(createChangedArtifactContentElement(changedArtifact.getContent()));
        changedArtifactElementBuilder.append(CDMS_CHANGED_ARTIFACT_ELEMENT_END + changedArtifactElementAttributes
                + CDMS_ELEMENT_ATTRIBUTES_CLOSE_CHARACTER + CDMS_ELEMENT_CLOSE_CHARACTER + "\n");
        return changedArtifactElementBuilder.toString();
    }
    
    /**
     * Creates a changed artifact's diff header element based on the given list of strings. This list of strings should
     * be the same as calling {@link ChangedArtifact#getDiffHeader()} on an artifact changed by the commit to be saved
     * to a CDMS-file.
     * 
     * @param diffHeader the list of strings representing the content of a changed artifact's diff header; should never
     *        be <code>null</code>
     * @return a string representing the changed artifact's diff header element for a CDMS-file; never
     *         <code>null</code>, but may be <i>empty</i>
     */
    private static String createChangedArtifactDiffHeaderElement(List<String> diffHeader) {
        StringBuilder changedArtifactDiffHeaderElementBuilder = new StringBuilder();
        changedArtifactDiffHeaderElementBuilder.append(CDMS_CHANGED_ARTIFACT_DIFF_HEADER_START + "\n");
        for (String diffHeaderLine : diffHeader) {
            changedArtifactDiffHeaderElementBuilder.append(diffHeaderLine + "\n");
        }
        changedArtifactDiffHeaderElementBuilder.append(CDMS_CHANGED_ARTIFACT_DIFF_HEADER_END_PATTERN + "\n");
        return changedArtifactDiffHeaderElementBuilder.toString();
    }
    
    /**
     * Creates a changed artifact's content element based on the given list of strings. This list of strings should be
     * the same as calling {@link ChangedArtifact#getContent()} on an artifact changed by the commit to be saved to a
     * CDMS-file.
     * 
     * @param content the list of strings representing the content of a changed artifact; should never be
     *        <code>null</code>
     * @return a string representing the changed artifact's content element for a CDMS-file; never <code>null</code>,
     *         but may be <i>empty</i>
     */
    private static String createChangedArtifactContentElement(List<String> content) {
        StringBuilder changedArtifactContentElementBuilder = new StringBuilder();
        changedArtifactContentElementBuilder.append(CDMS_CHANGED_ARTIFACT_CONTENT_START_PATTERN + "\n");
        for (String contentLine : content) {
            changedArtifactContentElementBuilder.append(contentLine + "\n");
        }
        changedArtifactContentElementBuilder.append(CDMS_CHANGED_ARTIFACT_CONTENT_END_PATTERN + "\n");
        return changedArtifactContentElementBuilder.toString();
    }
    
    /**
     * Adds the information of the given CDMS-file to the given {@link Commit}.
     * 
     * @param commit a commit to which the information of the given CDMS-file will be added; should never be
     *        <code>null</code>
     * @param commitCdmsFile the CDMS-file containing the information, which will be added to the commit; should never
     *        be <code>null</code>
     * @return <code>true</code> if the information of the CDMS-file was added to the commit successfully;
     *         <code>false</code> otherwise
     */
    public static boolean deserialize(Commit commit, File commitCdmsFile) {
        boolean deserializationSuccessful = false;
        List<String> commitCdmsFileLines = fileUtilities.readFile(commitCdmsFile);
        if (commitCdmsFileLines != null && !commitCdmsFileLines.isEmpty()) {
            int commitElementStartIndex = getIndexOf(CDMS_COMMIT_ELEMENT_START, commitCdmsFileLines, true, 0);
            if (commitElementStartIndex > -1) {
                String commitElementStartString = commitCdmsFileLines.get(commitElementStartIndex);
                int commitElementAttributesStartIndex = 
                        commitElementStartString.indexOf(CDMS_ELEMENT_ATTRIBUTES_OPEN_CHARACTER);
                int commitElementAttributesSeperatorIndex = commitElementStartString.indexOf(",");
                int commitElementAttributesEndIndex = 
                        commitElementStartString.lastIndexOf(CDMS_ELEMENT_ATTRIBUTES_CLOSE_CHARACTER);
                if (commitElementAttributesStartIndex > -1 
                        && commitElementAttributesSeperatorIndex > commitElementAttributesStartIndex 
                        && commitElementAttributesEndIndex > commitElementAttributesSeperatorIndex) {
                    commit.addId(commitElementStartString.substring(commitElementAttributesStartIndex + 1,
                            commitElementAttributesSeperatorIndex));
                    commit.addDate(
                            commitElementStartString.substring(commitElementAttributesSeperatorIndex + 1,
                            commitElementAttributesEndIndex));
                    addCommitHeader(commit, commitCdmsFileLines);
                    addChangedArtifacts(commit, commitCdmsFileLines);
                    deserializationSuccessful = true;
                } else {
                    Logger.getInstance().log(ID, "CDMS-file \"" + commitCdmsFile.getAbsolutePath() 
                        + "\" has no commit attributes", "Nothing to deserialize", MessageType.WARNING);
                }
            } else {
                Logger.getInstance().log(ID, "CDMS-file \"" + commitCdmsFile.getAbsolutePath() 
                        + "\" has no commit element", "Nothing to deserialize", MessageType.WARNING);
            }
        } else {
            Logger.getInstance().log(ID, "CDMS-file \"" + commitCdmsFile.getAbsolutePath() + "\" has no content",
                    "Nothing to deserialize", MessageType.WARNING);
        }
        return deserializationSuccessful;
    }
    
    /**
     * Adds the commit header element identified in the given CDMS-file lines as a set of strings to the given
     * {@link Commit}.
     * 
     * @param commit the commit to which the commit header shall be added
     * @param commitCdmsFileLines the content of a CDMS-file, in which the commit header information shall be found;
     *        should never be <code>null</code>
     */
    private static void addCommitHeader(Commit commit, List<String> commitCdmsFileLines) {
        int commitHeaderElementStartIndex = getIndexOf(CDMS_COMMIT_HEADER_ELEMENT_START, commitCdmsFileLines, false, 0);
        int commitHeaderElementEndIndex = getIndexOf(CDMS_COMMIT_HEADER_ELEMENT_END, commitCdmsFileLines, false,
                commitHeaderElementStartIndex);
        if (commitHeaderElementStartIndex > -1 && commitHeaderElementEndIndex > commitHeaderElementStartIndex) {
            String[] commitHeaderLines = new String[commitHeaderElementEndIndex - (commitHeaderElementStartIndex + 1)];
            int commitHeaderIndex = 0;
            for (int i = commitHeaderElementStartIndex + 1; i < commitHeaderElementEndIndex; i++) {
                commitHeaderLines[commitHeaderIndex] = commitCdmsFileLines.get(i);
                commitHeaderIndex++;
            }
            commit.addCommitHeader(commitHeaderLines);
        }
    }
    
    /**
     * Adds all changed artifact elements identified in the given CDMS-file lines as {@link ChangedArtifact}s to the
     * given {@link Commit}.
     * 
     * @param commit the commit to which the changed artifacts shall be added; should never be <code>null</code>
     * @param commitCdmsFileLines the content of a CDMS-file, in which the changed artifacts shall be found; should
     *        never be <code>null</code>
     */
    private static void addChangedArtifacts(Commit commit, List<String> commitCdmsFileLines) {
        int changedArtifactElementStartIndex = getIndexOf(CDMS_CHANGED_ARTIFACT_ELEMENT_START,
                commitCdmsFileLines, true, 0);
        int changedArtifactElementEndIndex = getIndexOf(CDMS_CHANGED_ARTIFACT_ELEMENT_END,
                commitCdmsFileLines, true, changedArtifactElementStartIndex);
        if (changedArtifactElementStartIndex > 0 && changedArtifactElementEndIndex > changedArtifactElementStartIndex) {
            while (changedArtifactElementStartIndex > 0 
                    && changedArtifactElementEndIndex > changedArtifactElementStartIndex) {
                addChangedArtifact(commit, commitCdmsFileLines.subList(changedArtifactElementStartIndex,
                        changedArtifactElementEndIndex + 1));
                changedArtifactElementStartIndex = getIndexOf(CDMS_CHANGED_ARTIFACT_ELEMENT_START,
                        commitCdmsFileLines, true, changedArtifactElementStartIndex + 1);
                changedArtifactElementEndIndex = getIndexOf(CDMS_CHANGED_ARTIFACT_ELEMENT_END,
                        commitCdmsFileLines, true, changedArtifactElementStartIndex);
            }            
        }
    }
    
    /**
     * Adds a changed artifact element represented by the given list of strings as {@link ChangedArtifact} to the
     * given {@link Commit}.
     * 
     * @param commit the commit to which the changed artifact shall be added; should never be <code>null</code>
     * @param changedArtifactElementStrings the list of strings representing a changed artifact element of a
     *        CDMS-file; should never be <code>null</code> nor <i>empty</i>
     */
    private static void addChangedArtifact(Commit commit,
            List<String> changedArtifactElementStrings) {
        String changedArtifactElementStartString = changedArtifactElementStrings.get(0);
        int changedArtifactElementAttributesStartIndex = 
                changedArtifactElementStartString.indexOf(CDMS_ELEMENT_ATTRIBUTES_OPEN_CHARACTER);
        int changedArtifactElementAttributesSeperatorIndex = changedArtifactElementStartString.indexOf(",");
        int changedArtifactElementAttributesEndIndex = 
                changedArtifactElementStartString.lastIndexOf(CDMS_ELEMENT_ATTRIBUTES_CLOSE_CHARACTER);
        if (changedArtifactElementAttributesStartIndex > -1 
                && changedArtifactElementAttributesSeperatorIndex > changedArtifactElementAttributesStartIndex 
                && changedArtifactElementAttributesEndIndex > changedArtifactElementAttributesSeperatorIndex) {
            ChangedArtifact changedArtifact = new ChangedArtifact();
            changedArtifact.addArtifactPath(changedArtifactElementStartString
                    .substring(changedArtifactElementAttributesStartIndex + 1,
                            changedArtifactElementAttributesSeperatorIndex));
            changedArtifact.addArtifactName(changedArtifactElementStartString
                    .substring(changedArtifactElementAttributesSeperatorIndex + 1,
                            changedArtifactElementAttributesEndIndex));

            // Use the same variables for calculating start and end indexes of diff header and content elements
            // Add the diff header strings
            int changedArtifactChildElementStartIndex = getIndexOf(CDMS_CHANGED_ARTIFACT_DIFF_HEADER_START,
                    changedArtifactElementStrings, false, 1);
            int changedArtifactChildElementEndIndex = getIndexOf(CDMS_CHANGED_ARTIFACT_DIFF_HEADER_END_PATTERN,
                    changedArtifactElementStrings, false, changedArtifactChildElementStartIndex);
            for (int i = changedArtifactChildElementStartIndex + 1; i < changedArtifactChildElementEndIndex; i++) {
                changedArtifact.addDiffHeaderLine(changedArtifactElementStrings.get(i));
            }
            // Add the content strings
            changedArtifactChildElementStartIndex = getIndexOf(CDMS_CHANGED_ARTIFACT_CONTENT_START_PATTERN,
                    changedArtifactElementStrings, false, changedArtifactChildElementEndIndex);
            changedArtifactChildElementEndIndex = getIndexOf(CDMS_CHANGED_ARTIFACT_CONTENT_END_PATTERN,
                    changedArtifactElementStrings, false, changedArtifactChildElementStartIndex);
            for (int i = changedArtifactChildElementStartIndex + 1; i < changedArtifactChildElementEndIndex; i++) {
                changedArtifact.addContentLine(changedArtifactElementStrings.get(i));
            }
            commit.addChangedArtifact(changedArtifact);
        }
    }
    
    /**
     * Returns the index of the first occurrence of the given search string after the given start index in the given
     * list of strings. A non-negative index is only returned, if an element in the list of strings after the start
     * index exists, which either starts with the given search string or is entirely equal to it. The criteria for a 
     * match depends on the given value for the parameter <code>startsWith</code>.
     *   
     * @param searchString the string for which the index in the given list shall be returned; should never be
     *        <code>null</code>
     * @param stringList the list of strings in which the given string shall be found; should never be <code>null</code>
     * @param startsWith <code>true</code> if the string in the list for which the index shall be returned should only
     *        start with the given searchString; <code>false</code> if that string must be entirely equal to the search
     *        string
     * @param startIndex a non-negative integer value defining the index of the list of strings from which searching for
     *        the search string shall be started 
     * @return the index of the string in the list of strings or <i>-1</i>, if that string could not be found
     */
    private static int getIndexOf(String searchString, List<String> stringList, boolean startsWith, int startIndex) {
        int index = -1;
        int stringListCounter = startIndex;
        if (startIndex > -1) {            
            if (startsWith) {
                while (index == -1 && stringListCounter < stringList.size()) {
                    if (stringList.get(stringListCounter).startsWith(searchString)) {
                        index = stringListCounter;
                    }
                    stringListCounter++;
                }
            } else {            
                while (index == -1 && stringListCounter < stringList.size()) {
                    if (stringList.get(stringListCounter).equals(searchString)) {
                        index = stringListCounter;
                    }
                    stringListCounter++;
                }
            }
        }
        return index;
    }
}
