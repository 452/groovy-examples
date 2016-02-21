@XmlSchema(
elementFormDefault = XmlNsForm.QUALIFIED,
namespace = "dto.client.auth.soft.com",
xmlns=@XmlNs(namespaceURI = "dto.client.auth.soft.com", prefix = "dto"))
package com.github.groovy.examples.xml

import javax.xml.bind.annotation.*
import javax.xml.bind.JAXB

@XmlRootElement(name = 'authTokenRequestDto', namespace = 'dto.client.auth.soft.com')
@XmlAccessorType(XmlAccessType.FIELD)
public class Band {
	private String login
	private String password

	Band() {} // no-argument constaructor is required for POGO

	Band(String n, String e) {
		login = n
		password = e
	}

	String toString() {
		"$login founded in $password"
	}
}

f = new File('pogo.xml')
println f.text
// let's save
JAXB.marshal(new Band('The Little Willies', '2003'), f)
// and then load
assert JAXB.unmarshal(f, Band.class).password == '2003'