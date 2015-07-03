/**
	This package is part of the application VIF.
	Copyright (C) 2010-2015, Benno Luthiger

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.core.search;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.hip.vif.core.util.WorkspaceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** <p>
 * The singleton for creating the content and members index. This singleton holds <code>IndexWriter</code>s open for
 * that they can be used for index manipulation in the application.
 * </p>
 * <p>
 * <b>Note</b>: This index writer uses <code>org.apache.lucene.store.FSDirectory</code>. (see
 * {@link FileSystemDirectoryFactory})
 * </p>
 *
 * @author Luthiger Created: 20.06.2010
 * @see DirectoryFactory */
public enum VIFIndexing {
    INSTANCE;

    private final Logger LOG = LoggerFactory.getLogger(VIFIndexing.class); // NOPMD

    private static final String ERROR_MSG = "Can't access the index in %s!";

    // the index directories are defined here
    private static final String CONTENT_INDEX = "content";
    private static final String MEMBER_INDEX = "members";

    private static final String INDEX_NAME = "vifindex";

    private final DirectoryFactory directoryFactory = new FileSystemDirectoryFactory();

    private Directory contentDir;
    private Directory memberDir;
    private IndexWriter contentIndexWriter;
    private IndexWriter memberIndexWriter;
    private IndexReader contentIndexReader;
    private IndexReader memberIndexReader;

    VIFIndexing() {
        try {
            contentDir = directoryFactory.getDirectory(CONTENT_INDEX);
            contentIndexWriter = createChecked(contentDir);

            memberDir = directoryFactory.getDirectory(MEMBER_INDEX);
            memberIndexWriter = createChecked(memberDir);

            contentIndexReader = DirectoryReader.open(contentDir);
            memberIndexReader = DirectoryReader.open(memberDir);
        } catch (final IOException exc) {
            LOG.error("Errer encountered while initializing the Lucene indexes.", exc);
        }
    }

    private IndexWriter createChecked(final Directory inDir) throws IOException {
        if (inDir.listAll().length == 0) {
            final IndexWriter lNew = new IndexWriter(inDir, createConfiguration(false));
            lNew.commit();
            lNew.close();
        }
        return new IndexWriter(inDir, createConfiguration(false));
    }

    protected IndexWriterConfig createConfiguration(final boolean inCreateNew) {
        final IndexWriterConfig out = new IndexWriterConfig(AbstractSearching.LUCENE_VERSION,
                AbstractSearching.getAnalyzer());
        out.setOpenMode(inCreateNew ? OpenMode.CREATE : OpenMode.CREATE_OR_APPEND);
        return out;
    }

    /** Checks whether the index directories have been created.
     *
     * @return boolean <code>true</code> if both the content and member indexer have successfully been created. */
    public boolean checkIndex() {
        return contentIndexWriter != null && memberIndexWriter != null;
    }

    /** Returns the <code>IndexWriter</code> to index the the content.
     *
     * @param inCreate Boolean <code>true</code> if the <code>IndexWirter</code> has <code>OpenMode.CREATE</code> (i.e.
     *            the index is created and an existing index is overwritten), if <code>false</code>, the
     *            <code>IndexWirter</code> has <code>OpenMode.APPEND</code> (i.e. new documents are appended to existing
     *            index).<br />
     *            Use <code>true</code> only to build a new index (e.g. for an admin refresh).
     *
     * @return {@link IndexWriter}
     * @throws IOException
     * @throws LockObtainFailedException */
    public IndexWriter getContentIndexWriter(final boolean inCreate) throws LockObtainFailedException, IOException {
        if (contentIndexWriter == null) {
            throw new CorruptIndexException(String.format(ERROR_MSG, createPath(CONTENT_INDEX)));
        }
        if (!inCreate) {
            return contentIndexWriter;
        }

        try {
            contentIndexWriter.close();
        } finally {
            IndexWriter.unlock(contentDir);
        }
        contentIndexWriter = new IndexWriter(contentDir, createConfiguration(inCreate));
        return contentIndexWriter;
    }

    /** Creates a read-only <code>IndexReader</code> to read content documents from the index.
     *
     * @return {@link IndexReader}
     * @throws CorruptIndexException
     * @throws IOException */
    public synchronized IndexReader createContentIndexReader() throws CorruptIndexException, IOException { // NOPMD
        final IndexReader outReader = DirectoryReader.openIfChanged((DirectoryReader) contentIndexReader);
        if (outReader != null) {
            contentIndexReader.close();
            contentIndexReader = outReader;
        }
        return contentIndexReader;
    }

    /** Refreshes the index reader for content entries.<br />
     * Note: This method must be called after the index has been changed or recreated.
     *
     * @throws IOException */
    public void refreshContentIndexReader() throws IOException {
        contentIndexWriter.commit();
        contentIndexReader = DirectoryReader.open(contentIndexWriter, true);
    }

