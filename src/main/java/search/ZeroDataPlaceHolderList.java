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
package search;

import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class ZeroDataPlaceHolderList extends ListView<String> {

	public ZeroDataPlaceHolderList() {
		this.setEditable(true);
		this.setFocusTraversable(false);
		this.getItems().addAll(" ");
		this.setCellFactory(param -> new MyCell());
		this.setStyle("-fx-background-insets: 0 ;");
    	this.setStyle("-fx-padding: 0px;");
    	
		this.setPrefHeight(200);
	}
	
	private class MyCell extends ListCell<String> {
		
	    public MyCell() {
	    	this.setPadding(new Insets(6,4,6,4));
	    	this.setStyle(" -fx-font-size: 10;");
		}

	    public void updateItem(String txt, boolean empty) {
	        super.updateItem(txt, empty);
	        if (empty) {
	        	this.setText(null);
	            this.setId(null);
	            this.setGraphic(null);
	        } else {
	        	this.setText(txt);
	        }
	    }
	    
	}
}
