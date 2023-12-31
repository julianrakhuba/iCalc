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
import javafx.animation.Animation.Status;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class BottomButton extends Pane {

	
	private Timeline showTimeLine;
	private Timeline hideTimeLine;
	private iCalc constellatio;
	
	
	public BottomButton(iCalc constellatio, String style) {
		this.constellatio = constellatio;
		this.getStyleClass().add(style);		
		this.setOnMouseEntered(e -> this.zoomIn());
		this.setOnMouseExited(e -> this.zoomOut());
	}
	
	public void zoomIn() {
		if(hideTimeLine != null && hideTimeLine.getStatus() == Status.RUNNING) hideTimeLine.stop();		
		if (constellatio.getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
			KeyFrame kf2 = new KeyFrame(Duration.millis(100), new KeyValue(this.scaleXProperty(), 1.3));
			KeyFrame kf3 = new KeyFrame(Duration.millis(100), new KeyValue(this.scaleYProperty(), 1.3));
			showTimeLine = new Timeline();
			showTimeLine.getKeyFrames().addAll(kf2, kf3);
		    showTimeLine.setCycleCount(1);
		    showTimeLine.play();
		}else {
			this.setScaleX(1.3);
			this.setScaleY(1.3);
		}	
	}

	public void zoomOut() {
		if(showTimeLine != null && showTimeLine.getStatus() == Status.RUNNING) showTimeLine.stop();
		if (constellatio.getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
			KeyFrame kf2 = new KeyFrame(Duration.millis(100), new KeyValue(this.scaleXProperty(), 1));
			KeyFrame kf3 = new KeyFrame(Duration.millis(100), new KeyValue(this.scaleYProperty(), 1));
		    hideTimeLine = new Timeline();
		    hideTimeLine.getKeyFrames().addAll(kf2, kf3);
		    hideTimeLine.setCycleCount(1);
		    hideTimeLine.play();
		}else {
			this.setScaleX(1);
			this.setScaleY(1);
		}	
	}

}
