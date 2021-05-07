package uzuki.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import uzuki.UzukiServer;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class UzukiStore {
    private final UzukiServer uzuki;
    private final String[] files;
    private final String dataDirectory;

    public UzukiStore(UzukiServer uzuki) {
        this.uzuki = uzuki;
        this.files = new String[]{"version.json", "ships.json", "misc.json"};
        this.dataDirectory = this.uzuki.uzukiConfig.directory + "data/";
        if (!this.getFileSystem().existsBlocking(this.dataDirectory)) {
            this.getFileSystem().mkdirBlocking(this.dataDirectory);
        }
        for (String fileName : this.files) {
            if (!this.getFileSystem().existsBlocking(this.dataDirectory + fileName)) {
                this.getFileSystem().createFileBlocking(this.dataDirectory + fileName);
            }
        }
    }

    public FileSystem getFileSystem() { return this.uzuki.vertx.fileSystem(); }

    public String getVersionFileName() { return this.files[0]; }

    public String getShipDataFileName() { return this.files[1]; }

    public String getMiscDataFileName() { return this.files[2]; }

    public JsonObject getLocalShipsData() {
        Buffer buffer = this.getFileSystem().readFileBlocking(this.dataDirectory + this.getShipDataFileName());
        return new Gson().fromJson(buffer.toString(StandardCharsets.UTF_8.name()), JsonObject.class);
    }

    public JsonObject getLocalMiscData() {
        Buffer buffer = this.getFileSystem().readFileBlocking(this.dataDirectory + this.getMiscDataFileName());
        return new Gson().fromJson(buffer.toString(StandardCharsets.UTF_8.name()), JsonObject.class);
    }

    public String getLocalVersionInfo() {
        Buffer buffer = this.getFileSystem().readFileBlocking(this.dataDirectory + this.getVersionFileName());
        JsonObject element = new Gson().fromJson(buffer.toString(StandardCharsets.UTF_8.name()), JsonObject.class);
        if (element == null) return "0";
        return element.get("version") != null ? element.get("version").getAsString() : "0";
    }

    public boolean needsUpdate() throws ExecutionException, InterruptedException {
        String remoteVersion = this.uzuki.uzukiRest.getRemoteVersion().get();
        String localVersion = this.getLocalVersionInfo();
        return !remoteVersion.equals(localVersion);
    }

    public void updateLocalData() throws ExecutionException, InterruptedException {
        JsonObject remoteShips = this.uzuki.uzukiRest.getRemoteShips().get();
        JsonObject remoteMisc = this.uzuki.uzukiRest.getRemoteMisc().get();
        JsonObject remoteVersion = new JsonObject();
        remoteVersion.addProperty("version", this.uzuki.uzukiRest.getRemoteVersion().get());
        this.getFileSystem()
                .writeFileBlocking(this.dataDirectory + this.getShipDataFileName(), Buffer.buffer(remoteShips.toString()));
        this.getFileSystem()
                .writeFileBlocking(this.dataDirectory + this.getMiscDataFileName(), Buffer.buffer(remoteMisc.toString()));
        this.getFileSystem()
                .writeFileBlocking(this.dataDirectory + this.getVersionFileName(), Buffer.buffer(remoteVersion.toString()));
    }
}
