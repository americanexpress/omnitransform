/*
 * Copyright 2020 American Express Travel Related Services Company, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.americanexpress.omnitransform.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

/**
 * @author Anant Athale @anant.athale@aexp.com
 *
 */

public class OmniTransformException extends Exception {
	
	private String sourceFile;

	private static final long serialVersionUID = 1L;

    private static Logger logger = LogManager.getLogger(OmniTransformException.class);

    public OmniTransformException() {
        super();
        log(this.getMessage());
    }
    
    private void logStackTrace(Throwable cause) {
        StringWriter stack = new StringWriter();
        cause.printStackTrace(new PrintWriter(stack));
        logger.error(stack.toString());
    }

    private void log(String errorMessage) {
        logger.error("ERROR | exception=" + this.getClass().getSimpleName() + ", " + errorMessage);
    }
    
    public OmniTransformException(String errorCode,String sourceFile, Throwable cause) {
        super(errorCode, cause);
        this.sourceFile = sourceFile;
        logger.error("{} occured at {}",  errorCode, sourceFile);
        logStackTrace(cause);
    }
	
	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
}
