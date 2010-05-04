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
package com.rapidminer.io.process;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rapidminer.BreakpointListener;
import com.rapidminer.Process;
import com.rapidminer.ProcessContext;
import com.rapidminer.io.process.rules.ChangeParameterValueRule;
import com.rapidminer.io.process.rules.DeleteAfterAutoWireRule;
import com.rapidminer.io.process.rules.DeleteUnnecessaryOperatorChainRule;
import com.rapidminer.io.process.rules.ExchangeSubprocessesRule;
import com.rapidminer.io.process.rules.OperatorEnablerRepairRule;
import com.rapidminer.io.process.rules.ParseRule;
import com.rapidminer.io.process.rules.PassthroughShortcutRule;
import com.rapidminer.io.process.rules.RenamePlotterParametersRule;
import com.rapidminer.io.process.rules.ReplaceIOContainerWriter;
import com.rapidminer.io.process.rules.ReplaceIOMultiplierRule;
import com.rapidminer.io.process.rules.ReplaceOperatorRule;
import com.rapidminer.io.process.rules.ReplaceParameterRule;
import com.rapidminer.io.process.rules.SetParameterRule;
import com.rapidminer.io.process.rules.SetRoleByNameRule;
import com.rapidminer.io.process.rules.SwitchListEntriesRule;
import com.rapidminer.io.process.rules.WireAllOperators;
import com.rapidminer.operator.Annotations;
import com.rapidminer.operator.DummyOperator;
import com.rapidminer.operator.ExecutionUnit;
import com.rapidminer.operator.ListDescription;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorChain;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ProcessRootOperator;
import com.rapidminer.operator.UnknownParameterInformation;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.InputPorts;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.OutputPorts;
import com.rapidminer.operator.ports.PortException;
import com.rapidminer.operator.ports.metadata.CompatibilityLevel;
import com.rapidminer.parameter.ParameterTypeEnumeration;
import com.rapidminer.parameter.ParameterTypeList;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.OperatorService;
import com.rapidminer.tools.ProgressListener;
import com.rapidminer.tools.XMLException;
import com.rapidminer.tools.container.Pair;
import com.rapidminer.tools.plugin.Plugin;


/** Class that parses an XML DOM into an {@link Operator}.
 * 
 * @author Simon Fischer
 *
 */
public class XMLImporter {

	public static final int VERSION_NONE = 0;
	public static final int VERSION_RM_3 = 30;
	public static final int VERSION_RM_4 = 40;
	public static final int VERSION_RM_5 = 50;
	public static final int CURRENT_VERSION = VERSION_RM_5;

	/** Encoding in which process files are written. UTF-8 is guaranteed to exist on any JVM, see
	 *  javadoc of {@link Charset}. */
	public static final Charset PROCESS_FILE_CHARSET = Charset.forName("UTF-8");

	private static List<ParseRule> PARSE_RULES = new LinkedList<ParseRule>();
	/** Reads the parse rules from parserules.xml */
	public static void init() {
		URL rulesResource = XMLImporter.class.getResource("/com/rapidminer/resources/parserules.xml");
		if (rulesResource != null) {			
			// registering the core rules without name prefix
			importParseRules(rulesResource, null);
		} else {
			LogService.getRoot().warning("Cannot find default parse rules.");
		}		
	}
	
	/**
	 * This method adds the parse rules from the given resource to the import rule set. The operator name prefix
	 * describes the operators coming from plugins. The core operators do not have any name prefix, while the 
	 * plugin operators are registered using <plugin>:<operatorname>
	 */
	public static void importParseRules(URL rulesResource, Plugin prover) {
		if (rulesResource == null) {
			throw new NullPointerException("Parserules resource must not be null.");
		} else {
			
			String operatorNamePrefix = "";
			if (prover != null)
				operatorNamePrefix = prover.getPrefix() + ":";

			LogService.getRoot().config("Reading parse rules from "+rulesResource);
			try {
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(rulesResource.openStream());
				if (!doc.getDocumentElement().getTagName().equals("parserules")) {
					LogService.getRoot().log(Level.SEVERE, "XML document "+rulesResource+" does not start with <parserules>");	
				} else {
					NodeList operatorElements = doc.getDocumentElement().getChildNodes();
					for (int i = 0; i < operatorElements.getLength(); i++) {
						if (operatorElements.item(i) instanceof Element) {
							Element operatorElement = (Element)operatorElements.item(i);
							String operatorTypeName = operatorElement.getNodeName();
							// Just used for testing if all parseRules are assigned to valid operators
							//							try {
							//								OperatorService.createOperator(operatorTypeName);
							//							} catch (OperatorCreationException e) {
							//								LogService.getRoot().warning("Could not find Operator: " + operatorTypeName);
							//							}

							NodeList ruleElements = operatorElement.getChildNodes();
							for (int j = 0; j < ruleElements.getLength(); j++) {
								if (ruleElements.item(j) instanceof Element) {
									PARSE_RULES.add(constructRuleFromElement(operatorNamePrefix + operatorTypeName, (Element)ruleElements.item(j)));
								}
							}
						}
					}
					LogService.getRoot().fine("Replacement rules are: "+PARSE_RULES);
				}
			} catch (Exception e) {
				LogService.getRoot().log(Level.SEVERE, "Error reading parse rules from "+rulesResource+": "+e, e);	
			}
		}
	}

