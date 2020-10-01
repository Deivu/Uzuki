package uzuki.endpoints;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uzuki.UzukiServer;
import uzuki.struct.UzukiEndpointContext;

public class UzukiEndpointManager {
    private final UzukiServer uzuki;
    private final UzukiShipEndpoint uzukiShipEndpoint;
    public final Logger logger;

    public UzukiEndpointManager(UzukiServer uzuki) {
        this.uzuki = uzuki;
        this.uzukiShipEndpoint = new UzukiShipEndpoint(this.uzuki);
        this.logger = LoggerFactory.getLogger(UzukiEndpointManager.class);
    }

    public void executeFail(RoutingContext context) {
        Throwable throwable = context.failure();
        HttpServerResponse response = context.response();
        int statusCode = context.statusCode();
        if (throwable != null) {
            this.logger.error(throwable.toString(), throwable);
        } else {
            this.logger.warn("Failed REST Request; Code: " + statusCode + " Reason: " + response.getStatusMessage());
        }
        response.setStatusCode(statusCode).end();
    }

    public void executeUpdate(RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();
        try {
            String auth = request.getHeader("authorization");
            if (!auth.equals(this.uzuki.uzukiConfig.pass)) {
                response.setStatusMessage("Unauthorized");
                context.fail(401);
                return;
            }
            if (this.uzuki.uzukiStore.needsUpdate()) {
                this.uzuki.uzukiStore.updateLocalData();
                this.uzuki.uzukiCache.updateShipCache(this.uzuki.uzukiStore.getLocalShipsData());
            }
            response.end();
        } catch (Throwable throwable) {
            context.fail(throwable);
        }
    }

    public void executeGet(String endpoint, RoutingContext context) {
        HttpServerRequest request = context.request();
        HttpServerResponse response = context.response();
        String auth = request.getHeader("authorization");
        if (this.uzuki.uzukiConfig.privateRest && !auth.equals(this.uzuki.uzukiConfig.pass)) {
            response.setStatusMessage("Unauthorized");
            context.fail(401);
            return;
        }
        String query = request.getParam("q");
        if (query == null) {
            response.setStatusMessage("Bad Request");
            context.fail(400);
            return;
        }
        response.putHeader("content-type", "application/json; charset=utf-8");
        UzukiEndpointContext uzukiContext = new UzukiEndpointContext(context, request, response, query);
        // in dev
        switch(endpoint) {
            case "/ship/search":
                this.uzukiShipEndpoint.search(uzukiContext);
                break;
            default:
                this.logger.warn("Unknown endpoint: " + endpoint);
                response.end();
        }
    }
}
