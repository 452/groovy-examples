@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
@Grab('com.xlson.groovycsv:groovycsv:1.1')
import static com.xlson.groovycsv.CsvParser.parseCsv
// import groovyx.net.http.HTTPBuilder
// import static groovyx.net.http.Method.GET
// import static groovyx.net.http.ContentType.XML
// import static groovyx.net.http.ContentType.JSON
import groovyx.net.http.RESTClient
// import groovy.util.slurpersupport.GPathResult
// import static groovyx.net.http.ContentType.URLENC
String csvContents = new File('result.csv').text
def data = parseCsv(csvContents)
File outputCsv = new File("output.csv")

for(line in data) {
  def login = line.login
  def groupsList = getGroupsForUser(login)
  def outputData = "$login;$groupsList\n"
  println outputData
  outputCsv << outputData
}

def getGroupsForUser(def user) {
  def list = []
  getData(user).user.groups.each {
    list << it.code
  }
  list
}

def getData(def user) {
  def restClient = new RESTClient( "https://api.awesome.com/$user" )
  def response = restClient.get(headers: ["tokenId": ""])
  return response.data
}
