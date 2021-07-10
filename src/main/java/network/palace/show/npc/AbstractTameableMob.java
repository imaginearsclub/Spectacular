package network.palace.show.npc;

import network.palace.show.pathfinding.Point;
import org.bukkit.entity.Player;


import java.util.Set;

public abstract class AbstractTameableMob extends AbstractAgeableMob {

    private boolean tame = false;
    private boolean sitting = false;
    private String ownerName = "Notch";

    public AbstractTameableMob(Point location, Set<Player> observers, String title, boolean tame, boolean sitting, String ownerName) {
        super(location, observers, title);

        this.tame = tame;
        this.sitting = sitting;
        this.ownerName = ownerName;
    }

    @Override
    protected void onDataWatcherUpdate() {
        int metadataIndex = 13;
        byte value = 0;
        if (sitting) value |= 0x01;
        if (tame) value |= 0x04;
        getDataWatcher().setObject(ProtocolLibSerializers.getByte(metadataIndex), value);
        if (ownerName == null) ownerName = "Notch";
        getDataWatcher().setObject(14, ownerName);
        super.onDataWatcherUpdate();
    }

    public void playHeartParticles() {
        playStatus(6);
    }

    public void playSmokeParticles() {
        playStatus(7);
    }

    public void playHeartParticles(Set<Player> players) {
        playStatus(players, 6);
    }

    public void playSmokeParticles(Set<Player> players) {
        playStatus(players, 7);
    }
}
