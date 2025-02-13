package network.palace.show.actions;

import lombok.Getter;
import lombok.Setter;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import org.bukkit.entity.Player;

@Getter
public abstract class ShowAction {
    protected Show show;
    protected long time;

    @Setter ShowAction next = null;

    public ShowAction(Show show, long time) {
        this.show = show;
        this.time = time;
    }

    public ShowAction(Show show, long time, Object o, Object o1) {
    }

    public abstract void play(Player[] nearPlayers);

    public abstract ShowAction load(String line, String... args) throws ShowParseException;

    protected abstract ShowAction copy(Show show, long time) throws ShowParseException;
}
