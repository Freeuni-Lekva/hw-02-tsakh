import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/*
  Unit test for Piece class -- starter shell.
 */
public class  PieceTest extends TestCase {
	// You can create data to be used in the your
	// test cases like this. For each run of a test method,
	// a new PieceTest object is created and setUp() is called
	// automatically by JUnit.
	// For example, the code below sets up some
	// pyramid and s pieces in instance variables
	// that can be used in tests.
	private Piece pyr1, pyr2, pyr3, pyr4;
	private Piece s, sRotated;
	private  Piece l1, l2, l3, l4;
	private  Piece reversedL1, reversedL2, reversedL3, reversedL4;
	private Piece square;
	private Piece stick, stickRotated;
	private Piece reversedS1, reversedS2;
	private Piece [] pieces;
	protected void setUp() throws Exception {
		super.setUp();
		
		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();
		
		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();

		pieces = Piece.getPieces();

		l1 = new Piece(Piece.L1_STR);
		l2 = l1.computeNextRotation();
		l3 = l2.computeNextRotation();
		l4 = l3.computeNextRotation();

		reversedL1 = pieces[Piece.L2];//new Piece(Piece.L2_STR); // [Piece.L2];
		reversedL2 = reversedL1.computeNextRotation();
		reversedL3 = reversedL2.computeNextRotation();
		reversedL4 = reversedL3.computeNextRotation();

		square = pieces[Piece.SQUARE];

		stick = new Piece(Piece.STICK_STR);
		stickRotated = stick.computeNextRotation();

		reversedS1 = new Piece(Piece.S2_STR);
		reversedS2 = reversedS1.computeNextRotation();
	}
	
	// Here are some sample tests to get you started
	
