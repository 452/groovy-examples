@Grab('org.apache.camel:camel-core:2.20.1')
@Grab('org.apache.camel:camel-slack:2.20.1')
@Grab('org.apache.camel:camel-groovy:2.20.1')
@Grab('org.apache.camel:camel-quartz2:2.20.1')
@Grab('org.slf4j:slf4j-simple:1.7.18')
import org.apache.camel.*
import org.apache.camel.impl.*
import org.apache.camel.builder.*
import org.apache.camel.component.slack.*
import org.apache.camel.util.jndi.JndiContext
import org.apache.camel.routepolicy.quartz2.CronScheduledRoutePolicy

def camelContext = new DefaultCamelContext()
def slack = "slack://mySlack?channel=#tetra-delivery&iconEmoji=:ghost:&username=ghost&webhookUrl=https://hooks.slack.com/services/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"
//def jndiContext = new JndiContext();
//jndiContext.bind("sslack", new SlackComponent())

camelContext.addRoutes(new RouteBuilder() {
    def void configure() {
        CronScheduledRoutePolicy startPolicy = new CronScheduledRoutePolicy();
        startPolicy.setRouteStartTime("*/1 * * * * ?");
        from("timer://logOutputTimer?period=1000")
            //.setBody(append("Message"))
            //.setBody(constant("the value"))
            .transform()
            .simple('timer: new ${date:now:yyyyMMdd} ${in.header.number}')
            //.to(slack)
            .process({ println "Hello World!" + it.in.body} as org.apache.camel.Processor);
        // from("timer://time1?time=2017-11-25 14:38:10&repeatCount=1")
        // .transform()
        // .simple('Fixed Time: new ${date:now:yyyyMMdd} ${in.header.number}').log("Fixed Time");

        from("direct:start").routeId("startPolicyTest").log("=))))) Fixed Time").routePolicy(startPolicy).to("mock:success");

        from("quartz2://quartz2Test?cron=0/5+*+*+*+*+?").routeId("quartz2Test").log("=))))) Fixed Time").to("mock:success");
    }
})
camelContext.start()

addShutdownHook{ camelContext.stop() }
synchronized(this){ this.wait() }
