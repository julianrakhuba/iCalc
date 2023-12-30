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
import java.util.HashSet;
import java.util.List;

import clientcomponents.ClientDAO;
import clientcomponents.NColumn;
import clientcomponents.NKey;
import clientcomponents.NTable;
import clientcomponents.NType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SqliteMeta extends Meta {	
	private ObservableList<NType> dataTypesList;
	private ObservableList<NColumn> columnsList;
	private ObservableList<NTable> tablesList;
	private ObservableList<NKey> keysList;
	
	//temporary here need to delete ??
	 List<String> schemas;
	 
	public SqliteMeta(Connection connection, List<String> schemas) {
		this.connection = connection;
		this.schemas = schemas;
		clientDAO = new ClientDAO(this.connection);
	}

	//CLIENT OVERWRITE
	public ObservableList<NType> getDataTypes() {
		if(dataTypesList != null) return dataTypesList;
		dataTypesList = FXCollections.observableArrayList();
		HashSet<String> types = new HashSet<String>();
		this.getColumns().forEach(col-> types.add(col.getData_type()));
		types.forEach(tp -> dataTypesList.add(new NType(tp)));
		return dataTypesList;
	}
	
	public ObservableList<NColumn> getColumns() {
		if(columnsList != null) return columnsList;
		columnsList = FXCollections.observableArrayList();
		this.getTables().forEach(tbl -> {
			String sqlt = " PRAGMA " + tbl.getSchema() + ".table_info('" + tbl.getTable() + "');";
			clientDAO.getSqliteColumns(sqlt).forEach(scol->{				
				NColumn newcol = new NColumn(scol.getCid(), tbl.getSchema(), tbl.getTable(), scol.getName(), scol.getTypeCleaned(), scol.getPk());
				columnsList.add(newcol);
			});
		});	
		return columnsList;
	}
	
	public ObservableList<NTable> getTables() {
		if(tablesList != null) return tablesList;
		tablesList = FXCollections.observableArrayList();
		schemas.forEach(sch -> {
			String sql = "SELECT '" + sch + "' as table_schema, tbl_name as table_name, CASE type WHEN 'table' THEN 'BASE TABLE'  WHEN 'view' THEN 'VIEW' END table_type FROM " + sch + ".sqlite_master where  type IN ('table','view') AND name NOT LIKE 'sqlite_%'";
			tablesList.addAll(clientDAO.getTables(sql));
		});		
		return tablesList;
	}

	public ObservableList<NKey> getKeys() {
		if(keysList != null) return keysList;
		keysList = FXCollections.observableArrayList();
		this.getTables().forEach(tbl -> {
			String sql = " PRAGMA "+ tbl.getSchema() + ".foreign_key_list('" + tbl.getTable() + "');";
			clientDAO.getSqliteKey(sql).forEach(skey -> {
				NKey newkeyBO = new NKey("FOREIGN KEY", tbl.getSchema(), tbl.getTable(), skey.getFrom(), tbl.getSchema(), skey.getTable(), skey.getTo());
			 keysList.add(newkeyBO);
		  });
		});			
		return keysList;
	}
}
