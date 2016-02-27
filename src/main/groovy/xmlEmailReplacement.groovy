def EMAIL_PATTERN = '(?:[a-z0-9!#$%&\\\'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])';

def xmlPath = 'fh4K8G.xml'

//def file = new File(xmlPath)
//def emailsList = file.text.findAll(EMAIL_PATTERN)

xml = '''
qa@strikersoft.com
qa-9-30@strikersoft.com
qa-9@strikersoft.com
'''

def newXml = xml.replaceAll(EMAIL_PATTERN, "qa@strikersoft.com")

def emailsList = newXml.findAll(EMAIL_PATTERN)

println "In formIn ${emailsList.size} email\\s: ${emailsList}"

emailsList.each {
	assert it == 'qa@strikersoft.com'
}

//file.write(newXml);