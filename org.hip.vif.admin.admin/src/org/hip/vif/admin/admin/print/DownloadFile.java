/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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

package org.hip.vif.admin.admin.print;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.hip.kernel.util.XSLProcessingException;
import org.hip.vif.admin.admin.Activator;
import org.hip.vif.admin.admin.Constants;
import org.ripla.interfaces.IMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.DownloadStream;
import com.vaadin.server.FileResource;

/**
 * This class handles the download of the OOOrg document.
 * 
 * @author Luthiger Created: 30.12.2011
 */
@SuppressWarnings("serial")
public class DownloadFile extends FileResource {
	private static final Logger LOG = LoggerFactory
			.getLogger(DownloadFile.class);

	private static final String DEFAULT_MIME_TYPE = "application/vnd.oasis.opendocument.text"; //$NON-NLS-1$
	private static final String DEFAULT_FILE_NAME = "vif_out_%s.odt"; //$NON-NLS-1$
	private static final DateFormat FORMAT_FILE_NAME = new SimpleDateFormat(
			"yyyy-MM-dd"); //$NON-NLS-1$

	private final static String MANIFEST = "manifest.xml"; //$NON-NLS-1$
	private final static String MIMETYPE = "mimetype"; //$NON-NLS-1$
	private final static String STYLES = "styles.xml"; //$NON-NLS-1$
	private final static String META = "meta.xml"; //$NON-NLS-1$
	private final static String CONTENT = "content.xml"; //$NON-NLS-1$
	private static final String CONTENT_START = "contentStart.txt"; //$NON-NLS-1$
	private static final String CONTENT_MIDDLE = "contentMiddle.txt"; //$NON-NLS-1$

	private final CircularByteBuffer buffer = new CircularByteBuffer(
			CircularByteBuffer.INFINITE_SIZE);
	private final static int BUFFER_LEN = 16384;
	private final DateFormat dateFormat;

	private final IMessages messages;
	private final Collection<GroupExtent> groups;
	private final Locale locale;

	/**
	 * Constructor.
	 * 
	 * @param inGroups
	 *            Collection of {@link GroupExtent}
	 * @param inLocale
	 *            {@link Locale}
	 */
	public DownloadFile(final Collection<GroupExtent> inGroups,
			final Locale inLocale) {
		super(new File(getFileName()));

		groups = inGroups;
		locale = inLocale;

		messages = Activator.getMessages();
		dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, inLocale);

