package org.cdsframework.ice.supportingdata;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsItem;
import org.cdsframework.ice.servicetmp.Season;
import org.opencds.common.exceptions.ImproperUsageException;


public class LocallyCodedSeasonItem extends LocallyCodedCdsItem {

	private Season season;
	
	private static Log logger = LogFactory.getLog(LocallyCodedSeasonItem.class);
	
	
	protected LocallyCodedSeasonItem(String pCdsSeasonName, Collection<String> pCdsVersions, Season pSeason) 
		throws ImproperUsageException { 
		
		super(pCdsSeasonName, pCdsVersions);
		
		String _METHODNAME = "LocallyCodedSeasonItem(): ";
		
		if (pSeason == null) {
			String lErrStr = "Season parameter not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		this.season = pSeason;
	}


	public Season getSeason() {
		return season;
	}
}
