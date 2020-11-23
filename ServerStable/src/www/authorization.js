var psw = window.prompt("Please enter the password","Password");
var xhr = new XMLHttpRequest();
xhr.onreadystatechange = function() {
    if(xhr.readyState == 4){
        //if Response Recived work with it
        if(xhr.status == 200){
            document.open("text/html", "replace");
            document.write(xhr.responseText);
            document.close();
        }
        else {
            location.reload();
        }
    }
};
xhr.open("POST", "Password");
xhr.send(psw + "\n\n");

