package fu.kung.midicontrol;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.midi.MidiManager;
import android.media.midi.MidiReceiver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;

import fu.kung.midicontrol.theory.ChordProgression;
import fu.kung.midicontrol.theory.Note;
import fu.kung.midicontrol.tools.MidiConstants;
import fu.kung.midicontrol.tools.MidiInputPortSelector;
import fu.kung.midicontrol.tools.MidiSequencer;

public class MainActivity extends Activity {
    private static final String TAG = "MidiControl";
    private static final int DEFAULT_VELOCITY = 64;
    private static final int MIDDLE_C = 60;

    private MidiInputPortSelector midiReceiverSelector;
    private int mChannel; // ranges from 0 to 15
    private byte[] midiByteBuffer = new byte[3];
    private MidiSequencer sequencer;

    public class ChannelSpinnerActivity implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            mChannel = pos & 0x0F;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            setupMidi();

            Spinner keySpinner = findViewById(R.id.keySpinner);
            keySpinner.setAdapter(
                    new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Note.values()));

            Spinner progressionSpinner = findViewById(R.id.progressionSpinner);
            progressionSpinner.setAdapter(
                    new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            ChordProgression.values()));

            ((Spinner) findViewById(R.id.midiChannelSpinner))
                    .setOnItemSelectedListener(new ChannelSpinnerActivity());

            ToggleButton playButton = findViewById(R.id.playButton);
//            playButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked) {
//                        // Play
//                        noteOn(mChannel, MIDDLE_C, DEFAULT_VELOCITY);
//                    } else {
//                        // Pause
//                        noteOff(mChannel, MIDDLE_C, DEFAULT_VELOCITY);
//                    }
//                }
//            });
            playButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Play
                        if (midiReceiverSelector != null) {
                            MidiReceiver receiver = midiReceiverSelector.getReceiver();
                            if (receiver != null) {
                                sequencer = new MidiSequencer(receiver, mChannel);
                                sequencer.start();
                            }
                        }
                    } else {
                        // Pause
                        if (sequencer != null) {
                            sequencer.stop();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(this, "MIDI not supported.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupMidi() {
        MidiManager midiManager = (MidiManager) getSystemService(MIDI_SERVICE);
        midiReceiverSelector =
                new MidiInputPortSelector(midiManager, this, R.id.midiReceiverSpinner);
    }

    private void noteOff(int channel, int pitch, int velocity) {
        midiCommand(MidiConstants.STATUS_NOTE_OFF + channel, pitch, velocity);
    }

    private void noteOn(int channel, int pitch, int velocity) {
        midiCommand(MidiConstants.STATUS_NOTE_ON + channel, pitch, velocity);
    }

    private void midiCommand(int status, int data1, int data2) {
        midiByteBuffer[0] = (byte) status;
        midiByteBuffer[1] = (byte) data1;
        midiByteBuffer[2] = (byte) data2;
        midiSend(midiByteBuffer, 3, System.nanoTime());
    }

    private void midiSend(byte[] buffer, int count, long timestamp) {
        if (midiReceiverSelector != null) {
            try {
                // send event immediately
                MidiReceiver receiver = midiReceiverSelector.getReceiver();
                if (receiver != null) {
                    receiver.send(buffer, 0, count, timestamp);
                }
            } catch (IOException e) {
                Log.e(TAG, "mKeyboardReceiverSelector.send() failed " + e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (midiReceiverSelector != null) {
            midiReceiverSelector.close();
        }
        super.onDestroy();
    }
}
