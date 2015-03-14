# Conversão de JPEG2000 para PNG #
A fotografia armazenada em cada Cartão de Cidadão está no formato jpeg2000.


Para este exemplo é necessário utilizar o Java Advanced Imaging Image-I/O ([disponível no repositório](http://code.google.com/p/poreid/source/browse/#svn%2Ftrunk%2Fjai.imageio)).

Exemplo de uma conversão de jpeg2000 para png.
```
package exemplo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.smartcardio.CardNotPresentException;
import org.poreid.CardFactory;
import org.poreid.CardTerminalNotPresentException;
import org.poreid.POReIDException;
import org.poreid.SmartCardFileException;
import org.poreid.UnknownCardException;
import org.poreid.cc.CitizenCard;
import org.poreid.dialogs.selectcard.CanceledSelectionException;


public class App {

    private void fromFile() {
        try {
            String fileSeparator = java.nio.file.FileSystems.getDefault().getSeparator();
            Path path = FileSystems.getDefault().getPath(System.getProperty("user.home") + fileSeparator + "FOTO.jp2");
            BufferedImage img = ImageIO.read(Files.newInputStream(path, StandardOpenOption.READ));
            ImageIO.write(img, "png", new File(System.getProperty("user.home") + fileSeparator +"FOTO.png"));
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void fromCard() {
        try {  
            CitizenCard cc = CardFactory.getCard();
            byte[] foto = cc.getPhotoData().getPhoto();
            cc.close();

            BufferedImage img = ImageIO.read(new ByteArrayInputStream(foto));
            String fileSeparator = java.nio.file.FileSystems.getDefault().getSeparator();
            ImageIO.write(img, "png", new File(System.getProperty("user.home") + fileSeparator + "FOTO.png"));
        } catch (CardTerminalNotPresentException | UnknownCardException | CardNotPresentException | 
                CanceledSelectionException | POReIDException | IOException | SmartCardFileException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        App app = new App();
        app.fromCard();
        //app.fromFile();
    }
}
```