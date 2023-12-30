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
package logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import layer.LAY;

public class Level {
	private HBox levelA = new HBox();
	private Pane levelB = new Pane();
	
	private ObservableList<Group> groups = FXCollections.observableArrayList();	
	private LAY lay;
	private Group activeGroup;
	private Group parentGroup;

	public Level(LAY lay, Group parentGroup) {
		this.lay = lay;
		this.parentGroup = parentGroup;
		levelA.setAlignment(Pos.CENTER_LEFT);
		levelA.setSpacing(1.0);		
		levelB.setStyle("-fx-background-color: null");
		levelB.setPickOnBounds(false);

		
		//AutoRemoveLevel
		groups.addListener((ListChangeListener<Group>) c -> {
			if(c.getList().size() == 0 && this.parentGroup != null) {
				lay.getLogic().getChildren().remove(levelA);// visual remove entire level
				parentGroup.setChildLevel(null);
			}
			
			c.next();
			c.getAddedSubList().forEach(addGrp ->{
				levelA.getChildren().add(addGrp.getPane());
				levelB.getChildren().add(addGrp.getArc());//add to scene
				this.layoutArcs();

			});
			c.getRemoved().forEach(remGrp ->{
				levelA.getChildren().remove(remGrp.getPane());
				levelB.getChildren().remove(remGrp.getArc());//add to scene
				this.layoutArcs();
			});
		});	
	}
	
	public void show() {	
		if(!lay.getNnode().getNmap().getNapp().getMenu().getViewMenu().getSimpleViewMenuItem().isSelected()) {
			if(!lay.getLogic().getChildren().contains(levelA)) lay.getLogic().getChildren().add(levelA);
		}else {
			if(!lay.getNnode().getNmap().contains(levelB)) lay.getNnode().getNmap().add(levelB);
			this.layoutArcs();
		}
		if(activeGroup != null) activeGroup.show();	
	}
	
	public void hide() {
		if(lay.getLogic().getChildren().contains(levelA)) lay.getLogic().getChildren().remove(levelA);
		if(activeGroup != null) activeGroup.hide();
		if(lay.getNnode().getNmap().contains(levelB)) lay.getNnode().getNmap().remove(levelB);

	}
	
	public Group getActiveGroup() {
		return activeGroup;
	}

	public void setActiveGroup(Group activeGroup) {
		this.activeGroup = activeGroup;
	}
	
	public Group getDynamicGroup() {
		if(activeGroup != null && activeGroup.getStatus().getValue().equals("Open")) {
			return activeGroup;
		}else if(activeGroup == null || activeGroup.getStatus().getValue().equals("Closed")) {
			Group group = new Group(this);//NEW GROUP
			activeGroup = group;
			groups.add(group);
			return group;
		}else if(activeGroup != null && activeGroup.getStatus().getValue().equals("Down")) {
			
			if(activeGroup.getChild() == null) {
				Level level = new Level(lay, activeGroup);
				activeGroup.setChildLevel(level);//NEW LEVEL
				level.show();
			}
			return activeGroup.getChild().getDynamicGroup();
		}
		return null;
	}

	
	public List<Group> getGroups() {
		return groups;
	}
	
	public Group getParentGroup() {
		return parentGroup;
	}

	//SQL MOVE TO LOGIC???
	public SQL buildSQL(SQL sql) {
		if (groups.size() > 1) sql.open();
		groups.forEach(group -> {
			group.buildSQL(sql);
			if ((groups.indexOf(group) + 1) < groups.size()) sql.OR();
		});
		if (groups.size() > 1) sql.close();
		return sql;
	}
	
	public List<Group> getGroupsAll() {
		List<Group> grz =  new ArrayList<Group>();
		grz.addAll(groups);
		groups.forEach(group ->  grz.addAll(group.getAllGroups()));
		return grz;
	}

	public HashSet<SearchCON> getLogicConditions(HashSet<SearchCON> searchConditions) {
		groups.forEach(group -> group.getLogicConditions(searchConditions));
		return searchConditions;
	}
	
	public LAY getLay() {
		return lay;
	}

	public void saveLevel(Document document, Element logic) {
		Element level = document.createElement("level");
		logic.appendChild(level);
		groups.forEach(group -> {
			group.saveGroup(document, level);
		});		
	}
	
	//ARCS ••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
	private int radius() {
		if(parentGroup == null) {
			return 17;
		}else {
			return parentGroup.getLevel().radius() + 10;
		}
	}
	
	private int radiusdiff() {
		if(parentGroup == null) {
			return 2;
		}else {
			return parentGroup.getLevel().radiusdiff() + 2;
		}
	}
	
	
	public void layoutArcs() {
		double anglePerSlice = 360.0 / groups.size();
		
		groups.forEach(sl ->{
			double startAngle = groups.indexOf(sl) * anglePerSlice - anglePerSlice / 2;
			double length = (anglePerSlice * ((groups.size() == 1)? 1 : 0.8)) - ((groups.size() == 1)? 0 : (radiusdiff() * groups.size()))  ; // (0.8 / groups.size())); // Length of each slice (90% of anglePerSlice)
			sl.getArc().setStartAngle(startAngle);
			sl.getArc().setLength(length );
			
			sl.getArc().setRadiusX(radius());
			sl.getArc().setRadiusY(radius());
			
			sl.getArc().setCenterX(lay.getCenterX());
			sl.getArc().setCenterY(lay.getCenterY());
		});
	}

	public Pane getLevelPane() {
		return levelB;
	}
}