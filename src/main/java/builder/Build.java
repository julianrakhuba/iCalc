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
package builder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import javafx.collections.ObservableList;
import clientcomponents.NColumn;
import clientcomponents.NKey;
import clientcomponents.NTable;
import clientcomponents.NType;
import clients.Meta;
import clients.XMLBase;
import generic.BaseConnection;

public class Build {
	Freemaker freemaker;
	ArrayList<String> generatedTables = new ArrayList<String>(); 
	private boolean chnagesMade;
	
	public void loops(BaseConnection conn) {
		Meta client = conn.getClientMetaData();
		XMLBase xmlBase = conn.getXMLBase();		
		String comment = "SQL TEXT";
		chnagesMade = false;
		if (!this.isJarClass())  backup_src(comment, xmlBase);
		
		boolean refresh = false;
		
		if(refresh) {
			refreshTypes(client, xmlBase); //types need to be first?
			refreshTables(client, xmlBase);
			refreshColumns(client, xmlBase);
			refreshKeys(client, xmlBase);
			if(chnagesMade) {
				System.out.println("•••••••••••••••••••••••••••••••••••••• SCHEMA WAS CHANGED IN DATABASE •••••••••••••••••••••••••••••••••••••••");
				xmlBase.save_existing_or_crate_new();
			}
		}
		
//		if (!this.isJarClass()) xmlBase.getSchemas().forEach(sch -> build(sch, xmlBase));
	}
	
	private  void refreshTypes(Meta clientBase, XMLBase xmlBase) {
		clientBase.getDataTypes().forEach(col -> {
			if(xmlBase.getXTypes().filtered(tp-> tp.getData_type().equalsIgnoreCase(col.getData_type())).size() == 0) {
				NType newType = new NType();
				newType.setData_type(col.getData_type());
//				newType.setJava_type("String"); //defaults every new record to string
				newType.setRowset_type("String");//defaults every new record to string
				xmlBase.getXTypes().add(newType);
				System.out.println("•	NEW TYPE: " + col.getData_type());
				chnagesMade = true;
			}//TYPES ONLY ADD, no remove no update
		});	
	}
	
	private void refreshTables(Meta clientBase, XMLBase xBase) {
		clientBase.getTables().forEach(ct -> {	
			if(xBase.getXTables().filtered(xt -> xt.eq(ct)).size() == 0) { //ADD
				NTable newxt = new NTable();
				newxt.setSchema(ct.getSchema());
				newxt.setTable(ct.getTable());
				newxt.setType(ct.getType());
				newxt.setLabel(ct.getTable());
				newxt.setX(1);
				newxt.setY(1);
				xBase.getXTables().add(newxt);
				System.out.println("••	ADD TABLE: " + newxt.getSchema() + "." +  newxt.getTable());
				chnagesMade = true;
			}else {
				//DON'T NEED UPDATE
			}
		});
		
		//REMOVE
		ObservableList<NTable>  clientTables = clientBase.getTables();
		ArrayList<NTable> deleteTables = new ArrayList<NTable>();
		xBase.getXTables().forEach(xTbl ->{
			if(clientTables.filtered(clTbl ->  clTbl.eq(xTbl)).size() == 0) {				 
				deleteTables.add(xTbl);
				chnagesMade = true;
			}
		});
		deleteTables.forEach(tb -> System.out.println("[*] REMOVE TABLE:" + tb.getSchema()+"." + tb.getTable()));
		xBase.getXTables().removeAll(deleteTables);
	}

	
	private  void refreshColumns(Meta clientBase, XMLBase xBase) {
		clientBase.getColumns().forEach(dbcol -> {
			NType nTypeBO = xBase.getXTypes().filtered(ntp -> ntp.getData_type().equalsIgnoreCase(dbcol.getData_type())).get(0);
			if(xBase.getXColumns().filtered(c -> c.eq(dbcol) ).size() == 0){
				NColumn ncolumnBO = new NColumn();
				ncolumnBO.setSchema(dbcol.getSchema());
				ncolumnBO.setTable(dbcol.getTable());
				ncolumnBO.setColumn(dbcol.getColumn());
				ncolumnBO.setPosition(dbcol.getPosition());
				ncolumnBO.setData_type(dbcol.getData_type());
				ncolumnBO.setRowset_type(nTypeBO.getRowset_type()); 
				ncolumnBO.setColumn_key(dbcol.getColumn_key());
				xBase.getXColumns().add(ncolumnBO);
				chnagesMade = true;
				System.out.println("•••	NEW COLUMN: "   + dbcol.getSchema() + "." + dbcol.getTable()+ "." + dbcol.getColumn());
			}else {//UPDATE
//				xBase.getXColumns().filtered(c -> c.eq(dbcol) ).forEach(col ->{
//					col.setRowset_type(nTypeBO.getRowset_type()); 
//				});
				
//				ncols.forEach(ncol ->{
//					ncol.setColumn_key(dbcol.getColumn_key());
//				});			
			}			
		});
		
		//REMOVE
		ArrayList<NColumn> deleteColumns = new ArrayList<NColumn>();
		ObservableList<NColumn>  clientTables = clientBase.getColumns();
		xBase.getXColumns().forEach(xCol ->{
			if(clientTables.filtered(clCol -> clCol.eq(xCol) ).size() == 0) {
				deleteColumns.add(xCol);
				chnagesMade = true;
			}
		});
		deleteColumns.forEach(tb -> System.out.println("[**] REMOVE COLUMN:" + tb.getSchema()+"." + tb.getTable() + "." + tb.getColumn()));
		xBase.getXColumns().removeAll(deleteColumns);
	}
	
