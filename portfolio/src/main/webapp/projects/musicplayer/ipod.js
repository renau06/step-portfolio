// Create your global variables below:
var tracklist = ["All We Got","No Problem", "Summer Friends", "D.R.A.M. Sings Special", "Blessings", "Same Drugs", "Mixtape", "Angels", "Juke Jam", "All Night"];
var volLevels = []; 
var colored; //variable to keep track of what the volume is currently filled in
var timeVar = null; //initializes the setInterval variable
var currentsong; //varibale to keep track of current song playing


function init() {
	var volumelevel='vl';
	for(var i=0; i<6; i++){
        var element= document.getElementById(volumelevel+i);
		volLevels.push(element);
        if(i<3){
            element=volLevels[i];
            element.style.backgroundColor= "rgba(92,157,221,1)";
        }
	}
	colored=2; //set intitial volume to 3
	currentsong=6; //set initial song to the 7th song "Mixtape"
}

function volUp() {
	if(colored==5){ //if volume is all the way up, do nothing
		return;
	}
	else{
		volLevels[colored+1].style.backgroundColor="rgba(92,157,221,1)"; //if volume not all the way up, color the next block and increase current volume variable
		colored++;
		return;
	}	
}


function volDown() {
	if(colored >=0){ //if volume is not all the way down, change current volume block to white and decrease current volume variable
	volLevels[colored].style.backgroundColor="rgba(0,0,0,0)";
	colored--;
    }
}

function timeElapsed(){
	document.getElementById('time-elapsed').innerText=secondsToMinutes(document.getElementById('player-time').value); //changes time to range bar value when range bar is clicked
}

function switchPlay() {
	if (timeVar){ //if the setInterval is running, stop it and show the pause button
		clearInterval(timeVar);
        document.getElementById('play-icon').className=('fa fa-play fa-lg');
		timeVar=null;
	}
	else{ //if setInterval not running, start it, and show the play button. Also, calls nextSong() once the range bar value is greater than 180
        document.getElementById('play-icon').className=('fa fa-pause fa-lg');
		clearInterval(timeVar);
		timeVar= setInterval(function increment(){
			if(document.getElementById('player-time').value==180){
				nextSong();
			}
			else if(document.getElementById('player-time').value<181){
				document.getElementById('player-time').value++;
				document.getElementById('time-elapsed').innerText=secondsToMinutes(document.getElementById('player-time').value);
				
			}
			
		}	
			,1000);
		
	}
}

function nextSong() { //sets range bar and time-elapsed value to 0, moves currentsong to one ahead (except if currentsong is 9,then moves to first song)
	document.getElementById('player-time').value= 0;
	document.getElementById('time-elapsed').innerText=secondsToMinutes(document.getElementById('player-time').value);
	if(currentsong==9){
		currentsong=0;
	}
	else{
		currentsong++;
	}
	
	document.getElementById('player-song-name').innerText= tracklist[currentsong];
}

function prevSong() { //sets range bar and time-elapsed value to 0, moves currentsong to previous (except if it is 0, then moves to last song)
	document.getElementById('player-time').value= 0;
	document.getElementById('time-elapsed').innerText=secondsToMinutes(document.getElementById('player-time').value);

	if(currentsong==0){
		currentsong=9
	}
	else{
		currentsong--;
	}
	document.getElementById('player-song-name').innerText=tracklist[currentsong];
}

function secondsToMinutes(d) {
    d = parseInt(d);

    var min = Math.floor(d / 60);
    var sec = Math.floor(d % 60);

    min= String(min).padStart(1,"0");
    sec= String(sec).padStart(2,"0");

    return min + ":" + sec;
}

init();