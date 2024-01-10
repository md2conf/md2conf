package io.github.md2conf.converter.view2md;

public class FileNameUtil {
    /*
     * list of characters not allowed in filenames
     */
    public static final char[] INVALID_CHARS = {'\\', '/', ':', '*', '"', '<', '>', '|', '[', ']', '\'', ';', '=', ','};
    private static final char SANITIZED_CHAR = '_';

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
        return filename;
    }
}
