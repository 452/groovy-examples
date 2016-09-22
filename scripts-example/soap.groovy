@Grab(group='com.github.groovy-wslite', module='groovy-wslite', version='1.1.3')
import wslite.soap.*

def client = new SOAPClient('http://www.webservicex.net/CurrencyConvertor.asmx')
def response = client.send(SOAPAction: 'http://www.webserviceX.NET/ConversionRate') {
    body {
        ConversionRate( xmlns: 'http://www.webserviceX.NET/') {
            FromCurrency('USD')
            ToCurrency('UAH')
        }
    }
}

assert response
assert 200 == response.httpResponse.statusCode

println response.ConversionRateResponse.ConversionRateResult.text()
