package com.github.groovy.examples

import groovy.transform.TypeChecked;

@TypeChecked
class Operators {

	def elvis() {
		def params = [name: "Max"]
		params.name = null
		params.name ?: true
	}

	def elvis2() {
		def params = [name: "Max"]
		params?.name
	}
}