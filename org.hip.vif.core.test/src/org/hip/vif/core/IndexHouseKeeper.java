package org.hip.vif.core;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.hip.kernel.sys.VSys;
import org.hip.vif.core.bom.search.VIFContentIndexer;
import org.hip.vif.core.bom.search.VIFMemberIndexer;
import org.hip.vif.core.search.AbstractVIFIndexer;
import org.hip.vif.core.search.NoHitsException;
import org.hip.vif.core.search.VIFIndexing;
import org.hip.vif.core.util.WorkspaceHelper;

/** @author Benno Luthiger Created on 15.10.2005 */
public class IndexHouseKeeper {
    public static final Version LUCENE_VERSION = Version.LUCENE_4_10_1;
    public static final int NUMBER_OF_HITS = 50;

    private final static String KEY_PROPERTY_DOCROOT = "org.hip.vif.docs.root";
    private final static String INDEX_DIR = "vifindex" + File.separator;
    private final static String MEMBERS_INDEX_DB = "members";
    private final static String MEMBERS_INDEX = INDEX_DIR + MEMBERS_INDEX_DB;
    private final static String CONTENT_INDEX_DB = "content";
    private final static String CONTENT_INDEX = INDEX_DIR + CONTENT_INDEX_DB;

    private static File cMembersDir;
    private static File cContentsDir;

    public static Analyzer getAnalyzer() {
        return new StandardAnalyzer();
    }

    /** @return Directory the directory for the members index.
     * @throws IOException */
    public static Directory getMembersIndexDir() throws IOException {
        if (cMembersDir == null) {
            cMembersDir = createDirStructure(getMemberDirName());
        }
        return FSDirectory.open(cMembersDir);
    }

    /** @return Directory the directory for the contentx index.
     * @throws IOException */
    public static Directory getContentsIndexDir() throws IOException {
        if (cContentsDir == null) {
            cContentsDir = createDirStructure(getContentDirName());
        }
        return FSDirectory.open(cContentsDir);
    }

    private static File createDirStructure(final String inDirName) {
        final File outDir = new File(inDirName);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        return outDir;
    }

    /** Delete the lucene index dirs created for testing purpose. Should be called during
     * <code>TestCase.tearDown()</code>.
     *
     * <b>Note:</b> (17.12.2010) we only close and don't delete. Deleting during the test run results in corrupt indexes
     * and stale file locks.
     *
     * @throws IOException */
    @SuppressWarnings("deprecation")
    public static void deleteTestIndexDir() throws IOException {
        VIFIndexing.INSTANCE.close();
        // deleteIndexDir(new File(getMemberDirName()));
        // deleteIndexDir(new File(getContentDirName()));
    }

    // private static void deleteIndexDir(File inIndexDir) {
    // if (inIndexDir.exists()) {
    // File[] lTempFiles = inIndexDir.listFiles();
    // for (int i = 0; i < lTempFiles.length; i++) {
    // if (!lTempFiles[i].delete()) {
    // lTempFiles[i].deleteOnExit();
    // }
    // }
    // if (!inIndexDir.delete()) {
    // inIndexDir.deleteOnExit();
    // }
    // }
    // }

    /** Redirects the root for the index directories for testing purposes and creates a member (and content) index.
     *
     * @param inBothIndexes boolean if <code>true</code> both member and content indexes are created, if
     *            <code>false</code>, only the member index is created
     * @throws Exception */
    public static void redirectDocRoot(final boolean inBothIndexes) throws Exception {
        final Properties lProperties = VSys.getVSysProperties();
        lProperties.setProperty(KEY_PROPERTY_DOCROOT, ".");
        VSys.setVSysProperties(lProperties);

        // we need that because we have to create new the indexes before we call the VIFIndexing singleton.
        createTestIndexes();
        VIFIndexing.INSTANCE.createTestIndexes();
        if (VIFIndexing.INSTANCE.checkIndex()) {
            AbstractVIFIndexer lIndexer = new VIFMemberIndexer();
            lIndexer.refreshIndex();

            if (inBothIndexes) {
                lIndexer = new VIFContentIndexer();
                lIndexer.refreshIndex();
            }
        }
    }

    private static void createTestIndexes() throws CorruptIndexException, LockObtainFailedException, IOException {
        createTestIndex(MEMBERS_INDEX_DB);
        createTestIndex(CONTENT_INDEX_DB);
    }

