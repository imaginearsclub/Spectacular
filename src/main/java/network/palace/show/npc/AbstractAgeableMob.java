package network.palace.show.npc;

import lombok.Setter;
import network.palace.show.pathfinding.Point;
import org.bukkit.entity.Player;


import java.util.Set;

public abstract class AbstractAgeableMob extends AbstractMob {

    @Setter private boolean baby = false;

    public AbstractAgeableMob(Point location, Set<Player> observers, String title) {
        super(location, observers, title);
    }

    @Override
    protected void onDataWatcherUpdate() {
        int babyIndex = 12;
        getDataWatcher().setObject(ProtocolLibSerializers.getBoolean(babyIndex), baby);
        super.onDataWatcherUpdate();
    }
}
