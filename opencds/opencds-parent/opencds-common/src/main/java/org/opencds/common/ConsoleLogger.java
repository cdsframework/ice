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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An Adaptor that allows us to log to Console
 * @author 
 *
 */
public class ConsoleLogger implements ILogger {

	
	private Log log = LogFactory.getLog(getClass());
	

	
	public ConsoleLogger(){
		
	}
	
	public void debug(String output){
		log.debug(output);
	}
	
	public void info(String output){
		log.info(output);
	}
	
	public void exception(String output, Throwable t){
		log.error(output,t);
	}


}
