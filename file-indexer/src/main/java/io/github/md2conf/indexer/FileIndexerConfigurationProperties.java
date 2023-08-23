package io.github.md2conf.indexer;

public class FileIndexerConfigurationProperties {


    private String fileExtension = "wiki"; //todo cwiki
    //maybe need to support several extensions in the same directory?
    private String excludePattern = "glob:**/.*";
//    private String attachmentDirectorySuffix; //todo
//    private String attachmentDirectoryBaseName;
//    private String childrenDirectorySuffix;
//    private String childrenDirectoryBaseName;
    private String rootPage = null;
    private ChildLayout childLayout = ChildLayout.NO;

    public String getExcludePattern() {
        return excludePattern;
    }

    public void setExcludePattern(String excludePattern) {
        this.excludePattern = excludePattern;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getRootPage() {
        return rootPage;
    }

    public void setRootPage(String rootPage) {
        this.rootPage = rootPage;
    }

    public ChildLayout getChildLayout() {
        return childLayout;
    }

    public void setChildLayout(ChildLayout childLayout) {
        this.childLayout = childLayout;
    }

    @Override
    public String toString() {
        return "FileIndexerConfigurationProperties{" +
                "fileExtension='" + fileExtension + '\'' +
                ", excludePattern='" + excludePattern + '\'' +
                ", rootPage='" + rootPage + '\'' +
                ", childLayout=" + childLayout +
                '}';
    }
}
