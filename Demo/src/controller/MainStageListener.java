package controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import JNFP.ComplexPolygonStage;
import JNFP.Minkowski;
import JNFP.MultiPolygon;
import JNFP.NoFitPolygon;
import JNFP.NoFitPolygonStages;
import JNFP.Orbiting;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import view.MainApp;

public class MainStageListener implements Initializable {

	@FXML
	private SplitPane mainSplitPane;
	@FXML
	private Button generateButton;

	@FXML
	private Button clearButton;

	@FXML
	private Button startOrbButton;
	@FXML
	private Button startMinkButton;
	@FXML
	private Button prevOrbButton;
	@FXML
	private Button prevMinkButton;
	@FXML
	private Button nextOrbButton;
	@FXML
	private Button nextMinkButton;
	@FXML
	private Button endOrbButton;
	@FXML
	private Button endMinkButton;

	@FXML
	private AnchorPane poly1Anchor;
	@FXML
	private AnchorPane poly2Anchor;

	@FXML
	private TextArea orbText;
	@FXML
	private TextArea minkText;

	Group root1;
	Group root2;

	List<Group> groupOrbList;
	List<Group> groupMinkList;

	int orbGroup = -1;
	int minkGroup = -1;

	List<Double> xCoordList1 = new ArrayList<>();
	List<Double> yCoordList1 = new ArrayList<>();
	int count1 = 0;
	boolean drawShape1 = true;
	Polygon polygon1;
	List<Double> values1 = new ArrayList<Double>();

	List<Double> xCoordList2 = new ArrayList<>();
	List<Double> yCoordList2 = new ArrayList<>();
	int count2 = 0;
	boolean drawShape2 = true;
	Polygon polygon2;
	List<Double> values2 = new ArrayList<Double>();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		root1 = new Group();
		poly1Anchor.setOnMouseDragged(mouseHandler);
		poly1Anchor.setOnMousePressed(mouseHandler);
		poly1Anchor.setOnMouseReleased(mouseHandler);

		poly1Anchor.getChildren().add(root1);

		root2 = new Group();
		poly2Anchor.setOnMouseDragged(mouseHandler2);
		poly2Anchor.setOnMousePressed(mouseHandler2);
		poly2Anchor.setOnMouseReleased(mouseHandler2);

		poly2Anchor.getChildren().add(root2);

