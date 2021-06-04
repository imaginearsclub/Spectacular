package network.palace.show.actions;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.packets.packets.BotNotification;
import org.bukkit.ChatColor;

import java.io.IOException;

public class DiscordAction extends ShowAction {
    private String title;
    private String channelId;
    private String desc;
    private String startTime;
    private String whereToWatch;


    public DiscordAction(Show show, long time) {
        super(show, time);
    }

    public DiscordAction(Show show, long time, String channelId, String title, String desc, String startTime, String whereToWatch) {
        super(show, time);
        this.title = title;
        this.channelId = channelId;
        this.desc = desc;
        this.startTime = startTime;
        this.whereToWatch = whereToWatch;
    }

    @Override
    public void play(CPlayer[] nearPlayers) {
        try {
            Core.getMessageHandler().sendMessage(new BotNotification(channelId, title, desc, startTime, whereToWatch), Core.getMessageHandler().BOT);
        } catch (IOException e) {
            try {
                Core.getMessageHandler().sendStaffMessage(ChatColor.RED + Core.getInstanceName() + " Failed on sending BotNotification packet via Core " + Core.getVersion() + " - Please alert the development team ASAP");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        if (args[0] == null) throw new ShowParseException("Invalid channelId " + line);
        if (args[1] == null) throw new ShowParseException("Invalid title " + line);
        if (args[2] == null) throw new ShowParseException("Invalid desc " + line);
        if (args[3] == null) throw new ShowParseException("Invalid startTime " + line);
        if (args[4] == null) throw new ShowParseException("Invalid whereToWatch " + line);
        this.channelId = args[0];
        this.title = args[1];
        this.desc = args[2];
        this.startTime = args[3];
        this.whereToWatch = args[4];
        return this;
    }

    @Override
    protected ShowAction copy(Show show, long time) throws ShowParseException {
        // Action should not be repeatable to prevent spamming
        return null;
    }

}
