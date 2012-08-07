/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2012 by Rapid-I and the contributors
 *
 *  Complete list of developers available at our web site:
 *
 *       http://rapid-i.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package com.rapidminer.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.rapidminer.example.test.ExampleTestSuite;
import com.rapidminer.operator.annotation.test.PolynomialFunctionTest;
import com.rapidminer.operator.io.test.DatabaseWriteTest;
import com.rapidminer.operator.learner.test.LearnerTestSuite;
import com.rapidminer.operator.performance.test.PerformanceTestSuite;

/**
 * 
 * @author Simon Fischer
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

	// Fast tests
	ExampleTestSuite.class,
	PerformanceTestSuite.class,

	PolynomialFunctionTest.class,
	
	EscapeTest.class,
	OperatorVersionTest.class,	
	IterationArrayListTest.class,
	MathUtilsTest.class,
	SECDTest.class,
	
	// Slow tests
	LearnerTestSuite.class,
	TestRepositorySuite.class,
	
	
	// Database tests
	// Depends on servers being up, timeout takes a while
	DatabaseWriteTest.class
	// SampleTest.class,
	
	// TODO MS CSV reader test not working
	//	CSVReaderTest.class//,
})
	
public class AllTests {}
