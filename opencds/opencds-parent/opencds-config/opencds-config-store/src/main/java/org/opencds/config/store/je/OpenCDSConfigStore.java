/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.config.store.je;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.store.je.evolve.StoreConversion;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.evolve.IncompatibleClassException;

public class OpenCDSConfigStore {
	private static final Log log = LogFactory.getLog(OpenCDSConfigStore.class);
	private static final String STORE_NAME = "OpenCDSConfigStore";

	private boolean initialized;

	private final File storeLocation;
	private final boolean readOnly;
	private Environment env;
	private EntityStore store;

	/**
	 * By default, readOnly is false.
	 * 
	 * @param path
	 */
	public OpenCDSConfigStore(File path) {
		this.storeLocation = path;
		this.readOnly = false;
		init();
	}

	public OpenCDSConfigStore(String path, boolean readOnly) {
		this.storeLocation = Paths.get(path).toFile();
		this.readOnly = readOnly;
		init();
	}

	private void init() {
		if (!initialized) {
			try {
				EnvironmentConfig envConfig = new EnvironmentConfig();
				StoreConfig storeConfig = new StoreConfig();

				// mutations here, when model evolves
				// see the documentation at the following URLs:
				// https://docs.oracle.com/cd/E17277_02/html/java/com/sleepycat/persist/model/Entity.html
				// https://docs.oracle.com/cd/E17277_02/html/java/com/sleepycat/persist/evolve/Mutations.html

				envConfig.setAllowCreate(!readOnly);
				storeConfig.setAllowCreate(!readOnly);

				env = new Environment(storeLocation, envConfig);
				store = new EntityStore(env, STORE_NAME, storeConfig);
				initialized = true;
			} catch (IncompatibleClassException e) {
				// close the store and env...
				close();
				// now we attempt a store conversion
				initConversion();
			} catch (DatabaseException e) {
				log.error("Unable to create/open environment and store: name= " + STORE_NAME + ", location= "
						+ storeLocation.getAbsolutePath(), e);
				throw new OpenCDSRuntimeException(e);
			}
		} else {
			log.warn("");
		}
	}

	private void initConversion() {
		log.debug("Converting store to new format");

		try {
			StoreConversion conv = new StoreConversion();
			store = conv.convert(storeLocation, STORE_NAME, readOnly);
			env = store.getEnvironment();
			initialized = true;
		} catch (DatabaseException e) {
			log.error("Unable to convert data store: name= " + STORE_NAME + ", location= "
					+ storeLocation.getAbsolutePath(), e);
			throw new OpenCDSRuntimeException(e);
		}
	}

	public EntityStore getEntityStore() {
		return store;
	}

	public void close() {
		try {
			if (store != null) {
				store.close();
			}
			if (env != null) {
				env.close();
			}
		} catch (DatabaseException e) {
			log.error("Unable to close store: name= " + STORE_NAME + ", location= " + storeLocation.getAbsolutePath());
		}
	}

}
