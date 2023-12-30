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
package menu;

import application.Constellatio;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.util.Duration;

public class ViewMenu extends Menu {
	private Constellatio constellatio;
	private MenuItem centerMenuItem = new MenuItem("Center");
	private MenuItem inMenuItem = new MenuItem("In");	
	private MenuItem outMenuItem = new MenuItem("Out");
	private MenuItem clearConsole = new MenuItem("Clear Console");
	
	private CheckMenuItem simpleViewMenuItem = new CheckMenuItem("Simple View");
	private CheckMenuItem animationMenuItem = new CheckMenuItem("Animation");
	private CheckMenuItem glassModeMenuItem = new CheckMenuItem("Glass Mode (restart)");
	private CheckMenuItem dynamicSearchMenuItem = new CheckMenuItem("Dynamic SQL");
	private CheckMenuItem autoFoldMenuItem = new CheckMenuItem("Auto-fold");

	public ViewMenu(String string, Constellatio constellatio) {
		super(string);
		this.constellatio = constellatio;
		
		dynamicSearchMenuItem.setSelected(true);
		animationMenuItem.setSelected(false);
		
		this.getItems().addAll(dynamicSearchMenuItem, new SeparatorMenuItem(), autoFoldMenuItem, new SeparatorMenuItem(), inMenuItem,
				centerMenuItem, outMenuItem, new SeparatorMenuItem(), simpleViewMenuItem,new SeparatorMenuItem(),glassModeMenuItem, new SeparatorMenuItem(),
				new SeparatorMenuItem(), clearConsole,new SeparatorMenuItem(),  animationMenuItem);
		
		clearConsole.setOnAction(e -> constellatio.getConsole().clear());

		autoFoldMenuItem.setSelected(true);
		simpleViewMenuItem.setSelected(true);
		
		simpleViewMenuItem.setOnAction(e -> {
			if (simpleViewMenuItem.isSelected()) {
				constellatio.getFilemanager().setCompactView(true);
			} else {
				constellatio.getFilemanager().setCompactView(false);
			}
		});
		
		glassModeMenuItem.setOnAction(e ->{
			constellatio.updateGlassMode(glassModeMenuItem.isSelected());
		});
		
		
		inMenuItem.setOnAction(e -> this.zoom(constellatio.getFilemanager().getActiveNFile().getActiveNmap().getSchemaPane().scaleXProperty().getValue() * 1.5));
		centerMenuItem.setOnAction(e -> this.zoom(1.0));
		outMenuItem.setOnAction(e -> this.zoom(constellatio.getFilemanager().getActiveNFile().getActiveNmap().getSchemaPane().scaleXProperty().getValue() * 0.75));
	}
	

	private void zoom(double zoomValue) {
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().clear();
		timeline.getKeyFrames()
				.add(new KeyFrame(Duration.millis(400),
						new KeyValue(constellatio.getFilemanager().getActiveNFile().getActiveNmap().getSchemaPane().scaleXProperty(),
								zoomValue, Interpolator.EASE_BOTH)));
		timeline.getKeyFrames()
				.add(new KeyFrame(Duration.millis(400),
						new KeyValue(constellatio.getFilemanager().getActiveNFile().getActiveNmap().getSchemaPane().scaleYProperty(),
								zoomValue, Interpolator.EASE_BOTH)));
		timeline.setCycleCount(1);
		timeline.playFromStart();
	}

	public MenuItem getInMenuItem() {
		return inMenuItem;
	}

	public MenuItem getCenterMenuItem() {
		return centerMenuItem;
	}

	public MenuItem getOutMenuItem() {
		return outMenuItem;
	}

	public CheckMenuItem getAutoFoldMenuItem() {
		return autoFoldMenuItem;
	}

	public CheckMenuItem getDynamicSearchMenuItem() {
		return dynamicSearchMenuItem;
	}

	public CheckMenuItem getSimpleViewMenuItem() {
		return simpleViewMenuItem;
	}


	public CheckMenuItem getGlassModeMenuItem() {
		return glassModeMenuItem;
	}
	
	public CheckMenuItem getAnimationMenuItem() {
		return animationMenuItem;
	}

}
