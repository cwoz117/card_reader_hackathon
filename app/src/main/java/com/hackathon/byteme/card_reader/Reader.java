package com.hackathon.byteme.card_reader;

import android.app.Fragment;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;

import com.hackathon.byteme.card_reader.Server_com;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * com.hackathon.byteme.card_reader.Reader handles the onTagDiscovered event prompted by another NFC device coming into range.
 */
public class Reader implements NfcAdapter.ReaderCallback{
	private String ip;
	private int port;
	MainActivity ptr;
	private static final String AID = "F222222222";
	private static final byte[] RECEIVED_OK = {(byte) 0x90, (byte)0x00};

	// Weak reference to prevent retain loop. mAccountCallback is responsible for exiting
	// foreground mode before it becomes invalid (e.g. during onPause() or onStop()).
	private WeakReference<AccountCallback> mAccountCallback;

	public interface AccountCallback {
		public void onAccountReceived(String account);
	}
	public void setServerInfo(String s, int i){
		ip = s;
		port = i;
	}
	public Reader(AccountCallback a) {
		mAccountCallback = new WeakReference<AccountCallback>(a);
	}
	public void onTagDiscovered(Tag t){
		Thread thr;
		//TODO Can be called before ip/port have any values.
		Server_com s = new Server_com(ip, port);
		System.out.println("Heard shit from NFC");
		// Setup Connection with NFC Card.
		IsoDep connection = IsoDep.get(t);
		try {
			connection.connect();
			System.out.println("Connected to NFC");

			// Define APDU_COMMAND AID, and send to Tag. Wait for a reply.
			byte[] msg = formatApdu(AID);
			System.out.println(byteToHex(msg));
			byte[] userCreds = connection.transceive(msg);
			connection.close();
			System.out.println("Sent message should be: " + byteToHex(RECEIVED_OK));
			System.out.println("Sent message, and received a reply:" + byteToHex(userCreds));
			byte[] status = {userCreds[userCreds.length-2],
			                     userCreds[userCreds.length-1]};
			byte[] payload = Arrays.copyOf(userCreds, userCreds.length-2);
			if (Arrays.equals(RECEIVED_OK, status)){
				// forward received response to server.
				System.out.println("Sending Payload" + byteToHex(payload));
				s.setData(new String(payload));
				thr = new Thread(s);
				thr.start();


			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] formatApdu(String a){
		return hexToByte("00A40400" + String.format("%02X", (a.length()/2)) + a);
	}

	private byte[] hexToByte(String s){
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
			+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	private String byteToHex(byte[] b){
		final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		char[] hexChars = new char[b.length * 2];
		int v;
		for ( int j = 0; j < b.length; j++ ) {
			v = b[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
