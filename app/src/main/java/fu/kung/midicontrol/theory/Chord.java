package fu.kung.midicontrol.theory;

/**
 * Chords.
 */

public enum Chord {
    MAJOR_I(Scale.MAJOR, "I", Triad.MAJ, 0),
    MAJOR_II(Scale.MAJOR, "ii", Triad.MIN, 2),
    MAJOR_III(Scale.MAJOR, "iii", Triad.MIN, 4),
    MAJOR_IV(Scale.MAJOR, "IV", Triad.MAJ, 5),
    MAJOR_V(Scale.MAJOR, "V", Triad.MAJ, 7),
    MAJOR_VI(Scale.MAJOR, "iv", Triad.MIN, 9),
    MAJOR_VII(Scale.MAJOR, "vii", Triad.DIM, 11);

    private final Scale scale;
    private final String text;
    private final Triad triad;
    private final int value;

    Chord(Scale scale, String text, Triad triad, int value) {
        this.scale = scale;
        this.text = text;
        this.triad = triad;
        this.value = value;
    }

    public Triad getTriad() {
        return triad;
    }

    public int getValue() {
        return value;
    }
}
