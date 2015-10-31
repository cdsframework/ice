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

package org.opencds.common.utilities;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * This utility has been updated to support fast generation of ids when building concept lists in a multithreaded environment.
 * 
 * The original intent was to return a UUID, but the performance is suboptimal due to synchronicity of the generator.
 * 
 * We use an AtomicInteger to guarantee uniqueness.
 *
 * @author David Shields
 * @author Phillip Warner
 */
public class MiscUtility {
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>(1000000);
    private static final ExecutorService es = Executors.newFixedThreadPool(10);
    private static final AtomicInteger key = new AtomicInteger(0);
    
    static {
        for (int i = 0; i < 50; i++) {
            es.submit(new QueueFiller(queue));
        }
    }
    
	/**
	 * Generate a UUID using default Java UUID generator.  
	 * 
	 * Will consider generating UUID using Johann Burkard's algorithm http://johannburkard.de/software/uuid/
	 * in the future as this algorithm is an order of magnitude faster than java.util.UUID.
	 * 
	 * However, this is not done now due to some Maven/POM issues with using Burkard's algorithm.
	 * 
	 * It is converted to String to be useable in our standard String ID classes
	 * 
	 * @return
	 */
	public static String getIDAsString() {
		//return new com.eaio.uuid.UUID().toString(); // using default Java UUID generator for now
		// TODO: consider switching to above UUID generator for performance
//		return (java.util.UUID.randomUUID()).toString();
	    try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	    return UUID.randomUUID().toString();
	}
	
	public void shutdown() {
	    es.shutdownNow();
	}
	
	private static class QueueFiller implements Runnable {

	    private final BlockingQueue<String> queue;

        public QueueFiller(BlockingQueue<String> queue) {
	        this.queue = queue;
	    }
	    
        @Override
        public void run() {
            try {
                while (true) {
//                    queue.put(UUID.randomUUID().toString());
                    int val = key.getAndIncrement();
                    queue.put(Integer.toString(val));
                }
            } catch (InterruptedException e) {
                //
            }
        }
	    
	}

}
