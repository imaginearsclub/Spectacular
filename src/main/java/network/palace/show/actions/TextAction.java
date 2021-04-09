package network.palace.show.actions;

import network.palace.core.player.CPlayer;
import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;

public class TextAction extends ShowAction {
    private String text;

    public TextAction(Show show, long time) {
        super(show, time);
    }

    @Override
    public boolean play(CPlayer[] nearPlayers) {
        show.displayText(text);
        return true;
    }

    @Override
    public ShowAction load(String line, String... args) throws ShowParseException {
        StringBuilder text = new StringBuilder();
        for (int i = 2; i < args.length; i++)
            text.append(args[i]).append(" ");
        if (text.length() > 1)
            text = new StringBuilder(text.substring(0, text.length() - 1));
        this.text = text.toString();
        return this;
    }
}
