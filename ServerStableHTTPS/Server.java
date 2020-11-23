import java.net.*;
import java.io.OutputStreamWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;

import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.util.ArrayList;

public class Server{
    // Port of Server
    final static int port = 443;
    //Dir in which the requestet Data is send
    public static final String reqdir = "src" + File.separator + "Requests";
    
    
    
    public static void main(String[] args){
      init();
      try{ 
        //Keystore
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream("full.pkcs12"),"keystore".toCharArray());

        // Create key manager
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, "keystore".toCharArray());
        KeyManager[] km = keyManagerFactory.getKeyManagers();

        //Trustmanager
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);
        TrustManager[] tm = trustManagerFactory.getTrustManagers();
      
        // Initialize SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLSv1");
        sslContext.init(km,  tm, null);

        

        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();

        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(Server.port);
      
        System.out.println("everything set up on Port: "  + Server.port);
        
        Thread redThread = new Thread(){
          public void run(){
            try{
            ServerSocket socket = new ServerSocket(80);
            while(true){
             Socket so = socket.accept();
             BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(so.getOutputStream()));
		         String response = "";
             response += "HTTP/1.1 301 Moved Permanently\r\n";
             response += "Location: https://nikosserver.xyz\r\n";
	          	response += "\r\n";
             bw.write(response);
             bw.close();
            }
          }
          catch(Exception e){e.printStackTrace();}
          }
        };
        redThread.start();

        while(true) {
          SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
          ClientHandler ch = new ClientHandler(sslSocket);
          ch.start();
        }
      }
      catch(Exception e){e.printStackTrace();}
    }


    private static void init(){
        ArrayList<Triple> ar = new ArrayList<Triple>();
        ar.add(new Triple("./src/DATA/BullyBuschData2018.txt","Bullyland",0));
        ar.add(new Triple("./src/DATA/Skyjo.txt","Magilano",0));
        ar.add(new Triple("./src/DATA/Tonies.txt","Tonies",0));
        ar.add(new Triple("./src/DATA/SC_Verschnitt.txt","SC",0));
        ar.add(new Triple("./src/DATA/Lyra.txt","Lyra",0));
        ar.add(new Triple("./src/DATA/Ballaballa.txt","Ballaballa",0));
        ar.add(new Triple("./src/DATA/Aurich.txt","Aurich",0));
        ar.add(new Triple("./src/DATA/Haba.txt","Haba",0));
        ar.add(new Triple("./src/DATA/Grimms1.txt","Grimms",0));
        ar.add(new Triple("./src/DATA/Grimms2.txt","Grimms",0));
        ar.add(new Triple("./src/DATA/Smartgames.txt","Smartgames",0));
        ar.add(new Triple("./src/DATA/Playmobil.txt","Playmobil",9));
        ar.add(new Triple("./src/DATA/Amigo.txt","Amigo",20));
        ar.add(new Triple("./src/DATA/Carletto.txt","Carletto",5));
        ar.add(new Triple("./src/DATA/Denkriese.txt","Denkriesen",0));
        ar.add(new Triple("./src/DATA/Depesche.txt","Depesche",0));
        ar.add(new Triple("./src/DATA/Emil.txt","Emil",0));
        ar.add(new Triple("./src/DATA/Fantasy4Kids.txt","Fantasy4Kids",0));
        ar.add(new Triple("./src/DATA/FunTrading.txt","FunTrading",0));
        ar.add(new Triple("./src/DATA/Goldbek.txt","Goldbek",0));
        ar.add(new Triple("./src/DATA/Herbertz.txt","Herbertz",0));
        ar.add(new Triple("./src/DATA/Intex.txt","Intex",0));
        ar.add(new Triple("./src/DATA/Jofrika.txt","Jofrika",0));
        ar.add(new Triple("./src/DATA/Kappla.txt","Kappla",0));
        ar.add(new Triple("./src/DATA/Kosmos.txt","Kosmos",5));
        ar.add(new Triple("./src/DATA/KosmosB.txt","Kosmos",5));
        ar.add(new Triple("./src/DATA/BBJunior.txt","BBJunior",10));
        ar.add(new Triple("./src/DATA/Lang.txt","Lang",0));
        ar.add(new Triple("./src/DATA/LutzMauder.txt","LutzMauder",0));
        ar.add(new Triple("./src/DATA/Mosese.txt","Moses",0));
        ar.add(new Triple("./src/DATA/Mottoland.txt","Mottoland",0));
        ar.add(new Triple("./src/DATA/PL.txt","Spielstabil",0));
        ar.add(new Triple("./src/DATA/PL2.txt","Spielstabil",0));
        ar.add(new Triple("./src/DATA/Ravensburger.txt","Ravensburger",5));
        ar.add(new Triple("./src/DATA/Rubies.txt","Rubies",0));
        ar.add(new Triple("./src/DATA/Schleich1.txt","Schleich",7.5));
        ar.add(new Triple("./src/DATA/Schleich2.txt","Schleich",7.5));
        ar.add(new Triple("./src/DATA/Schmidtspiele.txt","Schmidtspiele",4));
        ar.add(new Triple("./src/DATA/Selecta.txt","Selecta",10));
        ar.add(new Triple("./src/DATA/Sigikid.txt","Sigikid",0));
        ar.add(new Triple("./src/DATA/Spiegelburg.txt","Spiegelburg",0));
        ar.add(new Triple("./src/DATA/Sunflex.txt","Sunflex",0));
        ar.add(new Triple("./src/DATA/Trendhaus.txt","Trendhaus",5));
        ar.add(new Triple("./src/DATA/Klostermann.txt","Klostermann",0));
        ar.add(new Triple("./src/DATA/TYUK.txt","Ty_Uk",0));
        ar.add(new Triple("./src/DATA/Vedes.txt","Vedes",0));
        ar.add(new Triple("./src/DATA/HuZ.txt","H+Z",0));
        ar.add(new Triple("./src/DATA/Iden.txt","Iden",0));

    Loader.allFiles = ar;
    }

  }
