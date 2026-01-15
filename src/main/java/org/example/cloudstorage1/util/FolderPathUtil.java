package org.example.cloudstorage1.util;


public abstract class FolderPathUtil {

    public static String getParentPath(String fullPath){
        // Убираем последний слэш для корректной работы lastIndexOf
        String pathWithoutTrailingSlash = fullPath.substring(0, fullPath.length() - 1);

        // Находим позицию предпоследнего слэша
        int lastSlashIndex = pathWithoutTrailingSlash.lastIndexOf("/");
        return  fullPath.substring(0, lastSlashIndex + 1);
    }

    public static String getFolderName(String fullPath){
        // Убираем последний слэш для корректной работы lastIndexOf
        String pathWithoutTrailingSlash = fullPath.substring(0, fullPath.length() - 1);

        // Находим позицию предпоследнего слэша
        int lastSlashIndex = pathWithoutTrailingSlash.lastIndexOf("/");
        return pathWithoutTrailingSlash.substring(lastSlashIndex + 1);
    }

}
