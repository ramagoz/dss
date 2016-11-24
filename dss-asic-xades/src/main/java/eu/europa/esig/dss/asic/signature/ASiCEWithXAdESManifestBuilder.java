package eu.europa.esig.dss.asic.signature;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DomUtils;
import eu.europa.esig.dss.MimeType;

/**
 * This class is used to build the manifest.xml file (ASiC-E).
 * 
 * Sample:
 * 
 * <pre>
 * <code>
 * 		<manifest:manifest xmlns:manifest="urn:oasis:names:tc:opendocument:xmlns:manifest:1.0">
 * 			<manifest:file-entry manifest:full-path="/" manifest:media-type="application/vnd.etsi.asic-e+zip"/>
 * 			<manifest:file-entry manifest:full-path="test.txt" manifest:media-type="text/plain"/>
 * 			<manifest:file-entry manifest:full-path="test-data-file.bin" manifest:media-type="application/octet-stream"/>
 * 		</manifest:manifest>
 * </pre>
 * </code>
 *
 */
public class ASiCEWithXAdESManifestBuilder {

	public static final String MANIFEST_NS = "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0";

	private final DSSDocument document;

	public ASiCEWithXAdESManifestBuilder(DSSDocument document) {
		this.document = document;
	}

	public Document build() {
		final Document documentDom = DomUtils.buildDOM();
		final Element manifestDom = documentDom.createElementNS(MANIFEST_NS, "manifest:manifest");
		documentDom.appendChild(manifestDom);

		final Element rootDom = DomUtils.addElement(documentDom, manifestDom, MANIFEST_NS, "manifest:file-entry");
		rootDom.setAttribute("manifest:full-path", "/");
		rootDom.setAttribute("manifest:media-type", MimeType.ASICE.getMimeTypeString());

		DSSDocument currentDetachedDocument = document;
		do {
			Element fileDom = DomUtils.addElement(documentDom, manifestDom, MANIFEST_NS, "manifest:file-entry");
			fileDom.setAttribute("manifest:full-path", currentDetachedDocument.getName());
			fileDom.setAttribute("manifest:media-type", currentDetachedDocument.getMimeType().getMimeTypeString());

			currentDetachedDocument = currentDetachedDocument.getNextDocument();
		} while (currentDetachedDocument != null);

		return documentDom;
	}

}
