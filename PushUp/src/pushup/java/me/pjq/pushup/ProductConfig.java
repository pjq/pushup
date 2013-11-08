package me.pjq.pushup;

/**
 * Configuration related to the product flavor claro
 */
public enum ProductConfig implements ProductConfigInterface{
    INSTANCE;

    @Override
    public String getProductFlavorName() {
        return "pushup";
    }
}
