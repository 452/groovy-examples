package com.github.groovy.examples.spock

import spock.lang.Specification
import spock.lang.Unroll;

class SpockExample extends Specification {

	@Unroll
	def "check positive #input #expected"() {
		//given: "a new helper"

		//def helper = input
		when: "I filter a list of numbers"

		def result = input.collect { it.abs() }

		then: "Only the positive numbers are returned"

		result == expected

		where:

		input        |  expected
		[]           |  []
		[ 7, 9, 30]  |  [7, 9, 30]
		[-7,-9,-30]  |  [7, 9, 30]
		[ 7,-9,-30]  |  [7, 9, 30]
		[-7, 9,-30]  |  [7, 9, 30]
		[-7,-9, 30]  |  [7, 9, 30]
		[ 7,-9, 30]  |  [7, 9, 30]
	}
}