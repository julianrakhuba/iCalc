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

import application.Constellatio;
import application.NScene;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class PopUpStage extends Stage {
	private VBox vbox = new VBox();
	private StackPane root = new StackPane(vbox);
	private Constellatio napp;
	
	private NScene nscene;
	private Region anchor;
	
	public PopUpStage(Constellatio napp, Region anchor) {
		initOwner(napp.getStage());
		initStyle(StageStyle.TRANSPARENT);		
		initModality(Modality.NONE);
		this.napp = napp;
		this.anchor = anchor;
		
		nscene = new NScene(root, napp);
		
		nscene.setFill(Color.TRANSPARENT);
		this.setScene(nscene);
		vbox.setSpacing(5);
		if (napp.getMenu().getViewMenu().getGlassModeMenuItem().isSelected()) {
			vbox.setStyle(" -fx-background-color: rgba(0, 0, 0, 0.5); "
	        		+ "-fx-border-width: 0.5;"
	        		+ "-fx-border-color: derive(#1E90FF, 50%);"
	        		+ "-fx-effect: dropshadow(gaussian, derive(#1E90FF, 40%) , 8, 0.2, 0.0, 0.0);"
	        		+ "-fx-background-radius: 5;"
	        		+ "-fx-border-radius: 5;");

		}else {
			vbox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 3 3 3 3 ;");
		}
		vbox.setPadding(new Insets(10, 10 , 10 ,10));				

		root.setStyle("-fx-background-color: transparent;");
		root.setPadding(new Insets(0, 10 , 10 ,10));				
		this.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {if (!isNowFocused) {this.hidePopUp();} });
		this.setAlwaysOnTop(true);	
		
		
		this.setOnShowing(e ->{
			if (napp.getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
				this.setOpacity(0);
				KeyFrame kf1 = new KeyFrame(Duration.millis(200), new KeyValue(this.opacityProperty(), 1));
			    Timeline timeline = new Timeline(kf1);
			    timeline.setCycleCount(1);
			    timeline.play();
			}else {
				this.setOpacity(1);
			}						    
		});
	}
	
	public void showPopUp() {
		if(!this.isShowing()) {
			Point2D pt = anchor.localToScreen(0, anchor.getHeight() + 1);
			this.setX(pt.getX() + 5);
			this.setY(pt.getY());
			this.setWidth(anchor.getWidth() - 5);		
			this.show();
		}
	}
	
	/**
	 * animated hide
	 */
	public void hidePopUp() {
		if (napp.getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
			KeyFrame kf1 = new KeyFrame(Duration.millis(200), new KeyValue(this.opacityProperty(), 0));
		    Timeline timeline = new Timeline(kf1);
		    timeline.setCycleCount(1);
		    timeline.setOnFinished(e -> super.hide());
		    timeline.play();
		}else{
			super.hide();
		}
	}

	public void add(Node item) {
		if(!vbox.getChildren().contains(item)) {
			vbox.getChildren().add(item);
		}
	}
	
}