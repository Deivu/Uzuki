package uzuki.struct;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uzuki.UzukiServer;

public class UzukiShip {
    private final UzukiServer uzuki;
    public final String name;
    public final String id;
    public final String shipClass;
    public final JsonObject data;

    public UzukiShip(UzukiServer uzuki, String name, JsonObject data) {
        this.uzuki = uzuki;
        this.name = name;
        this.id = data.get("_id").getAsString();
        this.shipClass = data.get("_class").getAsString();
        this.data = data.deepCopy();
        this.constructData("_type", "ShipTypes");
        this.constructData("_speed", "SpeedNames");
        this.constructData("_range", "RangeNames");
    }

    private void constructData(String property, String miscKey) {
        JsonElement shipElement = this.data.get(property);
        if (shipElement == null) return;
        JsonElement miscElement = this.uzuki.uzukiCache.misc.get(miscKey);
        if (miscElement == null) return;
        JsonElement newElement = miscElement.getAsJsonObject().get(shipElement.getAsString());
        this.data.remove(property);
        this.data.add(property, newElement);
    }
}
