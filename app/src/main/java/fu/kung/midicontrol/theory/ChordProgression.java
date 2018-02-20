package fu.kung.midicontrol.theory;

/**
 * Chord progressions.
 * https://sourceforge.net/p/jchordbox/code/HEAD/tree/trunk/src/java/main/org/jchordbox/song/models/Chord.java
 */
public enum ChordProgression {
    CP_1_4_5("I IV V"),
    CP_6_2_5_1("vi ii V I");

    private final String name;

    ChordProgression(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
