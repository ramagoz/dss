package eu.europa.esig.dss.asic.signature;

import javax.xml.crypto.dsig.XMLSignature;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.DomUtils;
import eu.europa.esig.dss.MimeType;
import eu.europa.esig.dss.asic.ASiCNamespace;
import eu.europa.esig.dss.utils.Utils;

/**
 * This class is used to generate the ASiCManifest.xml content (ASiC-E)
 *
 * Sample:
 * 
 * <pre>
 * <code>
 * 		<asic:ASiCManifest xmlns:asic="http://uri.etsi.org/02918/v1.2.1#">
 *			<asic:SigReference MimeType="application/pkcs7-signature" URI="META-INF/signature001.p7s">
 *				<asic:DataObjectReference URI="document.txt">
 *					<DigestMethod xmlns="http://www.w3.org/2000/09/xmldsig#" Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"/>
 *					<DigestValue xmlns="http://www.w3.org/2000/09/xmldsig#">OuL0HMJE899y+uJtyNnTt5B/gFrrw8adNczI+9w9GDQ=</DigestValue>
 *				</asic:DataObjectReference>
 *			</asic:SigReference>
 *		</asic:ASiCManifest>
 * </code>
 * </pre>
 */
public class ASiCEWithCAdESManifestBuilder {

	private final DSSDocument document;
	private final DigestAlgorithm digestAlgorithm;
	private final String signatureUri;

	public ASiCEWithCAdESManifestBuilder(DSSDocument document, DigestAlgorithm digestAlgorithm, String signatureUri) {
		this.document = document;
		this.digestAlgorithm = digestAlgorithm;
		this.signatureUri = signatureUri;
	}

	public Document build() {
		final Document documentDom = DomUtils.buildDOM();
		final Element asicManifestDom = documentDom.createElementNS(ASiCNamespace.ASiC, "asic:ASiCManifest");
		documentDom.appendChild(asicManifestDom);

		final Element sigReferenceDom = DomUtils.addElement(documentDom, asicManifestDom, ASiCNamespace.ASiC, "asic:SigReference");
		sigReferenceDom.setAttribute("URI", signatureUri);
		sigReferenceDom.setAttribute("MimeType", MimeType.PKCS7.getMimeTypeString());

		DSSDocument currentDetachedDocument = document;
		do {
			final String detachedDocumentName = currentDetachedDocument.getName();
			final Element dataObjectReferenceDom = DomUtils.addElement(documentDom, sigReferenceDom, ASiCNamespace.ASiC, "asic:DataObjectReference");
			dataObjectReferenceDom.setAttribute("URI", detachedDocumentName);

			final Element digestMethodDom = DomUtils.addElement(documentDom, dataObjectReferenceDom, XMLSignature.XMLNS, "DigestMethod");
			digestMethodDom.setAttribute("Algorithm", digestAlgorithm.getXmlId());

			final Element digestValueDom = DomUtils.addElement(documentDom, dataObjectReferenceDom, XMLSignature.XMLNS, "DigestValue");
			final byte[] digest = DSSUtils.digest(digestAlgorithm, currentDetachedDocument);
			final String base64Encoded = Utils.toBase64(digest);
			final Text textNode = documentDom.createTextNode(base64Encoded);
			digestValueDom.appendChild(textNode);

			currentDetachedDocument = currentDetachedDocument.getNextDocument();
		} while (currentDetachedDocument != null);

		return documentDom;

	}
}
