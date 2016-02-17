import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.NdefMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * Reader handles the onTagDiscovered event prompted by another NFC device coming into range.
 */
public class Reader implements NfcAdapter.ReaderCallback{

	public void onTagDiscovered(Tag t){
		Ndef n = Ndef.get(t);
		if (n == null)
			System.exit(0);
		NdefMessage m = n.getCachedNdefMessage();

		List<NdefRecord> l = Arrays.asList(m.getRecords());
		NdefRecord rec = l.get(1);

		User user = (User) rec.getPayload();

		Server_com s = new Server_com("172.0.0.1", 65000);
		s.send(user);

	}
}
