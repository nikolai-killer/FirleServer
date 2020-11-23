var headerTag = "Firma;EAN-Nummer;Arikel-Nr;Bezeichnung;Preis;Anzahl;Gesamtpreis";
var saveList = [];
var lastAddedList = [];
var lastAddedNum = 9;
var notFoundTag = "Not Found:"
var anzUA = "Anzahl unt.. Artikel: ";
var anzA = "Anzahl Artikel: ";
var gesP = "Gesamtpreis: ";
var lastArticle;


document.addEventListener('DOMContentLoaded', function(){
	//savebutton handler
	document.getElementById("safe").addEventListener("click", safeHandler);
	//deleteButton handler
	document.getElementById("delete").addEventListener("click", deleteHandler);
	//back button handler
	document.getElementById("backBut").addEventListener("click", goBack);
	
	//if pressed enter in Textfield getISBN fire search Button
	document.getElementById("getISBN").addEventListener("keyup", function(event){
		if(event.keyCode == 13 || event.which == 13){
			document.getElementById("searchBut").click();
		}
	});

	//if search Button is Pressed execute handleButton()
	document.getElementById('searchBut').addEventListener('click',handleButton);

});

function handleButton(){
	//get Text and correct it
	var text = getISBNText();
	if(text === ""){
		return;
	}
	var xhr = new XMLHttpRequest();  
    xhr.onreadystatechange = function() {
        if(xhr.readyState == 4){
			//if Response Recived work with it
			handleResponseText(xhr.responseText);
			updateLastAdded();                              
			updateBotLine();
		}
    };
	xhr.open("POST", "DATA",true);
	xhr.send(text);

}


//reads the Text, deletes all unnecesseary newlines
function getISBNText(){
	var text = document.getElementById("getISBN").value;
	
	//first save old
	saveList.push(new Save(lastAddedList));
	//first del getISBN Textarea
	document.getElementById("getISBN").value = "";

	while(text.startsWith("\n")){
		text = text.substring(1);
	}
	while(text.substring(text.length-1) === "\n"){
		text = text.substring(0,text.length-1);
	}
	
	if(text === ""){
		return "";
	}
	
	var output = text.split("");
	var res = "";
	for(var i = 0; i<output.length-1; i++){
		if(output[i] === "\n" && output[i+1] === "\n"){
		}else{
			res =  res + output[i];
		}
	}
	res += output[output.length-1];
	res = res + "\n\n";
	return res;
	
}

//handle the response and fill all fields
function handleResponseText(text){
	//get old entrys
	var notFoundText = document.getElementById("notFound").value;
	var resF = document.getElementById("resISBN").value.split("\n");
	var resFields = [];
	for(var i = 1; i<resF.length;i++){
		var temp = resF[i].split(";");
		if((temp.length !== 7 ) && (temp != "")){
			document.getElementById("audio").currentTime = 0;
			document.getElementById("audio").play();
			alert("Bitte das Ergebnis-Feld untersuchen, etwas stimmt nicht!");
			goBack();
			goBack();
			return;
		}
		resFields.push(resF[i].split(";"));
	}
	resFields.shift;
	
	//go though all recieved Lines and check if they are not found, already in the res Field or new
	text = text.split("\n");
	for(var i = 0; i<text.length; i++){
		//if it wasnt found then display it in not Found Box
		if(text[i].startsWith(notFoundTag)){
			text[i] = text[i].substring(notFoundTag.length + 1);
			notFoundText = text[i] + "\n" +  notFoundText;
			document.getElementById("audio").currentTime = 0;
			document.getElementById("audio").play();
		}
		else if(!(text[i] === "")){
			//if its a number icrease the last or display it in not found if not possible
			if(isNumber(text[i])){
				if(lastAddedList.length > 0){
					var anz = parseInt(text[i]);
					if(lastArticle){
						anz -= 1;
					}
					var found = false;
					var lAdded = lastAddedList[lastAddedList.length-1].split(";");
					for(var z = 0; z<resFields.length;z++){
						if(resFields[z][1] === lAdded[1]){
							resFields[z][5] = (anz + parseInt(resFields[z][5])).toString()
							resFields[z][6] = toDec((parseInt(resFields[z][5]) * parseFloat(resFields[z][4]))).toString();
							found = true;
						}
					}
					if(found){
						lastArticle = false;
						var l = lastAddedList[lastAddedList.length-1];
						for(var z = 0; z<anz;z++){
							lastAddedList.push(l);
						}
					}
				}
				else{
					notFoundText += text[i]  + "\n";
					document.getElementById("audio").currentTime = 0;
					document.getElementById("audio").play();
				}
			}
			//otherwise its a match so include it in res
			else{
				var found = false;;
				var line = text[i].split(";");
				for(var z = 0; z<resFields.length; z++){
					if(resFields[z][1] === line[1]){
						resFields[z][5] = (1 + parseInt(resFields[z][5])).toString();
						resFields[z][6] = toDec((parseInt(resFields[z][5]) * parseFloat(resFields[z][4]))).toString();
						found = true;
					}
				}
				if(!found){
					line.push("1");
					line.push(line[4]);
					resFields.push(line);
				}
				lastAddedList.push(text[i]);
				lastArticle = true;
			}
		}
	}
	
	//wrap the resFields to a string
	for(var i = 0; i < resFields.length; i++){
		resFields[i] = resFields[i].join(";");
	}
	resFields.unshift(headerTag);
	var resText = resFields.join("\n");
	
	// and fill them in
	document.getElementById("notFound").value = notFoundText;
	document.getElementById("resISBN").value = resText;
}

