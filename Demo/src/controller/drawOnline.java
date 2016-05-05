package controller;

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
import javafx.scene.Group;
import javafx.scene.Scene;
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

/**
 *
 * @author jay.thakkar
 */
public class drawOnline extends Application {

	Group root;

	@Override

	public void start(Stage primaryStage) {

		AnchorPane anchorPane = new AnchorPane();
		root = new Group();
		anchorPane.setOnMouseDragged(mouseHandler);
		anchorPane.setOnMousePressed(mouseHandler);
		anchorPane.setOnMouseReleased(mouseHandler);

		// root.getChildren().add(triangle);

		// root.getChildren().addAll(createControlAnchorsFor(triangle.getPoints()));

		anchorPane.getChildren().add(root);
		primaryStage.setTitle("Hello World!");
		primaryStage.setScene(new Scene(anchorPane, 400, 400, Color.ALICEBLUE));
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

	double x[] = new double[3];
	double y[] = new double[3];
	int count = 0;
	boolean drawShape = true;
	Polygon triangle;
	List<Double> values = new ArrayList<Double>();
	EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent mouseEvent) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
				if (drawShape) {
					x[count] = mouseEvent.getX();
					y[count] = mouseEvent.getY();
					System.out.println("X:" + mouseEvent.getX());
					System.out.println("Y:" + mouseEvent.getY());
				}
			} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
				if (drawShape) {
					triangle = new Polygon();
					values.add(x[count]);
					values.add(y[count]);
					count++;
					triangle.getPoints().addAll(values);

					// triangle.getPoints().setAll(

					// x[0], y[0],

					// x[1], y[1],

					// x[2], y[2]);

					triangle.setStroke(Color.FORESTGREEN);
					triangle.setStrokeWidth(4);
					triangle.setStrokeLineCap(StrokeLineCap.ROUND);
					triangle.setFill(Color.CORNSILK.deriveColor(0, 1.2, 1, 0.6));
					root.getChildren().clear();
					root.getChildren().add(triangle);
					root.getChildren().addAll(createControlAnchorsFor(triangle.getPoints()));

					if (count == 3) {
						drawShape = false;
						count = 0;
						calculateAngle();
						// root.getChildren().addAll(createDegreeAngleFor(triangle.getPoints()));
					}

				}

			}

		}

	};

	double getLineDistance(double x1, double y1, double x2, double y2) {

		double s1 = Math.pow((x2 - x1), 2);
		double s2 = Math.pow((y2 - y1), 2);
		double s3 = Math.sqrt(s1 + s2);
		return s3;
	}

	int angels[] = new int[3];

	public void calculateAngle() {

		double x1 = triangle.getPoints().get(0);
		double y1 = triangle.getPoints().get(1);
		double x2 = triangle.getPoints().get(2);
		double y2 = triangle.getPoints().get(3);
		double x3 = triangle.getPoints().get(4);
		double y3 = triangle.getPoints().get(5);

		double a = getLineDistance(x1, y1, x2, y2);
		double b = getLineDistance(x2, y2, x3, y3);
		double c = getLineDistance(x3, y3, x1, y1);

		double pi = Math.PI;
		double angleA = Math.acos((b * b + c * c - a * a) / (2.0 * b * c)) * (180.0 / pi);
		double angleB = Math.acos((a * a + c * c - b * b) / (2.0 * a * c)) * (180.0 / pi);
		double angleC = (180.0 - angleA - angleB);

		System.out.println("=================================");
		System.out.println("angleA:" + angleA);
		System.out.println("angleB:" + angleB);
		System.out.println("angleC:" + angleC);

		angels[0] = (int) Math.round(angleA);
		angels[2] = (int) Math.round(angleB);
		angels[1] = (int) Math.round(angleC);

	}

	// a draggable anchor displayed around a point.

	class Anchor extends Circle {

		Anchor(Color color, DoubleProperty x, DoubleProperty y) {

			super(x.get(), y.get(), 10);
			setFill(color.deriveColor(1, 1, 1, 0.5));
			setStroke(color);
			setStrokeWidth(2);
			setStrokeType(StrokeType.OUTSIDE);
			x.bind(centerXProperty());
			y.bind(centerYProperty());
			enableDrag();
		}

		// make a node movable by dragging it around with the mouse.

		private void enableDrag() {

			final Delta dragDelta = new Delta();

			setOnMousePressed(new EventHandler<MouseEvent>() {

				@Override

				public void handle(MouseEvent mouseEvent) {
					// record a delta distance for the drag and drop operation.
					dragDelta.x = getCenterX() - mouseEvent.getX();
					dragDelta.y = getCenterY() - mouseEvent.getY();
					getScene().setCursor(javafx.scene.Cursor.MOVE);

				}

			});

			setOnMouseReleased(new EventHandler<MouseEvent>() {

				@Override

				public void handle(MouseEvent mouseEvent) {

					getScene().setCursor(javafx.scene.Cursor.HAND);

					System.out.println("values:" + values);

				}

			});

			setOnMouseDragged(new EventHandler<MouseEvent>() {

				@Override

				public void handle(MouseEvent mouseEvent) {

					double newX = mouseEvent.getX() + dragDelta.x;

					if (newX > 0 && newX < getScene().getWidth()) {

						setCenterX(newX);

					}

					double newY = mouseEvent.getY() + dragDelta.y;

					if (newY > 0 && newY < getScene().getHeight()) {

						setCenterY(newY);

					}

					calculateAngle();

				}

			});

			setOnMouseEntered(new EventHandler<MouseEvent>() {

				@Override

				public void handle(MouseEvent mouseEvent) {

					if (!mouseEvent.isPrimaryButtonDown()) {

						getScene().setCursor(javafx.scene.Cursor.HAND);

					}

				}

			});

			setOnMouseExited(new EventHandler<MouseEvent>() {

				@Override

				public void handle(MouseEvent mouseEvent) {

					if (!mouseEvent.isPrimaryButtonDown()) {

						getScene().setCursor(javafx.scene.Cursor.DEFAULT);

					}

				}

			});

		}

		// records relative x and y co-ordinates.

		private class Delta {

			double x, y;

		}

	}

	// @return a list of anchors which can be dragged around to modify points in
	// the format [x1, y1, x2, y2...]

	private ObservableList<Anchor> createControlAnchorsFor(final ObservableList<Double> points) {

		ObservableList<Anchor> anchors = FXCollections.observableArrayList();

		for (int i = 0; i < points.size(); i += 2) {

			final int idx = i;

			DoubleProperty xProperty = new SimpleDoubleProperty(points.get(i));
			DoubleProperty yProperty = new SimpleDoubleProperty(points.get(i + 1));

			xProperty.addListener(new ChangeListener<Number>() {

				@Override

				public void changed(ObservableValue<? extends Number> ov, Number oldX, Number x) {

					points.set(idx, (double) x);

				}

			});

			yProperty.addListener(new ChangeListener<Number>() {

				@Override

				public void changed(ObservableValue<? extends Number> ov, Number oldY, Number y) {

					points.set(idx + 1, (double) y);

				}

			});

			anchors.add(new Anchor(Color.GOLD, xProperty, yProperty));

		}

		return anchors;

	}

	private ObservableList<DegreeAngel> createDegreeAngleFor(final ObservableList<Double> points) {

		ObservableList<DegreeAngel> degreeAngels = FXCollections.observableArrayList();

		int j = 0;

		for (int i = 0; i < points.size(); i += 2) {

			final int idx = i;

			DoubleProperty xProperty = new SimpleDoubleProperty(points.get(i));

			DoubleProperty yProperty = new SimpleDoubleProperty(points.get(i + 1));

			xProperty.addListener(new ChangeListener<Number>() {

				@Override

				public void changed(ObservableValue<? extends Number> ov, Number oldX, Number x) {

					points.set(idx, (double) x);

				}

			});

			yProperty.addListener(new ChangeListener<Number>() {

				@Override

				public void changed(ObservableValue<? extends Number> ov, Number oldY, Number y) {

					points.set(idx + 1, (double) y);

				}

			});

			degreeAngels.add(new DegreeAngel(Color.BLACK, xProperty, yProperty, angels[j]));

			j++;

		}

		return degreeAngels;

	}

	class DegreeAngel extends Arc {

		public DegreeAngel(Color color, DoubleProperty x, DoubleProperty y, int angle) {
			super(x.get(), y.get(), 25.0f, 25.0f, 0, angle);
			setFill(color.deriveColor(1, 1, 1, 0.5));
			setStroke(color);
			setStrokeWidth(1);
			setStrokeType(StrokeType.OUTSIDE);
			setType(ArcType.ROUND);

			x.bind(centerXProperty());
			y.bind(centerYProperty());

		}

	}

}