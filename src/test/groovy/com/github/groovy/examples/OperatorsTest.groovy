package com.github.groovy.examples

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

class OperatorsTest {

	Operators operators = new Operators()

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void elvisTest() {
		assert operators.elvis() == true
	}

	@Test
	public void elvis2Test() {
		assert operators.elvis2() == "Max"
	}
}