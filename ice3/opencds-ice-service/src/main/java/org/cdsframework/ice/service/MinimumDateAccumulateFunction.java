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

package org.cdsframework.ice.service;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kie.api.runtime.rule.AccumulateFunction;

public class MinimumDateAccumulateFunction implements AccumulateFunction<MinimumDateAccumulateFunction.DateAccumulationData> {

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub

	}
	
	 public static class DateAccumulationData implements Externalizable {
		 public List<Date> list = new ArrayList<Date>();
	     
		 public DateAccumulationData() {}

		 public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {		 
		 }

		 public void writeExternal(ObjectOutput out) throws IOException {
		 }
	 }


	@Override
	public void accumulate(DateAccumulationData context, Object pDateAccumulationElement) {
		if (context != null && pDateAccumulationElement != null && pDateAccumulationElement instanceof Date) {
			context.list.add((Date) pDateAccumulationElement);
		}
	}

	@Override
	public DateAccumulationData createContext() {
		return new DateAccumulationData();
	}

	@Override
	public void init(DateAccumulationData context) throws Exception {
		if (context != null) {
			context.list.clear();
		}
	}

	
	@Override
	public void reverse(DateAccumulationData context, Object value) throws Exception {
		if (context != null && value instanceof Date) {
			context.list.remove((Date) value );
		}
	}

	@Override
	public boolean supportsReverse() {
		return true;
	}
	
	public Object getResult(DateAccumulationData context) throws Exception {
		if (context == null || context.list == null) {
			return null;
		}
		Date minDate = null;
		for (Date d : context.list) {
			if (minDate == null) {
				minDate = d;
			}
			else if (d != null && d.before(minDate)) {
				minDate = d;
			}
		}

		return minDate;
	}

	public Class<?> getResultType() {
		return Date.class;
	}

}
