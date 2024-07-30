package io.github.md2conf.indexer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileIndexerConfigurationProperties {

    private String fileExtension = "wiki";
    //maybe need to support several extensions in the same directory?
    private String excludePattern = "glob:**/.*";
//    private String attachmentDirectorySuffix; //todo
    private String rootPage = null;
    private ChildLayout childLayout = ChildLayout.SUB_DIRECTORY;
    private OrphanFileAction orphanFileAction = OrphanFileAction.IGNORE;

}
