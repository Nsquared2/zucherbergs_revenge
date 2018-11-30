// clear shell games
document.getElementById("open-games").innerHTML = "";

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

var Game = function(id, name, maxox, ox, rounds){
  this.id = id;
  this.name = name;
  this.maxox = maxox;
  this.occupancy = ox;
  this.rounds = rounds;
}

Game.prototype.show = function(){
  game = "<div class='open-game' onclick='joingame("+this.id+");'>"+this.name+"<br>"+this.occupancy+"/"+this.maxox+" | "+this.rounds+" rounds </div>";
  document.getElementById("open-games").innerHTML += game;
}


var ws = new WebSocket("ws://172.20.47.177:8090/");

// when a message is recieved from the server, parse it and decide how to update the interface/game information

ws.onmessage = function (evt) {
  console.log("IN : " + evt.data);
  message = evt.data.split(", ");
  switch(message[0]){
    case "game":
      // the message is a message to display in the chat
      id = message[1];
      name = message[2];
      maxox = message[3];
      ox = message[4];
      rounds = message[5];
      game = new Game(id, name, maxox, ox, rounds);
      game.show();
      break;
    case "newplayer":
      setCookie("playerid",message[1],10);
      break;
    case "game_joined":
      window.location.href = "../frontend/game.html";
      break;
    case "game_join_failed":
      alert("gmae join failed");
      // refresh games
      break;
  }
};

setCookie("playername",prompt("Player Name"),10);

ws.onopen = function(){
  // declare new player
  ws.send("newplayer "+getCookie("playername"));


  // get the public games
  ws.send("game_info");
}

// some fake data to show
game = new Game(1, "test my game boi", 80, 17, 30);
game.show();


// in case of network or server issues:
ws.onclose = function() {
    alert("Connection to server closed");
};
ws.onerror = function(err) {
    alert("Error: " + err);
};

function joingame(id){
  ws.send("joingame " + getCookie("playerid") + " " +id);
  console.log("OUT: joingame " + getCookie("playerid") + " " +id);
}

document.getElementById("sendgamecode").addEventListener("click",function(){
  ws.send("join code "+document.getElementById("gamecode").value);
  console.log("OUT: join code "+document.getElementById("gamecode").value);
});













