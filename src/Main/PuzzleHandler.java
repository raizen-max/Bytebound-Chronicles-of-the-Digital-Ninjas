package src.Main;

// runs a puzzle, data located in Puzzles.java
public class PuzzleHandler {

    GameManager gm;
    Puzzles p;
    boolean active = false;
    int step = 0;
    int state = 0;            
    double timeLeft, feedbackTimer;
    double limit, heapRate;     

    public PuzzleHandler(GameManager gm) { this.gm = gm; }

    public void start(int id) {
        p = Puzzles.get(id);
        active = true;
        step = 0;
        limit = p.timeLimit;
        heapRate = p.heapPerSecond;
        timeLeft = limit;
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
    }

    public void tick(double dt) {
        if (state == 0) {
            gm.player.changeHeap(heapRate * dt);
            if (limit > 0) {
                timeLeft -= dt;
                if (timeLeft <= 0) {
                    timeLeft = 0;
                    gm.player.timeout();
                    state = 2;
                    feedbackTimer = 2.2;
                    gm.ui.puzFeedback.setText("TIME'S UP! Alex slaps in a quick hack. (Sanity -15, Tech Debt +10)");
                }
            }
            if (p.gcButton && gm.player.heap >= 96)
                gm.ui.puzFeedback.setText("FARSEER: Heap at " + (int) gm.player.heap + " percent! Trigger a manual sweep - now!");
        } else {
            feedbackTimer -= dt;
            if (feedbackTimer <= 0) {
                if (state == 2) { finish(true); return; }
                step++;
                if (step >= p.correct.length) finish(false); else showStep();
            }
        }
        gm.ui.updatePuzzleTimer(p, timeLeft, limit);
    }

    private void finish(boolean timedOut) {
        active = false;
        gm.ui.puzzlePanel.setVisible(false);
        gm.ev.puzzleDone(p.id, timedOut);
    }
}
