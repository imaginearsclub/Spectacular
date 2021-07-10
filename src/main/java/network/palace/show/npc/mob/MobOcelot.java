package network.palace.show.npc.mob;

import network.palace.show.npc.AbstractAnimal;
import network.palace.show.pathfinding.Point;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;

public class MobOcelot extends AbstractAnimal {

    public MobOcelot(Point location, Set<Player> observers, String title) {
        super(location, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.OCELOT;
    }

    @Override
    public float getMaximumHealth() {
        return 1f;
    }
}
