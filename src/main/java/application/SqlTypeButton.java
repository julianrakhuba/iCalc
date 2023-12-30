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

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import status.SqlType;

public class SqlTypeButton extends Button {
	private Property<SqlType> sqlType = new SimpleObjectProperty<SqlType>(SqlType.SQLJ);
	
	private void updateStyle() {
		this.getStyleClass().clear();
		this.getStyleClass().add(sqlType.getValue().toString() + "btn");
	}
	
	public SqlType getSqlType() {
		return this.sqlType.getValue();
	}

	public SqlTypeButton(String sting) {
		super(sting);
		this.setFocusTraversable(false);
		this.updateStyle();
		sqlType.addListener((c,f,g) -> this.updateStyle());
		
		
		this.setOnMouseClicked(e -> {
			//Toggle
			if(sqlType.getValue() == SqlType.SQL) {
				sqlType.setValue(SqlType.SQLJ);
			}else if(sqlType.getValue() == SqlType.SQLJ) {
				sqlType.setValue(SqlType.SQL);
			}
			e.consume();
		});
	}


	public void setSqlType(SqlType st) {
		if(st == SqlType.SQLD || st == SqlType.SQLJ) {
			this.sqlType.setValue(SqlType.SQLJ);
		}else {
			this.sqlType.setValue(st);
		}
	}

}
