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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import application.JoinLine;
import clientcomponents.NFunction;
import elements.CursorBox;
import elements.ELM;
import file.NFile;
import generic.ACT;
import javafx.collections.ObservableList;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;
import layer.LAY;
import logic.Field;
import logic.Group;
import logic.Join;
import logic.SearchCON;
import map.Nnode;
import pivot.LayerMenu;
import search.PAIR;
import sidePanel.Message;
import status.ActivityMode;
import status.JoinType;
import status.LayerMode;
import status.Selection;
import status.Selector;
import status.SqlType;
import status.Status;
import status.ValueType;

public class Edit extends ACT {
	private boolean modefied = false;
	private SearchCON activeSearch;
	private boolean quickEdit = false;

	public Edit(NFile nFile) {
		this.nFile = nFile;
	}

	public void passNnode(Nnode nnode, MouseEvent e) {}
	
	public void passLAY(LAY inLay) {
		if(activeSearch != null) {
			this.passLAYtoSearchCON(inLay);//pass to active SearchCON
		}else {	
			if (rootLay == null) {
				this.enterEdit(inLay, false);
			}else if (rootLay.isValidJoin(inLay)) {
				this.autoJoinLayViaFirstJoin(inLay, false);				
			} else {
				//SELF JOIN OR OTHER UNRELATED LAYERS
				if(rootLay.isNotConnectedTo(inLay) && rootLay != inLay && !rootLay.getAllConnectedLayers().contains(inLay) && rootLay.isSQLJ_to_SQLJ(inLay)) {
					inLay.setSelection(Selection.SELECTED);
					this.connectLayer(inLay);
					//SELF JOIN ONLY
					if(rootLay.getNnode() == inLay.getNnode() && rootLay.getSqlType() == SqlType.SQLJ && inLay.getSqlType() == SqlType.SQLJ) {
						rootLay.getFields().forEach(rootField -> {
							if(rootField.getColumn_key().equals("PRI") || rootField.getColumn_key().equals("UNI")) {//
								SearchCON searchCON = this.createSearchCONForSelfJoin(inLay, rootField);
								this.selectSearchCONFromJoin(searchCON, inLay);
							}
						});
					}
				}else if(rootLay.isValidSQLDjoin(inLay)){
					this.autoJoinLayViaFirstJoin(inLay, false); //LOOP FOR POSSIBLE JOINS HERE this.relatedViaJoinsToLay(lay)
				}
			}
		}		
	}
	
	public void autoJoinLayViaFirstJoin(LAY inLay, boolean quickEdit) {
		if (!quickEdit) inLay.setSelection(Selection.SELECTED);
		this.connectLayer(inLay);
		ArrayList<Join> joins = inLay.getRelatedJoins(rootLay);
		joins.forEach(jn ->{
			SearchCON searchCON = this.createSearchCONFromJoin(jn);
			if(joins.indexOf(jn) == 0) this.selectSearchCONFromJoin(searchCON, inLay);//select first join only
		});	
	}
	
	private void connectLayer(LAY lay) {
		JoinLine joinLine = new JoinLine(lay, rootLay,JoinType.JOIN);
		rootLay.getParentJoins().add(joinLine);
		lay.getChildJoins().add(joinLine);
		if (!rootLay.getNnode().getNmap().contains(joinLine.getCubicCurve()) && lay.getNnode().getSchema().equals(rootLay.getNnode().getSchema())) {
			int index = rootLay.getNnode().getNmap().getSchemaPane().getChildren().indexOf(lay.getPane());
			int index2 = rootLay.getNnode().getNmap().getSchemaPane().getChildren().indexOf(rootLay.getPane());
			rootLay.getNnode().getNmap().getSchemaPane().getChildren().add(Math.min(index, index2) -1, joinLine.getCubicCurve());
		}
	}
	
