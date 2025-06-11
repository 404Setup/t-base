package one.tranic.t.base.command.source;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import one.tranic.t.base.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@SuppressWarnings("unused")
public abstract class SystemCommandSource<C, R> implements CommandSource<C, R> {
    private void unsupported() {
        throw new UnsupportedOperationException("Not supported by this command source");
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public String[] getArgs() {
        return new String[0];
    }

    @Override
    public int argSize() {
        return 0;
    }

    @Override
    public @Nullable Locale getLocale() {
        return Locale.getDefault();
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void showBossBar(@NotNull BossBar bossBar) {
        unsupported();
    }

    @Override
    public void hideBossBar(@NotNull BossBar bossBar) {
        unsupported();
    }

    @Override
    public void clearBossBars() {
        unsupported();
    }

    @Override
    public void showTitle(@NotNull Title title) {
        unsupported();
    }

    @Override
    public void clearTitle() {
        unsupported();
    }

    public abstract void broadcastMessage(@NotNull Component message);

    public abstract void broadcastMessage(@NotNull String message);

    @Override
    public @Nullable Player<R> asPlayer() {
        return null;
    }
}
