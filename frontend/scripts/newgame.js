document.getElementById("timeLimitCheck").addEventListener("change",function(){
  document.getElementById("timeLimit").disabled = !this.checked;
});

document.getElementById("gameCodeCheck").addEventListener("change",function(){
  document.getElementById("gameCode").disabled = !this.checked;
});

document.getElementById("submit").addEventListener("click",function(event){
  message = "";

  message += "name ";
  message += document.getElementById("gameName").value;

  message += ", humans ";
  message += document.getElementById("players").value;

  message += ", ais ";
  message += document.getElementById("ais").value;

  if(document.getElementById("timeLimitCheck").checked){
    message += ", limit ";
    message += document.getElementById("timeLimit").value;
  }

  if(document.getElementById("gameCodeCheck").checked){
    message += ", code ";
    message += document.getElementById("gameCode").value;
  }

  console.log(message);

  event.preventDefault();
  
});