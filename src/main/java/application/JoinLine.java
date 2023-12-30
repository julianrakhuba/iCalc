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
package application;

import java.util.ArrayList;

import activity.Edit;
import generic.ACT;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import layer.LAY;
import status.JoinType;
import status.SqlType;

public class JoinLine  {
	private Path path = new Path();
    private Property<Double> x1 = new SimpleObjectProperty<Double>();
    private Property<Double> y1 = new SimpleObjectProperty<Double>();
    private Property<Double> x2 = new SimpleObjectProperty<Double>();
    private Property<Double> y2 = new SimpleObjectProperty<Double>();
    private Property<Double> cx1 = new SimpleObjectProperty<Double>();
    private Property<Double> cy1 = new SimpleObjectProperty<Double>();
    private Property<Double> cx2 = new SimpleObjectProperty<Double>();
    private Property<Double> cy2 = new SimpleObjectProperty<Double>();
    private ArrayList<MoveTo> xy1 = new ArrayList<MoveTo>();
    private ArrayList<CubicCurveTo> cubicCurveParts = new ArrayList<CubicCurveTo>();
	
	private LAY fromLay;
	private LAY toLay;
	
	private Property<JoinType> joinType = new SimpleObjectProperty<JoinType>();
	
	
	private JainLabel parentLabel;	
	private JainLabel childLabel;	

	public JoinLine(LAY startLAY, LAY endLAY, JoinType jtype) {
		this.fromLay = startLAY;
		this.toLay = endLAY;
		
		joinType.addListener((c,f,g )-> {
			this.updateLayout();
			this.updateStyle();
		});	
		
		parentLabel = new JainLabel(this, "parent");	
		childLabel = new JainLabel(this, "child");	

	    for (int i = 0; i < 4; i++) {
	    	 MoveTo mTo =  new MoveTo();
	    	 mTo.xProperty().bind(x1);
//	    	 mTo.setY(y1.getValue() + i*1.5);// yProperty will be set manualy
	    	 xy1.add(mTo);
	    	 
	    	CubicCurveTo ccTo = new CubicCurveTo();
	    	ccTo.controlX1Property().bind(cx1);
	    	ccTo.controlY1Property().bind(cy1);
	    	ccTo.controlX2Property().bind(cx2);
	    	ccTo.controlY2Property().bind(cy2);
	    	ccTo.xProperty().bind(x2);
	    	ccTo.yProperty().bind(y2);
	    	cubicCurveParts.add(ccTo);
	    	
	    	
	    	
	    	path.getElements().addAll(mTo, ccTo);    	 
	    }
		this.joinType.setValue(jtype);

	    
	}

	// ••••••••••••••••••••••••••
	public void updateLayout() {
		x1.setValue(fromLay.getCenterX());
		y1.setValue(fromLay.getCenterY());
		x2.setValue(toLay.getCenterX());
		y2.setValue(toLay.getCenterY());
		
		cx1.setValue(isDline()? fromLay.getCenterX() - 50 : isSelfJoin()?  fromLay.getCenterX() + 50 : (fromLay.getCenterX() + toLay.getCenterX())/2);
		cy1.setValue(isDline()? fromLay.getCenterY() + 10: isSelfJoin()? fromLay.getCenterY() - 10 : fromLay.getCenterY());
		cx2.setValue(isDline()? toLay.getCenterX() - 50 : isSelfJoin()?  toLay.getCenterX() + 50 : (fromLay.getCenterX() + toLay.getCenterX())/2);
		cy2.setValue(isDline()? toLay.getCenterY() - 10 :isSelfJoin()? toLay.getCenterY() + 10: toLay.getCenterY());
		
//		cx1.setValue((isDline() || isSelfJoin())? fromLay.getCenterX() - 50 : (fromLay.getCenterX() + toLay.getCenterX())/2);
//		cy1.setValue((isDline() || isSelfJoin())? fromLay.getCenterY() + 10 : fromLay.getCenterY());
//		cx2.setValue((isDline() || isSelfJoin())? toLay.getCenterX() - 50 : (fromLay.getCenterX() + toLay.getCenterX())/2);
//		cy2.setValue((isDline() || isSelfJoin())? toLay.getCenterY() - 10 : toLay.getCenterY());

		
		//FLAT LINE
//		cx1.setValue(fromLay.getCenterX());
//		cy1.setValue(fromLay.getCenterY());
//		cx2.setValue(toLay.getCenterX());
//		cy2.setValue(toLay.getCenterY());
		xy1.forEach(moto -> moto.setY(y1.getValue() + ((xy1.indexOf(moto) * 1) - 1.5)));// manual update of multiline
	}
	
	private boolean isDline() {
		return fromLay.getNnode() == toLay.getNnode() && joinType.getValue() == JoinType.DLINE;
	}
	
