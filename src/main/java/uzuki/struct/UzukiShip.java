package uzuki.struct;

import com.google.gson.JsonObject;

public class UzukiShip {
    public final String id;
    public final String shipClass;
    public final String name;
    public final JsonObject data;

    public UzukiShip(String name, JsonObject data) {
        this.id = data.get("_id").getAsString();
        this.shipClass = data.get("_class").getAsString();
        this.name = name;
        this.data = data;
    }
}
