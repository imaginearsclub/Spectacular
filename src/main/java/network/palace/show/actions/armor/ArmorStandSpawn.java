package network.palace.show.actions.armor;

import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.actions.ShowAction;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.handlers.ArmorData;
import network.palace.show.handlers.armorstand.ShowStand;
import network.palace.show.utils.ShowUtil;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Handles spawning an ArmorStand as part of a show action.
 */
public class ArmorStandSpawn extends ShowAction {
    private final ShowStand stand;
    private final Location location;

    public ArmorStandSpawn(Show show, long time, ShowStand stand, Location location) {
        super(show, time);
        this.stand = stand;
        this.location = location;
    }

    @Override
    public void play(Player[] nearPlayers) {
        if (stand.isHasSpawned()) {
            ShowUtil.logDebug(show.getName(), "ArmorStand with ID " + stand.getId() + " has already been spawned.");
            return;
        }

        // Null safety for location and world
        if (location == null || location.getWorld() == null) {
            ShowUtil.logDebug(show.getName(), "Cannot spawn ArmorStand: Invalid location or world is null.");
            return;
        }

        // Spawn the ArmorStand entity
        ArmorStand armorStand = spawnArmorStand(location);

        // Configure the ArmorStand entity
        setupArmorStand(armorStand);

        // Set the spawned ArmorStand in the ShowStand
        stand.setStand(armorStand);

        // Mark the stand as spawned
        stand.spawn();
    }

    /**
     * Spawns an ArmorStand at the given location.
     *
     * @param location The spawn location.
     * @return The spawned ArmorStand entity.
     */
    private ArmorStand spawnArmorStand(Location location) {
        return location.getWorld().spawn(location, ArmorStand.class);
    }

    /**
     * Configures the provided ArmorStand with the settings from the ShowStand.
     *
     * @param armorStand The ArmorStand to configure.
     */
    private void setupArmorStand(ArmorStand armorStand) {
        armorStand.setCustomName(stand.getId());
        armorStand.setArms(true);
        armorStand.setBasePlate(false);
        armorStand.setGravity(false);
        armorStand.setSilent(true);
        armorStand.setSmall(stand.isSmall());

        // Set unique metadata to identify this ArmorStand
        armorStand.setMetadata("palace_show", new FixedMetadataValue(ShowPlugin.getInstance(), true));

        // Configure ArmorStand with armor items
        setupArmorData(armorStand, stand.getArmorData());
    }

    /**
     * Applies armor and hand items to the ArmorStand based on ArmorData.
     *
     * @param armorStand The ArmorStand to configure.
     * @param armorData  The ArmorData containing the items to apply.
     */
    private void setupArmorData(ArmorStand armorStand, ArmorData armorData) {
        if (armorData == null) return;

        if (armorData.getHead() != null) {
            armorStand.getEquipment().setHelmet(armorData.getHead());
        }
        if (armorData.getChestplate() != null) {
            armorStand.getEquipment().setChestplate(armorData.getChestplate());
        }
        if (armorData.getLeggings() != null) {
            armorStand.getEquipment().setLeggings(armorData.getLeggings());
        }
        if (armorData.getBoots() != null) {
            armorStand.getEquipment().setBoots(armorData.getBoots());
        }
        if (armorData.getItemInMainHand() != null) {
            armorStand.getEquipment().setItem(EquipmentSlot.HAND, armorData.getItemInMainHand());
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        // Potential improvement: Parse `line` and `args` to dynamically load the ArmorStand spawn action
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        // Clone the location to avoid sharing mutable references
        Location clonedLocation = location != null ? location.clone() : null;

        // Clone the ShowStand if it is mutable (if applicable in the `ShowStand` class)
        ShowStand clonedStand = stand; // Adjust this if `ShowStand` is not immutable

        return new ArmorStandSpawn(show, time, clonedStand, clonedLocation);
    }
}