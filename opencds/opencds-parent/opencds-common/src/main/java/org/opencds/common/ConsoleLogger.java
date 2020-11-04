/**
 * Copyright 2011 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.common;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An Adaptor that allows us to log to Console
 * @author 
 *
 */
public class ConsoleLogger implements ILogger {

	private static final Logger logger = LogManager.getLogger();
	
	public ConsoleLogger(){
		
	}
	
	public void debug(String output){
		logger.debug(output);
	}
	
	public void info(String output){
		logger.info(output);
	}
	
	public void exception(String output, Throwable t){
		logger.error(output,t);
	}


}
