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
package elements;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import application.XML;
import file.OpenContext;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import logic.Field;
import logic.SQL;
//import rakhuba.elements.ELM;
//import rakhuba.elements.RootELM;
import search.NVal;
import search.PopUpStage;
import sidePanel.ValueCell;
import status.Selector;
import status.ValueType;

public class ValuesELM extends ELM{
	private Field field;
	private TextField filterTextField = new TextField();
	private ListView<NVal> listView = new ListView<NVal>();
	private ObservableList<String> selectedValues = FXCollections.observableArrayList();
	private ObservableList<NVal> fullChache = FXCollections.observableArrayList();
	private ObservableList<NVal> filteredChache = FXCollections.observableArrayList();
	private Property<NVal> activeNVal = new SimpleObjectProperty<NVal>();
	private PopUpStage inMenu;
	private ValueType valueType;
	private Label searchLabel = new Label();
	boolean show = true;
	
	//SINGLE
	public ValuesELM(String value, RootELM rootELM, Field field) {
		this(rootELM, ValueType.SINGLE);
		if(value != null) selectedValues.add(value);
		this.field = field;
		searchLabel.setText(" " + selectedValues.get(0) + " ");
		searchLabel.setTooltip(new Tooltip(field.getFieldLay().getAliase() + "." + field.getColumn()));
	}
	
	//MULTI
	public ValuesELM(ArrayList<String> values, RootELM rootELM, Field field) {
		this(rootELM, ValueType.MULTI);
		if(values != null) selectedValues.addAll(values);
		this.field = field;
		searchLabel.setText(" ... ");
		searchLabel.setTooltip(new Tooltip(field.getFieldLay().getAliase() + "." + field.getColumn()));
	}
	
	//MENU
	public ValuesELM(RootELM rootELM, ValueType valueType, Field field) {
		this(rootELM, valueType);
		this.field = field;
		if(this.valueType ==  ValueType.SINGLE && selectedValues.size() == 1) {
			searchLabel.setText(" " + selectedValues.get(0) + " ");
		}else {
			searchLabel.setText(" ... ");
			searchLabel.setTooltip(new Tooltip(field.getFieldLay().getAliase() + "." + field.getColumn()));
		}
	}

	//XML
	public ValuesELM(OpenContext context, org.w3c.dom.Node inX, RootELM rootELM) {
		this(rootELM, ValueType.valueOf(XML.atr(inX, "valueType")));
		this.field = context.getFields().get(XML.atr(inX, "fieldAliase"));
		if(this.valueType ==  ValueType.SINGLE && selectedValues.size() == 1) {
			searchLabel.setText(" " + selectedValues.get(0) + " ");
		}else {
			searchLabel.setText(" ... ");
		}
		
		XML.children(inX).forEach(n ->{
				if(n.getNodeName().equals("selectedValues")) {
				XML.children(n).forEach(v ->{
					if(v.getNodeName().equals("val")) {
						selectedValues.add(v.getTextContent());
					}
				});				
			}			
		});
	}
	
		//LOCAL
	private ValuesELM(RootELM rootELM, ValueType valueType) {
		super(rootELM);
		this.valueType = valueType;
		
		searchLabel.setFocusTraversable(true); // Enable focus for the label
        
		searchLabel.setOnMouseClicked(e ->{
			if(show) {				
				if(this.getRootELM().getSearchCON() != null && this.getRootELM().getSearchCON().getRoot().getSelected() == Selector.SELECTED) {
					this.showValuesMenu();	
				}
			}else {
				if(inMenu != null && !inMenu.isShowing()) {
					show = true;
				}
			}
			e.consume();
		});
		searchLabel.getStyleClass().add("inValues");
		searchLabel.setPadding(new Insets(0,3,0,3));
		searchLabel.setMaxHeight(10);
		listView.setCellFactory(param -> new ValueCell());
		listView.setItems(filteredChache);
		listView.setMaxHeight(200);

		filterTextField.textProperty().addListener((observable, oldvalue, newvalue) -> { if(inMenu.isShowing()) this.localFileterList(newvalue); });
		
		selectedValues.addListener((ListChangeListener<String>) c -> {
			if(this.valueType ==  ValueType.SINGLE && selectedValues.size() == 1) {
				searchLabel.setText(" " + selectedValues.get(0) + " ");
			}
		});
	}	

