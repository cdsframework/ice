package org.cdsframework.ice.supportingdata;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsItem;
import org.cdsframework.ice.service.Season;
import org.opencds.common.exceptions.ImproperUsageException;


public class LocallyCodedSeasonItem extends LocallyCodedCdsItem {

	/**
	 * 
		<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
		<ns2:iceSeasonSpecificationFile xmlns:ns2="org.cdsframework.util.support.data.ice.season">
		    <ns2:seasonId>ca689767c72a9c07c79514d7aef74624</ns2:seasonId>
		    <ns2:name>2013-2014 Influenza Season</ns2:name>
		    <ns2:code>20132014InfluenzaSeason</ns2:code>
		    <ns2:defaultSeason>false</ns2:defaultSeason>
		    <ns2:startDate>2013-08-01-04:00</ns2:startDate>
		    <ns2:endDate>2014-06-30-04:00</ns2:endDate>
		    <ns2:vaccineGroup code="800" codeSystem="2.16.840.1.113883.3.795.12.100.1" codeSystemName="ICE Vaccine Group" displayName="Influenza"/>
		    <ns2:cdsVersion>org.nyc.cir^ICE^1.0.0</ns2:cdsVersion>
		</ns2:iceSeasonSpecificationFile>
	 *
	 */
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

	@Override
	public String toString() {
		
		String toStr = "LocallyCodedSeasonItem [getCdsItemName()=" + getCdsItemName() + "; getSeason()=" + this.season + "; getCdsVersions()=" + getCdsVersions() + "]";
		return toStr;
	}
	
	
}
