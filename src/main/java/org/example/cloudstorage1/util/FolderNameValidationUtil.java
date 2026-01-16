package org.example.cloudstorage1.util;

import lombok.extern.slf4j.Slf4j;
import org.example.cloudstorage1.exception.InvalidFolderNameException;

import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
public abstract class FolderNameValidationUtil {

    private static final Pattern VALID_NAME = Pattern.compile("^[a-zA-Z0-9._\\-\\s]+$");
    private static final Set<String> RESERVED_NAMES = Set.of(
            "CON", "PRN", "AUX", "NUL",
            "COM1", "COM2", "LPT1", "."
    );

    public static void validateFolderName(String name) {
        if (name == null || name.isBlank()) {
            log.warn("Folder name cannot be empty" + name);
            throw new InvalidFolderNameException("Folder name cannot be empty");
        }

        if (name.length() > 255) {
            log.warn("Folder name too long (max 255 characters)");
            throw new InvalidFolderNameException("Folder name too long (max 255 characters)");
        }

        if (!VALID_NAME.matcher(name).matches()) {
            log.warn("Folder name contains invalid characters");
            throw new InvalidFolderNameException("Folder name contains invalid characters");
        }

        if (RESERVED_NAMES.contains(name.toUpperCase())) {
            log.warn("Folder name is reserved");
            throw new InvalidFolderNameException("Folder name is reserved");
        }

        if (name.startsWith(".") || name.endsWith(".")) {
            log.warn("Folder name cannot start or end with a dot");
            throw new InvalidFolderNameException("Folder name cannot start or end with a dot");
        }

        if (name.trim().length() != name.length()) {
            log.warn("Folder name cannot start or end with spaces");
            throw new InvalidFolderNameException("Folder name cannot start or end with spaces");
        }
    }
}
