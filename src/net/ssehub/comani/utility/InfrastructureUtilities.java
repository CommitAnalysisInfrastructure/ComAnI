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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import net.ssehub.comani.core.Logger;
import net.ssehub.comani.core.Logger.MessageType;

/**
 * This class provides utility methods for the main infrastructure, like updating the classpath.
 * 
 * @author Christian Kröher
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
     * Constructs new {@link InfrastructureUtilities}.
     */
    private InfrastructureUtilities() {}
    
    /**
     * Returns the single instance of the {@link InfrastructureUtilities}.
     * 
     * @return the single instance of the {@link InfrastructureUtilities}
     */
    public static synchronized InfrastructureUtilities getInstance() {
        return instance;
    }
    
    /**
     * Updates the <i>Classpath</i> in terms of adding all <i>*.jar</i>-files in the given directory to it. These files
     * represent external components, which should be used for commit extraction and analysis (defined by the properties
     * file).
     * 
     * @param pluginsDirectory the directory containing the external plug-ins; should never be <code>null</code>
     * @return <code>true</code> if adding the <i>*.jar</i>-files to the <i>Classpath</i> was successful;
     *         <code>false</code> otherwise
     */
    public boolean updateClasspath(File pluginsDirectory) {
        boolean additionSuccessful = false;
        if (pluginsDirectory.exists() && pluginsDirectory.isDirectory()) {
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
            if (jarFiles != null && jarFiles.length > 0) {
                StringBuilder jarFileNames = new StringBuilder();
                for (File jarFile : jarFiles) {
                    try {
                        URL url = jarFile.toURI().toURL();
                        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                        method.setAccessible(true);
                        method.invoke(classLoader, url);
                        additionSuccessful = true;
                        jarFileNames.append("\n" + jarFile.getName());
                    } catch (NoSuchMethodException | SecurityException | MalformedURLException | IllegalAccessException
                            | IllegalArgumentException | InvocationTargetException e) {
                        additionSuccessful = false;
                        logger.logException(ID, "Adding plug-in \"" + jarFile.getName() + "\" to classpath failed", e);
                    }
                }
                logger.log(ID, "Adding plug-ins to classpath successful", "Plug-ins added:" + jarFileNames.toString(),
                        MessageType.INFO);
            } else {
                logger.log(ID, "No plug-ins available in specified plug-ins directory",
                        "Specified directory: \"" + pluginsDirectory.getAbsolutePath() + "\"",
                        MessageType.ERROR);
            }
        } else {
            logger.log(ID, "The plug-ins directory does not exist or is not a directory",
                    "Specified directory: \"" + pluginsDirectory.getAbsolutePath() + "\"", MessageType.ERROR);
        }
        return additionSuccessful;
    }
}
