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
package logic;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import activity.Calculation;
import application.XML;
import elements.ELM;
import elements.RootELM;
import file.OpenContext;
import generic.ACT;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import layer.LAY;

public class FormulaField extends Field {
	private RootELM root;
	
	public FormulaField(LAY fieldLay) {		
		super(fieldLay);
		root = new RootELM(this, fieldLay.getNnode().getNmap().getNapp());
	}
	
	public void activeClick(MouseEvent e) {
		ACT act = this.getFieldLay().getNnode().getNmap().getNapp().getFilemanager().getActiveNFile().getActivity();
		if(act instanceof Calculation) {
			((Calculation)act).activateClick(this, e);
		}
	}
	
	public String getFunction_Column() {
		SQL sql = new SQL();//Can I not use SQL here?
		root.buildSQL(sql);
		return sql.toString();
	}
	
	public void addNTextToSQL(SQL sql){
		root.buildSQL(sql);
	}
	
	public String getPivot_Column(Field pvtFld, String val) {
		return root.getPivotStringSQL(pvtFld, val);
	}

	public Pane getCursorBox() {
		return root.getNode();
	}
		
	public String getLabelText() {
		return root.getLabelText();
	}
	
	public Label getSelectLabel() {	
		select.getLabel().setText(this.getLabelText().trim());
		return  select.getLabel();
	}
	
	public void saveXml(Document document, Element fieldsE) {
		Element fieldE = document.createElement("field");
		fieldE.setAttribute("fieldType", "CALCULATED");	
		fieldE.setAttribute("rowset_type", this.getRowset_type());
		fieldE.setAttribute("aliase", this.getAliase());
		fieldE.setAttribute("select", this.isSelected() + "");
		fieldE.setAttribute("group", this.isGroupBy() + "");
		fieldE.setAttribute("pivot", this.isPivot() + "");
		fieldE.setAttribute("agrigate", this.isAgrigated() + "");
		fieldE.setAttribute("format_id", this.getFormat().getId());
		fieldsE.appendChild(fieldE);		
		root.saveXml(document, fieldE);
		
		Element pivotChacheE = document.createElement("pivotChache");
		fieldE.appendChild(pivotChacheE);		
		pivotCache.forEach(pch -> {
			Element headerE = document.createElement("header");
			headerE.setAttribute("name", pch);
			pivotChacheE.appendChild(headerE);
		});
	}
	
	public void loopB(OpenContext context, Node nn) {
		XML.children(nn).forEach(n2 ->{
			if(n2.getNodeName().equals("RootELM")) {
				root.openB(context, n2);
			}
		});
	}

	public Label getLabel() {
		return root.getLabel();
	}

	public  List<ELM> isUsed(Field f) {
		return  this.root.isUsedInElm(f);
	}
	
	//CREATE ELEMENTS ••••••••••••••••••••••••••••••••••••••••••
	public void createFunctionELM(String nname, String label,String open, String openPar, String closePar, String close) {
		root.getActiveELM().createFunctionELM(nname, label, open, openPar, closePar, close, root, true).getCursorBox().activateFocus();
	}

	public void createFieldELM(Field field) {
		root.getActiveELM().createFieldELM(field, root, true);
	}

	public void createStringELM(String string, boolean activate) {
		root.getActiveELM().createStringELM(string, root, true).getNode().requestFocus();
	}

	public RootELM getRoot() {
		return root;
	}

}
