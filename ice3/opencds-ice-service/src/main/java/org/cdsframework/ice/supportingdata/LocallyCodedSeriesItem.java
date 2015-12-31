package org.cdsframework.ice.supportingdata;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsItem;
import org.cdsframework.ice.service.SeriesRules;
import org.opencds.common.exceptions.ImproperUsageException;

public class LocallyCodedSeriesItem extends LocallyCodedCdsItem {

	private SeriesRules seriesRules;
	
	private static Log logger = LogFactory.getLog(LocallyCodedSeriesItem.class);
	
	
	protected LocallyCodedSeriesItem(String pCdsSeriesCode, Collection<String> pCdsVersions, SeriesRules pSeriesRules) 
		throws ImproperUsageException { 

		super(pCdsSeriesCode, pCdsVersions);

		String _METHODNAME = "LocallyCodedSeriesItem(): ";

		if (pSeriesRules == null) {
			String lErrStr = "Series parameter not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}

		this.seriesRules = pSeriesRules;
	}


	public SeriesRules getSeriesRules() {
		return seriesRules;
	}


	@Override
	public String toString() {
		return "LocallyCodedSeriesItem [getCdsItemName()=" + getCdsItemName() + ", getCdsVersions()=" + getCdsVersions() + 
			"seriesRules=" + this.seriesRules + "]";
	}
	
	

}
