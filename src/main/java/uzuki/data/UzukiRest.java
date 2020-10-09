package uzuki.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import uzuki.UzukiServer;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class UzukiRest {
    private final WebClient client;
    private final String remoteVersionSource;
    private final String remoteShipSource;
    private final String remoteMiscSource;

    public UzukiRest(UzukiServer uzuki) {
        this.client = WebClient.create(uzuki.vertx, new WebClientOptions().setUserAgent("Uzuki/" + uzuki.version));
        this.remoteVersionSource = "https://raw.githubusercontent.com/kcwiki/kancolle-data/master/package.json";
        this.remoteShipSource = "https://raw.githubusercontent.com/kcwiki/kancolle-data/master/wiki/ship.json";
        this.remoteMiscSource = "https://raw.githubusercontent.com/kcwiki/kancolle-data/master/wiki/misc.json";
    }

    public CompletableFuture<String> getRemoteVersion() {
        CompletableFuture<String> result = new CompletableFuture<>();
        this.client.requestAbs(HttpMethod.GET, this.remoteVersionSource)
                .send(response -> {
                    if (response.failed()) {
                        Throwable throwable = response.cause();
                        if (throwable == null) throwable = new Throwable("Can't fetch remote version info");
                        result.completeExceptionally(throwable);
                        return;
                    }
                    Gson gson = new Gson();
                    JsonObject data = gson.fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonObject.class);
                    result.complete(data.get("version").getAsString());
                });
        return result;
    }

    public CompletableFuture<JsonObject> getRemoteShips() {
        CompletableFuture<JsonObject> result = new CompletableFuture<>();
        this.client.requestAbs(HttpMethod.GET, this.remoteShipSource)
                .send(response -> {
                    if (response.failed()) {
                        Throwable throwable = response.cause();
                        if (throwable == null) throwable = new Throwable("Can't fetch remote ship data");
                        result.completeExceptionally(throwable);
                        return;
                    }
                    Gson gson = new Gson();
                    result.complete(gson.fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonObject.class));
                });
        return result;
    }

    public CompletableFuture<JsonObject> getRemoteMisc() {
        CompletableFuture<JsonObject> result = new CompletableFuture<>();
        this.client.requestAbs(HttpMethod.GET, this.remoteMiscSource)
                .send(response -> {
                    if (response.failed()) {
                        Throwable throwable = response.cause();
                        if (throwable == null) throwable = new Throwable("Can't fetch remote misc data");
                        result.completeExceptionally(throwable);
                        return;
                    }
                    Gson gson = new Gson();
                    result.complete(gson.fromJson(response.result().bodyAsString(StandardCharsets.UTF_8.name()), JsonObject.class));
                });
        return result;
    }
}
