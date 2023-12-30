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
package file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

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

import activity.Calculation;
import activity.Configure;
import activity.Edit;
import activity.Select;
import activity.View;
import application.XML;
import generic.ACT;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import managers.FileManager;
import managers.NSidePane;
import managers.TabManager;
import managers.UndoManager;
import map.NMap;
import sidePanel.HeadingLabel;
import sidePanel.Message;
import status.ActivityMode;
import status.VisualStatus;


public class NFile  {
	private File file;
	private HashMap<String, NMap> maps = new HashMap<String, NMap>();
	private UndoManager undoManager = new UndoManager(this);
	private boolean isNewFile;	
	private TabManager tabManager;
	private NSidePane sidePane;
	private NMap activeNmap;
	private FileManager fileManager;
	private ObservableList<Region> messagesSideVBox = FXCollections.observableArrayList();
	private Property<ActivityMode> mode = new SimpleObjectProperty<ActivityMode>(ActivityMode.SELECT);
	private HashMap<ActivityMode, ACT> activities = new HashMap<ActivityMode, ACT>();
	private ObservableList<Message> messages = FXCollections.observableArrayList();
	private VBox list = new VBox(10);
	private Pane messagesLbl = new HeadingLabel("to-do","#ade0ff");
	
	private StackPane glassStackPane = new StackPane();

	private CenterMessage centerMessage;

	//new split
	private QuadSplit quadSplit;
	
	public NFile(File file, FileManager fileManager) {
		this.file = file;
		this.fileManager = fileManager;
		quadSplit = new QuadSplit(this);
		centerMessage = new CenterMessage(this);
		tabManager = new TabManager(this);
		sidePane = new NSidePane(this);
		activities.put(ActivityMode.SELECT, new Select(this)); 
		activities.put(ActivityMode.VIEW, new View(this));
		activities.put(ActivityMode.EDIT, new Edit(this));
		activities.put(ActivityMode.CONFIGURE, new Configure(this));
		activities.put(ActivityMode.FORMULA, new Calculation(this));
		

		getGlassStackPane().setAlignment(Pos.CENTER);
		if(fileManager.getNapp().getStage().getStyle() == StageStyle.TRANSPARENT) {
			getGlassStackPane().setStyle("-fx-background-color: transparent; -fx-padding: 5 5 5 5;");	
		}else {
			getGlassStackPane().setStyle("-fx-effect: dropshadow(two-pass-box , white, 5, 0.4 , -2, -2); -fx-background-color: transparent; -fx-padding: 5 5 5 5;");
		}

		getGlassStackPane().setPickOnBounds(false);
		getGlassStackPane().setMinWidth(0);

		quadSplit.setTopLeft(getGlassStackPane());
		messagesSideVBox.addAll(messagesLbl, list);
		
		messages.addListener((ListChangeListener<? super Message>) c -> {
			if(c.next()) {
				c.getAddedSubList().forEach(jl -> {
					list.getChildren().add(jl.getLabel());
				});
				c.getRemoved().forEach(jl -> {
					list.getChildren().remove(jl.getLabel());
				});
			}
		});

		this.addMessage(new Message(this, "", "security, rolls"));
		this.addMessage(new Message(this, "", "disc joins on shcema delete"));
		this.addMessage(new Message(this, "", "func fld labl update"));
		this.addMessage(new Message(this, "", "clear search when deleted condition"));
	}	
	
	public UndoManager getUndoManager() {
		return undoManager;
	}

	public NSidePane getSidePane() {
		return sidePane;
	}
	
	public FileManager getFileManager() {
		return fileManager;
	}
	
	public NMap createNewMap(String schema) {
		NMap nmap = new NMap(this,schema);
		maps.put(schema, nmap);
		this.showNmap(schema);
		return nmap;
	}
	
	//************************************************************************ LOGIC STACK PANE
	public void showNmap(String schema) {
		NMap nmap = maps.get(schema);
		if(activeNmap !=null) {
			getGlassStackPane().getChildren().clear();
		}
		activeNmap = nmap;		
		getGlassStackPane().getChildren().addAll(nmap.getScrollPane(), centerMessage);		
		this.setAppTitle();
	}
	
