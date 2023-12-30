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
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import logic.Field;
import logic.SQL;

public class FunctionELM extends ELM{
	private String activeStyle = "-fx-background-color: white; -fx-padding: 0 0 0 0; -fx-text-fill: #7cd0f9;";
	private String unactiveStyle = "-fx-background-color: white; -fx-padding: 0 0 0 0; -fx-text-fill: #9DA1A1;";
	private String cusrorHboxStyle = "-fx-padding: 0 0 0 0; -fx-spacing: 2;  -fx-alignment:CENTER;" ;
	private BorderPane bpane = new BorderPane();
	private Label oLabel;
	private Label cLabel;
	
	private String name;
	private String open;
	private String oPar;
	private String cPar;
	private String close;
	private String label;

	public FunctionELM(String name, String label, String open, String oPar, String cPar, String close, RootELM rootELM) {
		super(rootELM);
		cursorBox = new CursorBox(this);
		cursorBox.getChildren().add(new Cursor(this));
		this.name = name; 
		this.label = label;
		this.open = open;
		this.oPar = oPar;
		this.cPar = cPar;
		this.close = close;
				
		oLabel = new Label(label + open);
		oLabel.setStyle(unactiveStyle);
		cLabel  = new Label(close);
		cLabel.setStyle(unactiveStyle);		
		bpane.setLeft(oLabel);
		bpane.setRight(cLabel);
		bpane.setCenter(cursorBox);
		bpane.setStyle("-fx-alignment:CENTER; -fx-background-color: white; -fx-padding: 0 0 0 0;" );
		BorderPane.setAlignment(oLabel, Pos.CENTER);
		BorderPane.setAlignment(cLabel, Pos.CENTER);

		cursorBox.setStyle(cusrorHboxStyle);
		oLabel.setOnMouseClicked(e->{
			cursorBox.requestFocus();
			cursorBox.showFirstCursor();
		});
		bpane.setOnMouseClicked(e ->{
			e.consume();
		});
		
		this.getElements().addListener((ListChangeListener<? super ELM>) a -> rootELM.refreshSideLableText());
	}
	
	public void styleFocused() {
		oLabel.setStyle(activeStyle);
		cLabel.setStyle(activeStyle);
	}

	public void styleUnfocused() {
		oLabel.setStyle(unactiveStyle);
		cLabel.setStyle(unactiveStyle);
	}

	public FunctionELM(OpenContext context, org.w3c.dom.Node fx, RootELM rootELM) {		
		this(XML.atr(fx, "name"), XML.atr(fx, "label"), XML.atr(fx, "open"), XML.atr(fx, "oPar"), XML.atr(fx, "cPar"), XML.atr(fx, "close"), rootELM);
		super.createXMLChildren(context, fx,rootELM);
	}

	//OUTPUT •••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
	public String getLabelText() {				
		StringBuilder ret = new StringBuilder();
		ret.append(" ");
		ret.append(oLabel.getText());
		this.getElements().forEach(elm -> ret.append(elm.getLabelText() + ""));
		ret.append(cLabel.getText());
		return ret.toString();
	}
	
	public void buildSQL(SQL sql) {
		sql.addNText(new NText(" " + name + open + oPar, getRootELM().getLay()));
		this.getElements().forEach(elm -> elm.buildSQL(sql));		
		sql.addNText(new NText(cPar + close, getRootELM().getLay()));
	}
	
	public String getPivotStringSQL(Field pvtFld, String val) {
		StringBuilder ret = new StringBuilder();
		ret.append(" " + name);
		ret.append(open);
		ret.append(oPar);
		this.getElements().forEach(elm -> ret.append(elm.getPivotStringSQL(pvtFld, val) + ""));
		ret.append(cPar);
		ret.append(close);
		return ret.toString();
	}

	public Node getNode() {
		return bpane;
	}
	
	public void saveXml(Document doc, Element parentE) {
		Element funcE = doc.createElement("FunctionELM");
		parentE.appendChild(funcE);
		funcE.setAttribute("name", name);
		funcE.setAttribute("label", label);
		funcE.setAttribute("open", open);
		funcE.setAttribute("oPar", oPar);
		funcE.setAttribute("cPar", cPar);
		funcE.setAttribute("close", close);
		funcE.setAttribute("name", name);
		this.getElements().forEach(ch -> ch.saveXml(doc, funcE));
	}
	
	public List<ELM> isUsedInElm(Field field) {
		List<ELM> used = new  ArrayList<ELM>();
		 this.getElements().forEach(el -> used.addAll(el.isUsedInElm(field)));
		return used;
	}

}
