/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2010 by Rapid-I and the contributors
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
package com.rapidminer.operator.generator;

/**
 * The label is att1*att1*att1 + att2*att2 + att1*att2 + att1/att2 -
 * 1.0d/(att3*att3).
 * 
 * @author Ingo Mierswa
 */
public class ComplicatedFunction2 extends RegressionFunction {

	public double calculate(double[] att) throws FunctionException {
		if (att.length < 3)
			throw new FunctionException("Complicated function2", "needs at least 3 attributes!");
		return (att[0] * att[0] * att[0] + att[1] * att[1] + att[0] * att[1] + att[0] / Math.abs(att[2]) - 1.0d / (att[2] * att[2]));
	}
}
