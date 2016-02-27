package com.github.groovy.examples.xml

class XMLSerializeDeserialize {

	def printXml(xmlAsString) {
		groovy.xml.XmlUtil.serialize(xmlAsString)
	}
}