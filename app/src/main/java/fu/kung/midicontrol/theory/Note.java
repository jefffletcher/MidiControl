package fu.kung.midicontrol.theory;

/**
 * Notes.
 */

public enum Note {
    C("C", "C", "C", 0),
    C_SHARP("C#", "C#", "Db", 1),
    D("D", "D", "D", 2),
    E_FLAT("Eb", "D#", "Eb", 3),
    E("E", "E", "E", 4),
    F("F", "F", "F", 5),
    F_SHARP("F#", "F#", "Gb", 6),
    G("G", "G", "G", 7),
    G_SHARP("G#", "G#", "Ab", 8),
    A("A", "A", "A", 9),
    B_FLAT("Bb", "A#", "Bb", 10),
    B("B", "B", "B", 11);

    private final String commonName;
    private final String sharpName;
    private final String flatName;
    private int value;

    Note(String commonName, String sharpName, String flatName, int value) {
        this.commonName = commonName;
        this.sharpName = sharpName;
        this.flatName = flatName;
        this.value = value;
    }

    public String getCommonName() {
        return this.commonName;
    }

    public String getSharpName() {
        return sharpName;
    }

    public String getFlatName() {
        return flatName;
    }

    // TODO: Populating UI Spinner with Note.values(), should be using
    // Note.getCommonNames() or similar
    @Override
    public String toString() {
        return this.commonName;
    }
}
