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

    // Primary constructor with validation
    public FireworkAction(Show show, long time, Location location, List<FireworkEffect> fireworkEffects, int power, Vector direction, double directionalPower) {
        super(show, time);

        validateInputs(location, fireworkEffects, power, direction, directionalPower);
        this.location = location;
        this.fireworkEffects = new ArrayList<>(fireworkEffects); // Defensive copy
        this.power = power;
        this.direction = direction;
        this.directionalPower = directionalPower;
    }

    // Simplified constructor for basic initialization
    public FireworkAction(Show show, long time) {
        this(show, time, null, new ArrayList<>(), 0, new Vector(), 0); // Defaults to empty or zero values
    }

    @Override
    public void play(Player[] nearPlayers) {
        try {
            playFirework();
        } catch (Exception e) {
            e.printStackTrace(); // Log errors for debugging
        }
    }

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

    private void scheduleInstantExplosion(Firework firework) {
        show.addLaterAction(new FireworkExplodeAction(show, show.getShowTime() + 50, firework));
    }

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
    private int parseFireworkPower(String powerArg) throws ShowParseException {
        int power = Integer.parseInt(powerArg);
        if (power < 0 || power > MAX_FIREWORK_POWER) {
            throw new ShowParseException("Power out of range (0-" + MAX_FIREWORK_POWER + "): " + power);
        }
        return power;
    }

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

    private double parseDirectionalPower(String dirPowerArg) throws ShowParseException {
        double directionalPower = Double.parseDouble(dirPowerArg);
        if (directionalPower < 0 || directionalPower > MAX_DIRECTIONAL_POWER) {
            throw new ShowParseException("Directional power out of range (0-" + MAX_DIRECTIONAL_POWER + "): " + directionalPower);
        }
        return directionalPower;
    }

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

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new FireworkAction(show, time, location, fireworkEffects, power, direction, directionalPower);
    }
}