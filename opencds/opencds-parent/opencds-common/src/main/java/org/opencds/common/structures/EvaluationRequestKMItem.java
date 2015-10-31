/**
 * 
 */
package org.opencds.common.structures;

import java.util.List;
import java.util.Map;


/**
 * @author David Shields
 * 
 * @date
 *
 */
public class EvaluationRequestKMItem {
	private final String 				requestedKmId;
	private final EvaluationRequestDataItem	evaluationRequestDataItem;
	private final Object			 	cdsInput;
    private final Map<Class<?>, List<?>> allFactLists;

	public EvaluationRequestKMItem(String requestedKmId, EvaluationRequestDataItem evaluationRequestDataItem, Object cdsInput, Map<Class<?>, List<?>> allFactLists) {
        this.requestedKmId = requestedKmId;
        this.evaluationRequestDataItem = evaluationRequestDataItem;
        this.cdsInput = cdsInput;
        this.allFactLists = allFactLists;
    }

    /**
	 * @return the requestedKmId
	 */
	public String getRequestedKmId() {
		return requestedKmId;
	}

	/**
	 * @return the dssRequestDataItem
	 */
	public EvaluationRequestDataItem getEvaluationRequestDataItem() {
		return evaluationRequestDataItem;
	}

	/**
	 * @return the cdsInput
	 * NOTE: the object returned must be cast to JAXBElement<org.opencds.vmr.v1_0.schema.CDSInput>
	 */
	public Object getCdsInput() {
		return cdsInput;
	}

    public Map<Class<?>, List<?>> getAllFactLists() {
        return allFactLists;
    }


}
