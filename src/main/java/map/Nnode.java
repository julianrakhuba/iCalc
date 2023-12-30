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
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import activity.Configure;
import application.JoinLine;
import application.NCircle;
import application.NnodeLine;
import application.XML;
import clientcomponents.NColumn;
import clientcomponents.NTable;
import file.OpenContext;
import generic.ACT;
import generic.DAO;
import generic.OpenDAO;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import layer.DLayer;
import layer.LAY;
import layer.SLayer;
import logic.SQL;
import sidePanel.Message;
import status.Population;
import status.SqlType;
import status.Visability;

public class Nnode extends StackPane {
	private double uzelX = 0;
	private double uzelY = 0;
	private double mouseEventX = 0;
	private double mouseEventY = 0;	
	private DAO dao;
	private OpenDAO openDAO = new OpenDAO(this);
	private NMap nmap;
	private NTable tableBO;
	private TreeSet<String> columnsList;
	private ArrayList<NColumn> columns;
	private HashMap<String, ArrayList<String>> distinctSearchList;
	private Text text = new Text();
	private Timeline timeline = new Timeline();
	
	private HashMap<Nnode, NnodeLine> rootLines = new HashMap<Nnode, NnodeLine>();
	private ArrayList<LAY> layers = new ArrayList<LAY>();
	
	private NCircle blueNeon;
	private NCircle greenNeon;
	
	//GraphLayout
    private Point2D position;
    private List<Node> connectedNodes;
    private double forceX;
    private double forceY;
    private boolean separated = false;
    
	public Nnode(NMap nmap, DAO dao, NTable tableBO) {
		this.tableBO = tableBO;
		this.nmap = nmap;
		this.dao = dao;
		this.setLayoutX(tableBO.getX().doubleValue());
		this.setLayoutY(tableBO.getY().doubleValue());
		
		position = new Point2D(tableBO.getX().doubleValue(), tableBO.getY().doubleValue());
        connectedNodes = new ArrayList<>();
				
		blueNeon = new NCircle(this, "#1E90FF", 22);
		greenNeon = new NCircle(this, "#2AFF77", 22);

		//NNODE MOVEMENT
		this.setOnMousePressed(e -> {
			ACT act = nmap.getNFile().getActivity();
			
			if (act instanceof Configure
					) {
				mouseEventX = e.getSceneX();
				mouseEventY = e.getSceneY();
				uzelX = getLayoutX();
				uzelY = getLayoutY();
			}
		});

		this.setOnMouseDragged(e -> {
			ACT act = nmap.getNFile().getActivity();
			if (act instanceof Configure
					) {
				double offsetX = e.getSceneX() - mouseEventX;
				double offsetY = e.getSceneY() - mouseEventY;
				uzelX += offsetX;
				uzelY += offsetY;
				setLayoutX(uzelX);
				setLayoutY(uzelY);
				mouseEventX = e.getSceneX();
				mouseEventY = e.getSceneY();
				rootLines.forEach((k, line) ->  line.updateLayout());
				layers.forEach(layer -> {
					layer.getParentJoins().forEach(line -> line.updateLayout());
					layer.getChildJoins().forEach(line -> line.updateLayout());
				});
				e.consume();
			}
		});

		this.createRootLayer();		
	}
	
	public DAO getDAO() {
		return dao;
	}
	
	public void updateRootLines() {
		rootLines.forEach((k, line) ->  line.updateLayout());
	}
	
	public void overlapLayers() {
		if(this.getNmap().getNapp().getMenu().getViewMenu().getAutoFoldMenuItem().isSelected()) {
			if(!getNmap().getNapp().getNscene().getHoldKeys().contains("SHIFT")){//MOVE THIS MORE OUT????
				if(layers.size()>1) {
					separated = false;
					this.moveLayers(Duration.millis(500), Duration.millis(800), false);
				}
			}
		}
	}
	
	public void separateLayers() {
		if(!getNmap().getNapp().getNscene().getHoldKeys().contains("SHIFT")){
			if(layers.size()>1 && !separated) {
				separated = true;
				this.moveLayers(Duration.millis(100), Duration.millis(100), true);
			}
		}
	}
	
