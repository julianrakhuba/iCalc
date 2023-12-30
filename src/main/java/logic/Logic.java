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
package logic;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import layer.LAY;

public class Logic extends VBox {
	private LAY lay;
	private Pane logicPane = new Pane(this);
	private Timeline showTimeLine;
	private Timeline hideTimeLine;

	public Logic(LAY lay) {
		this.lay = lay;
		this.setMinSize(52, 18);
		this.setSpacing(1.0);
		this.getStyleClass().add("logic");
		this.setLayoutY(26);
		logicPane.setPickOnBounds(false);
		this.setOpacity(0);
		this.setScaleX(0.5);
		this.setScaleY(0.5);
	}
	
	public void show() {
		ObservableList<Node> logicGlass = lay.getNnode().getNmap().getNFile().getGlassStackPane().getChildren();
		if(hideTimeLine != null && hideTimeLine.getStatus() == Status.RUNNING) hideTimeLine.stop();
		
		if(!lay.getNnode().getNmap().getNapp().getMenu().getViewMenu().getSimpleViewMenuItem().isSelected()) {
			if(!logicGlass.contains(logicPane)) logicGlass.add(logicPane);
		}

		
		if (lay.getNnode().getNmap().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
			showTimeLine = new Timeline();
			KeyFrame kf1 = new KeyFrame(Duration.millis(300), new KeyValue(this.opacityProperty(), 1));
			KeyFrame kf2 = new KeyFrame(Duration.millis(300), new KeyValue(this.scaleXProperty(), 1));
			KeyFrame kf3 = new KeyFrame(Duration.millis(300), new KeyValue(this.scaleYProperty(), 1));
			showTimeLine.getKeyFrames().addAll(kf1, kf2, kf3);

			//move group arcs
			lay.getRootLevel().getGroupsAll().forEach(gr ->{
				gr.getArc().setOpacity(0);
				KeyFrame kf4 = new KeyFrame(Duration.millis(300), new KeyValue(gr.getArc().opacityProperty(), 1));
				showTimeLine.getKeyFrames().addAll(kf4);
			});
			
		    showTimeLine.setCycleCount(1);
		    showTimeLine.play();
		}else {
			this.setOpacity(1);
			this.setScaleX(1);
			this.setScaleY(1);
			
			lay.getRootLevel().getGroupsAll().forEach(gr ->{
				gr.getArc().setOpacity(1);
			});
		}

			
				
	}
	
	public void hide() {
		ObservableList<Node> logicGlass = lay.getNnode().getNmap().getNFile().getGlassStackPane().getChildren();
		if(showTimeLine != null && showTimeLine.getStatus() == Status.RUNNING) showTimeLine.stop();	
		
		if (lay.getNnode().getNmap().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
			 hideTimeLine = new Timeline();

			    KeyFrame kf1 = new KeyFrame(Duration.millis(300), new KeyValue(this.opacityProperty(), 0));
				KeyFrame kf2 = new KeyFrame(Duration.millis(300), new KeyValue(this.scaleXProperty(), 0.5));
				KeyFrame kf3 = new KeyFrame(Duration.millis(300), new KeyValue(this.scaleYProperty(), 0.5));
			    hideTimeLine.getKeyFrames().addAll(kf1, kf2, kf3);
			    
				lay.getRootLevel().getGroupsAll().forEach(gr ->{
					hideTimeLine.getKeyFrames().addAll(new KeyFrame(Duration.millis(300), new KeyValue(gr.getArc().opacityProperty(), 0)));
				});
				
			    hideTimeLine.setCycleCount(1);
			    hideTimeLine.setOnFinished(e -> {
					logicGlass.removeAll(logicPane);//OLD
					lay.getRootLevel().hide();
				});
			    hideTimeLine.play();
		}else {
			this.setOpacity(0);
			this.setScaleX(0.5);
			this.setScaleY(0.5);
			
			lay.getRootLevel().getGroupsAll().forEach(gr ->{
				gr.getArc().setOpacity(0);
			});
			logicGlass.removeAll(logicPane);//OLD
			lay.getRootLevel().hide();
		}

	   			
	}
}
