import org.jets3t.service.impl.rest.httpclient.RestS3Service
import org.jets3t.service.model.S3Bucket
import org.jets3t.service.model.S3Object
import org.jets3t.service.security.AWSCredentials
@Grab('net.java.dev.jets3t:jets3t:0.9.0')
import au.com.bytecode.opencsv.*
@Grab('net.sf.opencsv:opencsv:2.3')

def accessKey
def secretKey
def bucketName
def credentials
def s3Service
def bucket

main()

def main() {
	setup()
	def assessmentsListForDownload = makeAssessmentsListForDownload()
	assessmentsListForDownload.eachWithIndex { assessment, i ->
		def assessmentPathOnS3 = assessment.value
		makeFolder('bembi', assessmentPathOnS3)
		downloadAssessments('bembi', assessmentPathOnS3)
		println "${i+1} from ${assessmentsListForDownload.size()}"
	}
	println 'done'
}

def makeAssessmentsListForDownload() {
	def assessmentsPathsMap = makeMapWithAssessmentPath('C:\\Users\\v\\data-formin-result.csv')
	def arr = [:]
	readAssessmentsListWith500ErrorCode('C:\\Users\\v\\data-hs-500.csv').each { assessmentName ->
		arr.put(assessmentName,  getAssessmentPathByName(assessmentsPathsMap, assessmentName).replaceAll("\\s",""))
	}
	arr
}

def getAssessmentPathByName(def assessmentsPathsMap, def assessmentName) {
	assessmentsPathsMap."$assessmentName"
}

def makeMapWithAssessmentPath(def fileName) {
	def assessmentCount = 0
	def file = new File(fileName)
	def arr = [:]
	file.eachLine { String line ->
		assessmentCount++
		def assessmentName = line.tokenize(',')[0]
		def assessmentPath = line.tokenize(',')[1]
		def assessment = arr.get(assessmentName)
		if (assessment == null) {
			arr.put(assessmentName, assessmentPath)
		} else {
			assessment = assessment +'*'+ assessmentPath
			arr.put(assessmentName, assessment)
		}
	}
	println "All assessment paths count: $assessmentCount"
	arr
}	

def readAssessmentsListWith500ErrorCode(def fileName) {
	def assessmentCount = 0
	def file = new File(fileName)
	def arr = []
	file.eachLine { String line ->
		assessmentCount++
		def assessmentName = line.tokenize(',')[1]
		arr << assessmentName
	}
	println "All assessment with 500 error code count: $assessmentCount"
	arr
}

def getFolderPath(def folderName, def path) {
	def folderForDownload = path
	if (folderForDownload != null) {
		folderForDownload = folderForDownload.tokenize('/')
		folderForDownload = "${folderName}/${folderForDownload[3]}/${folderForDownload[4]}"
	}
	folderForDownload
}

def getFilePath(def folderName, def path) {
	def fileForDownload = path
	if (fileForDownload != null) {
		fileForDownload = fileForDownload.tokenize('/')
		fileForDownload = "${folderName}/${fileForDownload[3]}/${fileForDownload[4]}/${fileForDownload[5]}"
	}
	fileForDownload
}

def makeFolder(def folderName, def path) {
	new File(getFolderPath(folderName, path)).mkdirs()  
}

def downloadAssessments(def folderName, def paths) {
	paths.tokenize('*').each { path ->
		println path
		S3Object objectComplete = s3Service.getObject(bucketName, path);
		def outFile = new File(getFilePath(folderName, path))
		outFile << objectComplete.getDataInputStream().getText()
	}
}

def setup() {
	accessKey = ''
	secretKey = ''
	bucketName = 'prod'
	credentials = new AWSCredentials(accessKey, secretKey)
	s3Service = new RestS3Service(credentials)
	bucket = new S3Bucket(bucketName)
}
