package com.github.groovy.examples.xls

import groovy.lang.Closure;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;

class Excel {
	def workbook;
	def sheet;
	def labels;
	def row;
	def infilename;
	def outfilename;

	Excel(String fileName) {
		HSSFRow.metaClass.getAt = {int index ->
			def cell = delegate.getCell(index);
			if(! cell) {
				return null;
			}

			def value;

			switch (cell.cellType) {
				case HSSFCell.CELL_TYPE_NUMERIC:
					if(HSSFDateUtil.isCellDateFormatted(cell)) {
						value = cell.dateCellValue;
					}
					else {
						value = new DataFormatter().formatCellValue(cell);
					}
					break;
				case HSSFCell.CELL_TYPE_BOOLEAN:
					value = cell.booleanCellValue
					break;
				default:
					value = new DataFormatter().formatCellValue(cell);
					break;
			}

			return value
		}

		XSSFRow.metaClass.getAt = {int index ->
			def cell = delegate.getCell(index);
			if(! cell) {
				return null;
			}
			def value = new DataFormatter().formatCellValue(cell);
			println cell.cellType
			switch (cell.cellType) {
				case XSSFCell.CELL_TYPE_NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						value = cell.dateCellValue;
					}
					else {
						value = new DataFormatter().formatCellValue(cell);
					}
					break;
				case XSSFCell.CELL_TYPE_BOOLEAN:
					value = cell.booleanCellValue
					break;
				default:
					value = new DataFormatter().formatCellValue(cell);
					break;
			}

			return value;
		}

		infilename = fileName;
		outfilename = fileName;

		try {
			workbook = WorkbookFactory.create(new FileInputStream(infilename));
		}
		catch (FileNotFoundException e) {
			workbook = (infilename =~ /(?is:\.xlsx)$/) ?  new XSSFWorkbook() : new HSSFWorkbook();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		workbook.getCreationHelper().createFormulaEvaluator().evaluateAll()
	}

	def getSheet(index) {
		def requested_sheet;
		if(!index) index = 0;
		if(index instanceof Number) {
			requested_sheet = (workbook.getNumberOfSheets >= index) ? workbook.getSheetAt(index) : workbook.createSheet();
		}
		else if (index ==~ /^\d+$/) {
			requested_sheet = (workbook.getNumberOfSheets >= Integer.valueOf(index)) ? workbook.getSheetAt(Integer.valueOf(index)) : workbook.createSheet();
		}
		else {
			requested_sheet = (workbook.getSheetIndex(index) > -1) ? workbook.getSheet(index) : workbook.createSheet(index);
		}
		return requested_sheet;
	}

	def cell(index) {
		if (labels && (index instanceof String)) {
			index = labels.indexOf(index.toLowerCase());
		}
		if (row[index] == null) {
			row.createCell(index);
		}
		return row[index];
	}

	def cell(index, value) {
		if (labels.indexOf(index.toLowerCase()) == -1) {
			labels.push(index.toLowerCase());
			def frow  = sheet.getRow(0);
			def ncell = frow.createCell(labels.indexOf(index.toLowerCase()));
			ncell.setCellValue(index.toString());
		}
		def cell = (labels && (index instanceof String)) ? row.getCell(labels.indexOf(index.toLowerCase())) : row.getCell(index);
		if (cell == null) {
			cell = (index instanceof String) ? row.createCell(labels.indexOf(index.toLowerCase())) : row.createCell(index);
		}
		cell.setCellValue(value);
	}

	def putRow (sheetName, Map values = [:]) {
		def requested_sheet = getSheet(sheetName);
		if (requested_sheet) {
			def lrow;
			if (requested_sheet.getPhysicalNumberOfRows() == 0) {
				lrow = requested_sheet.createRow(0);
				def lcounter = 0;
				values.each {entry->
					def lcell = lrow.createCell(lcounter);
					lcell.setCellValue(entry.key);
					lcounter++;
				}
			}
			else {
				lrow = requested_sheet.getRow(0);
			}

			def sheetLabels = lrow.collect{it.toString().toLowerCase()}
			def vrow = requested_sheet.createRow(requested_sheet.getLastRowNum() + 1);
			values.each {entry->
				def vcell = vrow.createCell(sheetLabels.indexOf(entry.key.toLowerCase()));
				vcell.setCellValue(entry.value);
			}
		}
	}

	def propertyMissing(String name) {
		cell(name);
	}

	def propertyMissing(String name, value) {
		cell(name, value);
	}

	def eachLine (Map params = [:], Closure closure) {
		/*
		 * Parameters:
		 * skiprows    : The number of rows to skip before the first line of data and/or labels
		 * offset      : The number of rows to skip (after labels) before returning rows
		 * max         : The maximum number of rows to iterate
		 * sheet       : The name (string) or index (integer) of the worksheet to use
		 * labels      : A boolean to treat the first row as a header row (data can be reference by label)
		 *
		 */
		def skiprows = params.skiprows ?: 0;
		def offset = params.offset ?: 0;
		def max = params.max ?: 9999999;
		sheet = getSheet(params.sheet);
		def rowIterator = sheet.rowIterator();
		def linesRead = 0;
		skiprows.times{ rowIterator.next() }
		if(params.labels) {
			labels = rowIterator.next().collect{it.toString().toLowerCase()}
		}
		offset.times{ rowIterator.next() }
		closure.setDelegate(this);
		while(rowIterator.hasNext() && linesRead++ < max) {
			row = rowIterator.next();
			closure.call(row);
		}
	}

	def save () {
		if (workbook.getClass().toString().indexOf("XSSF") > -1) {
			XSSFFormulaEvaluator.evaluateAllFormulaCells((XSSFWorkbook) workbook);
		}
		else {
			HSSFFormulaEvaluator.evaluateAllFormulaCells((HSSFWorkbook) workbook);
		}
		if (outfilename != null) {
			try {
				FileOutputStream output = new FileOutputStream(outfilename);
				workbook.write(output);
				output.close();
				workbook = null;
				workbook = WorkbookFactory.create(new FileInputStream(outfilename));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	def saveAs (String fileName) {
		if (workbook.getClass().toString().indexOf("XSSF") > -1) {
			XSSFFormulaEvaluator.evaluateAllFormulaCells((XSSFWorkbook) workbook);
		}
		else {
			HSSFFormulaEvaluator.evaluateAllFormulaCells((HSSFWorkbook) workbook);
		}

		try {
			FileOutputStream output = new FileOutputStream(fileName);
			workbook.write(output);
			output.close();
			outfilename = fileName;

			workbook = null;
			workbook = WorkbookFactory.create(new FileInputStream(outfilename));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}