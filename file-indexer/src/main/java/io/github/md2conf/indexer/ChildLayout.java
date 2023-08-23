package io.github.md2conf.indexer;

public enum ChildLayout {
//    NO, // All found pages are
    SUB_DIRECTORY, // Source files for children pages resides in directory with the name equals to basename of parent file
    SAME_DIRECTORY // File with name 'index.md' or 'README.md' is the source file of parent page. Other files in the directory are source files for children pages.

}
