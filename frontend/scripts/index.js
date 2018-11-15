var Game = function(id, name, maxox, ox, rounds){
  this.id = id;
  this.name = name;
  this.maxox = maxox;
  this.occupancy = ox;
  this.rounds = rounds;
}

Game.prototype.show = function(){
  game = "<div class='open-game'>"+this.name+"<br>"+this.occupancy+"/"+this.maxox+" | "+this.rounds+" rounds </div>";
  document.getElementById("open-games").innerHTML += game;
}


var ws = new WebSocket("ws://172.20.34.59:8090/");

// when a message is recieved from the server, parse it and decide how to update the interface/game information

ws.onmessage = function (evt) {
  console.log("IN : " + evt.data);
  message = evt.data.split(", ");
  switch(message[0].split(" ")[0]){
    case "game":
      // the message is a message to display in the chat
      id = message[0].split(" ")[2];
      name = message[1].split(" ")[1];
      maxox = message[2].split(" ")[1];
      ox = message[3].split(" ")[1];
      rounds = message[4].split(" ")[1];
      game = new Game(id, name, maxox, ox, rounds);
      game.show();
  }
};

// some fake data to show
game = new Game(2, "test my game boi", 80, 17, 30);
game.show();


// in case of network or server issues:
ws.onclose = function() {
    alert("Connection to server closed");
};
ws.onerror = function(err) {
    alert("Error: " + err);
};