package network.palace.show.actions;

import com.craftmend.openaudiomc.api.clients.Client;
import com.craftmend.openaudiomc.api.media.MediaOptions;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.MiscUtil;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AudioPlaceOnceAction extends ShowAction {

    private static final Logger LOGGER = Logger.getLogger(AudioPlaceOnceAction.class.getName());
    private static final int DEFAULT_VOLUME = 100; // Default volume to use if none is provided

    private String audioUrl;
    private Integer volume;

    public AudioPlaceOnceAction(Show show, long time) {
        super(show, time);
    }

    @Override
    public void play(Player[] nearPlayers) {
        if (audioUrl == null || audioUrl.isEmpty()) {
            LOGGER.log(Level.WARNING, "Audio URL is null or empty. Skipping playback.");
            return;
        }

        for (Player player : nearPlayers) {
            playForPlayer(player);
        }
    }

    private void playForPlayer(Player player) {
        if (player == null) {
            LOGGER.log(Level.WARNING, "Null player encountered. Skipping.");
            return;
        }

        Client client = ShowPlugin.getMediaApi().getClient(player.getUniqueId());
        if (client == null) {
            LOGGER.log(Level.WARNING, "Client not found for player: {0}", player.getName());
            return;
        }

        if (!client.isConnected()) {
            LOGGER.log(Level.INFO, "Client for player {0} is not connected. Skipping playback.", player.getName());
            return;
        }

        try {
            MediaOptions mediaOptions = new MediaOptions();
            mediaOptions.setId(show.getName());
            mediaOptions.setVolume(volume != null ? volume : DEFAULT_VOLUME); // Use default if volume is null
            ShowPlugin.getMediaApi().getMediaApi().playMedia(client, audioUrl, mediaOptions);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while playing media for player {0}: {1}",
                    new Object[]{player.getName(), e.getMessage()});
            e.printStackTrace();
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        if (args == null || args.length < 4) {
            throw new ShowParseException("Insufficient arguments provided! Expected at least 4 arguments.");
        }

        try {
            if (!MiscUtil.checkIfInt(args[2])) {
                throw new ShowParseException("The volume provided is not an integer!");
            }
            this.volume = Integer.valueOf(args[2]);

            this.audioUrl = args[3];
            if (audioUrl == null || audioUrl.trim().isEmpty()) {
                throw new ShowParseException("Audio URL cannot be null or empty!");
            }
        } catch (NumberFormatException e) {
            throw new ShowParseException("Invalid number format for volume: " + args[2], e);
        }

        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        throw new ShowParseException("The AudioPlaceOnceAction action does not support repeating.");
    }
}