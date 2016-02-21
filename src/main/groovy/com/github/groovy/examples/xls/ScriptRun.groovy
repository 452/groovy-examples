package com.github.groovy.examples.xls

import org.apache.poi.hssf.usermodel.HSSFRow

def NAME = 0

new Excel('/data/sample.xls').eachLine(sheet : 'Sheet1', labels : true) { HSSFRow data ->
	println "data: ${data.getCell(NAME).rowIndex} ${data.getCell(NAME)}"
}