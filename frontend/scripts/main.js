// represents other players in the game
var Player = function(id, name){
  this.id = id;
  this.name = name;
  this.action = "ignore";
  this.unread = false;
  this.requesting = "none";
  this.announce = "none";
  this.messages = [];
}

// produces HTML to display player in player list on left of UI
Player.prototype.html = function(){
  return  '<div class="player-container" onclick="game.focusOn('+this.id+');"><div class="player"><div class="name">'+this.name+'</div><div class="request">requesting: <div class="'+this.requesting+'">'+this.requesting+'</div></div>'+
          '<button class="action cooperate '+((this.action == "cooperate") ? "selected" : "")+'" onclick="event.stopPropagation(); game.changeAction('+this.id+',\'cooperate\');">cooperate</button>'+
          '<button class="action ignore '+((this.action == "ignore") ? "selected" : "")+'" onclick="event.stopPropagation(); game.changeAction('+this.id+',\'ignore\');">ignore</button>'+
          '<button class="action betray '+((this.action == "betray") ? "selected" : "")+'" onclick="event.stopPropagation(); game.changeAction('+this.id+',\'betray\');">betray</button>'+
          '</div></div>';
}

// holds information about the player using the client
var You = function(id){
  this.id = id;
  this.name = name;
  this.score = 0;
  this.place = "";
}

// represents game state information
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
  if(this.currentPlayer == false){
    this.focusOn(player.id);
  }
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

// displays info in dash for the currently selected player
Game.prototype.updateCurrentPlayer = function(){
  messages = document.getElementById("messages-pane");
  
  document.getElementById("current-player").innerHTML = "to: "+this.currentPlayer.name;
  document.getElementById("request-select").value = this.currentPlayer.requesting;
  document.getElementById("announce-select").value = this.currentPlayer.announce;
  document.getElementById("action-select").value = this.currentPlayer.action;
}

// changes the currently selected player
Game.prototype.focusOn = function(id){
  this.currentPlayer = this.getPlayer(id);
  this.updateCurrentPlayer();
}

// updates all players in the players list
Game.prototype.updatePlayers = function(){
  p = document.getElementById("players");
  p.innerHTML = "";
  for(i in this.players){
    p.innerHTML += this.players[i].html();
  }
}

// sends an action request to the server
Game.prototype.sendRequest = function(player_id,request){
  message = "message "+player_id+" request_"+request;
  ws.send(message);
  console.log("OUT: "+message);
}

// sends an action announcement to the server
Game.prototype.sendAnnounce = function(player_id,announcement){
  message = "message "+player_id+" announce_"+announcement;
  ws.send(message);
  console.log("OUT: "+message);
}

// changes user's selected action for a given player
Game.prototype.changeAction = function(player_id, action){
  this.getPlayer(player_id).action = action;

  this.updatePlayers();
  this.updateCurrentPlayer();

  message = "action "+player_id+" "+action;
  ws.send(message);
  console.log("OUT: "+message);
}

// adds a message to the current chat view
// text: the content of the message
// who: either "them", "server", "you"
function appendMessage(text, who){
  message = '<div class="message-container"><div class="message '+ who +'">'+ text +'</div></div>';
  document.getElementById("messages-pane").innerHTML += message;
}

// TEST DATA
me = new You(1337);
game = new Game("GAMENAME", "4", false, me);

player = new Player(20,"Paul");
game.addPlayer(player);

player = new Player(11,"Stebe");
game.addPlayer(player);

player = new Player(0,"Sarah");
game.addPlayer(player);

// establish connection to server
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



// when a request message is selected, send it to the server
document.getElementById("request-select").addEventListener("change",function(){
  game.currentPlayer.request = document.getElementById("request-select").value;
  game.sendRequest(game.currentPlayer.id,game.currentPlayer.request);
});

// when an announce message is selected, send it to the server
document.getElementById("announce-select").addEventListener("change",function(){
  game.currentPlayer.announce = document.getElementById("announce-select").value;
  game.sendAnnounce(game.currentPlayer.id, game.currentPlayer.announce);
});

// when an action is selected, send it to the server
document.getElementById("action-select").addEventListener("change",function(){
  game.currentPlayer.action = document.getElementById("action-select").value;
  game.updatePlayers();
  game.changeAction(game.currentPlayer.id,game.currentPlayer.action);
});