	private void passLAYtoSearchCON(LAY inLay) {
		if (rootLay != inLay && inLay.getSqlType() == SqlType.SQL && nFile.getFileManager().getNapp().getNscene().getFocusOwner() instanceof CursorBox) {
			activeSearch.createSubELM(inLay);
			if(activeSearch.getRemoteLay() == null) activeSearch.setRemoteLay(inLay);
			if(rootLay.isNotConnectedTo(inLay)) {
				inLay.setSelection(Selection.SELECTED);
				this.connectLayer(inLay);
			}
		}
	}

	private SearchCON createSearchCONForSelfJoin(LAY inLay, Field rootField) {
		SearchCON searchCON = new SearchCON(rootLay);
		searchCON.setRemoteLay(inLay);
		Field matchingField = inLay.getFieldOrFunction(inLay.getAliase() + "_"+ rootField.getSQL_Column_name());
		searchCON.autoJoin(rootField, matchingField);
		rootLay.addSearchCONtoSearchList(searchCON);
		return searchCON;
	}
	
	private SearchCON createSearchCONFromJoin(Join join) {
		SearchCON searchCON = new SearchCON(rootLay);
		searchCON.setRemoteLay(join.getLay());
		
		Stream<Field> strmFld = rootLay.getFields().stream().filter(f -> f.getJoins().stream().filter(jj -> jj.getSchema().equalsIgnoreCase(join.getRemoteSchema()) && jj.getTable().equalsIgnoreCase(join.getRemoteTable()) && jj.getColumn().equalsIgnoreCase(join.getRemoteColumn())).count() > 0);		
		Field localField = (Field) strmFld.toArray()[0];
		
		Field remoteField = (Field) join.getLay().getFields().stream().filter(f -> f.getSQL_Column_name().equalsIgnoreCase(join.getSqlColumn())).toArray()[0];
		
		if(join.getLay().getSqlType() == SqlType.SQL) {
			searchCON.autoSub(localField, join.getLay(), remoteField);
		}else {
			searchCON.autoJoin(localField, remoteField);
		}
		
		rootLay.addSearchCONtoSearchList(searchCON);
		return searchCON;
	}
	
	private void selectSearchCONFromJoin(SearchCON searchCON, LAY lay) {
		if ((rootLay.getSqlType() == SqlType.SQLJ) && lay.getSqlType() == SqlType.SQL) {
			((JoinLine)rootLay.getParentJoins().stream().filter(ln -> ln.getFromLay() == lay).toArray()[0]).setJoinType(JoinType.SHIFT);
		}		
		searchCON.getRoot().setSelected(Selector.SELECTED);
		rootLay.getRootLevel().getDynamicGroup().add(searchCON);			
		nFile.getSidePane().activateSearch(rootLay);
		rootLay.refreshFilterIndicator();
		modefied = true;
	}
	


	public void enterEdit(LAY lay, boolean quickEdit) {
		this.quickEdit = quickEdit;
		rootLay = lay;
		if(!quickEdit)  rootLay.getRootLevel().show();
		rootLay.getLogic().setLayoutX(rootLay.getNnode().getLayoutX());
		
		if(!quickEdit)  rootLay.getLogic().show();
		if(!quickEdit) rootLay.setMode(LayerMode.EDIT);
		
		nFile.getSidePane().activateSearch(rootLay);		
	}
	
	public void closeActivity() {
		if(activeSearch != null) {
			this.deactivateSearchCON();
		}
		
		if (rootLay != null) {			
			rootLay.getParentJoins().forEach(l-> l.getFromLay().setSelection(Selection.UNSELECTED));
			rootLay.getChildJoins().forEach(l-> l.getToLay().setSelection(Selection.UNSELECTED));
			if(!quickEdit) rootLay.getLogic().hide();
			rootLay.setSelection(Selection.UNSELECTED);
			rootLay.setMode(LayerMode.BASE);
			nFile.setActivityMode(ActivityMode.SELECT);
			nFile.getFileManager().getNapp().getUpperPane().getSearchTextField().exitEdit();
			rootLay = null;
//			add logic to unselect all sqlj children and sql parents, and select map for side panel ???
		}
		
		if (modefied) {
			nFile.getUndoManager().saveUndoAction();
			nFile.getFileManager().getNapp().getConsole().refreshActiveMonotor();
			modefied = false;
		}
		
		quickEdit = false;
	}
	
