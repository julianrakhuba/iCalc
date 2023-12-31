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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import application.iCalc;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import layer.LAY;
import logic.SQL;

public abstract class DAO {
	public iCalc napp;	
	public  Connection connection = null; 
	private  Statement statement = null; 
	private  ResultSet resultSet = null;
	
	public void openStatement(){
		try {
			if(napp != null) {
				statement = napp.getDBManager().getActiveConnection().getJDBC().createStatement();				
			}else {
				statement = connection.createStatement();
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		} //1045 wrong password e.getErrorCode()
	}
	
	
	public void closeStatement(){
		try {
		if(!(resultSet == null)){resultSet.close();}
			statement.close(); 
		} 
		catch (SQLException e) {e.printStackTrace();}
	}
	
	
	public ResultSet executeQuery(String  query){
		try {
			resultSet = statement.executeQuery(query);
		}
		catch (SQLException ex) { 
			ex.printStackTrace(); 
			} //1142 denied access to table ex.getErrorCode() 
		return resultSet;
	}
	
	public void executeUpdate(String query){		
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {e.printStackTrace();}
	}
	

	//is this used???
//	public int getSqlSize(SQL sql){
//		int rowsCountInDB = 0;
//		this.openStatement();
////		ResultSet resultSet = this.executeQuery(sql.COUNTDB().toString());
//		ResultSet resultSet = this.executeQuery(sql.toString());
//		try {resultSet.next(); rowsCountInDB = resultSet.getInt("countSql");} catch (SQLException e) {e.printStackTrace();}
//		this.closeStatement();
//		return rowsCountInDB;	
//	}
//	public ArrayList<String> readPivotColumns(SQL sql, Field field){
//		ArrayList<String> pivotColumns = new ArrayList<String>();
//		this.openStatement();		
//		ResultSet resultSet = this.executeQuery(sql.toString());
//		
//		try {
////			resultSet.last();
////			int row = resultSet.getRow();
////			
////			if(row < 25) {//REMOVED BECOUSE MYSQL IS FORWARD ONLY
////				resultSet.beforeFirst();			
////				while (resultSet.next()) pivotColumns.add(resultSet.getString(resultSet.findColumn(field.getSQL_Column_name())));
//				while (resultSet.next()) pivotColumns.add(resultSet.getString(resultSet.findColumn(field.getFunction_Column())));
//
////			}else {
////				//Create Message to to user to let them know to reduce pivot selection
////				
////			}
//		}
//		catch (SQLException e) {e.printStackTrace();}
//		this.closeStatement();
//		return pivotColumns;
//	}
	
	public ObservableList<OpenBO> readDB(SQL sql, LAY lay) {
		ClipboardContent content = new ClipboardContent();
		ObservableList<OpenBO> openBOs2 = FXCollections.observableArrayList();
		String statement = sql.toString();
		content.putString(statement);
		Clipboard.getSystemClipboard().setContent(content);	
		this.openStatement();
		ResultSet resultSet = this.executeQuery(statement);
		try {while (resultSet.next()) {
			openBOs2.add(new OpenBO(resultSet, lay));
		}} catch (SQLException e) {e.printStackTrace();}
		this.closeStatement();
		return openBOs2;
	}
	
	
	//NEW
	public ArrayList<String> readDistinctValues(SQL sql, String column){
		ArrayList<String> distinctValues = new ArrayList<String>();
		String query = sql.toString();
		this.openStatement();
		ResultSet resultSet = this.executeQuery(query);
		try {while (resultSet.next()) {
			String value = resultSet.getString(resultSet.findColumn(column));
			distinctValues.add(value == null ? "null" : value);
		}}
		catch (SQLException e) {e.printStackTrace();}	
		this.closeStatement();
		return distinctValues;
	}

}
