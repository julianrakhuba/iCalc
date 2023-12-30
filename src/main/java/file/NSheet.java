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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import generic.OpenBO;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import layer.LAY;
import logic.Field;
import pivot.FieldVersion;
import status.VersionType;
import status.VisualStatus;

@SuppressWarnings({ "rawtypes" })

public class NSheet extends Tab {
	private TableView<OpenBO> tableView = new TableView<OpenBO>();
	private SplitPane splitPane = new SplitPane();
	private ArrayList<NChart> charts = new ArrayList<NChart>();
	private NChart activeChart;
	private Property<VisualStatus> showChart = new SimpleObjectProperty<VisualStatus>(VisualStatus.UNAVALIBLE);

	private Timeline shoewGridTl;
	private Timeline hideGridTl;
	private Region currentRight;

	
	private LAY lay;
	private boolean calculateCells = false;

	public NSheet(LAY lay) {
		this.lay = lay;
		this.setText(lay.getNnode().getTable() + " ");
		this.setOnClosed(e -> {
			lay.clearPopulation();
		});
		
		HBox.setHgrow(tableView, Priority.ALWAYS);
		tableView.getSelectionModel().setCellSelectionEnabled(true);
		tableView.setTableMenuButtonVisible(true);
		StackPane.setMargin(tableView, new Insets(10));
		
		Pane tableP = new Pane();
		StackPane tableSP = new StackPane(tableP, tableView);		
		if(lay.getNnode().getNmap().getNapp().getStage().getStyle() == StageStyle.TRANSPARENT) {
			tableP.setStyle(" -fx-background-color: rgba(0, 0, 0, 0.5);-fx-border-width: 0.5;-fx-border-color: derive(#1E90FF, 50%);-fx-effect: dropshadow(gaussian, derive(#1E90FF, 40%) , 8, 0.2, 0.0, 0.0);-fx-background-radius: 3;-fx-border-radius: 3;");
			tableSP.setStyle("-fx-padding: 5 0 5 0; -fx-min-width:0;");
			splitPane.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-background-radius: 0 0 7 7;");
		}else {
//			tableP.setStyle(" -fx-effect: dropshadow(two-pass-box , rgba(0, 0, 0, 0.05), 5, 0.4 , 2, 2); -fx-background-radius: 7; -fx-background-color: rgb(234, 236, 241);");
//			tableSP.setStyle("-fx-padding: 5 5 5 5; -fx-min-width:0; -fx-effect: dropshadow(two-pass-box , white, 5, 0.4 , -2, -2);");
			tableP.setStyle("-fx-background-radius: 7; -fx-background-color: rgb(234, 236, 241); -fx-effect:dropshadow(two-pass-box , white, 5, 0.4 , 0, 0);"
					+ "	");
			tableSP.setStyle("-fx-padding: 5 5 5 5; -fx-min-width:0;");
			splitPane.setStyle("-fx-background-color: rgb(234, 236, 241); -fx-padding: 0; -fx-background-radius: 0 0 7 7;");
		}

		splitPane.getItems().addAll(tableSP);
		
//		scheetSplitPane.getDividers().get(0).setPosition(0.7);
		
		this.setContent(splitPane);
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tableView.getSelectionModel().getSelectedCells().addListener((ListChangeListener<? super TablePosition>) c -> {

			if (calculateCells) {
				FilteredList<TablePosition> list = tableView.getSelectionModel().getSelectedCells().filtered(p -> p.getTableColumn().getCellObservableValue(p.getRow()).getValue() instanceof Number);
				if (list.size() > 1) {
					lay.getNnode().getNmap().getNapp().getBottomBar().getSumLabel().clear();
					list.forEach(e -> lay.getNnode().getNmap().getNapp().getBottomBar().getSumLabel().add((Number) e.getTableColumn().getCellObservableValue(e.getRow()).getValue()));
				} else {
					lay.getNnode().getNmap().getNapp().getBottomBar().getSumLabel().clear();
				}
				int countSize = tableView.getSelectionModel().getSelectedCells().size();
				if (countSize > 1) {
					lay.getNnode().getNmap().getNapp().getBottomBar().getCountLabel().setCountValue(countSize);
				} else {
					lay.getNnode().getNmap().getNapp().getBottomBar().getCountLabel().clear();
				}
			}
			// does this belong here?? create method in column for usedData replacement
			if (tableView.getSelectionModel().getSelectedCells().size() == 1) {
				FieldVersion version = (FieldVersion) tableView.getSelectionModel().getSelectedCells().get(0).getTableColumn().getUserData();
				if (version != null) {
					lay.getNnode().getNmap().getNapp().getBottomBar().getSumLabel().setText(version.getTip());
					version.pulseLay();
					tableView.getSelectionModel().getSelectedItem();
				}
			}
		});
		
		tableView.getColumns().addListener(new ListChangeListener<TableColumn<OpenBO, ?>>() {
			public void onChanged(Change<? extends TableColumn<OpenBO, ?>> change) {
				change.next();
				ArrayList<Field> fldz = new ArrayList<Field>();
				change.getAddedSubList().forEach((tc) -> {
					FieldVersion version = (FieldVersion) tc.getUserData();
					if (version != null) {
						if (!fldz.contains(version.getField())) {
							fldz.add(version.getField());
						}
					}
				});
				Collections.sort(lay.getSelectedFields(), Comparator.comparing(item -> fldz.indexOf(item)));
			}
		});

		tableView.getSortOrder().addListener((ListChangeListener<? super TableColumn<?, ?>>) c -> {
			if (c.getList().size() > 0) {
				this.refreshChart();
			}
		});
	
		
		charts.add(new NBarChart(this));
		charts.add(new NLineChart(this));
		
		activeChart = charts.get(0);

	}

