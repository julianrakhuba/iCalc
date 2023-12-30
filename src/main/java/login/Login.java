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
package login;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import application.XML;

public class Login {	
	private String db;
	private String instance;
	private String label;
	private String username;
	private String password;
	private String url;
	private ArrayList<String> shcemas = new ArrayList<String>();
	private ArrayList<String> statements = new ArrayList<String>();
	
	public Login(Node conN) {		
		db = XML.atr(conN, "db");
		instance = XML.atr(conN, "instance");
		label = XML.atr(conN, "label");
		username = XML.atr(conN, "username");
		password = XML.atr(conN, "password");
		url = XML.atr(conN, "url");
		
		XML.children(conN).forEach(nn ->{
			if(nn.getNodeName().equals("schemas")) {
				XML.children(nn).forEach(sc ->{
					if(sc.getNodeName().equals("schema")) {
						shcemas.add(XML.atr(sc, "name"));
					}
				});					
			}else if(nn.getNodeName().equals("statements")) {
				XML.children(nn).forEach(sc ->{
					if(sc.getNodeName().equals("statement")) {
						statements.add(XML.atr(sc, "command"));
					}
				});					
			}
		});
	}

	public Login() {
		
	}

	public String getLabel() {
		return label;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String toString() {
		return label;//TODO NEED THIS ON LOGIN PAGE
	}

	public void saveToXml(Element docE) {
		Element loginE = docE.getOwnerDocument().createElement("connection");		    	
    	loginE.setAttribute("db", db);
    	loginE.setAttribute("instance", instance);
    	loginE.setAttribute("label", label);
    	loginE.setAttribute("username", username);
    	loginE.setAttribute("password", password);
    	loginE.setAttribute("url", url);
    	docE.appendChild(loginE);	
    	
    	// Schemas
    	Element schs = loginE.getOwnerDocument().createElement("schemas");
		loginE.appendChild(schs);	
    	shcemas.forEach(str ->{
    		Element sch = loginE.getOwnerDocument().createElement("schema");	    	
    		sch.setAttribute("name", str);
    		schs.appendChild(sch);	
    	});
 
     	Element stsE = loginE.getOwnerDocument().createElement("statements");
		loginE.appendChild(stsE);	
		statements.forEach(str ->{
    		Element st = loginE.getOwnerDocument().createElement("statement");	    	
    		st.setAttribute("command", str);
    		stsE.appendChild(st);	
    	});    	
	}

	public ArrayList<String> getShcemas() {
		return shcemas;
	}

	public ArrayList<String> getStatements() {
		return statements;
	}
	
	public String getDb() {
		return db;
	}
	

	public String getInstance() {
		return instance;
	}
	
	public String getDbInstance() {
		return db + instance;
	}
	
	public void sampleFill() {
		db = "dbname";
		instance = "1";
		label = "blank login file";
		username = "please don't connect cuz I will crash :) ";
		password = "password";
		url = "url";
		shcemas.add("schemaSample");
		statements.add("statementSample");
	}
}
