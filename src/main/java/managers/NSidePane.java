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
package managers;

import file.NFile;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import layer.LAY;
import status.VisualStatus;

public class NSidePane extends StackPane {
	private NFile nfile;
	private Property<VisualStatus> status = new SimpleObjectProperty<VisualStatus>(VisualStatus.SHOW);
	
	private VBox listVBox = new VBox();
	private ScrollPane scrollPane = new ScrollPane();

	public NSidePane(NFile nfile) {
		this.nfile = nfile;
		listVBox.getChildren().addAll(nfile.getMessagesRegion());
		scrollPane.setMinWidth(0);
		scrollPane.setMinHeight(0);
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		listVBox.setStyle("-fx-background-color: transparent;  -fx-padding: 10,5,10,10; -fx-spacing: 12;");	
		scrollPane.setContent(listVBox);
		this.getChildren().add(scrollPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        		
		if(nfile.getFileManager().getNapp().getStage().getStyle() == StageStyle.TRANSPARENT) {
			scrollPane.setStyle(" -fx-background-color: rgba(0, 0, 0, 0.5); "
	        		+ "-fx-border-width: 0.5;"
	        		+ "-fx-border-color: derive(#1E90FF, 50%);"
	        		+ "-fx-effect: dropshadow(gaussian, derive(#1E90FF, 40%) , 8, 0.2, 0.0, 0.0);"
	        		+ "-fx-background-radius: 3;"
	        		+ "-fx-border-radius: 3;");
			this.setStyle("-fx-background-color: transparent; -fx-padding: 5 5 5 2.5;");
		}else {
			scrollPane.setStyle("-fx-effect: dropshadow(two-pass-box , rgba(0, 0, 0, 0.05), 5, 0.4 , 2, 2); -fx-background-color: rgb(234, 236, 241); -fx-background-radius: 7;");
			this.setStyle("-fx-effect:dropshadow(two-pass-box , white, 5, 0.4 , -2, -2); -fx-background-color: transparent; -fx-padding: 5 5 5 5;");
		}

	}
	
	public void buttonClick() {
		if(status.getValue() == VisualStatus.SHOW) {
			status.setValue(VisualStatus.HIDE);
			this.hideSidePane();
		}else if(status.getValue() == VisualStatus.HIDE){
			status.setValue(VisualStatus.SHOW);
			this.showSidePane();
		}
	}
	
	public void close() {

	}

	public void activateSearch(LAY lay) {
		if(status.getValue() != VisualStatus.HIDE) {
			status.setValue(VisualStatus.SHOW);
			this.showSidePane();
		}
		lay.updateRowCount();
		listVBox.getChildren().clear();
		listVBox.getChildren().addAll(lay.getSearchRegion());
		
		if(lay.isValidForOptions()) {
			listVBox.getChildren().addAll(lay.getOptionsRegion());
		}	
	}
	
	public void activateFormula(LAY lay) {
		if(status.getValue() != VisualStatus.HIDE) {
			status.setValue(VisualStatus.SHOW);
			this.showSidePane();
		}
		lay.updateRowCount();
		listVBox.getChildren().clear();
		listVBox.getChildren().addAll(lay.getSearchRegion());
		if(lay.isValidForOptions()) {
			listVBox.getChildren().addAll(lay.getOptionsRegion());
		}
	}

	
	public void deactivate() {
		listVBox.getChildren().clear();
		listVBox.getChildren().addAll(nfile.getMessagesRegion());
	}
		

	public void showSidePane() {			
		nfile.getQuadSplit().setTopRight(this);
	}
	
	public void hideSidePane() {
		nfile.getQuadSplit().setTopRight(null);
	}
	
	public VisualStatus getStatus() {
		return status.getValue();
	}
	
	public void setStatus(VisualStatus vs) {
		this.status.setValue(vs);
	}
}
