import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class DiagGenerator {
	public static Boolean generatePNG(String grammar, String outPath){
		String webLink = "https://yuml.me/diagram/boring/class/" + grammar + ".png";
        URL url = null;
        HttpURLConnection conn = null;
		try {
			url = new URL(webLink);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		

		
		return null;
	}
}
