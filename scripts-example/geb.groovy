@Grab(group='org.gebish', module='geb-core', version='2.0')
@Grab(group='org.seleniumhq.selenium', module='selenium-java', version='3.8.1')
import geb.Browser
import java.net.URLEncoder;

def projects = '''sussex-full-10102016
L-21391
L-21332
'''

System.setProperty("webdriver.gecko.driver", "/home/ilavryniuk/Downloads/geckodriver")
Browser.drive {
	go "https://url/login.html"
	waitFor { $(type:'submit') }
	$(name:'j_username').value('')
	$(name:'j_password').value('')
	$(type:'submit').click()
}

projects.eachLine { project, count ->
	Browser.drive {
		println "${count+1} $project"
		go "https://url/edit.html?taskId=$project"
		waitFor {$(name: 'record.code', value: project)}
		$(name:'record.property.address4').value('Kovel')
		$(id:'actionUpdate', type:'submit').click()
		waitFor {js.'jQuery.active == 0'}
	}
}