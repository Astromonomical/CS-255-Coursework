package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
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

	/**
	 * Adds a coordinate to the graph
	 * @param coords New coordinate
	 */
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

	/**
	 * Removes a coordinate from the graph
	 * @param coords Coords to remove
	 */
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

	/**
	 * Update the contrast of an image input by the current state of the graph
	 * @param image Image to contrast stretch
	 * @return Contrast stretched image
	 */
	public Image updateContrast(Image image) {
		int height = (int) image.getHeight();
		int width = (int) image.getWidth();

		// New image to return
		WritableImage contrastStret = new WritableImage(width, height);

		// Open writer on new image, reader on old image
		PixelWriter contrastWriter = contrastStret.getPixelWriter();
		PixelReader imageReader = image.getPixelReader();

		for (int y = 0 ; y < height ; y++) {
			for (int x = 0 ; x < width ; x++) {
				// Read pixel from the image
				Color colour = imageReader.getColor(x, y);

				// Get gamma corrected values from table
				double red = getValue(colour.getRed() * 255);
				double green = getValue(colour.getGreen() * 255);
				double blue = getValue( colour.getBlue() * 255);

				// Create new pixel and change pixel
				colour = Color.color(red / 255.0, green / 255.0, blue / 255.0);
				contrastWriter.setColor(x, y, colour);
			}
		}

		return contrastStret;
	}

	/**
	 * Read through all point coordinates and draw points on graph
	 */
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

	/**
	 * Get stretched value from graph
	 * @param input Original value
	 * @return Stretched value
	 */
	private double getValue(double input) {
		int xOne = 0;
		int yOne = 0;

		int xTwo = 255;
		int yTwo = 255;

		boolean found = false;

		for(Coordinate a : points) {

			// Once i is less than a.getX() it must be between the last
			// coordinate and this one
			if (input < a.getX()) {
				xTwo = a.getX();
				yTwo = a.getY();
				found = true;
			} else {
				xOne = a.getX();
				yOne = a.getY();
			}
		}

		// Calculate the gradient
		double gradient = (yTwo - yOne) / (xTwo - xOne);

		// Calculate new value
		double result = gradient * (input - xTwo) + yTwo;

		return result;
	}
}