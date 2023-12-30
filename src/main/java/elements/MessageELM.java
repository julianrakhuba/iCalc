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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import logic.Field;
import logic.SQL;
import sidePanel.Message;

public class MessageELM extends ELM{
	private Label label = new Label();
	private Message error;
	private String errorStyle = "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 2, 0.2, 0, 0); -fx-text-fill: white; -fx-background-color: linear-gradient(#ffebeb, #ff6161), radial-gradient(center 50% -40%, radius 200%, #ff6161 45%, #ff3d3d 50%); -fx-background-radius: 10 10 10 10";
	
	public MessageELM(Message message, RootELM rootELM) {
		super(rootELM);
		this.error = message;
		label.setPadding(new Insets(0,5,0,5));
		this.styleUnfocused();
		label.setAlignment(Pos.CENTER);
		label.setText(message.getError());
		label.setTooltip(new Tooltip(message.getDescription()));
		label.focusedProperty().addListener((a, b, c) -> {
			if (c) {
				this.styleFocused();
			}else {
				this.styleUnfocused();
			}
		});
		label.setStyle(errorStyle);
	}
	
	public void styleFocused() {
		label.setStyle(errorStyle);
	}

	public void styleUnfocused() {
		label.setStyle(errorStyle);
	}

	//OUTPUT •••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
	public String getLabelText() {
		return " [" + label.getText() + "] ";
	}
	
	public String getSideLabelText() {
		return " [" + label.getText() + "] ";
	}
	
	public void buildSQL(SQL sql) {}
	
	public String getPivotStringSQL(Field pvtFld, String val) {
		return "";
	}

	
	public Node getNode() {
		return label;
	}

	public void saveXml(Document doc, Element parentE) {
		Element fldE = doc.createElement("MessageELM");
		parentE.appendChild(fldE);
		fldE.setAttribute("message", error.getError());
		fldE.setAttribute("description",error.getDescription());
	}
	
	public List<ELM> isUsedInElm(Field field) {	
		return new  ArrayList<ELM>();
	}
}