    private static void createTestIndex(final String inIndexName) throws CorruptIndexException,
            LockObtainFailedException, IOException {
        final IndexWriterConfig lConfig = new IndexWriterConfig(LUCENE_VERSION, getAnalyzer());
        lConfig.setOpenMode(OpenMode.CREATE);
        final Directory lDirectory = getDirectory(inIndexName);
        final IndexWriter lWriter = new IndexWriter(lDirectory, lConfig);
        try {
            lWriter.close();
        } finally {
            IndexWriter.unlock(lDirectory);
        }
    }

    private static Directory getDirectory(final String inIndexName) throws IOException {
        return FSDirectory.open(checkIndexDir(createPath(inIndexName)));
    }

    private static String createPath(final String inName) {
        return WorkspaceHelper.getRootDir() + File.separator + INDEX_DIR + inName;
    }

    private static File checkIndexDir(final String inPath) {
        final File outDir = new File(inPath);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        return outDir;
    }

    /** @return int the number of documents found in the members index.
     * @throws IOException */
    public static int countIndexedMembers() throws IOException {
        return VIFIndexing.INSTANCE.getMemberIndexWriter(false).numDocs();
    }

    /** @return int the number of documents found in the contents index
     * @throws IOException */
    public static int countIndexedContents() throws IOException {
        return VIFIndexing.INSTANCE.getContentIndexWriter(false).numDocs();
    }

    /** <p>
     * Searches the index for the specified <code>Query</code>.
     * </p>
     * <p>
     * You can evaluate the returned <code>Hits</code> object as follows:
     * </p>
     *
     * <pre>
     * Document[].length
     * </pre>
     *
     * @param inQuery Query as it is returned by
     *            <code>QueryParser.parse(String query, String field, Analyzer analyzer)</code>
     * @return Document[]
     * @throws IOException
     * @throws CorruptIndexException
     * @throws NoHitsException */
    public static Document[] search(final Query inQuery) throws CorruptIndexException, IOException, NoHitsException {
        Document[] outHits;
        final IndexReader lReader = DirectoryReader.open(getMembersIndexDir());
        try {
            outHits = search(inQuery, lReader);
        } finally {
            lReader.close();
        }
        return outHits;
    }

    /** <p>
     * Searches the index for the specified <code>Query</code> using the specified <code>IndexReader</code>.
     * </p>
     * <p>
     * You can evaluate the returned <code>Hits</code> object as follows:
     * </p>
     *
     * <pre>
     * Document[].length
     * Document[0].get(AbstractSearching.MEMBER_ID)
     * </pre>
     * <p>
     * <b>Note:</b> you have to close the <code>IndexReader</code> you pass by yourself.
     * </p>
     *
     * @param inQuery Query
     * @param inReader IndexReader
     * @return Document[]
     * @throws IOException
     * @throws NoHitsException
     * @throws CorruptIndexException */
    public static Document[] search(final Query inQuery, final IndexReader inReader) throws CorruptIndexException,
            NoHitsException, IOException {
        Document[] outHits;
        final IndexSearcher lSearcher = new IndexSearcher(inReader);
        outHits = processSearchResults(lSearcher.search(inQuery, NUMBER_OF_HITS), lSearcher);
        return outHits;
    }

    private static String getMemberDirName() throws IOException {
        return new File(".").getCanonicalFile().getParentFile().getCanonicalPath() + File.separator + MEMBERS_INDEX;
    }

    private static String getContentDirName() throws IOException {
        return new File(".").getCanonicalFile().getParentFile().getCanonicalPath() + File.separator + CONTENT_INDEX;
    }

    /** Wait for the specified amount of seconds.
     *
     * @param inSec int Seconds to wait.
     * @throws InterruptedException */
    public synchronized void waitFor(final int inSec) throws InterruptedException {
        final long lUntil = System.currentTimeMillis() + inSec * 1000;
        final long lSlice = inSec * 100;
        while (System.currentTimeMillis() < lUntil) {
            wait(lSlice);
        }
    }

    /** Evaluates the passed <code>TopDocs</code> and fills a <code>Document</code> array with the help of the passed
     * <code>Searcher</code>.
     *
     * @param inHits TopDocs
     * @param inSearcher Searcher
     * @return Document[] the top documents of the search.
     * @throws CorruptIndexException
     * @throws IOException
     * @throws NoHitsException */
    public static Document[] processSearchResults(final TopDocs inHits, final IndexSearcher inSearcher)
            throws CorruptIndexException, IOException, NoHitsException {
        if (inHits.totalHits == 0)
            throw new NoHitsException();

        final ScoreDoc[] lHits = inHits.scoreDocs;
        final Document[] outDocuments = new Document[lHits.length];
        for (int i = 0; i < outDocuments.length; i++) {
            outDocuments[i] = inSearcher.doc(lHits[i].doc);
        }
        return outDocuments;
    }

}
