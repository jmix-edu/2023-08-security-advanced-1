package com.company.jmixpm.screen.document;

import com.company.jmixpm.entity.Document;
import io.jmix.core.LoadContext;
import io.jmix.core.UnconstrainedDataManager;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@UiController("Document.browse")
@UiDescriptor("document-browse.xml")
@LookupComponent("documentsTable")
public class DocumentBrowse extends StandardLookup<Document> {

    @Autowired
    private UnconstrainedDataManager unconstrainedDataManager;

    @Install(to = "documentsDl", target = Target.DATA_LOADER)
    private List<Document> documentsDlLoadDelegate(final LoadContext<Document> loadContext) {
        return unconstrainedDataManager.loadList(loadContext);
    }
}