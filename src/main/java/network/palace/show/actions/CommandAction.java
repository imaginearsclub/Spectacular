package network.palace.show.actions;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandAction extends ShowAction {
    private String command;

    public CommandAction(Show show, long time) {
        super(show, time);
    }

    public CommandAction(Show show, long time, String command) {
        super(show, time);
        this.command = command;
    }

    @Override
    public void play(Player[] nearPlayers) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        // 0 Command tp @a ~ ~5 ~
        StringBuilder text = new StringBuilder();
        for (int i = 2; i < args.length; i++) text.append(args[i]).append(" ");
        if (text.length() > 1) text = new StringBuilder(text.substring(0, text.length() - 1));
        this.command = text.toString();
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new CommandAction(show, time, command);
    }
}
