package ist.meic.cm.bomberman.settings;

import ist.meic.cm.bomberman.Menu;
import ist.meic.cm.bomberman.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_settings);

		FragmentManager mFragmentManager = getFragmentManager();
		FragmentTransaction mFragmentTransaction = mFragmentManager
				.beginTransaction();
		PrefsFragment mPrefsFragment = new PrefsFragment();
		mFragmentTransaction.replace(R.id.fragment, mPrefsFragment);
		mFragmentTransaction.commit();

		Button btnGetPrefs = (Button) findViewById(R.id.btnGetPreferences);
		btnGetPrefs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				displaySharedPreferences();
			}
		});
	}

	@Override
	public void onBackPressed() {

		Toast.makeText(SettingsActivity.this, "Settings Saved!",
				Toast.LENGTH_SHORT).show();

		Intent intent = new Intent(this, Menu.class);
		startActivity(intent);
	}

	private void displaySharedPreferences() {

		Builder ad = new AlertDialog.Builder(SettingsActivity.this)
				.setTitle("Current Settings").setMessage(prefsToString())
				.setNeutralButton("OK", null).setIcon(R.drawable.ic_launcher);
		try {
			ad.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String prefsToString() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(SettingsActivity.this);

		StringBuilder builder = new StringBuilder();
		builder.append("Map: ");
		builder.append(prefs.getString(Settings.MAP, Settings.MAP_DEFAULT));
		builder.append("\n");

		builder.append("Game Duration: ");
		builder.append(prefs.getString(Settings.DURATION,
				Settings.DURATION_DEFAULT));
		builder.append("\n");

		builder.append("Robot Speed: ");
		builder.append(prefs.getString(Settings.RS, Settings.RS_DEFAULT));
		builder.append("\n");

		builder.append("Explosion Timeout: ");
		builder.append(prefs.getString(Settings.ET, Settings.ET_DEFAULT));
		builder.append("\n");

		builder.append("Explosion Duration: ");
		builder.append(prefs.getString(Settings.ED, Settings.ED_DEFAULT));
		builder.append("\n");

		builder.append("Explosion Range: ");
		builder.append(prefs.getString(Settings.ER, Settings.ER_DEFAULT));
		builder.append("\n");

		builder.append("Points Robot: ");
		builder.append(prefs.getString(Settings.PR, Settings.PR_DEFAULT));
		builder.append("\n");

		builder.append("Points Opponent: ");
		builder.append(prefs.getString(Settings.PO, Settings.PO_DEFAULT));
		return builder.toString();
	}
}
