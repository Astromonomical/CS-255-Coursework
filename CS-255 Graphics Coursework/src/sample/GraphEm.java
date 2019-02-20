package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageReader;
import java.util.ArrayList;

public class GraphEm {
	/**
	 * This arraylist contains all points the user has made
	 * and place on the graph
	 */
	private ArrayList<Coordinate> points = new ArrayList<>();

	/**
	 * Lookup table for the contrast stretched values
	 */
	private static int[] lookupTable = new int[256];

	/**
	 * Canvas to draw on
	 */
	private Canvas canvas;

	public GraphEm(Canvas canvas) {
		this.canvas = canvas;
		drawGraph();
	}

	public void addPoint(Coordinate coords) {
		if (points.size() != 0) {
			if (coords.getX() > points.get(points.size() - 1).getX()) {
				points.add(coords);
				drawGraph();
			} else {
				System.out.println("Error");
			}
		} else {
			points.add(coords);
			drawGraph();

		}
	}

	public void removePoint(Coordinate coords) {
		Coordinate toDelete = null;

		for (Coordinate a : points) {
			if (a.getX() > coords.getX() - 10 && a.getX() < coords.getX() + 10) {
				if (a.getY() > coords.getY() - 10 && a.getY() < coords.getY() + 10) {
					toDelete = a;
					drawGraph();
				}
			}
		}

		points.remove(toDelete);
		drawGraph();

	}

	public Image updateContrast(Image image) {
		int height = (int) image.getHeight();
		int width = (int) image.getWidth();

		// New image to return
		WritableImage contrastStret = new WritableImage(width, height);

		// Open writer on new image, reader on old image
		PixelWriter contrastWriter = contrastStret.getPixelWriter();
		PixelReader imageReader = image.getPixelReader();

		fillTable();

		for (int y = 0 ; y < height ; y++) {
			for (int x = 0 ; x < width ; x++) {
				// Read pixel from the image
				Color colour = imageReader.getColor(x, y);

				// Get gamma corrected values from table
				double red = lookupTable[(int) (colour.getRed() * 255)];
				double green = lookupTable[(int) (colour.getGreen() * 255)];
				double blue = lookupTable[(int) (colour.getBlue() * 255)];

				// Create new pixel and change pixel
				colour = Color.color(red / 255.0, green / 255.0, blue / 255.0);
				contrastWriter.setColor(x, y, colour);
			}
		}

		return contrastStret;
	}

	private void drawGraph() {
		int prevX = 0;
		int prevY = 0;

		canvas.getGraphicsContext2D().clearRect(0, 0, 255, 255);
		// Draw the lines
		for (Coordinate a : points) {
			canvas.getGraphicsContext2D().strokeRect(a.getY() - 3, a.getX() - 3
				, 6,	6);
			canvas.getGraphicsContext2D().strokeLine(prevX, prevY, a.getY(),
				a.getX());
			prevX = a.getY();
			prevY = a.getX();
		}

		canvas.getGraphicsContext2D().strokeLine(prevX, prevY, 255, 255);
	}

	private void fillTable() {
		double prevX = 0;
		double prevY = 0;
		double result;

		for (Coordinate a : points) {
			double gradient = ((a.getY() - prevY) / (a.getX() - prevX));

			for (int i = (int) prevX ; i < a.getX() ; i++) {
				// Equation
				result = gradient * (i - a.getX()) + a.getY();
				lookupTable[i] = (int) result;
			}

			prevX = a.getX();
			prevY = a.getY();

		}

		// Calculate for final coord
		for (int i = (int) prevX ; i <= 255 ; i++) {
			result = ((255 - prevY) / (255 - prevX)) * (i - prevX) + prevY;
		}

	}
}
