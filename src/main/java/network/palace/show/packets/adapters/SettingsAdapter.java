package network.palace.show.packets.adapters;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import network.palace.show.ShowPlugin;
import org.bukkit.entity.Player;


/**
 * The type Settings adapter.
 */
public class SettingsAdapter extends PacketAdapter {

    /**
     * Instantiates a new Settings adapter.
     */
    public SettingsAdapter() {
        super(ShowPlugin.getInstance(), PacketType.Play.Client.SETTINGS);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event == null) return;
        if (event.getPlayer() == null) return;
        Player player = event.getPlayer();
        if (player == null) return;
        if (event.getPacket() == null) return;
        if (event.getPacket().getStrings() == null) return;
        if (event.getPacket().getStrings().read(0) == null) return;
    }
}
