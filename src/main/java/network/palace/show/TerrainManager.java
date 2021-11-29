package network.palace.show;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.Closer;
import network.palace.show.utils.ShowUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.Objects;

@SuppressWarnings("deprecation")
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
        LocalSession worldEditSession = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
        we = wep.getWorldEdit();
        localSession = worldEditSession;
        localPlayer = player;
        editSession = worldEditSession.createEditSession(BukkitAdapter.adapt(player));
    }

    /**
     * Constructor
     *
     * @param wep   the WorldEdit plugin instance
     * @param world the world to work in
     */
    public TerrainManager(WorldEditPlugin wep, World world) {
        we = wep.getWorldEdit();
        localPlayer = null;
        localSession = new LocalSession(we.getConfiguration());
        editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world));
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
        BlockVector3 min = getMin(l1, l2);
        BlockVector3 max = getMax(l1, l2);

        saveFile = we.getSafeSaveFile(BukkitAdapter.adapt(localPlayer), saveFile.getParentFile(), saveFile.getName(), ".schematic", ".schematic");

        CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(Objects.requireNonNull(l1.getWorld())), min, max);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1);

        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
        forwardExtentCopy.setCopyingEntities(true);
        Operations.complete(forwardExtentCopy);
        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(saveFile))) {
            writer.write(clipboard);
        }
    }

    public void loadSchematic(WorldEditPlugin wep, String fileName, Location loc, boolean noAir) throws Exception {
        File f = wep.getWorldEdit().getSafeOpenFile(null, new File("plugins/WorldEdit/schematics"), fileName,
                "schematic", "schematic");
        if (!f.exists()) {
            ShowUtil.logDebug("Schematics", "Tried to load Schematic " + fileName + " but does not exist!");
            return;
        }
        ClipboardFormat format = ClipboardFormats.findByFile(f);
        try (ClipboardReader reader = format.getReader(new FileInputStream(f))) {
            Clipboard clipboard = reader.read();
            localSession.setClipboard(new ClipboardHolder(clipboard));
            Region region = clipboard.getRegion();
            BlockVector3 to = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
            Operation operation = localSession.getClipboard().createPaste(editSession).to(to).ignoreAirBlocks(noAir).build();
            Operations.completeLegacy(operation);
        }
    }

    public void loadSchematic(WorldEditPlugin wep, String fileName, boolean noAir) throws Exception {
        loadSchematic(wep, fileName, null, noAir);
    }

    private BlockVector3 getMin(Location l1, Location l2) {
        return BlockVector3.at(Math.min(l1.getBlockX(), l2.getBlockX()), Math.min(l1.getBlockY(), l2.getBlockY()),
                Math.min(l1.getBlockZ(), l2.getBlockZ())
        );
    }

    private BlockVector3 getMax(Location l1, Location l2) {
        return BlockVector3.at(Math.max(l1.getBlockX(), l2.getBlockX()), Math.max(l1.getBlockY(), l2.getBlockY()),
                Math.max(l1.getBlockZ(), l2.getBlockZ())
        );
    }
}