	public void refreshTempFixForOffsetIssue() {
		if(activeNmap !=null) {
			getGlassStackPane().getChildren().clear();
			getGlassStackPane().getChildren().addAll(activeNmap.getScrollPane(), centerMessage);
		}
	}
	
	public void removeSchema(String schema) {
		NMap nmapToRRemove = maps.get(schema);
		maps.remove(schema);
		tabManager.removeNSheetFor(nmapToRRemove);
		
		if(activeNmap == nmapToRRemove ) {
			getGlassStackPane().getChildren().clear();

//			logicGlassSP.getChildren().remove(activeNmap.schemaScrollPane);
			//REMOVE SHEETS
			//REMOVE SEARCHES
			//disconnect from other logics
			this.showNmap(maps.keySet().stream().findFirst().get());
		}else {
			//REMOVE SHEETS
			//REMOVE SEARCHES
			//disconnect from other logics
		}		
	}
//************************************************************************
	
	
	public void setAppTitle() {
		if(activeNmap != null) {
			fileManager.getNapp().setTitle(this.getXMLFile().getName() + " (" + activeNmap.getSchemaName() +")");
		}else {
			fileManager.getNapp().setTitle(this.getXMLFile().getName() + " ()");
		}
	}
	
	public NMap getActiveNmap() {
		return activeNmap;
	}
	public HashMap<String, NMap> getMaps() {
		return maps;
	}

	public File getXMLFile() {
		return file;
	}
	
	public void setZIP(File file) {
		this.file = file;
	}
	
	public boolean isNewFile() {
		return isNewFile;
	}

	public void setNewFile(boolean newFile) {
		this.isNewFile = newFile;
	}
	
