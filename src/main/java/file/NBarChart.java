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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

@SuppressWarnings({ "rawtypes", "unused", "unchecked" })

public class NBarChart extends NChart  {
	private CategoryAxis x = new CategoryAxis();
	private NumberAxis y = new NumberAxis();
	private BarChart chart;
	private NSheet nSheet;

	public NBarChart(NSheet nSheet) {
		this.nSheet = nSheet;
		chart = new BarChart(x, y);
		y.setMinorTickVisible(false);
		chart.setMinWidth(0);
		chart.setAnimated(false);
		chart.setVerticalGridLinesVisible(false);
		chart.setBarGap(2);
		chart.setCategoryGap(10);
	}

	public Region getRegion() {
		return chart;
	}

	public void refresh(ObservableList<String> categories, ObservableList<Series<String, Number>> data) {
		x.getCategories().clear();
		x.setCategories(categories);		
		chart.setData(data);
		chart.layout();
		
		data.forEach(ser ->{
			ser.getData().forEach(dt ->{
				if(dt.getNode() != null) {
					Pane pn = (Pane) dt.getNode();
					pn.setOnMouseEntered( ent ->{
						dt.getNode().setStyle("-fx-effect: dropshadow(two-pass-box , #1E90FF, 2, 0.8 , 0, 0); -fx-background-color:  radial-gradient(center 50.0% -40.0%, radius 200.0%,  derive(-fx-bar-fill, 70.0%) 45.0%, derive(-fx-bar-fill, 0.3) 50.0%);");
					});
					
					pn.setOnMouseExited( ent ->{
						dt.getNode().setStyle("-fx-effect: dropshadow(two-pass-box , rgba(0, 0, 0, 0.3), 5, 0.0 , 0, 0); -fx-background-color:  radial-gradient(center 50.0% -40.0%, radius 200.0%,  derive(-fx-bar-fill, 40.0%) 45.0%, -fx-bar-fill 50.0%);");
					});	
				}
			});
		});
	}

}
