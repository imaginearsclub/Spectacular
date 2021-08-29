package network.palace.show.sequence.laser;

import network.palace.show.Show;
import network.palace.show.beam.beam.Beam;
import network.palace.show.beam.beam.Laser;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.sequence.handlers.LaserObject;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * @author Marc
 * @since 8/2/17
 */
public class LaserMoveSequence extends ShowSequence {
    private final LaserSequence parent;
    private LaserObject object;
    private Location target;
    private Vector change = null;
    private int duration;

    public LaserMoveSequence(Show show, long time, LaserSequence parent) {
        super(show, time);
        this.parent = parent;
    }

    @Override
    public boolean run() {
        if (!parent.isSpawned()) return true;
        Laser.GuardianLaser laser = parent.getLaser();
        if (laser == null || !laser.isStarted()) return true;
        switch (object) {
            case SOURCE:
                laser.moveStart(target, duration, () -> {
                    Bukkit.getLogger().info("Completed move to " + target.toString());
                });
                break;
            case TARGET:
                laser.moveEnd(target, duration, () -> {
                    Bukkit.getLogger().info("Completed move to " + target.toString());
                });
                break;
        }

        return false;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        this.object = LaserObject.fromString(args[2]);
        this.target = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
        this.duration = ShowUtil.getInt(args[4]);
        return this;
    }
}
