package me.ryanpetschek.gatekeeper;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;

import java.util.Arrays;

public class MyHostApduService extends HostApduService {
    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        if (isSelectAidApdu(apdu)) {
            return "ACK".getBytes();
        }
        byte[] payload = Arrays.copyOfRange(apdu, 5, apdu.length);
        // Sign nonce with public key
        // TODO
        return payload;
    }
    private boolean isSelectAidApdu(byte[] apdu) {
        return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
    }

    @Override
    public void onDeactivated(int reason) {

    }
}