@Grab('org.apache.camel:camel-core:2.16.2')
@Grab('org.apache.camel:camel-slack:2.16.2')
@Grab('org.apache.camel:camel-groovy:2.16.2')
@Grab('org.slf4j:slf4j-simple:1.7.18')
import org.apache.camel.*
import org.apache.camel.impl.*
import org.apache.camel.builder.*
import org.apache.camel.component.slack.*
import org.apache.camel.util.jndi.JndiContext

def camelContext = new DefaultCamelContext()
def slack = "slack://mySlack?channel=#tetra-delivery&iconEmoji=:ghost:&username=ghost&webhookUrl=https://hooks.slack.com/services/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"
//def jndiContext = new JndiContext();
//jndiContext.bind("sslack", new SlackComponent())

camelContext.addRoutes(new RouteBuilder() {
    def void configure() {
        from("timer://logOutputTimer?period=3000")
            //.setBody(append("Message"))
            //.setBody(constant("the value"))
            .transform()
            .simple('new ${date:now:yyyyMMdd} ${in.header.number} range 100..199')
            //.log("123")
            //.to(slack)
            .process({ println "Hello World!" + it.in.body.reverse()} as org.apache.camel.Processor)
    }
})
camelContext.start()

addShutdownHook{ camelContext.stop() }
synchronized(this){ this.wait() }
