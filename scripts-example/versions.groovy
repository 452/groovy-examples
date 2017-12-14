// https://confluence.atlassian.com/bitbucketserver/using-branches-in-bitbucket-server-776639968.html
def text = '''release-1.0.1
release-1.0.9
release-1.0.30
release-1.0.30.1
release-1.0.30.2
release-1.0.30.3-41n
release-1.0.30.4
release-1.0.35
release-1.0.99
'''
def verionInProductionWithHotFix = '1.0.30.3'

def info = "\n$verionInProductionWithHotFix (Version in production with hotfix)\n"

text.readLines().each() { line ->
  info += checkCompromisedRelease(line, verionInProductionWithHotFix)
}

println info
if (hotFixInDev) {
  println "git branch dev contain hotfix"
} else {
  println "dev branch (Compromised)"
}

String checkCompromisedRelease(def line, def verionInProductionWithHotFix) {
  String result = ''
  try {
    Double versionNumber = getVersion(line)
    if (versionNumber>getVersion(verionInProductionWithHotFix)) {
      result = "${result}${line} (Compromised)\n"
    }
  } catch (NumberFormatException e) {
    result = "${result}unable to parse git tag ${line}\n"
  }
  result
}

Double getVersion(def line) {
  return Double.parseDouble(line.replaceAll('release-', '').replaceFirst('\\.', '').replaceFirst('\\.', ''))
}
