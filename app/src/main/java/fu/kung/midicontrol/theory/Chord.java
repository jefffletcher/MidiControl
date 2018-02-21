package fu.kung.midicontrol.theory;

/**
 * Chords.
 */

public enum Chord {
    M7("M7", new Integer[]{0, 4, 7, 11}),
    Maj("Maj", new Integer[]{0, 4, 7});

    private final String name;
    private final Integer[] notes;
    ;

    Chord(String name, Integer[] notes) {
        this.name = name;
        this.notes = notes;
    }
}
