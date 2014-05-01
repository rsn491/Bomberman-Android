package ist.meic.cm.bomberman.settings;

import ist.meic.cm.bomberman.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class SettingsActivity extends Activity implements
		OnSharedPreferenceChangeListener {

	private Settings settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_settings);

		settings = new Settings();

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
		super.onBackPressed();
	}

	private void displaySharedPreferences() {

		Builder ad = new AlertDialog.Builder(SettingsActivity.this)
				.setTitle("Current Settings").setMessage(settings.toString())
				.setNeutralButton("OK", null).setIcon(R.drawable.ic_launcher);
		try {
			ad.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this)
				.registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onPause() {
		PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this)
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if (key.equals(Settings.MAP)) {

			settings.setLevelName(prefs.getString(Settings.MAP, "Level1"));
		} else if (key.equals(Settings.DURATION)) {

			settings.setGameDuration(Integer.parseInt(prefs.getString(
					Settings.DURATION, "120")));
		} else if (key.equals(Settings.RS)) {

			settings.setRobotSpeed(Integer.parseInt(prefs.getString(
					Settings.RS, "1")));
		} else if (key.equals(Settings.ET)) {

			settings.setExplosionTimeout(Integer.parseInt(prefs.getString(
					Settings.ET, "3")));
		} else if (key.equals(Settings.ED)) {

			settings.setExplosionDuration(Integer.parseInt(prefs.getString(
					Settings.ED, "3")));
		} else if (key.equals(Settings.ER)) {

			settings.setExplosionRange(Integer.parseInt(prefs.getString(
					Settings.ER, "1")));
		} else if (key.equals(Settings.PR)) {

			settings.setExplosionRange(Integer.parseInt(prefs.getString(
					Settings.PR, "1")));
		} else if (key.equals(Settings.PO)) {

			settings.setPointsOpponent(Integer.parseInt(prefs.getString(
					Settings.PO, "5")));
		}

	}
}
