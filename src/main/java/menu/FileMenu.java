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

import activity.Select;
import application.Constellatio;
import javafx.application.Platform;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import status.ConnectionStatus;

public class FileMenu extends Menu {
	private Constellatio app;
	private Menu newMenu = new Menu("New");
	private MenuItem openMenuItem = new MenuItem("Open");
	private MenuItem closeMenuItem = new MenuItem("Close");
	private MenuItem closeAllMenuItem = new MenuItem("Close All");
	private MenuItem saveMenuItem = new MenuItem("Save");
	private MenuItem saveAsMenuItem = new MenuItem("Save As");
	private MenuItem exportMenuItem = new MenuItem("Export");
	private CheckMenuItem savePasswordMenuItem = new CheckMenuItem("Save Password");
	private MenuItem logoutMenuItem = new MenuItem("Logout");
	private MenuItem exitMenuItem = new MenuItem("Exit");
	
	
	public FileMenu(String string, Constellatio app) {
		super (string);
		this.app = app;
		exitMenuItem.setOnAction(e -> {
			try {
				app.getStartFX().stop();
				Platform.exit();
				System.exit(0);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		exportMenuItem.setOnAction(e -> {
			if (app.getFilemanager().getActiveNFile().getActivity() instanceof Select
					&& app.getFilemanager().getActiveNFile().getActivity().getActiveLayer() != null)
				app.getFilemanager().getActiveNFile().getActivity().getActiveLayer().getSheet().exportToCsv();
		});
		

		logoutMenuItem.setOnAction(e -> {
			app.getDBManager().closeUserConnectionIfOpen();
			app.getConnectionStage().show();// login screen
		});
		
		this.getItems().addAll(newMenu, openMenuItem, closeMenuItem, closeAllMenuItem, new SeparatorMenuItem(), saveMenuItem, saveAsMenuItem, exportMenuItem,new SeparatorMenuItem(), savePasswordMenuItem, logoutMenuItem, new SeparatorMenuItem(), exitMenuItem);
		this.setOnShowing(e -> this.reconfigureFileMenu());// FILE MENU IS RECONFIGURED EVERY TIME IT OPENS
		openMenuItem.setDisable(true);
		newMenu.setDisable(true);
		logoutMenuItem.setDisable(true);
		
		savePasswordMenuItem.setSelected(true);
		openMenuItem.setOnAction((e) -> app.getFilemanager().openFileChooser());
		openMenuItem.setAccelerator(this.createKeyCodeCombination(KeyCode.O));

		saveMenuItem.setOnAction((e) -> app.getFilemanager().save());
		saveMenuItem.setAccelerator(this.createKeyCodeCombination(KeyCode.S));

		saveAsMenuItem.setOnAction((e) -> app.getFilemanager().saveAs());
		closeMenuItem.setOnAction(e -> app.getFilemanager().closeActiveFile());
		closeMenuItem.setAccelerator(this.createKeyCodeCombination(KeyCode.W));
		closeAllMenuItem.setOnAction(e -> app.getFilemanager().closeAllFiles());
	}
	
	void reconfigureFileMenu() {
		this.getItems().clear();
		this.getItems().addAll(newMenu, openMenuItem, closeMenuItem, closeAllMenuItem, new SeparatorMenuItem());
		if (app.getBottomBar().getLight().getStatus() == ConnectionStatus.CONNECTED) {
			app.getFilemanager().getOpenFiles().forEach(nfile -> {
				CheckMenuItem menuItem = new CheckMenuItem(nfile.getXMLFile().getName());
				menuItem.setSelected(nfile == app.getFilemanager().getActiveNFile());
				menuItem.setOnAction(mie -> app.getFilemanager().selectNFile(nfile));
				this.getItems().add(menuItem);
			});
		}
		this.getItems().addAll(new SeparatorMenuItem(), saveMenuItem, saveAsMenuItem, exportMenuItem, new SeparatorMenuItem(), savePasswordMenuItem,
				logoutMenuItem, new SeparatorMenuItem(), exitMenuItem);
	}
	
	public void addNewSchemaToMenu(String schemaName) {
		MenuItem schemaMenuItem = new MenuItem(schemaName);
		schemaMenuItem.setOnAction((e) -> {
			app.getFilemanager().createNewFile(schemaName);
		});
		if (schemaName.equalsIgnoreCase("sakila")) {
			schemaMenuItem.setAccelerator(this.createKeyCodeCombination(KeyCode.N));
		}
		newMenu.getItems().add(schemaMenuItem);
	}

	public MenuItem getCloseMenuItem() {
		return closeMenuItem;
	}

	public MenuItem getCloseAllMenuItem() {
		return closeAllMenuItem;
	}

	public MenuItem getSaveMenuItem() {
		return saveMenuItem;
	}

	public MenuItem getSaveAsMenuItem() {
		return saveAsMenuItem;
	}

	public MenuItem getExportMenuItem() {
		return exportMenuItem;
	}

	public CheckMenuItem getSavePasswordMenuItem() {
		return savePasswordMenuItem;
	}

	public Menu getNewMenu() {
		return newMenu;
	}

	public void activateConnectionMenus() {
		openMenuItem.setDisable(false);
		newMenu.setDisable(false);
		logoutMenuItem.setDisable(false);
		savePasswordMenuItem.setDisable(true);
		if(app.getFilemanager().size()>0) app.getMenu().disableMenus(false);
		
	}

	public void deactivateConnectionMenus() {
		openMenuItem.setDisable(true);
		newMenu.setDisable(true);
		logoutMenuItem.setDisable(true);
		savePasswordMenuItem.setDisable(false);
		app.getMenu().disableMenus(true);	
	}
	
	private KeyCodeCombination createKeyCodeCombination(KeyCode key) {
		if (System.getProperty("os.name").startsWith("Mac")) {
			return new KeyCodeCombination(key, KeyCombination.META_DOWN);
		} else {
			return new KeyCodeCombination(key, KeyCombination.CONTROL_DOWN);
		}
	}
}
