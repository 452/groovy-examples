//https://habrahabr.ru/post/254165/
//http://itmuslim.org/blog/2013-04-19-565
//https://www.sslshopper.com
@Grab(group='com.github.groovy-wslite', module='groovy-wslite', version='1.1.3')
import wslite.soap.*

def client = new SOAPClient('http://www.xmlme.com/WSShakespeare.asmx?WSDL')
def response = client.send(SOAPAction: 'http://xmlme.com/WebServices/GetSpeech') {
    body {
        GetSpeech(xmlns: 'http://xmlme.com/WebServices') {
		Request('Get thee to a nunnery')
        }
    }
}

assert response
def statusCode = response.httpResponse.statusCode
assert 200 == statusCode
def result = response.GetSpeechResponse.GetSpeechResult.text()
println "status code: $statusCode \n $result"
