package org.example.cloudstorage1.util;

import org.example.cloudstorage1.exception.InvalidResourceNameException;

public abstract class ResourceValidationUtil {
    public static int MAX_FILENAME_LENGTH = 50;

    public static void validateFileName(String fileName) {

        if (fileName == null || fileName.isEmpty()) {
            throw new InvalidResourceNameException("File name cannot be empty");
        }

        // 2. Длина
        if (fileName.length() > MAX_FILENAME_LENGTH) {
            throw new InvalidResourceNameException(
                    "File name too long (max " + MAX_FILENAME_LENGTH + " chars): " + fileName
            );
        }

        // 3. Path traversal
        if (fileName.equals(".") || fileName.equals("..")) {
            throw new InvalidResourceNameException("File name cannot be '.' or '..': " + fileName);
        }

        // 4. Запрет слешей (это должно быть имя, не путь)
        if (fileName.contains("/") || fileName.contains("\\")) {
            throw new InvalidResourceNameException("File name cannot contain slashes: " + fileName);
        }

        // 5. Control characters
        if (fileName.matches(".*[\\x00-\\x1F].*")) {
            throw new InvalidResourceNameException("File name contains control characters: " + fileName);
        }
    }
}
