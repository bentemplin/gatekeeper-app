package me.ryanpetschek.gatekeeper;

import android.content.SharedPreferences;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import org.bitcoinj.core.Sha256Hash;

import java.math.BigInteger;
import java.util.Arrays;

public class MyHostApduService extends HostApduService {
    final SharedPreferences settings = getSharedPreferences("GK_settings", this.MODE_MULTI_PROCESS);

    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        if (isSelectAidApdu(apdu)) {
            return "ACK".getBytes();
        }
        byte[] payload = Arrays.copyOfRange(apdu, 5, apdu.length);
        // Sign nonce with public key
        //String serializedPrivateKey = settings.getString("privateKey", "");
        //if (serializedPrivateKey.length() == 0) {
            // Private key not available for some reason so return incorrect response
            //return new byte[] { (byte)0xFF };
        //}
        return new byte[] { 0x00, 0x00, 0x00, 0x00 };
        /*
        ECKey key = new ECKey(new BigInteger(serializedPrivateKey));
        byte[] hashedPayload = Sha256Hash.hash(payload);
        byte[] signature = key.sign(hashedPayload);
        int pubKeyLength = key.getPubKey().length;
        byte[] response = new byte[1 + pubKeyLength + signature.length];
        response[0] = (byte)pubKeyLength;
        System.arraycopy(key.getPubKey(), 0, response, 1, pubKeyLength);
        System.arraycopy(signature, 0, response, 1 + pubKeyLength, signature.length);
        return response;*/
    }
    private boolean isSelectAidApdu(byte[] apdu) {
        return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
    }

    @Override
    public void onDeactivated(int reason) {

    }
}