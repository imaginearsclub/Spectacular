package network.palace.show.npc;


import org.bukkit.entity.Player;

public interface NPCObserver {
    void onPlayerInteract(Player player, AbstractEntity entity, ClickAction action);
}
