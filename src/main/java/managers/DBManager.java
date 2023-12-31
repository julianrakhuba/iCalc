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
package managers;

import java.util.HashMap;

import application.iCalc;
import builder.Build;
import connections.MicroSoftConn;
import connections.MysqlConn;
import connections.OracleConn;
import connections.PostgresConn;
import connections.SqliteConn;
import generic.BaseConnection;
import login.Login;
import login.Configuration;
import status.ConnectionStatus;

public class DBManager {
	private iCalc napp;
	private Configuration logins;
	private HashMap<Login,BaseConnection> connections = new HashMap<Login,BaseConnection>();	
	private BaseConnection activeConnection;
	
	public DBManager(iCalc napp) {
		this.napp = napp;
		logins = new Configuration(napp);
		logins.getLoginList().forEach(lgin ->{
			if(lgin.getDb().equals("mysql")) {
				connections.put(lgin, new MysqlConn(lgin, napp));
			}else if(lgin.getDb().equals("sqlite")){
				connections.put(lgin, new SqliteConn(lgin, napp));
			}else if(lgin.getDb().equals("oracle")){
				connections.put(lgin, new OracleConn(lgin, napp));	
			}else if(lgin.getDb().equals("postgres")){
				connections.put(lgin, new PostgresConn(lgin, napp));
			}else if(lgin.getDb().equals("sqlserver")){
				connections.put(lgin, new MicroSoftConn(lgin, napp));
			}else {
				//why I am here??? No dtatbase driver?");
			}			
		});
	}

	public Configuration getConfiguration() {
		return logins;
	}
		
	public BaseConnection getActiveConnection() {
		return activeConnection;
	}

	public void activateConnection(Login login) {
		this.closeUserConnectionIfOpen();//close previous user connection
//		if(PasswordUtils.verifyUserPassword(login.getPassword(), login.getSecuredPassword(), login.getSalt())) {//IF VALID SECURED PASSWORD
		activeConnection = connections.get(login);	
		//••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
			Build b2 = new Build();
			b2.loops(activeConnection);		
		//••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
	
		//app config
		napp.getMenu().getFileMenu().getNewMenu().getItems().clear();
		activeConnection.getXMLBase().getSchemas().forEach(sk -> napp.getMenu().getFileMenu().addNewSchemaToMenu(sk));
		if(!napp.getMenu().getFileMenu().getSavePasswordMenuItem().isSelected()) login.setPassword("");//do not save visual password
		logins.save();
		napp.getBottomBar().getLight().setStatus(ConnectionStatus.CONNECTED);
		napp.getMenu().getFileMenu().activateConnectionMenus();
	}
	

	public void closeUserConnectionIfOpen() {
		if(activeConnection != null) activeConnection.closeIfOpen();
		napp.getBottomBar().getLight().setStatus(ConnectionStatus.DISCONNECTED);
		napp.getMenu().getFileMenu().deactivateConnectionMenus();
		activeConnection = null;
	}
}
