package network.palace.show;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import network.palace.show.utils.ShowUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;

@SuppressWarnings("deprecation")
/**
 * Manager responsible for handling terrain and schematic operations using WorldEdit for a Paper server.
 */
public class TerrainManager {
    private final LocalSession localSession;
    private final EditSession editSession;
    private final Player localPlayer;
    private final WorldEdit we;

    /**
     * Constructor
     *
     * @param wep   the WorldEdit plugin instance
     * @param player the player to work with
     */
    public TerrainManager(WorldEditPlugin wep, Player player) {
        Objects.requireNonNull(wep, "WorldEdit plugin cannot be null");
        Objects.requireNonNull(player, "Player cannot be null");

        this.we = wep.getWorldEdit();
        this.localPlayer = player;
        this.localSession = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
        this.editSession = localSession.createEditSession(BukkitAdapter.adapt(player));
    }

    /**
     * Constructor for TerrainManager to work within a specific world.
     *
     * @param wep   the WorldEdit plugin instance
     * @param world the world to work in
     */
    public TerrainManager(WorldEditPlugin wep, World world){
        Objects.requireNonNull(wep, "WorldEdit plugin cannot be null");
        Objects.requireNonNull(world, "World cannot be null");

        this.we = wep.getWorldEdit();
        this.localPlayer = null;
        this.localSession = new LocalSession(we.getConfiguration());
        this.editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));
    }

    /**
     * Write the terrain bounded by the given locations to the given file as a MCedit format
     * schematic.
     *
     * @param saveFile a File representing the schematic file to create
     * @param l1       one corner of the region to save
     * @param l2       the corner of the region to save, opposite to l1
     */
    public void saveTerrain(File saveFile, Location l1, Location l2) throws WorldEditException, IOException {
        validateLocations(l1, l2);

        // Calculate the minimum and maximum points of the region
        BlockVector3 min = getMin(l1, l2);
        BlockVector3 max = getMax(l1, l2);

        File safeFile = we.getSafeSaveFile(BukkitAdapter.adapt(localPlayer), saveFile.getParentFile(), saveFile.getName(), ".schematic", ".schematic");

        CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(Objects.requireNonNull(l1.getWorld())), min, max);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        // Perform the copy operation using auto-closing edit session
        try (EditSession editSession = we.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1)) {
            ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
            copy.setCopyingEntities(true);
            Operations.complete(copy);
        }

        // Write schematic to file
        try (ClipboardWriter writer = ClipboardFormats.findByAlias("sponge").getWriter(new FileOutputStream(safeFile))) {
            writer.write(clipboard);
        }
    }

    /**
     * Load a schematic file and paste it at the specified location.
     *
     * @param wep the WorldEdit plugin instance
     * @param fileName        the name of the schematic file
     * @param loc             the location to paste the schematic
     * @param ignoreAirBlocks whether to ignore air blocks while pasting
     * @throws Exception if an error occurs during loading or pasting
     */
    public void loadSchematic(WorldEditPlugin wep, String fileName, Location loc, boolean ignoreAirBlocks) throws Exception {
        Objects.requireNonNull(wep, "WorldEdit plugin cannot be null");
        Objects.requireNonNull(fileName, "Schematic file name cannot be null");
        Objects.requireNonNull(loc, "Location cannot be null");

        Path schematicDir = Path.of("plugins", "WorldEdit", "schematics");
        File schematicFile = wep.getWorldEdit().getSafeOpenFile(null, schematicDir.toFile(), fileName, "schematic", "schematic");

        if (!schematicFile.exists()) {
            ShowUtil.logDebug("Schematics", "Tried to load Schematic " + fileName + " but does not exist!");
            return;
        }

        ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
        if (format == null) {
            throw new IllegalArgumentException("Unknown schematic format for file: " + fileName);
        }
        try (
                FileInputStream fis = new FileInputStream(schematicFile);
                ClipboardReader reader = format.getReader(fis);
        ) {
            Clipboard clipboard = reader.read();
            localSession.setClipboard(new ClipboardHolder(clipboard));

            BlockVector3 destination = BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            Operation operation = localSession.getClipboard().createPaste(editSession)
                    .to(destination)
                    .ignoreAirBlocks(ignoreAirBlocks)
                    .build();
            Operations.completeLegacy(operation);
        }
    }

    /**
     * Validates that the given locations and their associated worlds are not null.
     *
     * @param l1 the first location to validate, must not be null
     * @param l2 the second location to validate, must not be null
     */
    private void validateLocations(Location l1, Location l2) {
        Objects.requireNonNull(l1, "Location 1 cannot be null");
        Objects.requireNonNull(l2, "Location 2 cannot be null");
        Objects.requireNonNull(l1.getWorld(), "Location 1 world cannot be null");
        Objects.requireNonNull(l2.getWorld(), "Location 2 world cannot be null");
    }

        /**
         * Calculates the minimum corner of a cuboid region defined by two locations.
         *
         * @param l1 the first location
         * @param l2 the second location
         * @return a BlockVector3 representing the minimum corner calculated
         *         by taking the smallest x, y, and z values from the two locations
         */
        private BlockVector3 getMin(Location l1, Location l2) {
        return BlockVector3.at(Math.min(l1.getBlockX(), l2.getBlockX()), Math.min(l1.getBlockY(), l2.getBlockY()),
                Math.min(l1.getBlockZ(), l2.getBlockZ())
        );
    }

    /**
     * Calculates the maximum corner of a cuboid region defined by two locations.
     *
     * @param l1 the first location
     * @param l2 the second location
     * @return a BlockVector3 representing the maximum corner calculated
     *         by taking the largest x, y, and z values from the two locations
     */
    private BlockVector3 getMax(Location l1, Location l2) {
        return BlockVector3.at(Math.max(l1.getBlockX(), l2.getBlockX()), Math.max(l1.getBlockY(), l2.getBlockY()),
                Math.max(l1.getBlockZ(), l2.getBlockZ())
        );
    }
}