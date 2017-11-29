@Grab('org.apache.camel:camel-core:2.20.1')
@Grab('org.apache.camel:camel-slack:2.20.1')
@Grab('org.apache.camel:camel-groovy:2.20.1')
@Grab('org.activiti:activiti-camel:6.0.0')
@Grab('org.slf4j:slf4j-simple:1.7.18')
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

// ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault()
                .buildProcessEngine();
// Get main service interfaces
RepositoryService repositoryService = processEngine.getRepositoryService();
RuntimeService runtimeService = processEngine.getRuntimeService();
HistoryService historyService = processEngine.getHistoryService();
// Deploy intro process definition
repositoryService.createDeployment().name("intro").addClasspathResource("intro.bpmn20.xml").deploy();

org.activiti.camel.SimpleContextProvider activiti = new org.activiti.camel.SimpleContextProvider('OrderProcess', camelContext)
// activiti.setValue('OrderProcess')
// activiti.setRef('camelContext')
def jndiContext = new JndiContext();
jndiContext.bind("activiti", activiti)

camelContext.addRoutes(new RouteBuilder() {
    def void configure() {
        Helper helper = new Helper();
        from("timer://logOutputTimer?period=3000")
            // .transform()
            // .simple('new ${date:now:yyyyMMdd} ${in.header.number} range 100..199')
            // .process({ println "Hello World!" + it.in.body.reverse()} as org.apache.camel.Processor)
            .setBody(bean(helper))
            .setProperty(PROCESS_KEY_PROPERTY, simple('OrderProcess.bpmn20.xml'))
            .to("activiti:OrderProcess")
    }
})
camelContext.start()

addShutdownHook{ camelContext.stop() }
synchronized(this){ this.wait() }

public class Helper {
  /*
   * This method will extract information from the Exchange (using Camel annotations) and put them in a
   * Map that will be used for setting up the process' variables.
   */
  @Handler
  public Map getProcessVariables(@Simple('${date:now:yyyy-MM-dd kk:mm:ss}') String timestamp) {
      Map<String, Object> variables = new HashMap<String, Object>();
      variables.put('message', 'testBody');
      variables.put('orderid', 'testIds');
      variables.put('timestamp', timestamp);
      return variables;
  }
}
