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

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a changed artifact as provided by the diff of a commit. Objects of this class always belong to
 * the a specific {@link Commit}. They basically provide two lists of strings, from which one contains all lines
 * belonging to the diff header (general information about the artifact, the number of changed lines, etc.) and the
 * other contains the actual contents of the artifact including the changed lines.
 * 
 * @author Christian Kroeher
 *
 */
public class ChangedArtifact {
    
    /**
     * The relative path to this changed artifact in the repository. Never <code>null</code>, but may be <i>empty</i>.
     */
    private String artifactPath;
    
    /**
     * The name of this changed artifact (the file it represents). Never <code>null</code>, but may be <i>empty</i>.
     */
    private String artifactName;
    
    /**
     * The diff header providing general information about this changed artifact, like its absolute path and name, the
     * number of changed lines, etc. Never <code>null</code>, but may be <i>empty</i>.
     */
    private List<String> diffHeader;
    
    /**
     * The actual content of this changed artifact including the changed lines. Never <code>null</code>, but may be
     * <i>empty</i>.
     */
    private List<String> content;

    /**
     * Constructs a new changed artifact.
     */
    public ChangedArtifact() {
        artifactPath = "";
        artifactName = "";
        diffHeader = new ArrayList<String>();
        content = new ArrayList<String>();
    }
    
    /**
     * Adds the given string as the relative path to this changed artifact in the repository.
     *  
     * @param artifactPath the relative path to this changed artifact in the repository; should never be
     *        <code>null</code>
     */
    public void addArtifactPath(String artifactPath) {
        this.artifactPath = artifactPath;
    }
    
    /**
     * Adds the given string as the name of this changed artifact.
     * 
     * @param artifactName the name of this changed artifact (the file it represents); should never be <code>null</code>
     */
    public void addArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }
    
    /**
     * Adds a new string representing a line in the diff header to this changed artifact's diff header.
     * 
     * @param diffHeaderLine a single line of the diff header; should never be <code>null</code>
     */
    public void addDiffHeaderLine(String diffHeaderLine) {
        diffHeader.add(diffHeaderLine);
    }
    
    /**
     * Adds a new string representing a line of content of this changed artifact.
     * 
     * @param contentLine a single content line; should never be <code>null</code>
     */
    public void addContentLine(String contentLine) {
        content.add(contentLine);
    }
    
    /**
     * Returns the string denoting the relative path to this changed artifact in the repository.
     * 
     * @return the relative path to this changed artifact in the repository; never <code>null</code>, but may be
     *         <i>empty</i>.
     */
    public String getArtifactPath() {
        return artifactPath;
    }
    
    /**
     * Returns the string denoting the name of this changed artifact. 
     * 
     * @return the name of this changed artifact (the file it represents); never <code>null</code>, but may be
     *         <i>empty</i>.
     */
    public String getArtifactName() {
        return artifactName;
    }
    
    /**
     * Returns the diff header of this changed artifact.
     * 
     * @return the diff header providing general information about this changed artifact, like its absolute
     *         path and name, the number of changed lines, etc.; never <code>null</code>, but may be <i>empty</i>
     */
    public List<String> getDiffHeader() {
        return diffHeader;
    }
    
    /**
     * Return the content of this changed artifact.
     * 
     * @return the actual content of this changed artifact including the changed lines; never <code>null</code>, but
     *         may be <i>empty</i>
     */
    public List<String> getContent() {
        return content;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {        
        StringBuilder changedArtifactStringBuilder = new StringBuilder();
        // Intro: name and path
        changedArtifactStringBuilder.append(artifactName + "@" + artifactPath);
        changedArtifactStringBuilder.append("\n");
        // First block: diff header
        String blockSingleString = listToString(diffHeader);
        if (!blockSingleString.isEmpty()) {
            changedArtifactStringBuilder.append(blockSingleString);
        }
        // Second block: content changes
        blockSingleString = listToString(content);
        if (!blockSingleString.isEmpty()) {
            changedArtifactStringBuilder.append("\n");
            changedArtifactStringBuilder.append(blockSingleString);
        }
        return changedArtifactStringBuilder.toString();
    }
    
    /**
     * Converts the given list of strings into a single string by concatenating each element in the list using line
     * breaks.
     * 
     * @param stringList the list of strings to be converted into a single string
     * @return a single string containing of all elements of the given list concatenated by line breaks; never
     *         <code>null</code> but may be <i>empty</i>
     */
    private String listToString(List<String> stringList) {
        StringBuilder stringBuilder = new StringBuilder();
        if (stringList != null && !stringList.isEmpty()) {
            stringBuilder.append(stringList.get(0));
            for (int i = 1; i < stringList.size(); i++) {
                stringBuilder.append("\n" + stringList.get(i));
            }
        }
        return stringBuilder.toString();
    }
}
