/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.wso2.dependencyupdater.FileHandler;

import org.apache.maven.shared.invoker.PrintStreamHandler;
import org.wso2.dependencyupdater.Constants;
import sun.nio.cs.StandardCharsets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * This method implements PrintStreamHandler class to handle maven build outputs
 */
public class MavenOutputHandler extends PrintStreamHandler {

    private final PrintStream printStream;
    private final boolean alwaysFlush;

    /**
     * This method append the maven output to a given file
     *
     * @param file File object
     * @throws FileNotFoundException Indicates file is not present in the location
     */
    public MavenOutputHandler(File file) throws FileNotFoundException, UnsupportedEncodingException {

        this(new PrintStream(new FileOutputStream(file, true),true,Constants.UTF_8_CHARSET_NAME));
    }

    /**
     * Constructor for the class
     *
     * @param printStream Print stream to output Maven outputs
     */
    private MavenOutputHandler(PrintStream printStream) {

        this.printStream = printStream;
        this.alwaysFlush = true;
    }

    /**
     * Determines how each output line is handled
     *
     * @param line Text line
     */
    @Override
    public void consumeLine(String line) {

        super.consumeLine(line);
        printStream.println(line);
        if (alwaysFlush) {
            printStream.flush();
        }
    }
}