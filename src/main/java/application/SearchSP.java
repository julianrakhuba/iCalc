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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import search.Search;
import status.ActivityMode;


public class SearchSP extends StackPane {
	private Constellatio constellatio;
	private HBox overlapBox = new HBox(-15);
	private FunctionsButton functionsButton;
	private ContextMenu searchContext;
	private Search searchTextField;
	private ScrollPane scrollpane = new ScrollPane();
	private boolean usescroll = true;
	
	private Pane holderPane = new Pane();

	
	public SearchSP(Constellatio constellatio) {
		this.getStyleClass().add("newSearchBar");
		
		this.constellatio = constellatio;
		functionsButton = new FunctionsButton("", constellatio);
		searchContext = new ContextMenu();
		
		if (constellatio.getMenu().getViewMenu().getGlassModeMenuItem().isSelected()) {
			searchContext.setSkin(new SkinFix(searchContext));
		}
		searchTextField = new Search(constellatio, this);
		searchTextField.minWidthProperty().bind(this.widthProperty().divide(1.7225));
		
//		bind text search to scroll width and little shorter		
		scrollpane.minWidthProperty().bind(this.widthProperty().divide(1.7));
		scrollpane.maxWidthProperty().bind(this.widthProperty().divide(1.7));
		
		scrollpane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollpane.setVbarPolicy(ScrollBarPolicy.NEVER);
		String focusedCursorBox = "-fx-padding: 0 5 0 5; -fx-effect: innershadow(three-pass-box, #99ddff, 4, 0.5, 0, 0); -fx-background-color: white; -fx-text-fill: #9DA1A1; -fx-border-width: 0 ; -fx-background-radius: 15 15 15 15;  -fx-border-radius: 15 15 15 15;" ;
		scrollpane.setStyle(focusedCursorBox);
		scrollpane.setMaxHeight(30);
		scrollpane.minViewportHeightProperty().bind(searchTextField.heightProperty().add(0));		
		overlapBox.getChildren().addAll(functionsButton, holderPane);
		overlapBox.maxWidthProperty().bind(this.widthProperty().divide(1.8));
		this.setRegularSearch();

		searchContext.setOnHiding(e -> {
			searchContext.getItems().clear();
			if (constellatio.getFilemanager().getActiveNFile().getActivityMode() == ActivityMode.VIEW) {
				constellatio.getFilemanager().getActiveNFile().getActivity().closeActivity();
				constellatio.getFilemanager().getActiveNFile().setActivityMode(ActivityMode.SELECT);
				constellatio.getFilemanager().getActiveNFile().getSidePane().deactivate();
			}
		});
		
		searchContext.setOnShowing(e ->{
			searchContext.setOpacity(0);
			KeyFrame kf1 = new KeyFrame(Duration.millis(200), new KeyValue(searchContext.opacityProperty(), 1));
		    Timeline timeline = new Timeline(kf1);
		    timeline.setCycleCount(1);
		    timeline.play();
		});
		
		functionsButton.setMinHeight(30);
		functionsButton.setMaxHeight(30);
		functionsButton.setMinWidth(40);
		functionsButton.setMaxWidth(40);
		
		overlapBox.setAlignment(Pos.CENTER);
		this.getChildren().add(overlapBox);
	}

	public FunctionsButton getFunctionsButton() {
		return functionsButton;
	}

	public ContextMenu getSearchContext() {
		return searchContext;
	}

	/**
	 * @return Pane for PopUp Stage anchor
	 */
	public Pane getPlaceHolder() {
		return overlapBox;
	}
	
	public void setFormulaSearch(Pane formulaHBox) {
		overlapBox.getChildren().clear();
		if(usescroll) {
			scrollpane.setContent(formulaHBox);
			overlapBox.getChildren().addAll(functionsButton, scrollpane);
			formulaHBox.minWidthProperty().bind(this.widthProperty().divide(1.7225));
		}else {
			overlapBox.getChildren().addAll(functionsButton, formulaHBox);
			formulaHBox.minWidthProperty().bind(this.widthProperty().divide(1.7225));
		}
	}

	public void setRegularSearch() {
		overlapBox.getChildren().clear();
		if(usescroll) {
			scrollpane.setContent(searchTextField);
			overlapBox.getChildren().addAll(functionsButton, scrollpane);
			
			searchTextField.setEffect(null);
		}else {
			overlapBox.getChildren().addAll(functionsButton, searchTextField);
		}
	}
	
	public void funcMenuClick(Node anchor) {
		if (!searchContext.isShowing()) {
			if (constellatio.getFilemanager().getActiveNFile() != null) {
				constellatio.getFilemanager().getActiveNFile().getActivity().rebuildFieldMenu();
			}
			if (!searchContext.isShowing() && searchContext.getItems().size() > 0) {
				if (anchor == null) {
					searchContext.show(overlapBox, Side.BOTTOM, 40, 2);
				} else {
					searchContext.show(anchor, Side.BOTTOM, 15, 2);
				}
			}
		} else {
			searchContext.hide();
		}
	}

	public Search getSearchTextField() {
		return searchTextField;
	}
}
