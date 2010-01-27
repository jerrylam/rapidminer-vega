/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2009 by Rapid-I and the contributors
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
package com.rapidminer.operator.learner.functions;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.learner.PredictionModel;
import com.rapidminer.tools.Tools;


/**
 * The model for linear regression.
 * 
 * If used the intercept is saved in the last cell of the coefficients array. 
 * 
 * @author Ingo Mierswa
 */
public class LinearRegressionModel extends PredictionModel {

	private static final long serialVersionUID = 8381268071090932037L;

	private String[] attributeNames;
	
	private String[] attributeConstructions;
	
	private boolean[] selectedAttributes;
	
	private double[] coefficients;

	private boolean useIntercept = true;
	
	private String firstClassName = null;
	
	private String secondClassName = null;
	
	public LinearRegressionModel(ExampleSet exampleSet, boolean[] selectedAttributes, double[] coefficients, boolean useIntercept, String firstClassName, String secondClassName) {
		super(exampleSet);
		this.attributeNames = com.rapidminer.example.Tools.getRegularAttributeNames(exampleSet);
		this.attributeConstructions = com.rapidminer.example.Tools.getRegularAttributeConstructions(exampleSet);
		this.selectedAttributes = selectedAttributes;
		this.coefficients = coefficients;
		this.useIntercept = useIntercept;
		this.firstClassName = firstClassName;
		this.secondClassName = secondClassName;
	}
	
	@Override
	public ExampleSet performPrediction(ExampleSet exampleSet, Attribute predictedLabel) throws OperatorException {
		Attribute[] attributes = new Attribute[attributeNames.length];
		for (int i = 0; i < attributeNames.length; i++) {
			attributes[i] = exampleSet.getAttributes().get(attributeNames[i]);
			if (attributes[i] == null && selectedAttributes[i])
				throw new UserError(null, 111, attributeNames[i]);
		}
		
		for (Example example : exampleSet) {
			double prediction = 0;
			int index = 0;
			int attributeCounter = 0;
			for (Attribute attribute : attributes) {
				if (selectedAttributes[attributeCounter]) {
					prediction += coefficients[index] * example.getValue(attribute);
					index++;
				}
				attributeCounter++;
			}
			
			if (useIntercept)
				prediction += coefficients[index];
			
			if (predictedLabel.isNominal()) {
				int predictionIndex = prediction > 0.5 ? predictedLabel.getMapping().getIndex(secondClassName): predictedLabel.getMapping().getIndex(firstClassName);
				example.setValue(predictedLabel, predictionIndex);
				// set confidence to numerical prediction, such that can be scaled later
				example.setConfidence(secondClassName, 1.0d / (1.0d + java.lang.Math.exp(-prediction)));
				example.setConfidence(firstClassName, 1.0d / (1.0d + java.lang.Math.exp(prediction)));
			} else {
				example.setValue(predictedLabel, prediction);
			}	
		}	
		return exampleSet;
	}
		
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		boolean first = true;
		int index = 0;
		for (int i = 0; i < selectedAttributes.length; i++) {
			if (selectedAttributes[i]) {
				result.append(getCoefficientString(coefficients[index], first) + " * " + attributeConstructions[i] + Tools.getLineSeparator());
				index++;
				first = false;
			}
		}
		if (useIntercept)
			result.append(getCoefficientString(coefficients[coefficients.length - 1], first));
		return result.toString();
	}
	
	private String getCoefficientString(double coefficient, boolean first) {
		if (!first) {
			if (coefficient >= 0)
				return "+ " + Tools.formatNumber(Math.abs(coefficient));
			else
				return "- " + Tools.formatNumber(Math.abs(coefficient));
		} else {
			if (coefficient >= 0)
				return "  " + Tools.formatNumber(Math.abs(coefficient));
			else
				return "- " + Tools.formatNumber(Math.abs(coefficient));
		}
	}

	public String[] getAttributeNames() {
		return attributeNames;
	}

	public boolean[] getSelectedAttributes() {
		return selectedAttributes;
	}

	public double[] getCoefficients() {
		return coefficients;
	}

	public String getFirstLabel() {
		return firstClassName;
	}

	public String getSecondLabel() {
		return secondClassName;
	}
}
