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

import java.io.File;

import application.Constellatio;
import file.NFile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class FileManager {
	private Constellatio napp;
	private ObjectProperty<NFile> activeNFile = new SimpleObjectProperty<NFile>();
	private ObservableList<NFile> openFiles = FXCollections.observableArrayList();	
	private File autoOpenFile;
	
	public FileManager(Constellatio napp) {
		this.napp = napp;
		activeNFile.addListener((c,f,h) -> {
			if(activeNFile.getValue() != null) {
				activeNFile.getValue().setAppTitle();
				napp.getMenu().getSchemaMenu().getEditSchema().setDisable(false);			
			}else {
				napp.setTitle("");// when all files are closed
				napp.getMenu().getSchemaMenu().getEditSchema().setDisable(true);
			}
		});		
		openFiles.addListener((ListChangeListener<NFile>) c -> {
			napp.getMenu().disableMenus(c.getList().size() == 0);
		});//if more than one file 
	}
	
	public void createNewFile(String schemaName) {
		String db = getNapp().getDBManager().getActiveConnection().getLogin().getDb();
		File file = new File(System.getProperty("user.home") + "/documents/"+ db + "_"+ (openFiles.size() + 1) + ".con");
		NFile nfile = new NFile(file, this);
		nfile.setNewFile(true);
		nfile.createNewMap(schemaName);
		this.selectNFile(nfile);	
		openFiles.add(nfile);
		activeNFile.set(nfile);
		nfile.getUndoManager().saveUndoAction();
	}

	
	public void openFileChooser() {
		final FileChooser fileChooser = new FileChooser();		
		fileChooser.setTitle("Open");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Constelattio Files", "*.xml", "*.con"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/documents"));
		File file = fileChooser.showOpenDialog(getNapp().getStage());
		this.openFile(file);	
	}
	
	public void setAutoOpenFile (File file) {
		this.autoOpenFile = file;
	}
	
	public void openFile(File file) {
		if (file != null) {
			if(file.isFile()) {
				NFile nfile = new NFile(file, this);
				openFiles.add(nfile);	
				activeNFile.set(nfile);
				nfile.openFile();				
			}
		}	
	}

	public NFile getActiveNFile() {
		return activeNFile.get();
	}

	public void save() {
		if(activeNFile.get().isNewFile()) {
			this.saveAs();
		}else {			
			activeNFile.get().saveFile();
		}
	}

	public void saveAs() {
		final FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Save As");
    	
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Constelattio","*.con"));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("XML", "*.xml"));
		
    	fileChooser.setInitialDirectory(activeNFile.get().getXMLFile().getParentFile());
    	fileChooser.setInitialFileName(activeNFile.get().getXMLFile().getName());   	
    	File zip = fileChooser.showSaveDialog(getNapp().getStage());
    	if(zip != null) {
    		activeNFile.get().setNewFile(false);
    		activeNFile.get().setZIP(zip);//SETS NEW FILE     		
        	activeNFile.get().saveFile();
        	activeNFile.getValue().setAppTitle();
    	}
	}
	
	public void closeActiveFile() {
		activeNFile.get().getActivity().closeActivity();
		activeNFile.get().getSidePane().close();
		getNapp().getBorderPane().setCenter(null);
		openFiles.remove(activeNFile.get());
		activeNFile.set(null);		
		getNapp().getBottomBar().getSumLabel().clear();
		getNapp().getBottomBar().getCountLabel().clear();
		getNapp().getBottomBar().getRowsCount().clear();
		if(openFiles.size() > 0) this.selectNFile(openFiles.get(0));
		getNapp().getConsole().clear();
	}

	public void closeAllFiles() {
		openFiles.clear();
		activeNFile.get().getSidePane().close();
		activeNFile.set(null);
		getNapp().getBorderPane().setCenter(null);
		getNapp().getConsole().clear();
	}
	
	public ObservableList<NFile> getOpenFiles() {
		return openFiles;
	}
	
	public int size() {
		return openFiles.size();
	}
	
	public void selectNFile(NFile nfile) {
		activeNFile.set(nfile);
		nfile.ActivateFile();
	}

	public void setCompactView(boolean b) {
		openFiles.forEach(nf -> {
			nf.setCompactView(b);
		});
		if(activeNFile.get() != null) activeNFile.get().refreshTempFixForOffsetIssue();
	}

	public void openAutoFile() {
		if(this.autoOpenFile != null) {
			this.openFile(autoOpenFile);
			autoOpenFile = null;
		}
	}

	/**
	 * @return the napp
	 */
	public Constellatio getNapp() {
		return napp;
	}

}
