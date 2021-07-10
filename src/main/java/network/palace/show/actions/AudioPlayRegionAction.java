package network.palace.show.actions;

import com.craftmend.openaudiomc.spigot.modules.regions.objects.TimedRegionProperties;
import network.palace.show.Show;
import network.palace.show.ShowPlugin;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.utils.MiscUtil;
import org.bukkit.entity.Player;

public class AudioPlayRegionAction extends ShowAction {
    private String audioUrl;
    private Integer duration;
    private String regionName;
    private Integer volume;

    public AudioPlayRegionAction(Show show, long time) {
        super(show, time);
    }

    @Override
    public void play(Player[] nearPlayers) {
        TimedRegionProperties timedRegionProperties = new TimedRegionProperties(audioUrl, duration, regionName);
        timedRegionProperties.setVolume(volume);
        ShowPlugin.getOpenAudioMcSpigot().getRegionModule().registerRegion(regionName, timedRegionProperties);
        ShowPlugin.getOpenAudioMcSpigot().getRegionModule().forceUpdateRegions();
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        if (!ShowPlugin.getOpenAudioMcSpigot().getRegionModule().getRegionAdapter().doesRegionExist(args[2])) {
            throw new ShowParseException("The specified worldguard region does not exist!");
        }
        if (!MiscUtil.checkIfInt(args[3])) {
            throw new ShowParseException("The duration provided is not an integer!");
        }
        if (!MiscUtil.checkIfInt(args[4])) {
            throw new ShowParseException("The volume provided is not an integer!");
        }
        this.regionName = args[2];
        this.duration = Integer.valueOf(args[3]);
        this.volume = Integer.valueOf(args[4]);
        this.audioUrl = args[5];
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        throw new ShowParseException("This action doesn't support repeating!");
    }
}
