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

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a specific commit analysis result, which provides information about code, build, and
 * variability model artifacts affected by the changes of the analyzed commit. All artifacts or artifact types listed in
 * this result are changed by the commit with respect to their variability information.
 * 
 * @author Christian Kroeher
 *
 */
public class VerificationRelevantResult extends AbstractAnalysisResult {

    /**
     * The result of analyzing changes to code artifacts of the commit this result belongs to. Each entry in this list
     * represents a code artifact (its path), for which the respective commit changes variability information.
     */
    private List<String> relevantCodeChanges;
    
    /**
     * The result of analyzing changes to build artifacts of the commit this result belongs to. An attribute value of
     * <code>true</code> indicates that variability information in at least one build artifact is changed by the
     * respective commit. An attribute value of <code>false</code> indicates that the respective commit does not change
     * any build artifact or at least no variability information in build artifacts. 
     */
    private boolean relevantBuildChanges;
    
    /**
     * The result of analyzing changes to variability model artifacts of the commit this result belongs to. An attribute
     * value of <code>true</code> indicates that variability information in at least one variability model artifact is
     * changed by the respective commit. An attribute value of <code>false</code> indicates that the respective commit
     * does not change any variability model artifact or at least no variability information in variability model
     * artifacts. 
     */
    private boolean relevantVariabilityModelChanges;
    
    /**
     * Constructs a new instance of this class.
     */
    public VerificationRelevantResult() {
        relevantCodeChanges = new ArrayList<String>();
        relevantBuildChanges = false;
        relevantVariabilityModelChanges = false;
    }
    
    /**
     * Returns the list of all code files (their path) for which the commit this analysis result belongs to changes
     * variability information. 
     * 
     * @return the list of changed code files; never <code>null</code>, but may be <i>empty</i>
     */
    public List<String> getRelevantCodeChanges() {
        return relevantCodeChanges;
    }
    
    /**
     * Returns whether the commit this analysis result belongs to changes variability information in at least one build
     * artifact.
     * 
     * @return <code>true</code>, if variability information in at least one build artifact changes; <code>false</code>
     *         otherwise
     */
    public boolean getRelevantBuildChanges() {
        return relevantBuildChanges;
    }
    
    /**
     * Returns whether the commit this analysis result belongs to changes variability information in at least one
     * variability model artifact.
     * 
     * @return <code>true</code>, if variability information in at least one variability model artifact changes;
     *         <code>false</code> otherwise
     */
    public boolean getRelevantVariabilityModelChanges() {
        return relevantVariabilityModelChanges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAnalysisResult() {
        StringBuilder resultStringBuilder = new StringBuilder();
        resultStringBuilder.append("Verification Analysis Result\n");
        resultStringBuilder.append("\tCommit: ");
        resultStringBuilder.append(getCommitIdentifier());
        resultStringBuilder.append("\n");
        resultStringBuilder.append("\tChanged code artifacts:");
        for (String changedCodeArtifact : relevantCodeChanges) {
            resultStringBuilder.append("\n\t\t");
            resultStringBuilder.append(changedCodeArtifact);
        }
        resultStringBuilder.append("\n");
        resultStringBuilder.append("\tChanged build artifacts:");
        resultStringBuilder.append(relevantBuildChanges);
        resultStringBuilder.append("\n");
        resultStringBuilder.append("\tChanged variability model artifacts:");
        resultStringBuilder.append(relevantVariabilityModelChanges);
        return resultStringBuilder.toString();
    }

    /**
     * Adds the given string as path of code file to this result. Code file paths should only be added, if the commit
     * this result belongs to changes their variability information.
     *  
     * @param path the path of a changed code file
     */
    public void addRelevantCodeChanges(String path) {
        relevantCodeChanges.add(path);
    }
    
    /**
     * Sets whether the commit this result belongs to changes variability information in at least one build artifact.
     * 
     * @param changed <code>true</code>, if variability information in at least one build artifact changes;
     *        <code>false</code> otherwise
     */
    public void setRelevantBuildChanges(boolean changed) {
        relevantBuildChanges = changed;
    }
    
    /**
     * Sets whether the commit this result belongs to changes variability information in at least one variability
     * model artifact.
     * 
     * @param changed <code>true</code>, if variability information in at least one variability model artifact changes;
     *        <code>false</code> otherwise
     */
    public void setRelevantVariabilityModelChanges(boolean changed) {
        relevantVariabilityModelChanges = changed;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getAnalysisResult();
    }

}