    /** Returns the <code>IndexWriter</code> to index the the members.
     *
     * @param inCreate Boolean <code>true</code> if the <code>IndexWirter</code> has <code>OpenMode.CREATE</code> (i.e.
     *            the index is created and an existing index is overwritten), if <code>false</code>, the
     *            <code>IndexWirter</code> has <code>OpenMode.APPEND</code> (i.e. new documents are appended to existing
     *            index).<br />
     *            Use <code>true</code> only to build a new index (e.g. for an admin refresh).
     *
     * @return {@link IndexWriter}
     * @throws IOException
     * @throws LockObtainFailedException */
    public IndexWriter getMemberIndexWriter(final boolean inCreate) throws LockObtainFailedException, IOException {
        if (memberIndexWriter == null) {
            throw new CorruptIndexException(String.format(ERROR_MSG, createPath(CONTENT_INDEX)));
        }
        if (!inCreate) {
            return memberIndexWriter;
        }

        try {
            memberIndexWriter.close();
        } finally {
            IndexWriter.unlock(memberDir);
        }
        memberIndexWriter = new IndexWriter(memberDir, createConfiguration(inCreate));
        return memberIndexWriter;
    }

    /** Creates a read-only <code>IndexReader</code> to read member documents from the index.
     *
     * @return {@link IndexReader}
     * @throws CorruptIndexException
     * @throws IOException */
    public synchronized IndexReader createMemberIndexReader() throws CorruptIndexException, IOException { // NOPMD
        final IndexReader outReader = DirectoryReader.openIfChanged((DirectoryReader) memberIndexReader);
        if (outReader != null) {
            memberIndexReader.close();
            memberIndexReader = outReader;
        }
        return memberIndexReader;
    }

    /** Refreshes the index reader for member entries.<br />
     * Note: This method must be called after the index has been changed or recreated.
     *
     * @throws IOException */
    public void refreshMemberIndexReader() throws IOException {
        memberIndexWriter.commit();
        memberIndexReader = DirectoryReader.open(memberIndexWriter, true);
    }

    /** For testing purposes: create the indexes manually.
     *
     * @throws CorruptIndexException
     * @throws LockObtainFailedException
     * @throws IOException */
    public void createTestIndexes() throws CorruptIndexException, LockObtainFailedException, IOException {
        if (contentIndexWriter != null && memberIndexWriter != null) {
            return;
        }
        final Directory lContentDir = directoryFactory.getDirectory(CONTENT_INDEX);
        contentIndexWriter = new IndexWriter(lContentDir, createConfiguration(false));

        final Directory lMemberDir = directoryFactory.getDirectory(MEMBER_INDEX);
        memberIndexWriter = new IndexWriter(lMemberDir, createConfiguration(false));

        contentIndexReader = DirectoryReader.open(lContentDir);
        memberIndexReader = DirectoryReader.open(lMemberDir);
    }

    /** <b>Note</b>: must be called in the application's <code>destroy()</code> method.
     *
     * @throws IOException
     * @throws CorruptIndexException
     * @deprecated no <code>close()</code> needed anymore, because index updates are commited */
    @Deprecated
    public void close() throws CorruptIndexException, IOException {
        if (memberIndexWriter != null) {
            memberIndexWriter.close();
            memberIndexWriter = null; // NOPMD
        }
        if (contentIndexWriter != null) {
            contentIndexWriter.close();
            contentIndexWriter = null; // NOPMD
        }

        if (memberIndexReader != null) {
            memberIndexReader.close();
            memberIndexReader = null; // NOPMD
        }

        if (contentIndexReader != null) {
            contentIndexReader.close();
            contentIndexReader = null; // NOPMD
        }
    }

    private String createPath(final String inName) {
        return WorkspaceHelper.getRootDir() + File.separator + INDEX_NAME + File.separator + inName;
    }

    // --- inner class ---

    private class FileSystemDirectoryFactory implements DirectoryFactory {
        private final Logger LOG = LoggerFactory.getLogger(FileSystemDirectoryFactory.class); // NOPMD

        @Override
        public Directory getDirectory(final String inIndexName) throws IOException {
            return FSDirectory.open(checkIndexDir(createPath(inIndexName)));
        }

        @Override
        public Directory getDirectoryForRefresh(final String inIndexName) throws IOException {
            return getDirectory(inIndexName);
        }

        @Override
        public void afterChangeAction(final String inIndexName) throws IOException {
            // Nothing to do.
        }

        @Override
        public void beforeChangeAction(final String inIndexName) throws IOException {
            // Nothing to do.
        }

        /** Returns the directory of the index. The directory (and its parents) is created if it doesn't exist yet.
         *
         * @param String The full path to the search index.
         * @return File The directory where the index resides. */
        private File checkIndexDir(final String inPath) {
            final File outDir = new File(inPath);
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            LOG.trace("Directory for lucene search index is {}.", outDir.getAbsolutePath());
            return outDir;
        }
    }

}
