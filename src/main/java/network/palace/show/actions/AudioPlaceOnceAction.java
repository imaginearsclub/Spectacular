package network.palace.show.actions;

import com.craftmend.openaudiomc.api.interfaces.Client;
import com.craftmend.openaudiomc.generic.media.objects.MediaOptions;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.MiscUtil;
import org.bukkit.entity.Player;

public class AudioPlaceOnceAction extends ShowAction {
    private String audioUrl;
    private Integer volume;

    public AudioPlaceOnceAction(Show show, long time) {
        super(show, time);
    }

    @Override
    public void play(Player[] nearPlayers) {
        for (Player p : nearPlayers) {
            Client tp = ShowPlugin.getAudioApi().getClient(p.getUniqueId());
            if (tp != null) {
                if (tp.isConnected()) {
                    MediaOptions mediaOptions = new MediaOptions();
                    mediaOptions.setId(show.getName());
                    mediaOptions.setVolume(volume);
                    ShowPlugin.getAudioApi().getMediaApi().playMedia(tp, audioUrl, mediaOptions);
                }
            }
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        if (!MiscUtil.checkIfInt(args[2])) {
            throw new ShowParseException("The volume provided is not an integer!");
        }
        this.audioUrl = args[3];
        this.volume = Integer.valueOf(args[2]);
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        throw new ShowParseException("This action doesn't support repeating!");
    }
}
