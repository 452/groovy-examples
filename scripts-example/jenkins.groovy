import hudson.model.*

Jenkins jenkins = new Jenkins()

jenkins.getVariables()

class Jenkins {

  def DEFAULT_TARGET_SERVER = 'qa'
  def DEFAULT_BUILD_NUMBER = 30
  def PROJECT_NAME = 'tetra'
  def targetServer = currentBuild?.buildVariableResolver?.resolve('TARGET_SERVER') ?: DEFAULT_TARGET_SERVER
  def currentBuild
  def tetraIntegrationBuild = Hudson.instance.getJob("$PROJECT_NAME-${targetServer}-integration").getLastBuild()
  def tetraBackendBuild = Hudson.instance.getJob("$PROJECT_NAME-${targetServer}-backend").getLastBuild()
  def marketBuildNumber = tetraIntegrationBuild.getNumber()
  def currentBuildNumber = currentBuild?.getNumber() ?: DEFAULT_BUILD_NUMBER

  Jenkins () {
    assert targetServer
    tetraIntegrationBuild = Hudson.instance.getJob("$PROJECT_NAME-${targetServer}-integration").getLastBuild()
    tetraBackendBuild = Hudson.instance.getJob("$PROJECT_NAME-${targetServer}-backend").getLastBuild()
    marketBuildNumber = tetraIntegrationBuild.getNumber()
  }

  def getVariables() {
    [TETRA_TAG_NAME:getTagName(), MARKET_BUILD_NUMBER: getMarketVersion(), PASSWORD: getCredentialsForTomcat().password, GIT_COMMIT_SHORT : getGitVersion()]
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

}