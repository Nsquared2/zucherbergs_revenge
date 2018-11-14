var Player = function(id, name){
  this.id = id;
  this.name = name;
  this.action = "ignore";
  this.unread = false;
  this.requesting = "none";
  this.announce = "none";
}

Player.prototype.html = function(){
  return  '<div class="player-container" onclick="game.focusOn('+this.id+');"><div class="player"><div class="name">'+this.name+'</div><div class="request">requesting: <div class="'+this.requesting+'">'+this.requesting+'</div></div>'+
          '<button class="action cooperate '+((this.action == "cooperate") ? "selected" : "")+'" onclick="event.stopPropagation(); game.changeAction('+this.id+',\'cooperate\');">cooperate</button>'+
          '<button class="action ignore '+((this.action == "ignore") ? "selected" : "")+'" onclick="event.stopPropagation(); game.changeAction('+this.id+',\'ignore\');">ignore</button>'+
          '<button class="action betray '+((this.action == "betray") ? "selected" : "")+'" onclick="event.stopPropagation(); game.changeAction('+this.id+',\'betray\');">betray</button>'+
          '</div></div>';
}

var You = function(id){
  this.id = id;
  this.name = name;
  this.score = 0;
  this.place = "";
}

var Game = function(name, rounds, time_limit, you){
  this.name = name;
  this.rounds = rounds;
  this.round = 0;
  this.time_limit = time_limit;
  this.you = you;
  this.players = [];
  this.currentPlayer = false;
}

// adds a player to the game
Game.prototype.addPlayer = function(player){
  this.players.push(player);
  this.updatePlayers();
}

// gets a player by their ID
Game.prototype.getPlayer = function(id){
  for(i in this.players){
    if(this.players[i].id == id){
      return this.players[i];
    }
  }
  return false;
}

Game.prototype.updateCurrentPlayer = function(){
  messages = document.getElementById("messages-pane");
  
  document.getElementById("current-player").innerHTML = "to: "+this.currentPlayer.name;
  document.getElementById("request-select").value = this.currentPlayer.requesting;
  document.getElementById("announce-select").value = this.currentPlayer.announce;
  document.getElementById("action-select").value = this.currentPlayer.action;
}

Game.prototype.focusOn = function(id){
  this.currentPlayer = this.getPlayer(id);
  this.updateCurrentPlayer();
}

Game.prototype.updatePlayers = function(){
  p = document.getElementById("players");
  p.innerHTML = "";
  for(i in this.players){
    p.innerHTML += this.players[i].html();
  }
}

Game.prototype.changeAction = function(player_id, action){
  this.getPlayer(player_id).action = action;

  this.updatePlayers();

  message = "action "+player_id+" "+action;
  ws.send(message);
  console.log("OUT: "+message);
}

var UI = function(){
  this.players = document.getElementById("players");
  this.messages = document.getElementById("messages-pane");
}

me = new You(1337);
game = new Game("GAMENAME", "4", false, me);

player = new Player(20,"Paul");
game.addPlayer(player);

player = new Player(11,"Stebe");
game.addPlayer(player);

player = new Player(0,"Sarah");
game.addPlayer(player);

var ws = new WebSocket("ws://172.20.34.59:8090/");

// when a message is recieved from the server, parse it and decide how to update the interface/game information

ws.onmessage = function (evt) {
  console.log("IN : " + evt.data);
  message = evt.data.split(" ");
  switch(message[0]){
    case "message":
      // the message is a message to display in the chat
      if(message[1] == "server"){
        appendMessage(message.slice(2,message.length).join(" "),"server");
      }else{
        appendMessage(message.slice(2,message.length).join(" "),"them");
      }
      break;
  }
};


// in case of network or server issues:
ws.onclose = function() {
    alert("Connection to server closed");
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
  message = "message "+player_id+" request_"+request;
  ws.send(message);
  console.log("OUT: "+message);
}

// updates UI and server with new action
// player_id: player to whom the action is being done
// action: the action being done
function changeAction(player_id,action){
  message = "action "+player_id+" "+action;
  ws.send(message);
  console.log("OUT: "+message);
}

// this data structure holds information about the other players in the game
// will probably be changed in the future
var players = {0:"Ted Cruz"};

// this is the id of the player who's chat is currently being shown in the UI
// actions and messages selected in the control pane will apply to this player
var current_player = 0;

// when a request message is selected, send it to the server
document.getElementById("request-select").addEventListener("change",function(){
  request = document.getElementById("request-select").value;
  sendRequest(current_player,request);
});

// when an action is selected, send it to the server
document.getElementById("action-select").addEventListener("change",function(){
  action = document.getElementById("action-select").value;
  changeAction(current_player,action);
});











