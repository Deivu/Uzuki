package uzuki.data;

import com.google.gson.JsonObject;
import uzuki.UzukiServer;
import uzuki.struct.UzukiShip;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UzukiCache {
    public final CopyOnWriteArrayList<UzukiShip> ships;

    public UzukiCache() {
        this.ships = new CopyOnWriteArrayList<>();
    }

    public void updateShipCache(JsonObject data) {
        if (data.keySet().size() == 0) return;
        if (!this.ships.isEmpty()) this.ships.clear();
        for (String key : data.keySet())
            this.ships.add(new UzukiShip(key, data.get(key).getAsJsonObject()));
    }
}
