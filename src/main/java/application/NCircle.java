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

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import layer.LAY;
import map.Nnode;

public class NCircle extends Circle {
	private Nnode nnode;
	private Timeline showTimeLine;
	private Timeline hideTimeLine;

	
	
	private NCircle(Nnode nnode) {
		this.nnode = nnode;
		this.setOpacity(0);
		this.setScaleX(0.5);
		this.setScaleY(0.5);
		this.setStroke(Color.WHITE);
		this.setFill(null);
	}

			
	public NCircle(Nnode nnode, String color, Number radius) {
		this(nnode);
		this.layoutXProperty().bind(nnode.layoutXProperty().add(10));
		this.layoutYProperty().bind(nnode.layoutYProperty().add(10));
		if (nnode.getNmap().getNapp().getMenu().getViewMenu().getGlassModeMenuItem().isSelected()) {
			this.setStyle("-fx-effect: dropshadow(gaussian, derive(" + color + ", 5%) , 8, 0.6, 0.0, 0.0);");
			this.setRadius(radius.doubleValue());
		} else {
			this.setStyle("-fx-effect: dropshadow(gaussian, derive(" + color + ", 5%) , 8, 0.2, 0.0, 0.0);");
			this.setRadius(radius.doubleValue());
		}
		
	}

	public NCircle(LAY lay, String color, Number radius) {
		this(lay.getNnode());
		this.layoutXProperty().bind(lay.getPane().layoutXProperty().add(10));
		this.layoutYProperty().bind(lay.getPane().layoutYProperty().add(10));
		if (nnode.getNmap().getNapp().getMenu().getViewMenu().getGlassModeMenuItem().isSelected()) {
			this.setStyle("-fx-effect: dropshadow(gaussian, derive(" + color + ", 5%) , 8, 0.6, 0.0, 0.0);");
			this.setRadius(radius.doubleValue());
		} else {
			this.setStyle("-fx-effect: dropshadow(gaussian, derive(" + color + ", 5%) , 8, 0.2, 0.0, 0.0);");
			this.setRadius(radius.doubleValue());
		}
	}

	public void show(int duration) {
		//check if timeline form is
		if(hideTimeLine != null && hideTimeLine.getStatus() == Status.RUNNING) hideTimeLine.stop();		
		if (nnode.getNmap().getNapp().getMenu().getViewMenu().getSimpleViewMenuItem().isSelected()) {
			if(!nnode.getNmap().contains(this)) nnode.getNmap().add(this);

			if(nnode.getNmap().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
				KeyFrame kf1 = new KeyFrame(Duration.millis(duration), new KeyValue(this.opacityProperty(), 1));
				KeyFrame kf2 = new KeyFrame(Duration.millis(duration), new KeyValue(this.scaleXProperty(), 1));
				KeyFrame kf3 = new KeyFrame(Duration.millis(duration), new KeyValue(this.scaleYProperty(), 1));
				showTimeLine = new Timeline();
				showTimeLine.getKeyFrames().addAll(kf1, kf2, kf3);
			    showTimeLine.setCycleCount(1);
			    showTimeLine.play();
			}else {
				this.setOpacity(1);
				this.setScaleX(1);
				this.setScaleY(1);
			}
			
		}
	}
	
//	public void show2() {
//		//check if timeline form is
//		if(hideTimeLine != null && hideTimeLine.getStatus() == Status.RUNNING) hideTimeLine.stop();		
//		if (nnode.nmap.napp.getMenu().getViewMenu().getSimpleViewMenuItem().isSelected()) {
//			if(!nnode.nmap.contains(this)) nnode.nmap.add(this);
//			KeyFrame kf1 = new KeyFrame(Duration.millis(100), new KeyValue(this.opacityProperty(), 1));
//			KeyFrame kf2 = new KeyFrame(Duration.millis(100), new KeyValue(this.scaleXProperty(), 1));
//			KeyFrame kf3 = new KeyFrame(Duration.millis(100), new KeyValue(this.scaleYProperty(), 1));
//
//			showTimeLine = new Timeline();
//			showTimeLine.getKeyFrames().addAll(kf1, kf2, kf3);
//		    showTimeLine.setCycleCount(1);
//		    showTimeLine.play();
//		}
//	}
	
//	public void showQuick() {
//		if(hideTimeLine != null && hideTimeLine.getStatus() == Status.RUNNING) hideTimeLine.stop();		
//		if (nnode.nmap.napp.getMenu().getViewMenu().getSimpleViewMenuItem().isSelected()) {			
//			if(!nnode.nmap.contains(this)) nnode.nmap.add(this);
//			this.setOpacity(1);
//			this.setScaleX(1);
//			this.setScaleY(1);
//		}
//	}

