package fu.kung.midicontrol.theory;

/**
 * Notes.
 */

public enum Note {
    C("C", "C", "C"),
    C_SHARP("C#", "C#", "Db"),
    D("D", "D", "D"),
    E_FLAT("Eb", "D#", "Eb"),
    E("E", "E", "E"),
    F("F", "F", "F"),
    F_SHARP("F#", "F#", "Gb"),
    G("G", "G", "G"),
    G_SHARP("G#", "G#", "Ab"),
    A("A", "A", "A"),
    B_FLAT("Bb", "A#", "Bb"),
    B("B", "B", "B");

    private final String commonName;
    private final String sharpName;
    private final String flatName;

    Note(String commonName, String sharpName, String flatName) {
        this.commonName = commonName;
        this.sharpName = sharpName;
        this.flatName = flatName;
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
    // Note.getCommonNames(); or similar
    @Override
    public String toString() {
        return this.commonName;
    }
}
