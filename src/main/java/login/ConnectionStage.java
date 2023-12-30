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
package login;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import application.Constellatio;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import managers.DBManager;

public class ConnectionStage extends Stage {
	private VBox vBox = new VBox();
	private StackPane rootStackPane = new StackPane(vBox);
	private Scene scene = new Scene(rootStackPane, Color.color(0, 0, 0, 0.0));
	private ComboBox<Login> dropDown = new ComboBox<Login>();

	private Button connectBtn = new Button("Connect");
	private Text title = new Text("database login");
	private TextField username = new TextField();
	private PasswordField password = new PasswordField();
	private Constellatio napp;
	
	public ConnectionStage(DBManager connectionManager, Stage primaryStage, Constellatio napp) {
		this.initStyle(StageStyle.TRANSPARENT);
		this.initModality(Modality.APPLICATION_MODAL);// to lock parent stage
		this.initOwner(primaryStage);// to lock parent stage
		this.setWidth(primaryStage.getWidth());
		this.setHeight(primaryStage.getHeight());
		
		this.napp = napp;
		vBox.setMaxSize(500, 300);
		vBox.setStyle("-fx-background-radius: 20px; -fx-border-color: white; -fx-border-width: 2px; -fx-background-color: rgba(0, 0, 0, 0.2); -fx-effect: dropshadow(gaussian, derive(#1E90FF, 5%) , 7, 0.4, 0.0, 0.0); -fx-border-radius: 20px;");
		rootStackPane.setStyle("-fx-background-color: transparent;");
		this.setScene(scene); 
		
		 // Calculate the center position of the parent Stage
        double centerXPosition = primaryStage.getX() + primaryStage.getWidth()/2d;
        double centerYPosition = primaryStage.getY() + primaryStage.getHeight()/2d;
        
        this.setOnShown(ev -> {
        	this.setX(centerXPosition - this.getWidth()/2d);
        	this.setY(centerYPosition - this.getHeight()/2d);
        	this.fadeAndShow();
        });

		dropDown.setCellFactory(param -> new LoginCell());
		dropDown.setPromptText("select connection");
		dropDown.setItems(this.napp.getDBManager().getConfiguration().getLoginList());
		dropDown.setMaxWidth(400);
		dropDown.setMinWidth(400);		
		connectBtn.setOnAction(ed -> {
			if(!this.isExpired() 
//					|| System.getProperty("user.name").equals("julianrakhuba")
					) {
				Login login = dropDown.getSelectionModel().getSelectedItem();
				if(login!= null) {
					login.setUsername(username.getText());
					login.setPassword(password.getText());
					connectionManager.activateConnection(login);
					//Close on successful login
					if(connectionManager.getActiveConnection().getJDBC() != null) {
						this.fadeAndclose();
						napp.getFilemanager().openAutoFile();
					}else {
						//Handle login fail here??;
					}
				}
			}else {
				connectBtn.setText("expired");
			}						
		});

		title.setFill(Color.WHITE);
		title.setStyle("-fx-font-size: 19; -fx-effect: dropshadow(gaussian, derive(#1E90FF, 5%) , 7, 0.4, 0.0, 0.0);");

		username.setPromptText("username");
		username.setStyle(" -fx-prompt-text-fill: #ade0ff;");
		username.setMaxWidth(400);
		
		password.setPromptText("password");
		password.setStyle(" -fx-prompt-text-fill: #ade0ff;");
		password.setMaxWidth(400);


		dropDown.valueProperty().addListener((a, b, c) -> {
			if(c.getPassword().equals("")) {
				napp.getMenu().getFileMenu().getSavePasswordMenuItem().setSelected(false);
			}else {
				napp.getMenu().getFileMenu().getSavePasswordMenuItem().setSelected(true);
			}
			username.setText(c.getUsername());
			password.setText(c.getPassword());
		});

		vBox.getChildren().addAll(title, dropDown, username, password, connectBtn);
		vBox.setSpacing(20);
		vBox.setPadding(new Insets(20, 10, 10, 10));
		vBox.setAlignment(Pos.CENTER);
	}
	
	private void fadeAndShow() {
		if (napp.getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
			KeyFrame kf1 = new KeyFrame(Duration.millis(300), new KeyValue(this.opacityProperty(), 1));
		    Timeline timeline = new Timeline(kf1);
		    timeline.setCycleCount(1);
		    timeline.play();
		}else {
			this.setOpacity(1);
		}

		
	}

	private void fadeAndclose() {
		if (napp.getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
			KeyFrame kf1 = new KeyFrame(Duration.millis(300), new KeyValue(this.opacityProperty(), 0));
		    Timeline timeline = new Timeline(kf1);
		    timeline.setCycleCount(1);
		    timeline.setOnFinished(e -> super.close());
		    timeline.play();
		}else {
			this.setOpacity(1);
			super.close();
		}

		
	}

	
	private boolean isExpired() {
		LocalDate webdt = LocalDate.now();
		LocalDate expdt = LocalDate.of(2024, Month.DECEMBER, 31);
		int years = Period.between(webdt, expdt).getYears();
	    int months = Period.between(webdt, expdt).getMonths();
	    int days = Period.between(webdt, expdt).getDays();
	    long p2 = ChronoUnit.DAYS.between(webdt, expdt);
		napp.setTitle("("+ p2 + " days left)  [" + System.getProperty("java.home") + "]" );
		if(years < 0 || months < 0 || days < 0) {
			return true;
		}else {
			return false;
		}
	}
	
}
