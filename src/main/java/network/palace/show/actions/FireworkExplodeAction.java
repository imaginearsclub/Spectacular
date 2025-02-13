package network.palace.show.actions;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;

public class FireworkExplodeAction extends ShowAction {
    private final Firework fw;

    public FireworkExplodeAction(Show show, long time, Firework fw) {
        super(show, time);
        if (fw == null){
            throw new IllegalArgumentException("Firework cannot be null");
        }
        this.fw = fw;
    }

    @Override
    public void play(Player[] nearPlayers) {
        if (fw != null && !fw.isDead()) { // Check if the firework is still alive
            try {
                fw.detonate();
            } catch (Exception e) {
                Bukkit.getLogger().warning("Failed to detonate firework: " + e.getMessage());
            }
        } else {
            Bukkit.getLogger().warning("Firework is null or already dead, unable to detonate");
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        try {
            // Implement proper parsing logic
            // Example: Parse 'args' to recreate or locate a Firework instance
            return this;
        } catch (Exception e) {
            throw new ShowParseException("Failed to load FireworkExplodeAction: " + e.getMessage());
        }
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        // Handle duplication or finding a new Firework entity
        return new FireworkExplodeAction(show, time, fw);
    }
}