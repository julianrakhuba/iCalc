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
package application;

import file.NFile;
import file.NSheet;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class BottomBar extends ToolBar {
	private InfoLabel rowsCount = new InfoLabel("rows:");
	private InfoLabel sumLabel = new InfoLabel("sum:");
	private InfoLabel countLabel = new InfoLabel("count:");
	private Pane spacerA = new Pane();
	private HBox centerBar = new HBox();
	private Pane spacerB = new Pane();
	private ConnectionLight light = new ConnectionLight();
	
	private HBox centerBarA = new HBox();
	private HBox formaters = new HBox();
	
	private BottomButton chartBtn;
	private BottomButton chartTgl;

	private BottomButton consoleBtn;
	private BottomButton listBtn;
	private BottomButton gridBtn;
	

	public BottomBar(iCalc constellatio) {
		HBox.setHgrow(spacerA, Priority.SOMETIMES);
		HBox.setHgrow(spacerB, Priority.SOMETIMES);
		
		chartBtn = new BottomButton(constellatio,"chartButton");
		chartTgl = new BottomButton(constellatio,"chartToggleButton");
		consoleBtn = new BottomButton(constellatio,"consoleButton");
		listBtn = new BottomButton(constellatio,"listButton");
		gridBtn = new BottomButton(constellatio,"gridButton");
		
		this.getItems().addAll(spacerA, centerBar, spacerB,gridBtn,chartBtn,chartTgl,  new Separator(),  consoleBtn, listBtn, light);
		centerBar.setSpacing(3.0);		
		centerBarA.setSpacing(3.0);
		centerBarA.setAlignment(Pos.CENTER_LEFT);
		centerBarA.getChildren().addAll(rowsCount, sumLabel, countLabel);
		formaters.setSpacing(3.0);
		formaters.setAlignment(Pos.CENTER_LEFT);
		centerBar.getChildren().addAll(centerBarA, formaters);
		
		consoleBtn.setOnMouseClicked(e ->{
			constellatio.toggleConsole();
		});
		
		gridBtn.setOnMouseClicked(e ->{
			NFile f = constellatio.getFilemanager().getActiveNFile();
			if(f !=null) f.getTabManager().buttonClick();
		});
		
		chartBtn.setOnMouseClicked(e ->{
			NFile f = constellatio.getFilemanager().getActiveNFile();
			if(f !=null) {
			Tab currentTab = f.getTabManager().getSelectionModel().getSelectedItem();
				if(currentTab != null) {
					((NSheet) currentTab).showHideChart();
				}
			}
		});
		
		chartTgl.setOnMouseClicked(e ->{
			NFile f = constellatio.getFilemanager().getActiveNFile();
			if(f !=null) {
			Tab currentTab = f.getTabManager().getSelectionModel().getSelectedItem();
				if(currentTab != null) {
					((NSheet) currentTab).toggleChart();
				}
			}
		});
		
		listBtn.setOnMouseClicked(e ->{
			NFile f = constellatio.getFilemanager().getActiveNFile();
			if(f !=null) f.getSidePane().buttonClick();
		});
	
	}
	
	public InfoLabel getSumLabel() {
		return sumLabel;
	}

	public InfoLabel getCountLabel() {
		return countLabel;
	}

	public InfoLabel getRowsCount() {
		return rowsCount;
	}

	public ConnectionLight getLight() {
		return light;
	}

	public HBox getFormaters() {
		return formaters;
	}

}
