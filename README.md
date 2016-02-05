# Kodi for SmartThings
Control and react to event changes from Kodi (formerly XBMC), your favorite media center software.

## Installation
There are two components needed for the Kodi integration to work. 

1. SmartApp - used for discovery of the Kodi instances on your network and creation of the Kodi devices in SmartThings
2. Device Type - the actual code used to control and react to changes from your Kodi devices
 
You must publish both the SmartApp and Device Type in the SmartThings IDE for everything to work properly.

### GitHub Integration
The best way to get updates for Kodi for SmartThings is to enable GitHub integration and add my repository:

1. Open the IDE and navigate to `My SmartApps` or `My Device Handlers`
2. Select `Settings`
3. Select Add new repository`
4. Enter the following details:
   Owner: `boshdirect`
   Name: `ST-Kodi`
   Branch: `master`

Now whenever you need to update to the latest version, you can choose `↓ Update from Repo`, select `ST-Kodi` and get your updates.

## How to Use
*Install the SmartApp* (one time)
Marketplace → Select the SmartApps tab → My Apps

*Find and Create Kodi Devices*
Open the SmartApp, wait for the discovery to complete, select your Kodi instances, and tap Done

