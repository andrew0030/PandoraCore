package com.github.andrew0030.pandora_core.utils.update_checker;

import com.github.andrew0030.pandora_core.PandoraCore;
import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.github.andrew0030.pandora_core.utils.logger.PaCoLogger;
import com.google.gson.Gson;
import org.slf4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class UpdateChecker {
    private static final Logger LOGGER = PaCoLogger.create(PandoraCore.MOD_NAME, "UpdateChecker");
    private static final int HTTP_TIMEOUT_SECS = 15;

    public static void checkForUpdate(ModDataHolder holder) {
        // If the mod has no valid update URL we stop all logic
        if (holder.getUpdateURL().isEmpty()) return;
        // If a check has already been performed we stop here. (One could set the status to null to perform a second check)
        if (holder.getUpdateStatus().isPresent()) return;
        // We set the status to pending
        holder.setUpdateStatus(Status.PENDING);
        // We create a client and a request
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(HTTP_TIMEOUT_SECS)).followRedirects(HttpClient.Redirect.NORMAL).build();
        Optional<HttpRequest> request = UpdateChecker.createRequest(holder.getUpdateURL().get());
        if (request.isEmpty()) {
            holder.setUpdateStatus(Status.FAILED);
            LOGGER.error("Failed to create URI from URL for [{}]", holder.getModId());
            return;
        }
        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request.get(), HttpResponse.BodyHandlers.ofString());
        // Handles the response
        responseFuture.thenAccept(response -> {
            try {
                // Checks if the response is GZIP encoded
                boolean isGzipEncoded = response.headers().firstValue("Content-Encoding").orElse("").equalsIgnoreCase("gzip");
                // Handles the body stream based on whether it's GZIP-encoded
                try (InputStream inputStream = isGzipEncoded
                        ? new GZIPInputStream(new ByteArrayInputStream(response.body().getBytes()))
                        : new ByteArrayInputStream(response.body().getBytes())) {
                    holder.setUpdateStatus(UpdateChecker.handleUpdateResponse(inputStream, holder));
                }
            } catch (IOException e) {
                holder.setUpdateStatus(Status.FAILED);
                LOGGER.error("Failed to handle GZIP or process update response for [{}]. Reason: {}", holder.getModId(), e.getMessage());
            }
        }).exceptionally(ex -> {
            holder.setUpdateStatus(Status.FAILED);
            LOGGER.error("Failed to check for updates for [{}]. Reason: {}", holder.getModId(), ex.getMessage());
            return null;
        });
    }

    /** @return An {@link Optional} containing the {@link HttpRequest}, if the {@link URL} was invalid or something else fails, an empty {@link Optional} is returned. */
    private static Optional<HttpRequest> createRequest(URL url) {
        try {
            return Optional.of(HttpRequest.newBuilder().uri(url.toURI()).build());
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }

    /** @return The {@link Status} of the update check. */
    @SuppressWarnings("unchecked")
    private static Status handleUpdateResponse(InputStream inputStream, ModDataHolder holder) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String responseBody = reader.lines().collect(Collectors.joining("\n"));
            // We retrieve the information from the response
            Map<String, Object> json = new Gson().fromJson(responseBody, Map.class);
            Map<String, String> promos = (Map<String, String>) json.get("promos");
            String homepage = (String) json.get("homepage"); //TODO somehow store this or show it on demand
            // We prepare all the versions
            String mcVersion = Services.PLATFORM.getMinecraftVersion();
            String recommendedStr = promos.get(mcVersion + "-recommended");
            String latestStr = promos.get(mcVersion + "-latest");
            ComparableVersion currentVersion = new ComparableVersion(holder.getModVersion());
            // And lastly we compare the versions and return the result
            return UpdateChecker.determineStatus(recommendedStr, latestStr, currentVersion, holder.getModId());
        } catch (IOException e) {
            LOGGER.error("Error processing update response for [{}]", holder.getModId(), e);
            return Status.FAILED;
        }
    }

    private static Status determineStatus(String recommendedStr, String latestStr, ComparableVersion currentVersion, String modId) {
        ComparableVersion recommendedVersion = recommendedStr != null ? new ComparableVersion(recommendedStr) : null;
        ComparableVersion latestVersion = latestStr != null ? new ComparableVersion(latestStr) : null;
        // Checks if there is a recommended version
        if (recommendedVersion != null) {
            int diff = recommendedVersion.compareTo(currentVersion);
            if (diff == 0) {
                LOGGER.info("[{}] Status: UP_TO_DATE | Current Version: {}", modId, currentVersion);
                return Status.UP_TO_DATE;
            }
            if (diff < 0) {
                String status = (latestVersion != null && currentVersion.compareTo(latestVersion) < 0) ? "OUTDATED" : "AHEAD";
                LOGGER.info("[{}] Status: {} | Current Version: {} | Recommended Version: {}", modId, status, currentVersion, recommendedVersion);
                return status.equals("OUTDATED") ? Status.OUTDATED : Status.AHEAD;
            }
            LOGGER.info("[{}] Status: OUTDATED | Current Version: {} | Recommended Version: {}", modId, currentVersion, recommendedVersion);
            return Status.OUTDATED;
        }
        // Checks if there is a latest version
        if (latestVersion != null) {
            String status = currentVersion.compareTo(latestVersion) < 0 ? "BETA_OUTDATED" : "BETA";
            LOGGER.info("[{}] Status: {} | Current Version: {} | Latest Version: {}", modId, status, currentVersion, latestVersion);
            return status.equals("BETA_OUTDATED") ? Status.BETA_OUTDATED : Status.BETA;
        }
        // The default status if no promos were found
        LOGGER.warn("[{}] No promos found, defaulting to BETA status.", modId);
        return Status.BETA;
    }

    public enum Status {
        PENDING(false),
        FAILED(false),
        UP_TO_DATE(false),
        OUTDATED(true),
        AHEAD(false),
        BETA(false),
        BETA_OUTDATED(true);

        private final boolean outdated;

        Status(boolean outdated) {
            this.outdated = outdated;
        }

        public boolean isOutdated() {
            return this.outdated;
        }
    }
}