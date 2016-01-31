/**
 *  Kodi Media Center
 *
 *  Copyright 2016 Josh Lyon
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
import groovy.json.JsonSlurper

metadata {
	definition (name: "Kodi Media Center", namespace: "boshdirect", author: "Josh Lyon") {
		capability "Media Controller"
		capability "Music Player"
		capability "Polling"
		capability "Refresh"
        
        attribute "destURL", "string"
        attribute "currentWindowID", "string" //the current window ID from Window.GetProperties 
        attribute "playerID", "number" //the current active player ID
        attribute "kodiVersion", "string" //kodi app version
        attribute "kodiName", "string" //kodi name
        
        command "splitURL", [ "string" ]
        command "toggleMute"
        
        command "inputUp"
        command "inputDown"
        command "inputLeft"
        command "inputRight"
        command "inputInfo"
        command "inputBack"
        command "inputSelect"
        command "inputHome"
        
        command "getActivePlayers"
        command "getVideoPlayerStatus", [ "number" ]
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
    	//Row 1 - Mult Attribute Tile
        multiAttributeTile(name:"kodiMulti", type:"generic", width:6, height:4) {
          tileAttribute("device.status", key: "PRIMARY_CONTROL") {
          	attributeState("default", label: '--', backgroundColor:"#79b821")
            attributeState("paused", label:'Paused', icon: "st.sonos.pause-btn", backgroundColor:"#79b821")
            attributeState("playing", label:'Playing', icon: "st.sonos.play-btn", backgroundColor:"#79b821")
            attributeState("stopped", label:'Stopped', icon: "st.sonos.stop-btn", backgroundColor:"#79b821")
          }
          /*
          tileAttribute("device.trackDescription", key: "SECONDARY_CONTROL") {
            attributeState("default", label:'${currentValue}', unit:"")
          }
          */
        }
        
        //Row 2 - Thin
        valueTile("track", "device.trackDescription", decoration: "flat", width: 6, height: 2){ 
        	state "default", label: 'Now Playing: ${currentValue}'
        }
        valueTile("activity", "device.currentActivity", decoration: "flat", width: 3){ 
        	state "default", label: '${currentValue}'
        }

        
        //----------------inputs-------------------
        //Row 3
        standardTile("input.home", "device.currentActivity", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputHome", label: "Home", icon: "st.Home.home2"
        }
        valueTile("input.up", "device.currentActivity", decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputUp", label: 'Γåæ'
        }
        valueTile("input.info", "device.currentActivity", decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputInfo", label: "  INFO  "
        }
        //Row 4
        valueTile("input.left", "device.currentActivity", decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputLeft", label: "ΓåÉ"
        }
        valueTile("input.select", "device.currentActivity",  decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputSelect", label: "SELECT"
        }
        valueTile("input.right", "device.currentActivity",  decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputRight", label: "ΓåÆ"
        }
        //Row 5
        valueTile("input.back", "device.currentActivity", decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputBack", label: "  BACK  "
        }
        valueTile("input.down", "device.currentActivity", decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputDown", label: "Γåô"
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
            state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        
        
        //----------------playback controls-----------------------
        //Row 1 (6)
        standardTile("input.previous", "device.status", decoration: "flat", height: 2, width: 2) {
            state "default", action:"previousTrack", icon:"st.sonos.previous-btn" //, label: "ΓùÇΓùÇ"
        }
        standardTile("input.playpause", "device.status", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
            state "paused", action:"play",icon: "st.sonos.play-btn", nextState:"playing"//, label: "Play" 
            state "playing", action:"pause",icon: "st.sonos.pause-btn", nextState:"paused"//, label: "Pause" //the second character is pause Γ¥ÜΓ¥Ü Γû╢/ΓÅ╕
            
        }
        standardTile("input.next", "device.status", decoration: "flat", height: 2, width: 2) {
            state "default", action:"nextTrack", icon:"st.sonos.next-btn" //, label: "Γû╢Γû╢"
        }
        
        //--------------Volume Control---------------------
        //Row 1 (7)
        controlTile("volume", "device.level", "slider", decoration: "flat", width:4){
        	state "level", action: "switch level.setLevel"
        }
        standardTile("mute", "device.mute", decoration: "flat", height: 2, width: 2){ 
        	state "default", label: '${currentValue}', action: "toggleMute"
            state "muted", label: "Unmute", icon: "st.custom.sonos.unmuted", action: "unmute", nextState:"unmuted"
            state "unmuted", label: "Mute", icon: "st.custom.sonos.muted", action: "mute", nextState:"muted"
        }
        //Row 2 (8) - extra data
        valueTile("destURL", "device.destURL", decoration: "flat", width: 4){ 
        	state "default", label: '${currentValue}'
        }
        
        //--------------TESTING -------------------
        //supporting function tiles -- most will be removed when this is released to production
        standardTile("urlsplitter", "device.destURL", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
            state "default", action:"splitURL", icon:"st.Office.office12", label: "Parse URL"
        }
        standardTile("videoStatus", "device.status", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
            state "default", action:"getVideoStatus", label: "Get Video Status"
        }

        
        //For the lists...
        standardTile("mainOverview", "device.status", decoration: "flat", height: 2, width: 2) {
            state "default", action:"refresh", icon: "st.Electronics.electronics18"
        }
        
        main(["mainOverview"])
        details([
        	//"kodiMulti",
        	"track",// "activity",
        	"input.home", "input.up", "input.info",
            "input.left", "input.select", "input.right",
            "input.back", "input.down", "refresh",
            "input.previous", "input.playpause", "input.next",
            "volume", "mute",
            "destURL"
            //"urlsplitter", "videoStatus"
            ])
	}
}

