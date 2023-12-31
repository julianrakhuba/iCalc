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
package search;

import java.util.ArrayList;

import application.iCalc;
import application.NScene;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BetweenMenu extends Stage {
	private HBox hbox = new HBox();
	private StackPane root = new StackPane(hbox);
	private iCalc napp;
	private NScene nscene;
	
	public BetweenMenu(iCalc napp, String table, String column, ArrayList<String> distinctValues) {
		initOwner(napp.getStage());
		initStyle(StageStyle.TRANSPARENT);
		initModality(Modality.WINDOW_MODAL);
		this.napp = napp;
		nscene = new NScene(root, napp);
		nscene.setFill(Color.TRANSPARENT);
		this.setScene(nscene);
		this.setTitle("" + column);
		
		if (napp.getMenu().getViewMenu().getGlassModeMenuItem().isSelected()) {
			hbox.setStyle(" -fx-background-color: rgba(0, 0, 0, 0.5); "
	        		+ "-fx-border-width: 0.5;"
	        		+ "-fx-border-color: derive(#1E90FF, 50%);"
	        		+ "-fx-effect: dropshadow(gaussian, derive(#1E90FF, 40%) , 8, 0.2, 0.0, 0.0);"
	        		+ "-fx-background-radius: 5;"
	        		+ "-fx-border-radius: 5;");

		}else {
			hbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 2 2 2 2;");			
		}
		hbox.setPadding(new Insets(10, 10 , 10 ,10));
		hbox.setSpacing(15);
		
		root.setStyle("-fx-background-color: transparent;");
		root.setPadding(new Insets(0, 10 , 10 ,10));

		this.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {if (!isNowFocused) {this.hide();} });
		this.setAlwaysOnTop(true);
		
		ListView<String> listViewA = new ListView<String>();
		ListView<String> listViewB = new ListView<String>();
		
		HBox.setHgrow(listViewA, Priority.ALWAYS);
		HBox.setHgrow(listViewB, Priority.ALWAYS);

		distinctValues.forEach(str -> {
			listViewA.getItems().add(str);
			listViewB.getItems().add(str);
			
		});

		listViewA.setOnKeyPressed(e -> {
			if (e.getCode().toString().equals("ENTER")) {
				if(listViewB.getSelectionModel().getSelectedItems().size() == 1) {
					this.prepareSearch(table, column, listViewA.getSelectionModel().getSelectedItem(), listViewB.getSelectionModel().getSelectedItem());
				}
			}
		});
		
		listViewB.setOnKeyPressed(e -> {
			if (e.getCode().toString().equals("ENTER")) {
				if(listViewA.getSelectionModel().getSelectedItems().size() == 1) {
					this.prepareSearch(table, column, listViewA.getSelectionModel().getSelectedItem(), listViewB.getSelectionModel().getSelectedItem());
				}
			}
		});

		this.hbox.getChildren().addAll(listViewA, listViewB);
		Point2D pt = napp.getUpperPane().getSearchTextField().localToScreen(0, napp.getUpperPane().getSearchTextField().getHeight() + 1);
		this.setX(pt.getX() + 15);
		this.setY(pt.getY());
		this.setMaxHeight(200);
		this.setWidth(napp.getUpperPane().getSearchTextField().getWidth() - 30);
		this.show();
	}
	
	private void prepareSearch(String table, String column, String fromValue, String toValue) {
		String from;
		String to;
		try {
			if(Double.valueOf(toValue).compareTo(Double.valueOf(fromValue)) >= 0) {
				from = fromValue;
				to = toValue;
			}else {
				from = toValue;
				to = fromValue;
			}
		} catch (NumberFormatException e){
			from = fromValue;
			to = toValue;
		}

		napp.getFilemanager().getActiveNFile().getActivity().newSearchBETWEEN(napp.getFilemanager().getActiveNFile().getActiveNmap().getNnode(table), column, from, to);		
		this.hide();
		napp.getUpperPane().getSearchTextField().clear();
		napp.getUpperPane().getSearchTextField().requestFocus();
	}
	
}