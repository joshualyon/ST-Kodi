# Kodi for SmartThings
Control and react to event changes from Kodi (formerly XBMC), your favorite media center software.

Unlike other Kodi implementations seen on the SmartThings platform, this solution works completely within SmartThings and does not require any additional plugins or middleware tools. 

## SmartThings Installation
There are two components needed for the Kodi integration to work. 

1. SmartApp - used for discovery of the Kodi instances on your network and creation of the Kodi devices in SmartThings
2. Device Type - the actual code used to control and react to changes from your Kodi devices
 
You must publish both the SmartApp and Device Type in the SmartThings IDE for everything to work properly.

**Option 1: GitHub Integration**

The best way to get updates for Kodi for SmartThings is to enable GitHub integration and add my repository. If you don't know what GitHub is, see the [Manual Install](#user-content-manual-install) method below:

1. Open the IDE and navigate to `My SmartApps` or `My Device Handlers`
2. Select `Settings`
3. Select Add new repository`
4. Enter the following details:
   Owner: `boshdirect`
   Name: `ST-Kodi`
   Branch: `master`

Now whenever you need to update to the latest version, you can choose `↓ Update from Repo`, select `ST-Kodi` and get your updates.

**Option 2: Manual Install**

While using the GitHub integration is the recommended method for installing the Kodi SmartApp, you can also install the SmartApp and Device type manually.

Follow the instructions from the [ThingsThatAreSmart.wiki](http://thingsthataresmart.wiki/index.php?title=Using_Custom_Code#Using_a_Custom_SmartApp) on how to install a custom device type and SmartApp.

* [Kodi SmartApp Code](https://raw.githubusercontent.com/iamcanadian2222/ST-Kodi/master/devicetypes/boshdirect/kodi-media-center.src/kodi-media-center.groovy)
* [Kodi Device Type Code](https://raw.githubusercontent.com/iamcanadian2222/ST-Kodi/master/smartapps/boshdirect/kodi-formerly-xbmc.src/kodi-formerly-xbmc.groovy)

## How to Use
**Configure Kodi**

Enable the Web Server and UPnP in Kodi.

1. In Kodi, open `System` (Settings)
2. Navigate down to `Services`
3. From the `UPnP` tab, select `Allow remote control via UPnP`
4. From the `Web server` tab, select `Allow remote control via HTTP`

**Install the SmartApp**

From the SmartThings mobile app:

1. Select `Marketplace` from the bottom navigation
2. Select the `SmartApps` tab 
3. Scoll down and select the `My Apps` category
4. Select the `Kodi (formerly XBMC)` SmartApp
5. Wait for the discovery to complete, then select your Kodi instances and tap `Done`
6. Tap `Done` again to complete the setup and beging using your devices
 
**Note**: If you have a username and password set for your Kodi instance, open the device from the `My Home`→`Things` screen in SmartThings, selected `Edit Device` from the menu and ensure you have entered a username and password.