		try {
			final Vector<GroupContentRenderer> lContents = new Vector<GroupContentRenderer>(
					groups.size());
			for (final GroupExtent lGroup : groups) {
				lContents
						.add(new GroupContentRenderer(lGroup, locale, messages));
			}

			renderToStream(buffer.getOutputStream(), lContents);
		}
		catch (final Exception exc) {
			LOG.error(
					"Error encountered while printing the discussion groups!", exc); //$NON-NLS-1$
		}
	}

	@Override
	public DownloadStream getStream() {
		try {
			final DownloadStream out = new DownloadStream(
					buffer.getInputStream(), getMIMEType(), getFileName());
			out.setCacheTime(0);
			out.setParameter(
					"Content-Disposition", "attachment; filename=\"" + getFileName() + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			out.setParameter(
					"Content-Length", String.valueOf(buffer.getSize() - buffer.getSpaceLeft())); //$NON-NLS-1$
			LOG.debug("Printed discusion group to file \"{}\".", getFileName()); //$NON-NLS-1$
			return out;
		}
		catch (final Exception exc) {
			LOG.error(
					"Error encountered while printing the discussion groups!", exc); //$NON-NLS-1$
		}
		return null;
	}

	private void renderToStream(final OutputStream inStream,
			final Vector<GroupContentRenderer> inContents) throws IOException {
		JarOutputStream lJar = new JarOutputStream(inStream);
		ByteArrayOutputStream lTOCArray = null;
		try {
			addEntry(
					lJar,
					MIMETYPE,
					"No mimetype file found.", "", createMimeTypeEntry(MIMETYPE)); //$NON-NLS-1$ //$NON-NLS-2$
			addEntry(lJar, MANIFEST, "No manifest found.", "META-INF/", null); //$NON-NLS-1$ //$NON-NLS-2$
			addEntry(lJar, STYLES, "No styles specification found."); //$NON-NLS-1$

			// write entry "content.xml"
			lJar.putNextEntry(new JarEntry(CONTENT));
			lJar = (JarOutputStream) printContent(lJar, CONTENT_START,
					"No content template (contentStart.txt) found."); //$NON-NLS-1$
			lJar.write(String
					.format(Constants.CONTENT_START,
							messages.getMessage("admin.print.meta.desc"), //$NON-NLS-1$
							messages.getFormattedMessage(
									"admin.print.meta.subtitle", dateFormat.format(new Date()))).getBytes()); //$NON-NLS-1$

			lTOCArray = new ByteArrayOutputStream();
			lTOCArray = (ByteArrayOutputStream) printContent(lTOCArray,
					CONTENT_MIDDLE,
					"No content template (contentMiddle.txt) found."); //$NON-NLS-1$
			final String lTOCLabel = messages
					.getMessage("admin.print.meta.toc"); //$NON-NLS-1$
			lJar.write(String
					.format(lTOCArray.toString(), lTOCLabel, lTOCLabel)
					.getBytes());

			for (final GroupContentRenderer lRenderer : inContents) {
				try {
					lRenderer.renderAsOO(lJar);
				}
				catch (final XSLProcessingException exc) {
					LOG.error(
							"Error encountered during XSL transformation!", exc); //$NON-NLS-1$
				}
			}
			lJar.write(Constants.CONTENT_END.getBytes());
			createOOMeta(lJar);
		} finally {
			lJar.close();
			if (lTOCArray != null) {
				lTOCArray.close();
			}
		}
	}

	private OutputStream printContent(final OutputStream inOutput,
			final String inResource, final String inMsg) throws IOException {
		final URL lResource = getResource(inResource, inMsg);
		InputStream lInput = null;
		try {
			lInput = lResource.openStream();
			final byte lBuffer[] = new byte[BUFFER_LEN];
			int lRead;
			while ((lRead = lInput.read(lBuffer, 0, BUFFER_LEN)) != -1) {
				inOutput.write(lBuffer, 0, lRead);
			}
		} finally {
			if (lInput != null)
				lInput.close();
		}
		return inOutput;
	}

	private JarEntry createMimeTypeEntry(final String inName) {
		final JarEntry outEntry = new JarEntry(inName);
		outEntry.setMethod(ZipEntry.STORED);
		outEntry.setSize(39);
		outEntry.setCrc(0xC32C65E);
		return outEntry;
	}

	private void addEntry(final JarOutputStream inOutputJar,
			final String inResourceName, final String inExceptionMsg,
			final String inPrefix, final JarEntry inEntry) throws IOException {
		final URL lResource = getResource(inResourceName, inExceptionMsg);
		JarEntry lEntry = inEntry;
		if (lEntry == null) {
			lEntry = new JarEntry(inPrefix + getResourceName(lResource));
		}
		inOutputJar.putNextEntry(lEntry);

		InputStream lInput = null;
		try {
			lInput = lResource.openStream();
			final byte lBuffer[] = new byte[BUFFER_LEN];
			int lRead;
			while ((lRead = lInput.read(lBuffer, 0, BUFFER_LEN)) != -1) {
				inOutputJar.write(lBuffer, 0, lRead);
			}
		} finally {
			if (lInput != null)
				lInput.close();
		}
	}

	private void addEntry(final JarOutputStream inOutputJar,
			final String inResourceName, final String inExceptionMsg)
			throws IOException {
		addEntry(inOutputJar, inResourceName, inExceptionMsg, "", null); //$NON-NLS-1$
	}

	private URL getResource(final String inResourceName,
			final String inExceptionMsg) throws IOException {
		final URL outUrl = getClass().getResource(inResourceName);
		if (outUrl == null) {
			throw new IOException(inExceptionMsg);
		}
		return outUrl;
	}

	private String getResourceName(final URL inResource) {
		final String[] lParts = inResource.getPath().split("/"); //$NON-NLS-1$
		return lParts[lParts.length - 1];
	}

	private void createOOMeta(final JarOutputStream inOutputJar)
			throws IOException {
		final URL lResource = getResource(META, "No metadata template found."); //$NON-NLS-1$
		final StringWriter lWriter = new StringWriter();

		BufferedReader lReader = null;
		Writer lOutput = null;
		try {
			// read template
			final InputStream lInput = lResource.openStream();
			lReader = new BufferedReader(new InputStreamReader(lInput));
			final char lBuffer[] = new char[BUFFER_LEN];
			int lRead;
			while ((lRead = lReader.read(lBuffer, 0, BUFFER_LEN)) != -1) {
				lWriter.write(lBuffer, 0, lRead);
			}

			// add document specific information
			String lMeta = lWriter.toString();
			final String lDateTime = dateFormat.format(new Date());
			final String lTitle = messages.getMessage("admin.print.meta.desc"); //$NON-NLS-1$
			lMeta = String
					.format(lMeta,
							lTitle,
							lTitle,
							messages.getMessage("admin.print.meta.subject"), lDateTime, lDateTime); //$NON-NLS-1$

			// write to output
			final JarEntry lEntry = new JarEntry(META);
			inOutputJar.putNextEntry(lEntry);

			final StringReader lMetaReader = new StringReader(lMeta);
			lOutput = new BufferedWriter(new OutputStreamWriter(inOutputJar));
			while ((lRead = lMetaReader.read(lBuffer, 0, BUFFER_LEN)) != -1) {
				lOutput.write(lBuffer, 0, lRead);
			}
		} finally {
			if (lReader != null)
				lReader.close();
			if (lOutput != null)
				lOutput.close();
		}
	}

	/**
	 * @return String the created file's name
	 */
	public static String getFileName() {
		return String.format(DEFAULT_FILE_NAME,
				FORMAT_FILE_NAME.format(new Date()));
	}

	/**
	 * @return String the file's MIME type
	 */
	@Override
	public String getMIMEType() {
		return DEFAULT_MIME_TYPE;
	}

}
