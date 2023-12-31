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
package map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import activity.Configure;
import application.iCalc;
import application.JoinLine;
import application.XML;
import clientcomponents.NFunction;
import clientcomponents.NTable;
import file.NFile;
import file.OpenContext;
import javafx.animation.FadeTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import layer.DLayer;
import layer.LAY;
import sidePanel.Message;
import status.ActivityMode;
import status.JoinType;
import status.Population;
import status.SqlType;

public class NMap  {
	private  iCalc napp;
	private  Pane schemaPane = new Pane();
	private ScrollPane scrollPane = new ScrollPane();
	private Group group = new Group(schemaPane);
	private String schemaName;
	private HashMap<String, Nnode> mapNodes = new HashMap<String, Nnode>();
	private LinkedHashMap<String, NFunction> dbFunctionsMap = new LinkedHashMap<String, NFunction>();
	private NFile nFile;
	
    private static final int PANE_WIDTH = 800;
    private static final int PANE_HEIGHT = 600;
    
    private static final double REPULSIVE_FORCE_FACTOR = 1000.0;
    private static final double ATTRACTIVE_FORCE_FACTOR = 2;

	public NMap(NFile nFile,String schema) {
		this.nFile = nFile;
		this.napp = nFile.getFileManager().getNapp();
		
		scrollPane.setContent(group);		
		if(getNapp().getStage().getStyle() == StageStyle.TRANSPARENT) {
			scrollPane.setStyle(" -fx-background-color: rgba(0, 0, 0, 0.5); "
		        		+ "-fx-border-width: 0.5;"
		        		+ "-fx-border-color: derive(#1E90FF, 50%);"
		        		+ "-fx-effect: dropshadow(gaussian, derive(#1E90FF, 40%) , 8, 0.2, 0.0, 0.0);"
		        		+ "-fx-background-radius: 3;"
		        		+ "-fx-border-radius: 3;");
		}else {
			
			scrollPane.setStyle("-fx-background-color: rgb(234, 236, 241); "
					+ "-fx-effect: dropshadow(two-pass-box , rgba(0, 0, 0, 0.05), 5, 0.4 , 2, 2);"
					+ "-fx-background-radius: 7;");
		}
		group.setStyle("-fx-background-color: orange;");
		schemaPane.setStyle("-fx-background-color: transparent;");

		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		
		schemaPane.setScaleX(1);
		schemaPane.setScaleY(1);
		schemaPane.setOpacity(0);

		schemaPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
			
			if (nFile.getFileManager().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
				 if (newScene != null) {
					 FadeTransition ft = new FadeTransition(Duration.millis(1000), schemaPane);
					 ft.setFromValue(0.0);
					 ft.setToValue(1.0);
					 ft.play();
				 }
			}else {
				schemaPane.setOpacity(1);
			}
			
		});

		scrollPane.setMinHeight(0);
		scrollPane.setPannable(false);// this prevents strange jumping ???
		
