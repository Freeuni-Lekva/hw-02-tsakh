import javax.swing.*;
import java.awt.*;

public class JBrainTetris extends JTetris{
    private Brain brain;
    private JCheckBox brainMode;
    private JSlider adversary;
    private JLabel statusLabel;
    /**
     * Creates a new JTetris where each tetris square
     * is drawn with the given number of pixels.
     *
     * @param pixels
     */
    JBrainTetris(int pixels) {
        super(pixels);
        brain = new DefaultBrain();
    }

    @Override
    public void tick(int verb) {
        if (!brainMode.isSelected() || verb != DOWN){
            super.tick(verb);
            return;
        }
        if (verb == DOWN){
            board.undo(); // remove the piece from its old position
            Brain.Move bestMove = new Brain.Move();
            bestMove = brain.bestMove(board, currentPiece, HEIGHT, bestMove);
            if (bestMove == null){
                super.tick(verb);
                return;
            }
            if (!currentPiece.equals(bestMove.piece))  super.tick(ROTATE);
            if (bestMove.x < currentX) super.tick(LEFT);
            if (bestMove.x > currentX) super.tick(RIGHT);

            super.tick(DOWN);
        }
    }

    @Override
    public JComponent createControlPanel() {
        JComponent panel = super.createControlPanel();
        panel.add(new JLabel("Brain:"));
        brainMode = new JCheckBox("Brain active");
        panel.add(brainMode);
        // adversary slider
        JPanel row = new JPanel();
        panel.add(Box.createVerticalStrut(12));
        row.add(new JLabel("Adversary:"));
        adversary = new JSlider(0, 100, 0);	// min, max, current
        adversary.setPreferredSize(new Dimension(100, 15));
        row.add(adversary);
        statusLabel = new JLabel("ok");
        row.add(statusLabel);
        panel.add(row);
        return panel;
    }

    @Override
    public Piece pickNextPiece(){
        int randomNum = random.nextInt(98); // generate random number between -98 and 98
        if (randomNum < 0) randomNum *= -1;
        randomNum++; //to get random number between 1 and 99
        if(adversary.getValue() == 0 || randomNum >= adversary.getValue()){
            statusLabel.setText("ok");
            return super.pickNextPiece();
        }
        Brain.Move worst = new Brain.Move();
        Brain.Move curr;
        for (int i = 0; i < pieces.length; i++){
            curr = brain.bestMove(board, pieces[i], HEIGHT, null);
            if (i == 0 || worst.score < curr.score) worst = curr;
        }
        statusLabel.setText("*ok*");
        return worst.piece;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        JTetris tetris = new JBrainTetris(16);
        JFrame frame = JBrainTetris.createFrame(tetris);
        frame.setVisible(true);
    }
}
