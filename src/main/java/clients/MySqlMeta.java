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
package clients;

import java.sql.Connection;

import clientcomponents.ClientDAO;


public class MySqlMeta  extends Meta{
	
	public MySqlMeta(Connection connection) {
		this.connection = connection;
		typesSql = "select distinct(data_type) 'data_type' FROM information_schema.COLUMNS";
		tablesSql = "select table_schema  as 'table_schema',  table_name as 'table_name', table_type as 'table_type' from information_schema.tables where table_schema not in ('sys', 'mysql','performance_schema')";
		columnsSql = "select ordinal_position as 'position', table_schema as 'table_schema', table_name as 'table_name', column_name as 'column_name', data_type as 'data_type', COLUMN_KEY as 'column_key' FROM information_schema.COLUMNS where table_schema not in ('sys', 'mysql','performance_schema')";
		keysSql = "select t.CONSTRAINT_TYPE as 'constraint', k.TABLE_SCHEMA as 'table_schema', k.TABLE_NAME as 'table_name', k.COLUMN_NAME as 'column_name', k.REFERENCED_TABLE_SCHEMA as 'ref_table_schema', k.REFERENCED_TABLE_NAME as 'ref_table_name' , k.REFERENCED_COLUMN_NAME as 'ref_column_name' from information_schema.KEY_COLUMN_USAGE k join  information_schema.TABLE_CONSTRAINTS t on k.CONSTRAINT_CATALOG = t.CONSTRAINT_CATALOG and k.CONSTRAINT_SCHEMA = t.CONSTRAINT_SCHEMA and k.CONSTRAINT_NAME = t.CONSTRAINT_NAME and k.TABLE_SCHEMA = t.TABLE_SCHEMA and k.TABLE_NAME = t.TABLE_NAME where t.CONSTRAINT_TYPE in ('PRIMARY KEY', 'FOREIGN KEY') and k.table_schema not in ('sys', 'mysql','performance_schema')";
		clientDAO = new ClientDAO(this.connection);
	}	
}
