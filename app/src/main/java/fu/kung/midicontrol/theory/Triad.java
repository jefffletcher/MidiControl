package fu.kung.midicontrol.theory;

/**
 * Triads.
 */

public enum Triad {
    MAJ("", new Integer[]{0, 4, 7}),
    MIN("m", new Integer[]{0, 3, 7}),
    AUG("aug", new Integer[]{0, 4, 8}),
    DIM("dim", new Integer[]{0, 3, 6}),
    SUS4("sus4", new Integer[]{0, 5, 7}),
    SUS2("sus2", new Integer[]{0, 2, 7});

    private final String text;
    private final Integer[] values;

    Triad(String text, Integer[] values) {
        this.text = text;
        this.values = values;
    }

    public Integer[] getNoteValues() {
        return values;
    }

    public String getText() {
        return text;
    }
}
