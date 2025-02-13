package network.palace.show.handlers.armorstand;

import lombok.Getter;
import lombok.Setter;
import network.palace.show.handlers.ArmorData;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 10/11/15
 */
public class ShowStand implements Cloneable {
    @Getter private String id;
    @Getter private boolean small;
    @Getter private ArmorData armorData;
    @Getter private boolean hasSpawned = false;
    @Getter @Setter private ArmorStand stand;
    @Getter @Setter private Movement movement;
    private List<Position> positions = new ArrayList<>();
    @Getter @Setter private Rotation rotation;

    public ShowStand(String id, boolean small, ArmorData armorData) {
        this.id = id;
        this.small = small;
        this.armorData = armorData;
        this.hasSpawned = false;
    }

    public void spawn() {
        hasSpawned = true;
    }

    public void addPosition(Position position) {
        this.positions.add(position);
    }

    public void removePosition(Position position) {
        this.positions.remove(position);
    }

    public List<Position> getPositions() {
        return new ArrayList<>(positions);
    }

    public void despawn() {
        hasSpawned = false;
    }

    @Override
    public ShowStand clone() {
        try {
            ShowStand cloned = (ShowStand) super.clone();
            // Perform deep copying if necessary for mutable fields
            cloned.stand = null; // Avoid copying ArmorStand reference (optional)
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // This can't happen if implementing Cloneable
        }
    }
}