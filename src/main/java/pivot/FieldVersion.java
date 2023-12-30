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
package pivot;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;

import generic.OpenBO;
import generic.TableCellNumber;
import javafx.scene.control.TableColumn;
import logic.Field;
import status.ColorMode;
import status.VersionType;

public class FieldVersion {
	
	private String function_column;
	private String aliase;
	private String label;
	private String tip;
	private VersionType versionType;
	
	private Field field;
	private Field pivotField;
	
	private TableColumn<OpenBO,?> col;
	
	public FieldVersion(VersionType versionType, Field field) {
		this.field = field;
		this.versionType = versionType;		
	}
	
	public FieldVersion(VersionType versionType) {
		this.versionType = versionType;
	}
	
	public String getAliase() {
		return aliase;
	}

	public void setAlias(String aliase) {
		this.aliase = aliase;
	}

	public String getLabel() {		
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTip() {
		return tip;
	}
	
	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getFunction_Column() {
		return function_column;
	}

	public void setFunction_Column(String funcColumn) {
		this.function_column = funcColumn;
	}

	public VersionType getVersionType() {
		return versionType;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}
	
	public Field getPivotField() {
		return pivotField;
	}
	
	public void setPivotField(Field pivotField) {
		this.pivotField = pivotField;
	}

	public void pulseLay() {
		if(pivotField == null) {
			field.getFieldLay().getStyler().pulse(ColorMode.VIEW);
		}else {
			field.getFieldLay().getStyler().pulse(ColorMode.VIEW);
			pivotField.getFieldLay().getStyler().pulse(ColorMode.FORMULA);
		}
	}
	
	public TableColumn<OpenBO,?> getTableColumn() {
		if(col == null) {
			col = this.createColumn();
			col.setUserData(this);			
		}		
		return col;
	}
	
	private TableColumn<OpenBO,?> createColumn() {
		if(field.isString() || field.isExcludedType()) {
			TableColumn<OpenBO, String> column;
			column = new TableColumn<OpenBO, String>(label);
			column.setCellValueFactory(cell -> cell.getValue().getString(aliase));
			return column;
		}else if(field.isTime()) {
			TableColumn<OpenBO, Time> column = new TableColumn<OpenBO, Time>(label);
			column.setCellValueFactory(cell -> cell.getValue().getTime(aliase));
			return column;
		}else if(field.isDate()) {
			TableColumn<OpenBO, Date> column = new TableColumn<OpenBO, Date>(label);
			column.setCellValueFactory(cell -> cell.getValue().getDate(aliase));
			return column;
		}else if(field.isTimestamp()) {
			TableColumn<OpenBO, Timestamp> column = new TableColumn<OpenBO, Timestamp>(label);
			column.setCellValueFactory(cell -> cell.getValue().getTimestamp(aliase));
			return column;
		}else if(field.isNumber()) {   
			TableColumn<OpenBO, Number> column = new TableColumn<OpenBO, Number>(this.getLabelFormated());
			column.setCellValueFactory(cell -> cell.getValue().getNumber(aliase));
			column.setCellFactory(c -> new TableCellNumber(this));
			return column;
		}else {
			return null;//should never be here
		}
	}
	
	public String getLabelFormated() {
		if(pivotField != null && pivotField.isInt() && pivotField.getFormat().getId().equals("month") && Integer.valueOf(label) > 0 && Integer.valueOf(label) <= 12) {			  
			return new DateFormatSymbols().getShortMonths()[Integer.valueOf(label) -1];
		}else if(pivotField != null && pivotField.isInt() && pivotField.getFormat().getId().equals("weekday") && Integer.valueOf(label) >=0 && Integer.valueOf(label) <=6) { 
			return new DateFormatSymbols().getShortWeekdays()[Integer.valueOf(label) +1];
		}else  {
			return label;
		}
	}
	
	public Number getLabelAsNumber() {
		if(pivotField != null && pivotField.isNumber()) {
			return Integer.valueOf(label);
		}else {			
			return 0;
		}		
	}
}
