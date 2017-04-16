import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class DiagGenerator {
	public static void generatePNG(String grammar, String outPath){
		String webLink = "https://yuml.me/diagram/plain/class/" + grammar + ".png";
        URL url = null;
        HttpURLConnection conn = null;
        OutputStream outputStream=null;
		
        try {
			url = new URL(webLink);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 try {
				conn.setRequestMethod("GET");
		} catch (ProtocolException e) {
				e.printStackTrace();
		}
		conn.setRequestProperty("Accept", "application/json");    
		
		try {
			if (conn.getResponseCode() != 200) {
			    throw new RuntimeException(
			            "Failed : HTTP error code : " + conn.getResponseCode());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			 outputStream = new FileOutputStream(new File(outPath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int read = 0;
        byte[] bytes = new byte[1024];
        
        try {
			while ((read = conn.getInputStream().read(bytes)) != -1) {
			    outputStream.write(bytes, 0, read);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        conn.disconnect();
		

	}
}
