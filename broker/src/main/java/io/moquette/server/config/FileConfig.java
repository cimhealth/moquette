
/*
 * Copyright (c) 2012-2015 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package io.moquette.server.config;

import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.Properties;

/**
 * Configuration that loads file from the file system
 *
 * @author andrea
 */
public class FileConfig implements IConfig {
    private static final Logger LOG = LoggerFactory.getLogger(FileConfig.class);

    private final Properties m_properties;

    public FileConfig(File file) throws IOException {
        m_properties =    new Properties();
        m_properties.load(new FileInputStream(file));
    }

    public FileConfig(URL url) throws IOException {
        m_properties =    new Properties();
        InputStream in = null;
        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setUseCaches(false);
            in = urlConnection.getInputStream();
            m_properties.load(in);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                }
            }
        }
    }

    @Override
    public void setProperty(String name, String value) {
        m_properties.setProperty(name, value);
    }

    @Override
    public String getProperty(String name) {
        return m_properties.getProperty(name);
    }

    @Override
    public String getProperty(String name, String defaultValue) {
        return m_properties.getProperty(name, defaultValue);
    }
}