//handle the safe Button
function safeHandler(){
	//getText
	var text = document.getElementById("resISBN").value + "\n;;;;;Gesamtpreis:; " + updateBotLine() + "\n";
	//get Date
	var today = new Date();
	var date = today.getFullYear()+'-'+(today.getMonth()+1)+'-'+today.getDate();
	var time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
	var dateTime = date+'_'+time;
	
	download(text,"Sicherung_am_" + dateTime, "text/plain");
}

//handle delete Button
function deleteHandler(){
	var r = confirm("Sicher das Sie die Daten löschen wollen?");
	if(r == true){
		saveList.push(new Save(lastAddedList));
		document.getElementById("notFound").value = "";
		document.getElementById("resISBN").value = headerTag;
		lastAddedList = [];
		updateLastAdded();
		updateBotLine();
	}
}

//back button activated
function goBack(){
	if(saveList.length > 0){
		var lastSave = saveList.pop();
		document.getElementById("getISBN").value = lastSave.getField;
		document.getElementById("notFound").value = lastSave.notFField;
		document.getElementById("resISBN").value = lastSave.resultField;
		lastAddedList = [...lastSave.lastAddedArr];
		updateBotLine();
		updateLastAdded();
	}
}

//to update the counters
function updateBotLine(){
	var text = document.getElementById("resISBN").value;
	text = text.split("\n");
	var counter = 0;
	var countAll = 0;
	var gesPr = 0.0;
	for(var i = 1; i<text.length; i++){
		if(text[i] != ""){
			counter += 1;
			var splited = text[i].split(";");
			countAll += parseInt(splited[5]);
			gesPr += parseFloat(splited[6]);
		}
	}
	//update
	document.getElementById("anzUA").innerHTML = anzUA + counter + " ";
	document.getElementById("anzA").innerHTML = anzA + countAll + " ";
	document.getElementById("gesP").innerHTML = gesP + toDec(gesPr) + "€";

	return toDec(gesPr);
}

function updateLastAdded(){
	//get the latest Entrys out of the List
	var lastAddedEntrys = []
	var count = lastAddedList.length-1;
	while(lastAddedEntrys.length <= lastAddedNum && count >= 0){
		var line = lastAddedList[count];
		split = line.split(";");
		var newText = split[0] + ";" + split[3] + ";" + split[4];
		var found = false;
		for(var i = 0; i<lastAddedEntrys.length; i++){
			if(lastAddedEntrys[i].startsWith(newText)){
				split = lastAddedEntrys[i].split(";");
				split[3] = parseInt(split[3]) + 1;
				line = split.join(";");
				lastAddedEntrys[i] = line;
				found = true;
			}
		}
		if(!found){
			lastAddedEntrys.push(newText + ";1")
		}
		count--;
	}
	
	
	//fill Table with it
	var table = document.getElementById("lastAddedField");
	var bodys = table.getElementsByTagName("tbody");
	var trs = bodys[0].getElementsByTagName("tr");
	
	for(var row = 0; row<trs.length ; row++){
		if(row < lastAddedEntrys.length){
			var cols = trs[row].getElementsByTagName("th");
			var text = lastAddedEntrys[row].split(";");
			for(var i = 0; i<cols.length && i<text.length;i++){
				cols[i].innerHTML = text[i];
			}
		}
		else{
			var cols = trs[row].getElementsByTagName("th");
			for(var i = 0; i<cols.length;i++){
				cols[i].innerHTML = "";
			}
		}		
	}
}

function toDec (num){
	return parseFloat(Math.round(num * 100) / 100).toFixed(2);
}

function isNumber(text){
	if(text == 0){
		return true;
	}
	var split = text.split(";");
	if((parseInt(split[0]) || 0) ==  0){
		return false;
	}
	return true;
}