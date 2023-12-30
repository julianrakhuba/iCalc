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
package elements;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import application.XML;
import file.NFile;
import file.OpenContext;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import layer.LAY;
import logic.Field;
import logic.SQL;
import sidePanel.Message;
import status.ValueType;

public abstract class ELM {
	protected CursorBox cursorBox; 
	protected ELM parent;
	protected RootELM rootELM;
	public abstract Node getNode();
	public abstract String getLabelText();	
	public abstract void buildSQL(SQL sql);
	public abstract String getPivotStringSQL(Field pvtFld, String val);	
	public abstract void saveXml(Document doc, Element rootE);
	public abstract List<ELM> isUsedInElm(Field field);

	public abstract void styleFocused();
	public abstract void styleUnfocused();
	
	public ELM(RootELM rootELM) {
		this();
		this.rootELM = rootELM;
	}
	
	public ELM() {

	}
	
	public ObservableList<ELM> getElements() {
		return cursorBox.elements;
	}
	
	public void setParent(ELM elm) {
		parent = elm;
	}
	
	public RootELM getRootELM() {
		if(this instanceof RootELM) {
			return (RootELM) this;
		}else {
			return rootELM;
		}
	}
	
	public boolean isParent() {
		return this instanceof RootELM || this instanceof FunctionELM ||this instanceof LayELM;
	}
	
	public CursorBox getCursorBox() {
		if(this.isParent()) {
			return cursorBox;
		}else {
			return null;
		}
	}
	
	public void createXMLChildren(OpenContext context, org.w3c.dom.Node fx, RootELM rootELM) {
		XML.children(fx).forEach(n ->{
			String nname = n.getNodeName();
			NFile  msgLst = rootELM.getLay().getNnode().getNmap().getNFile();
			if(nname.equals("StringELM")) 	{
				cursorBox.addELM(new StringELM(XML.atr(n, "string"), rootELM), false);	
			}else if(nname.equals("FunctionELM")) {
				cursorBox.addELM(new FunctionELM(context, n, rootELM), false);
			}else if(nname.equals("MessageELM")) {
				this.createMessageELM(new Message(msgLst, XML.atr(n, "message"), XML.atr(n, "description")), rootELM);
			}else if(nname.equals("ValuesELM")) {
				if(context.getFields().get(XML.atr(n, "fieldAliase")) != null) {
					cursorBox.addELM(new ValuesELM(context, n, rootELM), false);
				}else {
					this.createMessageELM(new Message(msgLst, "missing", "ValuesELM.Field: " + XML.atr(n, "fieldAliase")), rootELM);
				}
			}else if(nname.equals("FieldELM")) 	{
				if(context.getFields().get(XML.atr(n, "fieldAliase")) != null) {
					cursorBox.addELM(new FieldELM(context, n, rootELM), false);
				}else {
					this.createMessageELM(new Message(msgLst, "missing", "FieldsELM.Field: " + XML.atr(n, "fieldAliase")), rootELM);
				}
			}else if(nname.equals("LayELM")) {
				if(context.getAliaseLAYs().get(XML.atr(n, "layAliase")) != null) {
					cursorBox.addELM(new LayELM(context, n, rootELM), false);
				}else {
					this.createMessageELM(new Message(msgLst, "missing", "LayELM.LAY: " + XML.atr(n, "layAliase")), rootELM);
				}
			}
		});
	}
	
	private void createMessageELM(Message msg, RootELM rootELM) {
		cursorBox.addELM(new MessageELM(msg, rootELM), false);
		rootELM.getLay().getNnode().getNmap().getNFile().getMessages().add(msg);
	}
	
	public FieldELM createFieldELM(Field field, RootELM root, boolean useCursor) {
		if(isParent()) {
			FieldELM elm = new FieldELM(field, root);
			cursorBox.addELM(elm, useCursor);
			return elm;
		}else {
			return null;
		}
	}
	
	public StringELM createStringELM(String str, RootELM root, boolean useCursor) {
		if(isParent()) {
			StringELM elm = new StringELM(str, root);
			cursorBox.addELM(elm, useCursor);
			return elm;
		}else {
			return null;
		}
	}
	
	public ValuesELM createValuesELM(String from,Field field,  RootELM root, boolean useCursor) {
		if(isParent()) {
			ValuesELM elm = new ValuesELM(from, root, field);
			cursorBox.addELM(elm, useCursor);
			return elm;
		}else {
			return null;
		}
	}
	
	public ValuesELM createValuesELM(ArrayList<String> values,Field field,  RootELM root, boolean useCursor) {
		if(isParent()) {
			ValuesELM elm = new ValuesELM(values, root, field);
			cursorBox.addELM(elm, useCursor);
			return elm;
		}else {
			return null;
		}
	}
	
	public FunctionELM createFunctionELM(String name, String label,String open, String openPar, String closePar, String close, RootELM root, boolean useCursor) {
		if(isParent()) {
			FunctionELM elm = new FunctionELM(name, label, open, openPar, closePar, close, root);
			cursorBox.addELM(elm, useCursor);
			return elm;
		}else {
			return null;
		}
	}
	
	public LayELM createLayELM(LAY lay,  RootELM root, boolean useCursor) {
		if(isParent()) {
			LayELM elm = new LayELM(lay, root);
			cursorBox.addELM(elm, useCursor);
			return elm;
		}else {
			return null;
		}
	}
	
	public ValuesELM createValuesELM(Field field, ValueType valueType, RootELM root, boolean useCursor) {
		if(isParent()) {
			ValuesELM elm = new ValuesELM(root, valueType, field);
			cursorBox.addELM(elm, useCursor);
			return elm;
		}else {
			return null;
		}
	}
	
}
