package network.palace.show.npc.mob;

import network.palace.show.npc.AbstractAnimal;
import network.palace.show.pathfinding.Point;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;

public class MobChicken extends AbstractAnimal {

    public MobChicken(Point location, Set<Player> observers, String title) {
        super(location, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.CHICKEN;
    }

    @Override
    public float getMaximumHealth() {
        return 4f;
    }
}
