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

import java.text.DateFormatSymbols;
import java.text.NumberFormat;

import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import pivot.FieldVersion;
//import rakhuba.generic.OpenBO;

public class TableCellNumber extends TableCell<OpenBO, Number> {
	private FieldVersion version;
	
    public TableCellNumber(FieldVersion version) {
    	this.version = version;
    	this.setPadding(new Insets(6,4,6,4));
	}

    protected void updateItem(Number value, boolean empty) {
        super.updateItem(value, empty);
        if (value == null || empty) {
            setText(null);
        } else {
        	String id = version.getField().getFormat().getId();
        	if(id.equals("plain")) {
        		setText(value.toString());
        	}else if(id.equals("currency")) {// currency
            	setText(NumberFormat.getCurrencyInstance().format(value));            	
        	}else if(id.matches("double|longdouble")) {// 0.00
            	setText(String.format(version.getField().getFormat().getFormatString(), value.doubleValue()));            	
        	}else if(id.equals("month") && value.intValue() > 0 && value.intValue() <= 12) {//Month
            	setText(new DateFormatSymbols().getShortMonths()[value.intValue() -1]);            	
        	}else if(id.equals("weekday") && value.intValue() >=0 && value.intValue() <= 6) {//Weekday
            	setText(new DateFormatSymbols().getShortWeekdays()[value.intValue() +1]);            	
        	}else if(id.equals("year") && value.intValue() > 0) {//Year
        		setText(value.toString());
        	}else {
        		setText("-");        		
        	}
        }
    }
}