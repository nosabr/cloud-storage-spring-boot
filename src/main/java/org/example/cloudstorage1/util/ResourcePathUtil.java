package org.example.cloudstorage1.util;

public abstract class ResourcePathUtil {
    public static String getParentPath(String fullPath) {
        if (fullPath == null || fullPath.isEmpty()) {
            return "";
        }

        // Убираем trailing slash для папок
        String normalized = fullPath.endsWith("/")
                ? fullPath.substring(0, fullPath.length() - 1)
                : fullPath;

        int lastSlashIndex = normalized.lastIndexOf('/');

        if (lastSlashIndex == -1) {
            // Нет слэша - корневой элемент
            return "";
        }

        // Возвращаем всё до последнего слэша включительно
        return normalized.substring(0, lastSlashIndex + 1);
    }

    public static String getName(String fullPath) {
        if (fullPath == null || fullPath.isEmpty()) {
            return "";
        }

        // Убираем trailing slash для папок
        String normalized = fullPath.endsWith("/")
                ? fullPath.substring(0, fullPath.length() - 1)
                : fullPath;

        int lastSlashIndex = normalized.lastIndexOf('/');

        if (lastSlashIndex == -1) {
            // Нет слэша - это и есть имя
            return normalized;
        }

        // Возвращаем всё после последнего слэша
        return normalized.substring(lastSlashIndex + 1);
    }
}
