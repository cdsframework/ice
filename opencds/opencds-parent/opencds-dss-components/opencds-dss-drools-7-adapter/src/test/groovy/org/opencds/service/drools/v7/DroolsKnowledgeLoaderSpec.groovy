package org.opencds.service.drools.v7

import org.kie.api.KieBase
import org.opencds.config.api.model.KMId
import org.opencds.config.api.model.KnowledgeModule
import org.opencds.config.api.model.impl.KMIdImpl
import org.opencds.config.api.service.KnowledgePackageService
import spock.lang.Specification

class DroolsKnowledgeLoaderSpec extends Specification {
    private static final String BOUNCE_RULE = 'src/test/resources/bounce.drl'
    private DroolsKnowledgeLoader loader

    def setup() {
        loader = new DroolsKnowledgeLoader()
    }

    def 'test loading the bounce rule'() {
        given:
        InputStream bounce = new FileInputStream(BOUNCE_RULE)
        String drl = 'DRL'
        KMId bounceId = KMIdImpl.create('OPENCDS', 'BOUNCE', '1.0')
        KnowledgePackageService kps = Mock()
        KnowledgeModule km = Mock()
        KieBase kbase

        and:
        1 * km.getPackageType() >> drl
        2 * km.getKMId() >> bounceId
        0 * _._

        when:
        kbase = loader.loadKnowledgePackage(km, k -> bounce)

        then:
        notThrown(Exception)
        kbase
        kbase.pkgs.containsKey('Bounce_v7')
        kbase.pkgs.get('Bounce_v7')
        kbase.pkgs.containsKey('org.opencds.vmr.v1_0.internal')
        kbase.pkgs.get('org.opencds.vmr.v1_0.internal')
    }

    def 'test loading null package input stream'() {
        given:
        InputStream bounce = null
        String drl = 'DRL'
        KMId bounceId = KMIdImpl.create('OPENCDS', 'BOUNCE', '1.0')
        KnowledgePackageService kps = Mock()
        KnowledgeModule km = Mock()
        KieBase kbase

        and:
        1 * km.getPackageType() >> drl
        1 * km.getPackageId() >> 'bounce.drl'
        0 * _._

        when:
        kbase = loader.loadKnowledgePackage(km, k -> bounce)

        then:
        Exception e = thrown(Exception)
        e.getMessage() == 'KnowledgeModule package cannot be found (possibly due to misconfiguration?); packageId= bounce.drl, packageType= DRL'
    }
}
