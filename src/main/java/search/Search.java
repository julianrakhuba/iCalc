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
package search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import activity.Select;
import application.iCalc;
import application.SearchSP;
import file.NFile;
import generic.ACT;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import layer.LAY;
import map.Nnode;
import status.ActivityMode;
import status.SqlType;
import application.SkinFix;

public class Search extends TextField {	
	private ContextMenu contextMenu;
	private iCalc napp;
	private ArrayList<String> dynamicChache;
	private ListView<String> listView = new ListView<String>();
	private ArrayList<MenuItem> menuItems3 = new ArrayList<MenuItem>();
	private ArrayList<MenuItem> menuItems2 = new ArrayList<MenuItem>();
	private ArrayList<Nnode> currentNnodes = new  ArrayList<Nnode>();
	private Nnode focusNnode;
	
	public Search(iCalc app, SearchSP upperPane) {
		super();
		this.setFocusTraversable(false);
		this.napp = app;
			    
		this.setStyle(""
				+ "-fx-focus-color: transparent; "
				+ "-fx-text-box-border: transparent; "
				+ "-fx-border-width: 0 ;"
				+ " -fx-padding: 2 2 2 18;"
				+ " -fx-background-radius: 15 15 15 15;"
				+ " -fx-border-radius: 15 15 15 15;"
				);
		
		this.setMinHeight(30);
		
		HBox.setHgrow(this, Priority.ALWAYS);
		
		contextMenu = new ContextMenu();
		if (app.getMenu().getViewMenu().getGlassModeMenuItem().isSelected()) {
	        contextMenu.setSkin(new SkinFix(contextMenu));
		}

		
		
		this.setContextMenu(contextMenu);
		this.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, ev ->  ev.consume());
		
		contextMenu.setOnShowing(e -> {
			if(napp.getUpperPane().getSearchContext().isShowing()) {
				napp.getUpperPane().getSearchContext().hide();
			}
		});
	
		this.focusedProperty().addListener((observable, oldValue, newValue) -> {
		    if (!newValue) {
		        this.refreshNeonMarkers("");
		    }
		});

		this.textProperty().addListener((observable, oldvalue, newvalue) -> {
			if (getText().length() == 0) {
				contextMenu.hide();
				this.refreshNeonMarkers("");
			} else {
				if(napp.getFilemanager().size() > 0) {					
					String[] splitted = getText().split("\\.", 3);
					String table = splitted[0];
					Nnode toNnode = napp.getFilemanager().getActiveNFile().getActiveNmap().getNnode(table);
					 // TABLE
					if (splitted.length == 1) {
						regenerateContextMenu(napp.getFilemanager().getActiveNFile().getActiveNmap().getTablesList(), table, "");
						this.clearDynamicChache();
						
						this.refreshNeonMarkers(null);
						
						
					// COLUMN
					} else if (splitted.length == 2) {
						String column = splitted[splitted.length - 1];
						if (toNnode != null) {
							String string = "";
							for (int i = 0; i < (splitted.length - 1); i++)  string = string + splitted[i] + ".";
							regenerateContextMenu(toNnode.getColumnsList(), column, string);
						}
						this.clearDynamicChache();
						this.refreshNeonMarkers(table);

					//VALUE
					} else if (splitted.length == 3 && splitted[splitted.length - 2].length()>0) {
						String column = splitted[splitted.length - 2];
						String value = splitted[splitted.length - 1];
						String string = "";
						for (int i = 0; i < (splitted.length - 1); i++)  string = string + splitted[i] + ".";
						TreeSet<String> list = new TreeSet<String>(this.getValuesList(table, column));
						regenerateContextMenu(list, value, string);
						this.refreshNeonMarkers(table);
					}
					// workaround for menu shifting down every other time, needs work
					if (!contextMenu.isShowing()) {
						if(splitted.length == 1) {
							contextMenu.show(this, Side.BOTTOM, 15, 1);
							if (napp.getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
								contextMenu.setOpacity(0);
								KeyFrame kf1 = new KeyFrame(Duration.millis(200), new KeyValue(contextMenu.opacityProperty(), 1));
							    Timeline timeline = new Timeline(kf1);
							    timeline.setCycleCount(1);
							    timeline.play();
							}else {
								contextMenu.setOpacity(1);
							}
							
						}else {
							contextMenu.show(this, Side.BOTTOM, 15, 1);
						}
					}
				}
			}
		});
		
