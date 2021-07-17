package network.palace.show.actions;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import lombok.Getter;
import lombok.Setter;
import network.palace.show.packets.server.block.WrapperPlayServerBlockChange;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.BlockData;
import network.palace.show.utils.MiscUtil;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 7/1/15
 */
@Getter
@Setter
@SuppressWarnings("deprecation")
public class FakeBlockAction extends ShowAction {
    private Location loc;
    private Material mat;

    public FakeBlockAction(Show show, long time) {
        super(show, time);
    }

    public FakeBlockAction(Show show, long time, Location loc, Material mat) {
        super(show, time);
        this.loc = loc;
        this.mat = mat;
    }

    @Override
    public void play(Player[] nearPlayers) {
        try {
            WrapperPlayServerBlockChange p = new WrapperPlayServerBlockChange();
            p.setLocation(new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            p.setBlockData(WrappedBlockData.createData(mat));
            for (Player tp : nearPlayers) {
                if (tp != null) MiscUtil.sendPacket(p, tp);
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("FakeBlockAction -" + ChatColor.RED + "Error sending FakeBlockAction for type (" +
                    mat.toString() + ") at location " + loc.getX() + "," + loc.getY() + "," + loc.getZ() + " at time " +
                    time + " for show " + show.getName());
            e.printStackTrace();
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        Location loc = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
        if (loc == null) {
            throw new ShowParseException("Invalid Location " + line);
        }
        try {
            this.loc = loc;
            this.mat = Material.valueOf(args[2]);
        } catch (IllegalArgumentException e) {
            throw new ShowParseException(e.getMessage());
        }
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new FakeBlockAction(show, time, loc, mat);
    }
}