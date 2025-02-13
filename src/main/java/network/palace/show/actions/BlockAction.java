package network.palace.show.actions;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.WorldUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BlockAction extends ShowAction {
    private static final String VALIDATION_ERROR_MESSAGE = "Location and Material must not be null";
    public Location location;
    public Material blockMat;

    public BlockAction(final Show show, final long time) {
        super(show, time);
    }

    public BlockAction(final Show show, final long time, final Location location, final Material blockMat) {
        this(show, time);
        setLocation(location);
        setBlockMaterial(blockMat);
    }

    @Override
    public void play(Player[] nearPlayers) {
        validateState();
        final Block block = location.getBlock();
        block.setBlockData(blockMat.createBlockData());
    }

    @Override
    public ShowAction load(final String line, final String... args) throws ShowParseException {
        if (args.length < 4) {
            throw new ShowParseException("Invalid BlockAction: " + line);
        }
        final Location parsedLocation = WorldUtil.strToLoc(show.getWorld().getName() + "," + args[3]);
        if (parsedLocation == null) {
            throw new ShowParseException("Invalid Location " + line);
        }
        try {
            setBlockMaterial(Material.valueOf(args[2].toUpperCase()));
            setLocation(parsedLocation);
        } catch (final IllegalArgumentException e) {
            throw new ShowParseException("Cannot parse material: " + args[2] + " - " + e.getMessage());
        }
        return this;
    }

    @Override
    protected ShowAction copy(final Show show, final long time) throws ShowParseException {
        return new BlockAction(show, time, getLocation(), getBlockMaterial());
    }

    public Location getLocation() {
        return location.clone();
    }

    public void setLocation(final Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        this.location = location.clone();
    }

    public Material getBlockMaterial() {
        return blockMat;
    }

    public void setBlockMaterial(final Material blockMaterial) {
        if (blockMaterial == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }
        this.blockMat = blockMaterial;
    }

    private void validateState() {
        if (location == null || blockMat == null) {
            throw new IllegalStateException(VALIDATION_ERROR_MESSAGE);
        }
    }
}