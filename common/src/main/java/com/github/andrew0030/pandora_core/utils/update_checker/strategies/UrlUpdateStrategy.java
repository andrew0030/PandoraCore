package com.github.andrew0030.pandora_core.utils.update_checker.strategies;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.github.andrew0030.pandora_core.utils.update_checker.ComparableVersion;
import com.github.andrew0030.pandora_core.utils.update_checker.UpdateInfo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

public class UrlUpdateStrategy extends UpdateCheckStrategy {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "UpdateChecker", "URL");
    public static final UpdateInfo FAILED = new UpdateInfo(UpdateInfo.Status.FAILED, UpdateInfo.Source.URL, null);
    public static final UpdateInfo PENDING = new UpdateInfo(UpdateInfo.Status.PENDING, UpdateInfo.Source.URL, null);
    private final ModDataHolder holder;

    public UrlUpdateStrategy(ModDataHolder holder) {
        this.holder = holder;
    }

    @Override
    public void performUpdateCheck() {
        // Initially sets the holder to PENDING
        holder.setUpdateInfo(PENDING);

        // Attempts to create a http request from the given URL
        String userAgent = this.buildUserAgent();
        HttpRequest request;
        try {
             request = HttpRequest.newBuilder()
                    .uri(this.holder.getUpdateURL().get().toURI())
                    .header("Content-Type", "application/json")
                    .header("User-Agent", userAgent)
                    .header("Accept-Encoding", "gzip")
                    .GET()
                    .build();
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to create URI from URL for '{}'", holder.getModId());
            this.holder.setUpdateInfo(FAILED);
            return;
        }

        try {
            HttpResponse<InputStream> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

            // If the returned status code indicates something went wrong we set the holder to FAILED and return
            if (response.statusCode() >= 400) {
                LOGGER.warn("URL-based update check failed for '{}'. Status code: {}", this.holder.getModId(), response.statusCode());
                this.holder.setUpdateInfo(FAILED);
                return;
            }

            // If the status code didn't present any issues we parse the response stream
            JsonObject root;
            Optional<String> encodingOpt = response.headers().firstValue("Content-Encoding");
            try (InputStream rawStream = response.body();
                 InputStream decodedStream = switch (encodingOpt.map(s -> s.toLowerCase(Locale.ROOT)).orElse("")) {
                     case "" -> rawStream;
                     case "gzip" -> new GZIPInputStream(rawStream);
                     default -> throw new IOException("URL-based update response for '" + this.holder.getModId() + "' contains unsupported Content-Encoding: " + encodingOpt.get());
                 }) {
                root = JsonParser.parseReader(new InputStreamReader(decodedStream)).getAsJsonObject();
            } catch (IOException e) {
                LOGGER.error("Failed to read URL-based mod update response stream for '{}'. Reason: {}", this.holder.getModId(), e.getMessage());
                this.holder.setUpdateInfo(FAILED);
                return;
            }

            // Gets the homepage (optional) and promos (required)
            URL downloadURL = null;
            try {
                String downloadStr = root.has("homepage") && root.get("homepage").isJsonPrimitive() ? root.get("homepage").getAsString() : null;
                if (downloadStr != null) downloadURL = new URL(downloadStr);
            } catch (Exception ignored) {}
            JsonObject promos = root.has("promos") && root.get("promos").isJsonObject() ? root.getAsJsonObject("promos") : null;
            if (promos == null) {
                LOGGER.error("No 'promos' element found for '{}'.", this.holder.getModId());
                this.holder.setUpdateInfo(FAILED);
                return;
            }

            // Gets all the relevant versions (if available) and compares them to the current version
            String mcVersion = Services.PLATFORM.getMinecraftVersion();
            String recommendedStr = promos.has(mcVersion + "-recommended") && promos.get(mcVersion + "-recommended").isJsonPrimitive() ? promos.get(mcVersion + "-recommended").getAsString() : null;
            String latestStr = promos.has(mcVersion + "-latest") && promos.get(mcVersion + "-latest").isJsonPrimitive() ? promos.get(mcVersion + "-latest").getAsString() : null;
            UpdateInfo.Status status = this.determineStatus(recommendedStr, latestStr);

            // Updates the holder with the found update status and other information
            this.holder.setUpdateInfo(new UpdateInfo(status, UpdateInfo.Source.URL, downloadURL));
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error processing URL-based mod update for '{}'. Reason: {}", this.holder.getModId(), e.getMessage());
            this.holder.setUpdateInfo(FAILED);
        }
    }

    private UpdateInfo.Status determineStatus(String recommendedStr, String latestStr) {
        ComparableVersion current = new ComparableVersion(this.holder.getModVersion());
        ComparableVersion recommended = recommendedStr != null ? new ComparableVersion(recommendedStr) : null;
        ComparableVersion latest = latestStr != null ? new ComparableVersion(latestStr) : null;

        // Checks if there is a recommended version
        if (recommended != null) {
            int diff = recommended.compareTo(current);
            // If the current and recommended version are the same, it is up-to-date
            if (diff == 0) return UpdateInfo.Status.UP_TO_DATE;
            // If the current version is greater than recommended
            if (diff < 0) {
                if (latest != null && current.compareTo(latest) < 0) {
                    return UpdateInfo.Status.OUTDATED; // If latest exists and is greater than current, it is outdated
                } else {
                    return UpdateInfo.Status.AHEAD; // If latest doesn't exist current is ahead
                }
            }
            // If current is older than recommended, it is outdated
            return UpdateInfo.Status.OUTDATED;
        }

        // Checks if there is a latest version
        if (latest != null) {
            return current.compareTo(latest) < 0
                    ? UpdateInfo.Status.BETA_OUTDATED // If latest is greater than current, it is beta-outdated
                    : UpdateInfo.Status.BETA; // If current is greater or equal to latest its beta
        }

        return UpdateInfo.Status.NO_PROMOS; // The default status if no promos were found
    }
}