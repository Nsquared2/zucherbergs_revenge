var ws = new WebSocket("ws://172.20.33.125:8090/");

ws.onopen = function() {
    alert("Opened!");
    ws.send("Hello Server");
};

ws.onmessage = function (evt) {
  console.log(evt.data);
  message = evt.data.split(" ");
  switch(message[0]){
    case "message":
      if(message[1] == "server"){
        appendMessage(message[2],message[1]);
      }else{
        appendMessage(message[2],"them");
      }
      break;
  }
};

ws.onclose = function() {
    alert("Closed!");
};

ws.onerror = function(err) {
    alert("Error: " + err);
};


// adds a message to the current chat view
// text: the content of the message
// who: either "them", "server", "you"
function appendMessage(text, who){
  message = '<div class="message-container"><div class="message '+ who +'">'+ text +'</div></div>';
  document.getElementById("messages-pane").innerHTML += message;
}

// sends an action request to the server
// player_id: player who the request is being sent to
// request: either "ignore" "cooperate" "betray" or "none"
function sendRequest(player_id,request){
  ws.send("message "+ player_id +" request "+ request);
}

function changeAction(player_id,action){
  message = "action "+player_id+" "+action;
  ws.send(message);
  console.log("OUT: "+message);
}

var players = {0:"Ted Cruz"};

var current_player = 0;

document.getElementById("request-select").addEventListener("change",function(){
  request = document.getElementById("request-select").value;
  message = "message "+current_player+" request_"+request;
  ws.send(message);
  console.log("OUT: "+message);
});

document.getElementById("action-select").addEventListener("change",function(){
  action = document.getElementById("action-select").value;
  changeAction(current_player,action);
});











