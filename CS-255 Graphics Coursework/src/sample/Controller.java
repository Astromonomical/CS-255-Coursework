package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Controller {
	private Image originalImage;

	@FXML
	private ImageView imagePort;

	@FXML
	private Slider gammaSlider;

	@FXML
	private Label gamValLbl;

	@FXML
	private Button invertBtn;

	@FXML
	private Canvas contGraph;

	private GraphEm graph;

	public void initialize() throws FileNotFoundException {
		// Load image
		Image image = new Image(new FileInputStream("raytrace.jpg"));
		imagePort.setImage(image);
		originalImage = image;

		/**
		 * Listener for the gamma slider to update as it is moved
		 */
		gammaSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double gamma = Math.round(gammaSlider.getValue() * 10) / 10.0;

				Image gamIm = GammaCorrect.correctGamma(originalImage,
					gamma);

				imagePort.setImage(gamIm);
				gamValLbl.setText("Value: " + gamma);
			}
		});

		graph = new GraphEm(contGraph);
		imagePort.setImage(graph.updateContrast(imagePort.getImage()));

		contGraph.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				graph.movePoint(new Coordinate((int) event.getX(),
					(int) event.getY()));

				imagePort.setImage(graph.updateContrast(imagePort.getImage()));
			}

		});
	}

	public void handleReset() throws FileNotFoundException {
		// Reset Slider
		gammaSlider.setValue(1.0);
		gamValLbl.setText("Value: 1.0");

		// Load image
		Image image = new Image(new FileInputStream("raytrace.jpg"));
		imagePort.setImage(image);
		originalImage = image;
	}

	public void handleInvert() {
		Image invIm = GammaCorrect.ImageInverter(imagePort.getImage());
		originalImage = invIm;
		imagePort.setImage(invIm);
	}
}