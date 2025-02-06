package one.tranic.t.base.hook;

public class ViaVersion {
    public static final boolean isEnabled;

    static {
        boolean s;
        s = false;
        try {
            Class.forName(" com.viaversion.viaversion.api.Via");
            s = true;
        } catch (ClassNotFoundException ignored) {
        }
        isEnabled = s;
    }
}
