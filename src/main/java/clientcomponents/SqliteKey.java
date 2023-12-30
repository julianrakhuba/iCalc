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
package clientcomponents; //•mysql1

import java.sql.ResultSet;
import java.sql.SQLException;

public class  SqliteKey{
	private String id; 
	private String seq; 
	private String table; 
	private String from; 
	private String to; 
	private String on_update; 
	private String on_delete; 
	private String match; 
		
	public SqliteKey(ResultSet rs){
		try {
			id = rs.getString(rs.findColumn("id"));
			seq = rs.getString(rs.findColumn("seq"));
			table = rs.getString(rs.findColumn("table"));
			from = rs.getString(rs.findColumn("from"));
			to = rs.getString(rs.findColumn("to"));
			on_update = rs.getString(rs.findColumn("on_update"));
			on_delete = rs.getString(rs.findColumn("on_delete"));
			match = rs.getString(rs.findColumn("match"));
		} catch (SQLException e) {e.printStackTrace();}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getOn_update() {
		return on_update;
	}

	public void setOn_update(String on_update) {
		this.on_update = on_update;
	}

	public String getOn_delete() {
		return on_delete;
	}

	public void setOn_delete(String on_delete) {
		this.on_delete = on_delete;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}
	
	public String toString(){return "{" + "id: " + id + ", " + "seq: " + seq + ", " + "table: " + table + ", " + "from: " + from + ", " + "to: " + to + ", " + "on_update: " + on_update + ", " + "on_delete: " + on_delete + ", " + "match: " + match + "}";}
	
}