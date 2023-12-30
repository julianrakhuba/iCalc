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

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import logic.Field;
import logic.SQL;

public class StringELM extends ELM{
	private TextField textField = new TextField();
	private String acStyle = "-fx-effect: innershadow(three-pass-box, #99ddff, 4, 0.1, 0, 0); -fx-text-fill: #9DA1A1; -fx-background-color: white; -fx-background-radius: 4 4 4 4";
	private String unStyle = "-fx-text-fill: #9DA1A1; -fx-background-color: white; -fx-background-radius: 4 4 4 4";
	
	public StringELM(String string, RootELM rootELM) {
		super(rootELM);
		textField.setPadding(new Insets(0,0,0,0));
		this.styleUnfocused();
		textField.setAlignment(Pos.CENTER);
		textField.textProperty().addListener((ov, prevText, currText) -> {
			 Platform.runLater(() -> {
		        Text tmpTxt = new Text(currText);
		        tmpTxt.setFont(textField.getFont());
		        double txWdt = tmpTxt.getLayoutBounds().getWidth();
		        textField.setPrefWidth(Math.max(txWdt + 10, 12));	        
		        this.getRootELM().refreshSideLableText();
			 });
		});
		
		textField.setText(string);
		textField.focusedProperty().addListener((a, b, c) -> {
			if (c) {
				this.styleFocused();
			}else {
				this.styleUnfocused();
			}
		});
	}
	
	public void styleFocused() {
			textField.setStyle(acStyle);
	}

	public void styleUnfocused() {
			textField.setStyle(unStyle);
	}

	//OUTPUT •••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
	public String getLabelText() {
		return " " + textField.getText() + " ";
	}
	
	public String getSideLabelText() {
		return " " + textField.getText() + " ";
	}
	
	public void buildSQL(SQL sql) {
		sql.addNText(new NText(" " + textField.getText() + " ", rootELM.getLay()));
	}
	
	public String getPivotStringSQL(Field pvtFld, String val) {
		return textField.getText();
	}

	
	public Node getNode() {
		return textField;
	}

	public void saveXml(Document doc, Element parentE) {
		Element fldE = doc.createElement("StringELM");
		parentE.appendChild(fldE);
		fldE.setAttribute("string", textField.getText());
	}
	

	public void focusAtEnd() {
		textField.requestFocus();
		textField.positionCaret(textField.getText().length());
	}
	
	public List<ELM> isUsedInElm(Field field) {	
		return new  ArrayList<ELM>();
	}
	
}
