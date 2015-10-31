package org.cdsframework.ice.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.service.configurations.ScheduleImpl;
import org.drools.KnowledgeBase;
import org.opencds.common.exceptions.ImproperUsageException;

public class ICEKnowledgeBase {

	private KnowledgeBase rulesEngineKnowledgeBase;
	private Schedule schedule;

	private static Log logger = LogFactory.getLog(ScheduleImpl.class);
	

	public ICEKnowledgeBase(KnowledgeBase pRulesEngineKnowledgeBase, Schedule pSchedule) 
		throws ImproperUsageException{
		
		String _METHODNAME = "ICEKnowledgeBase(): ";
		if (pRulesEngineKnowledgeBase == null || pSchedule == null) {
			String lErrStr = "ICE knowledge base not properly initialzed: invalid rules engine knowledge base and/or ICE schedule/supporting data";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		rulesEngineKnowledgeBase = pRulesEngineKnowledgeBase;
		schedule = pSchedule;
	}

	public KnowledgeBase getRulesEngineKnowledgeBase() {
		return rulesEngineKnowledgeBase;
	}

	public Schedule getSchedule() {
		return schedule;
	}

}
