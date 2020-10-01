package uzuki;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uzuki.data.UzukiCache;
import uzuki.data.UzukiRest;
import uzuki.data.UzukiStore;
import uzuki.endpoints.UzukiEndpointManager;
import uzuki.util.UzukiConfig;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UzukiServer {
    public final Logger logger;
    public final String version;
    public final UzukiConfig uzukiConfig;
    public final Vertx vertx;
    public final UzukiRest uzukiRest;
    public final UzukiStore uzukiStore;
    public final UzukiCache uzukiCache;
    public final UzukiEndpointManager uzukiEndpointManager;

    private final ScheduledExecutorService singleThreadScheduler;
    private final HttpServer server;
    private final Router mainRouter;
    private final Router apiRoutes;
    private final String[] getEndpoints;

    public UzukiServer() throws IOException, URISyntaxException {
        this.logger = LoggerFactory.getLogger(UzukiServer.class);
        this.version = getClass().getPackage().getImplementationVersion() != null ? getClass().getPackage().getImplementationVersion() : "dev";
        this.uzukiConfig = new UzukiConfig();
        this.vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(this.uzukiConfig.threads));
        this.uzukiRest = new UzukiRest(this);
        this.uzukiStore = new UzukiStore(this);
        this.uzukiCache = new UzukiCache();
        this.uzukiEndpointManager = new UzukiEndpointManager(this);
        this.singleThreadScheduler = Executors.newSingleThreadScheduledExecutor();
        this.server = this.vertx.createHttpServer();
        this.mainRouter = Router.router(vertx);
        this.apiRoutes = Router.router(vertx);
        this.getEndpoints = new String[] { "/ship/search", "/ship/id", "/ship/class"  };
    }

    public UzukiServer buildRest() {
        for (String endpoint : getEndpoints)
            apiRoutes.route(HttpMethod.GET, endpoint)
                    .blockingHandler(context -> this.uzukiEndpointManager.executeGet(endpoint, context), false)
                    .failureHandler(this.uzukiEndpointManager::executeFail)
                    .enable();
        apiRoutes.route(HttpMethod.POST, "/update")
                .blockingHandler(this.uzukiEndpointManager::executeUpdate, false)
                .failureHandler(this.uzukiEndpointManager::executeFail)
                .enable();
        apiRoutes.route("/*")
                .handler(StaticHandler.create().setIndexPage("hiei.html"))
                .failureHandler(this.uzukiEndpointManager::executeFail)
                .enable();
        mainRouter.mountSubRouter(this.uzukiConfig.routePrefix, apiRoutes);
        return this;
    }

    public UzukiServer startServer() throws ExecutionException, InterruptedException {
        if (this.uzukiStore.needsUpdate()) this.uzukiStore.updateLocalData();
        this.uzukiCache.updateShipCache(this.uzukiStore.getLocalShipsData());
        server.requestHandler(this.mainRouter).listen(this.uzukiConfig.port);
        return this;
    }

    public void scheduleTasks() {
        if (this.uzukiConfig.checkUpdateInterval == 0) return;
        this.singleThreadScheduler.scheduleAtFixedRate(this::executeTasks, this.uzukiConfig.checkUpdateInterval, this.uzukiConfig.checkUpdateInterval, TimeUnit.HOURS);
    }

    private void executeTasks() {
        try {
            if (!this.uzukiStore.needsUpdate()) return;
            this.uzukiStore.updateLocalData();
            this.uzukiCache.updateShipCache(this.uzukiStore.getLocalShipsData());
        } catch (Throwable throwable) {
            this.logger.error(throwable.toString(), throwable);
        }
    }
}
