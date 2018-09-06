@Grab('org.apache.camel:camel-core:2.22.0')
@Grab('org.apache.camel:camel-quartz2:2.22.0')
@Grab('org.slf4j:slf4j-simple:1.7.18')
@Grab('io.rhiot:camel-pi4j:0.1.4')
import org.apache.camel.*
import org.apache.camel.impl.*
import org.apache.camel.builder.*
import org.apache.camel.component.slack.*
import org.apache.camel.util.jndi.JndiContext
import org.apache.camel.routepolicy.quartz2.CronScheduledRoutePolicy

def camelContext = new DefaultCamelContext()

camelContext.addRoutes(new RouteBuilder() {
    def void configure() {
        from('quartz2://quartz2Test?cron=0+0/1+*+*+*+?').routeId("irrigation").log('A ${exchangeId}')
      	.to("pi4j-gpio://GPIO_04?mode=DIGITAL_OUTPUT&state=HIGH")
      //&action=TOGGLE")
        .delay(3000)
        .log('B ${exchangeId}')
	.to("pi4j-gpio://GPIO_04?mode=DIGITAL_OUTPUT&state=LOW")
        .to("mock:success")
    }
})
camelContext.start()

addShutdownHook{ camelContext.stop() }
synchronized(this){ this.wait() }
