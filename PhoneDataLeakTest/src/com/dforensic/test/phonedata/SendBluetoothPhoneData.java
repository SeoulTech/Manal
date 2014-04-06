package com.dforensic.test.phonedata;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class SendBluetoothPhoneData implements ISendPhoneData {

	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	private BluetoothDevice mConnectedDev = null;
	private byte[] mOutputData = null;

	@Override
	public void sendFile(File file) {
		initBluetoothConnect();
		
		InputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			mOutputData = new byte[in.available()];
			in.read(mOutputData);
			
			new ConnectThread(mConnectedDev).run();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getInterfaceName() {
		return "bluetooth";
	}

	private void initBluetoothConnect() {
		if (mBluetoothAdapter != null) {
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
					.getBondedDevices();
			// If there are paired devices
			if (pairedDevices.size() > 0) {
				mConnectedDev = pairedDevices
						.toArray(new BluetoothDevice[pairedDevices.size()])[0];
			}
		}
	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				tmp = device.createRfcommSocketToServiceRecord(Constants.APP_UUID);
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			mBluetoothAdapter.cancelDiscovery();

			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();
			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				try {
					mmSocket.close();
				} catch (IOException closeException) {
				}
				return;
			}

			// Do work to manage the connection (in a separate thread)
			new ConnectedThread(mmSocket).run();
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmOutStream = tmpOut;
		}

		public void run() {
			write();
		}

		/* Call this from the main activity to send data to the remote device */
		public void write() {
			try {
				mmOutStream.write(mOutputData);
			} catch (IOException e) {
			}
		}

		/* Call this from the main activity to shutdown the connection */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

}
