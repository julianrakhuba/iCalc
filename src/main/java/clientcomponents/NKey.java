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
package clientcomponents; //

import java.sql.ResultSet;
import java.sql.SQLException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import application.XML;
import generic.BO;

public class  NKey  extends BO{
	public String constraint_name; 
	public String schema_name; 
	public String table_name; 
	public String column_name; 
	public String referenced_schema_name; 
	public String referenced_table_name; 
	public String referenced_column_name; 
		
	public NKey(){

	}
	
	public NKey(String constraint,String table_schema, String table_name, String column_name, String ref_table_schema, String ref_table_name,String ref_column_name) {
		this.constraint_name = constraint;
		this.schema_name = table_schema;
		this.table_name = table_name;
		this.column_name = column_name;
		this.referenced_schema_name = ref_table_schema;
		this.referenced_table_name = ref_table_name;
		this.referenced_column_name = ref_column_name;
	}
	
	public NKey(ResultSet rs){
		try {
			constraint_name = rs.getString(rs.findColumn("constraint"));
			schema_name = rs.getString(rs.findColumn("table_schema"));
			table_name = rs.getString(rs.findColumn("table_name"));
			column_name = rs.getString(rs.findColumn("column_name"));
			referenced_schema_name = rs.getString(rs.findColumn("ref_table_schema"));
			referenced_table_name = rs.getString(rs.findColumn("ref_table_name"));
			referenced_column_name = rs.getString(rs.findColumn("ref_column_name"));
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	public NKey(Node ch) {
		this.setConst(XML.atr(ch, "constraint_name") == null? null: XML.atr(ch, "constraint_name"));
		this.setSchema(XML.atr(ch, "schema_name") == null? null: XML.atr(ch, "schema_name"));
		this.setTable(XML.atr(ch, "table_name") == null? null: XML.atr(ch, "table_name"));
		this.setColumn(XML.atr(ch, "column_name") == null? null: XML.atr(ch, "column_name"));
		this.setRSchema(XML.atr(ch, "referenced_schema_name") == null? null: XML.atr(ch, "referenced_schema_name"));
		this.setRTable(XML.atr(ch, "referenced_table_name") == null? null: XML.atr(ch, "referenced_table_name"));
		this.setRColumn(XML.atr(ch, "referenced_column_name") == null? null: XML.atr(ch, "referenced_column_name"));
	}

	public void saveXml(Document doc, Element rowE) {
		rowE.setAttribute("constraint_name", constraint_name);
		rowE.setAttribute("schema_name", schema_name);
		rowE.setAttribute("table_name", table_name);
		rowE.setAttribute("column_name", column_name);
		rowE.setAttribute("referenced_schema_name", referenced_schema_name);
		rowE.setAttribute("referenced_table_name", referenced_table_name);
		rowE.setAttribute("referenced_column_name", referenced_column_name);
	}
	
	public String getConst() {
		return constraint_name;
	}

	public void setConst(String constraint_name) {
		this.constraint_name = constraint_name;
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

	public String getRSchema() {
		return referenced_schema_name;
	}

	public void setRSchema(String referenced_schema_name) {
		this.referenced_schema_name = referenced_schema_name;
	}

	public String getRTable() {
		return referenced_table_name;
	}

	public void setRTable(String referenced_table_name) {
		this.referenced_table_name = referenced_table_name;
	}

	public String getRColumn() {
		return referenced_column_name;
	}

	public void setRColumn(String referenced_column_name) {
		this.referenced_column_name = referenced_column_name;
	}

	public String toString(){return "{" + ", " + "constraint_name: " + this.constraint_name + ", " + "schema_name: " + this.schema_name + ", " + "table_name: " + this.table_name + ", " + "column_name: " + this.column_name + ", " + "referenced_schema_name: " + this.referenced_schema_name + ", " + "referenced_table_name: " + this.referenced_table_name + ", " + "referenced_column_name: " + this.referenced_column_name + "}";}
	
	public boolean eq(NKey k) {
		return k.getConst().equals(this.getConst()) 
				&& k.getSchema().equals(this.getSchema()) 
				&& k.getTable().equals(this.getTable()) 
				&& k.getColumn().equals(this.getColumn());
	}

	public boolean isKeyColumn(NColumn c) {
		return  c.getSchema().equalsIgnoreCase(schema_name)  && c.getTable().equalsIgnoreCase(table_name) && c.getColumn().equalsIgnoreCase(column_name);
	}
	
	public boolean isKeyRefColumn(NColumn c) {
		return  c.getSchema().equalsIgnoreCase(referenced_schema_name)  && c.getTable().equalsIgnoreCase(referenced_table_name) && c.getColumn().equalsIgnoreCase(referenced_column_name);
	}
	
}