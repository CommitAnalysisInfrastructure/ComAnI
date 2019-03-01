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
package net.ssehub.comani.utility;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import net.ssehub.comani.analysis.AbstractCommitAnalyzer;
import net.ssehub.comani.core.Logger;
import net.ssehub.comani.data.IAnalysisQueue;
import net.ssehub.comani.data.IExtractionQueue;
import net.ssehub.comani.extraction.AbstractCommitExtractor;

/**
 * This class provides utility methods for the main infrastructure, like updating the classpath.
 * 
 * @author Christian Kroeher
 *
 */
public class InfrastructureUtilities {
    
    /**
     * The identifier if this class, e.g. for printing messages.
     */
    private static final String ID = "InfrastructureUtilities";
    
    /**
     * Singleton instance of this class.
     */
    private static InfrastructureUtilities instance = new InfrastructureUtilities();
    
    /**
     * The {@link Logger} for printing messages.
     */
    private Logger logger = Logger.getInstance();
    
    /**
     * The directory in which the ComAnI plug-ins, like extractors and analyzers, are located. This is set at runtime
     * by {@link #setPluginsDirectore(File)} and based on the <code>core.plugins_dir</code> property value defined by
     * the user in the configuration file.
     */
    private File pluginsDirectory;
    
    /**
     * Constructs new {@link InfrastructureUtilities}.
     */
    private InfrastructureUtilities() {
        pluginsDirectory = null;
    }
    
    /**
     * Returns the single instance of the {@link InfrastructureUtilities}.
     * 
     * @return the single instance of the {@link InfrastructureUtilities}
     */
    public static synchronized InfrastructureUtilities getInstance() {
        return instance;
    }
    
    /**
     * Adds the given directory as the plug-ins directory in which extractors and analyzers are located. This directory
     * is defined at runtime based on the <code>core.plugins_dir</code> property value defined by the user in
     * the configuration file.
     * 
     * @param directory the ComAnI plug-ins directory as defined by the value of the 
     *        <code>core.plugins_dir</code> property; should never be <code>null</code>
     */
    public void setPluginsDirectory(File directory) {
        pluginsDirectory = directory;
    }
    
    /**
     * Instantiates the desired commit analyzer defined by the given analyzer main class name by using the given
     * analysis properties and queue.
     * 
     * @param analyzerMainClassName the fully qualified main class name of the analyzer to instantiate
     * @param analysisProperties the analysis {@link Properties} defined by the user in the configuration file 
     * @param analysisQueue the {@link IAnalysisQueue} to be used by the analyzer
     * @return an instance of {@link AbstractCommitAnalyzer} or <code>null</code>, if instantiating the desired
     *         analyzer failed
     */
    public AbstractCommitAnalyzer instantiateAnalyzer(String analyzerMainClassName, Properties analysisProperties,
            IAnalysisQueue analysisQueue) {
        AbstractCommitAnalyzer commitAnalyzer = null;
        Class<?> targetClass = getTargetClass(analyzerMainClassName);
        if (targetClass != null) {
            try {                
                Constructor<?> constructor = targetClass.getConstructor(Properties.class, IAnalysisQueue.class);
                commitAnalyzer = (AbstractCommitAnalyzer) constructor.newInstance(analysisProperties,
                        analysisQueue);
            } catch (NoSuchMethodException | SecurityException e) {
                logger.logException(ID, "Retrieving the constructor of class \"" + analyzerMainClassName + "\" failed",
                        e);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
                    | InvocationTargetException e) {
                logger.logException(ID, "Creating new instance of class \"" + analyzerMainClassName + "\" failed", e);
            }
        }
        return commitAnalyzer;
    }
    
    /**
     * Instantiates the desired commit extractor defined by the given extractor main class name by using the given
     * extraction properties and queue.
     * 
     * @param extractorMainClassName the fully qualified main class name of the extractor to instantiate
     * @param extractionProperties the extraction {@link Properties} defined by the user in the configuration file 
     * @param extractionQueue the {@link IExtractionQueue} to be used by the extractor
     * @return an instance of {@link AbstractCommitExtractor} or <code>null</code>, if instantiating the desired
     *         extractor failed
     */
    public AbstractCommitExtractor instantiateExtractor(String extractorMainClassName, Properties extractionProperties,
            IExtractionQueue extractionQueue) {
        AbstractCommitExtractor commitExtractor = null;
        Class<?> targetClass = getTargetClass(extractorMainClassName);
        if (targetClass != null) {
            try {                
                Constructor<?> constructor = targetClass.getConstructor(Properties.class, IExtractionQueue.class);
                commitExtractor = (AbstractCommitExtractor) constructor.newInstance(extractionProperties,
                        extractionQueue);
            } catch (NoSuchMethodException | SecurityException e) {
                logger.logException(ID, "Retrieving the constructor of class \"" + extractorMainClassName + "\" failed",
                        e);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
                    | InvocationTargetException e) {
                logger.logException(ID, "Creating new instance of class \"" + extractorMainClassName + "\" failed", e);
            }
        }
        return commitExtractor;
    }
    
    /**
     * Returns the {@link Class} specified by the given (fully qualified) class name. If {@link #pluginsAdded} is
     * <code>false</code>, it will include the jar-files in the {@link #pluginsDirectory}.
     * 
     * @param targetClassName the fully qualified class name for which the class shall be returned; should never be
     *        <code>null</code> nor <i>empty</i>
     * @return the {@link Class} specified by the given class name or <code>null</code>, if the class could not be found
     */
    private synchronized Class<?> getTargetClass(String targetClassName) {
        /*
         * This way of adding external jars to the classpath is taken from https://stackoverflow.com/a/48042549
         * It does not require the previous classpath update via ClassLoader.getSystemClassLoader() and the cast
         * to URLClassLoader, which is not supported anymore by Java 9 or higher.
         */
        Class<?> targetClass = null;
        try {            
            targetClass = Class.forName(targetClassName, true, new URLClassLoader(getPluginUrls()));
        } catch (ClassNotFoundException e) {
            logger.logException(ID, "Class \"" + targetClassName + "\" could not be found", e);
        }
        return targetClass;
    }
    
    /**
     * Returns the set of {@link URL}s of the jar-files in the {@link #pluginsDirectory}.
     * 
     * @return the set of plug-in {@link URL}s; never <code>null</code>, but may be <i>empty</i>
     */
    private URL[] getPluginUrls() {
        File[] jarFiles = pluginsDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                boolean acceptFile = false;
                if (name.toLowerCase().endsWith(".jar")) {
                    acceptFile = true;
                }
                return acceptFile;
            }
        });
        URL[] pluginUrls = new URL[jarFiles.length];
        for (int i = 0; i < jarFiles.length; i++) {
            try {
                pluginUrls[i] = jarFiles[i].toURI().toURL();
            } catch (MalformedURLException e) {
                logger.logException(ID, "Retrieving URL of plug-in \"" + jarFiles[i].getAbsolutePath() + "\" failed",
                        e);
            }
        }
        return pluginUrls;
    }
}
