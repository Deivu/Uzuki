package uzuki.endpoints;

import com.google.gson.JsonArray;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import uzuki.UzukiServer;
import uzuki.struct.UzukiEndpointContext;
import uzuki.struct.UzukiSearchResult;
import uzuki.struct.UzukiShip;

import java.util.List;
import java.util.stream.Collectors;

public class UzukiShipEndpoint {
    private UzukiServer uzuki;

    public UzukiShipEndpoint(UzukiServer uzuki) { this.uzuki = uzuki; }

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
