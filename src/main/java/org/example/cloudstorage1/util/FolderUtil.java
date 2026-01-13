package org.example.cloudstorage1.util;



import java.util.Set;
import java.util.regex.Pattern;

public abstract class FolderUtil {
    private static final Pattern VALID_NAME = Pattern.compile("^[a-zA-Z0-9._\\-\\s]+$");
    private static final Set<String> RESERVED_NAMES = Set.of(
            "CON", "PRN", "AUX", "NUL",
            "COM1", "COM2", "LPT1", "."
    );

    public static String getParentPath(String fullPath){
        // Убираем последний слэш для корректной работы lastIndexOf
        String pathWithoutTrailingSlash = fullPath.substring(0, fullPath.length() - 1);

        // Находим позицию предпоследнего слэша
        int lastSlashIndex = pathWithoutTrailingSlash.lastIndexOf("/");
        return  fullPath.substring(0, lastSlashIndex + 1);
    }

    public static String getChildPath(String fullPath){
        // Убираем последний слэш для корректной работы lastIndexOf
        String pathWithoutTrailingSlash = fullPath.substring(0, fullPath.length() - 1);

        // Находим позицию предпоследнего слэша
        int lastSlashIndex = pathWithoutTrailingSlash.lastIndexOf("/");
        return pathWithoutTrailingSlash.substring(lastSlashIndex + 1) + "/";
    }

    public static boolean isFolderNameValid(String folderName){
        return true;
    }



//    public static void validateFolderName(String name) {
//        if (name == null || name.isBlank()) {
//            throw new ValidationException("Folder name cannot be empty");
//        }
//
//        if (name.length() > 255) {
//            throw new ValidationException("Folder name too long (max 255 characters)");
//        }
//
//        if (!VALID_NAME.matcher(name).matches()) {
//            throw new ValidationException("Folder name contains invalid characters");
//        }
//
//        if (RESERVED_NAMES.contains(name.toUpperCase())) {
//            throw new ValidationException("Folder name is reserved");
//        }
//
//        if (name.startsWith(".") || name.endsWith(".")) {
//            throw new ValidationException("Folder name cannot start or end with a dot");
//        }
//
//        if (name.trim().length() != name.length()) {
//            throw new ValidationException("Folder name cannot start or end with spaces");
//        }
//    }
}
