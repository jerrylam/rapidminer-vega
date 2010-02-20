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
package com.rapidminer.repository.gui.process;

import java.awt.Component;
import java.text.DateFormat;
import java.util.Calendar;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.rapid_i.repository.wsimport.ProcessResponse;
import com.rapid_i.repository.wsimport.ProcessStackTraceElement;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.repository.RemoteProcessState;
import com.rapidminer.repository.remote.RemoteRepository;
import com.rapidminer.tools.Tools;

/**
 * 
 * @author Simon Fischer
 *
 */
public class RemoteProcessTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private Icon PROCESS_PENDING_ICON = SwingTools.createIcon("16/gear_new.png");
	private Icon PROCESS_RUNNING_ICON = SwingTools.createIcon("16/gear_run.png");
	private Icon PROCESS_STOPPED_ICON = SwingTools.createIcon("16/gear_stop.png");
	private Icon PROCESS_FAILED_ICON = SwingTools.createIcon("16/gear_error.png");
	private Icon PROCESS_DONE_ICON = SwingTools.createIcon("16/gear_ok.png");
	
	private Icon SERVER_ICON = SwingTools.createIcon("16/application_server_run.png");	
	private Icon OPERATOR_ICON = SwingTools.createIcon("16/element_selection.png");
	private Icon RESULT_ICON = SwingTools.createIcon("16/plug_new_next.png");
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value instanceof RemoteRepository) {
			label.setText(((RemoteRepository) value).getName());
			label.setIcon(SERVER_ICON);
		} else if (value instanceof ProcessResponse) {
			ProcessResponse processResponse = (ProcessResponse) value;
			RemoteProcessState processState = RemoteProcessState.valueOf(processResponse.getState());			
			Calendar startTime = processResponse.getStartTime() != null ? processResponse.getStartTime().toGregorianCalendar() : null;
			Calendar endTime = processResponse.getCompletionTime() != null ? processResponse.getCompletionTime().toGregorianCalendar() : null;
			
			StringBuilder b = new StringBuilder();
			b.append("<html><body>");
			b.append(""+processResponse.getProcessLocation());
			b.append(" <small style=\"color:gray\">(");
			if (processState != RemoteProcessState.COMPLETED) { // completed is obvious from completion time
				if (processState == RemoteProcessState.FAILED) {
					b.append("<span style=\"color:red;font-weight:bold;\">");
				}
				b.append(processState.toString().toLowerCase()).append("; ");
				if (processState == RemoteProcessState.FAILED) {
					b.append("</span>");
				}
			}
			if (startTime != null) {
				b.append("started ");
				b.append(DateFormat.getDateTimeInstance().format(startTime.getTime()));
			}
			if (endTime != null) {
				b.append("; completed ");
				b.append(DateFormat.getDateTimeInstance().format(endTime.getTime()));
			}
			b.append(")</small>");
			b.append("</body></html>");
			label.setText(b.toString());
			
			switch (processState) {
			case COMPLETED:
				label.setIcon(PROCESS_DONE_ICON);
				break;
			case FAILED:
				label.setIcon(PROCESS_FAILED_ICON);
				break;
			case RUNNING:
				label.setIcon(PROCESS_RUNNING_ICON);
				break;
			case PENDING:
				label.setIcon(PROCESS_PENDING_ICON);
				break;
			case STOPPED:
				label.setIcon(PROCESS_STOPPED_ICON);
				break;
			}			
		} else if (value instanceof ProcessStackTraceElement) {
			ProcessStackTraceElement element = (ProcessStackTraceElement) value;
			label.setText(element.getOperatorName() + " ["+element.getApplyCount()+", "+Tools.formatDuration(element.getExecutionTime())+"]");
			label.setIcon(OPERATOR_ICON);
		} else if (value instanceof OutputLocation) {
			label.setText(value.toString());
			label.setIcon(RESULT_ICON);
		} else {
			label.setText(value.toString());
		}
		return label;
	}
}
