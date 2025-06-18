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
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

//TODO stuff that this probably needs:
// - Add gzip support
public class ModrinthUpdateStrategy extends UpdateCheckStrategy {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "ModrinthUpdateStrategy");
    private static final int INVALID_STATUS_CODE = 400;
    private static final int DEPRECATED_STATUS_CODE = 410;
    private static final String MODRINTH_API_VERSION = "v2";
    public static final UpdateInfo FAILED = new UpdateInfo(UpdateInfo.Status.FAILED, UpdateInfo.Source.MODRINTH, null, null);
    public static final UpdateInfo PENDING = new UpdateInfo(UpdateInfo.Status.PENDING, UpdateInfo.Source.MODRINTH, null, null);
    private static boolean isDeprecated;
    private final Map<String, ModDataHolder> modHashes = new HashMap<>();

    public ModrinthUpdateStrategy(Set<ModDataHolder> holders) {
        super(holders);
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

        // TODO: modify the user agent to not include info about the mod, as there is no need in a bulk request
        String userAgent = this.buildUserAgent(PandoraCore.getModHolder("pandora_core"));
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
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

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

            // If the status code didn't present any issues we parse the response
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                String hash = entry.getKey();
                JsonObject versionData = entry.getValue().getAsJsonObject();
                ModDataHolder holder = this.modHashes.get(hash);
                // safety check
                if (holder == null) continue;

                ComparableVersion remoteVersion = new ComparableVersion(versionData.get("version_number").getAsString());
                ComparableVersion localVersion = new ComparableVersion(holder.getModVersion());
                // TODO: probably clean the changelog up as it may have markdown stuff in it that we don't want.
                JsonElement changelogElement = versionData.get("changelog");
                String changelog = (changelogElement != null && !changelogElement.isJsonNull()) ? changelogElement.getAsString() : null;

                // Extracts download URL and constructs Modrinth page URL (if possible)
                URL downloadURL = null;
                JsonArray filesArray = versionData.getAsJsonArray("files");
                if (filesArray != null && !filesArray.isEmpty()) {
                    JsonObject fileObj = filesArray.get(0).getAsJsonObject();
                    JsonElement urlElement = fileObj.get("url");
                    if (urlElement != null && !urlElement.isJsonNull()) {
                        String rawUrl = urlElement.getAsString();
                        try {
                            URI uri = new URI(rawUrl);
                            String[] parts = uri.getPath().split("/"); // path: /data/{project_id}/versions/{version_id}/{file}
                            if (parts.length >= 6) {
                                String projectId = parts[3];
                                String versionId = parts[5];
                                downloadURL = new URL("https://modrinth.com/mod/" + projectId + "/version/" + versionId);
                            }
                        } catch (Exception ignored) {}
                    }
                }

                UpdateInfo info = new UpdateInfo(this.getUpdateStatus(remoteVersion, localVersion), UpdateInfo.Source.MODRINTH, changelog, downloadURL);
                holder.setUpdateInfo(info);
            }

            // Since the response may not contain all hashes, we need to
            // set the holders that are still PENDING to FAILED
            this.modHashes.entrySet().stream()
                    .filter(entry -> !root.has(entry.getKey()))
                    .forEach(entry -> entry.getValue().setUpdateInfo(FAILED));
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error processing modrinth updates response. Reason: {}", e.getMessage());
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