//				ScrollPane sp = ...
//				sp.setOnMousePressed(e -> {
//				  if (e.getButton() == MouseButton.MIDDLE) sp.setPannable(true);
//				});
//				sp.setOnMouseReleased(e -> {
//				  if (e.getButton() == MouseButton.MIDDLE) sp.setPannable(false);
//				});
		
		
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		scrollPane.setOnMouseClicked(e -> {
			if(nFile.getActivityMode() != ActivityMode.CONFIGURE) {//
				nFile.getActivity().closeActivity();
				nFile.setActivityMode(ActivityMode.SELECT);
				getNapp().getBottomBar().getSumLabel().clear();
				getNapp().getBottomBar().getCountLabel().clear();
				getNapp().getBottomBar().getRowsCount().clear();
				nFile.getSidePane().deactivate();
			}else if(nFile.getActivityMode() == ActivityMode.CONFIGURE) {
				Configure conf  = 	(Configure) nFile.getActivity();
				conf.clearSelection();
			}
		}); 
		this.schemaName = schema;
		getNapp().getDBManager().getActiveConnection().getXMLBase().getXTables().filtered( t -> t.getSchema().equals(schemaName)).forEach(table -> {		
			this.getMapNodes().put(table.getTable(), new Nnode(this,null, table));
		});
		this.getMapNodes().forEach((k,nnode) -> nnode.addRootLines());
		this.getMapNodes().forEach((k,nnode) -> this.add(nnode));		
		this.addFunctions();
	
		scrollPane.setOnZoom( zz ->{
			 schemaPane.setScaleX(schemaPane.getScaleX() * zz.getZoomFactor());
			 schemaPane.setScaleY(schemaPane.getScaleY() * zz.getZoomFactor());
		 });
		
		scrollPane.setFocusTraversable(false);		
	}
	
	public void createRootLines() {
		 this.getMapNodes().forEach((k,nnode) -> nnode.addRootLines());
	}
	
	private void addFunctions() {
		getNapp().getDBManager().getActiveConnection().getXMLBase().getXFunctions().forEach(f -> {
			dbFunctionsMap.put(f.getLabel(), f);
		});		
	}

	public LinkedHashMap<String, NFunction> getDbFunctionsMap() {
		return dbFunctionsMap;
	}

