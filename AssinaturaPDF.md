# Assinatura digital em PDF #

Exemplo baseado no encontrado em [stackoverflow](http://stackoverflow.com/questions/22178665/signing-a-pdf-with-an-eid-using-pkcs11-and-itext/22238404#22238404)
```
package exemplo;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.CrlClientOnline;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.text.pdf.security.OcspClient;
import com.itextpdf.text.pdf.security.OcspClientBouncyCastle;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.ProviderDigest;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.TSAClientBouncyCastle;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import org.poreid.config.POReIDConfig;
import org.poreid.crypto.POReIDProvider;

public class App {

    public void createPdf(String filename) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        document.add(new Paragraph("Assinado com o Cartão de Cidadão!"));
        document.close();
    }

    public void signPdf(String src, String dest)
            throws IOException, DocumentException, GeneralSecurityException {
          
        KeyStore ks = KeyStore.getInstance(POReIDConfig.POREID);
        ks.load(null);
        PrivateKey pk = (PrivateKey) ks.getKey(POReIDConfig.ASSINATURA, null);
        Certificate[] chain = ks.getCertificateChain(POReIDConfig.ASSINATURA);

        // reader and stamper
        PdfReader reader = new PdfReader(src);
        FileOutputStream os = new FileOutputStream(dest);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');

        // appearance
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason("qualquer motivo");
        appearance.setLocation("qualquer localização");
        appearance.setVisibleSignature(new Rectangle(72, 732, 144, 780), 1, "primeira assinatura");
        
        // timestamp
        TSAClient tsc = new TSAClientBouncyCastle("http://ts.cartaodecidadao.pt/tsa/server", "", "");
        
        // OCSP
        OcspClient ocsp = new OcspClientBouncyCastle();
        
        // long term validation (LTV)
        List<CrlClient> crlList = new ArrayList<>();
        crlList.add(new CrlClientOnline(chain));

        // digital signature
        ExternalSignature es = new PrivateKeySignature(pk, "SHA-256", POReIDConfig.POREID);
        ExternalDigest digest = new ProviderDigest(null);
        MakeSignature.signDetached(appearance, digest, es, chain, crlList, ocsp, tsc, 0, CryptoStandard.CMS);
    }

    public static void main(String[] args) throws DocumentException, IOException, GeneralSecurityException {
        Security.addProvider(new POReIDProvider());
        
        String fileSeparator = java.nio.file.FileSystems.getDefault().getSeparator();
        App exemplo = new App();

        exemplo.createPdf(System.getProperty("user.home") + fileSeparator + "exemplo.pdf");
        exemplo.signPdf(System.getProperty("user.home") + fileSeparator + "exemplo.pdf", System.getProperty("user.home") + fileSeparator + "exemplo.assinado.pdf");
    }
}
```


---

**ATENÇÃO: Este é um exemplo bastante simplificado, numa aplicação que tenha uma interface gráfica, as interações com o cartão não podem ser efetuadas na Event Dispatch Thread (EDT), desta forma, como meio de prevenir, o método getCard() não pode ser executado no contexto da Event Dispatch Thread.**


---

**NOTA: Se a versão do poreid for inferior à versão 1.45, para assinar sem OCSP, CRL e Timestamp o valor a utilizar no parametro estimatedSize  do método `signDetached` deverá ser 9000.**
```
MakeSignature.signDetached(appearance, digest, es, chain, null, null, null, 9000, CryptoStandard.CMS);
```