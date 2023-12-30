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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import logic.Field;
import logic.SQL;
import status.ColorMode;
import status.FieldPivot;

public class FieldELM extends ELM{
	private String unactiveStyle = "-fx-background-color: #f0f0f0; -fx-text-fill: #708090;  -fx-background-radius: 10 10 10 10;  -fx-border-radius: 10 10 10 10;" ;
	private String activeStylePivotized = "-fx-effect: dropshadow(three-pass-box, #ffcf0f, 2, 0.5, 0, 0.5); -fx-background-color: #f0f0f0; -fx-text-fill: #708090;  -fx-background-radius: 10 10 10 10;  -fx-border-radius: 10 10 10 10;" ;
	private Label label = new Label();
	private Field field;
	private Property<FieldPivot> pivotized = new SimpleObjectProperty<FieldPivot>(FieldPivot.BASIC);
	
	public FieldELM(Field field, RootELM rootELM) {
		super(rootELM);
		label.setStyle(unactiveStyle);
		this.field = field;
		label.setText(" " + field.getLabelText() + " ");
		label.setTooltip(new Tooltip(field.getFieldLay().getAliase()));
		label.setPadding(new Insets(0,0,0,0));
		label.setMaxHeight(10);
		
		label.setOnMouseClicked(e -> {
			if(getRootELM().getFormula() != null && e.isControlDown()) {
				if(pivotized.getValue() == FieldPivot.BASIC) {
					pivotized.setValue(FieldPivot.PIVOTIZED);
				}else {
					pivotized.setValue(FieldPivot.BASIC);
				}
			}else {
				//focus click
				this.parent.cursorBox.focusNextTo(e, this);
				field.getFieldLay().getStyler().pulse(ColorMode.EDIT);
			}
			e.consume();
		});

		pivotized.addListener((a,b,c) -> this.updateStyle());
		if(getRootELM().getFormula() != null) pivotized.setValue(FieldPivot.PIVOTIZED);		
	}
	
	public FieldELM(OpenContext context, org.w3c.dom.Node n, RootELM rootELM) {
		this(context.getFields().get(XML.atr(n, "fieldAliase")), rootELM);
		pivotized.setValue(FieldPivot.valueOf(XML.atr(n, "pivotized")));
	}
	
	private void updateStyle() {	
		if(pivotized.getValue() == FieldPivot.PIVOTIZED) {
			label.setStyle(activeStylePivotized);
		}else {
			label.setStyle(unactiveStyle);
		}
	}

	//OUTPUT •••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
	public String getLabelText() {
		return  "" + field.getLabelText() + "";
	}
	
	public String getSideLabelText() {
		return  "" + field.getFieldLay().getNnode().getTable() + "";
	}
	
	public void buildSQL(SQL sql) {
		sql.addNText(new NText(field.getFunction_Column(), field.getFieldLay()));//TODO create ntext instace localy
	}

	public String getPivotStringSQL(Field pivotField, String val) {
		//makes pivot optional at field level, need to add pivotized option when creating columns 
		if(pivotized.getValue() == FieldPivot.PIVOTIZED) {
			return " CASE WHEN " + pivotField.getFunction_Column() +" = '" + val + "' THEN " + field.getFunction_Column() + " END";
		}else {
			return field.getFunction_Column();
		}
	}
	
	public Node getNode() {
		return label;
	}

	public Field getField() {
		return field;
	}

	public void saveXml(Document doc, Element parentE) {
		Element fldE = doc.createElement("FieldELM");
		parentE.appendChild(fldE);
		fldE.setAttribute("fieldAliase", field.getAliase());
		fldE.setAttribute("pivotized", pivotized.getValue().toString());				
	}

	public List<ELM> isUsedInElm(Field field) {
		 List<ELM> used = new  ArrayList<ELM>();
		if(this.field == field) used.add(this);		
		return used;
	}

	public void styleFocused() {
		
	}

	public void styleUnfocused() {
		
	}


}