	private  void refreshKeys(Meta dbBase, XMLBase xBase) {
		dbBase.getKeys().forEach(dbkey -> {
			if(xBase.getKeys().filtered(nkey -> nkey.eq(dbkey)).size() == 0){ 
				NKey nkeyBO = new NKey();
				nkeyBO.setConst(dbkey.getConst());
				nkeyBO.setSchema(dbkey.getSchema());
				nkeyBO.setTable(dbkey.getTable());
				nkeyBO.setColumn(dbkey.getColumn());
				nkeyBO.setRSchema(dbkey.getRSchema());
				nkeyBO.setRTable(dbkey.getRTable());
				nkeyBO.setRColumn(dbkey.getRColumn());
				xBase.getKeys().add(nkeyBO);
				chnagesMade = true;
				System.out.println("••••	NEW KEY: " + dbkey.getConst() + " " + dbkey.getSchema() + "." + dbkey.getTable() + "."+ dbkey.getColumn());
			}else {
				//UPDATE
			}
		});
		
		//REMOVE
		ArrayList<NKey> deleteKeys = new ArrayList<NKey>();
		ObservableList<NColumn> dbCols = dbBase.getColumns();
		ObservableList<NKey>  dbKeys = dbBase.getKeys();
		xBase.getKeys().forEach(xkey ->{
			if(dbKeys.filtered(dbkey -> dbkey.eq(xkey)).size() == 0){
				System.out.println("[***] MISSING key:" + xkey.getSchema()+"." + xkey.getTable() +  "."+ xkey.getColumn());
				if(!(dbCols.filtered(c -> (xkey.isKeyColumn(c)) ).size() > 0 && dbCols.filtered(c -> (xkey.isKeyRefColumn(c)) ).size() > 0)) {
					deleteKeys.add(xkey);
					chnagesMade = true;
				}else {
					System.out.println("[***] KEEP key:" + xkey.getSchema() + "." + xkey.getTable() +  "."+ xkey.getColumn());
				}
			}
		});
		xBase.getKeys().removeAll(deleteKeys);
		deleteKeys.forEach(xkey -> System.out.println("[***] REMOVE key:" + xkey.getSchema()+"." + xkey.getTable() +  "."+ xkey.getColumn()));
	}
	
	private Freemaker getFreemaker() {
		if(freemaker == null) freemaker = new Freemaker();
		return freemaker;
	}
	
    //GENERIC •••••••••••••••••••••••••••••••••••••••••••••••
	public  void build(String schemaName, XMLBase nbase) {
		//TO GENERATE USE ONLY NBASE TABLES
		ObservableList<NTable> tabls = nbase.getXTables().filtered(t -> t.getSchema().equals(schemaName)); 
		tabls.forEach(tbl ->{
			ObservableList<NColumn> columns = nbase.getXColumns().filtered(c -> c.getSchema().equals(schemaName) && c.getTable().equals(tbl.getTable()));
			ObservableList<NKey> keys = nbase.getKeys().filtered(k -> k.getSchema().equals(schemaName) && k.getTable().equals(tbl.getTable()) && k.getConst() != null && k.getConst().equals("PRIMARY KEY"));
			Options  options = new Options(columns);
			Freemaker fm = this.getFreemaker();
			fm.data.clear();
			fm.data.put("dbz", nbase.getClientBase().getLogin().getDbInstance());
			fm.data.put("table", tbl);
			fm.data.put("columns", columns);
			fm.data.put("keys", keys);
			fm.data.put("options", options);
			fm.build("BO", nbase.getClientBase());
//			fm.build("DAO",nbase.getClientBase());
			generatedTables.add(tbl.getTable());
		});
		System.out.println( generatedTables.size() + " TOTAL: "+ schemaName +" "+ tabls.size());
	}
	
	public  void backup_src(String comment, XMLBase xmlBase) {
//		if (!this.isJarClass()) {//TODO should not run when in [Jar]
		String dateTime = new SimpleDateFormat("yyyy.MM.dd.k.m").format(new Date());
		File srcDir = new File(System.getProperty("user.dir") + "/src");
		if(srcDir.exists()) {
			String destination = System.getProperty("user.home") + "/backup/" + dateTime  +  " " + comment;
			File destDir = new File(destination);
			try {FileUtils.copyDirectoryToDirectory(srcDir, destDir);} catch (IOException e) {e.printStackTrace();}					
			System.out.println("Backup: "+ comment + " at: " + dateTime);
		}
//		}
	}
	
	private boolean isJarClass() {
		 return this.getClass().getResource("/" + this.getClass().getName().replace('.', '/') + ".class").toString().startsWith("jar:");
	}
}

