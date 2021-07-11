package network.palace.show.sequence.fountain;

import network.palace.show.Show;
import network.palace.show.exceptions.ShowParseException;
import network.palace.show.sequence.ShowSequence;
import network.palace.show.utils.ShowUtil;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

/**
 * @author Marc
 * @since 8/2/17
 */
@SuppressWarnings("deprecation")
public class FountainBlockSequence extends ShowSequence {
    private final FountainSequence parent;
    private MaterialData data;

    public FountainBlockSequence(Show show, long time, FountainSequence parent) {
        super(show, time);
        this.parent = parent;
    }

    @Override
    public boolean run() {
        if (!parent.isSpawned()) {
            return true;
        }
        parent.data = data;
        return true;
    }

    @Override
    public ShowSequence load(String line, String... args) throws ShowParseException {
        String[] list = args[2].split(":");
        if (list.length == 1) {
            data = new MaterialData(ShowUtil.convertMaterialNoData(Integer.parseInt(list[0])));
        } else {
            data = new MaterialData(ShowUtil.convertMaterial(Integer.parseInt(list[0]), Byte.parseByte(list[1])));
        }
        return this;
    }
}