//	private DAO newDAO(NTableBO nTableBO) {
//		String shema = nTableBO.getSchema_name();
//		String table = nTableBO.getTable_name();
//		String db = nTableBO.getDb();
//		try {
//			DAO dao = (DAO) Class.forName("rakhuba.dbs."+ db + "." + shema + "." + table.replaceAll(" ", "_") + "DAO").getDeclaredConstructor(Napp.class).newInstance(napp);			
//			return dao;
//		} 
//		catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException  |InstantiationException | IllegalAccessException | ClassNotFoundException e) {
//			return null;
//		} 
//	}
	
	public Nnode getNnode(String tableName) {
		Nnode nnode =  getMapNodes().get(tableName);		
		return nnode;
	}
	
	public void clear(){
		if(this.nFile.getActivityMode() != ActivityMode.CONFIGURE) {
			nFile.getActivity().closeActivity();
			nFile.setActivityMode(ActivityMode.SELECT);
			this.getMapNodes().forEach((k,nnode) -> nnode.clear());		
			nFile.clear();
			getNapp().getUpperPane().getSearchTextField().clear();
			nFile.getSidePane().deactivate();			
			getNapp().getBottomBar().getSumLabel().clear();
			getNapp().getBottomBar().getCountLabel().clear();
			getNapp().getBottomBar().getRowsCount().clear();
		}
	}
	
	public void saveNnodeCoordinates(){
		this.getMapNodes().forEach((k,v) -> v.saveLayoutLocation());
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	
	public ArrayList<NTable> getSortedTablesList(){
		ArrayList<NTable> sortedTables = new ArrayList<NTable>(getNapp().getDBManager().getActiveConnection().getXMLBase().getXTables().filtered(t -> t.getSchema().equals(schemaName)));
		Collections.sort(sortedTables, new Comparator<NTable>() {
		    @Override
		    public int compare(NTable colA, NTable colB){
		    	return  colA.getTable().compareTo(colB.getTable()); 
		    } 
		});
		return sortedTables;
	}
	
	public boolean contains(javafx.scene.Node node) {
		return schemaPane.getChildren().contains(node);
	}
	
	public void add(javafx.scene.Node node) {
		schemaPane.getChildren().add(node);
	}
	
	public void remove(javafx.scene.Node node) { 
		schemaPane.getChildren().remove(node);
	}

	public HashMap<String, Nnode> getMapNodes() {
		return mapNodes;
	}
	
	public TreeSet<String> getTablesList(){
		return new TreeSet<String>(mapNodes.keySet());
	}

	//SAVE •••••••••••••••••••••••••••••••••••
	public void save(Document document, Element schemasE) {
		Element schemaE = document.createElement("schema");
        schemaE.setAttribute("schemaName", this.getSchemaName());
        schemasE.appendChild(schemaE);
        this.getMapNodes().forEach((k, nnode) -> nnode.saveXml(document, schemaE));
	}
	
	public void loopA(OpenContext context, Node xmap) {
		XML.children(xmap).forEach(n ->{
			if(n.getNodeName().equals("nnode")) {
				Nnode nnode = this.getNnode(XML.atr(n, "tableName"));
				if(nnode != null && nnode.isSelectable()) {
					nnode.loopA(context, n);
				} else {
					this.getNFile().getMessages().add(new Message(this.getNFile(), "table is not in database or no access to: ", "Nnode: " + XML.atr(n, "tableName")));
				}
			}
		});						
	}
	
	public void loopB(OpenContext context, Node xmap) {
		XML.children(xmap).forEach(n ->{
			if(n.getNodeName().equals("nnode")) {
				XML.children(n).forEach(nn ->{
					if(nn.getNodeName().equals("layer")) {
						//MOVE TO NNODE
						LAY lay = context.getAliaseLAYs().get(XML.atr(nn, "aliase"));
						if(lay != null) { //Should be null only when db was modefied
							lay.loopB(context, nn);
							lay.refreshFilterIndicator();
							if((lay.isRoot()) && lay.getSelectedFields().size() > 0) {
								lay.getIndicators().fieldsOn();
							}
							lay.getChildJoins().forEach(line -> {
								if(line.getToLay().getNnode().getSchema().equals(lay.getNnode().getSchema())) {
									int index = schemaPane.getChildren().indexOf(line.getToLay().getPane());
									int index2 = schemaPane.getChildren().indexOf(lay.getPane());
									schemaPane.getChildren().add(Math.min(index, index2) -1, line.getCubicCurve());
								}
							});
							
							if(XML.atr(nn, "sqlType").equals(SqlType.SQLD.toString())) {
								LAY parent = context.getAliaseLAYs().get(XML.atr(nn, "parentLay"));
//								//DLine-------
								JoinLine joinLine = new JoinLine(lay, parent, JoinType.DLINE);
								((DLayer)lay).setJoinLine(joinLine);
								int index = lay.getNnode().getNmap().schemaPane.getChildren().indexOf(lay.getPane());
								int index2 = lay.getNnode().getNmap().schemaPane.getChildren().indexOf(parent.getPane());
								lay.getNnode().getNmap().schemaPane.getChildren().add(Math.min(index, index2) -1, joinLine.getCubicCurve());
							}
						}else {
							this.getNFile().getMessages().add(new Message(this.getNFile(), "missing", "LAY: " + XML.atr(nn, "aliase")));
						}
					}
				});
			}
		});
		
		//after B
		this.mapNodes.forEach((nm,nd)-> {
			nd.getLayers().forEach(lay -> {
				if (lay.getPopulation().getValue() == Population.POPULATED) {
					lay.getSheet().createColumns();
					lay.getSheet().makeAvaliableIfValid();
					lay.getSheet().refreshChart();
					nFile.getTabManager().selectTab(lay.getSheet());
				}
			});
		});
		nFile.getSidePane().deactivate();			
	}

	public NFile getNFile() {
		return nFile;
	}

	public void setCompactView(boolean b) {
		this.mapNodes.forEach((nm, nd)-> {
			nd.setCompactView(b);
		});
	}

//	public void rearageNnodes() {
////        ArrayList<Nnode> nodes = new ArrayList<Nnode>(mapNodes.values());
////	        int numNodes = nodes.size();
////
////	        // Set up initial node positions randomly
////	        Random random = new Random();
////	        for (Nnode node : nodes) {
////	            double x = random.nextDouble() * PANE_WIDTH;
////	            double y = random.nextDouble() * PANE_HEIGHT;
////	            node.setPosition(new Point2D(x, y));
////	        }
////
////	        // Perform simple grid-based layout
////	        int numColumns = (int) Math.ceil(Math.sqrt(numNodes));
////	        int numRows = (int) Math.ceil((double) numNodes / numColumns);
////	        double columnWidth = PANE_WIDTH / numColumns;
////	        double rowHeight = PANE_HEIGHT / numRows;
//////
////	        for (int i = 0; i < numNodes; i++) {
////	        	Nnode node = nodes.get(i);
////	            int row = i / numColumns;
////	            int column = i % numColumns;
////	            double x = column * columnWidth + columnWidth / 2;
////	            double y = row * rowHeight + rowHeight / 2;
////	            node.setPosition(new Point2D(x, y));
////	        }
////	        
////	        mapNodes.values().forEach(mn ->{
////	        	mn.setLayoutX(mn.getPosition().getX());
////	        	mn.setLayoutY(mn.getPosition().getY());
////	        	mn.updateRootLines();
////	        });
//	}	
	
	
	  public void rearageNnodes() {
		ArrayList<Nnode> nodes = new ArrayList<Nnode>(mapNodes.values());
      
      	Random random = new Random();
	      for (Nnode node : nodes) {
	    	  
	    	  double x = random.nextDouble() * PANE_WIDTH;
	    	  double y = random.nextDouble() * PANE_HEIGHT;
	    	  node.setLayoutX(x);
	    	  node.setLayoutY(y);
	    	  node.setPosition(new Point2D(x, y));
	//    	  node.getPosition().getX()
	    	  
	    	  
	
	      }
      
	        // Calculate forces between nodes
	        for (Nnode node : nodes) {
	            for (Nnode otherNode : nodes) {
	                if (node != otherNode) {
	                    applyRepulsiveForce(node, otherNode);
	                    applyAttractiveForce(node, otherNode);
	                }
	            }
	        }

	        // Update node positions based on forces
	        for (Nnode node : nodes) {
	            updateNodePosition(node);
	        }

	        mapNodes.values().forEach(mn ->{
		    	mn.setLayoutX(mn.getPosition().getX());
		    	mn.setLayoutY(mn.getPosition().getY());
		    	mn.updateRootLines();
	        });
	        // Redraw the graph
//	        drawGraph();
	    }

	    private void applyRepulsiveForce(Nnode node, Nnode otherNode) {
	        double dx = otherNode.getPosition().getX() - node.getPosition().getX();
	        double dy = otherNode.getPosition().getY() - node.getPosition().getY();
	        double distance = Math.sqrt(dx * dx + dy * dy);

	        if (distance > 0) {
	            double force = REPULSIVE_FORCE_FACTOR / (distance * distance);
	            node.setForceX(node.getForceX() - force * dx / distance);
	            node.setForceY(node.getForceY() - force * dy / distance);
	        }
	    }

	    private void applyAttractiveForce(Nnode node, Nnode otherNode) {
	        double dx = otherNode.getPosition().getX() - node.getPosition().getX();
	        double dy = otherNode.getPosition().getY() - node.getPosition().getY();
	        double distance = Math.sqrt(dx * dx + dy * dy);

	        if (distance > 0) {
	            double force = ATTRACTIVE_FORCE_FACTOR * distance;
	            node.setForceX(node.getForceX() + force * dx / distance);
	            node.setForceY(node.getForceY() + force * dy / distance);
	        }
	    }

	    private void updateNodePosition(Nnode node) {
	        double velocityX = node.getForceX();
	        double velocityY = node.getForceY();
	        double damping = 0.2; // Damping factor to prevent overshooting

	        double newX = node.getPosition().getX() + velocityX;
	        double newY = node.getPosition().getY() + velocityY;

	        // Apply damping
	        velocityX *= damping;
	        velocityY *= damping;

	        node.setPosition(new Point2D(newX, newY));
	        node.setForceX(0.0);
	        node.setForceY(0.0);
	    }

		/**
		 * @return the scrollPane
		 */
		public ScrollPane getScrollPane() {
			return scrollPane;
		}

		/**
		 * @return the schemaPane
		 */
		public Pane getSchemaPane() {
			return schemaPane;
		}

		/**
		 * @return the napp
		 */
		public iCalc getNapp() {
			return napp;
		}
}