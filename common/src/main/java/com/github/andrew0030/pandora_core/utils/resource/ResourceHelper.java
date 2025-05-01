package com.github.andrew0030.pandora_core.utils.resource;

import net.minecraft.server.packs.resources.Resource;

import java.io.BufferedReader;
import java.io.IOException;

public class ResourceHelper {
    public static String readWholeResource(Resource resource) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = resource.openAsReader()) {
            reader.lines().forEach(line -> builder.append(line).append("\n"));
        }
        return builder.toString();
    }
}
