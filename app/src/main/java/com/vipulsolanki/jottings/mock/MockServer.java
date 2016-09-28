package com.vipulsolanki.jottings.mock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.SimpleWebServer;

/***
 * This is a mock web-server to `simulate` api calls
 */
public class MockServer extends Service {

    private static final String TAG = "MockServer";

    private static final CharSequence URI_LIST_ALL_JOTTINGS = "/api/v1/jottings/list";
    private static final CharSequence URI_GET_JOTTING_1 = "/api/v1/jottings/1";
    private static final CharSequence URI_GET_JOTTING_2 = "/api/v1/jottings/2";
    private static final CharSequence URI_GET_JOTTING_3 = "/api/v1/jottings/3";
    private static final CharSequence URI_GET_JOTTING_4 = "/api/v1/jottings/4";
    private static final CharSequence URI_GET_JOTTING_5 = "/api/v1/jottings/5";
    private static final CharSequence URI_GET_JOTTING_6 = "/api/v1/jottings/6";
    private static final CharSequence URI_GET_JOTTING_7 = "/api/v1/jottings/7";
    private static final CharSequence URI_GET_JOTTING_8 = "/api/v1/jottings/8";
    private static final CharSequence URI_GET_JOTTING_9 = "/api/v1/jottings/9";

    private final Object lock = new Object();
    private boolean running;
    private SimpleWebServer server;
    private Thread serverThread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        synchronized (lock) {
            if (!running) {
                serverThread = new ServerThread();
                serverThread.start();

                running = true;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        synchronized (lock) {
            if (running) {
                if (serverThread != null) {
                    serverThread.interrupt();
                    serverThread = null;
                }

                if (server != null) {
                    server.stop();
                    server = null;
                }

                running = false;
            }
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        synchronized (lock) {
            if (!running) {
                serverThread = new ServerThread();
                serverThread.start();

                running = true;
            }
        }
        return new Binder();
    }

    private String getLocalAddress() throws SocketException {
        String resultIpv6 = "";
        String resultIpv4 = "";

        for (Enumeration en = NetworkInterface.getNetworkInterfaces();
             en.hasMoreElements();) {

            NetworkInterface intf = (NetworkInterface) en.nextElement();
            for (Enumeration enumIpAddr = intf.getInetAddresses();
                 enumIpAddr.hasMoreElements();) {

                InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                if(!inetAddress.isLoopbackAddress()){
                    if (inetAddress instanceof Inet4Address) {
                        resultIpv4 = inetAddress.getHostAddress();
                    } else if (inetAddress instanceof Inet6Address) {
                        resultIpv6 = inetAddress.getHostAddress();
                    }
                }
            }
        }
        return ((resultIpv4.length() > 0) ? resultIpv4 : resultIpv6);
    }

    private class ServerThread extends Thread {
        @Override
        public void run() {
            try {
                int port = 9998;
                String address = getLocalAddress();

                synchronized (lock) {
                    if (!running) {
                        return;
                    }

                    server = new JottingWebServer();
                }

                server.start(5000, true);

                Log.i(TAG, "Serving web-server at http://" + address + ":" + port);
            } catch (IOException e) {
                Log.e(TAG, "Error while serving", e);
                server.stop();
            }
        }
    }

    public class Binder extends android.os.Binder {
        public boolean isRunning() {
            synchronized (lock) {
                return running;
            }
        }

        @Nullable
        public String getLocalAddress() {
            synchronized (lock) {
                if (running && server != null) {
                    return server.getHostname() + ":" + server.getListeningPort();
                }
            }
            return null;
        }
    }

    /**
     * This is the place to server your static responses.
     */
    public class JottingWebServer extends SimpleWebServer {

        public JottingWebServer() throws SocketException {
            super(getLocalAddress(), 9998, new File(getApplicationInfo().dataDir), false);
        }

        @Override
        public Response serve(IHTTPSession session) {

            String uri = session.getUri();
            InputStream data;
            long noOfBytes;
            String response_file_name = null;

            if (uri.contains(URI_LIST_ALL_JOTTINGS)) {
                response_file_name = "list_all.json";
            } else if (uri.contains(URI_GET_JOTTING_1)) {
                response_file_name = "jotting_1.json";
            }

            if( !TextUtils.isEmpty(response_file_name)) {
                try {
                    data = getAssets().open(response_file_name);
                    noOfBytes = data.available();
                    return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", data, noOfBytes);
                } catch (IOException e) { e.printStackTrace(); }

            }

            return super.serve(session);
        }
    }


}
