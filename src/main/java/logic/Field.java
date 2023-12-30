/*******************************************************************************
 *
 * Copyright (c) 2023 Constellatio BI
 *  
 * This software is released under the [Educational/Non-Commercial License or Commercial License, choose one]
 *  
 * Educational/Non-Commercial License (GPL):
 *  
 * Permission is hereby granted, free of charge, to any person or organization
 * obtaining a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute,
 * sub-license, and/or sell copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *  
 * THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *  
 * Commercial License:
 *  
 * You must obtain a separate commercial license if you wish to use this software for commercial purposes. 
 * Please contact me at 916-390-9979 or rakhuba@gmail.com for licensing information.
 *******************************************************************************/
package logic;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import activity.View;
import application.XML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import layer.LAY;
import pivot.FieldMenu;
import pivot.NSelector;
import pivot.FieldVersion;

public class Field {
	private LAY fieldLay;
	public NSelector select = new NSelector();
	public NSelector pivot = new NSelector("pivot");
	public NSelector group = new NSelector("group");
	public NSelector agrigate = new NSelector("value");
	
	private ObservableList<Join> joins = FXCollections.observableArrayList();
	protected ArrayList<String> pivotCache = new ArrayList<String>();
	
	private String aliase;
	private String sql_column_name;
	private String schema_name;
	private String table_name;
	private String column_name;
	private String rowset_type;
	private String column_key;
	private FieldMenu fieldMenu;
	private FieldVersion parentVersion;
	protected NFormat nFormat;
	
			
	public Field(LAY fieldLay) {
		this.fieldLay = fieldLay;
		nFormat = fieldLay.getNnode().getNmap().getNapp().getDBManager().getActiveConnection().getXMLBase().getNFormats().get("plain");
		select.getLabel().setOnMouseClicked(e -> this.getView().selectFieldClick(this));	
		pivot.getLabel().setOnMouseClicked(e -> this.getView().selectPivotFieldClick(this));
		group.getLabel().setOnMouseClicked(e -> this.getView().selectGroupFieldClick(this));
		agrigate.getLabel().setOnMouseClicked(e -> this.getView().selectAgrigateFieldClick(this));
	}

	public void setSelected(boolean status) {
		select.set(status);
	}

	public boolean isSelected() {
		return select.get();
	}

	public void setGroupBy(boolean status) {
		group.set(status);
	}

	public boolean isGroupBy() {
		return group.get();
	}

	public void setPivot(boolean status) {
		this.pivot.setValue(status);
	}
	
	public void setAgrigated(boolean status) {
		this.agrigate.setValue(status);
	}

	public boolean isPivot() {
		return this.pivot.getValue();
	}
	
	public boolean isAgrigated() {
		return this.agrigate.get();
	}

	public LAY getFieldLay() {
		return fieldLay;
	}

	public void setAliase(String aliase) {
		this.aliase = aliase;
	}

	public String getAliase() {
		return aliase;
	}

	public String getLabelText() {
		return select.getLabel().getText();
	}

	public void setText(String label) {
		select.getLabel().setText(label);
	}

	public String getSQL_Column_name() {
		return sql_column_name;
	}

	public void setSQL_Column_name(String sql_column_name) {
		this.sql_column_name = sql_column_name;
	}
	
	public String getFunction_Column() {
		return fieldLay.getAliase()  + "." + sql_column_name;
	}

	public ArrayList<String> getPivotCache() {
		return pivotCache;
	}

	public String getRowset_type() {
		return rowset_type;
	}

	public void setRowset_type(String rowset_type) {
		this.rowset_type = rowset_type;
	}

	public String getSchema() {
		return schema_name;
	}

	public void setSchema(String schema_name) {
		this.schema_name = schema_name;
	}

	public String getTable() {
		return table_name;
	}

	public void setTable(String table_name) {
		this.table_name = table_name;
	}

	public String getColumn() {
		return column_name;
	}

	public void setColumn(String column_name) {
		this.column_name = column_name;
	}
	
	public String   getColumn_key(){
		return column_key;
	}
	
	public void setColumn_key(String column_key) {
		this.column_key = column_key;
	}

	public void addJoin(Join joinCON) {	
		joins.add(joinCON);
	}

	public ObservableList<Join> getJoins() {
		return joins;
	}

	public boolean isBlank() {
		return !this.isGroupBy() && !this.isPivot() && !this.isAgrigated();
	}

	public FieldMenu getOrCreateFieldMenu() {
		if(fieldMenu == null) fieldMenu = new FieldMenu(this); 
		return fieldMenu;
	}

	public void setParentVersion(FieldVersion v) {
		parentVersion = v;
	}

	public FieldVersion getParentVersion() {
		return parentVersion;
	}

	public ArrayList<String> getValuesList(String func_full_name) {		
		return fieldLay.getValuesList(func_full_name, this.getFunction_Column(), sql_column_name);
	}
	 
	
	private Field getRootField() {
		if(parentVersion == null) {
			if(!(this instanceof FormulaField)) {
				return this;
			}else {
				return null;
			}
		}else {
			if(parentVersion.getPivotField() != null) {
				return parentVersion.getPivotField().getRootField();
			}else {
				return parentVersion.getField().getRootField();
			}
		}
	}
	
	public String  getColumnId() {
		return  this.getSchema() + "." + this.getTable() + "." + this.getColumn();
	}
	
