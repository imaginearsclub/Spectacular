package network.palace.show.actions.armor;

import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.actions.ShowAction;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.armorstand.Position;
import network.palace.show.handlers.armorstand.PositionType;
import network.palace.show.handlers.armorstand.ShowStand;
import network.palace.show.handlers.armorstand.StandAction;
import network.palace.show.utils.ShowUtil;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

/**
 * Represents an action to update the position of an ArmorStand.
 */
public class ArmorStandPosition extends ShowAction {
    private static final double TICKS_PER_SECOND = 20.0; // Constant for tick calculation
    private final ShowStand stand;
    private final PositionType positionType;
    private final EulerAngle targetAngle;
    private final double speed;
    private final double motionDuration; // Pre-calculated duration for reuse

    /**
     * Constructs a new ArmorStandPosition action.
     *
     * @param show         The show this action belongs to.
     * @param time         The timestamp for this action in the show.
     * @param stand        The ArmorStand to manipulate.
     * @param positionType The type of position (e.g., HEAD, BODY, etc.)
     * @param targetAngle  The target angle for the position.
     * @param speed        The speed at which the position changes.
     */
    public ArmorStandPosition(Show show, long time, ShowStand stand, PositionType positionType, EulerAngle targetAngle, double speed) {
        super(show, time);

        if (stand == null || positionType == null || targetAngle == null || speed <= 0) {
            throw new IllegalArgumentException("Arguments cannot be null, and speed must be greater than zero.");
        }

        this.stand = stand;
        this.positionType = positionType;
        this.targetAngle = targetAngle;
        this.speed = speed;
        this.motionDuration = TICKS_PER_SECOND * speed; // Pre-compute this value
    }

    @Override
    public void play(Player[] nearPlayers) {
        if (!stand.isHasSpawned()) {
            ShowUtil.logDebug(show.getName(), "ArmorStand with ID " + stand.getId() + " has not spawned.");
            return;
        }

        EulerAngle currentPose = getCurrentPose();
        if (currentPose == null) {
            ShowUtil.logDebug(show.getName(), "Unknown PositionType for stand ID: " + stand.getId() + ".");
            return;
        }

        Vector motionVector = calculateMotionVector(currentPose);
        stand.addPosition(new Position(motionVector, motionDuration, positionType));
        ShowPlugin.getInstance().getArmorStandManager().addStand(stand, StandAction.POSITION);
    }

    /**
     * Calculates the motion vector for the position change.
     *
     * @param currentPose The current Euler angle of the ArmorStand's part.
     * @return A vector representing the motion.
     */
    private Vector calculateMotionVector(EulerAngle currentPose) {
        double deltaX = (targetAngle.getX() - currentPose.getX()) / motionDuration;
        double deltaY = (targetAngle.getY() - currentPose.getY()) / motionDuration;
        double deltaZ = (targetAngle.getZ() - currentPose.getZ()) / motionDuration;
        return new Vector(deltaX, deltaY, deltaZ);
    }

    /**
     * Returns the current pose of the specified part of the ArmorStand.
     *
     * @return The current EulerAngle of the relevant part, or null if invalid.
     */
    private EulerAngle getCurrentPose() {
        switch (positionType) {
            case HEAD:
                return stand.getStand().getHeadPose();
            case BODY:
                return stand.getStand().getBodyPose();
            case ARM_LEFT:
                return stand.getStand().getLeftArmPose();
            case ARM_RIGHT:
                return stand.getStand().getRightArmPose();
            case LEG_LEFT:
                return stand.getStand().getLeftLegPose();
            case LEG_RIGHT:
                return stand.getStand().getRightLegPose();
            default:
                return null;
        }
    }

    @Override
    public ShowAction load(String line, String... args) {
        ShowUtil.logDebug(show.getName(), "Load method is currently not implemented for ArmorStandPosition.");
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) {
        return new ArmorStandPosition(show, time, stand, positionType, targetAngle, speed);
    }
}