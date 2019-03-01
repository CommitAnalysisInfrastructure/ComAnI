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

/**
 * This interface declares the methods a {@link CommitQueue} has to implement for its use by a commit extractor.
 * 
 * @author Christian Kroeher
 *
 */
public interface IExtractionQueue {

    /**
     * Adds the given {@link Commit} to the end of this queue if its status is
     * {@link CommitQueue.QueueState#OPEN} and the queue is not full.
     * 
     * @param commit the new commit added to the end of this queue if the queue is open
     * @return <code>true</code> if adding the commit to this queue was successful; <code>false</code> if the queue has
     * not the status {@link CommitQueue.QueueState#OPEN} or there are currently too much commits in the queue
     */
    public boolean addCommit(Commit commit);
}
