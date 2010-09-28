package com.rapidminer.operator.nio;

/** The meta data either guessed by RapidMiner or specified by the user for a
 *  column of an excel file, csv file, etc.
 * 
 * @author Simon Fischer
 *
 */
public class ColumnMetaData {

	/** Attribute name as specified in the file, or generated by the source. */
	private String originalAttributeName;
	/** Attribute name as specified by the user. */
	private String userDefinedAttributeName;
	private int attributeValueType;
	private String role;
	private boolean selected;
	
	
	public ColumnMetaData() {
		
	}
	
	public ColumnMetaData(String originalAttributeName, String userDefinedAttributeName, int attributeValueType, String role, boolean selected) {
		super();
		this.originalAttributeName = originalAttributeName;
		this.userDefinedAttributeName = userDefinedAttributeName;
		this.attributeValueType = attributeValueType;
		this.role = role;
		this.selected = selected;
	}
	public String getOriginalAttributeName() {
		return originalAttributeName;
	}
	public void setOriginalAttributeName(String originalAttributeName) {
		this.originalAttributeName = originalAttributeName;
	}
	public String getUserDefinedAttributeName() {
		return userDefinedAttributeName;
	}
	public void setUserDefinedAttributeName(String userDefinedAttributeName) {
		this.userDefinedAttributeName = userDefinedAttributeName;
	}
	public int getAttributeValueType() {
		return attributeValueType;
	}
	public void setAttributeValueType(int attributeValueType) {
		this.attributeValueType = attributeValueType;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}