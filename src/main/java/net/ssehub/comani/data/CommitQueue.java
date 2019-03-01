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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * This class realizes a commit queue. A commit extractor supplies this queue with {@link Commit} objects, which a
 * commit analyzer consumes.
 *  
 * @author Christian Kroeher
 *
 */
public class CommitQueue implements IExtractionQueue, IAnalysisQueue {
    
    /**
     * This enumeration defines the different states of the commit queue. The state is typically defined by the
     * {@link net.ssehub.comani.extraction.ExtractionManager} as the extraction process determines whether commits are
     * available.
     * The possible state are:
     * <ul>
     * <li>INIT: Indicates that the queue is ready for use but not yet open</li>
     * <li>OPEN: Indicates that more commits will be added</li>
     * <li>CLOSED: Indicates that no more commits will be added and it will not be used anymore</li>
     * </ul>
     * 
     * @author Christian Kroeher
     *
     */
    public enum QueueState { INIT, OPEN, CLOSED };
    
    /**
     * The maximum number of {@link Commit} elements this queue manages simultaneously. This value limits the addition
     * of elements (blocking the commit extractor), e.g., to prevent memory problems. 
     */
    private int maxQueueElements;

    /**
     * The current {@link QueueState} of this queue. 
     */
    private QueueState state;
    
    /**
     * The directory for saving extracted commits. Its value is set by the {@link #enableCommitCaching(File)} method,
     * which is called by the {@link net.ssehub.comani.extraction.ExtractionManager} based on the optional, user-defined
     * value of the {@link net.ssehub.comani.core.Setup#PROPERTY_EXTRACTION_CACHING} property. Hence, the default value
     * is <code>null</code>, if that property is not defined, meaning that no commits shall be saved for reuse.
     */
    private File commitsCacheDirectory;
    
    /**
     * Defines whether this queue will be closed if all commits are analyzed (<code>true</code>) or not
     * (<code>false</code>). This flag is needed in case the {@link net.ssehub.comani.extraction.ExtractionManager}
     * closes the queue due to finished extraction, but the analyzer is not finished analyzing. The queue is then
     * waiting for the analyzer to get all commits.
     */
    private boolean prepareClose;
    
    /**
     * Internal commit storage.
     */
    private List<Commit> commits;
    
    /**
     * Constructs an empty commit queue.
     * 
     * @param maxElements the maximum number of {@link Commit} elements this queue manages simultaneously; this value
     *        has to be between 1 and 100.000, which is not checked here, but during
     *        {@link net.ssehub.comani.core.Setup}. 
     */
    public CommitQueue(int maxElements) {
        state = QueueState.INIT;
        maxQueueElements = maxElements;
        commitsCacheDirectory = null;
        prepareClose = false;
        commits = new LinkedList<Commit>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOpen() {
        return state == QueueState.OPEN;
    }
    
    /**
     * Sets the state of this queue to the given new {@link QueueState}.
     * 
     * @param newState the new state for this queue
     */
    public synchronized void setState(QueueState newState) {
        if (newState == QueueState.CLOSED) {
            if (commits.isEmpty()) {
                // If there are no more commits to analyze, close this queue 
                state = newState;
            } else {
                // If there are commits to analyze, wait until this is done
                prepareClose = true;
            }
        } else {
            state = newState;
        }
    }
    
    /**
     * Activates the optional saving of commits received via {@link #addCommit(Commit)} for future analyses.
     * 
     * @param commitsCacheDirectory an existing and empty directory to which the {@link CommitSerializer} shall save
     *        the commits provided to this queue; if <code>null</code> is given, which is the default value, saving will
     *        be disabled
     */
    public void enableCommitCaching(File commitsCacheDirectory) {
        synchronized (this) {            
            this.commitsCacheDirectory = commitsCacheDirectory;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Commit getCommit() {
        synchronized (this) {            
            Commit commit = null;
            if (state == QueueState.OPEN && !commits.isEmpty()) {
                commit = commits.remove(0);
                if (commits.isEmpty() && prepareClose) {
                    setState(QueueState.CLOSED);
                }
            }
            return commit;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addCommit(Commit commit) {
        synchronized (this) {
            boolean additionSuccessful = false;
            if (state == QueueState.OPEN && !prepareClose && commits.size() < maxQueueElements) {
                additionSuccessful = commits.add(commit);
                if (commitsCacheDirectory != null) {
                    // Caching is enabled, which should save extracted commits to the directory above
                    CommitSerializer.serialize(commit, commitsCacheDirectory);
                }
            }
            return additionSuccessful;
        }
    }
}