		startOrbButton.setDisable(true);
		startMinkButton.setDisable(true);
		prevOrbButton.setDisable(true);
		prevMinkButton.setDisable(true);
		nextOrbButton.setDisable(true);
		nextMinkButton.setDisable(true);
		endOrbButton.setDisable(true);
		endMinkButton.setDisable(true);
	}

	public void onGenerateClickEvent() {
		generateButton.setText("Generating...");
		System.out.println("generating polygons");

		if (xCoordList1.size() > 2 || xCoordList2.size() > 2) {
			MultiPolygon polyA = new MultiPolygon(xCoordList1, yCoordList1);

			MultiPolygon polyB = new MultiPolygon(xCoordList2, yCoordList2);

			
			NoFitPolygonStages.sceneSizeX = poly1Anchor.getWidth();
			NoFitPolygonStages.sceneSizeY = poly1Anchor.getHeight();
			
			ComplexPolygonStage.sceneSizeX = poly1Anchor.getWidth();
			ComplexPolygonStage.sceneSizeY = poly1Anchor.getHeight();
			
			Orbiting.adjustRound(1);
			long startTime = System.currentTimeMillis();
			NoFitPolygon nfpOrb = Orbiting.generateNFP(new MultiPolygon(polyA), new MultiPolygon(polyB));
			long endTime = System.currentTimeMillis();

			long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			long duration = endTime - startTime;
			String orbInfo = "duration: " + duration + "ms\n";
			orbText.setText(orbInfo);
			if(Orbiting.fault){
				orbText.appendText("error caused by rounding mistake");
			}

			NoFitPolygonStages.drawAllNFP();
			groupOrbList = new ArrayList<>();
			for(Group g: NoFitPolygonStages.nfpGroupList){
				groupOrbList.add(g);
			}
			
			NoFitPolygonStages.nfpGroupList = new ArrayList<>();
			NoFitPolygonStages.nfpToDraw = new ArrayList<>();
			NoFitPolygonStages.aantalNFPStages = 0;
			
			root1.getChildren().clear();
			root1.getChildren().add(groupOrbList.get(groupOrbList.size()-1));

			startTime = System.currentTimeMillis();
			NoFitPolygon nfpMink = Minkowski.generateMinkowskiNFP(polyA, polyB);
			endTime = System.currentTimeMillis();

			
			endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			duration = endTime - startTime;
			String minkInfo = "duration: " + duration + "ms\n";
			minkText.setText(minkInfo);
			if(Minkowski.biggestCycle<2){
				minkText.appendText("error caused by rounding mistake");
			}

			ComplexPolygonStage.drawAllComplexPolygons();
			groupMinkList = new ArrayList<>();
			for(Group g: ComplexPolygonStage.complexGroupList){
				groupMinkList.add(g);
			}
			ComplexPolygonStage.complexGroupList =  new ArrayList<>();
			ComplexPolygonStage.complexPolygonToDraw = new ArrayList<>();
			ComplexPolygonStage.aantalComplexPolygonStages = 0;
			
			groupMinkList.add(NoFitPolygonStages.drawNFP(nfpMink));
			root2.getChildren().clear();
			root2.getChildren().add(groupMinkList.get(groupMinkList.size()-1));

			NoFitPolygonStages.nfpGroupList = new ArrayList<>();
			NoFitPolygonStages.nfpToDraw = new ArrayList<>();
			NoFitPolygonStages.aantalNFPStages = 0;
			
			generateButton.setText("Generate NFP");
			generateButton.setDisable(true);

			orbGroup = groupOrbList.size()-1;
			minkGroup = groupMinkList.size()-1;
			
			startOrbButton.setDisable(false);
			startMinkButton.setDisable(false);
			prevOrbButton.setDisable(false);
			prevMinkButton.setDisable(false);
		} else {
			generateButton.setText("Generate NFP");
			onClearClickEvent();
		}
	}

	public void onClearClickEvent() {

		root1.getChildren().clear();
		xCoordList1 = new ArrayList<>();
		yCoordList1 = new ArrayList<>();
		count1 = 0;
		drawShape1 = true;
		values1 = new ArrayList<Double>();

		root2.getChildren().clear();
		xCoordList2 = new ArrayList<>();
		yCoordList2 = new ArrayList<>();
		count2 = 0;
		drawShape2 = true;
		values2 = new ArrayList<Double>();

		generateButton.setDisable(false);

		startOrbButton.setDisable(true);
		startMinkButton.setDisable(true);
		prevOrbButton.setDisable(true);
		prevMinkButton.setDisable(true);
		nextOrbButton.setDisable(true);
		nextMinkButton.setDisable(true);
		endOrbButton.setDisable(true);
		endMinkButton.setDisable(true);

		orbGroup = -1;
		minkGroup = -1;
	}

	EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent mouseEvent) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				if (drawShape1) {
					xCoordList1.add(count1, mouseEvent.getX());
					yCoordList1.add(count1, mouseEvent.getY());

					System.out.println("X:" + mouseEvent.getX());
					System.out.println("Y:" + mouseEvent.getY());
				}
			} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
				if (drawShape1) {
					if (count1 > 0 && xCoordList1.get(count1) == xCoordList1.get(0)
							&& yCoordList1.get(count1) == yCoordList1.get(0)) {
						drawShape1 = false;
					} else {
						polygon1 = new Polygon();
						values1.add(xCoordList1.get(count1));
						values1.add(yCoordList1.get(count1));
						count1++;
						polygon1.getPoints().addAll(values1);

						polygon1.setStroke(Color.BLACK);
						polygon1.setStrokeWidth(4);
						polygon1.setStrokeLineCap(StrokeLineCap.ROUND);
						polygon1.setFill(Color.GRAY);
						root1.getChildren().clear();
						root1.getChildren().add(polygon1);

						if (count1 == 30) {
							drawShape1 = false;
							count1 = 0;
							// calculateAngle();
							// root.getChildren().addAll(createDegreeAngleFor(triangle.getPoints()));
						}
					}
				}
			}

		}

	};

	EventHandler<MouseEvent> mouseHandler2 = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent mouseEvent) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				if (drawShape2) {
					xCoordList2.add(count2, mouseEvent.getX());
					yCoordList2.add(count2, mouseEvent.getY());

					// System.out.println("X:" + mouseEvent.getX());
					// System.out.println("Y:" + mouseEvent.getY());
				}
			} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
				if (drawShape2) {
					if (count2 > 0 && xCoordList2.get(count2) == xCoordList2.get(0)
							&& yCoordList2.get(count2) == yCoordList2.get(0)) {
						drawShape2 = false;
					} else {
						polygon2 = new Polygon();
						values2.add(xCoordList2.get(count2));
						values2.add(yCoordList2.get(count2));
						count2++;
						polygon2.getPoints().addAll(values2);

						polygon2.setStroke(Color.BLACK);
						polygon2.setStrokeWidth(4);
						polygon2.setStrokeLineCap(StrokeLineCap.ROUND);
						polygon2.setFill(Color.DARKGRAY);
						root2.getChildren().clear();
						root2.getChildren().add(polygon2);

						if (count2 == 30) {
							drawShape2 = false;
							count2 = 0;
							// calculateAngle();
							// root.getChildren().addAll(createDegreeAngleFor(triangle.getPoints()));
						}
					}
				}
			}

		}

	};

	public void startOrbEvent() {
		orbGroup = 0;
		root1.getChildren().clear();
		root1.getChildren().add(groupOrbList.get(orbGroup));
		
		startOrbButton.setDisable(true);
		prevOrbButton.setDisable(true);
		
		nextOrbButton.setDisable(false);
		endOrbButton.setDisable(false);
	}

	public void startMinkEvent() {
		minkGroup = 0;
		root2.getChildren().clear();
		root2.getChildren().add(groupMinkList.get(minkGroup));
		
		startMinkButton.setDisable(true);
		prevMinkButton.setDisable(true);
		
		nextMinkButton.setDisable(false);
		endMinkButton.setDisable(false);
		
	}

	public void prevOrbEvent() {

		orbGroup--;
		root1.getChildren().clear();
		root1.getChildren().add(groupOrbList.get(orbGroup));
		
		nextOrbButton.setDisable(false);
		endOrbButton.setDisable(false);
		
		if(orbGroup == 0){
			prevOrbButton.setDisable(true);
			startOrbButton.setDisable(true);
		}
	}

	public void prevMinkEvent() {
		minkGroup--;
		root2.getChildren().clear();
		root2.getChildren().add(groupMinkList.get(minkGroup));
		
		nextMinkButton.setDisable(false);
		endMinkButton.setDisable(false);
		
		if(minkGroup == 0){
			prevMinkButton.setDisable(true);
			startMinkButton.setDisable(true);
		}
	}

	public void nextOrbEvent() {
		orbGroup++;
		root1.getChildren().clear();
		root1.getChildren().add(groupOrbList.get(orbGroup));
		
		prevOrbButton.setDisable(false);
		startOrbButton.setDisable(false);
		
		if(orbGroup == groupOrbList.size()-1){
			nextOrbButton.setDisable(true);
			endOrbButton.setDisable(true);
		}
	}

	public void nextMinkEvent() {
		minkGroup++;
		root2.getChildren().clear();
		root2.getChildren().add(groupMinkList.get(minkGroup));
		
		prevMinkButton.setDisable(false);
		startMinkButton.setDisable(false);
		
		if(minkGroup == groupMinkList.size()-1){
			nextMinkButton.setDisable(true);
			endMinkButton.setDisable(true);
		}
	}

	public void endOrbEvent() {

		orbGroup = groupOrbList.size()-1;
		root1.getChildren().clear();
		root1.getChildren().add(groupOrbList.get(orbGroup));
		
		startOrbButton.setDisable(false);
		prevOrbButton.setDisable(false);
		
		nextOrbButton.setDisable(true);
		endOrbButton.setDisable(true);
		
	}

	public void endMinkEvent() {
		minkGroup = groupMinkList.size()-1;
		root2.getChildren().clear();
		root2.getChildren().add(groupMinkList.get(minkGroup));
		
		startMinkButton.setDisable(false);
		prevMinkButton.setDisable(false);
		
		nextMinkButton.setDisable(true);
		endMinkButton.setDisable(true);
	}
}
