package network.palace.show.actions;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ActionBarAction extends ShowAction {
    private String text;

    public ActionBarAction(Show show, long time) {
        super(show, time);
    }

    public ActionBarAction(Show show, long time, String text) {
        super(show, time);
        this.text = text;
    }

    @Override
    public void play(Player[] nearPlayers) {
        for (Player player : nearPlayers) {
            if (player == null) continue;
            if (Show.offset(player.getLocation(), show.getLocation()) < show.getRadius()) {
                player.sendActionBar(text);
            }
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        // 0 ActionBar text...
        StringBuilder text = new StringBuilder();
        for (int i = 2; i < args.length; i++) text.append(args[i]).append(" ");
        if (text.length() > 1) text = new StringBuilder(text.substring(0, text.length() - 1));
        this.text = ChatColor.translateAlternateColorCodes('&', text.toString());
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new ActionBarAction(show, time, text);
    }
}