	public void styleFocused() {
		
	}

	public void styleUnfocused() {
		
	}	
	
	public void showValuesMenu() {
		if(inMenu == null || !inMenu.isShowing()) {
			inMenu = new PopUpStage(this.getRootELM().getConstellatio(), this.getRootELM().getConstellatio().getUpperPane().getPlaceHolder());
			
//			inMenu.focusedProperty().addListener((a, before, now) -> {				
//				if (!now && searchLabel.getScene().getFocusOwner() != this.getRootELM().getCursorBox()) {
//					inMenu.hide();
//				}
//			});

			inMenu.add(filterTextField);
			inMenu.add(listView);
			
			inMenu.setOnHidden(e -> {
				
				fullChache.clear();
				show = true;
			});
			
			inMenu.setOnShown(e -> show = false);
		
		
		listView.getItems().clear();
		this.rebuildChacheIfNeeded();
		if(selectedValues.size() == 0 && fullChache.size()>0) {
			fullChache.get(0).click(null);//auto select first item
		}
		
		if(valueType == ValueType.MULTI) {
			filterTextField.setPromptText("filter");
			filterTextField.setText("");
			this.localFileterList("");
			listView.requestFocus();
		}else {
			if(selectedValues.size() == 1) {					
				FilteredList<NVal> fl = fullChache.filtered(nv -> nv.getText().equals(selectedValues.get(0)));
				if(fl.size()>0) {
					NVal nValue = fl.get(0);
					activeNVal.setValue(nValue);
				}
			}
			filterTextField.setText("");
			this.localFileterList("");
			filterTextField.requestFocus();
		}

		listView.setOnKeyPressed(e -> {
			if (e.getCode().toString().equals("ENTER")) inMenu.close();
		});	
		
		filterTextField.positionCaret(filterTextField.getText().length());
		inMenu.showPopUp();
		}
	}
	
	private void localFileterList(String str) {
		filteredChache.clear();
		fullChache.forEach(nval ->{			
			if(nval.getText().toLowerCase().contains(str.toLowerCase()))  filteredChache.add(nval);
		});	
		filteredChache.sort((a,b) -> a.getText().compareTo(b.getText()));
		filteredChache.sort((a,b) -> b.getSelected().compareTo(a.getSelected()));
	}
	
	private void rebuildChacheIfNeeded() {
		if(fullChache.size() == 0 ) {//will be 0 every time its unselected			
			field.getValuesList(field.getFunction_Column()).forEach(str ->{
				if(this.selectedValues.contains(str)) {
					fullChache.add(new NVal(str, Selector.SELECTED, this));
				}else {
					fullChache.add(new NVal(str, Selector.UNSELECTED, this));
				}
			});
		}
	}

	//OUTPUT •••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••s
	public String getLabelText() {				
		StringBuilder ret = new StringBuilder();
		if(selectedValues.size()>0) {
			ret.append("'" + selectedValues.get(0) + "'");
			selectedValues.subList(1, selectedValues.size()).forEach(value -> ret.append(", '" + value + "'"));
		}
		return ret.toString();
	}
	
	public String getSideLabelText() {				
		StringBuilder ret = new StringBuilder();
		if(selectedValues.size()>0) {
			ret.append("'" + selectedValues.get(0) + "'");
			selectedValues.subList(1, selectedValues.size()).forEach(value -> ret.append(", '" + value + "'"));
		}
		return ret.toString();
	}
	