	public static ParseRule constructRuleFromElement(String operatorTypeName, Element element) throws XMLException {
		if (element.getTagName().equals("replaceParameter")) {
			return new ReplaceParameterRule(operatorTypeName, element);
		} else if (element.getTagName().equals("deleteAfterAutowire")) {
			return new DeleteAfterAutoWireRule(operatorTypeName, element);
		} else if (element.getTagName().equals("deleteUnnecessaryOperatorChain")) {
			return new DeleteUnnecessaryOperatorChainRule();
		} else if (element.getTagName().equals("replaceIOContainerWriter")) {
			return new ReplaceIOContainerWriter(element);
		} else if (element.getTagName().equals("passthroughShortcut")) {
			return new PassthroughShortcutRule();
		} else if (element.getTagName().equals("replaceIOMultiplier")) {
			return new ReplaceIOMultiplierRule();
		} else if (element.getTagName().equals("repairOperatorEnabler")) {
			return new OperatorEnablerRepairRule();
		} else if (element.getTagName().equals("setParameter")) {
			return new SetParameterRule(operatorTypeName, element);
		} else if (element.getTagName().equals("replaceParameterValue")) {
			return new ChangeParameterValueRule(operatorTypeName, element);
		} else if (element.getTagName().equals("replaceOperator")) {
			return new ReplaceOperatorRule(operatorTypeName, element);
		} else if (element.getTagName().equals("exchangeSubprocesses")) {
			return new ExchangeSubprocessesRule(operatorTypeName, element);
		} else if (element.getTagName().equals("wireSubprocess")) {
			return new WireAllOperators(operatorTypeName, element);
		} else if (element.getTagName().equals("switchListEntries")) {
			return new SwitchListEntriesRule(operatorTypeName, element);
		} else if (element.getTagName().equals("renamePlotterParameters")) {
			return new RenamePlotterParametersRule(operatorTypeName, element);
		} else if (element.getTagName().equals("replaceRoleParameter")) {
			return new SetRoleByNameRule(operatorTypeName, element);
		} else {
			throw new XMLException("Unknown rule tag: <"+element.getTagName()+">");
		}
	}

	private int version = VERSION_NONE;

	private final List<Runnable> jobsAfterAutoWire = new LinkedList<Runnable>();
	private final List<Runnable> jobsAfterTreeConstruction = new LinkedList<Runnable>();

	private boolean mustAutoConnect = false;

	private int total;
	private final ProgressListener progressListener;
	private int created = 0;

	private final StringBuilder messages = new StringBuilder();

	private boolean operatorAsDirectChildrenDeprecatedReported = false;

	/** Creates a new importer that reports progress to the given listener. */
	public XMLImporter(ProgressListener listener) {
		progressListener = listener;
	}

	private int messageCount = 0;
	public void addMessage(String msg) {
		LogService.getRoot().info(msg);
		messageCount++;
		messages.append("<li>");		
		messages.append(msg);
		messages.append("</li>");
	}

	private void setVersion(int version) {
		this.version = version;		
		LogService.getRoot().finest("Process file version is "+version);		
	}

