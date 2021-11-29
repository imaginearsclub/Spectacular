package network.palace.show.commands.showgen;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import network.palace.show.ShowPlugin;
import network.palace.show.generator.GeneratorSession;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetInitialCommand {

    public void handle(CommandSender sender) {
        Player player = (Player) sender;
        GeneratorSession session = ShowPlugin.getShowGenerator().getOrCreateSession(player.getUniqueId());

        LocalSession worldEditSession = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
        try {
            Region selection = worldEditSession.getSelection(worldEditSession.getSelectionWorld());

            if (selection == null) {
                player.sendMessage(ChatColor.RED + "Make a selection with a WorldEdit wand, then run this command!");
                return;
            }

            World world = BukkitAdapter.asBukkitWorld(selection.getWorld()).getWorld();
            BlockVector3 maxVec = selection.getMaximumPoint();
            BlockVector3 minVec = selection.getMinimumPoint();
            Location max = new Location(world, maxVec.getX(), maxVec.getY(), maxVec.getZ());
            Location min = new Location(world, minVec.getX(), minVec.getY(), minVec.getZ());
            session.setInitialScene(new GeneratorSession.ShowSelection(max, min));
            player.sendMessage(ChatColor.GREEN + "The initial scene has been set!");
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Error! Did you select the whole region?");
            e.printStackTrace();
        }

    }

}
