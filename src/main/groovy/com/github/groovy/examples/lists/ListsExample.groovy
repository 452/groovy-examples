package com.github.groovy.examples.lists

class ListsExample {

	def getRegionArrayListWithLinkedHashMap() {
		[
			[
				name:'London',
				code:'LD',
				note:'London Region'
			],
			[
				name:'Midlands',
				code:'MD',
				note:'Midlands Region'
			]
		]
	}

	def void printRegions() {
		getRegionArrayListWithLinkedHashMap().each { region -> println "$region.name $region.code $region.note" }
	}

	List getPowerOfTwo(int n) {
		return (0..n).collect { 2 ** it }
	}
}