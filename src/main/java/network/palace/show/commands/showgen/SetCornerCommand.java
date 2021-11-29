package network.palace.show.commands.showgen;

import network.palace.show.ShowPlugin;
import network.palace.show.generator.GeneratorSession;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCornerCommand {

    public void handle(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/showgen setcorner x,y,z");
            return;
        }
        String[] locData = args[1].split(",");
        if (locData.length < 3) {
            player.sendMessage(ChatColor.RED + "/showgen setcorner x,y,z");
            return;
        }
        try {
            int x = Integer.parseInt(locData[0]);
            int y = Integer.parseInt(locData[1]);
            int z = Integer.parseInt(locData[2]);
            World w = player.getWorld();
            GeneratorSession session = ShowPlugin.getShowGenerator().getOrCreateSession(player.getUniqueId());
            session.setCorner(new Location(w, x, y, z));
            player.sendMessage(ChatColor.GREEN + "Set north-west-bottom corner to " + x + "," + y + "," + z + "!");
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Are you sure those are all numbers?");
        }
    }
}
