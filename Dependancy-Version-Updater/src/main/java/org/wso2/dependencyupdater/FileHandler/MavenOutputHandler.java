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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * TODO:Class level comment
 */
public class MavenOutputHandler extends PrintStreamHandler {
    private final PrintStream tee;
    private final boolean alwaysFlush;

    public MavenOutputHandler(File tee) throws FileNotFoundException {
        this(true, new PrintStream(new FileOutputStream(tee, true)));
    }

    public MavenOutputHandler(boolean alwaysFlush, PrintStream tee) {
        this.tee = tee;
        this.alwaysFlush = alwaysFlush;
    }

    @Override
    public void consumeLine(String line) {
        super.consumeLine(line);
        tee.println(line);
        if (alwaysFlush) {
            tee.flush();
        }
    }
}