preferences{
	input("overrideURL", "text", title: "Kodi URL", description: "Full URL to Kodi Webserver, including port")
    //input("destPort", "text", title: "Port Number", description: "Kodi Web Server Port Number", defaultValue:80)
}

def installed(){
	//refresh()
    log.debug "installed"
}
def updated(){
	log.debug "updated"
    initialize()
    
}

def initialize(){
	log.debug "overriding the IP Address based on input preferences ${overrideURL}"
    setURL(overrideURL)
}

// parse events into attributes
def parse(String description) {
	//log.debug "Parsing '${description}'"
    def todo = []
    def map = stringToMap(description)
    if(map.headers && map.body){
    	log.trace "Response Received (with Headers and Body)"
    	
        def bodyString = new String(map.body.decodeBase64())
        def slurper = new JsonSlurper()
        def response = slurper.parseText(bodyString)
        
        log.debug response
        log.debug "Last Command: ${state.lastCommand}"
        
        //if we were requesting the current active players
        if(state.lastCommand == "Player.GetActivePlayers"){
        	state.lastCommand = null
        	//then parse the list of active players
            response?.result?.each {
            	//and if we got a player, let's get the actual status
                def playerid = it.playerid
                log.debug "Player ID: ${playerid}"
                sendEvent(name: "playerID", value: playerid)
                
                //for audio
                if(it.type && it.type == "audio"){
                	//{"jsonrpc": "2.0", "method": "Player.GetItem", "params": { "properties": ["title", "album", "artist", "duration", "thumbnail", "file", "fanart", "streamdetails"], "playerid": 0 }, "id": "AudioGetItem"}
                    log.trace "Audio Player Active"
                    todo << { getApplicationProperties() } //replace with call to get audio status
                }
                //for video
                if(it.type && it.type == "video"){
                	log.trace "Video Player Active"
                    todo <<  { getVideoPlayerStatus(playerid) } //Player.GetItem
                }
            }
            
            if(!response?.result){
                log.trace "NO PLAYERS Active"
                //clear out the track data if no players are active
                sendEvent(name: "trackDescription", value: "")
                sendEvent(name: "trackData", value: "")
                sendEvent(name: "status", value: "Inactive")
                //if we don't have anything playing, let's get the application properties 
                // (since it's normally triggered after parsing the current player data)
                todo << { getApplicationProperties() }
            }
        }
        
        //respond to getting the details for the current audio/video player
        if(state.lastCommand == "Player.GetItem"){
        	//item { id, title, thumbnail, label, streamdetails, type(movie|tvshow), tvshowid, episode, season, showtitle }
        	state.lastCommand = null
        	log.trace "Now Playing: ${response?.result?.item?.label}"
            sendEvent(name: "trackDescription", value: response?.result?.item?.label)
            sendEvent(name: "trackData", value: response?.result?.item)
            
            //when we are done parsing the Player.GetItem for video, let's get the other application properties
            todo << { getApplicationProperties() }
        }
        //if(response.result) log.debug "Result: ${response.result}"
        
        //handle the response for GUI.GetProperties
        if(state.lastCommand == "GUI.GetProperties"){
        	state.lastCommand = null
            //returns currentwindow { label, id } in response.result
        	log.trace "Received GUI Properties"
            log.trace "Current Window: ${response?.result?.currentwindow?.label}"
            sendEvent(name: "currentActivity", value: response?.result?.currentwindow?.label);
            sendEvent(name: "currentWindowID", value: response?.result?.currentwindow?.id);
        }
        
        //handle the play/pause command
        if(state.lastCommand == "Player.PlayPause"){
        	state.lastCommand = null
        	log.trace "speed: ${response?.result?.speed}"
            def status = response?.result?.speed > 0 ? "Playing" : "Paused"
            log.trace "Player Status is: ${status}"
            sendEvent(name: "status", value: status)
        }
        
        //handle the stop command
        if(state.lastCommand == "Player.Stop"){
        	state.lastCommand = null
            if(response?.result == "OK"){
            	log.trace "Player Status is: Stopped"
            	sendEvent(name: "status", value: "Stopped")	
            }
        }    
        
        //Application.SetMute
        if(state.lastCommand == "Application.SetMute"){
        	state.lastCommand = null
            def muteStatus = "unmuted"
            if(response?.result){ muteStatus = "muted"}else{ muteStatus = "unmuted"}
            log.trace "Mute Status is: ${muteStatus}"
            sendEvent(name: "mute", value: muteStatus)
        }
        //Application.SetVolume
        if(state.lastCommand == "Application.SetVolume"){
        	state.lastCommand = null
            def level = response?.result
            log.trace "Volume Level is: ${level}"
            sendEvent(name: "level", value: level)
        }
        
        
        //TODO: Also GET the VOLUME and MUTE status
        if(state.lastCommand == "Application.GetProperties"){
        	//volume
            sendEvent(name: "level", value: response?.result?.volume)
            //muted
            def muteStatus = "unmuted"
            if( response?.result?.muted){ muteStatus = "muted"}else{ muteStatus = "unmuted"}
            sendEvent(name: "mute", value: muteStatus)
            //name
            sendEvent(name: "kodiName", value: response?.result?.name)
            //version
            sendEvent(name: "kodiVersion", value: response?.result?.version?.revision)
            
            //when we are done getting the volume level, let's refresh the current activity (current window)
            todo << { getCurrentActivity() }
        }
        
        //TODO: Get the Player Properties
        //Player.GetProperties params [ properties [
        /*
        [ boolean canrotate = False ]
        [ boolean canrepeat = False ]
        [ integer speed = 0 ]
        [ boolean canshuffle = False ]
        [ boolean shuffled = False ]
        [ boolean canmove = False ]
        [ boolean subtitleenabled = False ]
        [ Player.Position.Percentage percentage = 0 ]
        [ Player.Type type = "video" ]
        [ Player.Repeat repeat = "off" ]
        [ boolean canseek = False ]
        [ Player.Subtitle currentsubtitle ]
        [ Player.Subtitle[] subtitles ]
        [ Global.Time totaltime ]
        [ boolean canzoom = False ]
        [ Player.Audio.Stream.Extended currentaudiostream ]
        [ Playlist.Id playlistid = -1 ]
        [ Player.Audio.Stream.Extended[] audiostreams ]
        [ boolean partymode = False ]
        [ Global.Time time ]
        [ Playlist.Position position = -1 ]
        [ boolean canchangespeed = False ]
        */
        
        
    }
    
    log.debug "running todos"
    def toReturn = []
    todo.each{ toReturn << it.call() }
    return toReturn
	// TODO: handle 'activities' attribute
    
	// TODO: handle 'status' attribute
	// TODO: handle 'level' attribute

	// TODO: handle 'mute' attribute

}

