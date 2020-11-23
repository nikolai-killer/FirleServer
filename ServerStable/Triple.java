import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;


public class Triple {

	//Header der Spalten
    final public static String ean = "EAN";
    final public static String anr= "Artikelnummer";
    final public static String bez = "Bezeichnung";
    final public static String preis = "Netto";
	
	
	//index 0 steht spalte von ean, 1 = artikelnr; 2 = bezeichnung; 3= netto 
	private Integer[] cols = new Integer[4];
	private HashMap<String, String[]> article = new HashMap<String, String[]>();
	private String company;
	private double discount;
	
	
	public Triple(String filepath, String company, double discount) {
		this.company = company;
		this.discount = 1.0 - (discount/(double)100);
		//get all text
		String[] articleArr = getAllArticle(filepath);
		//get cols of the file
		this.cols = getCols(articleArr[0]);
		if(cols == null) {
			return;
		}
		//now map all article to a map
		this.article = getArticleMap(articleArr);
		
	}
	public Triple(String filepath, String company) {
		new Triple(filepath,company,0.0);
	}
	
	public HashMap<String,String[]> getArticle() {
		return this.article;
	}
	public String getCompany() {
		return this.company;
	}
	public double getDiscount() {
		return this.discount;
	}
	public Integer getEanCol() {
		return cols[0];
	}
	public Integer getArticleNumCol() {
		return cols[1];
	}
	public Integer getTitleCol() {
		return cols[2];
	}
	public Integer getPriceCol() {
		return cols[3];
	}
	
	
	private HashMap<String,String[]> getArticleMap(String[] articleArr){
		HashMap<String,String[]> res = new HashMap<String,String[]>();
		String[] splited;
		String key;
		for(int i = 1;i< articleArr.length; i++ ) {
			splited = articleArr[i].split(";");
			if(splited.length > this.cols[0] && splited.length > this.cols[1] && splited.length > this.cols[2] && splited.length > this.cols[3]) {
				
				key = toEAN(splited[this.cols[0]]);
				if(!res.containsKey(key)) {
					try{
						splited[this.getPriceCol()] = getPrice(splited[this.getPriceCol()]);
						res.put(key, splited);
					}catch(Exception e){
						System.out.println("One Line not loaded: " + i);
						e.printStackTrace(); 
					}
				}
			}	
			else{
				System.out.println("One Line not loaded: " + i);
			}
		}
		return res;
	}
	
	//return just all text of the FIle
	private String[] getAllArticle(String path) {
		System.out.println("Get article of " + path);
		String[] toAdd;
		String line = "";
		String everything = "";      
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF8"));
			while((line = br.readLine()) != null) {
				char[] replace = line.toCharArray();
				for(int u = 0; u<replace.length; u++){
					if(replace[u] == '\u20AC'){
						replace[u] = '\0';
					}
				}
				everything = everything + delWS(new String(replace)) + "\n";
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		toAdd = everything.split("\n");
		return toAdd;
	}
	
	//find out what cols are what field
	private Integer[] getCols(String head) {
		Integer[] cols = new Integer[4];
		String[] header = head.split(";");
		for(int z = 0; z < header.length; z++) {
			String word = header[z];
			if(word.toUpperCase().contains(Triple.ean.toUpperCase())) {
				cols[0] = z;
			}
			else if(word.toUpperCase().contains(Triple.anr.toUpperCase())) {
				cols[1] = z;
			}
			else if(word.toUpperCase().contains(Triple.bez.toUpperCase())) {
				cols[2] = z;
			}
			else if(word.toUpperCase().contains(Triple.preis.toUpperCase())) {
				cols[3] = z;
			}
		}
		for(int i = 0; i<cols.length; i++) {
			if(cols[i] == null) {
				System.out.println("No coll Found for: " + head);
				return null;
			}
		}
		return cols;
	}
	
	public static String toEAN(String oldEAN) {
		oldEAN = oldEAN.replaceAll("\\s+", "");
		char[] oldi = oldEAN.toCharArray();
		if(oldi.length == 13) {return oldEAN;}
		else {
			String newEAN = "";
			for(int i = 0; i<(13-oldi.length); i++) {
				newEAN += "0";
			}
			for(int i = 0; i<oldi.length; i++) {
				newEAN += oldi[i];
			}
			return newEAN;
		}
	}


	private String delWS(String s){
		String [] sarr = s.split(";");
		for(int i = 0; i<sarr.length; i++){
			sarr[i] = sarr[i].trim();
		}

		return String.join(";", sarr);
	}

	//To remove the comma if there are numbers above 1000
	private String getPrice(String old){
		String newPrice;
		if(old.contains(".") && old.contains(",")){
			newPrice = old.replace(",","");
		}
		else {
			newPrice = old.replace(",", ".");
		}
		
		Double tempPrice = Double.parseDouble(newPrice);
		
		if(this.company == "Lyra"){
			tempPrice = tempPrice / 1000;
		}
		newPrice = get2Deci(round( tempPrice * this.discount));
		return newPrice;
	}
	private static Double round(Double zahl) {
		zahl = zahl*100;
		zahl = (double) Math.round(zahl);
		zahl = zahl / 100;
		return zahl;
	}
	//To garantee 2 Decimal Numbers
	private static String get2Deci(Double p) {
		String res = "";
		int pr = (int) (p*100);
		for(int i = 0; pr>0 || i<3; i++) {
			int n = pr % 10;
			res = n + res;
			if(i == 1) {
				res = "." + res;
			}
			pr = pr/10;
		}
		return res;
	}
}
