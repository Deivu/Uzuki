package uzuki.util;

import io.vertx.core.json.JsonObject;
import org.apache.commons.io.IOUtils;
import uzuki.UzukiServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class UzukiConfig {
    public final String pass;
    public final int port;
    public final int threads;
    public final String routePrefix;
    public final String directory;
    public final int checkUpdateInterval;
    public final int maxResults;
    public final int searchWeight;
    public final boolean privateRest;

    public UzukiConfig() throws IOException, URISyntaxException {
        File file = new File(UzukiServer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        this.directory = file.getPath().replace(file.getName(), "");
        try (InputStream is = new FileInputStream(this.directory + "config.json")) {
            JsonObject config = new JsonObject(IOUtils.toString(is));
            this.pass = config.getString("pass");
            this.port = config.containsKey("port") ? config.getInteger("port") : 1024;
            this.threads = config.containsKey("threads") ? config.getInteger("threads") : Runtime.getRuntime().availableProcessors();
            this.routePrefix = config.containsKey("routePrefix") ? "/" + config.getString("routePrefix") : "/";
            this.maxResults = config.containsKey("maxResults") ? config.getInteger("maxResults") : 5;
            this.searchWeight = config.containsKey("searchWeight") ? config.getInteger("searchWeight") : 75;
            this.privateRest = !config.containsKey("privateRest") || config.getBoolean("privateRest");
            this.checkUpdateInterval = config.containsKey("checkUpdateInterval") ? config.getInteger("checkUpdateInterval") : 0;
        }
    }
}
