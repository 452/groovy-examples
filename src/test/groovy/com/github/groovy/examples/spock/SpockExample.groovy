package com.github.groovy.examples.spock

import spock.lang.Specification
import spock.lang.Unroll;

class SpockExample extends Specification {

	@Unroll
	def "check positive #input #expected"() {
		given: "a new helper"
		def helper = input as int[]
		when: "I filter a list of numbers"
		def result = (input as int[]).collect { it.abs() }
		then: "Only the positive numbers are returned"
		result == expected as int[]
		where:
		input | expected
		[]| []
		[7, 9, 30]| [7, 9, 30]
		[-7, -9, -30]| [7, 9, 30]
	}
}