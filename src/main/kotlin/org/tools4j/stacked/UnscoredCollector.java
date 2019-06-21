package org.tools4j.stacked;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.SimpleCollector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnscoredCollector extends SimpleCollector {
    private final List<Integer> docIds = new ArrayList<>();
    private LeafReaderContext currentLeafReaderContext;

    @Override
    protected void doSetNextReader(LeafReaderContext context) throws IOException {
        this.currentLeafReaderContext = context;
    }

    @Override
    public boolean needsScores(){
        return false;
    }

    @Override
    public void collect(int localDocId) {
        docIds.add(currentLeafReaderContext.docBase + localDocId);
    }
}
