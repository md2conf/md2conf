package io.github.md2conf.converter.view2md;

import org.apache.commons.io.FilenameUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class FileNameUtil {
    /*
     * list of characters not allowed in filenames
     */
    public static final char[] INVALID_CHARS = {'\\', '/', ':', '*', '"', '<', '>', '|', '\'', ';', '=', ','};
    private static final char SANITIZED_CHAR = '_';
    private static final int MAX_FILE_NAME_LENGHT_IN_BYTES = 255;

    /**
     * Given an input, return a sanitized form of the input suitable for use as
     * a file/directory name
     *
     * @param filename the filename to sanitize.
     * @return a sanitized version of the input
     */

    public static String sanitizeFileName(String filename) {
        for (char invalidChar : INVALID_CHARS) {
            if (-1 != filename.indexOf(invalidChar)) {
                filename = filename.replace(invalidChar, FileNameUtil.SANITIZED_CHAR);
            }
        }
        return shortenIfNeed(filename);
    }

    private static String shortenIfNeed(String filename) {
        String ext = FilenameUtils.getExtension(filename);
        String name = FilenameUtils.getName(filename);
        if (filename.getBytes(StandardCharsets.UTF_8).length < MAX_FILE_NAME_LENGHT_IN_BYTES) {
            return filename;
        } else {
            int maxBytes = MAX_FILE_NAME_LENGHT_IN_BYTES - 1 - ext.length();
            if (maxBytes % 2 == 1) {
                maxBytes = maxBytes - 1;
            }
            byte[] bytes = Arrays.copyOfRange(name.getBytes(), 0, maxBytes);
            return new String(bytes, StandardCharsets.UTF_8) + "." + ext;
        }
    }
}
