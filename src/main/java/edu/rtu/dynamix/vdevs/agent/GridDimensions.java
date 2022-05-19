package edu.rtu.dynamix.vdevs.agent;

/**
 * Encapsulates the dimensions of a space in N dimensions.
 */
public class GridDimensions {
	
	int[] dimensions;
	
	/**
	 * Creates a Dimensions from the specified array. The first element
	 * in the array will be the size in the x dimension, the second the size in the
	 * y dimension and so on.
	 *
	 * @param dimensions the dimension values
	 */
	public GridDimensions(int... dimensions) {
		this.dimensions = dimensions;
	}

	/**
	 * Gets the dimension size at the specified index. The x coordinate is at index 0, y at 1, z at 2 and so on.
	 * @param index
	 * @return
	 */
	public int getDimension(int index) {
		return dimensions[index];
	}
}
