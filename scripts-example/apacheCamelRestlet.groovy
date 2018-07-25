@Grab('org.apache.camel:camel-core:2.22.0')
@Grab('org.apache.camel:camel-servlet:2.22.0')
@Grab('org.apache.camel:camel-restlet:2.22.0')
@Grab('org.apache.camel:camel-groovy:2.22.0')
@Grab('org.slf4j:slf4j-simple:1.7.25')
import org.apache.camel.*
import org.apache.camel.impl.*
import org.apache.camel.builder.*
import org.apache.camel.component.slack.*
import org.apache.camel.model.dataformat.*
import org.apache.camel.util.jndi.JndiContext
import java.util.regex.Matcher;
import java.util.regex.Pattern;

def camelContext = new DefaultCamelContext()

camelContext.addRoutes(new RouteBuilder() {
    def void configure() {
    	restConfiguration().component("restlet").host("localhost").port("8080")
	from('rest:get:hello/{me}').transform().simple('Bye ${header.me}')
    }
})

camelContext.start()

addShutdownHook{ camelContext.stop() }
synchronized(this){ this.wait() }