	public void parse(Document doc, Process process, List<UnknownParameterInformation> uli) throws XMLException {
		ProcessRootOperator rootOperator = parse(doc.getDocumentElement(), uli);
		process.setRootOperator(rootOperator);
		// Process context
		NodeList contextElems = doc.getDocumentElement().getElementsByTagName("context");
		switch (contextElems.getLength()) {
		case 0:
			break;
		case 1:
			parseContext((Element)contextElems.item(0), process);
			break;
		default:
			addMessage("&lt;process&gt; can have at most one &lt;context&gt; tag.");
			break;
		}

		// Annotations
		NodeList annotationsElems = doc.getDocumentElement().getElementsByTagName(Annotations.ANNOTATIONS_TAG_NAME);
		switch (annotationsElems.getLength()) {
		case 0:
			break;
		case 1:
			process.getAnnotations().parseXML((Element)annotationsElems.item(0));
			break;
		default:
			addMessage("&lt;process&gt; can have at most one &lt;annotations&gt; tag.");
			break;
		}
		
		if (hasMessage()) {
			process.setImportMessage(getMessage());
		}
		process.setProcessFileVersion(version);
	}

	private ProcessRootOperator parse(Element root, List<UnknownParameterInformation> uli) throws XMLException {
		if ("experiment".equals(root.getTagName())) {
			addMessage("<code>&lt;experiment&gt;</code> is deprecated XML syntax. Use <code>&lt;process&gt;</code> instead.");			
		}
		Element rootOpElement = null;
		if ("process".equals(root.getTagName()) || "experiment".equals(root.getTagName())) {
			parseVersion(root);
			NodeList children = root.getChildNodes();
			int length = children.getLength();
			for (int i = 0; i < length; i++) {
				Node childNode = children.item(i);
				if (childNode instanceof Element) {
					Element childElement = (Element)childNode;
					if (childElement.getTagName().equals("operator")) {
						rootOpElement = childElement;
						break;
					}						
				}
			}
			if (rootOpElement == null) {
				throw new XMLException("The <process> tag must contain exactly one inner operator of type 'Process'!");
			}
		} else if ("operator".equals(root.getTagName())) {
			rootOpElement = root;
		} else {
			throw new XMLException("Root element must be one out of <process>, <experiment>, or <operator>.");
		}
		Operator rootOp = parseOperator(rootOpElement, uli);

		for (Runnable runnable : jobsAfterTreeConstruction) {
			runnable.run();
		}

		if (mustAutoConnect) {
			if (rootOp instanceof OperatorChain) {
				try {
					((OperatorChain)rootOp).getSubprocess(0).autoWire(CompatibilityLevel.PRE_VERSION_5, true, true);
					addMessage("As of version 5.0, RapidMiner processes define an explicit data flow. This data flow has been constructed automatically.");
				} catch (Exception e) {
					addMessage("As of version 5.0, RapidMiner processes define an explicit data flow. This data flow could not be constructed automatically: "+e);
					LogService.getRoot().log(Level.WARNING, "Cannot autowire: "+e ,e);
				}
			}
		}
		for (Runnable runnable : jobsAfterAutoWire) {
			runnable.run();
		}
		if (rootOp instanceof ProcessRootOperator) {
			return (ProcessRootOperator)rootOp;
		} else {
			throw new XMLException("Outermost operator must be of type 'Process' (<operator class=\"Process\">)");
		}
	}

