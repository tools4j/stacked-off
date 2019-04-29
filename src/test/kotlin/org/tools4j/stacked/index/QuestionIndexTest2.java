package org.tools4j.stacked.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.join.ParentChildrenBlockJoinQuery;
import org.apache.lucene.search.join.QueryBitSetProducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionIndexTest2 {
    public void test(){

    }

    private List<Document> getChildrenForParentDoc(IndexSearcher indexSearcher, QueryBitSetProducer parentsFilter, int parentDocId) throws IOException {
        Query childrenQuery = new ParentChildrenBlockJoinQuery(parentsFilter, new MatchAllDocsQuery(), parentDocId);
        TopDocs matchingChildren = indexSearcher.search(childrenQuery, 1000);
        List<Document> childrenDocs = new ArrayList<>();
        for (ScoreDoc scoreDoc : matchingChildren.scoreDocs) {
            childrenDocs.add(indexSearcher.doc(scoreDoc.doc));
        }
        return childrenDocs;
    }
}
