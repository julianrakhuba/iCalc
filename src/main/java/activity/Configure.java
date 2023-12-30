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
package activity;

import java.util.ArrayList;

import application.NnodeLine;
import clientcomponents.NKey;
import clients.XMLBase;
import configure.NKeyCell;
import configure.NLink;
import file.NFile;
import generic.ACT;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import layer.LAY;
import map.Nnode;
import search.PAIR;
import search.PopUpStage;
import status.Selector;

public class Configure extends ACT {
	private Nnode activeNnode;
	public void rebuildFieldMenu() {}

	public Configure(NFile nFile) {
		this.nFile = nFile;
	}

	public void passLAY(LAY lay) {
		this.passNnode(lay.getNnode(), null);
	}
	public void newSearchFUNCTION(Nnode nnod, String col, PAIR funcVAL) {}
	public void newSearchBETWEEN(Nnode nnod, String col, String from, String to) {}
	public void newSearchIN(Nnode nnod, String col, String in, ArrayList<String> values) {}
	
	public void passNnode(Nnode nnode, MouseEvent e) {
		if(activeNnode != null && nnode != activeNnode && e != null && e.isShiftDown()) {
			this.createLinkStage(nnode);
		}else if(activeNnode != null && nnode == activeNnode && e != null && e.isShiftDown()) {
			this.createLinkStage();
		}else if(e.isShiftDown()){
			this.closeActivity();
			activeNnode = nnode;
			activeNnode.styleOrange();
			activeNnode.getGreenNeon().show(600);
		}else {
			this.closeActivity();
		}
	}

	public void closeActivity() {
		if(activeNnode != null) {
			activeNnode.styleGray();
			activeNnode.getGreenNeon().hide(600);
			activeNnode = null;
		}
	}

	public void clearSelection() {
		if(activeNnode != null) {
			activeNnode.styleGray();
			activeNnode.getGreenNeon().hide(600);
		}
		activeNnode = null;		
	}
	
	public Nnode getActiveNnode() {
		return activeNnode;
	}

	

	private void createLinkStage(Nnode nnode) {
		PopUpStage inMenu = new PopUpStage(nFile.getFileManager().getNapp(), nFile.getFileManager().getNapp().getUpperPane().getPlaceHolder());
		ListView<NLink> listViewKeyMap = new ListView<NLink>();
		ListView<String> listViewA = new ListView<String>();
		ListView<String> listViewB = new ListView<String>();
		
		listViewKeyMap.setMaxHeight(200);		
		listViewKeyMap.setCellFactory(param -> new NKeyCell());

		listViewA.setMaxHeight(200);
		listViewB.setMaxHeight(200);		
		HBox.setHgrow(listViewKeyMap, Priority.ALWAYS);

		XMLBase base = activeNnode.getNmap().getNapp().getDBManager().getActiveConnection().getXMLBase();
		base.getKeys().filtered(k -> k.getSchema().equals(activeNnode.getSchema())
				&& k.getConst().equals("FOREIGN KEY")		
				&& k.getTable().equals(activeNnode.getTable())
				).forEach(key -> {
					listViewKeyMap.getItems().add(new NLink(key, Selector.SELECTED, listViewKeyMap, base));
				});
		
		activeNnode.getColumns().forEach( s->{
			listViewA.getItems().add(s.getColumn());
		});
		
		nnode.getColumns().forEach( s->{
			listViewB.getItems().add(s.getColumn());
		});
		
		listViewB.setOnMouseClicked(ee -> {
			if (ee.isShiftDown()) {
				if(listViewA.getSelectionModel().getSelectedItems().size() == 1) {
					NKey key = new NKey();
					key.setSchema(activeNnode.getSchema());
					key.setTable(activeNnode.getTable());
					key.setColumn(listViewA.getSelectionModel().getSelectedItem());
					key.setRSchema(nnode.getSchema());
					key.setRTable(nnode.getTable());
					key.setRColumn(listViewB.getSelectionModel().getSelectedItem());
					key.setConst("FOREIGN KEY");
							
					listViewKeyMap.getItems().add(new NLink(key, Selector.SELECTED, listViewKeyMap, base));
					    	 base.getKeys().add(key);//don't like this design
					    	 if(nnode.getSchema().equals(activeNnode.getSchema())) {//visual link only for local schema
					    		if(!activeNnode.getRootLines().containsKey(nnode) && !nnode.getRootLines().containsKey(activeNnode)) {
					    			NnodeLine line = new NnodeLine(activeNnode, nnode);
					    			activeNnode.getRootLines().put(nnode, line);
									nnode.getRootLines().put(activeNnode, line);
									activeNnode.getNmap().add(line);
									line.toBack();
								}
					    	 }
					 ee.consume();
				}
			}
		});
		
		VBox vboxa = new VBox(5,new Label(activeNnode.getTable()), listViewA);
		VBox vboxb = new VBox(5,new Label(nnode.getTable()), listViewB);		
		HBox.setHgrow(vboxa, Priority.ALWAYS);
		HBox.setHgrow(vboxb, Priority.ALWAYS);
		HBox hbox = new HBox(5,vboxa, vboxb);
		inMenu.add(new Label(activeNnode.getTable() + " connections"));
		inMenu.add(listViewKeyMap);
		inMenu.add(hbox);
		inMenu.showPopUp();
		
	}
	
	private void createLinkStage() {
		PopUpStage inMenu = new PopUpStage(nFile.getFileManager().getNapp(), nFile.getFileManager().getNapp().getUpperPane().getPlaceHolder());
		ListView<NLink> listViewKeyMap = new ListView<NLink>();
		ListView<String> listViewA = new ListView<String>();		
		listViewKeyMap.setMaxHeight(200);		
		listViewKeyMap.setCellFactory(param -> new NKeyCell());
		listViewA.setMaxHeight(200);
		HBox.setHgrow(listViewKeyMap, Priority.ALWAYS);

		XMLBase base = activeNnode.getNmap().getNapp().getDBManager().getActiveConnection().getXMLBase();
		base.getKeys().filtered(k -> k.getSchema().equals(activeNnode.getSchema())
				&& k.getConst().equals("FOREIGN KEY")		
				&& k.getTable().equals(activeNnode.getTable())
				).forEach(key -> {
					listViewKeyMap.getItems().add(new NLink(key, Selector.SELECTED, listViewKeyMap, base));
				});
		
		activeNnode.getColumns().forEach( s->{
			listViewA.getItems().add(s.getColumn());
		});
		
		
		VBox vboxa = new VBox(5,new Label(activeNnode.getTable()), listViewA);
		HBox.setHgrow(vboxa, Priority.ALWAYS);
		HBox hbox = new HBox(5,vboxa);
		inMenu.add(new Label(activeNnode.getTable() + " joins"));
		inMenu.add(listViewKeyMap);
		inMenu.add(hbox);
		inMenu.showPopUp();		
	}

}
