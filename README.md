homewizard-lib-java
===================

A reversed engineered open-source library for the [HomeWizard](http://www.homewizard.nl) home automation system.
Uses the HomeWizard's JSON/REST API to read switches, sensors, weather data, etc.

Usage
-----

Initialize the main class, <code>HWSystem</code>:

    HWSystem hw = new HWSystem();
    // or
    HWSystem hw = new HWSystem("192.168.1.10", 80, "mypassword");
    
Use one of the managers to fetch information about your HomeWizard:

    hw.getSwitchManager().getAll(); // List of switches
    hw.getSwitchManager().get(1).isOn(); // Turn on switch #1
    hw.getSceneManager().get(2).getTimers(); // List of timers for scene #2

**Configuration**

Configuration of the HomeWizard connection can be passed to the constructor or read from a configuration file. Other configuration properties must be provided using a config file.
The default configuration file is 'homewizard.cfg'; the library looks for this file in the current working directory and the directory above it. If you want to use another location, set the runtime system property 'hwconfig' to point to the desired path.

Current status
--------------

Currently, this library supports reading data about:
* Switches (standard, dimmers and Philips Hue bulbs)
* Sensors
* Scenes (including codes, switches and timers)
* Cameras
* Thermometers (current value and graph data)
* Timers
 
Switches can be toggled, dimmed and changed color (the latter Hue only, obviously).
HomeWizard management, like adding switches, is not possible at this moment.
