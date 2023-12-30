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
package sidePanel;

import file.NFile;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import status.Status;

public class Message {
	private Pane selectorPane = new Pane();
	private Label label = new Label();	
	private Property<Status> status = new SimpleObjectProperty<Status>(Status.UNACTIVE);
	private String message;
	private String description;
	
	
	public Message(NFile nFile, String message, String description) {
		this.message = message;
		this.description = description;
		selectorPane.getStyleClass().add(status.getValue().toString());
		selectorPane.setMinSize(14, 14);
		selectorPane.setMaxSize(14, 14);
		
		if(nFile.getFileManager().getNapp().getStage().getStyle() == StageStyle.TRANSPARENT) {
			label.setStyle("-fx-text-fill: #ababab; -fx-font-size: 12;");//overwrite text, ugly work around
    	}else {
    		label.setStyle("-fx-font-size: 12; -fx-text-fill: #525e6b;");
    	}

		label.setGraphic(selectorPane);
		label.setText(message + " " + description);
		selectorPane.setOnMouseClicked(e -> {
			
			if(e.isControlDown()) {
				nFile.getMessages().remove(this);
			}
			e.consume();
		});
		status.addListener((e,a,d) -> {
			selectorPane.getStyleClass().clear(); 
			selectorPane.getStyleClass().add(status.getValue().toString());	
		});
	}
	
	public void activeClick() {

	}

	public Label getLabel() {
		label.setOnMouseClicked(e ->  this.activeClick());
		return label;		
	}

	public String getError() {
		return message;
	}

	public String getDescription() {
		return description;
	}

}
