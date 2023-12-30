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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import layer.LAY;
import logic.SQL;

public class Console extends VBox {
	private TextArea textArea = new TextArea();	
	private TextFlow textFlow = new TextFlow();	
	private ScrollPane scrollPane = new ScrollPane(textFlow);
  
	private PrintStream errorStream;
	private PrintStream outStream;
	private Constellatio napp;
	
	//new queue
	private ObservableList<String> queue = FXCollections.observableArrayList();
	private SequentialTransition sequentialTransition = new SequentialTransition();

	//
	boolean systemReroute = false;
	boolean useFlow = true;

	private LAY activeLAY;
	
	public Console(Constellatio napp) {
		this.napp = napp;
		
		if(useFlow) {
			this.getChildren().addAll(scrollPane);
			scrollPane.setFitToWidth(true);
			VBox.setVgrow(scrollPane, Priority.ALWAYS);
		}else {
			VBox.setVgrow(textArea, Priority.ALWAYS);
			this.outStream = System.out;
			this.errorStream = System.err;
			textArea.setWrapText(true);
			textArea.setEditable(true);
			this.getChildren().addAll(textArea);
		}
		
		if(napp.getStage().getStyle() == StageStyle.TRANSPARENT) {
			System.out.println("TRANSPARENT");
			scrollPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);"
			+ "    -fx-background-radius: 3;"
			+ "    -fx-border-color: derive(#1E90FF, 50%);"
			+ "    -fx-border-radius: 3;"
			+ "    -fx-border-width: 0.5;"
			+ "    -fx-effect: dropshadow(gaussian, derive(#1E90FF, 40%) , 8, 0.2, 0.0, 0.0);"
			+ "    -fx-border-insets: 0 5 5 5; "
			+ "    -fx-background-insets: 0 5 5 5;"
			+ "    -fx-padding: 5;"
			+ "    -fx-text-fill: white; ");
		}else {
			System.out.println("NOT TRANSPARENT");
			scrollPane.setStyle(""
					+ "-fx-background-color: rgb(234, 236, 241);"
//			+ "-fx-background-color: #f5f5f5, linear-gradient(from 0.0px 0.0px to 5.1px  0.0px, repeat, #ededed 5%, transparent 5%), linear-gradient(from 0.0px 0.0px to  0.0px 5.1px, repeat, #ededed 5%, transparent 5%);"
			+ "-fx-border-radius: 7;"
			+ "-fx-background-radius: 7;"
			+ "-fx-effect: dropshadow(two-pass-box , rgba(0, 0, 0, 0.05), 5, 0.4 , 2, 2);"
			+ "-fx-padding: 10;");
			
			this.setStyle("-fx-effect:dropshadow(two-pass-box , white, 5, 0.4 , -2, -2);");
//			
//			scrollPane.setStyle("-fx-effect: dropshadow(two-pass-box , rgba(0, 0, 0, 0.05), 5, 0.4 , 2, 2); -fx-background-color: rgb(234, 236, 241); -fx-background-radius: 7;");
//			this.setStyle("-fx-effect:dropshadow(two-pass-box , white, 5, 0.4 , -2, -2); -fx-background-color: transparent; -fx-padding: 5 5 5 5;");
//		
		}
	}	
	
	public void clear() {
		if(useFlow) {
			textFlow.getChildren().clear();
		}else {
			textArea.clear();
		}
	}

	public void routeToConsole() {
		if(systemReroute) {
			OutputStream out = new OutputStream() {
	            @Override
	            public void write(int b) throws IOException {
	                appendString(String.valueOf((char)b));
	            }
	        };
	        System.setOut(new PrintStream(out, true));
	        System.setErr(new PrintStream(out, true));	        
	        addTextToQue("Forward system output to Constellatio");
		}
	}
	
	public void routeBackToSystem() {
		if(systemReroute) {
			System.setOut(outStream);
			System.setErr(errorStream);
	        addTextToQue("Route Output to System");
		}
	}

	public void appendString(String str) {		
		if(useFlow) {
			Text text = new Text(str);
			text.setFont(new Font(12));
			text.setFill(Color.valueOf("#525e6b"));

			if(napp.getStage().getStyle() == StageStyle.TRANSPARENT) {
				text.setFill(Color.WHITE);
			}
			textFlow.getChildren().add(text);
			scrollPane.setVvalue(1.0);
		}else {
			textArea.appendText(str);
		}
	}

	public void addTextToQue(String query) {
		if(sequentialTransition.getStatus() == Status.STOPPED && queue.size() == 0) {
			queue.add(query);
			this.feedFirst();
		}else {
			queue.add(query);
		}
	}

	
	//PRIVATE
	private void feedFirst() {
		if(queue.size()>0) {
			this.feedItem(queue.remove(0));
		}
	}
	
	private void feedItem(String str) {
		for (String line : str.split("\n")) {//new line will be put back on in here
			if(str.length() < 500) {
		        for (char c : line.toCharArray()) {
					sequentialTransition.getChildren().add(new Timeline(new KeyFrame(Duration.seconds(0.005), e -> this.appendString(Character.toString(c)))));
		        }
				sequentialTransition.getChildren().add(new Timeline(new KeyFrame(Duration.seconds(0.005), e -> this.appendString("\n"))));
			}else {
				sequentialTransition.getChildren().add(new Timeline(new KeyFrame(Duration.seconds(0.03), e -> this.appendString(line + "\n"))));
			}			
		}
		
		sequentialTransition.setOnFinished(e ->{
	      	sequentialTransition.getChildren().clear();
	      	this.feedFirst();
		});  	
		sequentialTransition.play();
	}

	public ScrollPane getScrollPane() {
		return scrollPane;
	}

	public void monitorLay(LAY lay) {
		activeLAY = lay;
		SQL sql = lay.getSQL();
		this.clear();
		sql.getTexts().forEach(nt ->{
			textFlow.getChildren().add(nt.getText());
		});
	}
	
	public void refreshActiveMonotor() {
		System.out.println("Refresh Console");
		if(activeLAY != null) this.monitorLay(activeLAY);
	}

	public TextFlow getTextFlow() {
		return this.textFlow;
	}
}