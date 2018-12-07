// establish connection to server
var ws = new WebSocket("ws://172.20.42.147:8090/");

// writes a cookie
function setCookie(cname, cvalue, minutes) {
    var d = new Date();
    d.setTime(d.getTime() + (minutes * 60 * 1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

// reads a cookie
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

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
  selected = "";
  if(game.currentPlayer.id == this.id){
    selected = "selected";
  }
  return  '<div class="player-container" onclick="game.focusOn('+this.id+');"><div class="player"><div class="name '+selected+'">'+this.name+'</div>'+
          '<button class="action cooperate '+((this.action == "cooperate") ? "selected" : "")+'" onclick="event.stopPropagation(); game.changeAction('+this.id+',\'cooperate\');">cooperate</button>'+
          '<button class="action ignore '+((this.action == "ignore") ? "selected" : "")+'" onclick="event.stopPropagation(); game.changeAction('+this.id+',\'ignore\');">ignore</button>'+
          '<button class="action betray '+((this.action == "betray") ? "selected" : "")+'" onclick="event.stopPropagation(); game.changeAction('+this.id+',\'betray\');">betray</button>'+
          '</div></div>';
}

// HTML for all messages of a player
Player.prototype.allMessages = function(){
  out = "";
  for(i in this.messages){
    out += this.messages[i].html();
  }
  return out;
}

// messages (to display in chat)
var Message = function(from, text){
  this.from = from;
  this.text = text;
}

// produces HTML to display in chat
Message.prototype.html = function(){
  if(this.from == "server"){
    frum = "server";
  }else if(this.from == "you"){
    frum = "you"
  }else{
    frum = "them";
  }
  return '<div class="message-container"><div class="message '+frum+'">'+this.text+'</div></div>';
}

// holds information about the player using the client
var You = function(){
  this.id = getCookie("playerid");
  this.name = getCookie("playername");
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

// updates info in dash for the currently selected player
// updates selected player
// updates messages pane
Game.prototype.updateCurrentPlayer = function(){
  this.updatePlayers();

  document.getElementById("messages-pane").innerHTML = this.currentPlayer.allMessages();
  document.getElementById("messages-pane").scrollTop = document.getElementById("messages-pane").scrollHeight;
  
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

Game.prototype.update_info = function(){
  document.getElementById("score").innerHTML = "Score: "+this.you.score+"<br>Place: "+this.you.place;
  document.getElementById("round").innerHTML = "Round "+this.round+"/"+this.rounds;
}

Game.prototype.final_scores = function(message){
  message = message.slice(1,message.length);
  text = "FINAL SCORES:<br>"
  for(i = 0; i < message.length-1; i+=2){
    text += message[i]+" "+message[i+1]+"<br>";
  }

  text += "<button onclick='window.location.href = \"../frontend/index.html\";'>Return to Index</button>";

  message = new Message("server",text);

  for(i in this.players){
    this.players[i].messages.push(message);
  }

  this.updateCurrentPlayer();
}

// adds a message to the current chat view
// text: the content of the message
// who: either "them", "server", "you"
function appendMessage(text, who){
  message = '<div class="message-container"><div class="message '+ who +'">'+ text +'</div></div>';
  document.getElementById("messages-pane").innerHTML += message;
}

// instantiate game constants
me = new You();
game = new Game("GAMENAME", 100, false, me);

// when a message is recieved from the server, parse it
// and decide how to update the interface/game information
ws.onmessage = function (evt) {
  console.log("IN : " + evt.data);
  message = evt.data.split(" ");
  switch(message[0]){
    case "game_info":
      game.name = message[2];
      game.rounds = parseInt(message[4]);
      game.round = parseInt(message[5]);
      game.time_limit = parseInt(message[5]);
      game.update_info();
      break;
    case "player":
      // info about a player in this game
      game.addPlayer(new Player(message[1],message[2]));
      break;
    case "message":
      // the message is a message to display in the chat
      if(message[1] == "server"){
        game.getPlayer(parseInt(message[2])).messages.push(new Message("server",message.slice(3,message.length).join(" ")));
      }else if(parseInt(message[2]) == game.you.id){
        game.getPlayer(parseInt(message[1])).messages.push(new Message("you",message.slice(3,message.length).join(" ")));
      }else{
        game.getPlayer(parseInt(message[2])).messages.push(new Message(parseInt(message[2]),message.slice(3,message.length).join(" ")));
        
        console.log(game.currentPlayer.messages);
        document.getElementById("messages-pane").innerHTML = game.currentPlayer.allMessages();
        document.getElementById("messages-pane").scrollTop = document.getElementById("messages-pane").scrollHeight;
      }
      game.updateCurrentPlayer();
      break;
    case "new_score":
      game.you.score = parseInt(message[1]);
      game.update_info();
      break;
    case "new_place":
      game.you.place = parseInt(message[1]);
      game.update_info();
      break;
    case "round_number":
      game.round = parseInt(message[1]);
      game.update_info();
      break;
    case "final_scores":
      game.final_scores(message);
      break;
  }
};

// tell the server you've joined the game
ws.onopen = function() {
  ws.send("update_player "+me.id);
}

// in case of network or server issues:
ws.onclose = function() {
    alert("Connection to server closed");
};


// when a request message is selected, send it to the server
document.getElementById("request-select").addEventListener("change",function(){
  game.currentPlayer.requesting = document.getElementById("request-select").value;
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

// when actions are confirmed, notify the server
document.getElementById("confirm").addEventListener("click", function(){
  ws.send("confirm");
  console.log("OUT: confirm");
});










