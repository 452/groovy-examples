import hudson.model.*

println currentBuild.getBuildVariables()

Jenkins jenkins = new Jenkins(currentBuild)

jenkins.getVariables()

class Jenkins {

	def DEFAULT_TARGET_SERVER = 'qa'
	def DEFAULT_BUILD_NUMBER = 30
	def PROJECT_NAME = 'tetra'
	def targetServer = DEFAULT_TARGET_SERVER
	def tetraIntegrationBuild
	def tetraBackendBuild
	def marketBuildNumber
	def currentBuildNumber = DEFAULT_BUILD_NUMBER

	Jenkins (def currentBuild) {
		targetServer = currentBuild.buildVariableResolver.resolve('TARGET_SERVER') ?: DEFAULT_TARGET_SERVER
		currentBuildNumber =  currentBuild.getNumber() ?: DEFAULT_BUILD_NUMBER
		assert this.targetServer
		tetraIntegrationBuild = Hudson.instance.getJob("$PROJECT_NAME-${targetServer}-integration").getLastBuild()
		tetraBackendBuild = Hudson.instance.getJob("$PROJECT_NAME-${targetServer}-backend").getLastBuild()
		marketBuildNumber = tetraIntegrationBuild.getNumber()
	}

	def getVariables() {
		[TETRA_TAG_NAME:getTagName(), PASSWORD: getCredentialsForTomcat().password, MARKET_BUILD_NUMBER: getMarketVersion(), GIT_COMMIT_SHORT : getGitVersion()]
	}

	def getTagName() {
		"$PROJECT_NAME-$targetServer-$marketBuildNumber-$currentBuildNumber"
	}
	
	def getMarketVersion() {
		"$marketBuildNumber-$currentBuildNumber"
	}
	
	def getGitVersion() {
		def thr = Thread.currentThread()
		def build = thr.executable
		def envVarsMap = build.parent.builds[0].properties.get("envVars")
		return envVarsMap['GIT_COMMIT'].take(6);
	}

	void setDescription() {
		currentBuild.setDescription("${tetraIntegrationBuild}<br/>${tetraBackendBuild}")
	}
	
	def getCredentialsForTomcat() {
		getCredentialsByID("$PROJECT_NAME-$targetServer-tomcat")
	}
	
	def getCredentialsByID(id) {
		jenkins.model.Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].credentials.findResult { credential ->
				if (credential instanceof com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl) {
					if (credential.id == id)
							[id:credential.id, username:credential.username, password:credential.password, description:credential.description]
				}
		}
	}

	def printMessage(message) {
		def LINE_LENGTH = message.size() + 4
		println '*' * LINE_LENGTH
		println "* $message *"
		println '*' * LINE_LENGTH
	}

	String toString() {
		"$targetServer"
	}
}