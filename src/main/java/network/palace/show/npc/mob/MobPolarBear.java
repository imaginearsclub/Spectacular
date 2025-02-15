package network.palace.show.npc.mob;

import network.palace.show.npc.AbstractAnimal;
import network.palace.show.pathfinding.Point;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * @author Innectic
 * @since 3/18/2017
 */
public class MobPolarBear extends AbstractAnimal {
    public MobPolarBear(Point point, Set<Player> observers, String title) {
        super(point, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.POLAR_BEAR;
    }

    @Override
    public float getMaximumHealth() {
        return 30;
    }
}
