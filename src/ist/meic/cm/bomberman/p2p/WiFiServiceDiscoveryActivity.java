package ist.meic.cm.bomberman.p2p;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.R;
import ist.meic.cm.bomberman.multiplayerC.Message;
import ist.meic.cm.bomberman.p2p.WiFiDirectServicesList.DeviceClickListener;
import ist.meic.cm.bomberman.p2p.WiFiDirectServicesList.WiFiDevicesAdapter;
import ist.meic.cm.bomberman.p2p.handler.ClientHandler;
import ist.meic.cm.bomberman.p2p.handler.GroupOwnerHandler;
import ist.meic.cm.bomberman.p2p.manager.Client;
import ist.meic.cm.bomberman.p2p.manager.Game;
import ist.meic.cm.bomberman.p2p.manager.Manager;
import ist.meic.cm.bomberman.p2p.manager.WiFiGlobal;
import ist.meic.cm.bomberman.settings.Settings;
import ist.meic.cm.bomberman.settings.SettingsActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WiFiServiceDiscoveryActivity extends Activity implements
		DeviceClickListener, ConnectionInfoListener {

	public static final String TAG = "bombermanDirect";

	// TXT RECORD properties
	public static final String AVAILABLE = "available";
	public static final String SERVICE_INSTANCE = "_bombermanHost";
	public static final String SERVICE_REG_TYPE = "_tcp";

	public static final int MESSAGE_READ = 10;
	public static final int MY_HANDLE = 20;
	private WifiP2pManager manager;

	public static final int SERVER_PORT = 4545;

	private final IntentFilter intentFilter = new IntentFilter();
	private Channel channel;
	private BroadcastReceiver receiver = null;
	private WifiP2pDnsSdServiceRequest serviceRequest;

	private WiFiDirectServicesList servicesList;

	private TextView statusTxtView;

	private Button quit;

	private Button host, join;

	private Thread handler = null;

	private static Button start;

	private boolean canStart;

	private boolean canJoin, canHost;

	private static String playerName;

	private static boolean canPlay;

	private static WaitTask waitToStart;

	private boolean isClient, started;

	private InetAddress address;

	private Button play;

	private WiFiGlobal global;

	private boolean starting;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		canStart = false;
		canHost = true;
		canJoin = true;
		isClient = false;
		started = false;
		canPlay = false;
		starting = false;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_p2p);
		statusTxtView = (TextView) findViewById(R.id.status_text);

		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		Intent intent = this.getIntent();
		playerName = intent.getStringExtra("player_name");

		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(this, getMainLooper(), null);

		global = WiFiGlobal.getInstance();

		global.setContext(WiFiServiceDiscoveryActivity.this);

		play = (Button) findViewById(R.id.PlayP2P);
		play.setOnClickListener(new OnClickListener() {

			private StartTask startTask;

			@Override
			public void onClick(View v) {

				if (!isClient) {
					ArrayList<Client> clients = global.getClients();
					if (clients.size() > 0) {
						if (!starting) {
							global.setManager(manager);
							global.setChannel(channel);
							((GroupOwnerHandler) handler).setRunning();

							startTask = new StartTask();
							startTask.execute();
							starting = true;
						} else
							Toast.makeText(WiFiServiceDiscoveryActivity.this,
									"Waiting to Play!", Toast.LENGTH_SHORT)
									.show();
					} else
						Toast.makeText(WiFiServiceDiscoveryActivity.this,
								"No clients connected!", Toast.LENGTH_SHORT)
								.show();

				} else if (isClient && canPlay) {
					if (!starting) {
						starting = true;
						Toast.makeText(WiFiServiceDiscoveryActivity.this,
								"Waiting to Play!", Toast.LENGTH_SHORT).show();
						waitToStart = new WaitTask();
						waitToStart.execute();
					} else
						Toast.makeText(WiFiServiceDiscoveryActivity.this,
								"Waiting to Play!", Toast.LENGTH_SHORT).show();
				} else if (isClient)
					Toast.makeText(WiFiServiceDiscoveryActivity.this,
							"Wait for the host to start the game!",
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(WiFiServiceDiscoveryActivity.this,
							"You can't play without starting!",
							Toast.LENGTH_SHORT).show();
			}

		});

		start = (Button) findViewById(R.id.StartP2P);
		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (canStart)

					if (started && isClient) {
						handler = new ClientHandler(playerName, address);
						handler.start();
					} else if (!started) {
						handler.start();
						started = true;
					} else
						Toast.makeText(WiFiServiceDiscoveryActivity.this,
								"You have already started!", Toast.LENGTH_SHORT)
								.show();
				else
					Toast.makeText(WiFiServiceDiscoveryActivity.this,
							"You can't start without having a connection!",
							Toast.LENGTH_SHORT).show();
			}

		});

		join = (Button) findViewById(R.id.Join);
		join.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (canJoin) {

					servicesList = new WiFiDirectServicesList();
					Fragment frag = getFragmentManager().findFragmentByTag(
							"services");
					if (frag != null) {
						getFragmentManager().beginTransaction().remove(frag)
								.commit();
					}
					getFragmentManager().beginTransaction()
							.add(R.id.container_root, servicesList, "services")
							.commit();
					canHost = false;
					discoverService();
				} else
					Toast.makeText(WiFiServiceDiscoveryActivity.this,
							"You are a host!", Toast.LENGTH_SHORT).show();
			}

		});

		host = (Button) findViewById(R.id.Host);
		host.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (canHost) {
					canJoin = false;
					startRegistration();
				} else
					Toast.makeText(WiFiServiceDiscoveryActivity.this,
							"You are a client!", Toast.LENGTH_SHORT).show();
			}

		});

		quit = (Button) findViewById(R.id.QuitP2P);
		quit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (manager != null && channel != null) {
					manager.removeGroup(channel, new ActionListener() {

						@Override
						public void onFailure(int reasonCode) {
							Log.d(TAG, "Disconnect failed. Reason :"
									+ reasonCode);
						}

						@Override
						public void onSuccess() {
						}

					});
				}
				Intent intent = new Intent(WiFiServiceDiscoveryActivity.this,
						ist.meic.cm.bomberman.Menu.class);
				startActivity(intent);
			}

		});
	}

	public static void tryToStart() {
		start.performClick();
	}

	@Override
	protected void onRestart() {
		Fragment frag = getFragmentManager().findFragmentByTag("services");
		if (frag != null) {
			getFragmentManager().beginTransaction().remove(frag).commit();
		}
		super.onRestart();
	}

	/**
	 * Registers a local service and then initiates a service discovery
	 */

	private void startRegistration() {
		Map<String, String> record = new HashMap<String, String>();
		record.put(AVAILABLE, "visible");

		WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
				SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
		manager.addLocalService(channel, service, new ActionListener() {

			@Override
			public void onSuccess() {
				appendStatus("Added Local Service");
				request();
			}

			@Override
			public void onFailure(int error) {
				appendStatus("Failed to add a service");
			}
		});

	}

	private void discoverService() {

		/*
		 * Register listeners for DNS-SD services. These are callbacks invoked
		 * by the system when a service is actually discovered.
		 */

		manager.setDnsSdResponseListeners(channel,
				new DnsSdServiceResponseListener() {

					@Override
					public void onDnsSdServiceAvailable(String instanceName,
							String registrationType, WifiP2pDevice srcDevice) {

						// A service has been discovered. Is this our app?

						if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {

							// update the UI and add the item the discovered
							// device.
							WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager()
									.findFragmentByTag("services");
							if (fragment != null) {
								WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment
										.getListAdapter());
								WiFiP2PService service = new WiFiP2PService();
								service.device = srcDevice;
								service.instanceName = instanceName;
								service.serviceRegistrationType = registrationType;
								adapter.add(service);
								adapter.notifyDataSetChanged();
								Log.d(TAG, "onBonjourServiceAvailable "
										+ instanceName);
							}
						}

					}
				}, new DnsSdTxtRecordListener() {

					/**
					 * A new TXT record is available. Pick up the advertised
					 * buddy name.
					 */
					@Override
					public void onDnsSdTxtRecordAvailable(
							String fullDomainName, Map<String, String> record,
							WifiP2pDevice device) {
						Log.d(TAG,
								device.deviceName + " is "
										+ record.get(AVAILABLE));
					}
				});

		request();
	}

	private void request() {
		// After attaching listeners, create a service request and initiate
		// discovery.
		serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
		manager.addServiceRequest(channel, serviceRequest,
				new ActionListener() {

					@Override
					public void onSuccess() {
						appendStatus("Added service discovery request");
					}

					@Override
					public void onFailure(int arg0) {
						appendStatus("Failed adding service discovery request");
					}
				});
		manager.discoverServices(channel, new ActionListener() {

			@Override
			public void onSuccess() {
				appendStatus("Service discovery initiated");
			}

			@Override
			public void onFailure(int arg0) {
				appendStatus("Service discovery failed");

			}
		});
	}

	@Override
	public void connectP2p(WiFiP2PService service) {
		WifiP2pConfig config = new WifiP2pConfig();
		if (canHost)
			config.groupOwnerIntent = 15;
		else
			config.groupOwnerIntent = 0;
		config.deviceAddress = service.device.deviceAddress;
		config.wps.setup = WpsInfo.PBC;
		if (serviceRequest != null)
			manager.removeServiceRequest(channel, serviceRequest,
					new ActionListener() {

						@Override
						public void onSuccess() {
						}

						@Override
						public void onFailure(int arg0) {
						}
					});

		manager.connect(channel, config, new ActionListener() {

			@Override
			public void onSuccess() {
				appendStatus("Connecting to service");
				System.out.println("connect P2P");

			}

			@Override
			public void onFailure(int errorCode) {
				appendStatus("Failed connecting to service");
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
		registerReceiver(receiver, intentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {

		/*
		 * The group owner accepts connections using a server socket and then
		 * spawns a client socket for every client. This is handled by {@code
		 * GroupOwnerSocketHandler}
		 */

		if (p2pInfo.isGroupOwner) {
			Log.d(TAG, "Connected as group owner");
			try {
				String prefs = getPrefs();
				global.setPrefs(prefs);
				handler = new GroupOwnerHandler(playerName, prefs);
				canStart = true;

				Toast.makeText(WiFiServiceDiscoveryActivity.this,
						"Connected as a Host!", Toast.LENGTH_SHORT).show();

			} catch (IOException e) {
				Log.d(TAG,
						"Failed to create a server thread - " + e.getMessage());
				return;
			}
		} else {
			Log.d(TAG, "Connected as peer");
			handler = new ClientHandler(playerName, p2pInfo.groupOwnerAddress);
			address = p2pInfo.groupOwnerAddress;
			canStart = true;
			isClient = true;
			Toast.makeText(WiFiServiceDiscoveryActivity.this,
					"Connected as a client!", Toast.LENGTH_SHORT).show();

		}
	}

	private String getPrefs() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(WiFiServiceDiscoveryActivity.this);

		StringBuilder builder = new StringBuilder();
		builder.append(prefs.getString(Settings.MAP, Settings.MAP_DEFAULT));

		builder.append(" ");
		builder.append(prefs.getString(Settings.DURATION,
				Settings.DURATION_DEFAULT));

		builder.append(" ");
		builder.append(prefs.getString(Settings.RS, Settings.RS_DEFAULT));

		builder.append(" ");
		builder.append(prefs.getString(Settings.ET, Settings.ET_DEFAULT));

		builder.append(" ");
		builder.append(prefs.getString(Settings.ED, Settings.ED_DEFAULT));

		builder.append(" ");
		builder.append(prefs.getString(Settings.ER, Settings.ER_DEFAULT));

		builder.append(" ");
		builder.append(prefs.getString(Settings.PR, Settings.PR_DEFAULT));

		builder.append(" ");
		builder.append(prefs.getString(Settings.PO, Settings.PO_DEFAULT));
		return builder.toString();
	}

	private void appendStatus(String status) {
		String current = statusTxtView.getText().toString();
		int len = current.length();
		int count = len - current.replace("\n", "").length();

		if (count > 2)
			current = current.substring(current.indexOf("\n") + 1, len);

		statusTxtView.setText(current + "\n" + status);
	}

	@Override
	public void onBackPressed() {
		quit.performClick();
	}

	public static void setPlayerName(String name) {
		playerName = name;
	}

	public static void setCanPlay(boolean play) {
		canPlay = play;
	}

	private class StartTask extends AsyncTask<Object, Void, Void> {

		private Message toSend = new Message(Message.FAIL), received;
		private boolean started = true;

		@Override
		protected Void doInBackground(Object... objects) {
			try {
				ObjectInputStream input;
				ObjectOutputStream output;
				Game game = global.getGame();
				ArrayList<Client> clients = global.getClients();
				for (Client current : clients) {
					input = current.getIn();
					received = (Message) input.readObject();

					if (received.getCode() == Message.READY)
						game.setReady(received.getPlayerID());
				}

				boolean[] ready = game.getReady();

				for (int i = 0; i < clients.size(); i++)
					if (!ready[i]) {
						started = false;
						break;
					}

				if (started) {
					toSend = new Message(Message.SUCCESS);
					for (Client current : clients) {
						output = current.getOut();
						output.writeObject(toSend);
						output.reset();
					}
				}

			} catch (IOException e) {
				started = false;
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			if (started)
				startGame(toSend);
			else
				notStarted();
		}

	}

	private class WaitTask extends AsyncTask<Object, Void, Void> {

		private Message toSend, received;
		private boolean started = true;

		@Override
		protected Void doInBackground(Object... objects) {
			try {

				toSend = new Message(Message.READY, global.getPlayerID());

				ObjectOutputStream output = global.getOutput();
				output.writeObject(toSend);
				output.reset();

				ObjectInputStream input = global.getInput();
				received = (Message) input.readObject();

			} catch (IOException e) {
				started = false;
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			if (started)
				startGame(received);
			else
				notStarted();
		}

	}

	private void startGame(Message received) {
		if (received.getCode() == Message.SUCCESS) {
			Intent i = new Intent(WiFiServiceDiscoveryActivity.this,
					InGame.class);

			startActivity(i);
		} else
			Toast.makeText(WiFiServiceDiscoveryActivity.this,
					"No other players are connected at the moment!",
					Toast.LENGTH_SHORT).show();
	}

	private void notStarted() {
		starting = false;
		Toast.makeText(getApplicationContext(),
				"TIMEOUT: Couldn't play the game!\nTry again!",
				Toast.LENGTH_SHORT).show();
	}
}
