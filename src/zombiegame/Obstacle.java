package zombiegame;

import java.awt.Color;

public class Obstacle {

	double x;
	double y;
	double width;
	double height;
	Color colour;
	Color shadowColour = new Color (0,0,0,40);
	
	Obstacle (double x, double y, double width, double height, Color colour) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.colour = colour;
	 }
	
}
