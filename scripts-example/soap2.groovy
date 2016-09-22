//https://habrahabr.ru/post/254165/
@Grab(group='com.github.groovy-wslite', module='groovy-wslite', version='1.1.3')
import wslite.soap.*

def client = new SOAPClient('http://www.xmlme.com/WSShakespeare.asmx')
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
