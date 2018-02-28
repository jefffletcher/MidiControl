package fu.kung.midicontrol.theory;

import java.util.Arrays;
import java.util.List;

/**
 * Chord progressions.
 * https://sourceforge.net/p/jchordbox/code/HEAD/tree/trunk/src/java/main/org/jchordbox/song/models/Chord.java
 */
public enum ChordProgression {
    CP_1_4_5("I IV V", Arrays.asList(Chord.MAJOR_I, Chord.MAJOR_IV, Chord.MAJOR_V)),
    CP_6_2_5_1("vi ii V I",
            Arrays.asList(Chord.MAJOR_VI, Chord.MAJOR_II, Chord.MAJOR_V, Chord.MAJOR_I));

    private final String text;
    private final List<Chord> chords;

    ChordProgression(String text, List<Chord> chords) {
        this.text = text;
        this.chords = chords;
    }

    public List<Chord> getChords() {
        return chords;
    }

    public String getText() {
        return text;
    }

    public static ChordProgression getChordProgression(int text) {
        for (ChordProgression chordProgression : values()) {
            if (chordProgression.getText().equals(text)) {
                return chordProgression;
            }
        }
        throw new IllegalArgumentException(
                String.format("No chord progression defined for %d", text));
    }

    @Override
    public String toString() {
        return text;
    }
}
