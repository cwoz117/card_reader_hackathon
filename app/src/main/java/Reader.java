import android.nfc.NfcAdapter;
import android.nfc.Tag;

/**
 * Reader handles the onTagDiscovered event prompted by another NFC device coming into range.
 */
public class Reader implements NfcAdapter.ReaderCallback{

	public void onTagDiscovered(Tag t){
		//TODO What does the reader ask of the emulator, and how?

	}
}
