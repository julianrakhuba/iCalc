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
package clients;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import application.XML;
import clientcomponents.NColumn;
import clientcomponents.NFunction;
import clientcomponents.NKey;
import clientcomponents.NTable;
import clientcomponents.NType;
import generic.BO;
import generic.BaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import logic.NFormat;

public class XMLBase {
	
	private BaseConnection clientBase;
	private ObservableList<NTable> tables = FXCollections.observableArrayList();
	private ObservableList<NColumn> columns = FXCollections.observableArrayList();
	private ObservableList<NKey> keys = FXCollections.observableArrayList();
	private ObservableList<NType> types = FXCollections.observableArrayList();
	private ObservableList<NFunction> functions = FXCollections.observableArrayList();
	private ObservableList<DataType> ftype = FXCollections.observableArrayList();
	private ObservableMap<String, NFormat> nformats = FXCollections.observableMap(new LinkedHashMap<>());

	
	public XMLBase(BaseConnection clientBase) {
		this.clientBase = clientBase;
		if(!this.openfromFile()) this.openfromFileResources();
		
		
	}

	public List<String> getSchemas() {
		return clientBase.getLogin().getShcemas();
	}
	
	public ObservableList<NTable> getXTables() {
		return tables;
	}

	public ObservableList<NColumn> getXColumns() {
		return columns;
	} 
	
	public ObservableList<NKey> getKeys() {
		return keys;
	}
	
	public ObservableList<NType> getXTypes() {
		return types;
	}
	
	public ObservableList<NFunction> getXFunctions() {
		return functions;
	}
	
	private boolean openfromFile() {		
		File file = new File(clientBase.getNapp().getConfigurationPath() + "NBase_" + clientBase.getLogin().getDbInstance() + ".xml");
		if(file.exists()) {
			try { this.openDoc(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file));
			} catch (SAXException | IOException | ParserConfigurationException e) {e.printStackTrace();}
		}
		return file.exists();
	}
	
	private boolean openfromFileResources() {
		String pathname = getClass().getResource("/Meta_"+clientBase.getLogin().getDb() +".xml").getFile();//OSX ONLY
		File file = new File(pathname);
		if(file.exists()) {
			try { this.openDoc(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file));
			} catch (SAXException | IOException | ParserConfigurationException e) {e.printStackTrace();}
		}
		return file.exists();
	}
	
	public void backup_copy() {
		String dateTime = new SimpleDateFormat("yyyy.MM.dd.k.m").format(new Date());
		this.writeFile(clientBase.getNapp().getConfigurationBackUpPath() + dateTime +"/");
	}

	public void save_existing_or_crate_new() {//Configure And Export
		this.backup_copy();
		this.writeFile(clientBase.getNapp().getConfigurationPath());
	}

	public void writeFile(String dir) {
		try {
			Files.createDirectories(Paths.get(dir));
			File file = new File(dir + "NBase_"+ clientBase.getLogin().getDbInstance() + ".xml");
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		    Element nbaseE = doc.createElement("nbase");
		    doc.appendChild(nbaseE);
		    this.writeDocument(doc, nbaseE);
		    Transformer tf = TransformerFactory.newInstance().newTransformer();
	        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	        tf.setOutputProperty(OutputKeys.INDENT, "yes");
		    tf.transform(new DOMSource(doc), new StreamResult(file));
		}catch(TransformerException | ParserConfigurationException | IOException pce){  pce.printStackTrace();}
	}

	
	private void writeDocument(Document doc, Element nbaseE) {		
		this.write(doc, nbaseE, tables, "ntable");
		this.write(doc, nbaseE, columns, "ncolumn");
		this.write(doc, nbaseE, keys, "nkey");
		this.write(doc, nbaseE, functions, "nfunction");
		this.write(doc, nbaseE, types, "ntype");
		this.write(doc, nbaseE, nformats, "format");
		this.write(doc, nbaseE, ftype, "ftype");
	}
	
	private void write(Document doc, Element nbaseE, ObservableList<?> list, String obj) {
		Element tablesE = doc.createElement(obj +"s");		    	
    	nbaseE.appendChild(tablesE);
    	list.forEach(xtbl ->{
			Element tblE = doc.createElement(obj);
			tablesE.appendChild(tblE);
			((BO) xtbl).saveXml(doc, tblE);
		});
	}
	
	private void write(Document doc, Element nbaseE, ObservableMap<?,?> map, String obj) {
		Element tablesE = doc.createElement(obj +"s");		    	
    	nbaseE.appendChild(tablesE);
    	map.forEach((id,xtbl) ->{
			((BO) xtbl).saveXml(doc, tablesE);
		});
	}
	
	private void openDoc(Document doc) {		
		XML.children(doc).forEach(node ->{
			if(node.getNodeName().equals("nbase")) {
				this.openBase(node);
			}
		});
	}

	private void openBase(Node node) {
		XML.children(node).forEach(sec->{
			if(sec.getNodeName().equals("ntables")) this.openTables(sec);
			if(sec.getNodeName().equals("ncolumns")) this.openColumns(sec);
			if(sec.getNodeName().equals("nfunctions")) this.openFunctions(sec);
			if(sec.getNodeName().equals("nkeys")) this.openKeys(sec);
			if(sec.getNodeName().equals("ntypes")) this.openTypes(sec);
			if(sec.getNodeName().equals("formats")) this.openFormats(sec);
			if(sec.getNodeName().equals("ftypes")) this.openFTypes(sec);
		});
	}


	private void openTables(Node sec) {
		XML.children(sec).forEach(ch->{
			if(ch.getNodeName().equals("ntable")) tables.add(new NTable(ch));
		});
	}
	
	private void openColumns(Node sec) {
		XML.children(sec).forEach(ch->{
			if(ch.getNodeName().equals("ncolumn")) columns.add(new NColumn(ch));
		});		
	}
	
	private void openKeys(Node sec) {
		XML.children(sec).forEach(ch->{
			if(ch.getNodeName().equals("nkey")) keys.add(new NKey(ch));
		});		
	}
	
	private void openFunctions(Node sec) {
		XML.children(sec).forEach(ch->{
			if(ch.getNodeName().equals("nfunction")) functions.add(new NFunction(ch));
		});		
	}
	
	private void openTypes(Node sec) {
		XML.children(sec).forEach(ch->{
			if(ch.getNodeName().equals("ntype")) types.add(new NType(ch));
		});			
	}

	private void openFormats(Node sec) {
		XML.children(sec).forEach(ch->{
			if(ch.getNodeName().equals("format")) {
				NFormat format = new NFormat(ch);
				nformats.put(format.getId(), format);
			}
		});			
	}
	
	private void openFTypes(Node sec) {
		XML.children(sec).forEach(ch->{
			if(ch.getNodeName().equals("ftype")) {
				DataType format = new DataType(ch);
				ftype.add(format);
			}
		});	
	}
	
	public BaseConnection getClientBase() {
		return clientBase;
	}

	public ObservableMap<String, NFormat> getNFormats() {
		return nformats;
	}

	public ObservableList<DataType> getFtype() {
		return ftype;
	}

}
