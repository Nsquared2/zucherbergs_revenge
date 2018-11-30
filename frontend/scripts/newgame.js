// establish connection to server
var ws = new WebSocket("ws://172.20.47.177:8090/");

document.getElementById("timeLimitCheck").addEventListener("change",function(){
  document.getElementById("timeLimit").disabled = !this.checked;
});

document.getElementById("gameCodeCheck").addEventListener("change",function(){
  document.getElementById("gameCode").disabled = !this.checked;
});

document.getElementById("submit").addEventListener("click",function(event){
  message = "new_game";

  message += " ";
  message += document.getElementById("gameName").value;

  message += " ";
  message += document.getElementById("players").value;

  message += " ";
  message += document.getElementById("ais").value;

  if(document.getElementById("timeLimitCheck").checked){
    message += " time ";
    message += document.getElementById("timeLimit").value;
  }

  if(document.getElementById("gameCodeCheck").checked){
    message += " code ";
    message += document.getElementById("gameCode").value;
  }

  console.log(message);
  ws.send(message);

  event.preventDefault();

  window.location.href = "../frontend/index.html";
  
});