	//Must be done after node creation
	public String getNameLabel() {
		return tableBO.getTable().toLowerCase();
	}	

	
	public void createRootLayer() {
		text.setText(this.getNameLabel());
		text.setStyle(" -fx-font: 9px Verdana;");
		text.setFill(Color.rgb(100,100,100));
				
		this.setPrefWidth(20);
		this.setPrefHeight(20);
		this.setOnMouseEntered(e -> {
			this.getNmap().getNFile().getCenterMessage().setMessage(this, this.getNameLabel());
		});
		
		this.setOnMouseExited(e -> {
			this.getNmap().getNFile().getCenterMessage().setMessage(null, null);
		});
		
		this.setCompactView(getNmap().getNapp().getMenu().getViewMenu().getSimpleViewMenuItem().isSelected());
		this.styleGray();
		this.setOnMouseClicked(e -> {
			if(e.getButton().equals(MouseButton.PRIMARY)) {
				getNmap().getNFile().getActivity().passNnode(this, e);
			}
			e.consume();
		});
		
		this.sceneProperty().addListener((obs, oldScene, newScene) -> {
			 if (newScene != null) {
			 	Platform.runLater(() -> {
				this.updateRootLines();
					layers.forEach(layer -> {
						layer.getChildJoins().forEach(line -> {
							line.updateLayout();
							line.updateStyle();
						});
						if(layer instanceof DLayer) {
							((DLayer)layer).getJoinLine().updateLayout();
							((DLayer)layer).getJoinLine().updateStyle();
						}
					});
			 	});
		    }
		});
	}
	
	public boolean isSchemaJoin() {
		return
		(getNmap().getNapp().getDBManager().getActiveConnection().getXMLBase().getKeys().filtered(k -> 
		k.getConst().equalsIgnoreCase("FOREIGN KEY") &&		
		k.getSchema().equalsIgnoreCase(this.getSchema()) &&
		k.getTable().equalsIgnoreCase(this.getTable()) && !this.getSchema().equalsIgnoreCase(k.getRSchema())
		).size() + 
		getNmap().getNapp().getDBManager().getActiveConnection().getXMLBase().getKeys().filtered(k -> 
		k.getConst().equalsIgnoreCase("FOREIGN KEY") &&		
		k.getRSchema().equalsIgnoreCase(this.getSchema()) &&
		k.getRTable().equalsIgnoreCase(this.getTable())	&& !this.getSchema().equalsIgnoreCase(k.getSchema())	
		).size()) >0;
	}
	

	
	public void addRootLines() {
		getNmap().getNapp().getDBManager().getActiveConnection().getXMLBase().getKeys().filtered(k -> 
//		k.getRSchema() != null && // ref not null
		k.getConst().equals("FOREIGN KEY")	&&	
		k.getSchema().equals(this.getSchema()) //schema
		&& k.getTable().equals(this.getTable()) //table
		&& k.getRSchema().equals(this.getSchema())// reff is local schema
		).forEach(key -> {
			Nnode nnode =  getNmap().getNnode(key.getRTable());
			if(!rootLines.containsKey(nnode) && !nnode.rootLines.containsKey(this)) {//this will add only one root line per node
				NnodeLine line = new NnodeLine(this, nnode);
				this.rootLines.put(nnode, line);
				nnode.rootLines.put(this, line);
				getNmap().add(line);
			}
		});
	}
    
	public ArrayList<LAY> getLayers() {
		return layers;
	}
	
	public void saveLayoutLocation() {
		tableBO.setX(this.getLayoutX());
		tableBO.setY(this.getLayoutY());
	}
	
	public String getSchema() {
		return tableBO.getSchema();
	}
	
	public String getTable() {
		return tableBO.getTable();
	}
	
	public String getTableNameWUnderScr() {
		return tableBO.getTable().replaceAll(" ", "_") ;
	}
	
	public String getSchemaNameWUnderScr() {
		return tableBO.getSchema().replaceAll(" ", "_") ;
	}

	public String getFullNameWithOptionalQuotes() {
		return qt(tableBO.getSchema()) + "." + qt(tableBO.getTable());
	}
	
	private String qt(String str) {
		return str.contains(" ")? "`" + str + "`": str;
	}
	
	public HashMap<Nnode, NnodeLine> getRootLines() {
		return rootLines;
	}
	
