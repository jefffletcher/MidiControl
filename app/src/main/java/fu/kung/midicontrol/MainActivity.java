package fu.kung.midicontrol;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import fu.kung.midicontrol.theory.ChordProgression;
import fu.kung.midicontrol.theory.Note;

public class MainActivity extends Activity {

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
        }

    }

    private void setupMidi() {
        MidiManager midiManager = (MidiManager) getSystemService(MIDI_SERVICE);
        MidiDeviceInfo[] deviceInfos = midiManager.getDevices();
    }
}