	public void saveXml(Document document, Element fieldsE) {
		Element fieldE = document.createElement("field");
		fieldE.setAttribute("fieldType", "GENERIC");	
		fieldE.setAttribute("schema_name", this.getSchema());
		fieldE.setAttribute("table_name", this.getTable());
		fieldE.setAttribute("column_name", this.getColumn());
		fieldE.setAttribute("rowset_type", this.getRowset_type());
		fieldE.setAttribute("nculumnId", this.getSchema() + this.getTable() + this.getColumn());
		fieldE.setAttribute("sql_column_name", this.getSQL_Column_name());
		fieldE.setAttribute("label", this.getLabelText());
		fieldE.setAttribute("aliase", this.getAliase());
		fieldE.setAttribute("select", this.isSelected() + "");
		fieldE.setAttribute("group", this.isGroupBy() + "");
		fieldE.setAttribute("pivot", this.isPivot() + "");
		fieldE.setAttribute("agrigate", this.isAgrigated() + "");
		fieldE.setAttribute("column_key", this.getColumn_key());
		fieldE.setAttribute("format_id", this.getFormat().getId());

		if(parentVersion !=null) {
			fieldE.setAttribute("parentVersionAliase", parentVersion.getAliase());//IF NOT NULL
		}
		
		Field root = this.getRootField();
		if(root != null) {
			fieldE.setAttribute("rootFieldColumnId", root.getColumnId());//IF NOT NULL
		}
		
		fieldsE.appendChild(fieldE);		
		
		Element pivotChacheE = document.createElement("pivotChache");
		fieldE.appendChild(pivotChacheE);
		
				
		pivotCache.forEach(pch -> {
			Element headerE = document.createElement("header");
			headerE.setAttribute("name", pch);
			pivotChacheE.appendChild(headerE);
		});
		
		Element fieldJoins = document.createElement("fieldJoins");
		fieldE.appendChild(fieldJoins);
		joins.forEach(join -> {
			Element joinE = document.createElement("join");
			joinE.setAttribute("sql_column", join.getSqlColumn());						
			joinE.setAttribute("schema", join.getSchema());
			joinE.setAttribute("table", join.getTable());
			joinE.setAttribute("column", join.getColumn());
			joinE.setAttribute("remote_schema", join.getRemoteSchema());
			joinE.setAttribute("remote_table", join.getRemoteTable());
			joinE.setAttribute("remote_column", join.getRemoteColumn());
			fieldJoins.appendChild(joinE);
		});
	}



	public void loopAChache(Node nn) {
		List<Node> nodes = XML.children(nn);
		nodes.forEach(n2 ->{
			if(n2.getNodeName().equals("pivotChache")) {
				XML.children(n2).forEach(pc ->{
					if(pc.getNodeName().equals("header")) {
						this.getPivotCache().add(XML.atr(pc, "name"));
					}
				});
			}
		});
	}
	
	public void loopAJoins(Node nn) {
		List<Node> nodes = XML.children(nn);
		nodes.forEach(n2 ->{
			if(n2.getNodeName().equals("fieldJoins")) {
				//TODO MAYBE DON"T NEED THIS??? PIVOT CHACHE ONLY??
				XML.children(n2).forEach(jn ->{
					if(jn.getNodeName().equals("join")) {
						Join join = new Join(this.getFieldLay(), XML.atr(jn, "sql_column"), XML.atr(jn, "schema"), XML.atr(jn, "table"), XML.atr(jn, "column"), XML.atr(jn, "remote_schema"), XML.atr(jn, "remote_table"), XML.atr(jn, "remote_column"));
						this.addJoin(join);
					}
				});
			}
		});
	}
	
	public void loopAFormat(Node nn) {
		this.nFormat = fieldLay.getNnode().getNmap().getNapp().getDBManager().getActiveConnection().getXMLBase().getNFormats().get(XML.atr(nn, "format_id"));
	}
	
	private View getView() {
		return ((View) fieldLay.getNnode().getNmap().getNapp().getFilemanager().getActiveNFile().getActivity());
	}
	
	public Label getSelectLabel() {	
		return  select.getLabel();
	}
	
	public Label getPivotLabel() {	
		return  pivot.getLabel();
	}
	
	public Label getGroupLabel() {	
		return  group.getLabel();
	}
	
	public Label getAgrigateLabel() {	
		return  agrigate.getLabel();
	}
	
	public boolean isNumber() {
		if(rowset_type.equals("Double") || rowset_type.equals("Float") || rowset_type.equals("Int") || rowset_type.equals("BigDecimal")) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isString() {
		return rowset_type.equals("String") ? true : false;		
	}
	
	public boolean isTime() {
		return rowset_type.equals("Time") ? true : false;		
	}
		
	public boolean isTimestamp() {
		return rowset_type.equals("Timestamp") ? true : false;		
	}
	
	public boolean isDate() {
		return rowset_type.equals("Date") ? true : false;		
	}
	
	public boolean isExcludedType() {
		return rowset_type.equals("exclude") ? true : false;		
	}
	
	
	public boolean isDouble() {
		return rowset_type.equals("Double") ? true : false;		
	}
	
	public boolean isInt() {
		return rowset_type.equals("Int") ? true : false;		
	}
	
	public boolean isFloat() {
		return rowset_type.equals("Float") ? true : false;		
	}
	
	public boolean isBigDecimal() {
		return rowset_type.equals("BigDecimal") ? true : false;		
	}

	public NFormat getFormat() {
		return nFormat;
	}

	public void setFormat(NFormat nFormat) {
		this.nFormat = nFormat;
	}
}
