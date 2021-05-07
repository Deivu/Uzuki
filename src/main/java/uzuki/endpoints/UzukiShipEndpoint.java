package uzuki.endpoints;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import uzuki.UzukiServer;
import uzuki.struct.UzukiEndpointContext;
import uzuki.struct.UzukiSearchResult;
import uzuki.struct.UzukiShip;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class UzukiShipEndpoint {
    private UzukiServer uzuki;

    public UzukiShipEndpoint(UzukiServer uzuki) { this.uzuki = uzuki; }

    public void shipClass(UzukiEndpointContext uzukiContext) {
        List<UzukiShip> data = this.uzuki.uzukiCache.ships.stream()
                .filter(ship -> ship.shipClass.toLowerCase().equals(uzukiContext.queryString.toLowerCase()))
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (UzukiShip obj : data) json.add(obj.data);
        uzukiContext.response.end(json.toString());
    }

    public void id(UzukiEndpointContext uzukiContext) {
        List<UzukiShip> data = this.uzuki.uzukiCache.ships.stream()
                .filter(ship -> ship.id.equals(uzukiContext.queryString))
                .collect(Collectors.toList());
        JsonObject json = new JsonObject();
        if (!data.isEmpty()) json = data.get(0).data;
        uzukiContext.response.end(json.toString());
    }

    public void random(UzukiEndpointContext uzukiContext) {
        int random = new Random().nextInt(this.uzuki.uzukiCache.ships.size() - 1);
        UzukiShip data = this.uzuki.uzukiCache.ships.get(random);
        uzukiContext.response.end(data.toString());
    }

    public void search(UzukiEndpointContext uzukiContext) {
        List<UzukiShip> data = this.uzuki.uzukiCache.ships.stream()
                .map(ship -> new UzukiSearchResult(FuzzySearch.weightedRatio(uzukiContext.queryString, ship.name), ship))
                .filter(result ->  result.score > this.uzuki.uzukiConfig.searchWeight)
                .sorted((a, b) -> b.score - a.score)
                .limit(this.uzuki.uzukiConfig.maxResults)
                .map(result -> result.ship)
                .collect(Collectors.toList());
        JsonArray json = new JsonArray();
        for (UzukiShip obj : data) json.add(obj.data);
        uzukiContext.response.end(json.toString());
    }
}
