package org.cdsframework.ice.util;

import org.opencds.vmr.v1_0.internal.EvalTime;
import org.opencds.vmr.v1_0.internal.ObservationResult;
import org.opencds.vmr.v1_0.internal.ObservationValue;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

public abstract class VmrUtil {
    private static final String OPENCDS_ID_OID = "2.16.840.1.113883.3.795.5.1.1";
    private static final String OPENCDS_CS_OID = "2.16.840.1.113883.3.795.12.1.1";
    private static final String OPENCDS_CS_NAME = "OpenCDS concepts";
    private static final String CARAT = "^";

    public static IVLDate createObsTime(EvalTime evalTime) {
        IVLDate obsTime = new IVLDate();
        obsTime.setLow(evalTime.getEvalTimeValue());
        obsTime.setHigh(evalTime.getEvalTimeValue());
        return obsTime;
    }

    public static ObservationResult createObservationResult(String evaluatedPersonId, IVLDate obsTime,
            String focalPersonId, String observationType, String obsTypeCode, String obsTypeDisplayName, String observationValue) {
        ObservationResult obsResult = new ObservationResult();
        obsResult.setId(OPENCDS_ID_OID + CARAT + observationType);
        obsResult.setEvaluatedPersonId(evaluatedPersonId);
        obsResult.setObservationEventTime(obsTime);
        obsResult.setSubjectIsFocalPerson(evaluatedPersonId.equals(focalPersonId));

        obsResult.setObservationFocus(createObsFocus(obsTypeCode, obsTypeDisplayName));
        obsResult.setClinicalStatementToBeRoot(true);
        obsResult.setToBeReturned(true);

        ObservationValue obsValue = new ObservationValue();
        obsValue.setText(observationValue);

        obsResult.setObservationValue(obsValue);
        return obsResult;
    }

    public static CD createObsFocus(String obsTypeCode, String obsTypeDisplayName) {
        CD obsFocus = new CD();
        obsFocus.setCodeSystem(OPENCDS_CS_OID);
        obsFocus.setCodeSystemName(OPENCDS_CS_NAME);
        obsFocus.setCode(obsTypeCode);
        obsFocus.setDisplayName(obsTypeDisplayName);
        return obsFocus;
    }

}
