package network.palace.show.npc.mob;

import network.palace.show.npc.AbstractAnimal;
import network.palace.show.pathfinding.Point;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;

public class MobWolf extends AbstractAnimal {

    public MobWolf(Point location, Set<Player> observers, String title) {
        super(location, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.WOLF;
    }

    @Override
    public float getMaximumHealth() {
        return 10f;
    }
}
