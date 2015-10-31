package org.opencds.service.evaluate;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.NarrativeDt;
import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.BaseResource;
import ca.uhn.fhir.model.dstu2.resource.CommunicationRequest;
import ca.uhn.fhir.model.dstu2.resource.Provenance;
import ca.uhn.fhir.model.dstu2.resource.Provenance.Agent;
import ca.uhn.fhir.model.dstu2.resource.CommunicationRequest.Payload;
import ca.uhn.fhir.model.primitive.StringDt;

import java.util.List;

public class FhirUtils {

    public static CommunicationRequest createCommunicationRequest(String message) {
        CommunicationRequest comRequest = new CommunicationRequest();
        Payload payload = comRequest.addPayload();
        IDatatype stringType = new StringDt(message);
        payload.setContent(stringType);
        return comRequest;
    }

    public static Provenance createProvenance(String message) {
        Provenance prov = new Provenance();
        NarrativeDt narrative = new NarrativeDt() ;
        narrative.setDiv(message);
        prov.setText(narrative);
        return prov;

    }

    public static Boolean isCodingContains(CodeableConceptDt cc, String system, String code) {
        Boolean result = false;
        List<CodingDt> l = cc.getCoding();
        for (CodingDt codingDt : l) {
            if (codingDt.getSystem().equalsIgnoreCase(system) && codingDt.getCode().equalsIgnoreCase(code)) {
                result = true;
            }
        }
        return result;
    }

    public static QuantityDt getValueQuantity(IDatatype dt) {
        if (dt instanceof QuantityDt) {
            return (QuantityDt) dt;
        } else {
            return null;
        }
    }

    public static boolean hasInputFlag(BaseResource resource) {
        BaseResource baseResource = resource;
        List<ExtensionDt> extensions = baseResource.getAllUndeclaredExtensions();
        for (ExtensionDt extDt : extensions) {
            if (extDt.getValue() instanceof CodeDt) {
                CodeDt code = (CodeDt) extDt.getValue();
                if (extDt.getUrl().equals("http://org.cognitive.cds.invocation.fhir.datanature") && code.getValueAsString().equals("Input")) {
                    return true;
                }
            }
        }
        return false;
    }
    public static ExtensionDt createOutputExtension(){
        ExtensionDt ext = new ExtensionDt();
        ext.setUrl("http://org.cognitive.cds.invocation.fhir.datanature");
        ext.setModifier(true);
        CodeDt cd = new CodeDt();
        cd.setValueAsString("Output");
        ext.setValue(cd);
        return ext;
    }
}