	public void hide(int duration) {
		if(showTimeLine != null && showTimeLine.getStatus() == Status.RUNNING) showTimeLine.stop();		
		if (nnode.getNmap().contains(this)) {
			if(nnode.getNmap().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
				KeyFrame kf1 = new KeyFrame(Duration.millis(duration), new KeyValue(this.opacityProperty(), 0));
				KeyFrame kf2 = new KeyFrame(Duration.millis(duration), new KeyValue(this.scaleXProperty(), 0.5));
				KeyFrame kf3 = new KeyFrame(Duration.millis(duration), new KeyValue(this.scaleYProperty(), 0.5));
			    hideTimeLine = new Timeline();
			    hideTimeLine.getKeyFrames().addAll(kf1, kf2, kf3);
			    hideTimeLine.setCycleCount(1);
			    hideTimeLine.setOnFinished(e -> nnode.getNmap().remove(this));
			    hideTimeLine.play();
			}else {
				this.setOpacity(0);
				this.setScaleX(0.5);
				this.setScaleY(0.5);
			}			
		}
	}
	
//	public void hide2() {
//		if(showTimeLine != null && showTimeLine.getStatus() == Status.RUNNING) showTimeLine.stop();		
//		if (nnode.nmap.contains(this)) {
//			KeyFrame kf1 = new KeyFrame(Duration.millis(100), new KeyValue(this.opacityProperty(), 0));
//			KeyFrame kf2 = new KeyFrame(Duration.millis(100), new KeyValue(this.scaleXProperty(), 0.5));
//			KeyFrame kf3 = new KeyFrame(Duration.millis(100), new KeyValue(this.scaleYProperty(), 0.5));
//
//		    hideTimeLine = new Timeline();
//		    hideTimeLine.getKeyFrames().addAll(kf1, kf2, kf3);
//		    hideTimeLine.setCycleCount(1);
//		    hideTimeLine.setOnFinished(e -> {
//				nnode.nmap.remove(this);
//			});
//		    hideTimeLine.play();
//		}
//	}
	
//	public void hideQuick() {
//		if(showTimeLine != null && showTimeLine.getStatus() == Status.RUNNING) showTimeLine.stop();		
//		if (nnode.nmap.contains(this)) {
//			nnode.nmap.remove(this);
//			this.setOpacity(0);
//			this.setScaleX(0.5);
//			this.setScaleY(0.5);
//		}
//	}

}






//package application;
//
//import javafx.animation.KeyFrame;
//import javafx.animation.KeyValue;
//import javafx.animation.Timeline;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Circle;
//import javafx.util.Duration;
//
//public class NCircle extends Circle {
//	private Nnode nnode;
//
//	public NCircle(Nnode nnode, String color, Number radius) {
//		this.nnode = nnode;
//		this.layoutXProperty().bind(nnode.layoutXProperty().add(10));
//		this.layoutYProperty().bind(nnode.layoutYProperty().add(10));
//
//		this.setStroke(Color.WHITE);
//		this.setFill(null);
//		if (nnode.nmap.napp.getMenu().getViewMenu().getGlassModeMenuItem().isSelected()) {
//			this.setStyle("-fx-effect: dropshadow(gaussian, derive(" + color + ", 5%) , 10, 0.8, 0.0, 0.0);");
//			this.setRadius(radius.doubleValue());
//		} else {
//			this.setStyle("-fx-effect: dropshadow(gaussian, derive(" + color + ", 5%) , 7, 0.4, 0.0, 0.0);");
//			this.setRadius(radius.doubleValue());
//		}
//	}
//
//	public void show() {
//		//check if timeline form is
//		if (!nnode.nmap.contains(this) && nnode.nmap.napp.getMenu().getViewMenu().getSimpleViewMenuItem().isSelected()) {
//			this.setOpacity(0);
//			this.setScaleX(0.5);
//			this.setScaleY(0.5);
//
//			nnode.nmap.add(this);
//			
//			KeyFrame kf1 = new KeyFrame(Duration.millis(600), new KeyValue(this.opacityProperty(), 1));
//			KeyFrame kf2 = new KeyFrame(Duration.millis(600), new KeyValue(this.scaleXProperty(), 1));
//			KeyFrame kf3 = new KeyFrame(Duration.millis(600), new KeyValue(this.scaleYProperty(), 1));
//
//			Timeline timeline = new Timeline();
//			timeline.getKeyFrames().addAll(kf1, kf2, kf3);
//		    timeline.setCycleCount(1);
//		    timeline.play();
//		}
//	}
//
//	public void hide() {
//		if (nnode.nmap.contains(this)) {
//			
//			KeyFrame kf1 = new KeyFrame(Duration.millis(600), new KeyValue(this.opacityProperty(), 0));
//			KeyFrame kf2 = new KeyFrame(Duration.millis(600), new KeyValue(this.scaleXProperty(), 0.5));
//			KeyFrame kf3 = new KeyFrame(Duration.millis(600), new KeyValue(this.scaleYProperty(), 0.5));
//
//		    Timeline timeline = new Timeline();
//		    timeline.getKeyFrames().addAll(kf1, kf2, kf3);
//		    
//		    timeline.setCycleCount(1);
//		    timeline.setOnFinished(e -> {
//				nnode.nmap.remove(this);
//			});
//		    timeline.play();
////		    timeline.getStatus().
//		}
//	}
//
//}
