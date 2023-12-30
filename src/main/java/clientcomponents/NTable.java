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
//import rakhuba.clientcomponents.NTable;
import status.Visability;

public class  NTable  extends BO{
	private String table; 
	private String schema; 
	private Number x; 
	private Number y; 
	private String type; 
	private String label;
	
	private Visability visability;
	
	public NTable(ResultSet rs){
		try {
			schema = rs.getString(rs.findColumn("table_schema"));
			table = rs.getString(rs.findColumn("table_name"));
			type = rs.getString(rs.findColumn("table_type"));
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	public NTable() {

	}

	public NTable(Node ch) {
		this.setSchema(XML.atr(ch, "schema_name") == null? null: XML.atr(ch, "schema_name"));
		this.setTable(XML.atr(ch, "table_name") == null? null: XML.atr(ch, "table_name"));
		this.setX(XML.atr(ch, "x") == null? null: Double.valueOf(XML.atr(ch, "x")));
		this.setY(XML.atr(ch, "y") == null? null: Double.valueOf(XML.atr(ch, "y")));
		this.setType(XML.atr(ch, "table_type") == null? null: XML.atr(ch, "table_type"));
		this.setLabel(XML.atr(ch, "short_table_name") == null? null: XML.atr(ch, "short_table_name"));
		this.setVisability(XML.atr(ch, "visability") == null? Visability.VISIBLE: Visability.valueOf(XML.atr(ch, "visability")));
	}

	public void saveXml(Document doc, Element rowE) {		
		rowE.setAttribute("table_name", table);
		rowE.setAttribute("schema_name", schema);
		rowE.setAttribute("x", x.toString());
		rowE.setAttribute("y", y.toString());
		rowE.setAttribute("table_type", type);
		rowE.setAttribute("short_table_name", label);
		if(visability == null) {
			rowE.setAttribute("visability", Visability.VISIBLE.toString());	//TODO this is temporary default need to redo	
		}else {
			rowE.setAttribute("visability", visability.toString());		
		}
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public Number getX() {
		return x;
	}

	public void setX(Number x) {
		this.x = x;
	}

	public Number getY() {
		return y;
	}

	public void setY(Number y) {
		this.y = y;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String toString(){return "{" + ", " + "table: " + table + ", " + "schema: " + schema + ", " + "x: " + x + ", " + "y: " + y + ", " + "table_type: " + type + ", " + "label: " + label + "}";}

	public boolean eq(NTable tb) {
		return tb.getSchema().equals(schema) && tb.getTable().equals(table);
	}

	public Visability getVisability() {
		return visability;
	}

	public void setVisability(Visability visability) {
		this.visability = visability;
	}
	
}