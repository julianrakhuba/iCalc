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
package pivot;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class NSelector extends SimpleObjectProperty<Boolean> {
	private Pane pane = new Pane();
	private Label label = new Label();

	public NSelector() {
		this.setValue(false);
		pane.setMinSize(14,14); 
		pane.setMaxSize(14,14);
		pane.getStyleClass().add("grayMenuSelector");//default		
		label.setGraphic(pane);
		label.setTextFill(Color.rgb(60,60,60));
//		label.setMinWidth(120);
		
		this.addListener((a,b,c) ->{
			pane.getStyleClass().clear();
			if(c) pane.getStyleClass().add("blueMenuSelector");
			else pane.getStyleClass().add("grayMenuSelector");
		});
	}

	public NSelector(String string) {
		this();
		label.setText(string);
	}

	public NSelector(String string, boolean b) {
		this();
		label.setText(string);
//  	sideLabel.setStyle("-fx-text-fill: #ababab; -fx-font-size: 12;");//overwrite text, ugly work around
		label.setStyle("-fx-font-size: 12; -fx-text-fill: #ababab;");
	}

	public Label getLabel() {
		return label;
	}
	
	public Pane getPane() {
		return pane;
	}

}
