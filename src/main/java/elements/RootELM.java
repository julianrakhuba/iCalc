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

import activity.Edit;
import application.Console;
import application.Constellatio;
import file.OpenContext;
import generic.ACT;
import generic.SideLabel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import layer.LAY;
import logic.Field;
import logic.FormulaField;
import logic.SQL;
import logic.SearchCON;
import status.Selector;
import status.Status;

public class RootELM extends ELM{
	
	private SearchCON searchCON;
	private FormulaField formula;
	private SideLabel sideLabel = new SideLabel();
	private Property<Status> active = new SimpleObjectProperty<Status>(Status.UNACTIVE);
	private Property<Selector> selected = new SimpleObjectProperty<Selector>(Selector.UNSELECTED);
	private String focusedCursorBox =   "-fx-padding: 2; -fx-spacing: 2;  -fx-alignment:CENTER;  -fx-max-height: 30; -fx-min-height: 30;  -fx-background-color: transparent; -fx-text-fill: #9DA1A1;  -fx-background-radius: 15 15 15 15;" ;
	private String unfocusedCursorBox = "-fx-padding: 2; -fx-spacing: 2;  -fx-alignment:CENTER;  -fx-max-height: 30; -fx-min-height: 30;  -fx-background-color: transparent; -fx-text-fill: #9DA1A1;  -fx-background-radius: 15 15 15 15;" ;
	
	
	public RootELM(SearchCON searchCON) {
		this();
		this.searchCON = searchCON;
		sideLabel.setOnMouseClicked(e ->  {
			ACT act = searchCON.getLay().getNnode().getNmap().getNFile().getActivity();
			if(act instanceof Edit && act.getActiveLayer() == searchCON.getLay()) {
				((Edit)act).conditionActivateClick(searchCON);
			}
		});
		sideLabel.getPane().setOnMouseClicked(e -> {
			ACT act = searchCON.getLay().getNnode().getNmap().getNFile().getActivity();
			if(act instanceof Edit && act.getActiveLayer() == searchCON.getLay()) {
				((Edit)act).conditionClick(searchCON);
			}
			e.consume();
		});
		
		selected.addListener((a,b,c)-> this.updateStyle());
		this.updateStyle();
	}
	
	
	private Console getConsole() {
		if(searchCON != null) {
			return  searchCON.getLay().getNnode().getNmap().getNapp().getConsole();
		}else {
			return formula.getFieldLay().getNnode().getNmap().getNapp().getConsole();
		}
	}
	
	public RootELM(FormulaField formula, Constellatio app) {
		this();
		this.formula = formula;
		sideLabel.styleUnselected();
		sideLabel.setOnMouseClicked(e ->  formula.activeClick(e));
	}
	
	public RootELM() {
		cursorBox = new CursorBox(this);
		cursorBox.getChildren().add(new Cursor(this));
		cursorBox.setStyle(unfocusedCursorBox);		
		this.getElements().addListener((ListChangeListener<? super ELM>) a -> this.refreshSideLableText());
		active.addListener((a,b,c)-> updateConsole());
	}
	
	public LAY getLay() {
		if(searchCON !=null) {
			return searchCON.getLay();
		}else {
			return formula.getFieldLay();
		}
	}
	
	public void styleFocused() {
		cursorBox.setStyle(focusedCursorBox);//local
	}

	public void styleUnfocused() {
		cursorBox.setStyle(unfocusedCursorBox);
	}

	//OUTPUT •••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
	public SideLabel getLabel() {
    	if(this.getConstellatio().getStage().getStyle() == StageStyle.TRANSPARENT) {
      		sideLabel.setStyle("-fx-text-fill: #ababab; -fx-font-size: 12;");//overwrite text, ugly work around, move to dark css
    	}
		return sideLabel;
	}
	
	public String getLabelText() {	
		return sideLabel.getText();
	}
	
	private void updateConsole() {		
		if(active.getValue() == Status.ACTIVE) {
			Console con = this.getConsole();
			con.clear();
			SQL sql = new SQL();
			this.buildSQL(sql);
			con.appendString(sql.toString() + "\n");//TODO temporary disabled
		}
	}
	
	public void buildSQL(SQL sql){
		this.getElements().forEach(elm ->{
			elm.buildSQL(sql);
		});
	}
	
	public String getPivotStringSQL(Field pvtFld, String val) {
		StringBuilder ret = new StringBuilder();
		this.getElements().forEach(elm ->{
			ret.append(elm.getPivotStringSQL(pvtFld, val) + "");
		});
		return ret.toString();
	}	
	
	public Pane getNode() {
		return cursorBox;
	}

	public void refreshSideLableText() {
		StringBuilder ret = new StringBuilder();
		this.getElements().forEach(elm -> ret.append(elm.getLabelText() + ""));
		sideLabel.setText(ret.toString());
		
		updateConsole();
	}

	public void saveXml(Document doc, Element parent) {
		Element rootE = doc.createElement("RootELM");
		parent.appendChild(rootE);
		this.getElements().forEach(ch -> ch.saveXml(doc, rootE));
	}

	public void openB(OpenContext context, org.w3c.dom.Node fx) {
		super.createXMLChildren(context, fx, this);
	}
	
	public Constellatio getConstellatio() {
		if(searchCON !=null) {
			return searchCON.getLay().getNnode().getNmap().getNapp();
		}else {
			return formula.getFieldLay().getNnode().getNmap().getNapp();
		}
	}

	public SearchCON getSearchCON() {
		return searchCON;
	}

	public FormulaField getFormula() {
		return formula;
	}

	public List<ELM> isUsedInElm(Field field) {
		List<ELM> used = new  ArrayList<ELM>();
		 this.getElements().forEach(el -> used.addAll(el.isUsedInElm(field)));
		return used;
	}
	
	public ELM getActiveELM() {		
		Node nd = cursorBox.getScene().focusOwnerProperty().get();
		if (nd instanceof CursorBox) {
			return ((CursorBox) nd).getElm();
	    }else {
	    	return null;
	    }	
	}
	
    public void updateStyle() {
    	if(this.getSelected() == Selector.SELECTED) {
    		sideLabel.styleSelected();
    	}else{
    		sideLabel.styleUnselected();
    	}
    }
	
	public void setStatus(Status status) {
		this.active.setValue(status);
	}
	
	public Status getStatus() {
		return active.getValue();
	}
	
	public void setSelected(Selector sel) {
		this.selected.setValue(sel);
	}
	
	public Selector getSelected() {
		return selected.getValue();
	}
}
