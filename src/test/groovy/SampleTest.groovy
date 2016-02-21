import org.testng.annotations.Test
import groovy.io.FileType

import org.testng.annotations.BeforeSuite

class SampleTest extends GroovyTestCase {

	@BeforeSuite
	public void setUp() throws Exception {
	}

	@Test(description = "Autentificate on backend")
	public void testAuth() {
	}

}