def setURL(url){
	state.destURL = url
    sendEvent(name: "destURL", value: url, descriptionText: "URL set to ${url}")
    splitURL(url)
}

def getURL(){
	state.destURL
}

// handle commands
def startActivity(activity) {
	log.trace "Executing 'startActivity'"
	
    def window = activity
    def subsection = null
    if(activity.contains(".")){
        def values = activity.tokenize(".")
        window = values[0]
        subsection = values[1]
    }
    
    log.trace "Navigating to ${window} ΓåÆ ${subsection}"
    
    //{"jsonrpc":"2.0","method":"GUI.ActivateWindow","params":{"window":"videos", "parameters": "movietitles"},"id":"1"}}
    def command = "GUI.ActivateWindow"
    state.lastCommand = command
    def params = [ "window": window ]
    if(subsection){ params.put("parameters", [subsection] ) }
    sendCommand(command, params)
}

def getAllActivities() {
	log.debug "Executing 'getAllActivities'"
    def activities = [
        "videos", "videos.movietitles", "videos.recentlyaddedmovies", 
        "videos.tvshows", "Videos.recentlyaddedepisodes",
        "music", "music.genres", "music.artists", "music.albums", "music.top100",
        "programs.addons"
        ]
    sendEvent(name: "activities", value: activities)
}

