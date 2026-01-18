package org.example.cloudstorage1.util;

import java.util.ArrayList;
import java.util.List;

public abstract class ResourceUtil {

    public static List<String> parsePathIntoSegments(String path){
        if (path == null || path.isEmpty()) {
            return List.of();
        }

        // Убираем trailing slash перед split
        String pathWithoutTrailing = path.endsWith("/")
                ? path.substring(0, path.length() - 1)
                : path;

        if (pathWithoutTrailing.isEmpty()) {
            return List.of();
        }

        String[] parts = pathWithoutTrailing.split("/");
        List<String> segments = new ArrayList<>();

        for (String part : parts) {
            if (!part.isEmpty()) {
                segments.add(part);
            }
        }

        return segments;
    }
}
