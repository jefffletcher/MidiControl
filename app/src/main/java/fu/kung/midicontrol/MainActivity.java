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

import java.util.List;

import fu.kung.midicontrol.theory.ChordProgression;
import fu.kung.midicontrol.theory.Note;
import fu.kung.midicontrol.midi.MidiInputPortSelector;
import fu.kung.midicontrol.midi.MidiSequencer;
import fu.kung.midicontrol.theory.Resolver;

public class MainActivity extends Activity {
    private static final String TAG = "MidiControl";
    private static final int INITIAL_BPM = 240;
    private static final int MIN_BPM = 15;
    private static final int MAX_BPM = 300;

    private Spinner keySpinner;
    private Spinner progressionSpinner;
    private TextView chordsText;
    private List<List<Note>> notes = null;

    private SeekBar bpmSeekBar;
    private TextView bpmTextValue;
    private ToggleButton playButton;

    private MidiInputPortSelector midiReceiverSelector;
    private int mChannel; // ranges from 0 to 15
    private MidiSequencer sequencer;

    public class ChannelSpinnerActivity implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            mChannel = pos & 0x0F;
            playButton.setEnabled(true);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public class ChordSelectionActivity implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            updateChords();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            setupMidi();

            keySpinner = findViewById(R.id.keySpinner);
            keySpinner.setAdapter(
                    new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Note.values()));
            keySpinner.setOnItemSelectedListener(new ChordSelectionActivity());

            progressionSpinner = findViewById(R.id.progressionSpinner);
            progressionSpinner.setAdapter(
                    new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            ChordProgression.values()));
            progressionSpinner.setOnItemSelectedListener(new ChordSelectionActivity());
            chordsText = findViewById(R.id.chordsText);

            ((Spinner) findViewById(R.id.midiChannelSpinner))
                    .setOnItemSelectedListener(new ChannelSpinnerActivity());

            bpmTextValue = findViewById(R.id.bpmTextValue);
            bpmTextValue.setText("" + INITIAL_BPM);
            bpmSeekBar = findViewById(R.id.bpmSeekBar);
            bpmSeekBar.setMax(MAX_BPM - MIN_BPM);
            bpmSeekBar.setProgress(INITIAL_BPM);
            bpmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                    int adjustedValue = value + MIN_BPM;
                    if (sequencer != null) {
                        bpmTextValue.setText("" + adjustedValue);
                        sequencer.setBeatsPerMinute(adjustedValue);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            playButton = findViewById(R.id.playButton);
            playButton.setEnabled(false);
            playButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Play
                        if (midiReceiverSelector != null) {
                            MidiReceiver receiver = midiReceiverSelector.getReceiver();
                            if (receiver != null) {
                                sequencer = new MidiSequencer(receiver, mChannel);
                                if (notes != null) {
                                    sequencer.setNotes(notes);
                                }
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

    private void updateChords() {
        Note root = (Note) keySpinner.getSelectedItem();
        ChordProgression progression = (ChordProgression) progressionSpinner.getSelectedItem();

        chordsText.setText(Resolver.getChordText(root, progression));
        notes = Resolver.getNotes(root, progression);
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