	public void clear() {
		layers.forEach(layer -> layer.clear());
		layers.clear();
	}
	
//	public void relocateDirectConnectionsAround() {
//		
//		Nnode[] cons = (Nnode[]) foreignKeyNodes.toArray();
//		
//		//Nnode[] cons = foreignKeyNodes.values().toArray(new Nnode[foreignKeyNodes.values().size()]);
//
//		double slice = 2 * Math.PI / cons.length;
//		double radius = 200;
//		for (int i = 0; i < cons.length; i++) {
//			double angle = slice * i;
//			Nnode connectedGnode = cons[i];
//			connectedGnode.setLayoutX(this.propertyX.get() + radius * Math.cos(angle));
//			connectedGnode.setLayoutY(this.propertyY.get() + radius * Math.sin(angle));
//		}
//	}
//	public void relocateDirectConnectionsAround(){
//		Nnode[] cons = connections.values().toArray(new Nnode[connections.values().size()]);
//		double slice = 2 * Math.PI / cons.length;
//		double radius = 200;
//		for (int i = 0; i < cons.length; i++) {
//			double angle = slice * i;
//			Nnode connectedGnode = cons[i];
//			// connectedUzel.moveSlowlyToNewLocation(this.propertyX.get() + radius * Math.cos(angle), this.propertyY.get() + radius * Math.sin(angle));
//			connectedGnode.setLayoutX(this.propertyX.get() + radius * Math.cos(angle));
//			connectedGnode.setLayoutY(this.propertyY.get() + radius * Math.sin(angle));
//			//connectedGnode.setLayoutX(this.propertyX.get() + (i * 0.5));
//			//connectedGnode.setLayoutY(this.propertyY.get() + (i * 0.5));
//			connectedGnode.shaker.shake();
//		}
//	}
	
//	

	
	public void moveLayers(Duration durA, Duration durB, boolean expanded) {
		if(getNmap().getNapp().getMenu().getViewMenu().getAnimationMenuItem().isSelected()) {
			this.animatedMove(durA, durB, expanded);
		}else {
			layers.forEach(layer -> {
				//LAY
				layer.getPane().setLayoutY(layer.toY(expanded));
			
				//move group arcs
				layer.getRootLevel().getGroupsAll().forEach(gr ->{				
					double lineToY = layer.toY(expanded)  + (layer.getNnode().getHeight()/2);
					gr.getArc().setCenterY(lineToY);
				});
				
				//lines
				layer.getParentJoins().forEach(line -> {if(line != null) line.updateLayout();});
				layer.getChildJoins().forEach(line -> {if(line != null) line.updateLayout();});
				
				if(layer instanceof DLayer) {
					JoinLine line =  ((DLayer)layer).getJoinLine();
					line.updateLayout();
				}					
			});
		}
	}
	
	private void animatedMove(Duration durA, Duration durB, boolean expanded) {
		timeline.getKeyFrames().clear();
		layers.forEach(layer -> {
			double selfJoinFix = expanded ? 0 : 10;//fix for self join, affects all lines
			//LAY
			timeline.getKeyFrames().add(new KeyFrame(durA,new KeyValue(layer.getPane().layoutYProperty(), layer.toY(expanded), Interpolator.EASE_BOTH)));
						
		
			//move group arcs
			layer.getRootLevel().getGroupsAll().forEach(gr ->{				
				double lineToY = layer.toY(expanded)  + (layer.getNnode().getHeight()/2);
				timeline.getKeyFrames().add(new KeyFrame(durA,new KeyValue(gr.getArc().centerYProperty(), lineToY, Interpolator.EASE_BOTH)));
			});
			
			//line
			layer.getParentJoins().forEach(line -> {
				if(line != null) {
					boolean sameNnode = line.getFromLay().getNnode() == line.getToLay().getNnode();
					double lineToY = layer.toY(expanded)  + (layer.getNnode().getHeight()/2);
					timeline.getKeyFrames().addAll(line.yPropertyAnimated(layer, lineToY, durA));
					timeline.getKeyFrames().addAll(line.yControl(layer, lineToY + ((sameNnode) ? selfJoinFix : 0), durB));
				}					
			});
			
			layer.getChildJoins().forEach(line -> {
				if(line != null) {
					boolean sameNnode = line.getFromLay().getNnode() == line.getToLay().getNnode();
					double lineToY = layer.toY(expanded)  + (layer.getNnode().getHeight()/2);
					timeline.getKeyFrames().addAll(line.yPropertyAnimated(layer, lineToY, durA));							
					timeline.getKeyFrames().addAll(line.yControl(layer, lineToY - ((sameNnode) ? selfJoinFix : 0), durB));
				}
			});
			
			if(layer instanceof DLayer) {
				JoinLine line =  ((DLayer)layer).getJoinLine();
				boolean sameNnode = line.getFromLay().getNnode() == line.getToLay().getNnode();

				double lineToY = layer.toY(expanded)  + (layer.getNnode().getHeight()/2);
				timeline.getKeyFrames().addAll(line.yPropertyAnimated(layer, lineToY, durA));
				timeline.getKeyFrames().addAll(line.yControl(layer, lineToY + ((sameNnode) ? selfJoinFix : 0), durB));
				
				double lineToYz = line.getToLay().toY(expanded)  + (line.getToLay().getNnode().getHeight()/2);
				timeline.getKeyFrames().addAll(line.yPropertyAnimated(line.getToLay(), lineToYz, durA));
				timeline.getKeyFrames().addAll(line.yControl(line.getToLay(), lineToYz - ((sameNnode) ? selfJoinFix : 0), durB));						
			}					
		});
		
		timeline.setCycleCount(1);
		timeline.playFromStart();
	}
	
	public double getCenterX(){
		return this.getLayoutX() + (this.getWidth()/2);
	}
	
	public double getCenterY(){
		return this.getLayoutY() + (this.getHeight()/2);
	}
	
