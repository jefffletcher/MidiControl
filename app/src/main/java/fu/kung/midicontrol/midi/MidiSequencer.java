package fu.kung.midicontrol.midi;

import android.media.midi.MidiReceiver;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fu.kung.midicontrol.theory.Note;
import fu.kung.midicontrol.theory.Resolver;

/**
 * MIDI sequencer.
 */

public class MidiSequencer {
    private static final String TAG = "MidiSequencer";
    private static final int MIDDLE_C = 60;
    private static final long NANOS_PER_SECOND = 1000000000L;

    private MidiReceiver receiver;
    private int channel;
    private int beatsPerMinute = 250;
    private List<List<Note>> notes;

    private List<List<SequencerEvent>> events;
    private int tick = 0;

    private boolean isRunning = false;

    private HandlerThread handlerThread;
    private Handler handler;

    private Runnable eventProcessor = new Runnable() {
        @Override
        public void run() {
            if (events.get(tick).size() != 0) {
                for (SequencerEvent event : events.get(tick)) {
                    try {
                        playNote(event);
                    } catch (IOException e) {
                        Log.e(TAG, String.format("Error running command %s", event));
                    }
                }
            }
            tick += 1;
            tick %= events.size();

            if (isRunning) {
                processEvent();
            }
        }
    };

    public MidiSequencer(MidiReceiver receiver, int channel) {
        this.receiver = receiver;
        this.channel = channel;
        handlerThread = new HandlerThread("SequencerHandlerThread");
        tick = 0;
    }

    private void processEvent() {
        handler.postDelayed(eventProcessor, 60000 / (beatsPerMinute * 4));
    }

    public void start() {
        initializeEvents();
        isRunning = true;
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        processEvent();
    }

    private void initializeEvents() {
        if (notes == null) {
            events = new ArrayList<>(16);
            for (int i = 0; i < 16; i++) {
                events.add(Collections.EMPTY_LIST);
            }

            // Metronome
            events.set(0, Arrays.asList(new SequencerEvent(channel, MIDDLE_C, 127)));
            events.set(4, Arrays.asList(new SequencerEvent(channel, MIDDLE_C, 60)));
            events.set(8, Arrays.asList(new SequencerEvent(channel, MIDDLE_C, 60)));
            events.set(12, Arrays.asList(new SequencerEvent(channel, MIDDLE_C, 60)));
            return;
        }

        int numEvents = notes.size() * 4;
        events = new ArrayList<>(numEvents);
        for (int i = 0; i < numEvents; i++) {
            events.add(Collections.EMPTY_LIST);
        }

        int eventPointer = 0;
        for (List<Note> chord : notes) {
            ArrayList<SequencerEvent> tickEvents = new ArrayList<>();
            for (Note note : chord) {
                tickEvents.add(new SequencerEvent(channel, Resolver.getMidiNote(note), 90));
            }
            events.set(eventPointer, tickEvents);
            eventPointer += 4;
        }

    }

    public void stop() {
        isRunning = false;
        handlerThread.quit();
        try {
            receiver.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error flushing MIDI receiver");
        }
    }

    private void playNote(SequencerEvent event) throws IOException {
        long now = System.nanoTime();
        receiver.send(
                command(MidiConstants.STATUS_NOTE_ON + event.channel, event.data1, event.data2),
                0, 3, now);
        receiver.send(
                command(MidiConstants.STATUS_NOTE_OFF + event.channel, event.data1, 0),
                0, 3, (long) (now + (.1 * NANOS_PER_SECOND))); // TODO: Note duration.
    }

    private byte[] command(int status, int data1, int data2) {
        byte[] buffer = new byte[3];
        buffer[0] = (byte) status;
        buffer[1] = (byte) data1;
        buffer[2] = (byte) data2;
        return buffer;
    }

    public void setBeatsPerMinute(int beatsPerMinute) {
        this.beatsPerMinute = beatsPerMinute;
    }

    public void setNotes(List<List<Note>> notes) {
        this.notes = notes;
    }

    class SequencerEvent {
        int channel;
        int data1;
        int data2;

        SequencerEvent(int channel, int data1, int data2) {
            this.channel = channel;
            this.data1 = data1;
            this.data2 = data2;
        }
    }
}
