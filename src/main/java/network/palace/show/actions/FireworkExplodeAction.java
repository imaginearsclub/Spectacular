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

    public FireworkExplodeAction(Show show, long time, Firework fw) {
        super(show, time);
        this.fw = validateFirework(fw);
    }

    @Override
    public void play(Player[] nearPlayers) {
        if (isFireworkValid()) {
            attemptDetonation();
        } else {
            logWarning(FIREWORK_INVALID_WARNING);
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        try {
            return this;
        } catch (Exception e) {
            throw new ShowParseException("Failed to load FireworkExplodeAction: " + e.getMessage());
        }
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new FireworkExplodeAction(show, time, fw);
    }

    private Firework validateFirework(Firework fw){
        if (fw == null){
            throw new IllegalArgumentException(FIREWORK_NULL_MESSAGE);
        }
        return fw;
    }

    private boolean isFireworkValid(){
        return fw != null && !fw.isDead();
    }

    private void attemptDetonation(){
        try {
            fw.detonate();
        } catch (Exception e) {
            logWarning(FIREWORK_DETONATION_FAILURE + e.getMessage());
        }
    }

    private void logWarning(String message) {
        Bukkit.getLogger().warning(message);
    }
}