package src.Main;

/**
 * Runs a puzzle: shows each step, checks the answer, gives feedback.
 * There is NO time limit: take as long as you like. (Mistakes still cost sanity, tech debt and heap.)
 * The puzzle data is in Puzzles.java.
 */
public class PuzzleHandler {

    GameManager gm;
    Puzzles p;
    boolean active = false;
    int step = 0;
    int state = 0;         
    double feedbackTimer;
    boolean warned = false;   

    public PuzzleHandler(GameManager gm) { this.gm = gm; }

    public void start(int id) {
        p = Puzzles.get(id);
        active = true;
        step = 0;
        warned = false;
        gm.ui.puzzlePanel.setVisible(true);
        gm.ui.toastLabel.setVisible(false);
        gm.ui.hideMessage();          // no dialogue box behind the puzzle window
        showStep();
    }

    public void cancel() {
        active = false;
        gm.ui.puzzlePanel.setVisible(false);
    }

    private void showStep() {
        state = 0;
        gm.ui.showPuzzleStep(p, step, false);
        gm.ui.puzFeedback.setText("Click the correct code for the ??? slot.");
    }

    public void choose(int i) {
        if (!active || state != 0) return;
        if (i == p.correct[step]) {
            state = 1;
            feedbackTimer = 1.9;
            if (gm.player.claim("step:" + p.id + ":" + step)) gm.player.cleanStep();
            gm.ui.showPuzzleStep(p, step, true);
            gm.ui.puzFeedback.setText("COMPILES!  " + p.explanation[step]);
        } else {
            gm.ui.puzOption[i].setEnabled(false);
            gm.player.wrongAnswer();
            gm.ui.puzFeedback.setText("COMPILATION ERROR. Try again. (Sanity -8, Tech Debt +6, Heap +2)");
            if (p.gcButton && gm.player.heap >= 96 && !warned) {
                warned = true;
                gm.ui.puzFeedback.setText("FARSEER: Heap at " + (int) gm.player.heap + " percent! Trigger a manual sweep - now!");
            }
        }
    }

    public void hint() {
        if (!active || state != 0) return;
        if (gm.player.claim("hint:" + p.id + ":" + step)) gm.player.usedHint();
        gm.ui.puzFeedback.setText("HINT: " + p.hint[step] + "  (Tech Debt +4)");
    }

    public void gc() {
        if (!active || state != 0 || !p.gcButton) return;
        gm.ui.puzFeedback.setText(gm.player.gc());
        if (gm.player.heap < 96) warned = false;
    }

    /** Called every 0.1 seconds by the EventHandler timer. Only used to move on after a correct answer. */
    public void tick(double dt) {
        if (state != 1) return;
        feedbackTimer -= dt;
        if (feedbackTimer <= 0) {
            step++;
            if (step >= p.correct.length) finish(); else showStep();
        }
    }

    private void finish() {
        active = false;
        gm.ui.puzzlePanel.setVisible(false);
        gm.ev.puzzleDone(p.id);
    }
}
