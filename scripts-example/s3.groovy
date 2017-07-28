import org.jets3t.service.impl.rest.httpclient.RestS3Service
import org.jets3t.service.model.S3Bucket
import org.jets3t.service.model.S3Object
import org.jets3t.service.security.AWSCredentials
@Grab('net.java.dev.jets3t:jets3t:0.9.0')
import au.com.bytecode.opencsv.*
@Grab('net.sf.opencsv:opencsv:2.3')

accessKey = ''
secretKey = ''
bucketName = ''

credentials = new AWSCredentials(accessKey, secretKey)
s3Service = new RestS3Service(credentials)
bucket = new S3Bucket(bucketName)

FileWriter writer = new FileWriter(new File("/tmp/${bucketName}.csv"));
def csv = new CSVWriter(writer)
S3Bucket[] buckets = s3Service.listAllBuckets()
S3Object[] objects = s3Service.listObjects(bucketName, '', '')

csv.writeNext((String[])['Path', 'Size', 'LastModifiedDate', 'LastModifiedDateUnixTimeStamp', 'isDirectory', 'ETag', 'md5HashAsBase64'])

for (int o = 0; o < objects.length; o++) {
	def name = objects[o].getKey()
	def size = objects[o].getContentLength()
	def lastModifiedDate = objects[o].getLastModifiedDate()
	def lastModifiedDateTimeStamp = objects[o].getLastModifiedDate().getTime()/1000
	def isDirectoryPlaceholder = objects[o].isDirectoryPlaceholder()
	def eTag = objects[o].getETag()
	def md5HashAsBase64 = objects[o].getMd5HashAsBase64()
    csv.writeNext((String[])[name, size, lastModifiedDate, lastModifiedDateTimeStamp, isDirectoryPlaceholder, eTag, md5HashAsBase64])
}

writer.close()
System.out.println(objects.length)