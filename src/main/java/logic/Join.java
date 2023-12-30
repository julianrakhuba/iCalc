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
package logic;

import layer.LAY;

public class Join {
	
	private LAY fromLay;
	private String sqlColumn;
	private String schema;
	private String table;
	private String column;
	private String remote_schema;
	private String remote_table;
	private String remote_column;
		
	public Join(LAY fromLay, String sqlColumn, String schema, String table, String column,String remote_schema, String remote_table, String remote_column) {				
		this.sqlColumn = sqlColumn;
		this.fromLay = fromLay;
		
		this.schema = schema;
		this.table = table;
		this.column = column;
		this.remote_schema = remote_schema;
		this.remote_table = remote_table;
		this.remote_column = remote_column;
	}
	
	public LAY getLay() {
		return fromLay;
	}

	public String getSqlColumn() {
		return this.sqlColumn;
	}
	
	public String getColumn() {
		return column;
	}
		
	public String getTable() {
		return table;
	}
	
	public String getSchema() {
		return schema;
	}
	
	public String getRemoteColumn() {
		return remote_column;
	}
	
	public String getRemoteTable() {
		return remote_table;
	}
	
	public String getRemoteSchema() {
		return remote_schema;
	}
	
	public boolean isLocal_by_Derived () {
		return fromLay.getNnode().getSchema().equals(this.getRemoteSchema());
	}
	
	public String toString() {

		return super.toString() + " • " + fromLay.getAliase() + "	"
		+ " sql: " + sqlColumn 
		+ " s: " + schema 
		+ " t: " + table 
		+ " c: " + column 
		+ " rs: " + remote_schema 
		+ " rt: " + remote_table 
		+ " rc: " + remote_column 
		
		
		;
	}

}