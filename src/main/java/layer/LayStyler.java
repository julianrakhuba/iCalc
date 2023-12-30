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
package layer;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import status.ColorMode;

public class LayStyler {
	private LayColors layColors = new LayColors();
	private LAY lay;
	private ObjectProperty<Color> color = new SimpleObjectProperty<>();

	public LayStyler(LAY lay) {
		this.lay = lay;
		color.addListener((obs, oldColor, n) -> applyStyle(color.get()));
	}
	
	private void applyStyle(Color color) {
		String st4;
		if(color == null) {
			st4 =  "#7cd0f9";
		}else {
			st4 = String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
		}	
		String style = "-fx-background-color: linear-gradient(derive(" + st4 + ", 90.0%), derive(" + st4 + ", -10.0%)), radial-gradient(center 50% -40%, radius 200%, derive(" + st4 + ", 60.0%) " + " 45%, " + st4 + " 50%);";		
		lay.getPane().setStyle(style);
	}

	public void updateLayStyle(ColorMode c) {
//		boolean animate = true;
		if(lay.getNnode().getNmap().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected() && ( color.getValue() != null)) {
		    KeyFrame kf4 = new KeyFrame(Duration.millis(200), new KeyValue(color, layColors.getColor(c), Interpolator.EASE_BOTH));
		    Timeline timeline = new Timeline( kf4);
		    timeline.setCycleCount(1);
		    timeline.play();
		}else {
			color.setValue(layColors.getColor(c));
			applyStyle(color.get());
		}
	}
	
	public void pulse(ColorMode c) {
		ObjectProperty<Color> tc4 = new SimpleObjectProperty<>(layColors.getColor(c));
		applyStyle(tc4.get());
	    tc4.addListener((obs, oldColor, n) -> applyStyle(tc4.get()));
	    
	    if (lay.getNnode().getNmap().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
	    	lay.getPane().scaleXProperty().set(1.2);
			lay.getPane().scaleYProperty().set(1.2);
			KeyFrame kf4 = new KeyFrame(Duration.millis(1000), new KeyValue(tc4, color.get(), Interpolator.EASE_BOTH));
		    KeyFrame sx = new KeyFrame(Duration.millis(1000), new KeyValue(lay.getPane().scaleXProperty(), 1, Interpolator.EASE_BOTH));
		    KeyFrame sy = new KeyFrame(Duration.millis(1000), new KeyValue(lay.getPane().scaleYProperty(), 1, Interpolator.EASE_BOTH));
		    
		    Timeline timeline = new Timeline(kf4, sx, sy);
		    timeline.setCycleCount(1);
		    timeline.setOnFinished(e -> applyStyle(color.get()));//return to original color
		    timeline.play();
	    }else {
	    	applyStyle(color.get());
	    }
	    
	}
}
