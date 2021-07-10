package network.palace.show.npc;

import lombok.Setter;
import network.palace.show.packets.AbstractPacket;
import network.palace.show.packets.server.entity.WrapperPlayServerSpawnEntityLiving;
import network.palace.show.pathfinding.Point;
import org.bukkit.entity.Player;


import java.util.Set;

/**
 * The Core-equivalent to spigot's LivingEntity
 */
public abstract class AbstractMob extends AbstractEntity {
    @Setter private float health = 0;

    public abstract float getMaximumHealth();

    public AbstractMob(Point location, Set<Player> observers, String title) {
        super(location, observers, title);
    }

    public final Float getHealth() {
        return health == 0 ? getMaximumHealth() : Math.min(getMaximumHealth(), health);
    }

    @Override
    protected AbstractPacket getSpawnPacket() {
        WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();
        packet.setEntityID(entityId);
        packet.setX(location.getX());
        packet.setY(location.getY());
        packet.setZ(location.getZ());
        packet.setYaw(location.getYaw());
        packet.setPitch(location.getPitch());
        packet.setHeadPitch(location.getYaw());
        updateDataWatcher();
        packet.setMetadata(dataWatcher);
        packet.setType(getEntityType());
        return packet;
    }

    @Override
    protected void onDataWatcherUpdate() {
        int healthIndex = 7;
        getDataWatcher().setObject(ProtocolLibSerializers.getFloat(healthIndex), getHealth());
    }
}
