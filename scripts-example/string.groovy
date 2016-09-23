
def test = """
bbb
aaaa Password: a123
qqq
"""

print checkPassword(test, "Password")

int checkPassword(String content, String valueForFind) {
	int passwordLength = 0
	content.eachLine() { line ->
		if (line.contains(valueForFind)) {
			passwordLength = line.substring(line.indexOf(valueForFind)).length()
		}
	}
	return passwordLength
}
