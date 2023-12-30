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
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import status.ActivityMode;

public class SchemaMenu extends Menu {
	private Menu addschema = new Menu("Add");
	private Menu removeschema = new Menu("Delete");
	private CheckMenuItem editSchema = new CheckMenuItem("Edit Schema");
	private MenuItem saveSchema = new MenuItem("Save Schema");
	
	public SchemaMenu(String string, Constellatio constellatio) {
		super (string);
		this.getItems().addAll(getAddschema(), getRemoveschema(), new SeparatorMenuItem(), getEditSchema(), saveSchema);
		this.setOnAction(e -> {
			if (getEditSchema().isSelected()) {
				constellatio.getFilemanager().getActiveNFile().setActivityMode(ActivityMode.CONFIGURE);
			} else {
				constellatio.getFilemanager().getActiveNFile().getActivity().closeActivity();
				constellatio.getFilemanager().getActiveNFile().setActivityMode(ActivityMode.SELECT);
			}
		});
		saveSchema.setOnAction(e -> {
			constellatio.getFilemanager().getActiveNFile().getActiveNmap().saveNnodeCoordinates();
			constellatio.getDBManager().getActiveConnection().getXMLBase().save_existing_or_crate_new();
			constellatio.getFilemanager().getActiveNFile().getActivity().closeActivity();
			constellatio.getFilemanager().getActiveNFile().setActivityMode(ActivityMode.SELECT);
			getEditSchema().setSelected(false);
		});
		
		
		this.setOnShowing(meneu_event -> {
			this.getItems().clear();
			this.getItems().addAll(new SeparatorMenuItem());
			if (constellatio.getFilemanager().getActiveNFile() != null) {
				constellatio.getFilemanager().getActiveNFile().getMaps().forEach((name, map) -> {
					CheckMenuItem mapMenuItem = new CheckMenuItem(name);
					if (map == constellatio.getFilemanager().getActiveNFile().getActiveNmap())
						mapMenuItem.setSelected(true);
					mapMenuItem.setSelected(map == constellatio.getFilemanager().getActiveNFile().getActiveNmap());
					mapMenuItem.setOnAction(e -> {
						constellatio.getFilemanager().getActiveNFile().showNmap(name);
					});
					this.getItems().add(mapMenuItem);
				});
			}
			getAddschema().getItems().clear();

			constellatio.getDBManager().getActiveConnection().getXMLBase().getSchemas().forEach(schema -> {
				if (constellatio.getFilemanager().getActiveNFile() != null && !constellatio.getFilemanager().getActiveNFile().getMaps().containsKey(schema)) {
					MenuItem menuItem = new MenuItem(schema);
					menuItem.setOnAction(e -> {
						constellatio.getFilemanager().getActiveNFile().createNewMap(schema);
					});
					getAddschema().getItems().add(menuItem);
				}
			});
			getRemoveschema().getItems().clear();
			constellatio.getDBManager().getActiveConnection().getXMLBase().getSchemas().forEach(schema -> {
				if (constellatio.getFilemanager().getActiveNFile() != null && constellatio.getFilemanager().getActiveNFile().getMaps().size() > 1
						&& constellatio.getFilemanager().getActiveNFile().getMaps().containsKey(schema)) {
					MenuItem menuItem = new MenuItem(schema);
					menuItem.setOnAction(e -> {
						constellatio.getFilemanager().getActiveNFile().removeSchema(schema);
					});
					getRemoveschema().getItems().add(menuItem);
				}
			});
			this.getItems().addAll(new SeparatorMenuItem(), getAddschema(), getRemoveschema(), new SeparatorMenuItem(), getEditSchema(), saveSchema);			
		});

	}

	/**
	 * @return the editSchema
	 */
	public CheckMenuItem getEditSchema() {
		return editSchema;
	}

	/**
	 * @return the addschema
	 */
	public Menu getAddschema() {
		return addschema;
	}

	/**
	 * @return the removeschema
	 */
	public Menu getRemoveschema() {
		return removeschema;
	}
}
