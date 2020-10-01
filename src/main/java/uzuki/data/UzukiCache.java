package uzuki.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uzuki.UzukiServer;
import uzuki.struct.UzukiShip;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UzukiCache {
    private final UzukiServer uzuki;
    public final ConcurrentHashMap<String, JsonElement> misc;
    public final CopyOnWriteArrayList<UzukiShip> ships;
    public UzukiCache(UzukiServer uzuki) {
        this.uzuki = uzuki;
        this.misc = new ConcurrentHashMap<>();
        this.ships = new CopyOnWriteArrayList<>();
    }

    public void updateMiscCache(JsonObject data) {
        if (data.keySet().size() == 0) return;
        if (!this.misc.isEmpty()) this.misc.clear();
        for (String key : data.keySet()) this.misc.put(key, data.get(key));
    }

    public void updateShipCache(JsonObject data) {
        if (data.keySet().size() == 0) return;
        if (!this.ships.isEmpty()) this.ships.clear();
        for (String key : data.keySet())
            this.ships.add(new UzukiShip(this.uzuki, key, data.get(key).getAsJsonObject()));
    }
}
