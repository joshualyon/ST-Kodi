/**
 *  Kodi Media Center
 *
 *  Copyright 2016 Josh Lyon
 *
 *	!!!IMPORTANT!!! Feel free to learn from this code, but please don't STEAL IT / COPY-PASTE parts of it
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
        capability "Switch"
		capability "Polling"
		capability "Refresh"
        
        attribute "destURL", "string"
        attribute "currentWindowID", "string" //the current window ID from Window.GetProperties 
        attribute "playerID", "number" //the current active player ID
        attribute "kodiVersion", "string" //kodi app version
        attribute "kodiName", "string" //kodi name
        
        //command "setupDevice", ["string", "string", "string", "number"]
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
        
        command "playFile", [ "string" ]
        command "playPlaylist", [ "number" ]
        command "clearPlaylist", [ "number" ]
        command "addToPlaylist", [ "number", "string" ]
        
        command "getActivePlayers"
        command "getVideoPlayerStatus", [ "number" ]
        command "getAudioPlayerStatus", [ "number" ]
        
        command "showNotification", ["string", "string" ]
        command "executeAddon", [ "string" ]
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
    	//Row 1 - Mult Attribute Tile
        multiAttributeTile(name:"kodiMulti", type:"generic", width:6, height:4) {
          tileAttribute("device.status", key: "PRIMARY_CONTROL") {
          	attributeState("default", label: '--', backgroundColor:"#79b821")
            attributeState("paused", label:'Paused', icon: "st.sonos.pause-btn", backgroundColor:"#ffffff")
            attributeState("playing", label:'Playing', icon: "st.sonos.play-btn", backgroundColor:"#79b821")
            attributeState("stopped", label:'Stopped', icon: "st.sonos.stop-btn", backgroundColor:"#ffffff")
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
        standardTile("input.home", "device.status", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputHome", label: "Home", icon: "st.Home.home2"
        }
        valueTile("input.up", "device.status", decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputUp", label: '↑'
        }
        valueTile("input.info", "device.status", decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputInfo", label: "  INFO  "
        }
        //Row 4
        valueTile("input.left", "device.status", decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputLeft", label: "←"
        }
        valueTile("input.select", "device.status",  decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputSelect", label: "SELECT"
        }
        valueTile("input.right", "device.status",  decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputRight", label: "→"
        }
        //Row 5
        valueTile("input.back", "device.status", decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputBack", label: "  BACK  "
        }
        valueTile("input.down", "device.status", decoration: "flat", height: 2, width: 2) {
            state "default", action:"inputDown", label: "↓"
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
            state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        
        
        //----------------playback controls-----------------------
        //Row 1 (6)
        standardTile("input.previous", "device.status", decoration: "flat", height: 2, width: 2) {
            state "default", action:"previousTrack", icon:"st.sonos.previous-btn" //, label: "◀◀"
        }
        standardTile("input.playpause", "device.status", decoration: "flat", height: 2, width: 2) {
            state "paused", action:"play",icon: "st.sonos.play-btn", nextState:"playing"//, label: "Play" 
            state "playing", action:"pause",icon: "st.sonos.pause-btn", nextState:"paused"//, label: "Pause" //the second character is pause ❚❚ ▶/⏸
            
        }
        standardTile("input.next", "device.status", decoration: "flat", height: 2, width: 2) {
            state "default", action:"nextTrack", icon:"st.sonos.next-btn" //, label: "▶▶"
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
        standardTile("mainOverview", "device.status", height: 1, width: 1, canChangeIcon: true) {
            state "default", label: '${currentValue}', action:"playPause", backgroundColor: "#ffffff", icon: "st.Electronics.electronics18"
            state "paused", label: 'Paused', action:"play", nextState:"playing", backgroundColor: "#ffffff", icon: "st.Electronics.electronics18"
            state "playing", label: 'Playing', action:"pause", nextState:"paused", backgroundColor: "#79b821", icon: "st.Electronics.electronics18"
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
	section("Authorization (optional)"){
    	input("username", "text", title: "Username", description: "eg. kodi", autoCorrect: false, capitalization: "none")
        input("password", "text", title: "Password", description: "eg. kodi", autoCorrect: false, capitalization: "none")
    }
	section("IMPORTANT: Only use the override if instructed to do so!"){
    	paragraph "Your Kodi devices should be automatically discovered using the Kodi SmartApp. Overriding the URL here may cause unexpected results. ONLY USE THE OVERRIDE IF YOU KNOW WHAT YOU ARE DOING"
		input("overrideURL", "text", title: "Override URL [ADVANCED]", description: "Full URL to Kodi Webserver, including port", autoCorrect: false, capitalization: "none")
    }
}


//---------------- Setup Methods ----------------
def installed(){
    log.debug "installed"
    sendEvent(name: "mute", value: "unmuted")
    sendEvent(name: "level", value: 100)
    //We will manually call setupDevice from the Service Manager SmartApp
}
def updated(){
	log.debug "updated"
    initialize()
    //If the user updated their preferences, we need to reinitialize things with the new settings
}

def initialize(){
	log.debug "overriding the IP Address based on input preferences ${overrideURL}"
    //if the override URL is set, let's use it
    if(overrideURL) setURL(overrideURL)
    
    //immediately check the subscriptions
    runIn(5, CheckEventSubscription) //wait a few seconds since the updated seems to get hit twice??
}

/**
 * Called from the Service Manager SmartApp to initialize the URL to control the Kodi instance
 * and the device information needed for UPnP subscription and eventing end points 
 **/
