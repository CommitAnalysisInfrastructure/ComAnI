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
 * This interface defines a single method for accessing the result of a single commit. Specific commit analyzers may
 * implement this interface, if their usage requires a programmatic access to their results instead of writing them
 * to a results file.
 *  
 * @author Christian Kroeher
 *
 */
public interface IAccessibleResult {

    /**
     * Returns the result of the analysis of the current commit performed by this commit analyzer instance.
     * 
     * @return the current analysis result or <code>null</code>, if no analysis was performed
     */
    public AnalysisResult getAnalysisResult();

}
