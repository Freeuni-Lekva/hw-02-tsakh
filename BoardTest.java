import junit.framework.TestCase;
import org.junit.Test;


public class BoardTest extends TestCase {
	Board b;
	Piece pyr1, pyr2, pyr3, pyr4, s, sRotated;
	Piece stick, L, reversedL, square;

	// This shows how to build things in setUp() to re-use
	// across tests.
	
	// In this case, setUp() makes shapes,
	// and also a 3X6 board, with pyr placed at the bottom,
	// ready to be used by tests.
	
	protected void setUp() throws Exception {
		b = new Board(3, 6);
		
		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();
		
		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();

		stick = new Piece(Piece.STICK_STR);
		L = new Piece(Piece.L1_STR);
		reversedL = new Piece(Piece.L2_STR);
		square = new Piece(Piece.SQUARE_STR);

		b.place(pyr1, 0, 0);
	}
	
	// Check the basic width/height/max after the one placement
	public void testSample1() {
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(2, b.getMaxHeight());
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
	}
	
	// Place sRotated into the board, then check some measures
	public void testSample2() {
		b.commit();
		int result = b.place(sRotated, 1, 1);
		assertEquals(Board.PLACE_OK, result);
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(4, b.getMaxHeight());
	}
	
	// Make  more tests, by putting together longer series of
	// place, clearRows, undo, place ... checking a few col/row/max
	// numbers that the board looks right after the operations.

	public void testExtra1(){
		b = new Board(4, 6);
		int result = b.place(stick, 0, 0);
		assertEquals(Board.PLACE_OK, result);
		b.commit();
		result = b.place(stick, 1, 0);
		b.commit();
		assertEquals(Board.PLACE_OK, result);

		assertEquals(4, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(0, b.getColumnHeight(2));

		assertEquals(4, b.getMaxHeight());
		assertEquals(2, b.getRowWidth(0));

		assertEquals(4, b.getWidth());
		assertEquals(6, b.getHeight());

		System.out.println(b.toString());
		result = b.place(reversedL, 2, 0);
		b.commit();
		assertEquals(result, Board.PLACE_ROW_FILLED);
		b.clearRows();
		b.commit();
		assertEquals(3, b.getMaxHeight());
	}

	public void testExtra2(){
		b = new Board(4, 4);
		b.place(s, 0, 0);
		b.commit();
		int result = b.place(stick, 1, 0);
		assertEquals(result, Board.PLACE_BAD);
		b.undo();
		System.out.println(b.toString());
		result = b.dropHeight(square, 2);
		assertEquals(result, 2);
	}

	public void testExtra3(){
		b = new Board(3, 3);
		int result = b.place(stick, 0, 0);
		assertEquals(result, Board.PLACE_OUT_BOUNDS);
		assertEquals(Board.PLACE_OUT_BOUNDS, b.dropHeight(L, 2));
	}

	public void testExtra4(){
		b = new Board(5, 5);
		int result = b.place(square, 0 , 0);
		b.commit();
		assertEquals(result, Board.PLACE_OK);
		result = b.place(square, 2, 1);
		b.commit();
		assertEquals(result, Board.PLACE_OK);
		result = b.place(stick, 4, 1);
		b.commit();
		assertEquals(result, Board.PLACE_ROW_FILLED);
		System.out.println(b.toString());
		b.clearRows();
		System.out.println(b.toString());
		assertEquals(4, b.getMaxHeight());
	}

	public void testDropHeight(){
		b = new Board(4, 4);
		assertEquals(b.dropHeight(square, 0), 0);
		b.place(square, 0, 0);
		b.commit();
		Piece stickRotation = stick.computeNextRotation();
		assertEquals(b.dropHeight(stickRotation, 0), 2);
		assertEquals(Board.PLACE_ROW_FILLED, b.place(stickRotation, 0, 2));
		b.commit();
		b.clearRows();
		b.commit();
		assertEquals(b.dropHeight(square, 0), 2);
		assertEquals(b.dropHeight(square, 2), 0);
		assertEquals(b.getColumnHeight(0), 2);
		assertEquals(b.getColumnHeight(2), 0);
		assertEquals(b.getRowWidth(0), 2);
		assertEquals(b.getRowWidth(2), 0);
		assertEquals(true, b.getGrid(10, 10));
		System.out.println(b.toString());
	}

	public void testForCommitException(){
		b = new Board(4, 4);
		b.place(square, 0, 0);
		try{
			b.place(square, 2, 2);
		}catch(Exception e){}
	}

	public void testForExceptions1(){
		b = new Board(4, 4);
		b.place(L, 0, 0);
		b.commit();
		try{
			b.getColumnHeight(5);
		}catch(Exception e){}

		try{
			b.getRowWidth(-1);
		}catch(Exception e){}
	}
	@Test (expected = RuntimeException.class)
	public void testForExceptions2(){
		b = new Board(4, 4) {
			@Override
			public int getRowWidth(int y) {
				return -1;
			}
		};
		try{
			b.place(L, 0, 0);
		}catch(Exception e){}
	}

	@Test (expected = RuntimeException.class)
	public void testForExceptions3(){
		b = new Board(4, 4) {
			@Override
			public int getColumnHeight(int x) {
				return -1;
			}
		};
		try{
			b.place(L, 0, 0);
		}catch(Exception e){}
	}

	@Test (expected = RuntimeException.class)
	public void testForExceptions4(){
		b = new Board(4, 4) {
			@Override
			public int getMaxHeight(){
				return -1;
			}
		};
		try{
			b.place(L, 0, 0);
		}catch(Exception e){}
	}

	public void testClearRows(){
		b = new Board(0, 0);
		assertEquals(0, b.clearRows());

		b = new Board(4, 4);
		Piece s2 = stick.computeNextRotation();
		assertEquals(Board.PLACE_ROW_FILLED, b.place(s2, 0, 0));
		b.commit();
		assertEquals(1, b.clearRows());
		b.commit();
		assertEquals(b.place(L, 0, 0), Board.PLACE_OK);
		b.commit();
		assertEquals(b.place(square, 1, 1), Board.PLACE_OK);
		b.commit();
		assertEquals(b.place(stick, 3, 0), Board.PLACE_ROW_FILLED);
		b.commit();
		b.undo();
		assertEquals(2, b.clearRows());
		b.undo();
		System.out.println(b.toString());
		assertEquals(4, b.getMaxHeight());
	}

	public void testClearRows2(){
		b = new Board(5, 5);
		assertEquals(Board.PLACE_OK,b.place(L, 0, 0));
		b.commit();
		assertEquals(Board.PLACE_OK, b.place(square, 2, 0));
		b.commit();
		assertEquals(Board.PLACE_ROW_FILLED, b.place(stick, 4, 0));
		b.commit();
		assertEquals(Board.PLACE_ROW_FILLED, b.place(stick.computeNextRotation(), 0, 3));
		b.commit();
		assertEquals(2, b.clearRows());
		assertEquals(2, b.getMaxHeight());
		b.undo();
		assertEquals(4, b.getMaxHeight());
	}

}
