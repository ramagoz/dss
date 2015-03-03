package eu.europa.ec.markt.dss.cookbook.example.sign;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.smartcardio.CardException;

import be.fedict.eid.applet.Messages;
import be.fedict.eid.applet.sc.PcscEid;
import eu.europa.ec.markt.dss.DigestAlgorithm;
import eu.europa.ec.markt.dss.cookbook.sources.AppletView;
import eu.europa.ec.markt.dss.cookbook.sources.EidPrivateKeyEntry;
import eu.europa.ec.markt.dss.exception.DSSException;
import eu.europa.ec.markt.dss.signature.token.AbstractSignatureTokenConnection;
import eu.europa.ec.markt.dss.signature.token.DSSPrivateKeyEntry;
import eu.europa.ec.markt.dss.validation102853.CertificateToken;

public class EidNativeSignatureTokenConnection extends AbstractSignatureTokenConnection {

	private PcscEid eid;

	/**
	 * The default constructor for EidNativeSignatureTokenConnection.
	 */
	public EidNativeSignatureTokenConnection(AppletView view) {
		this.eid = new PcscEid(view, new Messages(Locale.ENGLISH));
	}

	@Override
	public void close() {
		eid.close();
	}

	@Override
	public List<DSSPrivateKeyEntry> getKeys() {
		try {
			eid.isEidPresent();

			List<X509Certificate> signatureChain = eid.getSignCertificateChain();
			List<DSSPrivateKeyEntry> entries = new ArrayList<DSSPrivateKeyEntry>();
			entries.add(new EidPrivateKeyEntry(new CertificateToken(signatureChain.get(0)), signatureChain));
			return entries;
		} catch (CardException ex) {
			Logger.getLogger(EidNativeSignatureTokenConnection.class.getName()).log(Level.SEVERE, null, ex);
			throw new DSSException(ex);
		} catch (IOException ex) {
			Logger.getLogger(EidNativeSignatureTokenConnection.class.getName()).log(Level.SEVERE, null, ex);
			throw new DSSException(ex);
		} catch (CertificateException ex) {
			Logger.getLogger(EidNativeSignatureTokenConnection.class.getName()).log(Level.SEVERE, null, ex);
			throw new DSSException(ex);
		}
	}

	//	@Override
	public byte[] encryptDigest(byte[] digestValue,  DigestAlgorithm digestAlgo, DSSPrivateKeyEntry keyEntry) throws NoSuchAlgorithmException {
		try {
			eid.isEidPresent();
			return eid.sign(digestValue, digestAlgo.getName());
		} catch (CardException ex) {
			Logger.getLogger(EidNativeSignatureTokenConnection.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}  catch (IOException ex) {
			Logger.getLogger(EidNativeSignatureTokenConnection.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		} catch (InterruptedException ex) {
			Logger.getLogger(EidNativeSignatureTokenConnection.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}
	}

}
