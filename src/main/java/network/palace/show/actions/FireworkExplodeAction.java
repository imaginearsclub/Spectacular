package network.palace.show.actions;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;

public class FireworkExplodeAction extends ShowAction {
    private static final String FIREWORK_NULL_MESSAGE = "Firework cannot be null";
    private static final String FIREWORK_DETONATION_FAILURE = "Failed to detonate firework: ";
    private static final String FIREWORK_INVALID_WARNING = "Firework is null or already dead, unable to detonate";

    private final Firework fw;

    /**
     * Creates an instance of FireworkExplodeAction. This class represents an action
     * where a firework detonates during a show at a specific time. The firework must
     * be valid and not null to perform the explosion action.
     *
     * @param show The show instance in which this action is executed.
     * @param time The scheduled time for this action to occur, in milliseconds.
     * @param fw   The firework to be detonated during the action.
     * @throws IllegalArgumentException if the provided firework is null.
     */
    public FireworkExplodeAction(Show show, long time, Firework fw) {
        super(show, time);
        this.fw = validateFirework(fw);
    }

    /**
     * Executes the play action for the firework explosion.
     * If the firework is valid, it attempts to detonate it.
     * If the firework is invalid (null or already detonated), a warning message is logged.
     *
     * @param nearPlayers an array of nearby players who may witness the firework detonation.
     */
    @Override
    public void play(Player[] nearPlayers) {
        if (isFireworkValid()) {
            attemptDetonation();
        } else {
            logWarning(FIREWORK_INVALID_WARNING);
        }
    }

    /**
     * Loads configuration or parameters into the current instance of the action based on the provided line and arguments.
     *
     * @param line the configuration line in string format, typically containing key-value pairs or parameters relevant to the action.
     * @param args an array of additional arguments or parameters used to initialize or configure the action.
     * @return the current instance of {@code ShowAction} with configuration applied.
     * @throws ShowParseException if an error occurs during parsing or applying configuration.
     */
    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        try {
            return this;
        } catch (Exception e) {
            throw new ShowParseException("Failed to load FireworkExplodeAction: " + e.getMessage());
        }
    }

    /**
     * Creates a copy of the current action with the specified show and time.
     * This method is used to duplicate the action while associating it with
     * a new show instance and scheduling it to execute at a different time.
     *
     * @param show The show instance to which the copied action will belong.
     * @param time The time at which the copied action is scheduled to occur, in milliseconds.
     * @return A new instance of {@code ShowAction}, specifically a {@code FireworkExplodeAction},
     *         associated with the provided show and scheduled time.
     * @throws ShowParseException If there is an error during the creation of the copied action.
     */
    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new FireworkExplodeAction(show, time, fw);
    }

    /**
     * Validates the given firework instance to ensure it is not null.
     *
     * @param fw the firework instance to be validated
     * @return the validated firework instance
     * @throws IllegalArgumentException if the provided firework instance is null
     */
    private Firework validateFirework(Firework fw){
        if (fw == null){
            throw new IllegalArgumentException(FIREWORK_NULL_MESSAGE);
        }
        return fw;
    }

    /**
     * Determines whether the firework is valid for detonation. A firework is
     * considered valid if it is not null and has not already detonated.
     *
     * @return true if the firework is valid; false otherwise
     */
    private boolean isFireworkValid(){
        return fw != null && !fw.isDead();
    }

    /**
     * Attempts to detonate the assigned firework object.
     * If the detonation fails due to an exception,
     * a warning is logged with the relevant failure message.
     *
     * This method internally calls the {@code detonate} method for the firework object
     * and catches any exceptions that occur during the process.
     * The failure is logged using the {@code FIREWORK_DETONATION_FAILURE} message,
     * along with the exception details in the log.
     *
     * This functionality is typically used when a firework needs to be activated in a
     * controlled show environment and any unexpected failures around firework behavior
     * need to be identified and logged without disrupting the overall process.
     */
    private void attemptDetonation(){
        try {
            fw.detonate();
        } catch (Exception e) {
            logWarning(FIREWORK_DETONATION_FAILURE + e.getMessage());
        }
    }

    /**
     * Logs a warning message to the console using Bukkit's logger.
     *
     * @param message the warning message to be logged
     */
    private void logWarning(String message) {
        Bukkit.getLogger().warning(message);
    }
}