	public void startLayDelete() {
		if(rootLay != null) {
			if(rootLay.isSQL() || (rootLay.getChildDLayer() == null && rootLay.isRoot()) || this.isDisconnectable(rootLay))  this.deleteLayer();
		} 
	}
	
	private void deleteLayer() {
		nFile.getSidePane().deactivate();
		rootLay.deleteLayer();
		nFile.setActivityMode(ActivityMode.SELECT);
		nFile.getUndoManager().saveUndoAction();
		this.closeActivity();
	}

	//JOIN CLICK
	public void disconnectJoin(JoinLine jn) {
		LAY lay1;
		LAY lay2;
		
		if(rootLay.getParentJoins().contains(jn)) {
			lay1 = rootLay;
			lay2 = jn.getFromLay();
		}else {
			lay1 = jn.getToLay();
			lay2 = rootLay;
		}
		
		if(lay1.isSQL() || lay2.isSQL() || this.isDisconnectable(lay1)) {
			lay1.disconnect(lay2);
		}
	}
	
	private boolean isDisconnectable(LAY lay) {	
		List<Field> flds = lay.getChildPathSelectedFields();
		List<ELM> elms = lay.getParentPathFormulaELMs();		
		boolean disc = flds.size() == 0 && elms.size() == 0;
		if(!disc) {
			nFile.getMessages().add(new Message(nFile, "LAY", "Can't discconect LAY:  " + lay.getAliase()));		
			if(flds.size() > 0) {
				flds.forEach(f ->{
					nFile.getMessages().add(new Message(nFile, "warning", "Cannot disconnect " + lay.getAliase() + " becouse field " + f.getAliase() + " is selected!"));
				});
			}
			
			if(elms.size() > 0) {
				elms.forEach(f ->{
					nFile.getMessages().add(new Message(nFile, "warning", "Cannot disconnect " + lay.getAliase() + " becouse field is used in " + f.getRootELM().getLabelText()));
				});
			}
			
		}		
		return disc;
	}

	//SEARCH CONDITION ••••••••••••••••••••••••••••••••••••••••••••••••
	public void conditionClick(SearchCON searchCON) {
		if(nFile.getFileManager().getNapp().getNscene().getHoldKeys().contains("CONTROL") && searchCON.getRemoteLay() == null) {
			new ArrayList<Group>(searchCON.getGroups()).forEach(g -> g.remove(searchCON));
			rootLay.getSearchList().remove(searchCON);//DELETE		
		}else {
			Group group = rootLay.getRootLevel().getDynamicGroup();
			if (!group.contains(searchCON)) {// ••••SELECT••••
				searchCON.getRoot().setSelected(Selector.SELECTED);
				group.add(searchCON);
			}else if (group.contains(searchCON)) {//UNSELECT
				group.remove(searchCON);
				searchCON.getRoot().setSelected(Selector.UNSELECTED);
				if(searchCON.getRemoteLay() != null) {
//					rootLay.disconnectFromParentLayerIfNotSelected(searchCON.getRemoteLay());// TEMPORARY DISABLE AUTODISCONNECT ON UNSELECT LINK
				}				
			}
		}
		
		rootLay.refreshFilterIndicator();
		modefied = true;	
	}
	
	public void conditionActivateClick(SearchCON con) {
		if(activeSearch != con) {
			this.activateSerachCON(con);
		}else {
			this.deactivateSearchCON();
		}
	}
	
