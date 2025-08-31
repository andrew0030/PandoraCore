package com.github.andrew0030.pandora_core.utils.update_checker.strategies;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.github.andrew0030.pandora_core.utils.update_checker.ComparableVersion;
import com.github.andrew0030.pandora_core.utils.update_checker.UpdateInfo;
import com.google.gson.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.zip.GZIPInputStream;

// TODO filter out alpha versions and add config option to toggle the filtering
public class ModrinthUpdateStrategy extends UpdateCheckStrategy {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "UpdateChecker", "Modrinth");
    public static final UpdateInfo FAILED = new UpdateInfo(UpdateInfo.Status.FAILED, UpdateInfo.Source.MODRINTH, null, null, null);
    public static final UpdateInfo PENDING = new UpdateInfo(UpdateInfo.Status.PENDING, UpdateInfo.Source.MODRINTH, null, null, null);
    private static boolean isDeprecated;
    private static final int INVALID_STATUS_CODE = 400;
    private static final int DEPRECATED_STATUS_CODE = 410;
    private static final String MODRINTH_API_VERSION = "v2";
    private final Set<ModDataHolder> holders;
    private final Map<String, ModDataHolder> modHashes = new HashMap<>();

    public ModrinthUpdateStrategy(Set<ModDataHolder> holders) {
        this.holders = holders;
    }

    @Override
    public void performUpdateCheck() {
        // If the modrinth API version is deprecated we skip all checks as they wouldn't work
        if (ModrinthUpdateStrategy.isDeprecated) {
            this.setHolderUpdateInfos(this.holders, FAILED);
            return;
        }

        // If Modrinth API isn't deprecated we start the update check logic and set the holders to PENDING
        this.setHolderUpdateInfos(this.holders, PENDING);

        // We clear and repopulate the hashes cache. If a mod doesn't have a
        // valid Sha512 hash we instantly set that mods update status to FAILED
        this.modHashes.clear();
        for (ModDataHolder holder : this.holders) {
            holder.getSha512Hash().ifPresentOrElse(
                    hash -> this.modHashes.put(hash, holder),
                    () -> holder.setUpdateInfo(FAILED)
            );
        }

        // If there is no hashes that need checking we exit early
        // Since this should only be the case if there are no mods, or if
        // they all failed, we don't need to set the update status here
        if (this.modHashes.isEmpty()) return;

        String userAgent = this.buildUserAgent();
        String mcVersion = Services.PLATFORM.getMinecraftVersion();
        String loader = Services.PLATFORM.getPlatformName().replaceAll("[\\s_-]", "").toLowerCase(Locale.ROOT);

        JsonObject requestBody = new JsonObject();
        requestBody.add("hashes", new Gson().toJsonTree(this.modHashes.keySet()));
        requestBody.addProperty("algorithm", "sha512");
        requestBody.add("loaders", new Gson().toJsonTree(List.of(loader)));
        requestBody.add("game_versions", new Gson().toJsonTree(List.of(mcVersion)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://api.modrinth.com/%s/version_files/update", MODRINTH_API_VERSION)))
                .header("Content-Type", "application/json")
                .header("User-Agent", userAgent)
                .header("Accept-Encoding", "gzip")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        try {
            HttpResponse<InputStream> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

            // If the returned status code indicates that the API is deprecated we skip all further logic
            if (response.statusCode() == DEPRECATED_STATUS_CODE) {
                LOGGER.warn("Modrinth API {} is deprecated, skipping update checks!", MODRINTH_API_VERSION);
                this.setHolderUpdateInfos(this.holders, FAILED);
                ModrinthUpdateStrategy.isDeprecated = true;
                return;
            }

            // If the returned status code indicates something went wrong we set the holders to FAILED and return
            if (response.statusCode() == INVALID_STATUS_CODE) {
                LOGGER.warn("Modrinth update check failed! Status code: 400");
                this.setHolderUpdateInfos(this.holders, FAILED);
                return;
            }

            // If the status code didn't present any issues we parse the response stream
            JsonObject root;
            Optional<String> encodingOpt = response.headers().firstValue("Content-Encoding");
            try (InputStream rawStream = response.body();
                 InputStream decodedStream = switch (encodingOpt.map(s -> s.toLowerCase(Locale.ROOT)).orElse("")) {
                     case "" -> rawStream;
                     case "gzip" -> new GZIPInputStream(rawStream);
                     default -> throw new IOException("Modrinth update response contains unsupported Content-Encoding: " + encodingOpt.get());
                 }) {
                root = JsonParser.parseReader(new InputStreamReader(decodedStream)).getAsJsonObject();
            } catch (IOException e) {
                LOGGER.error("Failed to read Modrinth mod updates response stream. Reason: {}", e.getMessage());
                this.setHolderUpdateInfos(this.holders, FAILED);
                return;
            }

            // Goes over all returned entries and checks if their versions are ahead
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                String hash = entry.getKey();
                JsonObject versionData = entry.getValue().getAsJsonObject();
                ModDataHolder holder = this.modHashes.get(hash);
                // safety check
                if (holder == null) continue;

                // Finds the primary file and checks if the hash matches the
                // current file's hash, in which case it means its up-to-date
                JsonArray files = versionData.getAsJsonArray("files");
                String primaryHash = null;
                URL downloadURL = null;
                // Locates the primary file
                for (JsonElement fileElement : files) {
                    JsonObject file = fileElement.getAsJsonObject();
                    if (file.has("primary") && file.get("primary").getAsBoolean()) {
                        // Gets the sha 512 hash of the primary file
                        JsonObject hashes = file.getAsJsonObject("hashes");
                        if (hashes.has("sha512"))
                            primaryHash = hashes.get("sha512").getAsString();
                        // Extracts the download URL, and constructs a Modrinth page URL (if possible)
                        JsonElement urlElement = file.get("url");
                        if (urlElement != null && !urlElement.isJsonNull()) {
                            String rawUrl = urlElement.getAsString();
                            try {
                                URI uri = new URI(rawUrl);
                                String[] parts = uri.getPath().split("/"); // path: /data/{project_id}/versions/{version_id}/{file}
                                if (parts.length >= 6) {
                                    String projectId = parts[2];
                                    String versionId = parts[4];
                                    downloadURL = new URL(String.format("https://modrinth.com/project/%s/version/%s", projectId, versionId));
                                }
                            } catch (Exception ignored) {}
                        }
                        break; // If the primary file was found and data extracted we exit the loop
                    }
                }

                // If the hash of what we sent matches the primary file, we're up-to-date
                if (hash.equals(primaryHash)) {
                    holder.setUpdateInfo(new UpdateInfo(UpdateInfo.Status.UP_TO_DATE, UpdateInfo.Source.MODRINTH, null, null, downloadURL));
                    continue;
                }

                //TODO: probably filter out alpha versions right here if the config option is enabled
                // ##################################################################################

                UpdateInfo.Type type = UpdateInfo.Type.valueOf(versionData.get("version_type").getAsString().toUpperCase(Locale.ROOT));
                String remoteVersionStr = versionData.get("version_number").getAsString();
                ComparableVersion remoteVersion = new ComparableVersion(remoteVersionStr);
                ComparableVersion localVersion = new ComparableVersion(holder.getModVersion());
                UpdateInfo info = new UpdateInfo(this.getUpdateStatus(remoteVersion, localVersion), UpdateInfo.Source.MODRINTH, type, remoteVersionStr, downloadURL);
                holder.setUpdateInfo(info);
            }

            // Since the response may not contain all hashes, we need to
            // set the holders that are still PENDING to FAILED
            this.modHashes.entrySet().stream()
                    .filter(entry -> !root.has(entry.getKey()))
                    .forEach(entry -> entry.getValue().setUpdateInfo(FAILED));
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error processing Modrinth mod updates. Reason: {}", e.getMessage());
            this.setHolderUpdateInfos(this.holders, FAILED);
        }
    }

    private UpdateInfo.Status getUpdateStatus(ComparableVersion remoteVersion, ComparableVersion localVersion) {
        int result = remoteVersion.compareTo(localVersion);
        UpdateInfo.Status status;
        if (result > 0) {
            status = UpdateInfo.Status.OUTDATED;
        } else if (result < 0) {
            status = UpdateInfo.Status.AHEAD;
        } else {
            status = UpdateInfo.Status.UP_TO_DATE;
        }
        return status;
    }

    /**
     * Sets the update info of all given {@link ModDataHolder} instances.
     *
     * @param holders the {@link ModDataHolder} instances that should be set
     * @param info    the {@link UpdateInfo} the holders should be set to
     */
    private void setHolderUpdateInfos(Set<ModDataHolder> holders, UpdateInfo info) {
        holders.forEach(holder -> holder.setUpdateInfo(info));
    }
}