	public void testSampleSize() {
		// Check size of pyr piece
		assertEquals(3, pyr1.getWidth());
		assertEquals(2, pyr1.getHeight());
		
		// Now try after rotation
		// Effectively we're testing size and rotation code here
		assertEquals(2, pyr2.getWidth());
		assertEquals(3, pyr2.getHeight());
		
		// Now try with some other piece, made a different way
		Piece l = new Piece(Piece.STICK_STR);
		assertEquals(1, l.getWidth());
		assertEquals(4, l.getHeight());

		//check for L
		assertEquals(2, l1.getWidth());
		assertEquals(3, l1.getHeight());
		// now check for rotated L
		assertEquals(3, l2.getWidth());
		assertEquals(2, l2.getHeight());

		//check for reversed L
		assertEquals(2, reversedL1.getWidth());
		assertEquals(3, reversedL1.getHeight());
		// now check for rotated
		assertEquals(3, reversedL2.getWidth());
		assertEquals(2, reversedL2.getHeight());

		//check for square
		assertEquals(2, square.getWidth());
		assertEquals(2, square.getHeight());
		// now check for new piece which is same figure
		Piece rotatedSquare = square.computeNextRotation();
		assertEquals(2, rotatedSquare.getWidth());
		assertEquals(2, rotatedSquare.getHeight());

		//check for stick
		assertEquals(1, stick.getWidth());
		assertEquals(4, stick.getHeight());
		// now check for rotated stick
		assertEquals(4, stickRotated.getWidth());
		assertEquals(1, stickRotated.getHeight());

		//check for reversed S
		assertEquals(3, reversedS1.getWidth());
		assertEquals(2, reversedS1.getHeight());
		//now check for rotated piece
		assertEquals(2, reversedS2.getWidth());
		assertEquals(3, reversedS2.getHeight());
	}
	
	
	// Test the skirt returned by a few pieces
	public void testSampleSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyr1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 1}, pyr3.getSkirt()));
		
		assertTrue(Arrays.equals(new int[] {0, 0, 1}, s.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, sRotated.getSkirt()));

		assertTrue(Arrays.equals(new int[] {1, 0, 0}, reversedS1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 1}, reversedS2.getSkirt()));

		assertTrue(Arrays.equals(new int[] {0, 0}, l1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, l2.getSkirt()));

		assertTrue(Arrays.equals(new int[] {0, 0}, square.getSkirt()));

		assertTrue(Arrays.equals(new int[] {0}, stick.getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0, 0, 0}, stickRotated.getSkirt()));

		assertTrue(Arrays.equals(new int[] {0, 0}, reversedL1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 1, 0}, reversedL2.getSkirt()));
	}
	// tests both rotations and also tests .equals()
	public void testRotation(){


		Piece reversedLTmp = pieces[Piece.L2];
		assertEquals(true, reversedL1.equals(reversedLTmp));

		assertEquals(true, reversedLTmp.fastRotation().equals(reversedL2));
		assertEquals(true, reversedLTmp.fastRotation().equals(reversedLTmp.computeNextRotation()));
		assertEquals(true, reversedLTmp.fastRotation().fastRotation().equals(reversedL3));
		assertEquals(true, reversedLTmp.fastRotation().fastRotation().fastRotation().equals(reversedL4));

		Piece lTmp = pieces[Piece.L1];
		assertEquals(true, l1.equals(lTmp));

		assertEquals(true, lTmp.fastRotation().equals(l2));
		assertEquals(true, lTmp.fastRotation().equals(lTmp.computeNextRotation()));
		assertEquals(true, lTmp.fastRotation().fastRotation().equals(l3));
		assertEquals(true, lTmp.fastRotation().fastRotation().fastRotation().equals(l4));
		assertEquals(true, lTmp.fastRotation().fastRotation().fastRotation().fastRotation().equals(l1));

		Piece pyr = pieces[Piece.PYRAMID];
		assertEquals(true, pyr.equals(pyr1));

		assertEquals(true, pyr.fastRotation().equals(pyr2));
		assertEquals(true, pyr.fastRotation().equals(pyr1.computeNextRotation()));
		assertEquals(true, pyr.fastRotation().fastRotation().equals(pyr3));
		assertEquals(true, pyr.fastRotation().fastRotation().fastRotation().equals(pyr4));
		assertEquals(true, pyr.fastRotation().fastRotation().fastRotation().fastRotation().equals(pyr1));

		Piece sTmp = pieces[Piece.S1];
		assertEquals(true, sTmp.equals(s));

		assertEquals(true, sTmp.fastRotation().equals(sRotated));
		assertEquals(true, sTmp.fastRotation().equals(s.computeNextRotation()));
		assertEquals(true, sTmp.fastRotation().fastRotation().equals(s));

		Piece sRevTmp = pieces[Piece.S2];
		assertEquals(true, sRevTmp.equals(reversedS1));

		assertEquals(true, sRevTmp.fastRotation().equals(reversedS2));
		assertEquals(true, sRevTmp.fastRotation().equals(reversedS1.computeNextRotation()));
		assertEquals(true, sRevTmp.fastRotation().fastRotation().equals(reversedS1));

		Piece squareTmp = pieces[Piece.SQUARE];
		assertEquals(true, squareTmp.equals(square));

		assertEquals(true, squareTmp.fastRotation().equals(square));
		assertEquals(true, squareTmp.fastRotation().equals(square.computeNextRotation()));

		Piece stickTmp = pieces[Piece.STICK];
		assertEquals(true, stickTmp.equals(stick));

		assertEquals(true, stickTmp.fastRotation().equals(stickRotated));
		assertEquals(true, stickTmp.fastRotation().fastRotation().equals(stick));
		assertEquals(true, stickTmp.fastRotation().equals(stick.computeNextRotation()));

	}

	public void testEquals(){
		assertEquals(false, l1.equals(l2));
		assertEquals(false, l2.equals(null));
		assertEquals(true, stick.equals(stick));

		TPoint[] fakePieceBody ={new TPoint(0,0), new TPoint(0,1)};
		Piece fakePiece = new Piece(fakePieceBody);
		assertEquals(false, square.equals(fakePiece));

		// TODO: tests with same pieces but different arrays
	}

	public void testGetBody(){
		List<TPoint> squareBody = Arrays.asList(square.getBody());
		List<TPoint> stickBody = Arrays.asList(stick.getBody());
		assertEquals(false, squareBody.containsAll(stickBody));
		assertEquals(false, stickBody.containsAll(squareBody));
	}

	public void testStringConstructor(){
		try{
			Piece fakePiece = new Piece("abc");
		}catch(Exception e){}
	}
}
