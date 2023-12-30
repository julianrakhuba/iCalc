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
package clientcomponents;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import application.XML;
import generic.BO;

public class  NFunction  extends BO{
	public String label; 
	public String open; 
	public String close; 
	public String type; 
	public String realname; 
	public String openParam; 
	public String closeParam;
	public String rowset_type = "String"; 

	
	public void saveXml(Document doc, Element rowE) {
		rowE.setAttribute("label", label);
		rowE.setAttribute("open", open);
		rowE.setAttribute("close", close);
		rowE.setAttribute("type", type);
		rowE.setAttribute("realname", realname);
		rowE.setAttribute("openParam", openParam);
		rowE.setAttribute("closeParam", closeParam);
		rowE.setAttribute("rowset_type", rowset_type);
	}
	
	public NFunction(Node ch) {
		this.setLabel(XML.atr(ch, "label") == null? null: XML.atr(ch, "label"));
		this.setOpen(XML.atr(ch, "open") == null? null: XML.atr(ch, "open"));
		this.setClose(XML.atr(ch, "close") == null? null: XML.atr(ch, "close"));
		this.setType(XML.atr(ch, "type") == null? null: XML.atr(ch, "type"));
		this.setRealname(XML.atr(ch, "realname") == null? null: XML.atr(ch, "realname"));
		this.setOpenParam(XML.atr(ch, "openParam") == null? null: XML.atr(ch, "openParam"));
		this.setCloseParam(XML.atr(ch, "closeParam") == null? null: XML.atr(ch, "closeParam"));
		this.setRowset_type(XML.atr(ch, "rowset_type") == null? null: XML.atr(ch, "rowset_type"));
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getClose() {
		return close;
	}

	public void setClose(String close) {
		this.close = close;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getOpenParam() {
		return openParam;
	}

	public void setOpenParam(String openParam) {
		this.openParam = openParam;
	}

	public String getCloseParam() {
		return closeParam;
	}

	public void setCloseParam(String closeParam) {
		this.closeParam = closeParam;
	}
	
	public String toString(){return "{" + ", " + "label: " + label + ", " + "open: " + open + ", " + "close: " + close + ", " + "type: " + type + ", " + "realname: " + realname + ", " + "openParam: " + openParam + ", " + "closeParam: " + closeParam + "}";}

	public String getRowset_type() {
		return rowset_type;
	}

	public void setRowset_type(String rowset_type) {
		this.rowset_type = rowset_type;
	}

	
}