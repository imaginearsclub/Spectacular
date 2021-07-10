package network.palace.show.npc.entity;

import lombok.Getter;
import network.palace.show.npc.AbstractEntity;
import network.palace.show.packets.AbstractPacket;
import network.palace.show.packets.server.entity.WrapperPlayServerSpawnEntity;
import network.palace.show.pathfinding.Point;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class EntityFallingBlock extends AbstractEntity {
    @Getter private final int typeId;
    @Getter private final byte data;

    public EntityFallingBlock(Point location, Set<Player> observers, String title, int typeId, byte data) {
        super(location, observers, title);
        this.typeId = typeId;
        this.data = data;
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.FALLING_BLOCK;
    }

    @Override
    protected AbstractPacket getSpawnPacket() {
        WrapperPlayServerSpawnEntity wrapper = new WrapperPlayServerSpawnEntity();
        wrapper.setType(WrapperPlayServerSpawnEntity.ObjectTypes.FALLING_BLOCK);
        wrapper.setEntityID(entityId);
        wrapper.setUniqueId(UUID.randomUUID());
        wrapper.setX(location.getX());
        wrapper.setY(location.getY());
        wrapper.setZ(location.getZ());
        wrapper.setObjectData(typeId | data << 12);
        return wrapper;
    }
}
