package me.pjq.pushup.lan;

import me.pjq.pushup.MyApplication;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by pengjianqing on 11/14/13.
 */
public class LanPlayerHelper {

    private static ArrayList<LanPlayer> players;

    public synchronized static ArrayList<LanPlayer> getLanPlayers() {

        Map<String, LanPlayer> peers = MyApplication.getPeersMgr().getPeers();

        players = new ArrayList<LanPlayer>();
        for (Map.Entry<String, LanPlayer> entry : peers.entrySet()) {
            players.add(entry.getValue());
        }
        return players;
    }

}
