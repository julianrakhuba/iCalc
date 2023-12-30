package rakhuba.dbs.${dbz}.${table.schema}; //

import javafx.beans.property.SimpleObjectProperty;
import java.sql.ResultSet;
import java.sql.SQLException;
import rakhuba.generic.BO;
//<#if options.getImportDate() == "true">import java.sql.Date;
//</#if><#if options.getTimestamp() == "true">import java.sql.Timestamp;
//</#if><#if options.getTime() == "true">import java.sql.Time;</#if>

public class  ${table.table?replace(" ", "_")}BO  extends BO{
	//<#list columns as column> 
	//public final SimpleObjectProperty<${column.java_type}> ${column.column} = new SimpleObjectProperty<${column.java_type}>(this , "${column.column}"); 
	//</#list>
		
	//public ${table.table?replace(" ", "_")}BO(){
	//	this.populateFields();
	//}
	
	public ${table.table?replace(" ", "_")}BO(ResultSet resultSet){
		this.populateFields();
		try {
			<#list columns as column>  
			${column.column}.setValue(resultSet.get${column.rowset_type}(resultSet.findColumn("${column.column}")));
			</#list>
		//if(resultSet.wasNull()) rental_id.setValue(999999999);// use after value was read for each column to check for null Int
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	private void populateFields() {
		<#list columns as column>
		 fields.put("${column.column}", ${column.column});
		</#list>
	}
	
	public String getUNIQUE_WHERE() {
	StringBuilder sb = new StringBuilder("WHERE ");
	<#if (keys?size > 0)>
		<#list keys as key>
		sb.append(" ${key.column} "  + (${key.column}.getValue() == null ? " is null " : "= '" +  ${key.column}.getValue()  + "'") ${key?is_last?then('', ' + " AND "')});
		</#list>
	<#else>
	//UniqueId (Using All Columns because there is no Primary Key in Database) UNIQUE_WHERE
		<#list columns as column>
		sb.append(" ${column.column} "  + (${column.column}.getValue() == null ? " is null " : "= '" +  ${column.column}.getValue()  + "'") ${column?is_last?then('', ' + " AND "')});
		</#list>		
	</#if>
		return sb.toString();
	}
	
	public String getINTO() {
		StringBuilder columnsSB = new StringBuilder("("); StringBuilder valueSB = new StringBuilder("VALUE (");
		<#list columns as column>
		columnsSB.append("${column.column}${column?is_last?then('', ',')}"); valueSB.append((${column.column}.getValue() == null ? "null" : "'" + ${column.column}.getValue() + "'") + "${column?is_last?then('', ',')}");
		</#list>
		columnsSB.append(") "); valueSB.append(") ");
		return columnsSB.toString() + valueSB.toString();
	}
	
	
	<#list columns as column> 
	public ${column.java_type}   get${column.column?cap_first}(){return ${column.column}.get();}
	public void set${column.column?cap_first}(${column.java_type} ${column.column}){this.${column.column}.set(${column.column});}
	</#list>
	
	public String toString(){return "{" + <#list columns as column>"${column.column}: " + this.${column.column}.getValue()${column?is_last?then('', ' + ", " + ')}</#list> + "}";}
	
}