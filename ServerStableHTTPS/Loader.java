import java.util.ArrayList;

public class Loader {
	private static Integer grenze = 5000;
	public static ArrayList<Triple> allFiles = new ArrayList<Triple>();
	
	public static String getData(String rec){
		String[] recArr = rec.split("\n");
		String result = "";
		for(int i = 0; i<recArr.length; i++) {
			if(isNumber(recArr[i])) {
				result += recArr[i].substring(0, recArr[i].length()-1) + "\n";
			}
			else {
				boolean found = false;
				for(int z = 0; z<allFiles.size() && !found; z++) {
					String ean = Triple.toEAN(recArr[i]);
					if(allFiles.get(z).getArticle().containsKey(ean)) {
						String[] temp = allFiles.get(z).getArticle().get(ean);
						result += (allFiles.get(z).getCompany() + ";" 
							+ ean + ";" 
							+ temp[allFiles.get(z).getArticleNumCol()] + ";" 
							+ temp[allFiles.get(z).getTitleCol()] + ";" 
							+ temp[allFiles.get(z).getPriceCol()] + "\n");
						found = true;
					}
				}
				if(!found) {
					result += "Not Found: " + recArr[i] + "\n";
				}
			}
		}
		return result;
	}
	
	
	private static boolean isNumber(String text) {
		try {
			Integer i = Integer.parseInt(text);
			if(i < Loader.grenze) {
				return true;	
			}
			return false;
		}catch(Exception e){return false;}
	}

	
	

}


