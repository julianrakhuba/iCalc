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
package file;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class QuadSplit extends SplitPane {
	private SplitPane upper = new SplitPane();
	private SplitPane lower = new SplitPane();

	private Timeline showInfoTl;
	private Timeline hideInfoTl;
	
	private Timeline shoewChartTl;
	private Timeline hideChartTl;
	
	private Timeline showBottomTl;
	private Timeline hideBottomTl;
	
	private Timeline shoewGridTl;
	private Timeline hideGridTl;
	
	private Region topLeft;
	private Region topRight;
	private Region bottomLeft;
	private Region bottomRight;
	private NFile nfile;
//	private boolean anumation = false;
	
	public QuadSplit(NFile nfile) {
		this.nfile = nfile;
		this.setOrientation(Orientation.VERTICAL);
		this.setStyle("-fx-background-color: rgba(0,0,0,0);");
		
		if(nfile.getFileManager().getNapp().getStage().getStyle() == StageStyle.TRANSPARENT) {
			upper.setStyle("-fx-background-color: rgba(0,0,0,0);");
			lower.setStyle("-fx-background-color: rgba(0,0,0,0);");
		}else {
			upper.setStyle("-fx-background-color: rgb(234, 236, 241);");
			lower.setStyle("-fx-background-color: rgb(234, 236, 241);");
		}
	

		upper.setMinHeight(0);
		lower.setMinHeight(0);
		this.getItems().addAll(upper);
	}

	public void setTopLeft(Region region) {
		if(topLeft == null && region != null) {//new
			upper.getItems().add(0,region);
		}else if(topLeft != null && region != null) {//swop
			if(topLeft != region) {
				upper.getItems().remove(topLeft);
				upper.getItems().add(0,region);
			}
		}else {
			upper.getItems().remove(topLeft);
		}
		topLeft = region;
	}
	
	public void setTopRight(Region region) {
		if(topRight == null && region != null) {//new
			this.showTopRight(region);
		}else if(topRight != null && region != null) {//swop
			if(topRight != region) {
				upper.getItems().remove(topRight);
				upper.getItems().add(1,region);
				if(upper.getDividers().size()>0) upper.getDividers().get(0).setPosition(0.82);//DO I NEED DEVIDER UPDATE ON SWOP???
			}
		}else {
			hideTopRight(topRight);
		}
		topRight = region;
	}
	
	private void showTopRight(Region region) {
		if(hideInfoTl != null && hideInfoTl.getStatus() == Status.RUNNING) hideInfoTl.stop();
		if(!upper.getItems().contains(region)) {
			upper.getItems().add(1,region);
			region.setOpacity(0);
			Divider div = upper.getDividers().get(0);
			div.setPosition(1);
			
//			if (nfile.getFileManager().napp.getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
//				
//			}else {
//				
//			}
			
			if (nfile.getFileManager().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
				KeyFrame kf1 = new KeyFrame(Duration.millis(200), new KeyValue(div.positionProperty(), 0.82));
				KeyFrame kf2 = new KeyFrame(Duration.millis(200), new KeyValue(region.opacityProperty(), 1));
				showInfoTl = new Timeline();
				showInfoTl.getKeyFrames().addAll(kf1, kf2);
			    showInfoTl.setCycleCount(1);
			    showInfoTl.play();
			}else {
				div.setPosition(0.82);
				region.setOpacity(1);	
			}
		}
	}
		
	private void hideTopRight(Region region) {
		if(showInfoTl != null && showInfoTl.getStatus() == Status.RUNNING) showInfoTl.stop();				
		if (upper.getItems().contains(region)) {
			Divider div = upper.getDividers().get(0);
			
			if (nfile.getFileManager().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
				KeyFrame kf1 = new KeyFrame(Duration.millis(200), new KeyValue(div.positionProperty(), 1));
				KeyFrame kf2 = new KeyFrame(Duration.millis(200), new KeyValue(region.opacityProperty(), 0));
			    hideInfoTl = new Timeline();
			    hideInfoTl.getKeyFrames().addAll(kf1, kf2);
			    hideInfoTl.setCycleCount(1);
			    hideInfoTl.setOnFinished(e -> {
			    	upper.getItems().remove(region);
				});
			    hideInfoTl.play();
			}else {
				upper.getItems().remove(region);
				div.setPosition(1);
				region.setOpacity(0);
			}
			
		}
	}
	
	//BOTTOM *******************************************************************************
	public void setBottomRight(Region region) {
		if(!this.getItems().contains(lower)) this.getItems().addAll(lower);//move to animate

		if(bottomRight == null && region != null) {//new
			this.showBottomRight(region);
		}else if(bottomRight != null && region != null) {//swop
			if(bottomRight != region) {
				lower.getItems().remove(bottomRight);
				lower.getItems().add(region);
			}
		}else {
			hideBottomRight(bottomRight);
		}
		bottomRight = region;
	}
	
	private void showBottomRight(Region region) {
		if(lower.getItems().size() > 0) {
			//add
			if(hideChartTl != null && hideChartTl.getStatus() == Status.RUNNING) hideChartTl.stop();
			if(!lower.getItems().contains(region)) {
				lower.getItems().add(region);
				region.setOpacity(0);			
				Divider div = lower.getDividers().get(0);
				div.setPosition(1);
				if (nfile.getFileManager().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
					KeyFrame kf1 = new KeyFrame(Duration.millis(400), new KeyValue(div.positionProperty(), 0.5));
					KeyFrame kf2 = new KeyFrame(Duration.millis(400), new KeyValue(region.opacityProperty(), 1));
					shoewChartTl = new Timeline();
					shoewChartTl.getKeyFrames().addAll(kf1, kf2);
					shoewChartTl.setCycleCount(1);
				    shoewChartTl.play();
				}else {
					div.setPosition(0.5);
					region.setOpacity(1);
				}				
			}
		}else {
			showBottom(region);//new USE BOTTOM LEFT
		}		
	}

	private void hideBottomRight(Region region) {
		if(lower.getItems().size() == 2) {
			if(shoewChartTl != null && shoewChartTl.getStatus() == Status.RUNNING) shoewChartTl.stop();				
			if (lower.getItems().contains(region)) {
				Divider div = lower.getDividers().get(0);
				
				if (nfile.getFileManager().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
					KeyFrame kf1 = new KeyFrame(Duration.millis(400), new KeyValue(div.positionProperty(), 1));
					KeyFrame kf2 = new KeyFrame(Duration.millis(400), new KeyValue(region.opacityProperty(), 0));
					hideChartTl = new Timeline();
					hideChartTl.getKeyFrames().addAll(kf1, kf2);
					hideChartTl.setCycleCount(1);
					hideChartTl.setOnFinished(e -> {
						lower.getItems().remove(region);
						if(lower.getItems().size() == 0) this.getItems().remove(lower);//make it optional
					});
					hideChartTl.play();
				}else {
					lower.getItems().remove(region);
					if(lower.getItems().size() == 0) this.getItems().remove(lower);//make it optional					
					div.setPosition(1);
					region.setOpacity(0);
				}				
			}
		}else {
			hideBottom(region);
		}	
	}

	
	private void showBottomLeft(Region region) {
		if(lower.getItems().size() > 0) {
			//add
			if(hideGridTl != null && hideGridTl.getStatus() == Status.RUNNING) hideGridTl.stop();
			if(!lower.getItems().contains(region)) {
				lower.getItems().add(0,region);
				region.setOpacity(0);			
				Divider div = lower.getDividers().get(0);
				div.setPosition(0);
				
				if (nfile.getFileManager().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
					KeyFrame kf1 = new KeyFrame(Duration.millis(400), new KeyValue(div.positionProperty(), 0.5));
					KeyFrame kf2 = new KeyFrame(Duration.millis(400), new KeyValue(region.opacityProperty(), 1));
					shoewGridTl = new Timeline();
					shoewGridTl.getKeyFrames().addAll(kf1, kf2);
					shoewGridTl.setCycleCount(1);
					shoewGridTl.play();
				}else {
					div.setPosition(0.5);
					region.setOpacity(1);
				}
			}
		}else {
			showBottom(region);//new USE BOTTOM LEFT
		}		
	}
	

	private void hideBottomLeft(Region region) {
		if(lower.getItems().size() == 2) {
			if(shoewGridTl != null && shoewGridTl.getStatus() == Status.RUNNING) shoewGridTl.stop();				
			if (lower.getItems().contains(region)) {
				Divider div = lower.getDividers().get(0);
				if (nfile.getFileManager().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
					KeyFrame kf1 = new KeyFrame(Duration.millis(400), new KeyValue(div.positionProperty(), 0));
					KeyFrame kf2 = new KeyFrame(Duration.millis(400), new KeyValue(region.opacityProperty(), 0));
					hideGridTl = new Timeline();
					hideGridTl.getKeyFrames().addAll(kf1, kf2);
					hideGridTl.setCycleCount(1);
					hideGridTl.setOnFinished(e -> {
						lower.getItems().remove(region);
						if(lower.getItems().size() == 0) this.getItems().remove(lower);//make it optional
					});
					hideGridTl.play();
				}else {					
					div.setPosition(0);
					region.setOpacity(0);
					lower.getItems().remove(region);
					if(lower.getItems().size() == 0) this.getItems().remove(lower);//make it optional	
				}				
			}
		}else {
			hideBottom(region);
		}
		
	}
	//••••••••••••••••••••••••••••••••••••8
	
	public void setBottomLeft(Region region) {
		if(!this.getItems().contains(lower)) this.getItems().addAll(lower);//move to animate

		if(bottomLeft == null && region != null) {//new
			this.showBottomLeft(region);
		}else if(bottomLeft != null && region != null) {//swop
				lower.getItems().remove(bottomLeft);
				lower.getItems().add(0,region);
				if(lower.getDividers().size() > 1) {
					Divider div = lower.getDividers().get(0);//DO I NEED DEVIDER UPDATE ON SWOP???
					div.setPosition(0.55);
				}
		}else {
			hideBottomLeft(bottomLeft);
		}
		bottomLeft = region;
	}
	
	private void showBottom(Region region) {
		if(hideBottomTl != null && hideBottomTl.getStatus() == Status.RUNNING) hideBottomTl.stop();
		
		if(!this.getItems().contains(lower)) this.getItems().add(lower);
		if(!lower.getItems().contains(region)) {
			lower.getItems().add(0, region);
			region.setOpacity(0);
			Divider div = this.getDividers().get(0);
			div.setPosition(1);
			
			if (nfile.getFileManager().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
				KeyFrame kf1 = new KeyFrame(Duration.millis(200), new KeyValue(div.positionProperty(), 0.55));
				KeyFrame kf2 = new KeyFrame(Duration.millis(200), new KeyValue(region.opacityProperty(), 1));
				showBottomTl = new Timeline();
				showBottomTl.getKeyFrames().addAll(kf1, kf2);
				showBottomTl.setCycleCount(1);
				showBottomTl.play();
			}else {
				div.setPosition(0.55);
				region.setOpacity(1);
			}
			
			
		}
	}
	
	//ANIMATION ********************************************************************************************* ANIMATION
	private void hideBottom(Region region) {
		if(showBottomTl != null && showBottomTl.getStatus() == Status.RUNNING) showBottomTl.stop();				
		if (lower.getItems().contains(region)) {
			Divider div = this.getDividers().get(0);
			
			if (nfile.getFileManager().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
				KeyFrame kf1 = new KeyFrame(Duration.millis(200), new KeyValue(div.positionProperty(), 1));
				KeyFrame kf2 = new KeyFrame(Duration.millis(200), new KeyValue(region.opacityProperty(), 0));
				hideBottomTl = new Timeline();
				hideBottomTl.getKeyFrames().addAll(kf1, kf2);
				hideBottomTl.setCycleCount(1);
			    hideBottomTl.setOnFinished(e -> {
			    	lower.getItems().remove(region);
					this.getItems().remove(lower);//move to animate,  make this optional if size content is 0
				});
			    hideBottomTl.play();
			}else {				
				div.setPosition(1);
				region.setOpacity(0);
				lower.getItems().remove(region);
				this.getItems().remove(lower);//move to animate,  make this optional if size content is 0
			}			
		}
	}
}
