# Kodi Device Capabilities
The Kodi for SmartThings device type implements several base capabilities from SmartThings and implements most of the 
attributes and commands for these capabiltiies. Additionally, there are several commands and attributes which were custom
developed for this device type which may be useful in other SmartApps or integrations. 

For example, using [SharpTools](http://sharptools.boshdirect.com) 
on Android, you might setup widgets to quickly send actions to Kodi or setup Tasker events to trigger custom events on your 
Kodi setup. Additionally, these custom commands are useful in the advanced version of Rule Machine which allows you to send 
custom commands.

## Capabilities
The following capabilities are implemented in this device type:

* `Media Controller`
* `Music Player`
* `Switch`
* `Polling`
* `Refresh`

## Attributes

* `trackDescription` - the currently playing movie/TV show name
* `trackData` - the current track data in JSON format (especially useful in [Tasker](http://sharptools.boshdirect.com/features))
* `trackType` - the class of track currently playing (eg. `object.item.videoItem.videoBroadcast`, `object.item.videoItem.movie`, etc) 
* `level` - the current volume level (eg. 100)
* `mute` - the current mute status (eg. `muted`, `unmuted`)
* `status` - current playback status of the active player `Playing`, `Paused`, `Stopped`
* `switch` - maps from the `status` attribute (eg. on = "Playing"; off = "Paused" or "Stopped")
* `destURL` - the destination URL used to control your Kodi endpoint
* `currentWindowID` - the currently active Window ID (useful for determining the current activity)
* `currentActivity` - the currently active Activity (eg. `Video`, `Fullscreen Video`
* `activities` - the available activities 
  
  [`videos`, `videos.movietitles`, `videos.recentlyaddedmovies`, 
        `videos.tvshows`, `Videos.recentlyaddedepisodes`,
        `music`, `music.genres`, `music.artists`, `music.albums`, `music.top100`,
        `programs.addons` ]
* `playerID` - the currently active Kodi player ID
* `kodiVersion` - the version number for the Kodi instance (eg. `20150721-2f34a0c`)
* `kodiName` - the name of the Kodi instance (eg. `Kodi`)

## Commands

#### Volume

* `toggleMute()` - toggles between muted and unmuted (especially useful in [Android widgets](http://sharptools.boshdirect.com/features))
* `mute()`
* `unmute()`
* `setLevel(number)` - sets the volume level (`0-100`)

#### Navigational (Input) Controls

* `startActivity(string)` - starts the selected activity (choose from the availabile `activities`)
* `inputUp()` - send the UP (↑) button press
* `inputDown()` - send the DOWN (↓) button press
* `inputLeft()` - send the LEFT (←) button press
* `inputRight()` - send the RIGHT (→) button press
* `inputInfo()` - send the INFO button press (eg. for viewing details of movies/music)
* `inputBack()` - send the BACK button press
* `inputSelect()` - send the SELECT/OK button press
* `inputHome()` - send the HOME button press
* `inputContextMenu()` send the CONTEXT MENU button press
* `inputShowCodec()` send the SHOW CODEC button press
* `inputShowOSD()` send the SHOW ON SCREEN DISPLAY button press
* `play()` - send the play/pause command
* `pause()` - send the play/pause command
* `stop()` - send the stop command
* `on()` - maps to `play()`
* `off()` - maps to `pause()`
* `nextTrack()` - send the next track command
* `previousTrack()` - send the previous track command

#### Player Status

* `getActivePlayers()` - get the list of currently active players
* `getVideoPlayerStatus(number)` - gets the status of a specific player
* `getCurrentActivity()` - get the current activity; sets `currentActivity` and `currentWindowID`
* `poll` - sends the `refresh()` command
* `refresh()` - gets the updated media playback status and forces a refresh of event subscriptions
 

#### Play File/Playlist

* `playFile(filename)` - starts playing a file (can be a local or HTTP streamed file!)
* `playPlaylist(playlistid)` - starts playing the specified playlist (by ID) `(0: Music, 1: Video, 2: Pictures)`
* `clearPlaylist(playlistid)` - clears the specified playlist (by ID)
* `addToPlaylist(playlistid, filename)` - adds a file to the specified playlist `(0: Music, 1: Video, 2: Pictures)`

#### Music Player Capabilities
(those not covered by above sections)

* `playTrack(filename)` - maps to `playFile(filename)`
* `playText()` - NOT IMPLEMENTED
* `setTrack(filename)` - maps to `addToPlaylist(1, filename)`
* `resumeTrack()` - NOT IMPLEMENTED
* `restoreTrack()` - NOT IMPLEMENTED

#### Special Kodi Methods

* `showNotification(title, message, [image])` - sends a notification with the desired `title` and `message`  
  **Note**: The optional `image` can either be a value of `info`, `warning`, `error`, or a path to an image that Kodi can access per the standard Kodi ShowNotification implementation.
* `executeAddon(addonid)` - executes the desired addon based on the addon id (eg. `script.globalsearch`)
