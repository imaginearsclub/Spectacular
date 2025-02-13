package network.palace.show.actions;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import lombok.Getter;
import lombok.Setter;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.packets.server.block.WrapperPlayServerBlockChange;
import network.palace.show.utils.MiscUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Getter
@Setter
@SuppressWarnings("deprecation")
public class FakeBlockAction extends ShowAction {
    private Location location;
    private Material material;

    public FakeBlockAction(Show show, long time) {
        super(show, time, null, null);
    }

    public FakeBlockAction(@NotNull Show show, long time, @Nullable Location location, @Nullable Material material) {
        super(show, time);
        this.location = location;
        this.material = material;
    }

    @Override
    public void play(Player[] nearPlayers) {
        if (location == null || material == null) {
            Bukkit.getLogger().warning("FakeBlockAction: Cannot play action as Location or Material is null.");
            return;
        }

        try {
            WrapperPlayServerBlockChange packet = createBlockChangePacket(location, material);

            for (Player player : nearPlayers) {
                if (player != null) {
                    sendPacketToPlayer(packet, player);
                }
            }
        } catch (Exception e) {
            logErrorWithLocation(e);
        }
    }

    private WrapperPlayServerBlockChange createBlockChangePacket(Location location, Material material) {
        WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange();
        packet.setLocation(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        packet.setBlockData(WrappedBlockData.createData(material));
        return packet;
    }

    private void sendPacketToPlayer(WrapperPlayServerBlockChange packet, Player player) {
        try {
            MiscUtil.sendPacket(packet, player);
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Failed to send packet to player " + player.getName() + ": " + ex.getMessage());
        }
    }

    private void logErrorWithLocation(Exception e) {
        String locationInfo = location != null ? location.getX() + "," + location.getY() + "," + location.getZ() : "unknown";
        Bukkit.getLogger().severe("FakeBlockAction: Error in Material (" + material + ") at Location: " + locationInfo +
                " | Time: " + time + " | Show: " + show.getName());
        e.printStackTrace();
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        if (args.length < 4) {
            throw new ShowParseException("Insufficient arguments for FakeBlockAction: " + line);
        }

        try {
            Material material = Material.valueOf(args[2].toUpperCase());
            Location location = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);

            if (location == null) {
                throw new ShowParseException("Invalid Location provided in: " + line);
            }
            return new FakeBlockAction(show, time, location, material);

        } catch (IllegalArgumentException e) {
            throw new ShowParseException("Invalid Material or Location: " + e.getMessage());
        }
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new FakeBlockAction(show, time, location, material);
    }
}