package network.palace.show.actions;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.BlockData;
import network.palace.show.utils.ShowUtil;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BlockAction extends ShowAction {
    public Location location;
    public int type;
    public byte data;

    public BlockAction(Show show, long time) {
        super(show, time);
    }

    public BlockAction(Show show, long time, Location location, int type, byte data) {
        super(show, time);
        this.location = location;
        this.type = type;
        this.data = data;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void play(Player[] nearPlayers) {
        Block block = location.getBlock();
        if (ShowUtil.convertMaterial(type, data) != null) {
            block.setType(ShowUtil.convertMaterial(type, data));
        } else {
            block.setType(Material.STONE);
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        Location loc = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
        if (loc == null) {
            throw new ShowParseException("Invalid Location " + line);
        }
        try {
            BlockData data = ShowUtil.getBlockData(args[2]);
            this.location = loc;
            this.type = data.getId();
            this.data = data.getData();
            if (ShowUtil.convertMaterial(type, data.getData()) == null) {
                throw new ShowParseException("Could not parse the given id into a Spigot Material");
            }
        } catch (ShowParseException e) {
            throw new ShowParseException(e.getReason());
        }
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        return new BlockAction(show, time, location, type, data);
    }
}
