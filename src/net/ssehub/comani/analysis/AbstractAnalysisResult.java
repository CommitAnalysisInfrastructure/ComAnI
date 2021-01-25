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

/**
 * This abstract class represents a generic result of an analysis of a single commit. A specific analyzer may realize
 * its specific sub-class of this class in order to provide its results as required by the user or a third-party tool.
 *  
 * @author Christian Kroeher
 *
 */
public abstract class AbstractAnalysisResult {

    /**
     * The identifier (e.g., SHA) of the commit this analysis result belongs to.
     */
    private String commitIdentifier;
    
    /**
     * Constructs a new instance of this class.
     */
    public AbstractAnalysisResult() {
        commitIdentifier = null;
    }
    
    /**
     * Returns the identifier (e.g., SHA) of the commit to which this analysis result belongs to.
     * 
     * @return the commit identifier or <code>null</code>, if this attribute is not set yet
     * @see #setCommitIdentifier(String)
     */
    public String getCommitIdentifier() {
        return commitIdentifier;
    }
    
    /**
     * Returns textual description of this analysis result as a single string.
     * 
     * @return the textual description of this analysis result
     */
    public abstract String getAnalysisResult();
    
    /**
     * Sets the identifier (e.g., SHA) of the commit to which this analysis result belongs to.
     *  
     * @param identifier the commit identifier; should not be <code>null</code>
     */
    public void setCommitIdentifier(String identifier) {
        commitIdentifier = identifier;
    }
    
}
