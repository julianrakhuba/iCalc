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
package activity;

import java.util.ArrayList;

import file.NFile;
import generic.ACT;
import javafx.scene.input.MouseEvent;
import layer.DLayer;
import layer.LAY;
import layer.SLayer;
import logic.Field;
import map.Nnode;
import search.PAIR;
import sidePanel.Message;
import status.LayerMode;
import status.SqlType;

public class View extends ACT {
	private boolean modefied = false;
	public void passNnode(Nnode nnode, MouseEvent e) {}
	public void newSearchFUNCTION(Nnode nnod, String col, PAIR funcVAL) {}
	public void newSearchBETWEEN(Nnode nnod, String col, String from, String to) {}
	public void newSearchIN(Nnode nnod, String col, String in, ArrayList<String> values) {}

	public View(NFile nFile) {
		this.nFile = nFile;
	}

	public void passLAY(LAY lay) {
		if (rootLay == null) {
			if (lay.getSqlType() != SqlType.SQL) {
				rootLay = lay;
				rootLay.setMode(LayerMode.VIEW);
				rootLay.remoteFieldsOn();
				nFile.getSidePane().activateSearch(rootLay);
			}
		}
	}

	public void closeActivity() {
		if (rootLay != null) {
			rootLay.setMode(LayerMode.BASE);
			rootLay.remoteFieldsOff();
		}
		
		if (modefied) {
			nFile.getUndoManager().saveUndoAction();
			nFile.getFileManager().getNapp().getConsole().refreshActiveMonotor();
			modefied = false;
		}
		rootLay = null;
	}
	
	public void setAsModified() {
		modefied = true;
	}

	public void rebuildFieldMenu() {		
		ArrayList<LAY> allLays = rootLay.getFullChildren();
		allLays.add(0, rootLay);
		allLays.forEach(joinLay -> {
			if((joinLay instanceof SLayer && joinLay.getSqlType() == SqlType.SQLJ) || joinLay instanceof DLayer ) {	
				rootLay.getNnode().getNmap().getNapp().getUpperPane().getSearchContext().getItems().add(joinLay.rebuildLayerMenu(rootLay));
			}			
		});
	}
	
	public void selectFieldClick(Field field) {
		if(!field.isSelected()) {
			field.setSelected(true);
			this.selectField(field);
		}else {
			this.unselectField(field);
		}
	}
	
	public void selectField(Field field) {
		rootLay.addSelectedField(field);
		rootLay.getIndicators().fieldsOn();			
		if (rootLay != field.getFieldLay()) field.getFieldLay().getIndicators().remoteFieldsOn();
		field.getFieldLay().setLabelBold();		
		if(rootLay.getChildDLayer()!= null)	{
			rootLay.getChildDLayer().rebuildDFieldsAndJoins();
		}
		modefied = true;
	}
	
	public void unselectField(Field field) {
		if(rootLay.getChildDLayer() != null && (rootLay.getChildDLayer().isUsedInDlayer(field)|| rootLay.getChildDLayer().getFieldsAndFormulas().size() == 1)) {
			nFile.getMessages().add(new Message(nFile, "warning", "Can't unselect " + field.getAliase() + "!"));

		}else {
			//do unselect
			field.setSelected(false);
			field.setGroupBy(false);
			field.setPivot(false);
			field.setAgrigated(false);
			
			rootLay.removeSelectedField(field);
			if (rootLay.getSelectedFields().filtered(i -> (i.getFieldLay() == field.getFieldLay())).size() == 0) {
				field.getFieldLay().getIndicators().remoteFieldsOff();
				field.getFieldLay().setLabelNormal();
			}
			if (rootLay.getSelectedFields().size() == 0) {
				rootLay.getIndicators().fieldsOff();
			}			
			
			if(rootLay.getChildDLayer() != null)	{
				rootLay.getChildDLayer().rebuildDFieldsAndJoins();
			}
			modefied = true;
		}
	}

	public void selectPivotFieldClick(Field field) {
		if (rootLay.getChildDLayer() != null && rootLay.getChildDLayer().isUsedInDlayer(field)) {
			nFile.getMessages().add(new Message(nFile, "Field", "Can't edit pivot field:  " + field.getLabelText()));		
		} else {
			if (!field.isPivot()) {
				field.setPivot(true);
				if (!field.isSelected()) {
					field.setSelected(true);
					this.selectField(field);
				}
				field.setGroupBy(false);
				field.setAgrigated(false);
			} else {
				field.setPivot(false);
			}
		}
	}

	public void selectGroupFieldClick(Field field) {
		if (rootLay.getChildDLayer() != null && rootLay.getChildDLayer().isUsedInDlayer(field)) {
			nFile.getMessages().add(new Message(nFile, "Field", "Can't edit group field:  " + field.getLabelText()));		
		} else {
			if (!field.isGroupBy()) {
				field.setGroupBy(true);
				if (!field.isSelected()) {
					field.setSelected(true);
					this.selectField(field);
				}
				field.setPivot(false);
				field.setAgrigated(false);
			} else {
				field.setGroupBy(false);
			}
		}
	}

	public void selectAgrigateFieldClick(Field field) {
		if (rootLay.getChildDLayer() != null && rootLay.getChildDLayer().isUsedInDlayer(field)) {
			nFile.getMessages().add(new Message(nFile, "Field", "Can't edit agrigate field:  " + field.getLabelText()));		
		} else {
			if (!field.isAgrigated()) {
				field.setAgrigated(true);
				if (!field.isSelected()) {
					field.setSelected(true);
					this.selectField(field);
				}
				field.setPivot(false);
				field.setGroupBy(false);
			} else {
				field.setAgrigated(false);
			}
		}
	}
}