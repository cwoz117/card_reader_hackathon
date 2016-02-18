package com.hackathon.byteme.card_reader;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;

import com.hackathon.byteme.card_reader.Server_com;

import java.io.IOException;
import java.util.Arrays;

/**
 * com.hackathon.byteme.card_reader.Reader handles the onTagDiscovered event prompted by another NFC device coming into range.
 */
public class Reader implements NfcAdapter.ReaderCallback{
	private Server_com s;
	private static final String AID = "F222222222";
	private static final byte[] RECEIVED_OK = {(byte) 0x90, (byte)0x00};

	public Reader(String server, int port){
		new Server_com(server, port);
	}

	public void onTagDiscovered(Tag t){
		System.out.println("Heard shit from NFC");
		// Setup Connection with NFC Card.
		IsoDep connection = IsoDep.get(t);
		try {
			connection.connect();
			System.out.println("Connected to NFC");

			// Define APDU_COMMAND AID, and send to Tag. Wait for a reply.
			byte[] msg = formatApdu(AID);
			byte[] userCreds = connection.transceive(msg);

			System.out.println("Sent message, and received a reply:" + byteToHex(userCreds));
			byte[] status = {userCreds[userCreds.length-2],
			                     userCreds[userCreds.length-1]};
			byte[] payload = Arrays.copyOf(userCreds, userCreds.length-2);
			if (Arrays.equals(RECEIVED_OK, status)){
				// forward received response to server.
				System.out.println("Sending Payload");
				s.send(new String(payload));
				System.out.println("Received Payload");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] formatApdu(String a){
		return hexToByte("00A40400" + String.format("%02X", AID.length()) + a);
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
