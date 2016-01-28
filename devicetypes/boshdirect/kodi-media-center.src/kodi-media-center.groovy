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
        
        command "splitURL", [ "string" ]
        
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

	tiles {
		// TODO: define your main and details tiles here
        valueTile("destURL", "device.destURL", decoration: "flat", width: 3){ 
        	state "destURL", label: '${currentValue}'
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat") {
            state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        standardTile("urlsplitter", "device.destURL", inactiveLabel: false, decoration: "flat") {
            state "default", action:"splitURL", icon:"st.Office.office12", label: "Parse URL"
        }
        standardTile("videoStatus", "device.status", inactiveLabel: false, decoration: "flat") {
            state "default", action:"getVideoStatus", label: "Get Video Status"
        }
        
        //inputs
        valueTile("input.up", "device.input", inactiveLabel: false, decoration: "flat") {
            state "default", action:"inputUp", label: "Γåæ"
        }
        valueTile("input.down", "device.input", inactiveLabel: false, decoration: "flat") {
            state "default", action:"inputDown", label: "Γåô"
        }
        valueTile("input.left", "device.input", inactiveLabel: false, decoration: "flat") {
            state "default", action:"inputLeft", label: "ΓåÉ"
        }
        valueTile("input.right", "device.input", inactiveLabel: false, decoration: "flat") {
            state "default", action:"inputRight", label: "ΓåÆ"
        }
        valueTile("input.select", "device.input", inactiveLabel: false, decoration: "flat") {
            state "default", action:"inputSelect", label: "Select"
        }
        valueTile("input.info", "device.input", inactiveLabel: false, decoration: "flat") {
            state "default", action:"inputInfo", label: "  Info  "
        }
        valueTile("input.back", "device.input", inactiveLabel: false, decoration: "flat") {
            state "default", action:"inputBack", label: "  Back  "
        }
        standardTile("input.home", "device.input", inactiveLabel: false, decoration: "flat") {
            state "default", action:"inputHome", label: "Home", icon: "st.Home.home2"
        }
        
        //playback controls
        valueTile("input.playpause", "device.input", inactiveLabel: false, decoration: "flat") {
            state "default", action:"pause", label: "Γû╢/Γ¥ÜΓ¥Ü"
        }
        valueTile("input.next", "device.input", inactiveLabel: false, decoration: "flat") {
            state "default", action:"nextTrack", label: "Γû╢Γû╢"
        }
        valueTile("input.previous", "device.input", inactiveLabel: false, decoration: "flat") {
            state "default", action:"previousTrack", label: "ΓùÇΓùÇ"
        }
        
        standardTile("mainOverview", "device.status", inactiveLabel: false, decoration: "flat") {
            state "default", action:"refresh", icon: "st.Electronics.electronics18"
        }
        
        main(["mainOverview"])
        details([
        	"destURL", 
        	"input.home", "input.up", "input.info",
            "input.left", "input.select", "input.right",
            "input.back", "input.down", "refresh",
            "input.previous", "input.playpause", "input.next",
            "urlsplitter", "videoStatus"
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
                
                //for audio
                if(it.type && it.type == "audio"){
                	//{"jsonrpc": "2.0", "method": "Player.GetItem", "params": { "properties": ["title", "album", "artist", "duration", "thumbnail", "file", "fanart", "streamdetails"], "playerid": 0 }, "id": "AudioGetItem"}
                    log.trace "Audio Player Active"
                }
                //for video
                if(it.type && it.type == "video"){
                	log.trace "Video Player Active"
                    todo <<  { getVideoPlayerStatus(playerid) }
                }

				//if we didn't get a parseable result, let's clear out the last command tracked
                if(!it.type){ 
                	log.trace "NO PLAYERS Active"
                }
                sendResult(name: "playerID", value: playerid)
            }
        }
        
        //respond to getting the details for the current audio/video player
        if(state.lastCommand == "Player.GetItem"){
        	//item { id, title, thumbnail, label, streamdetails, type(movie|tvshow), tvshowid, episode, season, showtitle }
        	state.lastCommand = null
        	log.trace "Now Playing: ${response?.result?.item?.label}"
            sendEvent(name: "trackDescription", value: response?.result?.item?.label)
            sendEvent(name: "trackData", value: response?.result?.item)
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
            def muteStatus = "unmute"
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
	def params = [ "properties": [ "title", "season", "episode", "duration", "showtitle", "tvshowid", "thumbnail", "streamdetails" ],
                  "playerid": playerID ?: 1
                 ]
    sendCommand("Player.GetItem", params, "VideoGetItem")
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