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
import java.util.ArrayList;

import file.NFile;
import file.NSheet;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;
import map.NMap;
import status.VisualStatus;

public class TabManager extends TabPane {
	private NFile nfile;
	private Property<VisualStatus> status = new SimpleObjectProperty<VisualStatus>(VisualStatus.UNAVALIBLE);
	private StackPane stackPane;

	public TabManager(NFile nfile) {
		this.nfile = nfile;
		this.setSide(Side.TOP);
		this.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		this.setPrefHeight(250);			
		
		this.getSelectionModel().selectedItemProperty().addListener((a,b,c)->{
			if(c instanceof NSheet) {
				NSheet nsheet = (NSheet)c;
				nfile.getFileManager().getNapp().getBottomBar().getRowsCount().setCountValue(nsheet.getLay().getItems().size());
			}else {
				nfile.getFileManager().getNapp().getBottomBar().getRowsCount().clear();
			}
		});	
		stackPane = new StackPane(this);

		if(nfile.getFileManager().getNapp().getStage().getStyle() == StageStyle.TRANSPARENT) {
			stackPane.setStyle(" -fx-padding: 0 5 0 0; -fx-background-color: transparent;");		
			this.setStyle("-fx-border-width: 0; -fx-border-color: transparent; -fx-padding: 0 0 0 5; -fx-background-color: transparent; -fx-background-radius: 3; -fx-effect: dropshadow(two-pass-box , rgba(0, 0, 0, 0.3), 10, 0.0 , 0, 0);");

		}else {
			stackPane.setStyle("-fx-effect: dropshadow(two-pass-box , rgba(0, 0, 0, 0.05), 5, 0.4 , 2, 2); -fx-padding: 0; -fx-background-color: transparent;");		
			this.setStyle("-fx-effect:dropshadow(two-pass-box , white, 5, 0.4 , -2, -2); -fx-border-width: 0; -fx-border-color: transparent; -fx-padding: 5; -fx-background-color: transparent; -fx-background-radius: 3;");	
		}
		
	}
	
	public void buttonClick() {
		if(status.getValue() == VisualStatus.SHOW) {
			status.setValue(VisualStatus.HIDE);
			this.hideGrid();
		}else if(status.getValue() == VisualStatus.HIDE){
			this.showGrid();
		}
	}

	public void selectTab(Tab tab) {
		if(!this.getTabs().contains(tab)) {
			this.getTabs().add(tab);
		}
		Tab currentTab = this.getSelectionModel().getSelectedItem();
		if(currentTab == null || currentTab instanceof NSheet) {
			this.getSelectionModel().select(tab);
		}
	}	

	public void removeTab(Tab tab) {
		this.getTabs().remove(tab);
		if(this.getTabs().size() == 0)  {
			status.setValue(VisualStatus.UNAVALIBLE);
			this.hideGrid();		
		}
	}
	
	public void removeNSheetFor(NMap nmap) {
		ArrayList<Tab> panesToRemove = new ArrayList<Tab>();
		this.getTabs().forEach(tab -> {
			if(((NSheet)tab).getLay().getNnode().getNmap() == nmap) panesToRemove.add(tab);
		});
		panesToRemove.forEach(sheet -> this.removeTab(sheet));
	}
	
	//MOVE TAB SPLIT TO FILE
	public void showGrid() {
		status.setValue(VisualStatus.SHOW);	
		nfile.getQuadSplit().setBottomLeft(stackPane);
	}
	
	private void hideGrid() {
		nfile.getQuadSplit().setBottomLeft(null);
	}

	public void clear() {
		this.getTabs().clear();
		status.setValue(VisualStatus.UNAVALIBLE);
		this.hideGrid();
	}
	
	//used to create document for file or undo
	public VisualStatus getStatus() {
		return status.getValue();
	}
	
	//used in open file and undo
	public void setStatus(VisualStatus vs) {
		this.status.setValue(vs);
	}
}
