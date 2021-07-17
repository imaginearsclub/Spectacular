package network.palace.show;

import network.palace.show.handlers.Fountain;
import network.palace.show.utils.ShowUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FountainManager implements Listener {
    public List<Fountain> fountains = new ArrayList<>();

    public FountainManager() {
        start();
    }

    @SuppressWarnings("deprecation")
    private void start() {
        Bukkit.getScheduler().runTaskTimer(ShowPlugin.getInstance(), () -> {
            for (Fountain fon : new ArrayList<>(fountains)) {
                double duration = fon.getDuration();
                fon.setDuration(duration - 0.2);
                if (duration <= 0) {
                    fountains.remove(fon);
                    continue;
                }
                Location loc = fon.getLocation();
                Material mat = fon.getMat();
                Vector force = fon.getForce();
                FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, mat, (byte) 0);
                Bukkit.getLogger().info(String.valueOf(mat));
                fb.setVelocity(force);
                fb.setMetadata("dontplaceblock", new FixedMetadataValue(ShowPlugin.getInstance(), true));
            }
        }, 0L, 4L);
    }

    @EventHandler
    public void entityToBlock(EntityChangeBlockEvent event) {
        Entity e = event.getEntity();
        if (e.hasMetadata("dontplaceblock") && e.getMetadata("dontplaceblock").get(0).asBoolean()) {
            event.setCancelled(true);
            e.remove();
        }
    }

    public void addFountain(Fountain fountain) {
        fountains.add(fountain);
    }
}