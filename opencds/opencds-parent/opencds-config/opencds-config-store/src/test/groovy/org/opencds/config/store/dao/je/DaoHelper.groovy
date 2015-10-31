package org.opencds.config.store.dao.je

import org.opencds.config.api.model.Concept
import org.opencds.config.api.model.ConceptDeterminationMethod
import org.opencds.config.api.model.ConceptMapping
import org.opencds.config.api.model.DssOperation
import org.opencds.config.api.model.ExecutionEngine
import org.opencds.config.api.model.KMStatus
import org.opencds.config.api.model.KnowledgeModule
import org.opencds.config.api.model.PluginId
import org.opencds.config.api.model.SemanticSignifier
import org.opencds.config.api.model.SupportMethod
import org.opencds.config.api.model.SupportingData
import org.opencds.config.api.model.impl.CDMIdImpl
import org.opencds.config.api.model.impl.ConceptDeterminationMethodImpl
import org.opencds.config.api.model.impl.ConceptImpl
import org.opencds.config.api.model.impl.ConceptMappingImpl
import org.opencds.config.api.model.impl.ExecutionEngineImpl
import org.opencds.config.api.model.impl.KMIdImpl
import org.opencds.config.api.model.impl.KnowledgeModuleImpl
import org.opencds.config.api.model.impl.PluginIdImpl;
import org.opencds.config.api.model.impl.SSIdImpl
import org.opencds.config.api.model.impl.SecondaryCDMImpl
import org.opencds.config.api.model.impl.SemanticSignifierImpl
import org.opencds.config.api.model.impl.SupportingDataImpl;
import org.opencds.config.api.model.impl.TraitIdImpl
import org.opencds.config.api.model.impl.XSDComputableDefinitionImpl

class DaoHelper {
    static ConceptDeterminationMethod createCDM() {
        return createCDM(1,1)
    }

    static ConceptDeterminationMethod createCDM(int numConceptMappings, int numConceptsPerMapping) {
        return ConceptDeterminationMethodImpl.create(
        CDMIdImpl.create(generateId(), generateId(), generateId()),
        generateId(),
        generateId(),
        new Date(),
        generateId(),
        createCMs(numConceptMappings, numConceptsPerMapping))
    }

    static List<ConceptMapping> createCMs() {
        return createCMs(1,1)
    }

    static List<ConceptMapping> createCMs(int numConceptMappings, int numConceptsPerMapping) {
        int num = numConceptMappings <= 0 ? 1 : numConceptMappings
        return (1..num).inject([]) {a, i ->
            a << ConceptMappingImpl.create(
                    createC(),
                    createCs(numConceptsPerMapping))
        }
    }

    static List<Concept> createCs() {
        return createCs(1)
    }

    static def createCs(int numConceptsPerMapping) {
        int num = numConceptsPerMapping <= 0 ? 1 : numConceptsPerMapping
        return (1..num).inject([]) {a, i ->
            a << createC()
        }
    }

    static createC() {
        return ConceptImpl.create(
        generateId(),
        generateId(),
        generateId(),
        generateId())
    }

    static SemanticSignifier createSemanticSignifier() {
        return SemanticSignifierImpl.create(
        SSIdImpl.create(generateId(), generateId(), generateId()),
        generateId(),
        generateId(),
        XSDComputableDefinitionImpl.create([
            XSDComputableDefinitionImpl.create(generateId(), generateId(), [generateId()], generateId())
        ]),
        generateId(),
        generateId(),
        generateId(),
        generateId(),
        new Date(),
        generateId())
    }

    static KnowledgeModule createKnowledgeModule() {
        return KnowledgeModuleImpl.create(
        KMIdImpl.create(generateId(), generateId(), generateId()),
        KMStatus.APPROVED,
        generateId(),
        SSIdImpl.create(generateId(), generateId(), generateId()),
        CDMIdImpl.create(generateId(), generateId(), generateId()),
        [
            SecondaryCDMImpl.create(CDMIdImpl.create(generateId(), generateId(), generateId()), SupportMethod.ADDITIVE)
        ],
        generateId(),
        generateId(),
        false,
        generateId(),
        [
            TraitIdImpl.create(generateId(), generateId(), generateId())
        ],
        [generatePluginId()],
        [generatePluginId()],
        new Date(),
        generateId()
        )
    }

    static ExecutionEngine createExecutionEngine() {
        return ExecutionEngineImpl.create(generateId(), generateId(), new Date(), generateId(), [
            DssOperation.EVALUATION_EVALUATE
        ])
    }

    static SupportingData createSupportingData() {
        return SupportingDataImpl.create(
        generateId(),
        KMIdImpl.create(generateId(), generateId(), generateId()),
        generateId(),
        generateId(),
        generatePluginId(),
        new Date(),
        generateId())
    }
    
    static PluginId generatePluginId() {
       PluginIdImpl.create(generateId(), generateId(), generateId()) 
    }

    static String generateId() {
        return UUID.randomUUID()
    }
}
