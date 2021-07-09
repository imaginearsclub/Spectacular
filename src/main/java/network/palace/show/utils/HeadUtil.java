package network.palace.show.utils;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

/**
 * Written by Marc (Legobuilder), Clutch and Innectic <3
 * https://github.com/PalaceInteractiveArchive/Core/blob/f663582d6cb3734c9bde98dbdd1eaeb5949d039c/src/main/java/network/palace/core/utils/HeadUtil.java
 */
public class HeadUtil {

    /**
     * Get a player skull ItemStack from a texture hash
     *
     * @param hash hash
     * @return ItemStack player head
     */
    public static ItemStack getPlayerHead(String hash) {
        return getPlayerHead(hash, "Head");
    }

    /**
     * Get a player skull ItemStack with a custom name from a texture hash
     *
     * @param hash    hash
     * @param display display
     * @return ItemStack player head
     */
    public static ItemStack getPlayerHead(String hash, String display) {
        ItemStack head = getHead(hash);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(display + ChatColor.RESET);
        head.setItemMeta(meta);
        return head;
    }

    /**
     * Get a player skull ItemStack from a texture hash
     *
     * @param hash hash
     * @return ItemStack player head
     */
    private static ItemStack getHead(String hash) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        item = MinecraftReflection.getBukkitItemStack(item);
        NbtCompound nbt = NbtFactory.asCompound(NbtFactory.fromItemTag(item));
        NbtCompound texture = NbtFactory.ofCompound("");
        texture.put("Value", hash);
        NbtList<NbtCompound> textures = NbtFactory.ofList("textures", texture);
        NbtCompound properties = NbtFactory.ofCompound("Properties");
        properties.put(textures);
        NbtCompound skullOwner = NbtFactory.ofCompound("SkullOwner");
        skullOwner.put("Id", UUID.randomUUID().toString());
        skullOwner.put(properties);
        nbt.put(skullOwner);
        return item;
    }
}