	private void parseProcess(Element element, ExecutionUnit executionUnit, List<UnknownParameterInformation> unknownParameterInformation) throws XMLException {
		assert("process".equals(element.getTagName()));		
		parseVersion(element);		

		if (element.hasAttribute("expanded")) {
			String expansionString = element.getAttribute("expanded");
			if ("no".equals(expansionString) || "false".equals(expansionString)) {
				executionUnit.setExpanded(false);
			} else if ("yes".equals(expansionString) || "true".equals(expansionString)) {
				executionUnit.setExpanded(true);
			} else {
				throw new XMLException("Expansion mode `" + expansionString + "` is not defined!");
			}
		}

		
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element){
				Element opElement = (Element)child; 
				if ("operator".equals(opElement.getTagName())) {					
					parseOperator(opElement, executionUnit, unknownParameterInformation);
				} else if ("connect".equals(opElement.getTagName())) {
					parseConnection(opElement, executionUnit);
				} else if ("portSpacing".equals(opElement.getTagName())) {
					// ignore, parsed by ProcessRenderer
				} else {
					addMessage("<em class=\"error\">ExecutionUnit must only contain <operator> tags as children. Ignoring unknown tag <code>&lt;"+opElement.getTagName()+"&gt;</code>.</em>");
				}
			}
		}

		ProcessXMLFilterRegistry.fireExecutionUnitImported(executionUnit, element);
	}

	private void parseConnection(Element connectionElement, ExecutionUnit executionUnit) throws XMLException {
		final OutputPorts outputPorts;		
		if (connectionElement.hasAttribute("from_op")) {			
			String fromOp = connectionElement.getAttribute("from_op");
			Operator from = executionUnit.getOperatorByName(fromOp);		
			if (from == null) {
				addMessage("<em class=\"error\">Unkown operator " + fromOp + " referenced in <code>from_op</code>.</em>");
				return;
			}
			outputPorts = from.getOutputPorts();
		} else {			
			outputPorts = executionUnit.getInnerSources();
		}
		String fromPort = connectionElement.getAttribute("from_port");
		OutputPort out = outputPorts.getPortByName(fromPort);		
		if (out == null) {
			addMessage("<em class=\"error\">The output port <var>"+fromPort+"</var> is unknown at operator <var>"+outputPorts.getOwner().getName()+"</var>.</em>");
			return;
		}

		final InputPorts inputPorts;
		if (connectionElement.hasAttribute("to_op")) {
			String toOp = connectionElement.getAttribute("to_op");
			Operator to = executionUnit.getOperatorByName(toOp);		
			if (to == null) {
				addMessage("<em class=\"error\">Unkown operator " + toOp + " referenced in <code>to_op</code>.</em>");
				return;				
			}
			inputPorts = to.getInputPorts();
		} else {
			inputPorts = executionUnit.getInnerSinks();
		}
		String toPort = connectionElement.getAttribute("to_port");
		InputPort in = inputPorts.getPortByName(toPort);
		if (in == null) {
			addMessage("<em class=\"error\">The input port <var>"+toPort+"</var> is unknown at operator <var>"+inputPorts.getOwner().getName()+"</var>.</em>");
			return;
		}
		try {
			out.connectTo(in);
		} catch (PortException e) {
			throw new XMLException(e.getMessage(), e);
		}
	}

	public Operator parseOperator(Element opElement, List<UnknownParameterInformation> unknownParameterInformation) throws XMLException {
		total = opElement.getElementsByTagName("operator").getLength();
		Operator operator = parseOperator(opElement, null, unknownParameterInformation);
		unlockPorts(operator);
		return operator;
	}

	private Operator parseOperator(Element opElement, ExecutionUnit addToProcess, List<UnknownParameterInformation> unknownParameterInformation) throws XMLException {
		assert("operator".equals(opElement.getTagName()));		
		String className = opElement.getAttribute("class");
		String replacement = OperatorService.getReplacementForDeprecatedClass(className);

		if (replacement != null) {			
			addMessage("Deprecated operator '<code>"+className+"</code>' was replaced by '<code>"+replacement+"</code>'.");
			className = replacement;
		}
		OperatorDescription opDescr = OperatorService.getOperatorDescription(className);
		if (opDescr == null) {			
			OperatorDescription[] operatorDescriptions = OperatorService.getOperatorDescriptions(DummyOperator.class);
			if (operatorDescriptions.length == 1) {
				opDescr = operatorDescriptions[0];
				if (className.indexOf(':') == -1) {
					addMessage("<em class=\"error\">The operator class '"+className+"' is unknown.</em>");
				} else {
					addMessage("<em class=\"error\">The operator class '"+className+"' is unknown. Possibly you must install a plugin for operators of group '"+className.substring(0, className.indexOf(':'))+"'.</em>");
				}
			} else {
				throw new XMLException("Unknown operator class: '" + className + "'!");
			}
		}
		Operator operator;
		try {
			operator = opDescr.createOperatorInstance();
			if (operator instanceof DummyOperator) {
				((DummyOperator) operator).setReplaces(className);
			}
			ProcessXMLFilterRegistry.fireOperatorImported(operator, opElement);
			created++;
			if (progressListener != null && total > 0) {			
				progressListener.setCompleted(100 * created / total);
			}
		} catch (OperatorCreationException e) {
			throw new XMLException("Cannot create operator: " + e.getMessage(), e);
		}
		operator.rename(opElement.getAttribute("name"));		

		if (opElement.hasAttribute("breakpoints")) {
			String breakpointString = opElement.getAttribute("breakpoints");
			boolean ok = false;
			if (breakpointString.equals("both")) {
				operator.setBreakpoint(BreakpointListener.BREAKPOINT_BEFORE, true);
				operator.setBreakpoint(BreakpointListener.BREAKPOINT_AFTER, true);				
				ok = true;
			}
			for (int i = 0; i < BreakpointListener.BREAKPOINT_POS_NAME.length; i++) {
				if (breakpointString.indexOf(BreakpointListener.BREAKPOINT_POS_NAME[i]) >= 0) {
					operator.setBreakpoint(i, true);
					ok = true;
				}
			}
			if (!ok) {
				throw new XMLException("Breakpoint `" + breakpointString + "` is not defined!");
			}
		}

		if (opElement.hasAttribute("activated")) {
			String activationString = opElement.getAttribute("activated");
			if ((activationString.equals("no") || activationString.equals("false"))) {
				operator.setEnabled(false);
			} else if (activationString.equals("yes") || activationString.equals("true")) {
				operator.setEnabled(true);
			} else {
				throw new XMLException("Activation mode `" + activationString + "` is not defined!");
			}
		}

		if (opElement.hasAttribute("expanded")) {
			String expansionString = opElement.getAttribute("expanded");
			if ("no".equals(expansionString) || "false".equals(expansionString)) {
				operator.setExpanded(false);
			} else if ("yes".equals(expansionString) || "true".equals(expansionString)) {
				operator.setExpanded(true);
			} else {
				throw new XMLException("Expansion mode `" + expansionString + "` is not defined!");
			}
		}

		if (addToProcess != null) {
			addToProcess.addOperator(operator);
		}

		// parameters and inner operators
		NodeList innerTags = opElement.getChildNodes();
		for (int i = 0; i < innerTags.getLength(); i++) {
			Node node = innerTags.item(i);
			if (node instanceof Element) {
				Element inner = (Element) node;
				if (inner.getTagName().toLowerCase().equals("description")) {
					// first check for old-style <description text="bla"/>
					if (inner.hasAttribute("text")) {
						String descriptionText = inner.getAttribute("text");
						if (version < VERSION_RM_5) {
							// this is only necessary for old versions
							descriptionText = descriptionText.replaceAll("#yquot#", "&quot;");
							descriptionText = descriptionText.replaceAll("#ygt#", ">");
							descriptionText = descriptionText.replaceAll("#ylt#", "<");
						}
						operator.setUserDescription(descriptionText);
						if (version >= VERSION_RM_5) {
							addMessage("The tag &lt;description text=\"TEXT\"&gt; is deprecated. From version 5.0 on, use &lt;description&gt;TEXT&lt;/description&gt;.");
						}
					} else {
						// Then check RM 5.0 <description>bla</description>
						String textContent = inner.getTextContent();
						if ((textContent != null) && (textContent.length() > 0)) {
							operator.setUserDescription(textContent);
							if (version < VERSION_RM_5) {
								addMessage("The tag &lt;description&gt; is missing a text attribute. Using the version 5.0 style XML text content as description text.");	
							}
						} else {
							if (version < VERSION_RM_5) {
								addMessage("The tag &lt;description&gt; is missing a text attribute.");
							}
						}					
					}
				} else if (inner.getTagName().toLowerCase().equals("parameter")) {
					String[] parameter = parseParameter(inner);
					boolean knownType = operator.getParameters().setParameter(parameter[0], parameter[1]);
					if (!knownType) {
						addMessage("The parameter '<code>"+parameter[0]+"</code>' is unknown for operator '<var>"+operator.getName() + "</var>' (<code>"+operator.getOperatorDescription().getName()+"</code>).");
						unknownParameterInformation.add(new UnknownParameterInformation(operator.getName(), operator.getOperatorDescription().getName(), parameter[0], parameter[1]));
					}
				} else if (inner.getTagName().toLowerCase().equals("list")) {
					ListDescription listDescription = parseParameterList(inner);
					boolean knownType = operator.getParameters().setParameter(listDescription.getKey(), ParameterTypeList.transformList2String(listDescription.getList()));
					if (!knownType) {
						addMessage("The parameter '"+listDescription.getKey()+"' is unknown for operator '"+operator.getName() + "' ("+operator.getOperatorDescription().getName()+").");
						unknownParameterInformation.add(new UnknownParameterInformation(operator.getName(), operator.getOperatorDescription().getName(), listDescription.getKey(), listDescription.getList().toString()));
					}
				} else if (inner.getTagName().toLowerCase().equals("enumeration")) {
					boolean knownType = operator.getParameters().setParameter(inner.getAttribute("key"), ParameterTypeEnumeration.transformEnumeration2String(parseParameterEnumeration(inner)));
					if (!knownType) {
						addMessage("The parameter '"+inner.getAttribute("key")+"' is unknown for operator '"+operator.getName() + "' ("+operator.getOperatorDescription().getName()+").");
						unknownParameterInformation.add(new UnknownParameterInformation(operator.getName(), operator.getOperatorDescription().getName(), inner.getAttribute("key"), parseParameterEnumeration(inner).toString()));
					}					
				} else if (inner.getTagName().toLowerCase().equals("description")) {					
					operator.setUserDescription(inner.getAttribute("text"));
				} else if (inner.getTagName().toLowerCase().equals("operator") ||
						inner.getTagName().toLowerCase().equals("process")) {
					if (!(operator instanceof OperatorChain)) {
						addMessage("<em class=\"error\">Operator '<class>"+operator.getOperatorDescription().getName()+"</class>' may not have children. Ignoring.");
					}
					// otherwise, we do the parsing later
				} else {
					addMessage("<em class=\"error\">Ignoring unknown inner tag for <code>&gt;operator&lt;</code>: <code>&lt;" + inner.getTagName()+"&gt;</code>.");
				}
			}
		}		

		if (operator instanceof OperatorChain) {
			OperatorChain nop = (OperatorChain)operator;
			NodeList children = opElement.getChildNodes();
			int subprocessIndex = 0;		
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);				
				if (child instanceof Element){
					Element childProcessElement = (Element)child;
					if ("process".equals(childProcessElement.getTagName()) ||
							"operator".equals(childProcessElement.getTagName())) {
						if (subprocessIndex >= nop.getNumberOfSubprocesses() && !nop.areSubprocessesExtendable()) {
							addMessage("<em class=\"error\">Cannot add child "+childProcessElement.getAttribute("name")+"</var>.</em> Operator <code>"+nop.getOperatorDescription().getName() + "</code> has only "+nop.getNumberOfSubprocesses()+ " subprocesses.");
						} else {
							if (subprocessIndex >= nop.getNumberOfSubprocesses()) {
								// we know nop.areSubprocessesExtendable()==true now
								nop.addSubprocess(subprocessIndex);
							}
							if ("process".equals(childProcessElement.getTagName())) {
								ExecutionUnit subprocess = nop.getSubprocess(subprocessIndex);
								subprocessIndex++;
								parseProcess(childProcessElement, subprocess, unknownParameterInformation);
							} else if ("operator".equals(childProcessElement.getTagName())) {
								if (version >= VERSION_RM_5) {
									addMessage("<em class=\"error\"><code>&lt;operator&gt;</code> as children of <code>&lt;operator&gt</code> is deprecated syntax. From version 5.0 on, use <code>&lt;process&gt;</code> as children.</em>");									
								} else {
									if (!operatorAsDirectChildrenDeprecatedReported) {				
										addMessage("<code>&lt;operator&gt;</code> as children of <code>&lt;operator&gt</code> is deprecated syntax. From version 5.0 on, use <code>&lt;process&gt;</code> as children.");	
										operatorAsDirectChildrenDeprecatedReported = true;
									}
									final ExecutionUnit subprocess = nop.getSubprocess(subprocessIndex);
									if ((subprocessIndex <= nop.getNumberOfSubprocesses() - 2) || (nop.areSubprocessesExtendable())) {
										subprocessIndex++;
									}
									parseOperator(childProcessElement, subprocess, unknownParameterInformation);							
									mustAutoConnect = true;
								}
							}
						}
					}
				}
			}
		}

		if (version < VERSION_RM_5) {
			for (ParseRule rule : PARSE_RULES) {
				String msg = rule.apply(operator, this);
				if (msg != null) {
					addMessage(msg);
				}
			}
		}
		return operator;
	}

	private List<String> parseList(Element parent, String childName) {
		List<String> result = new LinkedList<String>();
		NodeList childNodes = parent.getElementsByTagName(childName);
		switch (childNodes.getLength()) {
		case 0:
			addMessage("Missing &lt;"+childName+"&gt; tag in context.");
			break;
		case 1:
			NodeList locationNodes = ((Element)childNodes.item(0)).getElementsByTagName("location");
			for (int i = 0; i < locationNodes.getLength(); i++) {
				result.add(locationNodes.item(i).getTextContent());
			}
			break;
		default:
			addMessage("&lt;context&gt; can have at most one &lt;"+childName+"&gt; tag.");
			break;
		}
		return result;
	}

	private void parseContext(Element element, Process process) {
		ProcessContext context = process.getContext();
		context.setInputRepositoryLocations(parseList(element, "input"));
		context.setOutputRepositoryLocations(parseList(element, "output"));
		NodeList childNodes = element.getElementsByTagName("macros");
		switch (childNodes.getLength()) {
		case 0:
			addMessage("Missing &lt;macros&gt; tag in context.");
			break;
		case 1:
			NodeList locationNodes = ((Element)childNodes.item(0)).getElementsByTagName("macro");
			for (int i = 0; i < locationNodes.getLength(); i++) {
				Element macroElem = (Element)locationNodes.item(i);
				context.addMacro(new Pair<String,String>(XMLTools.getTagContents(macroElem, "key"), 
						XMLTools.getTagContents(macroElem, "value")));
			}
			break;
		default:
			addMessage("&lt;context&gt; can have at most one &lt;macros&gt; tag.");
			break;
		}
	}

	private ListDescription parseParameterList(Element list) throws XMLException {
		List<String[]> values = new LinkedList<String[]>();
		NodeList children = list.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node instanceof Element) {
				Element inner = (Element) node;
				if (inner.getTagName().toLowerCase().equals("parameter")) {
					values.add(parseParameter(inner));
				} else {
					addMessage("<em class=\"error\">Ilegal inner tag for <code>&lt;list&gt;</code>: <code>&lt;" + inner.getTagName()+"&gt;</code>.</em>");
					return new ListDescription(list.getAttribute("key"), Collections.<String[]>emptyList());
				}
			}
		}
		return new ListDescription(list.getAttribute("key"), values);
	}

	private List<String> parseParameterEnumeration(Element list) throws XMLException {
		List<String> values = new LinkedList<String>();
		NodeList children = list.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node instanceof Element) {
				Element inner = (Element) node;
				if (inner.getTagName().toLowerCase().equals("parameter")) {
					values.add(inner.getAttribute("value"));
				} else {
					addMessage("<em class=\"error\">Ilegal inner tag for <code>&lt;enumeration&gt;</code>: <code>&lt;" + inner.getTagName()+"&gt;</code>.</em>");
					return new LinkedList<String>();
				}
			}
		}
		return values;
	}

	private String[] parseParameter(Element parameter) {
		return new String[] { parameter.getAttribute("key"), parameter.getAttribute("value") };				
	}

	private void parseVersion(Element element) {
		// TODO: parse version properly
		if (element.hasAttribute("version")) {
			String versionString = element.getAttribute("version");
			if (versionString.startsWith("3.")) {
				setVersion(VERSION_RM_3);
			} else if (versionString.startsWith("4.")) {
				setVersion(VERSION_RM_4);
			} else if (versionString.startsWith("5.")) {
				setVersion(VERSION_RM_5);
			} else {
				addMessage("<em class=\"error\">The version "+versionString+" is not a legal RapidMiner version, assuming 4.0.</em>");
				setVersion(VERSION_RM_4);
			}
		}
	}

	private void unlockPorts(Operator operator) {		
		operator.getInputPorts().unlockPortExtenders();
		operator.getOutputPorts().unlockPortExtenders();
		if (operator instanceof OperatorChain) {
			for (ExecutionUnit unit : ((OperatorChain)operator).getSubprocesses()) {				
				unit.getInnerSinks().unlockPortExtenders();
				unit.getInnerSources().unlockPortExtenders();		
				for (Operator child : unit.getOperators()) {
					unlockPorts(child);
				}
			}
		}		
	}

	public void doAfterAutoWire(Runnable runnable) {
		jobsAfterAutoWire.add(runnable);
	}

	public void doAfterTreeConstruction(Runnable runnable) {
		jobsAfterTreeConstruction.add(runnable);
	}

	private boolean hasMessage() {
		return messages.length() > 0;		
	}

	private String getMessage() {		
		return "<html><body><h3>Importing process produced the following messages:</h3><ol>" + messages.toString() + "</ol></body></html>";
	}
}
