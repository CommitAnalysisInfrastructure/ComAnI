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

/**
 * This class represents the specific type of exception thrown if checking and preparing the properties fails at some
 * point, e.g., due to missing information, false arguments, etc.
 * 
 * @author Christian Kroeher
 *
 */
public class SetupException extends Exception {

    /**
     * The generated serial ID of this class of exceptions.
     */
    private static final long serialVersionUID = -4578304055797070802L;

    /**
     * Constructs a new {@link SetupException}.
     * 
     * @param message the description of the problem causing this exception
     */
    public SetupException(String message) {
        super(message);
    }   

    /**
     * Constructs a new {@link SetupException}.
     * 
     * @param message the description of the problem causing this exception
     * @param cause the exception causing this exception
     */
    public SetupException(String message, Throwable cause) {
        super(message, cause);
    }
}
