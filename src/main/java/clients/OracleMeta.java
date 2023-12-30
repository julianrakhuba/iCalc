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

public class OracleMeta extends Meta{
	
	public OracleMeta(Connection connection) {
		this.connection = connection;
		typesSql = "select distinct(cols.data_type) \"data_type\" from all_tab_columns cols WHERE OWNER in ('COMPUTERSTORE','SAKILA')";
		tablesSql = "select owner as \"table_schema\", VIEW_NAME as \"table_name\", 'VIEW' as \"table_type\" from all_views WHERE OWNER in ('COMPUTERSTORE','SAKILA') union select owner as \"table_schema\", TABLE_NAME as \"table_name\", 'BASE_TABLE' as \"table_type\"  from all_tables  WHERE OWNER in ('COMPUTERSTORE','SAKILA')";
		columnsSql = "select cols.column_id  as \"position\", cols.owner as \"table_schema\", cols.table_name  as \"table_name\",  cols.column_name  as \"column_name\", cols.data_type as \"data_type\" from all_tab_columns cols WHERE OWNER in ('COMPUTERSTORE','SAKILA')";
		keysSql = "SELECT 'PRIMARY KEY' \"constraint\", cons.owner \"table_schema\", cols.table_name \"table_name\", cols.column_name \"column_name\", '' as \"ref_table_schema\",   '' as \"ref_table_name\",  '' as \"ref_column_name\" FROM all_constraints cons, all_cons_columns cols WHERE cons.constraint_type = 'P' AND cols.OWNER in ('COMPUTERSTORE','SAKILA') AND cons.constraint_name = cols.constraint_name AND cons.owner = cols.owner  union SELECT 'FOREIGN KEY' \"constraint\", a.owner \"table_schema\", a.table_name \"table_name\", a.column_name \"column_name\",  b.owner as \"ref_table_schema\",   b.table_name \"ref_table_name\",  b.column_name \"ref_column_name\"  FROM all_cons_columns a  JOIN all_constraints c ON a.owner = c.owner  AND a.constraint_name = c.constraint_name  JOIN all_constraints c_pk ON c.r_owner = c_pk.owner  AND c.r_constraint_name = c_pk.constraint_name  JOIN all_cons_columns b ON C_PK.owner = b.owner  AND  C_PK.CONSTRAINT_NAME = b.constraint_name AND b.POSITION = a.POSITION  WHERE c.constraint_type = 'R'  and c.OWNER in ('COMPUTERSTORE','SAKILA')";
		clientDAO = new ClientDAO(this.connection);
	}
}