def getCurrentActivity() {
	log.debug "Executing 'getCurrentActivity'"
    def command = "GUI.GetProperties"
    state.lastCommand = command
    def params = ["properties": [ "currentwindow" ] ]
    sendCommand(command, params)
}

def play() {
	log.debug "Executing 'play'"
	playPause()
}

def pause() {
	log.debug "Executing 'pause'"
	playPause()
}

def playPause(){ sendPlayerCommand("Player.PlayPause") }

def stop() { sendPlayerCommand("Player.Stop") }

def nextTrack() {
	log.debug "Executing 'nextTrack'"
    sendPlayerCommand("Player.GoTo", ["to": "next"])
}

def previousTrack() {
	log.debug "Executing 'previousTrack'"
    sendPlayerCommand("Player.GoTo", ["to": "previous"])
}

def playTrack() {
	log.debug "Executing 'playTrack'"
	// TODO: handle 'playTrack' command
}

def setLevel(level) { sendCommand("Application.SetVolume", ["volume": level as int]) }

def playText() {
	log.debug "Executing 'playText'"
	// TODO: handle 'playText' command
}

def mute() {
	log.debug "Executing 'mute'"
    sendCommand("Application.SetMute", ["mute": true ])
}

def unmute() {
	log.debug "Executing 'unmute'"
    sendCommand("Application.SetMute", ["mute": false ])
}

def toggleMute(){
	log.debug "Executing 'unmute'"
    sendCommand("Application.SetMute", ["mute": "toggle" ])
}

def setTrack() {
	log.debug "Executing 'setTrack'"
	// TODO: handle 'setTrack' command
}

def resumeTrack() {
	log.debug "Executing 'resumeTrack'"
	// TODO: handle 'resumeTrack' command
}

def restoreTrack() {
	log.debug "Executing 'restoreTrack'"
	// TODO: handle 'restoreTrack' command
}

def poll() {
	log.debug "Executing 'poll'"
	refresh()
}

//--------------- INPUT.CONTROL ---------------
def inputBack(){ sendCommand("Input.Back") }
def inputHome(){ sendCommand("Input.Home") }
def inputUp(){ sendCommand("Input.Up") }
def inputDown(){ sendCommand("Input.Down") }
def inputLeft(){ sendCommand("Input.Left") }
def inputRight(){ sendCommand("Input.Right") }
def inputContextMenu(){ sendCommand("Input.ContextMenu") }
def inputInfo(){ sendCommand("Input.Info") }
def inputSelect(){ sendCommand("Input.Select") }
//def inputUp(){ sendCommand("Input.SendText") } //requires STRING (text) BOOLEAN (is whole input TRUE=closedialog)
def inputShowCodec(){ sendCommand("Input.ShowCodec") }
def inputShowOSD(){ sendCommand("Input.ShowOSD") }