	public void buildSQL(SQL sql) {
		StringBuilder sb = new StringBuilder();
		
		if(selectedValues.size() > 0) {
			sb.append("'" + selectedValues.get(0) + "'");
			selectedValues.subList(1, selectedValues.size()).forEach(value -> sb.append(", '" + value + "'"));
		}
		sql.addNText(new NText(sb.toString(), field.getFieldLay()));
	}
	
	public String getPivotStringSQL(Field pvtFld, String val) {
		return "";
	}

	public Node getNode() {
		return searchLabel;
	}
	
	public void saveXml(Document doc, Element parentE) {
		Element inE = doc.createElement("ValuesELM");
		inE.setAttribute("valueType", valueType.toString());
		inE.setAttribute("fieldAliase", field.getAliase());

		parentE.appendChild(inE);		
		Element inValuesE = doc.createElement("selectedValues");
		inE.appendChild(inValuesE);
		selectedValues.forEach(value -> {
			Element valloop = doc.createElement("val");
			inValuesE.appendChild(valloop);
			valloop.setTextContent(value);
		});
	}	
	
	public List<ELM> isUsedInElm(Field field) {
		List<ELM> used = new  ArrayList<ELM>();
		if(this.field == field) used.add(this);		
		return used;
	}
	
	private void unselectAll(MouseEvent e) {		
		if(System.getProperty("os.name").startsWith("Mac")) {
			if((e != null && !e.isMetaDown()) || e == null) {
				fullChache.forEach(fnv -> fnv.setSelected(Selector.UNSELECTED));// add filtered list to selected onley and not nValue
				selectedValues.clear();
			}
		}else {
			if((e != null && !e.isControlDown()) || e == null) {
				fullChache.forEach(fnv -> fnv.setSelected(Selector.UNSELECTED));// add filtered list to selected onley and not nValue
				selectedValues.clear();
			}
		}
	}

	public void click(NVal nValue, MouseEvent e) {		
		
		if(this.valueType == ValueType.SINGLE && activeNVal.getValue() != nValue) {//select or swap single value
			if( activeNVal.getValue() != null) activeNVal.getValue().setSelected(Selector.UNSELECTED);
			nValue.setSelected(Selector.SELECTED);
			selectedValues.clear();
			selectedValues.add(nValue.getText());
			activeNVal.setValue(nValue);
			this.getRootELM().refreshSideLableText();
		}else if(valueType == ValueType.MULTI) {
			
			
//			HashSet<String> keys = field.getFieldLay().nnode.nmap.napp.getNscene().getHoldKeys();
			if(e != null && e.isShiftDown()
//					keys.contains("SHIFT")
					) {
				int a = filteredChache.indexOf(activeNVal.getValue());
				int b = filteredChache.indexOf(nValue);				
				if(a>=0 && b>=0 && a != b) {
					this.unselectAll(e);
					filteredChache.subList(Math.min(a, b), Math.max(a, b) + 1).forEach(fltVal ->{
						if(fltVal.getSelected() == Selector.UNSELECTED) {
							fltVal.setSelected(Selector.SELECTED);
							selectedValues.add(fltVal.getText());
						}
					});
					activeNVal.setValue(nValue);
				}else {
					this.unselectAll(e);
					if(nValue.getSelected() == Selector.UNSELECTED) {
						nValue.setSelected(Selector.SELECTED);
						selectedValues.add(nValue.getText());
					}
					activeNVal.setValue(nValue);
				}
			}else  {
				this.unselectAll(e);
				if(nValue.getSelected() == Selector.UNSELECTED) {
					nValue.setSelected(Selector.SELECTED);
					selectedValues.add(nValue.getText());
					activeNVal.setValue(nValue);
				}else if(selectedValues.size()>1) {
					nValue.setSelected(Selector.UNSELECTED);
					selectedValues.remove(nValue.getText());
					activeNVal.setValue(nValue);
				}
			}
			this.getRootELM().refreshSideLableText();
		}
	}
}
