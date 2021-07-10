package network.palace.show.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.show.Show;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Marc on 1/15/17.
 */
@AllArgsConstructor
public class GlowDoneEvent extends Event {
    @Getter private Show show;
    private static final HandlerList handlers = new HandlerList();


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }
}
