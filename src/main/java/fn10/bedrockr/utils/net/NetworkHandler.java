package fn10.bedrockr.utils.net;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fn10.bedrockr.addons.element.supporting.item.ItemInfo;
import fn10.bedrockr.utils.ImageHandler;
import fn10.bedrockr.utils.RFileOperations;
import fn10.bedrockr.utils.web.Format1Latest;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static fn10.bedrockr.utils.RFileOperations.gson;

//Wrapper used to wrap around different 
public abstract class NetworkHandler {
    public ArrayList<PrismarineBlockInfo> getVanillaBlocks() {
        return prisBlocks;
    }

    public ArrayList<PrismarineBiomeInfo> getVanillaBiomes() {
        return prisBiomes;
    }

    public ArrayList<PrismarineItemInfo> getVanillaItems() {
        return prisItems;
    }

    protected ArrayList<PrismarineBlockInfo> prisBlocks;
    protected ArrayList<PrismarineBiomeInfo> prisBiomes;
    protected ArrayList<PrismarineItemInfo> prisItems;

    public void setup() {
    }

    /// @return a bool declaring if the device this is running on is connected to the internet.
    public abstract boolean isOnline();

    public abstract Pair<Integer, String> postJSON(URI uri, Object obj) throws Exception;


    /// A helper function that wraps over {@code getJson()}
    /// @return The JSON serialized as the class given, or {@code null} if the request fails.
    public <T> T getJSON(String uri, Class<T> cls) throws Exception {
        Pair<Integer, String> res = getJSON(URI.create(uri));
        if (res.getLeft() != 200 || res.getRight() == null) return null;
        return gson.fromJson(res.getRight(), cls);
    }

    /// A helper function that wraps over {@code getJson()}
    /// @return The JSON as a JsonObject, or {@code null} if the request fails.
    public JsonObject getJSON(String uri) throws Exception {
        Pair<Integer, String> res = getJSON(URI.create(uri));
        if (res.getLeft() != 200 || res.getRight() == null) return null;
        return JsonParser.parseString(res.getRight()).getAsJsonObject();
    }
    
    /// Perform an HTTP GET request and return the body as JSON.
    /// @return The JSON as a String, or {@code null} if the request fails.
    public abstract Pair<Integer, String> getJSON(URI url) throws Exception;

    /// Perform an HTTP GET request and return the body as JSON.
    public abstract CompletableFuture<Pair<Integer, String>> getJSONAsync(URI url) throws Exception;

    /// Perform an HTTP GET request and return the body as raw data.
    /// @return The response as bytes, or {@code null} if the request fails.
    public abstract Pair<Integer, byte[]> getBytes(URI url) throws Exception;

    /// Perform an HTTP GET request and return the body as raw data.
    public abstract CompletableFuture<Pair<Integer, byte[]>> getBytesAsync(URI url) throws Exception;

    /// (Executed in {@link RFileOperations#init(ImageHandler, NetworkHandler)})
    /// <br/>
    ///Downloads the most recent vanilla in-game info of certain things, like biomes, items, and blocks.
    ///
    /// @see getVanillaBlocks()
    /// @see getVanillaBiomes()
    /// @see getVanillaItems()
    public void downloadVanillaInfo(String ver) {
       try {
           JsonObject dataPaths = getJSON("https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/dataPaths.json");
           JsonObject versionPaths = dataPaths.getAsJsonObject("bedrock").getAsJsonObject(ver);

           String itemPath = versionPaths.getAsJsonPrimitive("items").getAsString();
           String blockPath = versionPaths.getAsJsonPrimitive("blocks").getAsString();
           String biomePath = versionPaths.getAsJsonPrimitive("biomes").getAsString();

           PrismarineItemInfo[] itemEntrys = getJSON(
                   "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/"
                           + itemPath
                           + "/items.json", PrismarineItemInfo[].class);
           prisItems.addAll(List.of(itemEntrys));

           PrismarineBlockInfo[] blockEntrys = getJSON(
                   "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/"
                           + blockPath
                           + "/blocks.json", PrismarineBlockInfo[].class);
           prisBlocks.addAll(List.of(blockEntrys));

           PrismarineBiomeInfo[] biomeEntrys = getJSON(
                   "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/refs/heads/master/data/"
                           + biomePath
                           + "/biomes.json", PrismarineBiomeInfo[].class);
           prisBiomes.addAll(List.of(biomeEntrys));
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }

    /// Returns whether this version of bedrockR is the latest (stable) release.
    public Triple<Boolean, String, String> upToDate() throws Exception {
        JsonObject json = getJSON("https://raw.githubusercontent.com/xFN10x/bedrockR/refs/heads/master/latest.json");
        long format = json.get("format").getAsLong();
        //NOTE: format for latest.json here
        if (format != 1)
            return Triple.of(false, "This version is TOO out of date. Please update bedrockR!", json.get("currentStringVersion").getAsString());
        else {
            Format1Latest serialized = gson.fromJson(json, Format1Latest.class);
            return Triple.of((serialized.LatestVersion > RFileOperations.NUM_VERSION && serialized.ShouldUse), serialized.Message, serialized.CurrentStringVersion);
        }
    }
}
