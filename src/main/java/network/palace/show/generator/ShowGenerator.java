package network.palace.show.generator;

import com.goebl.david.Webb;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.show.ShowPlugin;
import network.palace.show.actions.FakeBlockAction;
import org.bukkit.Location;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ShowGenerator {
    private static final String ACCESS_TOKEN = "550a319f2ef3b2e0d489a761b6e12ea88f7f8b4f";
    private HashMap<UUID, GeneratorSession> generatorSessions = new HashMap<>();

    public GeneratorSession getSession(UUID uuid) {
        return generatorSessions.get(uuid);
    }

    public GeneratorSession getOrCreateSession(UUID uuid) {
        GeneratorSession session = getSession(uuid);
        if (session == null) {
            session = new GeneratorSession(uuid);
            addSession(session);
        }
        return session;
    }

    public void addSession(GeneratorSession session) {
        generatorSessions.put(session.getUuid(), session);
    }

    public void removeSession(UUID uuid) {
        generatorSessions.remove(uuid);
    }

    public String postGist(List<FakeBlockAction> actions, String name) throws Exception {
        Webb webb = Webb.create();

        JsonObject obj = new JsonObject();
        obj.addProperty("description", "Generated by Show v" + ShowPlugin.getInstance().getDescription().getVersion() + " on " + Core.getInstanceName() + " at " + System.currentTimeMillis());
        obj.addProperty("public", "false");

        JsonObject files = new JsonObject();
        JsonObject file = new JsonObject();

        StringBuilder content = new StringBuilder();

        for (FakeBlockAction action : actions) {
            Location loc = action.getLoc();
            double time = ((int) ((action.getTime() / 1000.0) * 10)) / 10.0;
            int id = action.getId();
            byte data = action.getData();
            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();
            String actionString = time + "\u0009" + "FakeBlock" + "\u0009" + id + ":" + data + "\u0009" + x + "," + y + "," + z;
            content.append(actionString).append("\n");
        }

        file.addProperty("content", content.toString());

        files.add(name + ".show", file);

        obj.add("files", files);

        System.out.println("SENDING (" + actions.size() + "): " + obj.toString());

        JSONObject response = webb.post("https://api.github.com/gists")
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", "token " + ACCESS_TOKEN)
                .header("Content-Type", "application/json")
                .body(obj).asJsonObject().getBody();
        return response.getString("html_url");
    }
}
