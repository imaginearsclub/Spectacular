package network.palace.show.actions.armor;

import network.palace.show.Show;
import network.palace.show.actions.ShowAction;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.armorstand.ShowStand;
import network.palace.show.utils.ShowUtil;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ArmorStandDespawn extends ShowAction {
    private static final String SHOW_STAND_CANNOT_BE_NULL = "ShowStand cannot be null";
    private final ShowStand stand;

    /**
     * Constructs a new ArmorStandDespawn action.
     *
     * @param show  The show this action belongs to.
     * @param time  The time this action occurs.
     * @param stand The ShowStand to despawn (cannot be null).
     */
    public ArmorStandDespawn(Show show, long time, ShowStand stand) {
        super(show, time);
        if (stand == null) {
            throw new IllegalArgumentException(SHOW_STAND_CANNOT_BE_NULL);
        }
        this.stand = stand;
    }

    /**
     * Verifies if the ArmorStand is valid and logs any issues.
     *
     * @return True if the ArmorStand can be processed, false otherwise.
     */
    private boolean logAndVerifyStand() {
        if (!stand.isHasSpawned()) {
            ShowUtil.logDebug(show.getName(), "ArmorStand with ID " + stand.getId() + " has not spawned");
            return false;
        }
        if (stand.getStand() == null) {
            ShowUtil.logDebug(show.getName(), "ArmorStand with ID " + stand.getId() + " is null");
            return false;
        }
        return true;
    }

    /**
     * Despawns the armor stand, if spawned.
     *
     * @param players The players near the action (currently unused).
     */
    @Override
    public void play(Player[] players) {
        synchronized (stand) {
            if (!logAndVerifyStand()) {
                return;
            }
            // Safely handle ArmorStand using Optional for better readability.
            Optional.ofNullable(stand.getStand()).ifPresent(ArmorStand::remove);
            ShowUtil.logDebug(show.getName(), "Despawned ArmorStand with ID " + stand.getId());
            stand.setStand(null);
            stand.despawn();
        }
    }

    /**
     * Loads a new instance of this action from a serialized string.
     *
     * @param line The serialized data line
     * @param args Additional arguments for initialization
     * @return A new instance of ArmorStandDespawn
     * @throws ShowParseException If parsing the data fails
     */
    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        if (line == null || args == null) {
            throw new ShowParseException("Invalid data for ArmorStandDespawn load");
        }

        ShowUtil.logDebug(show.getName(), "Loading ArmorStandDespawn action from line " + line);
        return new ArmorStandDespawn(show, time, stand);
    }

    /**
     * Creates a copy of this action with the same behavior for a different Show.
     *
     * @param show The new Show object.
     * @param time The scheduled time for the copy.
     * @return A new ArmorStandDespawn with the same behavior.
     * @throws ShowParseException If there is an error during copying.
     */
    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        // Defensive copying of ShowStand, if possible (assuming ShowStand has a clone() method)
        ShowStand clonedStand = stand.clone(); // Use the public clone method
        return new ArmorStandDespawn(show, time, clonedStand);
    }
}