	public void activateSerachCON(SearchCON searchCON) {
		if(activeSearch != searchCON && activeSearch != null) {
			this.deactivateSearchCON();
		}
		activeSearch = searchCON;
		activeSearch.getRoot().setStatus(Status.ACTIVE);
		nFile.getFileManager().getNapp().getUpperPane().setFormulaSearch(searchCON.getCursorBox());
		searchCON.getCursorBox().requestFocus();
	}
	
	public void deactivateSearchCON() {
		if(activeSearch != null) {
			activeSearch.getRoot().setStatus(Status.UNACTIVE);
			activeSearch = null;
		}
		nFile.getFileManager().getNapp().getUpperPane().setRegularSearch();
	}
	
	private void finishCreatingNewSearchCON(SearchCON con) {
		rootLay.addSearchCONtoSearchList(con);
		rootLay.getRootLevel().getDynamicGroup().add(con);
		nFile.getSidePane().activateSearch(rootLay);
		rootLay.refreshFilterIndicator();
		con.getRoot().setSelected(Selector.SELECTED);
		modefied = true;
	}
	
	public void newSearchFUNCTION(Nnode inNnode, String col, PAIR funcVAL) {
		if(rootLay.getNnode() == inNnode) {
			Field field = rootLay.getFieldOrFunction(rootLay.getAliase() + "_" + col);
			SearchCON con = new SearchCON(rootLay);
			con.autoSearchFunc(field, funcVAL);			
			this.finishCreatingNewSearchCON(con);
		}
	}

	public void newSearchBETWEEN(Nnode inNnode, String col, String from, String to) {
		if(rootLay.getNnode() == inNnode) {
			Field field = rootLay.getFieldOrFunction(rootLay.getAliase() + "_" + col);
			SearchCON con = new SearchCON(rootLay);
			con.autoBetween(field, from, to);
			this.finishCreatingNewSearchCON(con);
		}
	}

	public void newSearchIN(Nnode inNnode, String col, String in, ArrayList<String> values) {
		if(rootLay.getNnode() == inNnode) {
			Field field = rootLay.getFieldOrFunction(rootLay.getAliase() + "_" + col);
			SearchCON con = new SearchCON(rootLay);
			con.autoIn(field, in, values);			
			this.finishCreatingNewSearchCON(con);			
		}
	}

