class Save{
	
	constructor(lastAddedAr){
		this.getField = document.getElementById("getISBN").value;
		this.notFField = document.getElementById("notFound").value;
		this.resultField = document.getElementById("resISBN").value;
		this.lastAddedArr = [...lastAddedAr];
	}
}