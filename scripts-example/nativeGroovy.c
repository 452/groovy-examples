#include <stdio.h>
#include <time.h>

void printValue(char* value) {
	printf("\n%s\n\n", value);
	time_t t = time(NULL);
	struct tm *tm = localtime(&t);
	printf("%s\n", asctime(tm));
}
