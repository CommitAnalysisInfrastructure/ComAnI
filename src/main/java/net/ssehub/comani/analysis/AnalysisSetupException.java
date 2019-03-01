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
 * Custom exception indicating and describing problems while setting-up an analysis.
 * 
 * @author Christian Kroeher
 *
 */
public class AnalysisSetupException extends Exception {

    /**
     * The ID of this custom exception as required when sub-classing {@link Exception}.
     */
    private static final long serialVersionUID = -4197311491699091553L;
    
    /**
     * Constructs a new {@link AnalysisSetupException}. 
     */
    public AnalysisSetupException() {}
    
    /**
     * Constructs a new {@link AnalysisSetupException}.
     * 
     * @param message the description of the problem causing this exception
     */
    public AnalysisSetupException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link AnalysisSetupException}.
     * 
     * @param message the description of the problem causing this exception
     * @param cause the exception causing this exception
     */
    public AnalysisSetupException(String message, Throwable cause) {
        super(message, cause);
    }
}
    