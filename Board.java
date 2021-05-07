// Board.java

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private boolean[][] grid, gridCopy;
	private int [] widths, widthsCopy;
	private int[] heights, heightsCopy;
	private int maxHeight, maxHeightCopy;
	private boolean DEBUG = true;
	boolean committed;
	
	
	// Here a few trivial methods are provided:
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		gridCopy = new boolean[width][height];
		widths = new int [height];
		widthsCopy = new int [height];
		heights = new int[width];
		heightsCopy = new int[width];
		maxHeight = maxHeightCopy = 0;
		committed = true;
	}
	
	
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() { return maxHeight; }
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (DEBUG) {
			//checking heights
			int correctMaxHeight = 0;
			for (int i = 0; i < width; i++){
				int correctHeight = 0;
				for (int j = 0; j < height; j++){
					if (grid[i][j]) correctHeight = Math.max(correctHeight, j + 1);
				}
				if (getColumnHeight(i) != correctHeight) throw new RuntimeException("height is not correct");
				correctMaxHeight = Math.max(correctMaxHeight, correctHeight);
			}
			if (correctMaxHeight != getMaxHeight()) throw new RuntimeException("Max height is not correct");

			//checking widths
			for (int i = 0; i < height; i++){
				int correctWidth = 0;
				for (int j = 0; j < width; j++){
					if (grid[j][i]) correctWidth++;
				}
				if (getRowWidth(i) != correctWidth) throw new RuntimeException("width is not correct");
			}
		}
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		if (x < 0) return  Board.PLACE_OUT_BOUNDS;
		int [] pieceSkirt = piece.getSkirt();
		int res = 0;
		for (int deltaX = 0; deltaX < pieceSkirt.length; deltaX++){
			// for every column, (0,x) point will be placed at y = height-skirt of that column
			// since gravity does not work, we have to find maximum value of height-skirt
			int deltaY = pieceSkirt[deltaX];
			int currentX = x + deltaX;
			if (currentX >= width) return Board.PLACE_OUT_BOUNDS;
			res = Math.max(heights[currentX] - deltaY, res);
		}
		return res;
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		if (x < 0 || x >= width) throw new RuntimeException("x is not in bounds");
		return heights[x];
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		if (y < 0 || y >= height) throw new RuntimeException("y not in bounds");
		return widths[y];
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		if (x >= width || x < 0 || y >= height || y < 0) return true;
		return grid[x][y];
	}
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;

	private void copyData (){
		// copying data for uncommitted versions
		committed = false;
		System.arraycopy(widths, 0, widthsCopy, 0, widths.length);
		System.arraycopy(heights, 0, heightsCopy, 0, heights.length);
		for (int i = 0; i < grid.length; i++){
			System.arraycopy(grid[i], 0, gridCopy[i], 0, grid[i].length);
		}
		maxHeightCopy = maxHeight;
	}
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");
		int result = PLACE_OK;
		copyData();
		TPoint[] pieceBody = piece.getBody();
		for (int i = 0; i < pieceBody.length; i++){
			int currX = x + pieceBody[i].x;
			int currY = y + pieceBody[i].y;
			if (currX < 0 || currX >= width || currY < 0 || currY >= height) return PLACE_OUT_BOUNDS;
			if (grid[currX][currY]) return PLACE_BAD;
			grid[currX][currY] = true;
			heights[currX] = Math.max(heights[currX], currY + 1);
			widths[currY]++;
			if (widths[currY] == width) result = PLACE_ROW_FILLED;
			maxHeight = Math.max(maxHeight, currY + 1);
		}
		sanityCheck();
		return result;
	}

	private void clearSingleRow(int row){
		for (int i = 0; i < grid.length; i++){
			grid[i][row] = false;
		}
	}

	private void moveDownOneRow (int dest, int src){
		for (int i = 0; i < grid.length; i++){
			grid[i][dest] = grid[i][src];
		}
	}

	private void moveDown(int row){
		for (int i = row; i < grid[0].length - 1; i++){
			widths[i] = widths[i + 1];
			moveDownOneRow(i, i + 1);
		}
		clearSingleRow(grid[0].length - 1);
		widths[widths.length - 1] = 0;
	}

	private void changeHeights (){
		maxHeight = 0;
		for (int i = 0; i < width; i++){
			int correctHeight = 0;
			for (int j = 0; j < height; j++){
				if (grid[i][j]) correctHeight = Math.max(correctHeight, j + 1);
			}
			heights[i] = correctHeight;
			maxHeight = Math.max(maxHeight, correctHeight);
		}
	}

	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		if (grid.length == 0) return 0;
		int rowsCleared = 0;
		if (committed)copyData();
		committed = false;
		for (int i = 0; i < widths.length; i++){
			if (widths[i] == width) {
				moveDown(i);
				rowsCleared++;
				i--; // because we need to visit row, which moved on ith row
			}
		}
		changeHeights();
		sanityCheck();
		return rowsCleared;
	}



	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		if (committed) return;
		int[] tmp = widths;
		widths = widthsCopy;
		widthsCopy = tmp;

		tmp = heights;
		heights = heightsCopy;
		heightsCopy = tmp;

		maxHeight = maxHeightCopy;

		boolean [] []tmpGrid = grid;
		grid = gridCopy;
		gridCopy = tmpGrid;

		sanityCheck();
		committed = true;
	}
	
	
	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		committed = true;
	}


	
	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}


