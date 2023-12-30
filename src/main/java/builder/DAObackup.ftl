package rakhuba.dbs.${dbz}.${table.schema}; //••

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

import rakhuba.generic.DAO;
import rakhuba.logic.SQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import rakhuba.application.Napp;

public class ${table.table?replace(" ", "_")}DAO  extends DAO{

	
	public ${table.table?replace(" ", "_")}DAO(Connection connection){
		this.connection = connection;
	}
	
	public ${table.table?replace(" ", "_")}DAO(Napp napp){
		this.napp = napp;
	}
	
	public ObservableList<${table.table?replace(" ", "_")}BO> readFromDB(String sql){
		ObservableList<${table.table?replace(" ", "_")}BO> list = FXCollections.observableArrayList();
		String query = sql + ";";
		super.openStatement();
		ResultSet resultSet = super.executeQuery(query);
		try {while (resultSet.next()) {
				${table.table?replace(" ", "_")}BO bo = new ${table.table?replace(" ", "_")}BO(resultSet);
				list.add(bo);
		}} 
		catch (SQLException e) {e.printStackTrace();}	
		super.closeStatement();
		return list;
	}
	
	public void save${table.table?replace(" ", "_")}BOtoDatabase(${table.table?replace(" ", "_")}BO bo){
		int existingRecords = this.getSqlSize(new SQL().SELECT(" COUNT(*) as countSql FROM ${table.schema?replace(" ", "_")}.${table.table?replace(" ", "_")} " + bo.getUNIQUE_WHERE() + ";"));
		if(existingRecords==0){
			this.insertIntoDB(bo);
		}else if (existingRecords == 1){
			this.deleteRecord(bo);
			this.insertIntoDB(bo);
		}
		else{
			System.out.println(bo.getUNIQUE_WHERE() + " Should not have more than one record with same primary key in Database");		
		}
	}
	
	public void deleteRecord(${table.table?replace(" ", "_")}BO bo){
		String query = "DELETE from ${table.schema?replace(" ", "_")}.${table.table?replace(" ", "_")} " + bo.getUNIQUE_WHERE() + ";";
		super.openStatement();
		super.executeUpdate(query);
		super.closeStatement();
	}
	
	private void insertIntoDB(${table.table?replace(" ", "_")}BO bo){
		String query = "INSERT INTO ${table.schema?replace(" ", "_")}.${table.table?replace(" ", "_")} " + bo.getINTO() + ";";
		super.openStatement();
		super.executeUpdate(query);
		super.closeStatement();
	}
}