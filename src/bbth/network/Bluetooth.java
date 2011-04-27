package bbth.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import bbth.core.GameActivity;

public class Bluetooth {

	private static final int BLUETOOTH_ENABLED = Activity.RESULT_FIRST_USER;
	private static final int BLUETOOTH_DISCOVERABLE = Activity.RESULT_FIRST_USER + 1;
	private static final int LISTEN_DELAY_IN_SECONDS = 300;

	private static abstract class StateBase {
		public abstract State getState();

		public void transitionIn() {
		}

		public void transitionOut() {
		}
	}

	private class EnableBluetoothState extends StateBase {
		public State getState() {
			return State.ENABLE_BLUETOOTH;
		}

		public StateBase nextState;

		@Override
		public void transitionIn() {
			if (bluetooth != null) {
				if (!bluetooth.isEnabled()) {
					context.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BLUETOOTH_ENABLED);
				} else {
					Bluetooth.this.transition(nextState);
				}
			}
		}
	}

	private class ListDevicesState extends StateBase {
		public State getState() {
			return State.LIST_DEVICES;
		}

		private BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					CONNECT_TO_EACH_DEVICE.devices.add((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
					transition(CONNECT_TO_EACH_DEVICE);
				}
			}
		};

		@Override
		public void transitionIn() {
			CONNECT_TO_EACH_DEVICE.devices.clear();
			context.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
			context.registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
			bluetooth.startDiscovery();
		}

		@Override
		public void transitionOut() {
			bluetooth.cancelDiscovery();
			context.unregisterReceiver(receiver);
		}
	}

	private class ConnectToEachDeviceState extends StateBase {
		public State getState() {
			return State.CONNECT_TO_EACH_DEVICE;
		}

		public ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
		private BluetoothSocket socket;

		@Override
		public void transitionIn() {
			connectToNextDevice();
		}

		@Override
		public void transitionOut() {
			closeSocket();
		}

		private void connectToNextDevice() {
			closeSocket();
			if (devices.isEmpty()) {
				transition(DISCONNECTED);
			} else {
				connectToDevice(devices.get(0));
				devices.remove(0);
			}
		}

		private void closeSocket() {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}

		private void connectToDevice(BluetoothDevice device) {
			bluetooth.cancelDiscovery();
			closeSocket();
			try {
				socket = device.createRfcommSocketToServiceRecord(protocol.getUUID());
				new Thread() {
					@Override
					public void run() {
						try {
							socket.connect();
							CONNECTED.socket = socket;
							socket = null;
							transition(CONNECTED);
						} catch (IOException e) {
							connectToNextDevice();
						}
					}
				}.start();
			} catch (IOException e) {
				connectToNextDevice();
			}
		}
	}

	private class ConnectedState extends StateBase {
		public State getState() {
			return State.CONNECTED;
		}

		public BluetoothSocket socket;
		private Thread readingThread;
		private Thread writingThread;

		@Override
		public void transitionIn() {
			packetsToBeWritten.clear();

			// create a reading thread
			readingThread = new Thread() {
				@Override
				public void run() {
					if (socket != null) {
						try {
							DataInputStream inputStream = new DataInputStream(socket.getInputStream());
							while (true) {
								try {
									packetsToBeRead.put(protocol.readPacket(inputStream));
								} catch (InterruptedException e) {
									break;
								}
							}
						} catch (IOException e) {
						}
					}
					if (state == CONNECTED) {
						transition(DISCONNECTED);
					}
				}
			};
			readingThread.start();

			// create a writing thread to write everything in packetsToBeWritten
			writingThread = new Thread() {
				@Override
				public void run() {
					if (socket != null) {
						try {
							DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
							while (true) {
								try {
									packetsToBeWritten.take().write(outputStream);
									outputStream.flush();
								} catch (InterruptedException e) {
									break;
								}
							}
						} catch (IOException e) {
						}
					}
					if (state == CONNECTED) {
						transition(DISCONNECTED);
					}
				}
			};
			writingThread.start();
		}

		@Override
		public void transitionOut() {
			if (readingThread != null) {
				readingThread.interrupt();
			}
			if (writingThread != null) {
				writingThread.interrupt();
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private class ListenForConnectionsState extends StateBase {
		public State getState() {
			return State.LISTEN_FOR_CONNECTIONS;
		}

		private BluetoothServerSocket socket;
		private BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)
						&& intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR) != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
					transition(DISCONNECTED);
				}
			}
		};

		@Override
		public void transitionIn() {
			// disconnect after trying for the timeout period
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, LISTEN_DELAY_IN_SECONDS);
			context.registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
			context.startActivityForResult(intent, BLUETOOTH_DISCOVERABLE);

			// try to accept a connection
			try {
				socket = bluetooth.listenUsingRfcommWithServiceRecord("THIS POINTLESS STRING DOESN'T DO ANYTHING", protocol.getUUID());
				new Thread() {
					@Override
					public void run() {
						try {
							while (true) {
								CONNECTED.socket = socket.accept();
								if (CONNECTED.socket != null) {
									transition(CONNECTED);
									break;
								}
							}
						} catch (IOException e) {
							transition(DISCONNECTED);
						}
					}
				}.start();
			} catch (IOException e) {
				transition(DISCONNECTED);
			}
		}

		@Override
		public void transitionOut() {
			// BUG: android will segfault if we close the server socket
			// immediately and then use the accepted socket afterwards (which is
			// what the docs tell you to do)

			// if (socket != null) {
			// try {
			// socket.close();
			// } catch (IOException e) {
			// }
			// }
		}
	}

	private class DisconnectedState extends StateBase {
		public State getState() {
			return State.DISCONNECTED;
		}
	}

	private final DisconnectedState DISCONNECTED = new DisconnectedState();
	private final EnableBluetoothState ENABLE_BLUETOOTH = new EnableBluetoothState();
	private final ListenForConnectionsState LISTEN_FOR_CONNECTIONS = new ListenForConnectionsState();
	private final ListDevicesState LIST_DEVICES = new ListDevicesState();
	private final ConnectToEachDeviceState CONNECT_TO_EACH_DEVICE = new ConnectToEachDeviceState();
	private final ConnectedState CONNECTED = new ConnectedState();

	private Protocol protocol;
	private GameActivity context;
	private StateBase state = DISCONNECTED;
	private BlockingQueue<Packet> packetsToBeRead = new LinkedBlockingQueue<Packet>();
	private BlockingQueue<Packet> packetsToBeWritten = new LinkedBlockingQueue<Packet>();
	private BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

	public Bluetooth(GameActivity context) {
		this.context = context;
	}

	protected void transition(StateBase newState) {
		state.transitionOut();
		state = newState;
		state.transitionIn();
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public State getState() {
		return state.getState();
	}

	public void listen() {
		if (state == DISCONNECTED) {
			ENABLE_BLUETOOTH.nextState = LISTEN_FOR_CONNECTIONS;
			transition(ENABLE_BLUETOOTH);
		}
	}

	public void connect() {
		if (state == DISCONNECTED) {
			ENABLE_BLUETOOTH.nextState = LIST_DEVICES;
			transition(ENABLE_BLUETOOTH);
		}
	}

	public void disconnect() {
		transition(DISCONNECTED);
	}

	public Packet read() {
		return packetsToBeRead.poll();
	}

	public void write(Packet packet) {
		if (state == CONNECTED) {
			try {
				packetsToBeWritten.put(packet);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Stupid method necessary because of android's weird context/activity mess.
	 */
	public void stupidTrampolineMethod(int requestCode, int resultCode) {
		if (state == ENABLE_BLUETOOTH && requestCode == BLUETOOTH_ENABLED && resultCode == Activity.RESULT_OK) {
			transition(ENABLE_BLUETOOTH.nextState);
		} else if (state == LISTEN_FOR_CONNECTIONS && requestCode == BLUETOOTH_DISCOVERABLE && resultCode == Activity.RESULT_CANCELED) {
			transition(DISCONNECTED);
		}
	}
}