	private boolean isSelfJoin() {
		return fromLay.getNnode() == toLay.getNnode();
	}

	public ArrayList<KeyFrame> yPropertyAnimated(LAY layer, double lineToY, Duration dur) {
		ArrayList<KeyFrame> kfs = new ArrayList<KeyFrame>();
		if (layer == fromLay) {
			xy1.forEach(moto ->{
				KeyFrame kf = new KeyFrame(dur,new KeyValue(moto.yProperty(), lineToY  + ((xy1.indexOf(moto) * 1) - 1.5), Interpolator.EASE_BOTH));
				kfs.add(kf);
			});
		}else {
			KeyFrame kf = new KeyFrame(dur,new KeyValue(y2, lineToY, Interpolator.EASE_BOTH));
			kfs.add(kf);
		}
		return kfs;
	}
	
	public KeyFrame yControl(LAY layer, double lineToY, Duration dur) {
		if (layer == fromLay) {
			return new KeyFrame(dur,new KeyValue(cy1, lineToY, Interpolator.EASE_BOTH));
		}else {
			return new KeyFrame(dur,new KeyValue(cy2, lineToY, Interpolator.EASE_BOTH));
		}
	}
	
	public void createGredient(String fromColor) {
		String gredient;
		if(this.isSelfJoin()) {
			gredient = fromColor;
		}else {
			gredient = "linear-gradient(from " + x1.getValue().intValue() +"px " + y1.getValue().intValue() +"px to "+ x2.getValue().intValue()+ "px " + y2.getValue().intValue()+ "px, "+ fromColor + " 0%, #fff 90%)";		
		}		
//		if(fromLay.nnode.nmap.napp.getStage().getStyle() == StageStyle.TRANSPARENT) {
//			path.setStyle("-fx-stroke:"+ gredient+";" + "-fx-fill: transparent;  -fx-effect: dropshadow(gaussian, derive(#1E90FF, 70%) , 4, 0.2, 0.0, 0.0);  -fx-stroke-width: 1.5; -fx-stroke-line-cap: butt;");
//		}else {
		path.setStyle("-fx-stroke:"+ gredient+";" + "-fx-fill: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 3, 0.1, 0, 2);  -fx-stroke-width: 1.5; -fx-stroke-line-cap: butt;");
//		}

	}

	public void updateStyle() {	
		if(this.getJoinType() == JoinType.JOIN) {
			if(this.fromLay.getSqlType() == SqlType.SQL) {//NEED WORK 
				createGredient("#99ddff");//Blue
			}else {
				createGredient("#7cbbf9");//darkblue
			}
		}else if(this.getJoinType() == JoinType.LEFT) {
			createGredient("#ebe5ff");//lavander
		}else if(this.getJoinType() == JoinType.RIGHT) {
			createGredient("#ffd6eb");//red
		}else if(this.getJoinType() == JoinType.SHIFT) {
			createGredient("#99ddff");//Blue
		}else if(joinType.getValue() == JoinType.DLINE) {
			path.getStyleClass().add("dotedDLine");
		}
		getParentLabel().styleAsParent();
		getChildLabel().styleAsChild();

	}
	
	public void setJoinType(JoinType joinType) {
		this.joinType.setValue(joinType);
	}
	
	public JoinType getJoinType() {
		return joinType.getValue();
	}
	

	public LAY getFromLay() {
		return fromLay;
	}

	public LAY getToLay() {
		return toLay;
	}
	
	public String toString() {
		return joinType.getValue() + " [" + fromLay + "] to [" + toLay + "] x1:" + x1.getValue().intValue()  + " y1:" + y1.getValue().intValue()  + " x2:" + x2.getValue().intValue()  + " y2:" + y2.getValue().intValue() + " dl:" + this.isDline() +" slf:"+ this.isSelfJoin() ;
	}

	public Node getCubicCurve() {
		return path;
	}
	
	public void joinClick(MouseEvent e) {
		ACT act = this.getFromLay().getNnode().getNmap().getNFile().getActivity();
		if(act instanceof Edit && e.isControlDown()				
				) {
			((Edit)act).disconnectJoin(this);				
		}else if(act instanceof Edit && this.getFromLay().getSqlType() != SqlType.SQL && this.getToLay().getSqlType() != SqlType.SQL) {
			if (this.getJoinType() == JoinType.JOIN) {
				this.setJoinType(JoinType.LEFT);
			} else if (this.getJoinType() == JoinType.LEFT) {
				this.setJoinType(JoinType.RIGHT);
			} else if (this.getJoinType() == JoinType.RIGHT) {
				this.setJoinType(JoinType.JOIN);
			}
		}		
	}

	/**
	 * @return the childLabel
	 */
	public JainLabel getChildLabel() {
		return childLabel;
	}

	/**
	 * @return the parentLabel
	 */
	public JainLabel getParentLabel() {
		return parentLabel;
	}
}