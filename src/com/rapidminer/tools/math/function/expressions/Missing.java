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
package com.rapidminer.tools.math.function.expressions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * Returns true if the given argument is a missing value; false otherwise.
 * 
 * @author Marco Boeck
 */
public class Missing extends PostfixMathCommand {
	
	public Missing() {
		numberOfParameters = 1;
	}
	
	/**
	 * Checks for missing value.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run(Stack stack) throws ParseException {
		checkStack(stack);
		
		Object numberObject = stack.pop();
		if (!(numberObject instanceof Double)) {
			throw new ParseException("Invalid argument type for 'missing', must be a number");
		}
		Double number = (Double)numberObject;
		stack.push(number.isNaN());
	}
}
