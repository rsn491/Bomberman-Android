package ist.meic.cm.bomberman.settings;

import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SettingsActivity extends Activity {

	private static final String FILENAME = ".settings";
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

		writeToFile();

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

	private void displaySharedPreferences() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(SettingsActivity.this);

		String username = prefs.getString("username", "Default NickName");
		String passw = prefs.getString("password", "Default Password");
		boolean checkBox = prefs.getBoolean("checkBox", false);
		String listPrefs = prefs.getString("listpref", "Default list prefs");

		StringBuilder builder = new StringBuilder();
		builder.append("Username: " + username + "\n");
		builder.append("Password: " + passw + "\n");
		builder.append("Keep me logged in: " + String.valueOf(checkBox) + "\n");
		builder.append("List preference: " + listPrefs);

		Builder ad = new AlertDialog.Builder(SettingsActivity.this)
				.setTitle("Current Settings").setMessage(builder.toString())
				.setNeutralButton("OK", null).setIcon(R.drawable.ic_launcher);
		try {
			ad.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeToFile() {
		StringBuilder sb = new StringBuilder(settings.getLevelName());
		sb.append("\n");
		sb.append(settings.getGameDuration());
		sb.append("\n");
		sb.append(settings.getExplosionTimeout());
		sb.append("\n");
		sb.append(settings.getExplosionDuration());
		sb.append("\n");
		sb.append(settings.getExplosionRange());
		sb.append("\n");
		sb.append(settings.getRobotSpeed());
		sb.append("\n");
		sb.append(settings.getPointsRobot());
		sb.append("\n");
		sb.append(settings.getPointsOpponent());

		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					openFileOutput(FILENAME, Context.MODE_PRIVATE));
			outputStreamWriter.write(sb.toString());
			outputStreamWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String readFromFile() {

		String ret = "";

		try {
			InputStream inputStream = openFileInput(FILENAME);

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}
}
