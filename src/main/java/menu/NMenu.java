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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class NMenu extends MenuBar {
	private ObservableList<MenuItem> disableMenus = FXCollections.observableArrayList();
	private SchemaMenu schemaMenu;
	private ViewMenu viewMenu;	
	private EditMenu editMenu;	
	private FileMenu fileMenu;
	
	public NMenu(Constellatio constellatio) {
		fileMenu = new FileMenu("File", constellatio);
		editMenu = new EditMenu("Edit", constellatio);	
		viewMenu = new ViewMenu("View", constellatio);
		schemaMenu = new SchemaMenu("Schema", constellatio);
		
		this.getMenus().addAll(fileMenu, editMenu, viewMenu, schemaMenu);
		this.setUseSystemMenuBar(true);

		disableMenus.addAll(schemaMenu.getAddschema(), schemaMenu.getRemoveschema(), fileMenu.getExportMenuItem(), fileMenu.getCloseMenuItem(), fileMenu.getCloseAllMenuItem(), fileMenu.getSaveMenuItem(), fileMenu.getSaveAsMenuItem(), editMenu.getUndoMenuItem(), editMenu.getRedoMenuItem(), editMenu.getCopyMenuItem(), editMenu.getClearMenuItem(),
				viewMenu.getDynamicSearchMenuItem(), viewMenu.getAutoFoldMenuItem(), viewMenu.getInMenuItem(), viewMenu.getCenterMenuItem(), viewMenu.getOutMenuItem(), schemaMenu.getEditSchema());
		this.disableMenus(true);

	}
	
	public void disableMenus(boolean b) {
		disableMenus.forEach(m -> m.setDisable(b)); // HOW TO REFRESH APPLE MENU, JAVA ISSUE???
	}
	
	public SchemaMenu getSchemaMenu() {
		return schemaMenu;
	}

	public ViewMenu getViewMenu() {
		return viewMenu;
	}


	public FileMenu getFileMenu() {
		return fileMenu;
	}

}
