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
package com.rapidminer.tools.math;

import javax.swing.Icon;

import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.operator.ResultObjectAdapter;

/**
 * This class encapsulates the result of a statistical significance test.
 * Subclasses may also want to override the method getVisualizationComponent to
 * provide a nicer graphical version for the GUI.
 * 
 * @author Ingo Mierswa
 */
public abstract class SignificanceTestResult extends ResultObjectAdapter {

	private static final long serialVersionUID = 2586381371596047181L;

	private static final String RESULT_ICON_NAME = "symbol_percent.png";

	private static Icon resultIcon = null;

	static {
		resultIcon = SwingTools.createIcon("16/" + RESULT_ICON_NAME);
	}

	/** Returns the name of the test. */
	@Override
	public abstract String getName();

	/** Returns a string describing the test result. */
	@Override
	public abstract String toString();

	/** Returns the calculated probability value. */
	public abstract double getProbability();

	public String getExtension() { return "sgf"; }

	public String getFileDescription() { return "significance test"; }

	@Override
	public Icon getResultIcon() {
		return resultIcon;
	}
}
