// @Grab(group='org.jodreports', module='jodreports-library', version='3.0.0')
@Grab(group='net.sf.jodreports', module='jodreports', version='2.4.0')
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateFactory;
import groovy.util.slurpersupport.GPathResult

DocumentTemplate template = new DocumentTemplateFactory().getTemplate(new File('test.odt'));
def dataModel = [:]
dataModel.put("mimiText", "Some awesome text from groovy code")
dataModel.put("mimiList", ['Opel', 'Audi', 'Nissan'])
dataModel.put("mimiListOfCars", [new Car(name: 'Opel'), new Car(name: 'Audi'), new Car(name: 'Nissan')])
dataModel.put("mimiObject", new Car(name: 'BMW'))
dataModel.put("mimiObjectXML", exampleObjectFromXML())
template.createDocument(dataModel, new FileOutputStream("GenerateSimpleOdfInstanceTest.odt"));

GPathResult exampleObjectFromXML() {
  def text = """
  <list>
    <technologies>
      <technology>
        <name>Java</name>
      </technology>
      <technology>
        <name>Groovy</name>
      </technology>
    </technologies>
  </list>
  """
  new XmlSlurper().parseText(text)
}

class Car {
  def name
  def someListOfBugs = ['No window', 'Broken radio', 'Crash']
}
