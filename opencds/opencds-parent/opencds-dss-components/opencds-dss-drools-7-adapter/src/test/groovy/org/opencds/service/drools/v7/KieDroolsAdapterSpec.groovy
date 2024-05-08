package org.opencds.service.drools.v7

import org.kie.api.KieBase
import org.kie.api.command.Command
import org.kie.api.runtime.ExecutionResults
import org.opencds.common.structures.EvaluationRequestDataItem
import org.opencds.common.structures.EvaluationRequestKMItem
import org.opencds.config.api.EvaluationContext
import org.opencds.config.api.ExecutionEngineContext
import org.opencds.config.api.model.KMId
import org.opencds.config.api.model.KnowledgeModule
import org.opencds.config.api.model.impl.KMIdImpl
import org.opencds.vmr.v1_0.internal.AdverseEvent
import spock.lang.Specification

class KieDroolsAdapterSpec extends Specification {
    private static final String BOUNCE_RULE = 'src/test/resources/bounce.drl'
    private KieDroolsAdapter adapter
    private KieBase kbase
    KMId bounceId

    def setup() {
        DroolsKnowledgeLoader loader = new DroolsKnowledgeLoader()
        adapter = new KieDroolsAdapter()
        bounceId = KMIdImpl.create('OPENCDS', 'BOUNCE', '1.0')

        InputStream bounce = new FileInputStream(BOUNCE_RULE)
        String drl = 'DRL'
        KnowledgeModule km = Mock()
        1 * km.getPackageType() >> drl
        2 * km.getKMId() >> bounceId
        kbase = loader.loadKnowledgePackage(km, k -> bounce)
    }

    def 'test bounce execution'() {
        given:
        ExecutionEngineContext<List<Command<?>>, ExecutionResults> context = new DroolsExecutionEngineContext()
        Map<Class<?>, List<?>> allFactLists = [(AdverseEvent.class): [new AdverseEvent()]]
        EvaluationRequestDataItem dataItem = new EvaluationRequestDataItem()
        dataItem.setFocalPersonId('focalperson')
        dataItem.setEvalTime(new Date())
        dataItem.setServerBaseUri(new URI('http://localhost.local'))

        EvaluationRequestKMItem kmItem = new EvaluationRequestKMItem(bounceId.toString(), dataItem, allFactLists)

        EvaluationContext evaluationContext = EvaluationContext.create(kmItem, null)
        context.setEvaluationContext(evaluationContext)

        and:
        0 * _._

        when:
        def result = adapter.execute(kbase, context)


        then:
        notThrown(Exception)
        result.getResults().get('AdverseEvent')
        result.getResults().get('AdverseEvent')*.each {it instanceof AdverseEvent }
        result.getResults().get('AdverseEvent').size() == 1
        ((AdverseEvent)result.getResults().get('AdverseEvent').get(0)).toBeReturned
    }
}