	public void setCalculateCells(boolean calculateCells) {
		this.calculateCells = calculateCells;
		if (!calculateCells) {
			lay.getNnode().getNmap().getNapp().getBottomBar().getSumLabel().clear();
			lay.getNnode().getNmap().getNapp().getBottomBar().getCountLabel().clear();
		}
	}

	// CSV Export
	public void exportToCsv() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save CSV");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.setInitialFileName(lay.getNnode().getTableNameWUnderScr() + ".csv");
		File file = fileChooser.showSaveDialog(lay.getNnode().getNmap().getNapp().getStage());
		if (file != null) {
			StringBuilder fileString = new StringBuilder();
			ArrayList<FieldVersion> vers = new ArrayList<FieldVersion>(lay.getVersions());
			if (vers.size() > 0) {
				vers.forEach(col -> fileString
						.append(col.getLabel() + (vers.indexOf(col) < (vers.size() - 1) ? "," : "\n")));
				tableView.getItems().forEach(bo -> {
					vers.forEach(version -> {
						SimpleObjectProperty<?> property = bo.getProperty(version);
						fileString.append((property.get() == null ? "null," : property.get())
								+ (vers.indexOf(version) < (vers.size() - 1) ? "," : "\n"));
					});
				});
			}
			try {
				Writer writer = new BufferedWriter(new FileWriter(file));
				writer.write(fileString.toString());
				writer.flush();
				writer.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public TableView<OpenBO> getTableView() {
		return tableView;
	}

	public void createColumns() {
		lay.getVersions().forEach(version -> {
			TableColumn<OpenBO, ?> col = version.getTableColumn();
			col.visibleProperty().addListener((a, b, c) -> {
				this.refreshChart();
			});
			
			col.sortTypeProperty().addListener((x, y, z) -> {
				this.refreshChart();
			});
			tableView.getColumns().add(col);
		});
	}

	public LAY getLay() {
		return lay;
	}
	
	public void clearPopulation() {
		tableView.getColumns().clear();
		tableView.getItems().clear();
	}
	
	//**************************************************
	//*				       Charts	   			       *
	//**************************************************
	
	public void showHideChart() {
		if(showChart.getValue() == VisualStatus.SHOW ) {
			this.setRight(null);
			showChart.setValue(VisualStatus.HIDE);
		}else {			
			showChart.setValue(VisualStatus.SHOW);
			this.makeAvaliableIfValid();
		}
	}
	
	public void toggleChart() {
		if(charts.indexOf(activeChart) == 0) {
			activeChart = charts.get(1);
		}else {
			activeChart = charts.get(0);
		}
		
		if(lay.isChartValid()) {
			showChart.setValue(VisualStatus.SHOW);
			this.setRight(activeChart.getRegion());
			this.refreshChart();
		}
	}
	
	public void makeAvaliableIfValid() {
		if(lay.isChartValid() && showChart.getValue() != VisualStatus.HIDE ) {
			showChart.setValue(VisualStatus.SHOW);
			this.setRight(activeChart.getRegion());
		}
	}
	
	public void refreshChart() { 
		if (lay.isChartValid()) {
			activeChart.refresh(this.getCategories(), this.getData());
		}else {// else remove
			this.setRight(null);
			showChart.setValue(VisualStatus.UNAVALIBLE);
		}
	}
	
	private ObservableList<String> getCategories() {
		ObservableList<String> categoryList = FXCollections.observableArrayList();
		tableView.getColumns().forEach(col -> {
			FieldVersion ver = (FieldVersion) col.getUserData();
			if (ver.getTableColumn().isVisible() &&(ver.getVersionType() == VersionType.PIVOT || !lay.isPivotLay())) {
				categoryList.add(col.getText());
			}
		});
		return categoryList;
	}

	private ObservableList<Series<String, Number>> getData() {
		ObservableList<Series<String, Number>> boSeries = FXCollections.observableArrayList();
		tableView.getItems().forEach(bo -> {
			Series<String, Number> series = new Series<String, Number>();
			// Get first GROUPBY column
			series.setName("" + bo.getProperty(lay.getVersions().get(0)).get());
			// Get values
			lay.getVersions().subList(1, lay.getVersions().size()).forEach(ver -> {
				//visible and (lay pivot zero or fied is pivot)
				if (ver.getTableColumn().isVisible() &&(ver.getVersionType() == VersionType.PIVOT || !lay.isPivotLay())) {
//					Data<String, Number> dt = new Data<String, Number>(ver.getLabelFarmated(), (Number) bo.getProperty(ver).get());
					Data<String, Number> dt = new Data<String, Number>();
					dt.setXValue(ver.getLabelFormated());
					dt.setYValue((Number) bo.getProperty(ver).get());
					dt.setExtraValue((Number) bo.getProperty(ver).get());

					series.getData().add(dt);
				}
			});
			boSeries.add(series);
		});
		return boSeries;
	}
	
	
	//New chart automation
	public void setRight(Region region) {
		
		if(currentRight == null && region != null) {//new
			this.showRight(region);
		}else if(currentRight != null && region != null) {//swop
			if(currentRight != region) {
				splitPane.getItems().remove(currentRight);
				splitPane.getItems().add(region);
				Divider div = splitPane.getDividers().get(0);//DO I NEED DEVIDER UPDATE ON SWOP???
				div.setPosition(0.5);
				region.setOpacity(1);			
			}
		}else {
			hideRight(currentRight);
		}
		currentRight = region;
	}
	
	private void showRight(Region region) {
		//add
		if(hideGridTl != null && hideGridTl.getStatus() == Status.RUNNING) hideGridTl.stop();	
	
		if(!splitPane.getItems().contains(region)) {
			splitPane.getItems().add(region);
			region.setOpacity(0);			
			Divider div = splitPane.getDividers().get(0);
			div.setPosition(1);

			if (lay.getNnode().getNmap().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
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
	}
	

	private void hideRight(Region region) {
		if(splitPane.getItems().size() == 2) {
			if(shoewGridTl != null && shoewGridTl.getStatus() == Status.RUNNING) shoewGridTl.stop();				
			if (splitPane.getItems().contains(region)) {
				Divider div = splitPane.getDividers().get(0);
				if (lay.getNnode().getNmap().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
					KeyFrame kf1 = new KeyFrame(Duration.millis(400), new KeyValue(div.positionProperty(), 1));
					KeyFrame kf2 = new KeyFrame(Duration.millis(400), new KeyValue(region.opacityProperty(), 0));
					hideGridTl = new Timeline();
					hideGridTl.getKeyFrames().addAll(kf1, kf2);
					hideGridTl.setCycleCount(1);
					hideGridTl.setOnFinished(e -> splitPane.getItems().remove(region));
					hideGridTl.play();
				}else {
					splitPane.getItems().remove(region);
					div.setPosition(1);
					region.setOpacity(0);
				}		
			}
		}
	}

}
