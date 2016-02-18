import android.nfc.FormatException;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.NdefMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Reader handles the onTagDiscovered event prompted by another NFC device coming into range.
 */
public class Reader implements NfcAdapter.ReaderCallback{

	private static final String AID = "ABCDEFABCD";
	private static final byte[] RECEIVED_OK = {(byte) 0x90, (byte)0x00};

	public Reader(){

	}

	public void onTagDiscovered(Tag t){

		// Setup Connection with NFC Card.
		IsoDep connection = IsoDep.get(t);
		try {
			connection.connect();

			// Define APDU_COMMAND AID
			byte[] msg = formatApdu(AID);

			// Send Command to remote device and
			// wait for a received response from remote device
			byte[] userCreds = connection.transceive(msg);
			byte[] status = {userCreds[userCreds.length-2],
			                     userCreds[userCreds.length-1]};
			byte[] payload = Arrays.copyOf(userCreds, userCreds.length-2);
			if (Arrays.equals(RECEIVED_OK, status)){
				// forward received response to server.
				Server_com s = new Server_com("172.0.0.1", 65000);
				s.send(new String(payload));
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
