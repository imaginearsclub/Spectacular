package network.palace.show;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.craftmend.openaudiomc.api.MediaApi;
import com.craftmend.openaudiomc.spigot.OpenAudioMcSpigot;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import network.palace.show.commands.ShowCommand;
import network.palace.show.commands.ShowDebugCommand;
import network.palace.show.commands.ShowgenCommand;
import network.palace.show.generator.ShowGenerator;
import network.palace.show.listeners.ChunkListener;
import network.palace.show.listeners.PlayerInteract;
import network.palace.show.npc.SoftNPCManager;
import network.palace.show.utils.BuildUtil;
import network.palace.show.utils.FileUtil;
import network.palace.show.utils.UpdateUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Plugin(name = "Show", version = "1.6.0")
@Description(value = "Create Shows in Minecraft with easy-to-use files!")
@LoadOrder(value = PluginLoadOrder.POSTWORLD)
@Author(value = "Legobuilder0813")
@Author(value = "Parker")
@Author(value = "Innectic")
@Author(value = "Cubits")
@LogPrefix(value = "Show")
@Dependency(value = "WorldEdit")
@Dependency(value = "ProtocolLib")
@Dependency(value = "OpenAudioMc")
@ApiVersion(value = ApiVersion.Target.v1_13)
@Command(name = "show", desc = "Main show command", permission = "show.main", permissionMessage = "You do not have permission!", usage = "/show [list|start|stop]")
@Command(name = "showdebug", desc = "Showdebug command", permission = "show.debug", permissionMessage = "You do not have permission!", usage = "/showdebug")
@Command(name = "showgen", desc = "Showgen commands", permission = "show.showgen", permissionMessage = "You do not have permission!", usage = "/showgen")
public class ShowPlugin extends JavaPlugin {

    @Getter private ArmorStandManager armorStandManager;
    @Getter private FountainManager fountainManager;
    @Getter private static ShowGenerator showGenerator;
    @Getter private static BuildUtil buildUtil;
    @Getter private static SoftNPCManager softNPCManager;
    @Getter private final boolean isMinecraftGreaterOrEqualTo11_2 = MinecraftVersion.getCurrentVersion().getMinor() >= 12;
    @Getter private static MediaApi mediaApi;
    @Getter private static OpenAudioMcSpigot openAudioMcSpigot;
    @Getter private String githubToken;
    @Getter private String serverIp;

    private static ShowPlugin instance;
    private static final Map<String, Show> shows = new ConcurrentHashMap<>();
    private final Map<String, Boolean> debugMap = new ConcurrentHashMap<>();
    private int taskid;

    public static ShowPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        getSLF4JLogger().info("Enabling Show plugin...");

        // Initialize managers
        armorStandManager = new ArmorStandManager();
        fountainManager = new FountainManager();
        buildUtil = new BuildUtil();
        softNPCManager = new SoftNPCManager();

        // Initialize dependencies
        try {
            mediaApi = MediaApi.getInstance();
            openAudioMcSpigot = OpenAudioMcSpigot.getInstance();
            if (mediaApi == null || openAudioMcSpigot == null) {
                throw new IllegalStateException("MediaApi or OpenAudioMcSpigot are not properly initialized!");
            }
        } catch (Exception e) {
            getSLF4JLogger().error("Failed to initialize dependencies: {}", e.getMessage(), e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Setup files
        FileUtil.setupFiles();

        // Setup commands
        this.getCommand("show").setExecutor(new ShowCommand());
        this.getCommand("showdebug").setExecutor(new ShowDebugCommand());

        FileConfiguration config = this.getConfig();
        this.saveDefaultConfig();

        if (config.getString("github.token") != null) {
            githubToken = config.getString("github.token");
            this.getCommand("showgen").setExecutor(new ShowgenCommand());
            getSLF4JLogger().info("{}[Show] Showgen has been enabled in show!", NamedTextColor.GREEN);
        } else {
            getSLF4JLogger().warn("{}[Show] Showgen will not be running in Show! To enable it, add a github token to the config!", NamedTextColor.RED);
        }

        // Get server IP address (optional)
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream()))) {
            serverIp = in.readLine();
        } catch (Exception e) {
            getSLF4JLogger().warn("Could not obtain server IP. This feature is only used for Gist generation.");
        }

        // Initialize Show Generator
        showGenerator = new ShowGenerator();

        // Register events
        getServer().getPluginManager().registerEvents(fountainManager, this);
        getServer().getPluginManager().registerEvents(new PlayerInteract(), this);
        getServer().getPluginManager().registerEvents(new ChunkListener(), this);

        // Schedule Show Ticker
        taskid = getServer().getScheduler().runTaskTimer(this, this::tickShows, 0L, 20L).getTaskId();

        getSLF4JLogger().info("{}[Show] Plugin enabled successfully!", NamedTextColor.GREEN);
    }

    @Override
    public void onDisable() {
        getSLF4JLogger().info("{}[Show] Disabling Show plugin...", NamedTextColor.RED);

        // Cancel scheduler tasks
        if (taskid != 0) {
            getServer().getScheduler().cancelTask(taskid);
        }

        // Remove protocol listeners
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);

        // Stop all running shows
        for (Show show : shows.values()) {
            show.stop();
        }
        shows.clear();

        getSLF4JLogger().info("{}[Show] Plugin disabled successfully.", NamedTextColor.RED);
    }

    private void tickShows() {
        for (Map.Entry<String, Show> entry : new HashMap<>(shows).entrySet()) {
            Show show = entry.getValue();
            if (show.update()) {
                show.stop();
                shows.remove(entry.getKey());
            }
        }
    }

    public static void startShow(String name, Show show) {
        if (shows.containsKey(name)) {
            Bukkit.getLogger().warning("[Show] Show '" + name + "' already exists and will be overwritten.");
        }
        shows.put(name, show);
        Bukkit.getLogger().info("[Show] Show '" + name + "' started.");
    }

    public static void stopShow(String name) {
        if (shows.remove(name) != null) {
            Bukkit.getLogger().info("[Show] Show '" + name + "' stopped.");
        } else {
            Bukkit.getLogger().warning("[Show] Attempted to stop non-existent show: " + name);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("show.main")) {
        }
    }
}