package network.palace.show.actions.armor;

import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.actions.ShowAction;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.armorstand.Movement;
import network.palace.show.handlers.armorstand.ShowStand;
import network.palace.show.handlers.armorstand.StandAction;
import network.palace.show.utils.ShowUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ArmorStandMove extends ShowAction {
    private static final int TICKS_PER_SECOND = 20; // 20 ticks per second
    private final ShowStand stand;
    private final Location loc;
    private final double speed;

    /**
     * Constructor for the ArmorStandMove action.
     *
     * @param show  The show this action belongs to.
     * @param time  The time in ticks at which this action will execute.
     * @param stand The target ArmorStand.
     * @param loc   The destination location for the ArmorStand.
     * @param speed The speed at which the ArmorStand moves.
     */
    public ArmorStandMove(Show show, long time, ShowStand stand, Location loc, double speed) {
        super(show, time);
        if (stand == null || loc == null){
            throw new IllegalArgumentException("ArmorStand or Location cannot be null");
        }
        if (speed <= 0){
            throw new IllegalArgumentException("Speed must be greater than 0");
        }
        this.stand = stand;
        this.loc = loc;
        this.speed = speed;
    }

    /**
     * Plays the action, moving the ArmorStand towards its destination.
     *
     * @param nearPlayers Players near the show (not used in this context).
     */
    @Override
    public void play(Player[] nearPlayers) {
        if (!isStandValid()) return;

        double ticksPerStep = TICKS_PER_SECOND * speed;
        Vector motion = calculateMotionVector(stand.getStand().getLocation(), loc, ticksPerStep);

        ShowUtil.logDebug(show.getName(), "Moving ArmorStand with ID " + stand.getId()
                + " to " + loc + " with motion vector " + motion);

        stand.setMovement(new Movement(motion, ticksPerStep));
        scheduleStandMovement();
    }

    /**
     * Checks whether the ArmorStand is valid or logs a debug message if invalid.
     *
     * @return true if valid, otherwise false.
     */
    private boolean isStandValid() {
        if (stand == null || stand.getStand() == null || !stand.getStand().isValid()) {
            ShowUtil.logDebug(
                    show != null ? show.getName() : "Unknown",
                    "ArmorStand with ID " + (stand != null ? stand.getId() : "Unknown") + " is null or invalid"
            );
            return false;
        }
        if (loc == null) {
            ShowUtil.logDebug(show != null ? show.getName() : "Unknown",
                    "Target location is null.");
            return false;
        }
        return true;
    }

    /**
     * Calculates the motion vector for the ArmorStand based on its current location,
     * target destination, and ticks per step (derived from speed).
     *
     * @param currentLocation The current location of the ArmorStand.
     * @param targetLocation  The destination location.
     * @param ticksPerStep    The number of ticks per step.
     * @return The calculated motion vector.
     */
    private Vector calculateMotionVector(Location currentLocation, Location targetLocation, double ticksPerStep) {
        double motionX = (targetLocation.getX() - currentLocation.getX()) / ticksPerStep;
        double motionY = (targetLocation.getY() - currentLocation.getY()) / ticksPerStep;
        double motionZ = (targetLocation.getZ() - currentLocation.getZ()) / ticksPerStep;
        return new Vector(motionX, motionY, motionZ);
    }

    /**
     * Schedules the stand movement on the server thread.
     */
    private void scheduleStandMovement() {
        Bukkit.getScheduler().runTask(ShowPlugin.getInstance(), () ->
                ShowPlugin.getInstance().getArmorStandManager().addStand(stand, StandAction.MOVE)
        );
    }

    /**
     * Loads this action from a script or configuration line.
     *
     * @param line The script line.
     * @param args Additional arguments for the action.
     * @return This action (not implemented for custom loading).
     * @throws ShowParseException If there is an error parsing the action.
     */
    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        return this;
    }

    /**
     * Creates a copy of this action for use in another Show.
     *
     * @param show The new show.
     * @param time The time in ticks for the copied action.
     * @return A new instance of ArmorStandMove.
     * @throws ShowParseException If there is an error creating the copy.
     */
    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new ArmorStandMove(show, time, stand, loc, speed);
    }
}