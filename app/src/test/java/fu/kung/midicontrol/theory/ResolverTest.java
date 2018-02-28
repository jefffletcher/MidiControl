package fu.kung.midicontrol.theory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link Resolver}.
 */
@RunWith(JUnit4.class)
public class ResolverTest {
    @Test
    public void testC_Major_vi_ii_V_I_NoteValues() {
        List<Integer[]> notes = Resolver.getNoteValues(Note.C, ChordProgression.CP_6_2_5_1);
        assertEquals(4, notes.size());
        assertArrayEquals(new Integer[]{9, 0, 4}, notes.get(0));
        assertArrayEquals(new Integer[]{2, 5, 9}, notes.get(1));
        assertArrayEquals(new Integer[]{7, 11, 2}, notes.get(2));
        assertArrayEquals(new Integer[]{0, 4, 7}, notes.get(3));

        notes = Resolver.getNoteValues(Note.D, ChordProgression.CP_6_2_5_1);
        assertEquals(4, notes.size());
        assertArrayEquals(new Integer[]{11, 2, 6}, notes.get(0));
        assertArrayEquals(new Integer[]{4, 7, 11}, notes.get(1));
        assertArrayEquals(new Integer[]{9, 1, 4}, notes.get(2));
        assertArrayEquals(new Integer[]{2, 6, 9}, notes.get(3));
    }

    @Test
    public void testC_Major_vi_ii_V_I_Notes() {
        List<List<Note>> notes = Resolver.getNotes(Note.C, ChordProgression.CP_6_2_5_1);
        assertEquals(4, notes.size());
        assertEquals(Arrays.asList(Note.A, Note.C, Note.E), notes.get(0));
        assertEquals(Arrays.asList(Note.D, Note.F, Note.A), notes.get(1));
        assertEquals(Arrays.asList(Note.G, Note.B, Note.D), notes.get(2));
        assertEquals(Arrays.asList(Note.C, Note.E, Note.G), notes.get(3));

        notes = Resolver.getNotes(Note.D, ChordProgression.CP_6_2_5_1);
        assertEquals(4, notes.size());
        assertEquals(Arrays.asList(Note.B, Note.D, Note.F_SHARP), notes.get(0));
        assertEquals(Arrays.asList(Note.E, Note.G, Note.B), notes.get(1));
        assertEquals(Arrays.asList(Note.A, Note.C_SHARP, Note.E), notes.get(2));
        assertEquals(Arrays.asList(Note.D, Note.F_SHARP, Note.A), notes.get(3));
    }

    @Test
    public void testC_Major_I_IV_V() {
        List<Integer[]> notes = Resolver.getNoteValues(Note.C, ChordProgression.CP_1_4_5);
        assertEquals(3, notes.size());
        assertArrayEquals(new Integer[]{0, 4, 7}, notes.get(0));
        assertArrayEquals(new Integer[]{5, 9, 0}, notes.get(1));
        assertArrayEquals(new Integer[]{7, 11, 2}, notes.get(2));
    }

    @Test
    public void testText() {
        assertEquals("Am Dm G C", Resolver.getChordText(Note.C, ChordProgression.CP_6_2_5_1));
        assertEquals("Bm Em A D", Resolver.getChordText(Note.D, ChordProgression.CP_6_2_5_1));
        assertEquals("C#m F#m B E", Resolver.getChordText(Note.E, ChordProgression.CP_6_2_5_1));
        assertEquals("Dm Gm C F", Resolver.getChordText(Note.F, ChordProgression.CP_6_2_5_1));
        assertEquals("Em Am D G", Resolver.getChordText(Note.G, ChordProgression.CP_6_2_5_1));
        assertEquals("F#m Bm E A", Resolver.getChordText(Note.A, ChordProgression.CP_6_2_5_1));
        assertEquals("G#m C#m F# B", Resolver.getChordText(Note.B, ChordProgression.CP_6_2_5_1));
    }

    @Test
    public void testGetMidiNote() {
        assertEquals(60, Resolver.getMidiNote(Note.C));
        assertEquals(61, Resolver.getMidiNote(Note.C_SHARP));
        assertEquals(62, Resolver.getMidiNote(Note.D));
        assertEquals(63, Resolver.getMidiNote(Note.E_FLAT));
        assertEquals(64, Resolver.getMidiNote(Note.E));
        assertEquals(65, Resolver.getMidiNote(Note.F));
        assertEquals(66, Resolver.getMidiNote(Note.F_SHARP));
        assertEquals(67, Resolver.getMidiNote(Note.G));
        assertEquals(68, Resolver.getMidiNote(Note.G_SHARP));
        assertEquals(69, Resolver.getMidiNote(Note.A));
        assertEquals(70, Resolver.getMidiNote(Note.B_FLAT));
        assertEquals(71, Resolver.getMidiNote(Note.B));
    }
}