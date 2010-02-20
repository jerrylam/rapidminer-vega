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
package com.rapidminer.parameter;

import com.rapidminer.operator.ports.InputPort;

/**
 * 
 * @author Simon Fischer
 *
 */
public class ParameterTypeDateFormat extends ParameterTypeStringCategory {

	private static final long serialVersionUID = 1L;

	private InputPort inPort;

	private ParameterTypeAttribute attributeParameter;

	public static final String[] PREDEFINED_DATE_FORMATS = new String[] {
		"",
		"yyyy.MM.dd G 'at' HH:mm:ss z",
		"EEE, MMM d, ''yy",
		"h:mm a",
		"hh 'o''clock' a, zzzz",
		"K:mm a, z",
		"yyyy.MMMMM.dd GGG hh:mm aaa",
		"EEE, d MMM yyyy HH:mm:ss Z",
		"yyMMddHHmmssZ",
		"yyyy-MM-dd'T'HH:mm:ss.SSSZ"
	};

	public ParameterTypeDateFormat(ParameterTypeAttribute attributeParameter, String key, String description, InputPort inPort, boolean expert) {
		super(key, description, PREDEFINED_DATE_FORMATS, "");
		setExpert(expert);
		this.inPort = inPort;
		this.attributeParameter = attributeParameter;
	}

	public InputPort getInputPort() {
		return inPort;
	}
	
	public ParameterTypeAttribute getAttributeParameterType() {
		return attributeParameter;
	}
}
