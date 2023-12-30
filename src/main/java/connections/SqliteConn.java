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
package connections;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import application.Constellatio;
import clients.Meta;
import clients.SqliteMeta;
import generic.BaseConnection;
import login.Login;

public class SqliteConn extends BaseConnection {
	private Meta meta;

	public SqliteConn(Login lgin, Constellatio napp) {
		super (lgin, napp);
	}
	
	public void connectToDB() {
		this.copySampleDb();
				
        try {
        	con = DriverManager.getConnection(login.getUrl());
        	Statement stmt  = con.createStatement();
        	login.getStatements().forEach(st ->{ try { 
	        	stmt.execute(st);
	        } catch (SQLException e) { e.printStackTrace(); }});

	        stmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	
	private void copySampleDb() {
		this.getLogin().getShcemas().forEach(sk ->{
			try {
	            InputStream inputStream =  getClass().getResourceAsStream("/"+sk+".db");
	            Path targetPath = Path.of(this.getNapp().getConfigurationPath() + sk + ".db");
	            if(!targetPath.toFile().exists()) {
	            	Files.copy(inputStream, targetPath);
	 	            System.out.println("Database copied to: " + targetPath);
//	 	           <statement command="Attach '/Users/julianrakhuba/dbs/Chinook.db' as 'Chinook';"/>
//	 	            <statement command="Attach '/Users/julianrakhuba/dbs/sakila.db' as 'sakila';"/>
//	 	            <statement command="Attach '/Users/julianrakhuba/dbs/North.db' as 'North';"/>
	 	            
	            }
	        } 
		  catch (IOException e) { 
//			  e.printStackTrace(); 
		  }
		});		
	}

	public String end() {
		return ";";
	}
	
	public Meta getClientMetaData() {
		if(meta == null) meta = new SqliteMeta(getJDBC(), login.getShcemas());
		return  meta;
	}
}
