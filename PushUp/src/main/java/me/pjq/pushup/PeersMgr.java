package me.pjq.pushup;

import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.internal.el;
import me.pjq.pushup.lan.LanPlayer;
import me.pjq.pushup.lan.LanUtils;
import me.pjq.pushup.utils.Utils;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kicoolzhang on 11/12/13.
 */
public class PeersMgr {

    private static final String TAG = "PeersMgr";

    private static final String CHAT_CLUSTER = "WeUp";
    private static final int DELAY_TIME = 1000 * 5;

    JChannel channel;
    String userName;
    boolean inLoop = false;
    String localIpAddress;

    List<Address> peersIpAddress = new ArrayList<Address>();

    public Map<String, LanPlayer> getPeers() {
        return peers;
    }

    Map<String, LanPlayer> peers = new HashMap<String, LanPlayer>();

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
        WifiNetworkHelper.WifiNetworkInfo wifiInfo = networkHelper.getWifiInfo();
        localIpAddress = wifiInfo.getWifiIpAddress();

        String address = initUsername();

        channel = new JChannel(false);
        channel.setName(address);

        ProtocolStack stack = new ProtocolStack();
        channel.setProtocolStack(stack);

        stack.addProtocol(new UDP().setValue("bind_addr", InetAddress.getByName(localIpAddress)))
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
        String name = AppPreference.getInstance(MyApplication.getContext()).getLoginName();

        if (!TextUtils.isEmpty(name)) {
            userName = name;
        } else {
            userName = Utils.getDeviceModel();
        }

        return userName;
    }

    private void loop() {
        while (inLoop) {

            if (channel.isConnected()) {
                Date data = new Date();

                sendNtp(data.getTime());

                try {
                    Thread.sleep(DELAY_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    protected void sendMessage(final String message) {
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

    public void sendCount(final int count) {
        String line = "COUNT:" + count;
        sendMessage(line);
    }

    public void sendNtp(final long time) {
        String line = "NTP:" + time;
        sendMessage(line);
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

            int peersNumber = new_view.size();
            if (peersNumber > 1) {
                List<Address> members = new_view.getMembers();
                peersIpAddress.clear();
                peers.clear();

                for (Address a : members) {
                    String addr = ((Object) a).toString();
                    Log.i(TAG, "members:" + addr);

                    if (!addr.equalsIgnoreCase(localIpAddress)) {
                        peersIpAddress.add(a);
                        LanPlayer lanPlayer = new LanPlayer();
                        lanPlayer.setIp(addr);
                        lanPlayer.setUsername(channel.getName(a));
                        lanPlayer.setId(channel.getName(a));
                        peers.put(addr, lanPlayer);
                    }
                }

                LanUtils.sendUpdatePlayerInfoMsg();
            }

        }

        @Override
        public void receive(Message msg) {

            String srcAddr = ((Object) msg.getSrc()).toString();
            if (srcAddr.equalsIgnoreCase(localIpAddress)) {
                return;
            }

            Log.v(TAG, "Recv: -> " + msg.getObject());

            String line = msg.getObject().toString();
            if (line.contains("COUNT")) {
                String messages[] = line.split(":");
                int count = Integer.valueOf(messages[1]);
                Log.i(TAG, "COUNT: -> " + count);


                LanPlayer lanPlayer = peers.get(srcAddr);
                lanPlayer.setScore(messages[1]);

                LanUtils.sendUpdatePlayerInfoMsg();
            } else if (line.contains("NTP")) {
                String messages[] = line.split(":");
                long time = Long.valueOf(messages[1]);
                Log.i(TAG, "NTP: -> " + time);

                LanPlayer lanPlayer = peers.get(srcAddr);
                String score = lanPlayer.getScore();
                if (score == null || score.length() == 0) score = "0";
                int fake = Integer.valueOf(score) + 1;
                lanPlayer.setScore(Integer.valueOf(fake).toString());

                Log.i(TAG, "fake: -> " + srcAddr + ":" + fake);

                LanUtils.sendUpdatePlayerInfoMsg();
            }
        }
    }

}
