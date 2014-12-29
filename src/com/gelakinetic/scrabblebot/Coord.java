package com.gelakinetic.scrabblebot;

public class Coord {
	int x, y;
	
	/**
	 * Constructor 
	 * 
	 * @param x The x component of this coordinate
	 * @param y The y component of this coordinate
	 */
	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Check to see if two coordinates are equivalent
	 * 
	 * @param obj The other coordinate to compare against
	 * @return true if they have the same values, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Coord) {
			return ((Coord)obj).x == x && ((Coord)obj).y == y;
		}
		return false;
	}
	
	/**
	 * Used to calculate the distance between two coordinates
	 * 
	 * @param other The other coordinate to measure from
	 * @return A double, the distance between the coordinates
	 */
	public double distFrom(Coord other) {
		return Math.sqrt(
				Math.pow(other.x - this.x, 2) + 
				Math.pow(other.y - this.y, 2));
	}
}
