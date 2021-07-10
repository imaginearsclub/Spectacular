package network.palace.show.npc.mob;

import network.palace.show.npc.AbstractAmbient;
import network.palace.show.npc.ProtocolLibSerializers;
import network.palace.show.pathfinding.Point;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;

public class MobBat extends AbstractAmbient {
    private boolean awake = true;

    public MobBat(Point location, Set<Player> observers, String title) {
        super(location, observers, title);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityType.BAT;
    }

    @Override
    public float getMaximumHealth() {
        return 6f;
    }

    @Override
    protected void onDataWatcherUpdate() {
        getDataWatcher().setObject(ProtocolLibSerializers.getBoolean(13), awake);
        super.onDataWatcherUpdate();
    }

    public void setAwake(boolean b) {
        this.awake = b;
        getDataWatcher().setObject(ProtocolLibSerializers.getBoolean(13), b);
        updateDataWatcher();
    }

    public boolean isAwake() {
        return awake;
    }
}
