// establish connection to server
var ws = new WebSocket("ws://172.20.42.147:8090/");

// enable time limit field only when time limit is checked
document.getElementById("timeLimitCheck").addEventListener("change",function(){
  document.getElementById("timeLimit").disabled = !this.checked;
});

// enable game code field only when game is private
document.getElementById("gameCodeCheck").addEventListener("change",function(){
  document.getElementById("gameCode").disabled = !this.checked;
});

document.getElementById("submit").addEventListener("click",function(event){
  message = "new_game";

  message += " ";
  message += document.getElementById("gameName").value;

  message += " ";
  message += document.getElementById("rounds").value;

  message += " ";
  message += document.getElementById("players").value;

  message += " ";
  message += document.getElementById("ais").value;

  message += " ";
  message += document.getElementById("ai-difficulty").value;

  if(document.getElementById("timeLimitCheck").checked){
    message += " time ";
    message += document.getElementById("timeLimit").value;
  }

  if(document.getElementById("gameCodeCheck").checked){
    message += " code ";
    message += document.getElementById("gameCode").value;
  }

  if(document.getElementById("biased").checked){
    message += " biased";
  }

  // send the message
  ws.send(message);
  console.log(message);

  // don't actually send the form
  event.preventDefault();

  // after game creation, redirect to index
  window.location.href = "../frontend/index.html";
  
});