	public void rebuildFieldMenu() {
		boolean hideOnClick = true;
		if(nFile.getFileManager().getNapp().getUpperPane().getSearchTextField().getLength() > 0) {
			nFile.getFileManager().getNapp().getUpperPane().getSearchContext().getItems().addAll(nFile.getFileManager().getNapp().getUpperPane().getSearchTextField().getMenuItems());
		}else {
			//Strings••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
			Label lbl = new Label("strings");
			Menu funcMenu = new Menu("",lbl);
			Label ll = new Label("string");
			ll.setPrefWidth(100);
			CustomMenuItem mnI = new CustomMenuItem(ll, true);
			funcMenu.getItems().addAll(mnI, new SeparatorMenuItem());
			mnI.setOnAction(e ->{
				this.getActiveSearch().createStringELM(" ", true);
				modefied = true;
			});	
			
			List<String>  functrings =  Arrays.asList("=", "!=", ">", "<", ">=", "<=", "like", "is null", "is not null", "between" , "and", "not");		
			functrings.forEach(s -> {
				Label label = new Label(s);
				label.setPrefWidth(100);				
				CustomMenuItem menuItem = new CustomMenuItem(label, hideOnClick);
				funcMenu.getItems().add(menuItem);
				menuItem.setOnAction(e ->{
					this.getActiveSearch().createStringELM(s, false);
					modefied = true;
				});			
			});
			
			//Functions••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
			Menu functionsMenu = new Menu("", new Label("functions"));
			rootLay.getNnode().getNmap().getNapp().getUpperPane().getSearchContext().getItems().addAll(funcMenu, functionsMenu,  new SeparatorMenuItem());
			ObservableList<NFunction> functions = nFile.getFileManager().getNapp().getDBManager().getActiveConnection().getXMLBase().getXFunctions();
	
			functions.forEach(nf -> {
				if(nf.getType().equals("OTHER")) {
					Label label = new Label(nf.getLabel());
					label.setPrefWidth(100);
					CustomMenuItem menuItem = new CustomMenuItem(label,hideOnClick);
					functionsMenu.getItems().add(menuItem);
					menuItem.setOnAction(e ->{
						this.getActiveSearch().createFunctionELM(nf.getRealname(), nf.getLabel(), nf.getOpen(), nf.getOpenParam(), nf.getCloseParam(), nf.getClose());
						modefied = true;
					});
				}							
			});
			functionsMenu.getItems().add(new SeparatorMenuItem());
			functions.forEach(nf -> {
				if(nf.getType().equals("GROUP")) {
					Label label = new Label(nf.getLabel());
					label.setPrefWidth(100);
					CustomMenuItem menuItem = new CustomMenuItem(label,hideOnClick);
					functionsMenu.getItems().add(menuItem);
					menuItem.setOnAction(e ->{
						this.getActiveSearch().createFunctionELM(nf.getRealname(), nf.getLabel(), nf.getOpen(), nf.getOpenParam(), nf.getCloseParam(), nf.getClose());
						modefied = true;
					});
				}							
			});
			
			//Fields••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
			rootLay.getMenuLayers_for_Edit().forEach(joinLay -> {
//				if(rootLay.getSqlType() != SqlType.SQL || rootLay == joinLay) {//sql get only local fields here TODO REMOVED TO GET EFFFDT MENU WORKING
					LayerMenu menu = new LayerMenu(rootLay, joinLay);
					 joinLay.getFields().forEach(field ->{
						Label label = new Label(field.getLabelText());
						CustomMenuItem menuItem = new CustomMenuItem(label,hideOnClick);
						
				        menuItem.setOnAction(je ->{	
				        	HashSet<String> keys = rootLay.getNnode().getNmap().getNapp().getNscene().getHoldKeys();
				        	if(keys.contains("SHIFT") && !keys.contains("CONTROL")) {
			    				this.getActiveSearch().createValuesELM(field, ValueType.SINGLE);
				    		}else if(keys.contains("SHIFT") && keys.contains("CONTROL")) {
			    				this.getActiveSearch().createValuesELM(field, ValueType.MULTI);
				    		}else {
								this.getActiveSearch().createFieldELM(field);									
				    		}
							modefied = true;
				        });
				        menu.getItems().add(menuItem);
					 });
					 rootLay.getNnode().getNmap().getNapp().getUpperPane().getSearchContext().getItems().add(menu);
			});	
			
			
			//SQL ONLY? only one level deep
			rootLay.getParents().forEach(joinLay ->{
				if(joinLay.getSqlType() == SqlType.SQL) {
					LayerMenu menu = new LayerMenu(rootLay, joinLay);
					menu.setText("[•]");
					 joinLay.getFields().forEach(field ->{
						 Label label = new Label(field.getLabelText());
						CustomMenuItem menuItem = new CustomMenuItem(label,hideOnClick);							
				        menuItem.setOnAction(je ->{
				        	this.getActiveSearch().createFieldELM(field);
							modefied = true;
				        });
				        menu.getItems().add(menuItem);
					 });
					 rootLay.getNnode().getNmap().getNapp().getUpperPane().getSearchContext().getItems().add(menu);
				}
			});
		}
	}

	private SearchCON getActiveSearch() {
		if(activeSearch == null) {
			SearchCON con = new SearchCON(rootLay);
			this.activateSerachCON(con);
			this.finishCreatingNewSearchCON(con);
		}
		return activeSearch;
	}
}