@Grab('org.apache.camel:camel-core:2.22.0')
@Grab('org.apache.camel:camel-groovy:2.22.0')
@Grab('org.activiti:activiti-camel:6.0.0')
@Grab('com.h2database:h2:1.4.197')
@Grab('org.slf4j:slf4j-simple:1.7.25')
import org.apache.camel.*
import org.apache.camel.impl.*
import org.apache.camel.builder.*
import org.apache.camel.component.slack.*
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.language.Simple;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.util.jndi.JndiContext

import static org.activiti.camel.ActivitiProducer.PROCESS_KEY_PROPERTY;
import org.activiti.engine.*

def camelContext = new DefaultCamelContext()
String fromActivitiEndPoint = "activiti:testCamelTask:sendMsgToCamel?copyCamelBodyToBody=true";

ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault().buildProcessEngine();
// Get main service interfaces
RepositoryService repositoryService = processEngine.getRepositoryService();
RuntimeService runtimeService = processEngine.getRuntimeService();
HistoryService historyService = processEngine.getHistoryService();
// Deploy intro process definition
repositoryService.createDeployment().name("OrderProcess").addClasspathResource("OrderProcess.bpmn20.xml").deploy();

org.activiti.camel.SimpleContextProvider activiti = new org.activiti.camel.SimpleContextProvider('OrderProcess', camelContext)
//activiti.setValue('OrderProcess')
//activiti.setRef('camelContext')
def jndiContext = new JndiContext();
jndiContext.bind("activiti", activiti)

camelContext.addRoutes(new RouteBuilder() {
    def void configure() {
        Helper helper = new Helper();
        from("timer://logOutputTimer?period=10000")
            .setBody(bean(helper))
            .setProperty(PROCESS_KEY_PROPERTY, simple('OrderProcess'))
            //.setProperty("PROCESS_ID_PROPERTY", simple('OrderProcess'))
            .to('log:aaa?showAll=true')
            .log('Process to handle incoming order file has been started (process instance id ${body})')
            //.to('activiti:OrderProcess:receiveDelivery');
            .to("activiti:OrderProcess");
            
        from("activiti:OrderProcess:processOrder")
            .log('Processing order ${property.orderid} created on ${property:timestamp}')
            .log('  original message: ${property.message}');

        from("activiti:OrderProcess:processDelivery")
            .log('Processing delivery for order ${property.orderid} created on ${property:timestamp}')
            .log('  original message: ${property.message}');
    }
})
camelContext.start()

addShutdownHook{ camelContext.stop() }
synchronized(this){ this.wait() }

public class Helper {

        @Handler
        public Map getProcessVariables(@Body String body,
                                       @Header(Exchange.FILE_NAME) String filename,
                                       @Simple('${date:now:yyyy-MM-dd kk:mm:ss}') String timestamp) {
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("message", body);
            variables.put("orderid", filename);
            variables.put("timestamp", timestamp);
            return variables;
        }
}
