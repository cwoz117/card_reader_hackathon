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

	public void onTagDiscovered(Tag t){

		// Define APDU_COMMAND AID
		IsoDep connection = IsoDep.get(t);
		try {
			connection.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Send Command to remote device

		
		// Wait for a received response from remote device

		// forward received response to server.




		// Get the NDEFMessage from the Tag.
		Ndef n = Ndef.get(t);
		if (n == null)
			System.exit(0);		//TODO: Update how we want to exit.
		NdefMessage m = n.getCachedNdefMessage();

		// Get the records from the Message
		List<NdefRecord> l = Arrays.asList(m.getRecords());
		NdefRecord rec = l.get(1);

		// Send the byte data to the server.
		Server_com s = new Server_com("172.0.0.1", 65000);
		s.send(new String(rec.getPayload()));
	}
}
