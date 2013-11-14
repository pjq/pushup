package me.pjq.pushup;

import android.util.Log;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.protocols.BARRIER;
import org.jgroups.protocols.FD_ALL;
import org.jgroups.protocols.FD_SOCK;
import org.jgroups.protocols.FRAG2;
import org.jgroups.protocols.MERGE2;
import org.jgroups.protocols.MFC;
import org.jgroups.protocols.PING;
import org.jgroups.protocols.UDP;
import org.jgroups.protocols.UFC;
import org.jgroups.protocols.UNICAST2;
import org.jgroups.protocols.VERIFY_SUSPECT;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.stack.ProtocolStack;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kicoolzhang on 11/12/13.
 */
public class PeersMgr {

    private static final String TAG = "PeersMgr";

    private static final String CHAT_CLUSTER = "WeUp";

    JChannel channel;
    String userName;
    boolean inLoop = false;

    WifiNetworkHelper networkHelper;

    ExecutorService senderExecutorService = Executors.newSingleThreadExecutor();
    ExecutorService controlerExecutorService = Executors.newSingleThreadExecutor();

    //TODO:
    // *. a better peer id
    // *. peers list
    // *. network state
    // *. session id for different group in a same localnetwork, maybe use different CHAT_CLUSTER?
    // *. Sync time
    // *. Sync action

    public PeersMgr(WifiNetworkHelper networkHelper) {
        this.networkHelper = networkHelper;
        userName = "U/";
    }

    public void start() {
        inLoop = true;

        try {
            initChannel();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        controlerExecutorService.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    channel.connect(CHAT_CLUSTER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        controlerExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                loop();
            }
        });

    }

    private void initChannel() throws Exception {
        String address = initUsername();

        channel = new JChannel(false);
        channel.setName(address);

        ProtocolStack stack = new ProtocolStack();
        channel.setProtocolStack(stack);

        stack.addProtocol(new UDP().setValue("bind_addr", InetAddress.getByName(address)))
                .addProtocol(new PING())
                .addProtocol(new MERGE2())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK())
                .addProtocol(new UNICAST2())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2());
        stack.init();

        channel.setReceiver(new Receiver());
    }

    private String initUsername() {
        WifiNetworkHelper.WifiNetworkInfo wifiInfo = networkHelper.getWifiInfo();
        String address = wifiInfo.getWifiIpAddress();

        Log.i(TAG, "Bind Address:" + address);
        userName = userName + address;
        return address;
    }

    private void loop() {
        while (inLoop) {

            if (channel.isConnected()) {
                Date data = new Date();

                sendMessage("NTP:" + data.getTime());

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void sendMessage(final String message) {
        senderExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String line = "[" + userName + "] " + message;
                    Message msg = new Message(null, null, line);
                    channel.send(msg);

                    Log.v(TAG, "Send: -> " + line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void stop() {
        inLoop = false;

        controlerExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                channel.close();
            }
        });

    }

    protected class Receiver extends ReceiverAdapter {

        @Override
        public void getState(OutputStream output) throws Exception {
            super.getState(output);
        }

        @Override
        public void setState(InputStream input) throws Exception {
            super.setState(input);
        }

        @Override
        public void suspect(Address mbr) {
            super.suspect(mbr);
        }

        @Override
        public void block() {
            super.block();
        }

        @Override
        public void unblock() {
            super.unblock();
        }

        @Override
        public void viewAccepted(View new_view) {
            Log.i(TAG, "** view: " + new_view);
        }

        @Override
        public void receive(Message msg) {
            Log.i(TAG, "Recv: -> " + msg.getObject());
        }

    }

}
