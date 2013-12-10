package me.pjq.pushup.lan;

import me.pjq.pushup.ServiceProvider;
import me.pjq.pushup.msg.MsgUpdatePlayer;

/**
 * Created by pengjianqing on 11/14/13.
 */
public class LanUtils {
    public static void sendUpdatePlayerInfoMsg() {
        MsgUpdatePlayer msgUpdatePlayer = new MsgUpdatePlayer();
        ServiceProvider.getBus().post(msgUpdatePlayer);
    }

}
