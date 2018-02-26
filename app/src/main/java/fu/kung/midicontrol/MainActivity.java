package fu.kung.midicontrol;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.midi.MidiManager;
import android.media.midi.MidiReceiver;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import fu.kung.midicontrol.theory.ChordProgression;
import fu.kung.midicontrol.theory.Note;
import fu.kung.midicontrol.midi.MidiInputPortSelector;
import fu.kung.midicontrol.midi.MidiSequencer;

public class MainActivity extends Activity {
    private static final String TAG = "MidiControl";
    private static final int INITIAL_BPM = 240;

    private SeekBar bpmSeekBar;
    private TextView bpmTextValue;

    private MidiInputPortSelector midiReceiverSelector;
    private int mChannel; // ranges from 0 to 15
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

            bpmTextValue = findViewById(R.id.bpmTextValue);
            bpmTextValue.setText("" + INITIAL_BPM);
            bpmSeekBar = findViewById(R.id.bpmSeekBar);
            bpmSeekBar.setMin(15);
            bpmSeekBar.setMax(300);
            bpmSeekBar.setProgress(INITIAL_BPM);
            bpmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                    if (sequencer != null) {
                        bpmTextValue.setText("" + value);
                        sequencer.setBeatsPerMinute(value);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            ToggleButton playButton = findViewById(R.id.playButton);
            playButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Play
                        if (midiReceiverSelector != null) {
                            MidiReceiver receiver = midiReceiverSelector.getReceiver();
                            if (receiver != null) {
                                sequencer = new MidiSequencer(receiver, mChannel);
                                sequencer.setBeatsPerMinute(bpmSeekBar.getProgress());
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

    @Override
    protected void onDestroy() {
        if (midiReceiverSelector != null) {
            midiReceiverSelector.close();
        }
        super.onDestroy();
    }
}
