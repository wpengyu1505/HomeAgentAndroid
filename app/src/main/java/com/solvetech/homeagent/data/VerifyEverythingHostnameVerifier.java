package com.solvetech.homeagent.data;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by wpy on 9/27/15.
 */
public class VerifyEverythingHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String string, SSLSession sslSession) {
        return true;
    }
}
