package network.palace.show.actions.armor;

import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.actions.ShowAction;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.armorstand.Rotation;
import network.palace.show.handlers.armorstand.ShowStand;
import network.palace.show.handlers.armorstand.StandAction;
import network.palace.show.utils.ShowUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ArmorStandRotate extends ShowAction {
    private final ShowStand stand;
    private final float yaw;
    private final double speed;

    public ArmorStandRotate(Show show, long time, ShowStand stand, float yaw, double speed) {
        super(show, time);

        // Validate inputs during creation
        if (stand == null) {
            throw new IllegalArgumentException("ShowStand cannot be null.");
        }
        if (speed <= 0) {
            throw new IllegalArgumentException("Speed must be greater than zero.");
        }

        this.stand = stand;
        this.yaw = yaw;
        this.speed = speed;
    }

    @Override
    public void play(Player[] nearPlayers) {
        // Null check
        if (stand == null) {
            ShowUtil.logDebug(show.getName(), "ArmorStand is null. Cannot perform rotation.");
            return;
        }

        // Ensure the stand has been spawned
        if (!stand.isHasSpawned()) {
            ShowUtil.logDebug(show.getName(), "ArmorStand with ID " + stand.getId() + " has not spawned.");
            return;
        }

        double ticks = speed * 20;
        if (ticks <= 0) {
            ShowUtil.logDebug(show.getName(), "Ticks must be greater than zero. Rotation skipped.");
            return;
        }

        float interval = (float) (this.yaw / ticks);

        // Thread-safe rotation update
        Bukkit.getScheduler().runTask(ShowPlugin.getPlugin(), () -> {
            stand.setRotation(new Rotation(interval, speed * 20));
            show.getArmorStandManager().addStand(stand, StandAction.ROTATION); // Corrected method access
        });
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        if (args == null || args.length < 3) {
            throw new ShowParseException("Invalid arguments. Expected: [standID, yaw, speed]");
        }

        try {
            String standID = args[0];
            ShowStand stand = show.getArmorStandManager().getStandById(standID); // Fixed method call
            if (stand == null) {
                throw new ShowParseException("Stand with ID " + standID + " not found.");
            }

            float yaw = Float.parseFloat(args[1]);
            double speed = Double.parseDouble(args[2]);

            if (speed <= 0) {
                throw new ShowParseException("Speed must be greater than zero.");
            }

            return new ArmorStandRotate(show, System.currentTimeMillis(), stand, yaw, speed);
        } catch (NumberFormatException e) {
            throw new ShowParseException("Invalid number format in arguments: " + e.getMessage());
        }
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new ArmorStandRotate(show, time, stand, yaw, speed);
    }
}