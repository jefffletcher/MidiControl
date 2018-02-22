package fu.kung.midicontrol.tools;

import android.media.midi.MidiReceiver;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.IOException;

/**
 * MIDI sequencer.
 */

public class MidiSequencer {
    private static final String TAG = "MidiSequencer";
    private static final int MIDDLE_C = 60;
    private static final long NANOS_PER_SECOND = 1000000000L;

    private MidiReceiver receiver;
    private int beatsPerMinute = 250;

    private SequencerEvent[] events;
    private int tick = 0;

    private boolean isRunning = false;

    private HandlerThread handlerThread;
    private Handler handler;

    private Runnable eventProcessor = new Runnable() {
        @Override
        public void run() {
            if (events[tick] != null) {
                try {
                    playNote(events[tick]);
                } catch (IOException e) {
                    Log.e(TAG, String.format("Error running command %s", events[tick]));
                }
            }
            tick += 1;
            tick %= 16;

            if (isRunning) {
                processEvent();
            }
        }
    };

    public MidiSequencer(MidiReceiver receiver, int channel) {
        this.receiver = receiver;
        handlerThread = new HandlerThread("SequencerHandlerThread");
        events = new SequencerEvent[16];
        tick = 0;

        events[0] = new SequencerEvent(channel, MIDDLE_C, 127);
        events[4] = new SequencerEvent(channel, MIDDLE_C, 60);
        events[8] = new SequencerEvent(channel, MIDDLE_C, 60);
        events[12] = new SequencerEvent(channel, MIDDLE_C, 60);
    }

    private void processEvent() {
        handler.postDelayed(eventProcessor, 60000 / (beatsPerMinute * 4));
    }

    public void start() {
        isRunning = true;
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        processEvent();
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
