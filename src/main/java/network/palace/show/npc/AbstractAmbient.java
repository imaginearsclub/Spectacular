package network.palace.show.npc;

import network.palace.show.pathfinding.Point;
import org.bukkit.entity.Player;


import java.util.Set;

public abstract class AbstractAmbient extends AbstractMob {

    public AbstractAmbient(Point location, Set<Player> observers, String title) {
        super(location, observers, title);
    }
}