		this.setOnAction(e ->{
			String[] splitted = this.getText().split("\\.", 3);	
			if (splitted.length == 3 && splitted[2].length() > 0) {
				Nnode nnod = napp.getFilemanager().getActiveNFile().getActiveNmap().getNnode(this.getSplit()[0]);
				String col = this.getSplit()[1];
				String val = this.getSplit()[2];					
				napp.getFilemanager().getActiveNFile().getActivity().newSearchFUNCTION(nnod, col, new PAIR("=", val));//default function is =
				this.clear();
				this.requestFocus();
			}else if(splitted.length == 1) {
				Nnode nnod = napp.getFilemanager().getActiveNFile().getActiveNmap().getNnode(this.getSplit()[0]);
				if(nnod != null) {
					ACT activity = napp.getFilemanager().getActiveNFile().getActivity();
					if(activity instanceof Select) {
						activity.passNnode(nnod, null);
						((Select) activity).createSLayer(nnod);
						this.clear();
						this.requestFocus();
					}
				}
			}
			e.consume();
		});
		
		listView.setMaxHeight(200);
		this.createFunctionMenu();
	}
	
	private void refreshNeonMarkers(String table) {		
		if(napp.getFilemanager().getActiveNFile() !=null) {
			ChangeListener<? super Node> focusListener = (observable, oldValue, newValue) -> {	    	
		    	if(newValue != null) {
		 	    	 ((Parent) newValue).getChildrenUnmodifiable().forEach(ch ->{	 	    		
		 	    		if(ch instanceof Label) {
		 					Nnode toNnode = napp.getFilemanager().getActiveNFile().getActiveNmap().getNnode(((Label)ch).getText());
		 					if(focusNnode != null && focusNnode != toNnode) focusNnode.getGreenNeon().hide(600);		 					
		 					if(toNnode != null) {
		 						toNnode.getGreenNeon().show(600);
		 						focusNnode = toNnode;
		 					}		 		            
		 	    		}else {
		 					if(focusNnode != null) focusNnode.getGreenNeon().hide(600);
		 	    		}
		 	    	});
		    	}else {
					if(focusNnode != null) focusNnode.getGreenNeon().hide(600);
		    	}
	        };
			contextMenu.setOnShown(e -> contextMenu.getScene().focusOwnerProperty().addListener(focusListener));
			contextMenu.setOnHidden(e -> {
					if(focusNnode != null) focusNnode.getGreenNeon().hide(600);					
					contextMenu.getScene().focusOwnerProperty().removeListener(focusListener);
			});
			
			ArrayList<String> strings = new  ArrayList<String>();
			if(table !=null) {
				strings.add(table);
			}else {
				contextMenu.getItems().forEach(item ->{	
					strings.add(((Label)((CustomMenuItem)item).getContent()).getText());
				});
			}
			
			//new Nnodes
			ArrayList<Nnode> newNodes = new  ArrayList<Nnode>();
			strings.forEach(str ->{
				Nnode toNnode = napp.getFilemanager().getActiveNFile().getActiveNmap().getNnode(str);
				if(toNnode != null) {
					toNnode.getBlueNeon().show(600);
					newNodes.add(toNnode);
				}
			});

			ArrayList<Nnode> removeNodes = new ArrayList<Nnode>();
			currentNnodes.forEach(old -> {
				if (!newNodes.contains(old)) removeNodes.add(old);
			});
			
			
			removeNodes.forEach(rmnd ->{
				rmnd.getBlueNeon().hide(600);
			});
			
			currentNnodes.clear();
			currentNnodes.addAll(newNodes);
		}
		
	    
	}

	private void clearDynamicChache() {
		dynamicChache = null;
	}

	private String[] getSplit() {
		return  this.getText().split("\\.", 3);
	}
	
	private ArrayList<String>  getValuesList(String table, String sql_column_name) {
		NFile file = napp.getFilemanager().getActiveNFile();
		LAY actLAY = file.getActivity().getActiveLayer();
	
		Nnode toNnode = file.getActiveNmap().getNnode(table);
		if(napp.getMenu().getViewMenu().getDynamicSearchMenuItem().isSelected() &&  file.getActivityMode() == ActivityMode.EDIT  &&  actLAY.getNnode() == toNnode  && (actLAY.getRootLevel().getActiveGroup() != null || actLAY.getSqlType() == SqlType.SQLJ)) {			
			if(dynamicChache ==  null) {
				String full_name = actLAY.getAliase() + "." + sql_column_name;
				dynamicChache =  toNnode.getOpenDAO().readDistinctValues(actLAY.getSearchSQL(full_name, full_name));
			}
			return dynamicChache;
		}else {
			return  toNnode.getStaticValuesList(sql_column_name);
		}
	}
	
	private void regenerateContextMenu(TreeSet<String> list,String val, String dot) {
		LinkedList<String> resultsVal = new LinkedList<>(list.subSet(val, val + Character.MAX_VALUE));		
		LinkedList<String> resultsMax = new LinkedList<>(resultsVal.subList(0, Math.min(resultsVal.size(), 10)));	
		
		ArrayList<CustomMenuItem> menuItems = new ArrayList<CustomMenuItem>();
		resultsMax.forEach(mx -> {
			Label entryLabel = new Label(mx);
			entryLabel.setPrefWidth(670);
			entryLabel.prefWidthProperty().bind(this.widthProperty().subtract(45));
			
			CustomMenuItem item = new CustomMenuItem(entryLabel, true);	
			item.setContent(entryLabel);
			item.setMnemonicParsing(true);  
			item.setOnAction(e -> {
				e.consume();
				setText(dot + mx);
				positionCaret(getLength());
			});
			menuItems.add(item);
		});

		contextMenu.getItems().clear();
		contextMenu.getItems().addAll(menuItems);
	}
	
	public void exitEdit() {
		this.clear();
		contextMenu.hide();
	}
	
	private void createFunctionMenu() {
		//3 PART MENU
		List<String>  funcStrings =  Arrays.asList("=", "!=", ">", "<", ">=", "<=", "like");
		funcStrings.forEach(fnc ->{
			Label lbl = new Label(fnc);
			CustomMenuItem mi = new CustomMenuItem(lbl, true);
			menuItems3.add(mi);
			mi.setOnAction( e ->{
				if((this.getSplit().length == 3 && this.getSplit()[2].length() > 0) || (this.getSplit().length == 2 && this.getSplit()[1].length() > 0)) {
					Nnode nnod = napp.getFilemanager().getActiveNFile().getActiveNmap().getNnode(this.getSplit()[0]);
					String col = this.getSplit()[1];
					String val = ((this.getSplit().length == 3) ? this.getSplit()[2] : null);
					napp.getFilemanager().getActiveNFile().getActivity().newSearchFUNCTION(nnod, col, new PAIR(fnc, val));					
					this.clear();
					this.requestFocus();
				}
			});
		});
		
		List<String>  inList =  Arrays.asList("in");
		inList.forEach(ml ->{			
			Label lbl = new Label(ml);
			CustomMenuItem mi = new CustomMenuItem(lbl, true);
			menuItems2.add(mi);
			mi.setOnAction( e ->{
				PopUpStage inMenu = new PopUpStage(napp,this);
				inMenu.add(listView);
				listView.getItems().clear();
				listView.getItems().addAll(this.getValuesList(this.getSplit()[0], this.getSplit()[1]));
				inMenu.showPopUp();
				
				listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
				listView.setOnKeyPressed(eee -> {
					if (eee.getCode().toString().equals("ENTER")) {
						ArrayList<String> selectedList = new ArrayList<String>(listView.getSelectionModel().getSelectedItems());						
						napp.getFilemanager().getActiveNFile().getActivity().newSearchIN(napp.getFilemanager().getActiveNFile().getActiveNmap().getNnode(this.getSplit()[0]), this.getSplit()[1], ml, selectedList);		
						inMenu.hidePopUp();
						this.clear();
						this.requestFocus();
					}
				});				
				inMenu.setOnHidden(d -> this.requestFocus());
			});
		});
		Label lbl = new Label("between");
		CustomMenuItem btwItem = new CustomMenuItem(lbl, true);
		menuItems2.add(btwItem);
		btwItem.setOnAction(e ->{
			BetweenMenu between = new BetweenMenu(napp, this.getSplit()[0], this.getSplit()[1], this.getValuesList(this.getSplit()[0], this.getSplit()[1]));
			between.setOnHidden(ee -> this.requestFocus());
		});		
	}
		
	public ArrayList<MenuItem> getMenuItems() {
		if(this.getSplit().length == 3) {
			return menuItems3;
		}else if(this.getSplit().length == 2) {
			return menuItems2;
		}else {
			return  new ArrayList<MenuItem>();
		}
	}
	
	public void clear() {
		super.clear();
		this.clearDynamicChache();
	}
	
	public boolean isSwitchableMode() {
		return !this.isFocused() || this.getSplit().length <=1;
	}
}