	public void openFile() {
		this.fileManager.selectNFile(this);
		String extension = "";
		if (file.getName().contains("."))   extension = file.getName().substring(file.getName().lastIndexOf("."));
		
		if(extension.equals(".xml")) {//XML
			try {this.openNewDocument(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file));}
			catch (SAXException | IOException | ParserConfigurationException e) {e.printStackTrace();}
		}else if(extension.equals(".con")) {//CON
			try {
				ZipFile zf = new ZipFile (file);
				InputStream in = zf.getInputStream(zf.getEntry("document.xml"));
				try {this.openNewDocument(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in));}
				catch (SAXException | IOException | ParserConfigurationException e) {e.printStackTrace();}
				zf.close();
			} catch (IOException e) {e.printStackTrace();}
		}	       
	}

	public void openNewDocument(Document doc) {
		
		XML.children(doc).forEach(n->{
			if(n.getNodeName().equals("NFile")) {
				this.openNewRoot(n);
			}
		});
		if(tabManager.getStatus() == VisualStatus.SHOW) tabManager.showGrid();
		undoManager.saveUndoAction();
	}
	
	private void openNewRoot (Node rootX) {
		OpenContext context = new OpenContext();
		List<Node> nodes = XML.children(rootX);
		nodes.forEach(n ->{
			if(n.getNodeName().equals("configuration")) {
				tabManager.setStatus(VisualStatus.valueOf(XML.atr(n, "gridVisablity")));
				sidePane.setStatus(VisualStatus.valueOf(XML.atr(n, "searchVisability")));
			}
		});
		
		nodes.forEach(n ->{
			if(n.getNodeName().equals("schemas")) {
				this.openNewSchemas(context, n);
			}
		});
	}
	
	private void openNewSchemas (OpenContext context, Node schemasX) {
		List<Node> nodes = XML.children(schemasX);
		nodes.forEach(n ->{
			if(n.getNodeName().equals("schema")) {
				NMap newMap = this.createNewMap(XML.atr(n, "schemaName"));
				newMap.loopA(context, n);
			}
		});
		
		nodes.forEach(n ->{
			if(n.getNodeName().equals("schema")) {
				maps.get(XML.atr(n, "schemaName")).loopB(context, n);
			}
		});
	}
	
	private void openUndoSchemas (OpenContext context, Node schemasX) {
		List<Node> nodes = XML.children(schemasX);
		nodes.forEach(n ->{
			if(n.getNodeName().equals("schema")) {
				maps.get(XML.atr(n, "schemaName")).loopA(context, n);
			}
		});
		
		nodes.forEach(n ->{
			if(n.getNodeName().equals("schema")) {
				maps.get(XML.atr(n, "schemaName")).loopB(context, n);
			}
		});
	}
	
	public void openUndoDocument(Document doc) {
		maps.forEach((s, m) -> m.clear());//Clear all maps of this file
		XML.children(doc).forEach(n->{
			if(n.getNodeName().equals("NFile")) {
				this.openUndoRoot(n);
			}
		});
		if(tabManager.getStatus() == VisualStatus.SHOW) tabManager.showGrid();
	}
	
	private void openUndoRoot (Node rootX) {
		OpenContext context = new OpenContext();
		
		List<Node> nodes = XML.children(rootX);
		nodes.forEach(n ->{
			if(n.getNodeName().equals("configuration")) {
				tabManager.setStatus(VisualStatus.valueOf(XML.atr(n, "gridVisablity")));
				sidePane.setStatus(VisualStatus.valueOf(XML.atr(n, "searchVisability")));
			}
		});
		
		nodes.forEach(n ->{
			if(n.getNodeName().equals("schemas")) {
				this.openUndoSchemas(context,n);
			}
		});
	}
	


	public void clear() {
		tabManager.clear();
	}

	public TabManager getTabManager() {
		return tabManager;
	}

	public void saveFile() {
    	String extension = "";
		if (file.getName().contains("."))   extension = file.getName().substring(file.getName().lastIndexOf("."));
		if(extension.equals(".xml")) {
			try {
				if (!(file == null)){	        
			        Transformer transformer = TransformerFactory.newInstance().newTransformer();
			        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			        transformer.transform(new DOMSource(this.createDocument()), new StreamResult(file));
				} 
			} catch (TransformerException e){e.printStackTrace();}  
		}else if(extension.equals(".con")) {
			try {
				FileOutputStream fos = new FileOutputStream(file);
				ZipOutputStream zipOut = new ZipOutputStream(fos);
				ZipEntry zipEntry = new ZipEntry("document.xml");
				zipOut.putNextEntry(zipEntry);
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				transformer.transform(new DOMSource(this.createDocument()), new StreamResult(bout));
				zipOut.write(bout.toByteArray());
				zipOut.closeEntry();
				zipOut.close();
			} catch (IOException | TransformerException e) {
				e.printStackTrace();
			}
		}
	}
			
	public Document createDocument() {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = document.createElement("NFile");
			document.appendChild(root);
			//Configuration info
			Element configE = document.createElement("configuration");
			configE.setAttribute("gridVisablity", this.tabManager.getStatus().toString());
			configE.setAttribute("searchVisability", this.sidePane.getStatus().toString());
			root.appendChild(configE);

			Element schemas = document.createElement("schemas");
			root.appendChild(schemas);	       
			maps.forEach((name, map) -> {//multi-schema Currently will have only one map
				map.save(document, schemas);
			});
	        return document;
	        
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	public ActivityMode getActivityMode() {
		return mode.getValue();
	}

	public void setActivityMode(ActivityMode mode) {
		this.mode.setValue(mode);
	}
	
	public ACT getActivity() {
		return activities.get(mode.getValue());
	}
	
	public HashMap<ActivityMode, ACT> getActivities() {
		return activities;
	}

	public void setCompactView(boolean b) {
		maps.forEach((s,m) -> {
			m.setCompactView(b);
		});		
	}
	
	public ObservableList<Message> getMessages() {
		return messages;
	}

	public void addMessage(Message message) {
		messages.add(message);
	}

	public void ActivateFile() {
		fileManager.getNapp().getBorderPane().setCenter(quadSplit);
//		if(show console) set bottom left console
		fileManager.getNapp().attachConsoleToFile(this);
	}

	public ObservableList<Region> getMessagesRegion() {
		return messagesSideVBox;
	}

	public QuadSplit getQuadSplit() {
		return quadSplit;
	}

	public CenterMessage getCenterMessage() {
		return centerMessage;
	}

	/**
	 * @return the glassStackPane
	 */
	public StackPane getGlassStackPane() {
		return glassStackPane;
	}
	
}
