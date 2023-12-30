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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import layer.DLayer;
import layer.LAY;
import status.Population;
import status.SqlType;

public class EditMenu extends Menu {
	private MenuItem undoMenuItem = new MenuItem("Undo");
	private MenuItem redoMenuItem = new MenuItem("Redo");
	private MenuItem copyMenuItem = new MenuItem("Copy");
	private MenuItem clearMenuItem = new MenuItem("Clear");
	
	public EditMenu(String string, Constellatio constellatio) {
		super(string);
		this.getItems().addAll(undoMenuItem, redoMenuItem, new SeparatorMenuItem(), copyMenuItem, new SeparatorMenuItem(), clearMenuItem);
		clearMenuItem.setOnAction(e -> {
			if (constellatio.getFilemanager().size() > 0) {
				constellatio.getFilemanager().getActiveNFile().getActiveNmap().clear();
				constellatio.getFilemanager().getActiveNFile().getUndoManager().saveUndoAction();
			}
		});
		
		undoMenuItem.setAccelerator(this.createKeyCodeCombination(KeyCode.Z));
		undoMenuItem.setOnAction(e -> {
			if (constellatio.getFilemanager().size() > 0) constellatio.getFilemanager().getActiveNFile().getUndoManager().undo();
		});

		if (System.getProperty("os.name").startsWith("Mac")) {
			redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHIFT_DOWN, KeyCombination.META_DOWN));
		} else {
			redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
		}

		redoMenuItem.setOnAction(e -> {
			if (constellatio.getFilemanager().size() > 0) constellatio.getFilemanager().getActiveNFile().getUndoManager().redo();
		});

		copyMenuItem.setAccelerator(this.createKeyCodeCombination(KeyCode.C));
		copyMenuItem.setOnAction(e -> {

			LAY lay = constellatio.getFilemanager().getActiveNFile().getActivity().getActiveLayer();
			ClipboardContent content = new ClipboardContent();
			if (lay != null && (lay.getSqlType() == SqlType.SQL || lay.isRoot())) {
				if (lay.getPopulation().getValue() == Population.UNPOPULATED) {
					if (lay instanceof DLayer)
						((DLayer) lay).rebuildDFieldsAndJoins();
				}
				if (lay.getSqlType() == SqlType.SQL) {
					lay.recreateVersions();
					String sql = lay.getSQL().toString();
					content.putString(sql);
					lay.getNnode().getNmap().getNapp().getConsole().addTextToQue(sql+ "\n");

				} else if (lay.isRoot()) {
					lay.refreshPivotCache();
					lay.recreateVersions();
					String sql = lay.getSQL().toString();
					content.putString(sql);
					lay.getNnode().getNmap().getNapp().getConsole().addTextToQue(sql+ "\n");
				}
			} else {
				if (lay != null) content.putString("Can't copy to clipboard lay: " + lay);
			}
			Clipboard.getSystemClipboard().setContent(content);
		});
	}

	public MenuItem getUndoMenuItem() {
		return undoMenuItem;
	}

	public MenuItem getRedoMenuItem() {
		return redoMenuItem;
	}

	public MenuItem getCopyMenuItem() {
		return copyMenuItem;
	}

	public MenuItem getClearMenuItem() {
		return clearMenuItem;
	}
	
	private KeyCodeCombination createKeyCodeCombination(KeyCode key) {
		if (System.getProperty("os.name").startsWith("Mac")) {
			return new KeyCodeCombination(key, KeyCombination.META_DOWN);
		} else {
			return new KeyCodeCombination(key, KeyCombination.CONTROL_DOWN);
		}
	}

}
