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
package builder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import clientcomponents.NTable;
import generic.BaseConnection;

public class Freemaker {
	private Configuration config = new Configuration(Configuration.VERSION_2_3_25);
	public HashMap<String, java.lang.Object> data = new HashMap<String, java.lang.Object>();

	public Freemaker()  {
		try {
			config.clearTemplateCache();
			config.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir") + "/src/builder/"));
			config.setDefaultEncoding("UTF-8");
			config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			config.setLogTemplateExceptions(false);
		}
		catch (IOException e) {e.printStackTrace();}
	}
//create method with if null for configuration
	
	public void build(String type, BaseConnection baseConnection) {
		try {
			NTable tbo = ((NTable) data.get("table"));
			String directory = System.getProperty("user.dir") + "/src/dbs/" + baseConnection.getLogin().getDbInstance() +"/" + tbo.getSchema();
			File dir = new File(directory); 
			if (!dir.exists()) dir.mkdirs();
			String filename =  ((NTable) data.get("table")).getTable() + type + ".java";
			FileWriter writer = new FileWriter(new File(directory + "/" + filename.replaceAll(" ", "_")));
			Template template = config.getTemplate(type + ".ftl");
			template.process(data, writer);
			writer.close();
		} 
		catch (TemplateException | IOException e) { e.printStackTrace(); }
	}

	public void prebuild(String type) {
		try {
			String table = (String) data.get("table");
			String database = (String) data.get("database");
			String directory = System.getProperty("user.dir") + "/src/builder/" + database;
			File dir = new File(directory); 
			if (!dir.exists()) dir.mkdirs();
			String filename =  table + "_" + type + ".java";
			FileWriter writer = new FileWriter(new File(directory + "/" + filename.replaceAll(" ", "_")));
			Template template = config.getTemplate(type + ".ftl");
			template.process(data, writer);
			writer.close();
		} 
		catch (TemplateException | IOException e) { e.printStackTrace(); }
	}

}