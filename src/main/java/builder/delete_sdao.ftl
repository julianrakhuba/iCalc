package rakhuba.builder.${database};//•

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

import rakhuba.generic.DAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ${table?replace(" ", "_")}_sdao  extends DAO{
	
	public ${table?replace(" ", "_")}_sdao(Connection connection){
		this.connection = connection;
	}
	
	public ObservableList<${table?replace(" ", "_")}_sbo> readFromDB(String sql){		
		ObservableList<${table?replace(" ", "_")}_sbo> list = FXCollections.observableArrayList();
		super.openStatement();
		ResultSet resultSet = super.executeQuery(sql);		
		try {while (resultSet.next()) {
			 ${table?replace(" ", "_")}_sbo bo = new  ${table?replace(" ", "_")}_sbo(resultSet);
				list.add(bo);
		}} 
		catch (SQLException e) {e.printStackTrace();}	
		super.closeStatement();
		return list;
	}
}