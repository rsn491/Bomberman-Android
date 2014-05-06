package ist.meic.cm.bomberman.p2p;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import ist.meic.cm.bomberman.R;
import ist.meic.cm.bomberman.p2p.Manager.WiFiOperation;
import ist.meic.cm.bomberman.p2p.Manager.WiFiOperation.MessageTarget;
import ist.meic.cm.bomberman.p2p.WiFiDirectServicesList.DeviceClickListener;
import ist.meic.cm.bomberman.p2p.WiFiDirectServicesList.WiFiDevicesAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The main activity for the sample. This activity registers a local service and
 * perform discovery over Wi-Fi p2p network. It also hosts a couple of fragments
 * to manage chat operations. When the app is launched, the device publishes a
 * chat service and also tries to discover services published by other peers. On
 * selecting a peer published service, the app initiates a Wi-Fi P2P (Direct)
 * connection with the peer. On successful connection with a peer advertising
 * the same service, the app opens up sockets to initiate a chat.
 * {@code WiFiChatFragment} is then added to the the main activity which manages
 * the interface and messaging needs for a chat session.
 */
public class WiFiServiceDiscoveryActivity extends Activity implements
		DeviceClickListener, ConnectionInfoListener, Handler.Callback {

	public static final String TAG = "bombermanDirect";

	// TXT RECORD properties
	public static final String AVAILABLE = "available";
	public static final String SERVICE_INSTANCE = "_bomberman";
	public static final String SERVICE_REG_TYPE = "_tcp";

	public static final int MESSAGE_READ = 10;
	public static final int MY_HANDLE = 20;
	private WifiP2pManager manager;

	static final int SERVER_PORT = 4545;

	private final IntentFilter intentFilter = new IntentFilter();
	private Channel channel;
	private BroadcastReceiver receiver = null;
	private WifiP2pDnsSdServiceRequest serviceRequest;

	private Handler handler = new Handler(this);
	private WiFiOperation wifiMessage;
	private WiFiDirectServicesList servicesList;

	private TextView statusTxtView;

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		channel = manager.initialize(this, getMainLooper(), null);
		startRegistrationAndDiscovery();

		servicesList = new WiFiDirectServicesList();
		getFragmentManager().beginTransaction()
				.add(R.id.container_root, servicesList, "services").commit();

	}

	@Override
	protected void onRestart() {
		Fragment frag = getFragmentManager().findFragmentByTag("services");
		if (frag != null) {
			getFragmentManager().beginTransaction().remove(frag).commit();
		}
		super.onRestart();
	}

	@Override
	protected void onStop() {
		if (manager != null && channel != null) {
			manager.removeGroup(channel, new ActionListener() {

				@Override
				public void onFailure(int reasonCode) {
					Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
				}

				@Override
				public void onSuccess() {
				}

			});
		}
		super.onStop();
	}

	/**
	 * Registers a local service and then initiates a service discovery
	 */
	private void startRegistrationAndDiscovery() {
		Map<String, String> record = new HashMap<String, String>();
		record.put(AVAILABLE, "visible");

		WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
				SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
		manager.addLocalService(channel, service, new ActionListener() {

			@Override
			public void onSuccess() {
				appendStatus("Added Local Service");
			}

			@Override
			public void onFailure(int error) {
				appendStatus("Failed to add a service");
			}
		});

		discoverService();

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
			}

			@Override
			public void onFailure(int errorCode) {
				appendStatus("Failed connecting to service");
			}
		});
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MESSAGE_READ:
			// byte[] readBuf = (byte[]) msg.obj;
			// construct a string from the valid bytes in the buffer
			// String readMessage = new String(readBuf, 0, msg.arg1);
			// Log.d(TAG, readMessage);
			// (chatFragment).pushMessage("Buddy: " + readMessage);
			break;

		case MY_HANDLE:
			// Object obj = msg.obj;
			// (chatFragment).setChatManager((ChatManager) obj);

		}
		return true;
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
		Thread handler = null;
		/*
		 * The group owner accepts connections using a server socket and then
		 * spawns a client socket for every client. This is handled by {@code
		 * GroupOwnerSocketHandler}
		 */

		if (p2pInfo.isGroupOwner) {
			Log.d(TAG, "Connected as group owner");
			try {
				handler = new GroupOwnerHandler(
						((MessageTarget) this).getHandler());
				handler.start();
			} catch (IOException e) {
				Log.d(TAG,
						"Failed to create a server thread - " + e.getMessage());
				return;
			}
		} else {
			Log.d(TAG, "Connected as peer");
			handler = new ClientHandler(
					((MessageTarget) this).getHandler(),
					p2pInfo.groupOwnerAddress);
			handler.start();
		}

		Intent i = new Intent();

		i.putExtra("playerId", 1);
//Tem que passar os handlers? e mais qualquer coisa.
		setResult(2, i);

		// finish();
		setVisible(false);

		wifiMessage = new WiFiOperation();
		/*
		 * getFragmentManager().beginTransaction() .replace(R.id.container_root,
		 * chatFragment).commit();
		 */
		// statusTxtView.setVisibility(View.GONE);
	}

	public void appendStatus(String status) {
		String current = statusTxtView.getText().toString();
		statusTxtView.setText(current + "\n" + status);
	}
}
