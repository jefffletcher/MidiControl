package fu.kung.midicontrol.tools;

import android.media.midi.MidiReceiver;

import java.io.IOException;

/**
 * MIDI sequencer.
 */

public class MidiSequencer implements Runnable {
    private static final String TAG = "MidiSequencer";
    private static final int MIDDLE_C = 60;

    private MidiReceiver receiver;
    private int beatsPerMinute = 60;

    private byte[] accentOnBuffer;
    private byte[] accentOffBuffer;
    private byte[] nonAccentOnBuffer;
    private byte[] nonAccentOffBuffer;

    private boolean isRunning = false;

    public MidiSequencer(MidiReceiver receiver, int channel) {
        this.receiver = receiver;
        accentOnBuffer = command(MidiConstants.STATUS_NOTE_ON + channel, MIDDLE_C, 127);
        accentOffBuffer = command(MidiConstants.STATUS_NOTE_ON + channel, MIDDLE_C, 0);
        nonAccentOnBuffer = command(MidiConstants.STATUS_NOTE_ON + channel, MIDDLE_C, 90);
        nonAccentOffBuffer = command(MidiConstants.STATUS_NOTE_ON + channel, MIDDLE_C, 0);
    }

    public void start() {
        isRunning = true;
        new Thread(this).start();
    }

    public void stop() {
        isRunning = false;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        try {
            while (isRunning) {
                runCommand(accentOnBuffer);
                Thread.sleep(100);
                runCommand(accentOffBuffer);
                Thread.sleep(getTimeTillNextBeat(startTime));
                for (int i = 0; i < 3; i++) {
                    runCommand(nonAccentOnBuffer);
                    Thread.sleep(100);
                    runCommand(nonAccentOffBuffer);
                    Thread.sleep(getTimeTillNextBeat(startTime));
                }
            }
        } catch (InterruptedException e) {
            isRunning = false;
        } catch (IOException e) {
            isRunning = false;
        }
    }

    private void runCommand(byte[] command) throws IOException {
        receiver.send(command, 0, 3, System.nanoTime());
    }

    private long getTimeTillNextBeat(long startTime) {
        long position = System.currentTimeMillis() - startTime;
        long timeRemaining = position % 500; // assuming 120bpm (500ms per beat)
        return timeRemaining;
    }

    private byte[] command(int status, int data1, int data2) {
        byte[] buffer = new byte[3];
        buffer[0] = (byte) status;
        buffer[1] = (byte) data1;
        buffer[2] = (byte) data2;
        return buffer;
    }
}
