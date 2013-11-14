package me.pjq.pushup.lan;

/**
 * Created by pengjianqing on 11/14/13.
 */
public class LanPlayer {
    String ip;
    String id;
    String username;

    String score;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getScore() {
        if (score == null) return "0";

        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
