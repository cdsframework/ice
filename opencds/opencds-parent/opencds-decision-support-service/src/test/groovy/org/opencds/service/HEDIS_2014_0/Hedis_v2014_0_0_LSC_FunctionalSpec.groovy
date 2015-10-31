package org.opencds.service.HEDIS_2014_0;

import org.opencds.service.util.OpencdsClient
import org.opencds.service.util.VMRUtil

import spock.lang.Specification
import spock.lang.Unroll

public class Hedis_v2014_0_0_LSC_FunctionalSpec extends Specification {
	private static final String LSC0000 = "src/test/resources/samples/hedis-lsc/LSC0000.xml"
	private static final String LSC0001 = "src/test/resources/samples/hedis-lsc/LSC0001.xml"
	private static final String LSC0002 = "src/test/resources/samples/hedis-lsc/LSC0002.xml"
	private static final String LSC0003 = "src/test/resources/samples/hedis-lsc/LSC0003.xml"
	private static final String LSC0004 = "src/test/resources/samples/hedis-lsc/LSC0004.xml"
	private static final String LSC0005 = "src/test/resources/samples/hedis-lsc/LSC0005.xml"
	private static final String LSC0006 = "src/test/resources/samples/hedis-lsc/LSC0006.xml"
	private static final String LSC0007 = "src/test/resources/samples/hedis-lsc/LSC0007.xml"
	private static final String LSC0008 = "src/test/resources/samples/hedis-lsc/LSC0008.xml"

	@Unroll
	def "test HEDIS LSCtest"() {
		when:
		def input = new File(vmr).text
		def params = [
			kmEvaluationRequest:[scopingEntityId: 'edu.utah', businessId: 'HEDIS_LSC', version: '2014.0.0'],
			specifiedTime: '2012-01-01'
		]
		def responsePayload = OpencdsClient.sendEvaluateAtSpecifiedTimeMessage(params, input)
		println responsePayload

		then:
		def data = new XmlSlurper().parseText(responsePayload)
		def results = VMRUtil.getResults(data, '\\|')
		assertions.size() == results.assertions.size()
		assertions.each {entry ->
			assert results.assertions.containsKey(entry.key)
			if (entry.value) {
				assert results.assertions.get(entry.key) == entry.value
			}
		}
		numerator == results.measure.numerator
		denominator == results.measure.denominator

		where:
		vmr | assertions | denominator | numerator
		LSC0000 | [C569: '', C529: '',reject: '', wrapper: ''] | '0' | '0'	// no DOB
		LSC0001 | [C3285: '',C54: '',DOB:'', SecondBirthday:'', numNotMet: '', wrapper : ''] | '1' | '0' //last day eligible
		LSC0002 | [DOB:'', SecondBirthday:'',denomNotMet: '', wrapper : ''] | '0' | '0'// too young
		LSC0003 | [C3285: '',C54: '',DOB:'', SecondBirthday:'', numNotMet: '', wrapper : ''] | '1' | '0' // testing boundary - earliest DOB eligible
		LSC0004 | [DOB:'', SecondBirthday:'',denomNotMet: '', wrapper : ''] | '0' | '0' // too old
		LSC0005 | [C3285: '', C54: '',DOB:'', SecondBirthday:'', C2701:'', C539:'' ,numMet: '', wrapper : ''] | '1' | '1' //cpt test within timeframe
		LSC0006 | [C3285: '',C54: '',DOB:'', SecondBirthday:'', numNotMet: '', wrapper : ''] | '1' | '0' //CPT test but day after 2nd birthday
		LSC0007 | [C3285: '', C54: '',DOB:'', SecondBirthday:'', C2701:'', C539:'' ,numMet: '', wrapper : ''] | '1' | '1' //testing the LOINC lab test in correct timeframe
		LSC0008 | [C3285: '',C54: '',DOB:'', SecondBirthday:'', numNotMet: '', wrapper : ''] | '1' | '0' //LOINC lab but before DOB
	}
}