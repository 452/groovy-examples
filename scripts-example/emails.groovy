@Grapes(
    @Grab(group='com.sun.mail', module='javax.mail', version='1.5.5')
)
import javax.mail.*

println getEmailsFromFolder('INBOX').messages.eachWithIndex { msg, i ->
	//log.info // for SOAPUI
	//println "email: ${i+1} SentDate: ${msg.sentDate} ReceivedDate: ${msg.receivedDate} Sublect: ${msg.subject} From: ${msg.from} Sender: ${msg.sender} Headers: ${msg.allHeaders.toList().collect { "$it.name:$it.value" }}"
	println "email: ${i+1} Headers: ${msg.allHeaders.toList().collect { "$it.name:$it.value" }}"
	//msg.setFlag(Flags.Flag.SEEN, true)
}

def getEmailsFromFolder(emailFolderName) {
	//http://www.technipelago.se/blog/show/groovy-imap
	//http://docs.oracle.com/javaee/7/api/javax/mail/search/package-summary.html
	//http://stackoverflow.com/questions/9428772/javax-mail-message-trying-to-fetch-messages-for-a-given-date-range
	def host = 'mail.strikersoft.com'
	def port = '993'
	def username = ''
	def password = ''
	Properties props = new Properties()
	props.setProperty("mail.store.protocol", "imap")
	props.setProperty("mail.imap.host", host)
	props.setProperty("mail.imap.port", port)
	props.setProperty("mail.imap.ssl.enable", "true");
	props.setProperty("mail.imap.ssl.trust", "*");
	Session session = Session.getDefaultInstance(props, null)
	def store = session.getStore("imap")
	store.connect(host, username, password)
	def folder = store.getFolder(emailFolderName)
	folder.open(Folder.READ_WRITE)
	return folder
}
