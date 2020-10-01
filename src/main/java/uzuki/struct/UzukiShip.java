package uzuki.struct;

import com.google.gson.JsonObject;

public class UzukiShip {
    public final String name;
    public final JsonObject data;

    public UzukiShip(String name, JsonObject data) {
        this.name = name;
        this.data = data;
    }
}