def setupDevice(url, udn, udnAddress, udnPort){
    state.udn = udn
    log.debug "Received: $udnAddress : $udnPort"
    state.udnAddress = udnAddress
    state.udnPort = udnPort
    log.trace "Setup device with address ${udnAddress}:${udnPort} and UDN: ${udn}"
    
    setURL(url)
    
    //get the initial full set of data from the Kodi instance
    refresh() 
    
    //check the subscriptions immediately
    CheckEventSubscription() 
}

def setURL(url){
	state.destURL = url
    sendEvent(name: "destURL", value: url, descriptionText: "URL set to ${url}")
    splitURL(url)
}

def getURL(){
	state.destURL
}

//-------------- parse events into attributes ----------------
def parse(String description) {
	//log.debug "Parsing '${description}'"
    def todo = []
    def map = stringToMap(description)
    def msg = parseLanMessage(description)
    if(msg.headers){
    //if(map.headers && map.body){
    	log.trace "Response Received (with Headers and Body)"
        
        log.debug "HEADER: ${msg.headers}"
        
        //Check for authorization issues
        if(msg.header.toLowerCase().contains("unauthorized")){
            def authMessage = "UNAUTHORIZED: Edit this device to set your Kodi Username/Password"
            log.debug authMessage
            sendEvent(name: "trackDescription", value: authMessage, descriptionText: authMessage)
            //TODO: can we redirect to the authorization screen automatically?
        }
        
        
        def server = msg?.headers?.server
        //sid:xxxx-xxxxx <-- Subscriber ID?
        //TIMEOUT
        
        //[nts:upnp:propchange, nt:upnp:event, content-length:1243, 
        //sid:uuid:2ac7714b-1bd6-df84-512e-ff9b4ce73bb2, host:192.168.1.118:39500, 
        // seq:0, user-agent:Neptune/1.1.3, content-type:text/xml; charset="utf-8", 
        // notify /notify http/1.1:null] 
        if(msg?.headers?.sid && msg?.headers?.timeout){
        	log.debug "Current Event Subscriptions: ${state.transportSID}"
            
        	//if the SID map is not created, let's create it
        	if(!state.transportSID) state.transportSID = [:]
            
        	//capture the SID
            def sid = msg?.headers?.sid.replaceAll("uuid:", "")
            log.debug "Event SID: $sid"
            
            //capture the timeout
            def timeout =  msg?.headers?.timeout.replaceAll("Second-", "")
            log.debug "Event Subscription Timeout: $timeout"
            
            def expires = now() as long
            log.debug "The time now is $expires"
            expires += (timeout.toLong() * 1000) //multiple the expiration in seconds * 1000 to get millis
            log.debug "The subscription will expire at $expires"
            //update the item in the state map
            state.transportSID << ["$sid": expires]
        }
        
        if(msg?.headers?.nt && msg?.headers?.nt.toLowerCase().contains("upnp:event")){ //server?.contains("UPnP") && server.contains("DLNA")
        	//UPnP Event Subscription Response
            log.trace "UPnP Response"
            //log.debug "Body: ${msg.body}"
            //log.debug "XML: ${msg.xml}"
            
            //<e:propertyset xmlns:e="urn:schemas-upnp-org:event-1-0"><e:property><LastChange>&lt;Event xmlns="urn:schemas-upnp-org:metadata-1-0/AVT/"&gt;&lt;InstanceID val="0"&gt;&lt;TransportState val="PAUSED_PLAYBACK"/&gt;&lt;/InstanceID&gt;&lt;/Event&gt;</LastChange></e:property></e:propertyset>
            //as long as we have the LastChange item, let's take its HTML encoded contents and parse them as XML
            def transportState = ""
            if(!msg.xml?.property?.LastChange.isEmpty()){
            	//<Event xmlns="urn:schemas-upnp-org:metadata-1-0/AVT/"><InstanceID val="0"><TransportState val="PAUSED_PLAYBACK"/></InstanceID></Event>
                //Parse the inner content of the last change (which was HTML encoded)
                def event = new XmlSlurper().parseText(msg.xml?.property?.LastChange.toString())
                //And if we got the TransportState, let's update the event status
                if(!event.InstanceID.TransportState.isEmpty()){
                    transportState = event.InstanceID.TransportState.@val
                    def transportStates = [PAUSED_PLAYBACK: "paused", PLAYING: "playing", STOPPED: "stopped"]
                    def status = transportStates."$transportState"
                    log.debug "Current state is: ${status}"
                    setStatus(status)
                    
                    if(transportState == "STOPPED")
                    	clearTrack()
                }
                
                //Check for first playback as a large amount of data gets sent over: CurrentTrackMetaData
                if(!event.InstanceID.CurrentTrackMetaData.isEmpty() && transportState != "STOPPED"){ //need to check if it's stopped as on the first subscription, KODI will send over the last track data
                	log.debug "WE HAVE METADATA"
                    //Parse the inner content of the CurrentTrackMetadata (which was HTML encoded)
                    def metadata = new XmlSlurper().parseText(event.InstanceID.CurrentTrackMetaData.@val.toString())
                    log.debug "METADATA: $metadata"
                    //And if we got the TransportState, let's update the event status
                    if(!metadata.item?.title.isEmpty()){
                        def title = metadata.item.title
                        log.debug "Current playing title is: ${title}"
                        sendEvent(name:"trackDescription", value: title)
                    }
                    
                    
                    //see if we have the UPnP object class type
                    //def itemClass = metadata.'**'.find { it.name() == 'class' } //NOT ALLOWED
                    //def itemClass = metadata.depthFirst().findAll { it.name() == 'class' } //NOT ALLOWED
                    def itemClass = metadata.item.children().find{ it.name() == "class" }.toString()
                    if(!itemClass.isEmpty()){
                        log.debug "itemClass: $itemClass"
                        def playerID = 1 //default to video
                        if(itemClass.contains("video")) playerID = 1 //object.item.videoItem.movie
                        else if(itemClass.contains("audio")) playerID = 0 //object.item.audioItem.musicTrack
                        else if(itemClass.contains("imageItem")) playerID = 2 //object.item.imageItem.*
                        else getActivePlayers() //if it's not a known item, fallback to getting the info via JSON-RPC
                        sendEvent(name: "playerID", value: playerID)
                    }
                    else{
                    	log.debug "item.upnp:class is empty. Falling back to Player.GetActive"
                    	getActivePlayers()
                    }
                }
            }
        }
        //Response to KODI commands via JSON RPC
        else if(msg.body && msg.headers?.'content-type'?.contains("json")){ //don't try to respond to XML responses
            //def bodyString = new String(map.body.decodeBase64())

            //log.debug "BODY: $bodyString"
            //log.debug "BODY: $msg.data"

            //def slurper = new JsonSlurper()
            //def response = slurper.parseText(bodyString)
            def response = msg.json

            log.debug response
            log.debug "Last Command: ${state.lastCommand}"
            
            //If we were previously unauthorized and now controls are working, let's clear out the warning message
            //log.debug "STATUS: ${msg.status}"
            if(state.lastCommand 
            	&& device?.currentValue("trackDescription")?.contains("UNAUTHORIZED")
                && msg.status == 200){
                clearTrack()
            }

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
                        log.trace "Audio Player Active"
                        todo << { getAudioPlayerStatus(playerid) } //Player.GetItem
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
                    clearTrack()
                    //if we don't have anything playing, let's get the application properties 
                    // (since it's normally triggered after parsing the current player data)
                    todo << { getApplicationProperties() }
                }
            }

            //respond to getting the details for the current audio/video player
            if(state.lastCommand == "Player.GetItem"){
            	//Audio: item {"title", "album", "artist", "duration", "thumbnail", "file", "fanart", "streamdetails" }
                //Video: item { id, title, thumbnail, label, streamdetails, type(movie|tvshow), tvshowid, episode, season, showtitle }
                state.lastCommand = null
                def nowPlaying = response?.result?.item?.label ?: response?.result?.item?.title
                log.trace "Now Playing: ${nowPlaying}"
                sendEvent(name: "trackDescription", value: nowPlaying)
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
                def status = response?.result?.speed > 0 ? "playing" : "paused"
                log.trace "Player Status is: ${status}"
                setStatus(status)
            }

            //handle the stop command
            if(state.lastCommand == "Player.Stop"){
                state.lastCommand = null
                if(response?.result == "OK"){
                    log.trace "Player Status is: Stopped"
                    setStatus("stopped")
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
}

def clearTrack(){
	sendEvent(name: "trackDescription", value: "")
	sendEvent(name: "trackData", value: "")
    setStatus("stopped") //formerly Inactive
}

def setStatus(status){
	sendEvent(name: "status", value: status)
	//map playing/paused/stopped/inactive to on/off
    switch(status){
    	case "playing":
        	sendEvent(name: "switch", value: "on")
        	break;
        case "paused":
        case "stopped":
        case "inactive":
        default:
        	sendEvent(name: "switch", value: "off")
        	break;
    }
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
    
    log.trace "Navigating to ${window} → ${subsection}"
    
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


//-------------Player.Open()-------------------
//Play a single video from file
def playFile(fileName){
    //{"jsonrpc":"2.0","id":"1","method":"Player.Open","params":{"item":{"file":"Media/Big_Buck_Bunny_1080p.mov"}}}
    sendCommand("Player.Open", ["item": ["file": fileName]])
}
//Play a Playlist given the numeric playlist ID
def playPlaylist(playlistid){
    //{"jsonrpc":"2.0","id":1,"method":"Player.Open","params":{"item":{"playlistid":1},"options":{"repeat":"all"}}}
    sendCommand("Player.Open", ["item": [ "playlistid": playlistid ]]) //does not include the repeat option
}
//Clear a Playlist given the numeric playlist ID
def clearPlaylist(playlistid){
    //{"jsonrpc":"2.0","id":1,"method":"Playlist.Clear","params":{"playlistid":1}}
    sendCommand("Playlist.Clear", ["playlistid": playlistid])
}
//add a file to a playlist, given the file and the numeric playlist id
def addToPlaylist(playlistid, fileName){
	//{"jsonrpc":"2.0","id":1,"method":"Playlist.Add","params":{"playlistid":1,"item":{"file":"Media/Big_Buck_Bunny_1080p.mov"}}}
	sendCommand("Playlist.Add", ["playlistid": playlistid, "item": ["file": fileName]])
}

//------------Music Player - Mapped Commands-----------------
def playTrack(filename){ playFile(filename) }
def playText(textToSpeak){
	//TODO: implement TTS
}
//Default playlists: music (playlistid = 0), video (1) and pictures (2) 
def setTrack(filename){ addToPlaylist(1, filename) }
def resumeTrack(someMap){ 
	//TODO: implement resumeTrack ("Set and play the given track and maintain queue position")
}
def restoreTrack(someMap){ 
	//TODO: implement restoreTrack ("Restore the track with the given data")
}

//------------- Player Status ------
def getActivePlayers(){ sendCommand("Player.GetActivePlayers") }
def getVideoPlayerStatus(playerID){
	playerID = playerID ?: 1 //default to getting the video player info
	log.debug "Getting status for player id: ${playerID}"
	def params = [ "properties": [ "title", "season", "episode", "duration", "showtitle", "tvshowid", "thumbnail", "streamdetails" ],
                  "playerid": playerID ?: 1
                 ]
    sendCommand("Player.GetItem", params, "VideoGetItem")
}
def getAudioPlayerStatus(playerID){
	playerID = playerID ? playerID : 0 //default to getting the audio player info
	log.debug "Getting status for player id: ${playerID}"
	def params = [ "properties": ["title", "album", "artist", "duration", "thumbnail", "file", "fanart", "streamdetails"],
                  "playerid": playerID
                 ]
    sendCommand("Player.GetItem", params, "AudioGetItem")
}

def getApplicationProperties(){
	//{"jsonrpc": "2.0", "method": "Application.GetProperties", "params": {"properties": ["volume"]}, "id": 1}
    sendCommand("Application.GetProperties", ["properties": [ "volume", "muted", "name", "version" ] ])
}


//------------- Extra Super Fun Methods ---------------
def showNotification(title, message, image="info"){
	sendCommand("GUI.ShowNotification", [ "title": title, "message": message, "image": image ])
    //TODO: Add SmartThings image to notification
}

def executeAddon(addonid){
	sendCommand("Addons.ExecuteAddon", [ "wait": false, "addonid": addonid ])
}



def refresh() {
	log.debug "Executing 'refresh'"
    log.debug "Getting status from ${state.host}:${state.port}"
    
    //CheckEventSubscription()
    ResetEventSubscriptions()
    
    def hubActions = []
    hubActions << getActivePlayers()
    hubActions
}


//--------map Switch On/Off to usable commands
def on(){ play() }
def off(){ pause() }





// -------------------- HTTP Command Helpers -----------------------------
def sendPlayerCommand(command, parameters=null, id=null){
	def playerid = device.currentValue("playerID")
    playerid = (playerid != null ? playerid : 1) //don't use elvis operator here as 0 is a valid playerid
    def params = parameters ?: [:]
    params.put("playerid", playerid)
    log.debug "params: $params"
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
    
    if(username){
    	def pair ="$username:$password"
        def basicAuth = pair.bytes.encodeBase64();
    	headers.put("Authorization", "Basic " + basicAuth )
    }

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




//------------------------- UPnP Callbacks -----------------------------------
def CheckEventSubscription(){
	//check to see if our UPnP Event Subscriptions are still valid
    def todo = []
    def allTransport = state.transportSID ?: [:]
    
    //remove any of the expired or invalid subscriptions
    log.debug "Checking for expired subscriptions in $allTransport"
    def toRemove = allTransport.findAll { 
    	def expiration = it.value.toLong()
    	def isRemovable = !expiration || expiration < now() || expiration == 0 
        isRemovable
    }
    log.debug "Unsubscribing from and removing: $toRemove"
    toRemove.each { todo << unsubscribeAction("/AVTransport/${state.udn}/event.xml", it.key) }
    
    //TODO: We really only need to keep one of each subscription type live
    //Refresh any of the still valid subscriptions
    def toKeep = allTransport - toRemove
    state.transportSID = toKeep
    log.debug "Refreshing subscription for: $toKeep"
    toKeep.each { todo << renewSubscription("/AVTransport/${state.udn}/event.xml", it.key) }
    
    //if we don't have any valid subscriptions left, let's subscribe so we have at least one
    if(toKeep.size() == 0)
    	todo << subscribeAction("/AVTransport/${state.udn}/event.xml")

    //runIn(300, CheckEventSubscription) -- replaced with schedule(cron, event)
    def numActions = todo.size()
    log.debug "Returning $numActions HubActions"
    //sendHubCommand(todo) //force the send of the HubActions -- they don't seem to send from updated()
    todo.each{ sendHubCommand(it) }

}

def ResetEventSubscriptions(){
	log.trace "Clearing existing event subscriptions and getting a new subscription."
	//queue up all our actions
	def todo = []
	//unsubscribe everything
	state.transportSID.each { todo << unsubscribeAction("/AVTransport/${state.udn}/event.xml", it.key)}
    //then subscribe once
    todo << subscribeAction("/AVTransport/${state.udn}/event.xml")
    //send the queued up commands
    todo.each{ sendHubCommand(it) }
}

private subscribeAction(path, callbackPath="") {
    log.trace "subscribe($path, $callbackPath)"
    def address = getCallBackAddress()
    def ip = getUDNAddress() //varies from example code -- we are passing in the UDN during setup from the Service Manager

    def result = new physicalgraph.device.HubAction(
        method: "SUBSCRIBE",
        path: path,
        headers: [
            HOST: ip,
            CALLBACK: "<http://${address}/notify$callbackPath>",
            NT: "upnp:event",
            TIMEOUT: "Second-28800"
        ]
    )

    log.trace "SUBSCRIBE $ip to $path"

    return result
}
/*
SUBSCRIBE publisher path HTTP/1.1
 HOST: publisher host:publisher port
 USER-AGENT: OS/version UPnP/1.1 product/version
CALLBACK: <delivery URL>
NT: upnp:event
TIMEOUT: Second-requested subscription duration
*/

private renewSubscription(path, SID){
	log.trace "renewSubscription($path, $SID)"
    def address = getCallBackAddress()
    def ip = getUDNAddress() //We are passing in the UDN during setup from the Service Manager

    def result = new physicalgraph.device.HubAction(
        method: "SUBSCRIBE",
        path: path,
        headers: [
            HOST: ip,
            SID: "uuid:${SID}",
            TIMEOUT: "Second-28800" //8 hours isn't respected by Kodi
        ]
    )

    log.trace "RENEW SUBSCRIPTION $ip for $SID"

    return result
}
/*
SUBSCRIBE publisher path HTTP/1.1
 HOST: publisher host:publisher port
SID: uuid:subscription UUID
TIMEOUT: Second-requested subscription duration
*/

private unsubscribeAction(path, SID){
	log.trace "unsubscribeAction($path, $SID)"
    def address = getCallBackAddress()
    def ip = getUDNAddress() //We are passing in the UDN during setup from the Service Manager

    def result = new physicalgraph.device.HubAction(
        method: "UNSUBSCRIBE",
        path: path,
        headers: [
            HOST: ip,
            SID: "uuid:${SID}"
        ]
    )

    log.trace "UNSUBSCRIBE FROM $ip at $path"

    return result
}
//     UNSUBSCRIBE
/*
UNSUBSCRIBE publisher path HTTP/1.1
 HOST: publisher host:publisher port
SID: uuid:subscription UUID
*/


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
// gets the address of the hub
private getCallBackAddress() {
    return device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
}

def getHostHexAddress(){
	def hosthex = convertIPtoHex(state.host)
    def porthex = convertPortToHex(state.port)
    return "$hosthex:$porthex"
}

def getHostAddress(){
	return state.host + ":" + state.port
}

def getUDNAddress(){
	return state.udnAddress + ":" + state.udnPort
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02X', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04X', port.toInteger() )
    return hexport
}