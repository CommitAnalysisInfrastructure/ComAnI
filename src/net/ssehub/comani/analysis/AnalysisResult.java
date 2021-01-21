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
 * This class represents the results of the analysis of a particular commit.
 * 
 * @author Christian Kroeher
 *
 */
public class AnalysisResult {

    /**
     * The id of the commit this analysis result belongs to.
     */
    private String commitId;
    
    /**
     * The result of analyzing changes to code artifacts of the given commit. Each entry in this list represents a code
     * artifact (its path), which was changed in a way that requires a dead code analysis.
     */
    private List<String> relevantCodeChanges;
    
    /**
     * The result of analyzing changes to build artifacts of the given commit in terms of whether they are relevant
     * (<code>true</code>) or not (<code>false</code>).
     */
    private boolean relevantBuildChanges;
    
    /**
     * The result of analyzing changes to variability model artifacts of the given commit in terms of whether they are
     * relevant (<code>true</code>) or not (<code>false</code>).
     */
    private boolean relevantVariabilityModelChanges;
    
    /**
     * Constructs an empty {@link AnalysisResult}.
     */
    public AnalysisResult() {
        commitId = null;
        relevantCodeChanges = new ArrayList<String>();
        relevantBuildChanges = false;
        relevantVariabilityModelChanges = false;
    }
    
    /**
     * Returns the id of the commit to which this analysis result belongs to.
     * 
     * @return the {@link #commitId}; may be <code>null</code> 
     */
    public String getCommitId() {
        return commitId;
    }
    
    /**
     * Returns the list of all code files (their path) for which relevant changes were identified, that require a dead
     * code analysis.
     * 
     * @return the {@link #relevantCodeChanges}; never <code>null</code>, but may be <i>empty</i>
     */
    public List<String> getRelevantCodeChanges() {
        return relevantCodeChanges;
    }
    
    /**
     * Returns whether relevant changes to build files were identified (<code>true</code>) or not (<code>false</code>).
     * 
     * @return the {@link #relevantBuildChanges}
     */
    public boolean getRelevantBuildChanges() {
        return relevantBuildChanges;
    }
    
    /**
     * Returns whether relevant changes to variability model files were identified (<code>true</code>) or not 
     * (<code>false</code>).
     * 
     * @return the {@link #relevantVariabilityModelChanges}
     */
    public boolean getRelevantVariabilityModelChanges() {
        return relevantVariabilityModelChanges;
    }
    
    /**
     * Sets the id of the commit to which this analysis result belongs to.
     *  
     * @param id the {@link #commitId}
     */
    public void setCommitId(String id) {
        commitId = id;
    }
    
    /**
     * Adds the given string as path of a changed code file to this result.
     *  
     * @param path the path of a changed code file
     */
    public void addRelevantCodeChanges(String path) {
        relevantCodeChanges.add(path);
    }
    
    /**
     * Sets whether build artifacts have been changed in a relevant way.
     * 
     * @param relevant (<code>true</code>) if build changes were relevant; (<code>false</code>) otherwise
     */
    public void setRelevantBuildChanges(boolean relevant) {
        relevantBuildChanges = relevant;
    }
    
    /**
     * Sets whether variability model artifacts have been changed in a relevant way.
     * 
     * @param changed (<code>true</code>) if variability model changes were relevant; (<code>false</code>) otherwise
     */
    public void setRelevantVariabilityModelChanges(boolean changed) {
        relevantVariabilityModelChanges = changed;
    }
}
