var app = require('express')();
//server will listen to login and auth
var server = require('http').Server(app);
var io = require('socket.io')(server);

var playServer = require('http').Server(app);
var playIo = require('socket.io')(playServer);

//configure match server to listen on 8080
playServer.listen(8080, function(){
	console.log('Playserver listening...');	
});
//auth server running on 8000
server.listen(8000, function(){
	console.log('listening on the server...');	
});

//configuring server
io.on('connection', function(socket){
	console.log('player connected');

	socket.emit('socketId', { id: socket.id })

	socket.broadcast.emit('newPlayer', {id:  socket.id});

	socket.on('disconnect', function(){
		console.log('player disconected');
	})
});
//configuring 
playIo.on('connection', function(socket){
	console.log('player came to te match');

	socket.on('placeCard', function(resp){
		console.log(resp);
		socket.emit('enemyCardPlaced', resp);
		
	});



})