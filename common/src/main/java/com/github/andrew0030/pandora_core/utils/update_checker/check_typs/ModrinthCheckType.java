package com.github.andrew0030.pandora_core.utils.update_checker.check_typs;

import com.github.andrew0030.pandora_core.platform.Services;
import com.github.andrew0030.pandora_core.utils.data_holders.ModDataHolder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

//TODO stuff that this probably needs:
// - Add deprecation check / handling for modrinth api (status 410)
// - Retrieve only data holders for mods that don't specify an update URL
// - Cache hashes and use them to compare versions based on the response
// - Apply update status based on what the version comparison returns
public class ModrinthCheckType extends BaseCheckType {

    public ModrinthCheckType(ModDataHolder holder) {
        super(holder);
    }

    @Override
    public void performUpdateCheck() {
        String userAgent = this.buildUserAgent(this.holder);
        String hash = this.holder.getSha512Hash();

        if (hash == null || hash.isEmpty()) {
            System.out.println("No SHA-512 hash available for mod: " + this.holder.getModId());
            return;
        }

        String mcVersion = Services.PLATFORM.getMinecraftVersion();
        String loader = "fabric";

        JsonObject requestBody = new JsonObject();
        requestBody.add("hashes", new Gson().toJsonTree(List.of(hash)));
        requestBody.addProperty("algorithm", "sha512");
        requestBody.add("loaders", new Gson().toJsonTree(List.of(loader)));
        requestBody.add("game_versions", new Gson().toJsonTree(List.of(mcVersion)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.modrinth.com/v2/version_files/update"))
                .header("Content-Type", "application/json")
                .header("User-Agent", userAgent)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status: " + response.statusCode());
            System.out.println("Response Header: " + response.headers());
            System.out.println("Response Body: " + response.body());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}