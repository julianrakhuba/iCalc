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
package generic;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.beans.property.SimpleObjectProperty;

public abstract class BO {
	public HashMap<String, SimpleObjectProperty<?>> fields = new HashMap<String, SimpleObjectProperty<?>>();	
	public abstract void saveXml(Document doc, Element rowE);
}























	

	
//	public String getINTO() {
//		ArrayList<SimpleStringProperty> fields = new ArrayList<SimpleStringProperty>(fieldsHashMap.values());
//		// column 1
//		StringBuilder columnsSB = new StringBuilder("(" + fields.get(0).getName());
//		StringBuilder valueSB = new StringBuilder("VALUE (" + valueCheck(fields.get(0).getValue()));
//
//		if (fields.size() > 1) {
//			fields.subList(1, fields.size()).forEach((field) -> {
//				columnsSB.append(", " + field.getName());
//				valueSB.append(", " + valueCheck(field.getValue()));
//			});
//		}
//
//		columnsSB.append(") ");
//		valueSB.append(") ");
//		return columnsSB.toString() + valueSB.toString();
//	}
//	
//	public String valueCheck(String string) {
//		if(string != null){
//			return "'" + string + "'";
//		}else{
//			return "null";
//		}
//	}
//}
	
	
//private ArrayList<String>  changedFields = new ArrayList<String>();
//	public void addChangedField(String fieldName){
//		if(!changedFields.contains(fieldName)){
//			changedFields.add(fieldName);
//		}
//	}
	
//	public void addAllFieldsToChangedFields(){
//		fieldsHashMap.forEach((k,property) -> {
//			changedFields.add(k);
//		});
//	}
//	
//	public boolean containsChangedField(String fieldName){
//		return changedFields.contains(fieldName);
//	}
//	
//	public void clearChangedFields(){
//		changedFields.clear();
//	}
	
//	public boolean isDifferent (BO inBO){
//		this.compare(this, inBO);
//		this.compare(inBO, this); // need to change revere compare only if columns count is different
//		
//		if(changedFields.size()>=1){
//			return true;	
//		}else{
//			return false;
//		}
//	}
//	private void compare(BO a, BO b){
//		a.fieldsHashMap.forEach((columnName, AProperty) -> {
//			if (b.fieldsHashMap.get(columnName) == null){
//				b.addChangedField(columnName);
//				a.addChangedField(columnName);
//				
//			}else{
//				String inValue = b.fieldsHashMap.get(columnName).get();
//				String localValue = AProperty.get();
//				
//				if(localValue == null){
//					if(!(inValue == null)){
//						b.addChangedField(columnName);
//						a.addChangedField(columnName);
//					}
//				}else if(inValue == null){
//					if(!(localValue == null)){
//						b.addChangedField(columnName);
//						a.addChangedField(columnName);
//					}
//				}else{
//					if(!localValue.equals(inValue)){
//						b.addChangedField(columnName);
//						a.addChangedField(columnName);
//					}	
//				}
//			}
//		});
//	}
//}










//public HashMap<String, SimpleStringProperty> getFieldsHashMap() {return fieldsHashMap;}






//INSERT INTO base.NColumn (visible, order_position, column_name, schema_name, table_name) VALUE ('1', '8', 'rental_rate', 'sakila', 'film') ;
//public String getINTO(){
//	ArrayList<COLUMN> arrColumns = new ArrayList<COLUMN>(whereColumns.values());
//	StringBuilder columnNameSB = new StringBuilder("(" + arrColumns.get(0).getColomnName());// first column name
//	
//	StringBuilder valueSB = new StringBuilder("VALUE ('" + arrColumns.get(0).getValue() + "'"); // first value
//	if (whereColumns.size() > 1) {
//		arrColumns.subList(1, arrColumns.size()).forEach((col) -> {
//			columnNameSB.append(", " + col.getColomnName());
//			
//			if(col.getValue() == null){
//				valueSB.append(", null" );
//			}else{
//				valueSB.append(", '" + col.getValue() + "'");
//			}
//		});
//	}
//	columnNameSB.append(") ");
//	valueSB.append(") ");
//	return columnNameSB.toString() + valueSB.toString();
//}
//private void addUNIQUEID(BO bo) {
//	bo.getUniqueIdHashMap().forEach((k,v) -> {
//		if (primaryColumnsFromBO.containsKey(k)) {
//			primaryColumnsFromBO.get(k).setValue(v.get());;
//		} else {
//			COLUMN newColumn = new COLUMN(k, v.get());
//			primaryColumnsFromBO.put(k, newColumn);
//		}		
//	});		
//}
//public String getUNIQUE_WHERE() {
//	ArrayList<COLUMN> arrColumns = new ArrayList<COLUMN>(primaryColumnsFromBO.values());
//	
//	StringBuilder sb = new StringBuilder("WHERE " + arrColumns.get(0).colEqVal());
//	if (primaryColumnsFromBO.size() > 1) {
//		arrColumns.subList(1, arrColumns.size()).forEach((colVal) -> {
//			sb.append("AND " + colVal.colEqVal());
//		});
//	}
//	return sb.toString();
//}