//------------- Player Status ------
def getActivePlayers(){ sendCommand("Player.GetActivePlayers") }
def getVideoPlayerStatus(playerID){
	playerID = playerID ?: 1
	log.debug "Getting status for player id: ${playerID}"
	def params = [ "properties": [ "title", "season", "episode", "duration", "showtitle", "tvshowid", "thumbnail", "streamdetails" ],
                  "playerid": playerID ?: 1
                 ]
    sendCommand("Player.GetItem", params, "VideoGetItem")
}

def getApplicationProperties(){
	//{"jsonrpc": "2.0", "method": "Application.GetProperties", "params": {"properties": ["volume"]}, "id": 1}
    sendCommand("Application.GetProperties", ["properties": [ "volume", "muted", "name", "version" ] ])
}



def refresh() {
	log.debug "Executing 'refresh'"
    log.debug "Getting status from ${state.host}:${state.port}"
    
    getActivePlayers()
}





// -------------------- HTTP Command Helpers -----------------------------
def sendPlayerCommand(command, parameters=null, id=null){
	def playerid = device.currentValue("playerID")
    def params = parameters ?: [:]
    params.put("playerid", playerid ?: 1)
    sendCommand(command, params, id)
}
def sendCommand(command, parameters=null, id=null){
	state.lastCommand = command
	def content = [
    	"jsonrpc":"2.0",
        "method":"$command",
        "id": 1
    ]
    //if the parameters and id were passed in, add them to the JSON command
    if(parameters) content.put("params", parameters)
    if(id) content.put("id", id)
    
	def json = new groovy.json.JsonBuilder()
    def payload = json.call(content)
    log.trace "Sending command: ${command}"
    //def pretty = json.toPrettyString()
	
    //don't set the DNI here, we'll use the MAC from the Parent SmartApp
    //device.deviceNetworkId = getHostHexAddress()
    
    def path = "/jsonrpc"
    
    def headers = [:] 
    headers.put("HOST", getHostAddress())
    headers.put("Content-Type", "application/json")

    def method = "POST"
    
    def result = new physicalgraph.device.HubAction(
        method: method,
        path: path,
        body: payload,
        headers: headers
	)
    
    result
}

def basicGet() {
	//don't set the DNI here, we'll use the MAC from the Parent SmartApp
    //device.deviceNetworkId = getHostHexAddress()
    
    def path = "/jsonrpc"
    
    def headers = [:] 
    headers.put("GET", getHostAddress())
    //headers.put("Content-Type", "application/x-www-form-urlencoded")

    def method = "GET"
    
    def result = new physicalgraph.device.HubAction(
        method: method,
        path: path,
        headers: headers
	)
    
    result
}



//--------------------------- Helper Methods -----------------------------------
def splitURL(url){
	log.debug "splitting atoms"
    url = url ?: state.destURL
	if(url.contains("http://")){
    	url = url.replaceAll("http://", "")
        //TODO: only pull off the last character if it's a /
        url = url.replaceAll("/", "")
        
        //uri = new URI(url)
        //host = uri?.getHost()    
    }
    
    if(url.contains(":")){
    	log.debug "Splitting out the host and port"
        def values = url.tokenize(":")
        state.host = values[0]
        state.port = values[1]
    }
    else{
    	log.debug "Host ONLY provided. Defaulting port to 80"
        state.host = url
        state.port = "80"
    }

    log.debug "Host: ${state.host} Port: ${state.port}"
}


//----------------- IP and Port Hex Conversion Utilities -----------------------
def getHostHexAddress(){
	def hosthex = convertIPtoHex(state.host)
    def porthex = convertPortToHex(state.port)
    return "$hosthex:$porthex"
}

def getHostAddress(){
	return state.host + ":" + state.port
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02X', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04X', port.toInteger() )
    return hexport
}