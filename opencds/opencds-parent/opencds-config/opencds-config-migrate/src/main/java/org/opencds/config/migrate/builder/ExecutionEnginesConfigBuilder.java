package org.opencds.config.migrate.builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.common.cache.OpencdsCache;
import org.opencds.common.xml.XmlEntity;
import org.opencds.config.api.model.DssOperation;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.impl.ExecutionEngineImpl;
import org.opencds.config.migrate.ConfigResourceType;
import org.opencds.config.migrate.OpencdsBaseConfig;
import org.opencds.config.migrate.cache.ConfigCacheRegion;
import org.opencds.config.migrate.utilities.ConfigResourceUtility;
import org.opencds.config.migrate.utilities.XmlUtility;

public class ExecutionEnginesConfigBuilder {
	private static final Logger log = LogManager.getLogger();
	
	private static final Map<String, DssOperation> operations = new HashMap<>();
	static {
	    operations.put("doesEvaluate", DssOperation.EVALUATION_EVALUATE);
	    operations.put("doesEvaluateAtSpecifiedTime", DssOperation.EVALUATION_EVALUATE_AT_SPECIFIED_TIME);
	    operations.put("doesEvaluateIteratively", DssOperation.EVALUATION_EVALUATE_ITERATIVELY);
	    operations.put("doesEvaluateIterativelyAtSpecifiedTime", DssOperation.EVALUATION_EVALUATE_ITERATIVELY_AT_SPECIFIED_TIME);
	}
	
	private XmlUtility xmlUtility = new XmlUtility();

	private ConfigResourceUtility configResourceUtility = new ConfigResourceUtility();

	public void loadExecutionEngines(OpencdsBaseConfig opencdsBaseConfig, OpencdsCache cache) {
		// read executionEngines resource
		XmlEntity executionEnginesRootEntity = xmlUtility
				.unmarshalXml(configResourceUtility
						.getResourceAsString(
								opencdsBaseConfig,
								ConfigResourceType.EXECUTION_ENGINES));
		
		// load executionEngine array
		List<XmlEntity>  executionEngineList = executionEnginesRootEntity.getChildrenWithLabel("executionEngine");
		List<ExecutionEngine> execEngines = new ArrayList<>();
		for (XmlEntity executionEngine : executionEngineList) {
			// load kmId supported operations 
			String engineName = executionEngine.getAttributeValue("name");
			String logEngine = engineName;
			List<DssOperation> supportedDSSOperations = new ArrayList<>(); 
			Set<String> supportedOps = executionEngine.getAttributeLabels();
			for (String supportedOp : supportedOps) {
				String so = executionEngine.getAttributeValue(supportedOp);
				if ("true".equals(so)) {
				    supportedDSSOperations.add(operations.get(supportedOp));
					logEngine = logEngine + ", " + supportedOp;
				}
			}
			log.debug("Execution Engine: " + logEngine + ", name: " + engineName );
			execEngines.add(ExecutionEngineImpl.create(engineName, "Execution Engine " + engineName, new Date(), System.getenv("USER"), supportedDSSOperations));
//			myExecutionEngineToSupportedOperationsMap.put( engineName, supportedDSSOperations );
		}
		cache.put(ConfigCacheRegion.METADATA, ConfigResourceType.EXECUTION_ENGINES, execEngines);
	}
	
}
