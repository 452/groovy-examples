// @Grab(group='org.jodreports', module='jodreports-library', version='3.0.0')
@Grab(group='net.sf.jodreports', module='jodreports', version='2.4.0')
@Grab(group='org.freemarker', module='freemarker', version='2.3.23')
@Grab(group='commons-io', module='commons-io', version='2.5')
@Grab(group='xom', module='xom', version='1.2.5')
@Grab(group='org.slf4j', module='slf4j-api', version='1.7.25')
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateFactory;
import groovy.util.slurpersupport.GPathResult

DocumentTemplate template = new DocumentTemplateFactory().getTemplate(new File('test.odt'));
def dataModel = [:]
dataModel.put("mimiText", "Some awesome text from groovy code")
dataModel.put("mimiList", ['Opel', 'Audi', 'Nissan'])
dataModel.put("mimiListOfCars", [new Car(name: 'Opel'), new Car(name: 'Audi'), new Car(name: 'Nissan')])
dataModel.put("mimiObject", new Car(name: 'BMW'))
dataModel.put("mimiObjectXML", new XmlSlurper().parseText(exampleXML()))
dataModel.put("xml", freemarker.ext.dom.NodeModel.parse(new org.xml.sax.InputSource( new StringReader((exampleXML())))))
template.createDocument(dataModel, new FileOutputStream("GenerateSimpleOdfInstanceTest.odt"));

dataModel.eachWithIndex { a, b ->
  def className = a.value.class
  println "Object: $a.key $className\n"
    a.value.class.declaredFields.each { field ->
      if (!field.name.contains('__') && !field.name.contains('$') && !field.name.contains('metaClass') && !field.type.toString().contains('[Ljava')
      && !field.type.toString().contains('interface java')
      && !field.name.contains('DEFAULT_CAPACITY')
      && !field.name.toString().contains('MAX_ARRAY_SIZE')) {
        println "${field.type} ${field.name}"
      }
    a.value.metaClass.methods.each { method ->
      println "${method.returnType.name} ${method.name}( ${method.parameterTypes*.name.join( ', ' )} )"
    }
  }
}

String exampleXML() {
  def text = """
  <mlist>
    <title>Test Good List</title>
    <technologies>
      <technology>
        <name>Java</name>
      </technology>
      <technology>
        <name>Groovy</name>
      </technology>
    </technologies>
  </mlist>
  """
}

class Car {
  String name
  ArrayList someListOfBugs = ['No window', 'Broken radio', 'Crash']
}