	public TreeSet<String> getColumnsList(){
		if (columnsList == null) {
			columnsList = new TreeSet<String>();
			getNmap().getNapp().getDBManager().getActiveConnection().getXMLBase().getXColumns().filtered(c ->  (c.getSchema().equals(tableBO.getSchema()) && c.getTable().equals(tableBO.getTable()))).forEach(col -> columnsList.add(col.getColumn()));
		}
		return columnsList;
	}
	
	public ArrayList<NColumn> getColumns(){
		if (columns == null) {
			columns = new ArrayList<NColumn>();
			getNmap().getNapp().getDBManager().getActiveConnection().getXMLBase().getXColumns().filtered(c ->  (c.getSchema().equals(tableBO.getSchema()) && c.getTable().equals(tableBO.getTable()))).forEach(col -> columns.add(col));
		}
		return columns;
	}
	
	public ArrayList<String> getStaticValuesList(String column){
		if(distinctSearchList == null) distinctSearchList = new HashMap<String, ArrayList<String>>();
		if(!distinctSearchList.containsKey(column)) {
			String nameWQuotes =  this.getFullNameWithOptionalQuotes();
			SQL sql = new SQL().SELECT().DISTINCT(nameWQuotes + "." +  column + " FROM " + nameWQuotes + " ORDER BY " + nameWQuotes + "." +  column);
			distinctSearchList.put(column, this.getOpenDAO().readDistinctValues(sql));
		}
		return distinctSearchList.get(column);
	}
	
	public void add(LAY lay) {
		layers.add(lay);
	}
	
	public void remove(LAY lay) {
		layers.remove(lay);
	} 

 	public void saveXml(Document doc, Element schema){
 		if(layers.size()>0) {
 			Element eNnode = doc.createElement("nnode");
 	 		eNnode.setAttribute("tableName", this.getTable());
 	 		schema.appendChild(eNnode);
 			layers.forEach(lay -> {
 				lay.saveXml(doc, eNnode);
 			});
 		}
 	}

	public void loopA(OpenContext context, Node xNode) {
		XML.children(xNode).forEach(n ->{
			if(n.getNodeName().equals("layer")) {
				String aliase = XML.atr(n, "aliase");
				String sqlType = XML.atr(n, "sqlType");
				String population = XML.atr(n, "population");
				
				if(sqlType.equals(SqlType.SQL.toString()) || sqlType.equals(SqlType.SQLJ.toString())) {
					SLayer lay = new SLayer(this, SqlType.valueOf(sqlType));
					lay.getPopulation().setValue(Population.valueOf(population));
					lay.updateColorMode();
					context.getAliaseLAYs().put(aliase, lay);
					lay.loopA(context, n);
					lay.addToMap();	
				}else if(sqlType.equals(SqlType.SQLD.toString())) {
					LAY parent = context.getAliaseLAYs().get(XML.atr(n, "parentLay"));
					DLayer lay = new DLayer(this, parent);
					lay.getPopulation().setValue(Population.valueOf(population));
					lay.updateColorMode();
					context.getAliaseLAYs().put(aliase, lay);
					lay.loopA(context, n);
					lay.addToMap();	
				}
			}
		});
	}

	public OpenDAO getOpenDAO() {
		return openDAO;
	}
	
	public void setCompactView(boolean b) {
		if(b) {
			this.getChildren().clear();
		}else {			
			this.getChildren().addAll(text );
		}
		
		layers.forEach(lay ->{
			lay.setCompactView(b);
		});
	}
	
	public boolean isSelectable() {
		if(tableBO.getVisability() == Visability.VISIBLE) {
			return true;
		}else {
			getNmap().getNFile().getMessages().add(new Message(getNmap().getNFile(), "Table Access", "Can't create layer for hidden table " + this.getTable()));
			return false;
		}
	}
		
	public void styleOrange() {
		this.getStyleClass().clear();
		this.getStyleClass().add("configNnode");
	}
	
	public void styleGray() {
		this.getStyleClass().clear();		
		if(this.isSchemaJoin()) {
			this.getStyleClass().add("unpopulatedNnodeWithSchemaJoins");
		}else {
			this.getStyleClass().add("unpopulatedNnode");
		}
	}

	public NCircle getBlueNeon() {
		return blueNeon;
	}

	public NCircle getGreenNeon() {
		return greenNeon;
	}
	
	//[Keep it for auto layout of nodes]
	public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public List<Node> getConnectedNodes() {
        return connectedNodes;
    }

    public double getForceX() {
        return forceX;
    }

    public void setForceX(double forceX) {
        this.forceX = forceX;
    }

    public double getForceY() {
        return forceY;
    }

    public void setForceY(double forceY) {
        this.forceY = forceY;
    }

	public NMap getNmap() {
		return nmap;
	}

}
