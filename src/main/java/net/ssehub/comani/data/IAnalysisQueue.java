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
 * This interface declares the methods a {@link CommitQueue} has to implement for its use by a commit analysis.
 * 
 * @author Christian Kroeher
 *
 */
public interface IAnalysisQueue {
    
    /**
     * Returns whether this queue is open (<code>true</code>) or not (<code>false</code>). This method should be
     * used in combination with the {@link #getCommit()} method in order to check whether obtaining commits from
     * this queue is possible as only open queues will provide commits. Further, only checking for non-null returns
     * from the {@link #getCommit()} method is not enough as extraction of commits may take longer than analyzing them.
     * Hence, a proposed combination of usage is:
     * <pre>
     * {@code
     * while(queue.isOpen()) {
     *     Commit commit = queue.getCommit();
     *     if (commit != null) {
     *         // Do something with the commit
     *     }
     * }
     * }
     * </pre>
     * @return <code>true</code> if this queue is open and will provide commits; <code>false</code> otherwise
     */
    public boolean isOpen();

    /**
     * Returns the first {@link Commit} in the queue if its status is
     * {@link CommitQueue.QueueState#OPEN}. If the queue has a different status or there are currently no
     * commits available, this method will return <code>null</code>. However, returning <code>null</code> does not
     * indicate that there are nor further commits extracted. See {@link #isOpen()} for more information.
     * 
     * @return the first commit in this queue or <code>null</code> if the queue is not open or no commit is available
     */
    public Commit getCommit();
}