package network.palace.show.npc;

import network.palace.show.pathfinding.Point;
import org.bukkit.entity.Player;


import java.util.Set;

public abstract class AbstractAnimal extends AbstractAgeableMob {

    public AbstractAnimal(Point location, Set<Player> observers, String title) {
        super(location, observers, title);
    }

    public void playMateAnimation() {
        playStatus(18);
    }

    public void playMateAnimation(Set<Player> players) {
        playStatus(players, 18);
    }
}
