package network.palace.show.utils;

import network.palace.show.packets.AbstractPacket;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MiscUtil {

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static <T> boolean contains(T[] ts, T t) {
        if (t == null || ts == null) return false;
        for (T t1 : ts) {
            if (t1 == null) continue;
            if (t1.equals(t)) return true;
        }
        return false;
    }

    public static final HashMap<BlockFace, Float> DIRECTIONAL_YAW = new HashMap<BlockFace, Float>() {{
        put(BlockFace.NORTH, 180F);
        put(BlockFace.EAST, -90F);
        put(BlockFace.SOUTH, 0F);
        put(BlockFace.WEST, 90F);
    }};

    /**
     * Check if is integer.
     *
     * @param toCheck the string to check
     * @return if is integer
     */
    public static boolean checkIfInt(String toCheck) {
        try {
            Integer.parseInt(toCheck);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void sendPacket(AbstractPacket packet, Player p) {
        if (p == null) return;
        if (packet == null) return;
        packet.sendPacket(p);
    }

}
