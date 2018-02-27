package fu.kung.midicontrol.theory;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolve (Root, ChordProgression) into actual notes to play.
 */

public class Resolver {
    public static List<Integer[]> getNoteValues(Note key, ChordProgression chordProgression) {
        List<Integer[]> result = new ArrayList<>();

        for (Chord chord : chordProgression.getChords()) {
            Integer[] noteValues = chord.getTriad().getNoteValues();
            Integer[] notes = new Integer[noteValues.length];
            int root = key.getValue() + chord.getValue();
            for (int i = 0; i < noteValues.length; i++) {
                notes[i] = sumNotes(root, noteValues[i]);
            }
            result.add(notes);
        }

        return result;
    }

    public static List<List<Note>> getNotes(Note key, ChordProgression chordProgression) {
        List<List<Note>> result = new ArrayList<>();

        for (Chord chord : chordProgression.getChords()) {
            Integer[] noteValues = chord.getTriad().getNoteValues();
            List<Note> notes = new ArrayList<>(noteValues.length);
            int root = key.getValue() + chord.getValue();
            for (int i = 0; i < noteValues.length; i++) {
                notes.add(Note.getNote(sumNotes(root, noteValues[i])));
            }
            result.add(notes);
        }

        return result;
    }

    public static String getChordText(Note key, ChordProgression chordProgression) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < chordProgression.getChords().size(); i++) {
            Chord chord = chordProgression.getChords().get(i);
            Note root = Note.getNote(sumNotes(key.getValue(), chord.getValue()));
            result.append(root.getCommonName());
            result.append(chord.getTriad().getText());

            if (i < (chordProgression.getChords().size() - 1)) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    public static int sumNotes(int... a) {
        int total = 0;
        for (int i : a) {
            total += i;
        }
        return total % 12;
    }
}
