package network.palace.show.npc;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class IDManager {

    private int CURRENT = 2000;

    public int getNextID() {
        do {
            CURRENT++;
        } while (isDuplicate(CURRENT));
        return CURRENT;
    }

    private boolean isDuplicate(int id) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getEntityId() == id) return true;
            }
        }
        return false;
    }
}
