package me.pjq.pushup;

public enum ApplicationConfig implements MyWordsConfigInterface {
    INSTANCE;

    public boolean DEBUG() {
        return true;
    }

    public boolean DEBUG_LOG() {
        return true;
    }

    public boolean API_DEV() {
        return false;
    }
}
