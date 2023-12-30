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

public class PostgresMeta extends Meta {
	
	public PostgresMeta(Connection connection) {
		this.connection = connection;
		typesSql = "select distinct(data_type) as data_type from information_schema.columns";
		tablesSql = "select table_schema,  table_name , table_type from information_schema.tables where table_schema not in ('information_schema', 'pg_catalog')";	
		columnsSql = "select ordinal_position as position, table_schema, table_name, column_name, data_type, 'need_to_add_key' as column_key from information_schema.columns  where table_schema not in ('information_schema', 'pg_catalog')";
		keysSql = "select  tc.constraint_type as constraint , kcu.table_schema, kcu.table_name, kcu.column_name, ccu.table_schema as ref_table_schema , ccu.table_name as ref_table_name, ccu.column_name as ref_column_name from information_schema.key_column_usage kcu left join information_schema.referential_constraints rc on kcu.constraint_name  = rc.constraint_name  left join information_schema.constraint_column_usage ccu on ccu.constraint_name  = rc.constraint_name join information_schema.table_constraints tc on tc.constraint_name = kcu.constraint_name where tc.constraint_type in ('PRIMARY KEY', 'FOREIGN KEY')";
		clientDAO = new ClientDAO(this.connection);
	}
}