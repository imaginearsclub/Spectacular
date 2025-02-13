package network.palace.show.actions;

import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.Fountain;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Objects;

public class FountainAction extends ShowAction {
    private static final String NULL_LOCATION_MESSAGE = "Location cannot be null";
    private static final String NULL_MATERIAL_MESSAGE = "Material cannot be null";
    private double duration;
    private Location location;
    private Vector forceVector;
    private Material material;

    public FountainAction(Show show, long time) {
        super(show, time);
    }

    public FountainAction(Show show, long time, double duration, Location location, Material material, Vector forceVector) {
        super(show, time);
        validateParameters(duration, location, material, forceVector);
        this.duration = duration;
        this.location = location.clone();
        this.forceVector = forceVector.clone();
        this.material = material;
    }

    @Override
    public void play(Player[] nearbyPlayers) {
        if (logWarningIfUninitialized()) return;
        ShowPlugin.getInstance()
                .getFountainManager()
                .addFountain(new Fountain(location.clone(), duration, material, forceVector.clone()));
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        if (args.length < 6) {
            throw new ShowParseException("Insufficient arguments provided to load the FountainAction. Minimum 6 arguments required.");
        }
        try {
            this.location = parseLocation(show.getWorld().getName(), args[4]).clone();
            this.forceVector = parseForceVector(show.getWorld().getName(), args[5]).clone();
            this.material = parseMaterial(args[2]);
            this.duration = parseDuration(args[3]);
        } catch (Exception e) {
            throw new ShowParseException("Failed to load FountainAction: " + e.getMessage(), e);
        }
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new FountainAction(show, time, duration, location.clone(), material, forceVector.clone());
    }

    /**
     * Logs a warning if any of the important parameters are null.
     *
     * @return true if uninitialized, false otherwise.
     */
    private boolean logWarningIfUninitialized() {
        if (location == null || material == null || forceVector == null) {
            ShowPlugin.getInstance().getLogger().warning("FountainAction parameters are not initialized properly!");
            return true;
        }
        return false;
    }

    /**
     * Validates key parameters to ensure they aren't null or invalid.
     *
     * @param duration     the duration
     * @param location     the location
     * @param material     the material
     * @param forceVector  the force vector
     */
    private void validateParameters(double duration, Location location, Material material, Vector forceVector) {
        validateDuration(duration);
        Objects.requireNonNull(location, NULL_LOCATION_MESSAGE);
        Objects.requireNonNull(material, NULL_MATERIAL_MESSAGE);
        Objects.requireNonNull(forceVector, "Force vector cannot be null");
    }

    /**
     * Validates the duration value.
     *
     * @param duration the duration to validate
     */
    private void validateDuration(double duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be greater than zero.");
        }
    }

    /**
     * Parses and validates a material from a string.
     *
     * @param materialStr the string representing a material
     * @return the Material object
     * @throws IllegalArgumentException if the material is invalid
     */
    private Material parseMaterial(String materialStr) {
        try {
            return Material.valueOf(materialStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid material: " + materialStr, e);
        }
    }

    /**
     * Parses and validates duration from a string.
     *
     * @param durationStr the string representing the duration
     * @return the duration as a double
     */
    private double parseDuration(String durationStr) {
        try {
            double duration = Double.parseDouble(durationStr);
            validateDuration(duration);
            return duration;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid duration value: " + durationStr, e);
        }
    }

    /**
     * Parses a location from a string.
     *
     * @param worldName the name of the world
     * @param locStr    the location string
     * @return the parsed Location object
     */
    private Location parseLocation(String worldName, String locStr) {
        Location loc = WorldUtil.strToLoc(worldName + "," + locStr);
        if (loc == null) {
            throw new IllegalArgumentException("Invalid location string: " + locStr);
        }
        return loc;
    }

    /**
     * Parses a force vector from a string.
     *
     * @param worldName the name of the world
     * @param vectorStr the vector string
     * @return the parsed Vector object
     */
    private Vector parseForceVector(String worldName, String vectorStr) {
        Double[] values = WorldUtil.strToDoubleList(worldName + "," + vectorStr);
        if (values == null || values.length != 3) {
            throw new IllegalArgumentException("Invalid vector string: " + vectorStr);
        }
        return new Vector(values[0], values[1], values[2]);
    }
}