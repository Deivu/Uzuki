package uzuki;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import uzuki.data.UzukiCache;
import uzuki.data.UzukiRest;
import uzuki.data.UzukiStore;
import uzuki.util.UzukiConfig;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class UzukiServer {
    public final String version;
    public final UzukiConfig uzukiConfig;
    public final Vertx vertx;
    public final UzukiRest uzukiRest;
    public final UzukiStore uzukiStore;
    public final UzukiCache uzukiCache;

    public UzukiServer() throws IOException, URISyntaxException {
        this.version = getClass().getPackage().getImplementationVersion() != null ? getClass().getPackage().getImplementationVersion() : "dev";
        this.uzukiConfig = new UzukiConfig();
        this.vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(this.uzukiConfig.threads));
        this.uzukiRest = new UzukiRest(this);
        this.uzukiStore = new UzukiStore(this);
        this.uzukiCache = new UzukiCache();
    }

    public void startServer() throws ExecutionException, InterruptedException {
        if (this.uzukiStore.needsUpdate()) this.uzukiStore.updateLocalData();
        this.uzukiCache.updateShipCache(this.uzukiStore.getLocalShipsData());
        // test stuff
        System.out.println(this.uzukiCache.ships.size());
        System.out.println(this.uzukiCache.ships.get(125).name);
    }
}
