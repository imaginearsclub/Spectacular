package network.palace.show.actions;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.WorldUtil;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FireworkAction extends ShowAction {
    private static final int MAX_FIREWORK_POWER = 5; // Extracted constant for clarity
    private static final double MAX_DIRECTIONAL_POWER = 10.0; // Extracted constant for clarity

    private final Location location; // Made final for immutability
    private final List<FireworkEffect> fireworkEffects; // Changed from ArrayList to List for abstraction
    private final int power;
    private final Vector direction;
    private final double directionalPower;

    /**
     * Constructs a new FireworkAction with detailed configuration,
     * including specified location, firework effects, power, direction,
     * and directional power. Ensures that all inputs are validated before
     * initializing the instance.
     *
     * @param show the show instance to which this action belongs
     * @param time the time at which the action is scheduled to occur
     * @param location the location where the firework will be launched; must not be null and must have a valid world
     * @param fireworkEffects the list of firework effects to apply; must not be null or empty
     * @param power the power level of the firework, determining how high it launches; must be between 0 and the maximum allowed power
     * @param direction the direction of the firework's trajectory; must not be null
     * @param directionalPower the strength of the directional force applied to the firework's trajectory; must be between 0 and the maximum allowed directional power
     * @throws IllegalArgumentException if any parameter is invalid or fails validation constraints
     */
    public FireworkAction(Show show, long time, Location location, List<FireworkEffect> fireworkEffects, int power, Vector direction, double directionalPower) {
        super(show, time);

        validateInputs(location, fireworkEffects, power, direction, directionalPower);
        this.location = location;
        this.fireworkEffects = new ArrayList<>(fireworkEffects); // Defensive copy
        this.power = power;
        this.direction = direction;
        this.directionalPower = directionalPower;
    }

    /**
     * Constructs a new FireworkAction with basic initialization.
     * This simplified constructor uses default values for location, firework effects, power,
     * direction, and directional power while requiring only the show instance and the scheduled time.
     *
     * @param show the show instance to which this action belongs
     * @param time the time at which the action is scheduled to occur
     */
    public FireworkAction(Show show, long time) {
        this(show, time, null, new ArrayList<>(), 0, new Vector(), 0); // Defaults to empty or zero values
    }

    /**
     * Executes the firework action by attempting to launch a firework and applying
     * the predefined effects, power, and direction settings. This method is triggered
     * for a group of nearby players to display the firework action.
     *
     * @param nearPlayers an array of players who are nearby and will experience the firework action
     */
    @Override
    public void play(Player[] nearPlayers) {
        try {
            playFirework();
        } catch (Exception e) {
            e.printStackTrace(); // Log errors for debugging
        }
    }

    /**
     * Launches a firework at the specified location with predefined effects, power, direction,
     * and optional directional force. This method spawns a {@link Firework} entity at the given location,
     * applies the configured effects, and modifies its trajectory or velocity if a direction is specified.
     *
     * If the firework power is set to zero, the firework explodes immediately by scheduling an instant
     * explosion action. Exceptions during execution will result in the cleanup of the firework entity.
     *
     * @throws Exception if an error occurs during the creation, configuration, or handling of the firework
     */
    public void playFirework() throws Exception {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        try {
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            fireworkMeta.addEffects(fireworkEffects);
            fireworkMeta.setPower(Math.min(MAX_FIREWORK_POWER, Math.max(0, power)));
            firework.setFireworkMeta(fireworkMeta);

            if (direction.length() > 0) {
                firework.setVelocity(direction.normalize().multiply(directionalPower * 0.05));
            }
            if (power == 0) {
                scheduleInstantExplosion(firework);
            }
        } catch (Exception e) {
            firework.remove(); // Ensure cleanup
            throw e;
        }
    }

    /**
     * Schedules an instant explosion action for the specified firework.
     * Creates a new {@code FireworkExplodeAction} with a slight delay
     * relative to the current show time, and adds it to the list of
     * scheduled actions in the show.
     *
     * @param firework the firework instance for which the explosion is to be scheduled
     */
    private void scheduleInstantExplosion(Firework firework) {
        show.addLaterAction(new FireworkExplodeAction(show, show.getShowTime() + 50, firework));
    }

    /**
     * Loads a firework action from the provided input parameters, parsing the string arguments
     * to initialize the required properties of the action. Validates the number of arguments
     * and their contents, including location, effects, power, direction, and directional power.
     *
     * @param line the input line representing the firework action; used for potential logging or debugging.
     * @param args the string array of arguments containing the properties of the firework action:
     *             index 2 - location (String), index 3 - firework effects (comma-separated names),
     *             index 4 - firework power (int), index 5 - direction vector (x, y, z as String),
     *             index 6 - directional power (double), and more as needed.
     * @return a new {@code ShowAction} instance representing the parsed firework action.
     * @throws ShowParseException if the number of arguments is incorrect, or if any argument is invalid
     *                            (e.g., malformed location, invalid numeric values, etc.).
     */
    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        if (args.length != 7) {
            throw new ShowParseException("Invalid Firework Line Length: expected 7 arguments, got " + args.length);
        }
        try {
            // Parse location
            Location location = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[2]);
            if (location == null || location.getWorld() == null) {
                throw new ShowParseException("Invalid location");
            }

            // Parse effects
            List<FireworkEffect> effectList = parseEffects(args[3]);
            int power = parseFireworkPower(args[4]);
            Vector direction = parseDirection(args[5]);
            double directionalPower = parseDirectionalPower(args[6]);
            return new FireworkAction(show, time, location, effectList, power, direction, directionalPower);
        } catch (NumberFormatException e) {
            throw new ShowParseException("Invalid numeric value in arguments: " + e.getMessage());
        }
    }

    /**
     * Parses a comma-separated string of effect names and maps them to {@link FireworkEffect} objects
     * using the effect map from the associated show. Only valid effects present in the effect map
     * will be added to the result list.
     *
     * @param effectsArg a comma-separated string representing the names of firework effects to parse
     * @return a list of {@link FireworkEffect} objects corresponding to the provided effect names
     * @throws ShowParseException if none of the provided effect names are valid or found in the effect map
     */
    private List<FireworkEffect> parseEffects(String effectsArg) throws ShowParseException {
        List<FireworkEffect> effects = new ArrayList<>();
        String[] effectNames = effectsArg.split(",");
        for (String effectName : effectNames) {
            if (show.getEffectMap().containsKey(effectName)) {
                effects.add(show.getEffectMap().get(effectName));
            } else {
                System.out.println("Waring: Effect \"" + effectName + "\" not found in the effect map.");
            }
        }
        if (effects.isEmpty()) {
            throw new ShowParseException("No valid effects provided");
        }
        return effects;
    }
    /**
     * Parses a string argument representing the firework power, validates its value,
     * and ensures it falls within the acceptable range of 0 to MAX_FIREWORK_POWER.
     * If the value is invalid or out of range, an exception is thrown.
     *
     * @param powerArg the string representation of the firework power to be parsed;
     *                 must be a valid numeric value within the range of 0 to MAX_FIREWORK_POWER.
     * @return the parsed and validated firework power as an integer within the acceptable range.
     * @throws ShowParseException if the parsed firework power is out of the valid range or is not a valid number.
     */
    private int parseFireworkPower(String powerArg) throws ShowParseException {
        int power = Integer.parseInt(powerArg);
        if (power < 0 || power > MAX_FIREWORK_POWER) {
            throw new ShowParseException("Power out of range (0-" + MAX_FIREWORK_POWER + "): " + power);
        }
        return power;
    }

    /**
     * Parses a string representing a direction vector and converts it into a {@link Vector} object.
     * The input string must contain three comma-separated numeric components (x, y, z).
     *
     * @param directionArg the string representation of the direction vector; must contain three numeric components separated by commas
     * @return a {@link Vector} object representing the parsed direction
     * @throws ShowParseException if the input string does not have exactly three components or if any component is not a valid number
     */
    private Vector parseDirection(String directionArg) throws ShowParseException {
        String[] directionElements = directionArg.split(",");
        if (directionElements.length != 3) {
            throw new ShowParseException("Invalid direction format: " + directionArg);
        }
        return new Vector(
                Double.parseDouble(directionElements[0]),
                Double.parseDouble(directionElements[1]),
                Double.parseDouble(directionElements[2])
        );
    }

    /**
     * Parses a string argument to determine the directional power, validates its value,
     * and ensures it falls within an acceptable range. If the value is invalid or out of range,
     * an exception is thrown.
     *
     * @param dirPowerArg the string representation of the directional power to be parsed;
     *                    it must be a valid numeric value within the range of 0 to MAX_DIRECTIONAL_POWER.
     * @return the parsed and validated directional power as a double within the acceptable range.
     * @throws ShowParseException if the parsed directional power is out of the valid range or is not a valid number.
     */
    private double parseDirectionalPower(String dirPowerArg) throws ShowParseException {
        double directionalPower = Double.parseDouble(dirPowerArg);
        if (directionalPower < 0 || directionalPower > MAX_DIRECTIONAL_POWER) {
            throw new ShowParseException("Directional power out of range (0-" + MAX_DIRECTIONAL_POWER + "): " + directionalPower);
        }
        return directionalPower;
    }

    /**
     * Validates the inputs required for performing a firework action.
     * Ensures that the provided parameters meet specified constraints.
     *
     * @param location the location where the firework action will take place; must not be null and must have a valid world.
     * @param effects the list of firework effects to be used; must not be null or empty.
     * @param power the power level of the firework; must be between 0 and the maximum firework power defined.
     * @param direction the direction vector for the firework; must not be null.
     * @param directionalPower the intensity of the directional force applied to the firework; must be between 0 and the maximum directional power defined.
     * @throws IllegalArgumentException if any parameter is invalid or fails the constraints.
     */
    private void validateInputs(Location location, List<FireworkEffect> effects, int power, Vector direction, double directionalPower) {
        if (location == null || location.getWorld() == null) {
            throw new IllegalArgumentException("Location or world cannot be null");
        }
        if (effects == null || effects.isEmpty()) {
            throw new IllegalArgumentException("Effects list cannot be null or empty");
        }
        if (power < 0 || power > MAX_FIREWORK_POWER) {
            throw new IllegalArgumentException("Power must be between 0 and " + MAX_FIREWORK_POWER);
        }
        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }
        if (directionalPower < 0 || directionalPower > MAX_DIRECTIONAL_POWER) {
            throw new IllegalArgumentException("Directional power must be between 0 and " + MAX_DIRECTIONAL_POWER);
        }
    }

    private static Location defaultLocation() {
        return new Location(null, 0, 0, 0); // Placeholder for no location
    }

    /**
     * Creates a copy of the current FireworkAction instance with the specified show
     * and time, retaining the original properties of the action.
     *
     * @param show the show instance to associate with the copied action
     * @param time the time at which the action occurs in the show
     * @return a new FireworkAction object with the same properties as the original but associated with the specified show and time
     * @throws ShowParseException if an error occurs during the creation of the FireworkAction instance
     */
    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new FireworkAction(show, time, location, fireworkEffects, power, direction, directionalPower);
    }
}