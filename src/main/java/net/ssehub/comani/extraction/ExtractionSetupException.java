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
package net.ssehub.comani.extraction;

/**
 * Custom exception indicating and describing problems while setting-up an extractor.
 * 
 * @author Christian Kroeher
 *
 */
public class ExtractionSetupException extends Exception {

    /**
     * The ID of this custom exception as required when sub-classing {@link Exception}.
     */
    private static final long serialVersionUID = 8503949260237418396L;
    
    /**
     * Constructs a new {@link ExtractionSetupException}. 
     */
    public ExtractionSetupException() {}
    
    /**
     * Constructs a new {@link ExtractionSetupException}.
     * 
     * @param message the description of the problem causing this exception
     */
    public ExtractionSetupException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link ExtractionSetupException}.
     * 
     * @param message the description of the problem causing this exception
     * @param cause the exception causing this exception
     */
    public ExtractionSetupException(String message, Throwable cause) {
        super(message, cause);
    }
}
