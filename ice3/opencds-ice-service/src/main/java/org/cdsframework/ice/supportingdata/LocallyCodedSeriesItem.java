/**
 * Copyright (C) 2023 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 *
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */
 
package org.cdsframework.ice.supportingdata;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsItem;
import org.cdsframework.ice.service.SeriesRules;
import org.opencds.common.exceptions.ImproperUsageException;

public class LocallyCodedSeriesItem extends LocallyCodedCdsItem {

	private SeriesRules seriesRules;
	
	private static final Logger logger = LogManager.getLogger();
	
	
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
		return "LocallyCodedSeriesItem [getCdsItemName()=" + getCdsItemName() + "; getCdsVersions()=" + getCdsVersions() + "; seriesRules=" + this.seriesRules + "]";
	}
	
}
