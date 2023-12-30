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
package layer;

import logic.Field;
import logic.Join;
import map.Nnode;
import status.SqlType;

public class SLayer extends LAY {
	public SLayer(Nnode nnode, SqlType type) {
		super(nnode, type);
		nnode.getNmap().getNapp().getDBManager().getActiveConnection().getXMLBase().getXColumns().filtered(c ->  (c.getSchema().equals(nnode.getSchema()) && c.getTable().equals(nnode.getTable()))).forEach(xcol -> {
			//CREATE FIELD
			Field field = new Field(this);
			field.setSchema(xcol.getSchema());
			field.setTable(xcol.getTable());
			field.setColumn(xcol.getColumn());
			field.setRowset_type(xcol.getRowset_type());
			field.setColumn_key(xcol.getColumn_key());
			
			field.setSQL_Column_name(xcol.getColumn());
			field.setAliase(this.getAliase() + "_" + xcol.getColumn());
			field.setText(xcol.getColumn());
			this.addField(field);
			
			//CREATE JOINS
			nnode.getNmap().getNapp().getDBManager().getActiveConnection().getXMLBase().getKeys().filtered(k -> 
			k.getConst().equals("FOREIGN KEY") &&
			k.getSchema().equalsIgnoreCase(field.getSchema()) &&
			k.getTable().equalsIgnoreCase(field.getTable()) &&
			k.getColumn().equalsIgnoreCase(field.getColumn())
			).forEach(key -> {
				Join join = new Join(this, key.getColumn(), key.getSchema(), key.getTable(), key.getColumn(), key.getRSchema(), key.getRTable(), key.getRColumn());
				field.addJoin(join);
			});
			
			//r keys
			nnode.getNmap().getNapp().getDBManager().getActiveConnection().getXMLBase().getKeys().filtered(k -> 
			k.getConst().equals("FOREIGN KEY") &&		
			k.getRSchema().equalsIgnoreCase(field.getSchema()) &&
			k.getRTable().equalsIgnoreCase(field.getTable()) && 
			k.getRColumn().equalsIgnoreCase(field.getColumn())		
					).forEach(key -> {
				Join join = new Join(this, key.getRColumn(), key.getRSchema() , key.getRTable() ,  key.getRColumn() , key.getSchema() , key.getTable() , key.getColumn());
				field.addJoin(join);
			});
		});		
	}
}
