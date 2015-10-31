package org.opencds.config.store.je;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

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

                envConfig.setAllowCreate(!readOnly);
                storeConfig.setAllowCreate(!readOnly);

                env = new Environment(storeLocation, envConfig);
                store = new EntityStore(env, STORE_NAME, storeConfig);
                initialized = true;
            } catch (DatabaseException e) {
                log.error("Unable to create/open environment and store: name= " + STORE_NAME + ", location= " + storeLocation.getAbsolutePath(), e);
            }
        } else {
            log.warn("");
        }
    }
    
    public EntityStore getEntityStore() {
        return store;
    }
    
    public void close() {
        try {
            store.close();
            env.close();
        } catch (DatabaseException e) {
            log.error("Unable to close store: name= " + STORE_NAME + ", location= " + storeLocation.getAbsolutePath());
        }
    }

}
