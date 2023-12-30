package rakhuba.builder.${database};//•

import java.sql.ResultSet;
import java.sql.SQLException;

public class  ${table?replace(" ", "_")}_sbo{
	<#list columns as column>
	private String ${column};	
	</#list>

	public ${table?replace(" ", "_")}_sbo(ResultSet resultSet){
		try {
			<#list columns as column>    
			${column} = resultSet.getString(resultSet.findColumn("${column}"));
			</#list>
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	<#list columns as column>
	public String get${column}() {return ${column};}
	public void set${column}(String ${column}) {this.${column} = ${column};}	
	</#list>
}