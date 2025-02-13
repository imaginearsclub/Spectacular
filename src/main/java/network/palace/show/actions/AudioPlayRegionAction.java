package network.palace.show.actions;

import com.craftmend.openaudiomc.api.WorldApi;
import com.craftmend.openaudiomc.api.exceptions.InvalidRegionException;
import com.craftmend.openaudiomc.api.exceptions.InvalidThreadException;
import com.craftmend.openaudiomc.api.exceptions.UnknownWorldException;
import com.craftmend.openaudiomc.api.regions.RegionMediaOptions;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import org.bukkit.Bukkit;
import org.bukkit.World;
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
        // Get the world for the player(s) or some other source
        World world = Bukkit.getWorld("world"); // Replace with your actual logic for retrieving the world
        if (world == null) {
            Bukkit.getLogger().severe("World 'world' does not exist! Cannot register region.");
            return;
        }

        String worldName = world.getName(); // Get the world name

        // Define audio options for the region
        RegionMediaOptions options = new RegionMediaOptions(audioUrl);
        options.setVolume(volume != null ? volume : 50); // Default volume to 50 if null

        try {
            // Register the region with the WorldApi
            WorldApi.getInstance().registerRegion(worldName, regionName, options);
            Bukkit.getLogger().info("Successfully registered audio region '" + regionName + "' in world '" + worldName + "'");
        } catch (UnknownWorldException e) {
            Bukkit.getLogger().severe("Failed to register region: Unknown world '" + worldName + "'");
        } catch (InvalidThreadException e) {
            Bukkit.getLogger().severe("Failed to register region: Invalid thread use! Ensure this is called on the main server thread.");
        } catch (InvalidRegionException e) {
            Bukkit.getLogger().severe("Failed to register region: Invalid region parameters for '" + regionName + "'");
        }
    }

    // Validate the properties before registering the region
    private void validateProperties() throws ShowParseException {
        if (regionName == null || regionName.isBlank()) {
            throw new ShowParseException("Region name cannot be null or empty.");
        }
        if (audioUrl == null || audioUrl.isBlank()) {
            throw new ShowParseException("Audio URL cannot be null or empty.");
        }
        if (volume != null && (volume < 0 || volume > 100)) {
            throw new ShowParseException("Volume must be between 0 and 100.");
        }
        if (duration != null && duration < 0) {
            throw new ShowParseException("Duration cannot be negative.");
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        // Example: audioRegion|region-name|https://example.com/audio.mp3|50|30
        if (args.length < 3) {
            throw new ShowParseException("Invalid number of arguments.");
        }

        this.regionName = args[0];
        this.audioUrl = args[1];

        // Parse optional volume and duration
        this.volume = args.length >= 3 ? Integer.parseInt(args[2]) : null;
        this.duration = args.length >= 4 ? Integer.parseInt(args[3]) : null;

        // Validate the provided properties
        validateProperties();

        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        AudioPlayRegionAction copy = new AudioPlayRegionAction(show, time);
        copy.audioUrl = this.audioUrl;
        copy.duration = this.duration;
        copy.regionName = this.regionName;
        copy.volume = this.volume;

        return copy;
    }
}