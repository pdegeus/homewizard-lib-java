homewizard-lib-java
===================

An reversed engineered open-source library for the Homewizard ( http://www.homewizard.nl ).
Uses Maven for dependency management.

Use the code from the package nl.rgonline.homewizardlibgui to see how you should use this library. The most import calls are:

**Init HWSystem**

	HWSystem hwsystem = new HWSystem(ipadres, password);
	hwsystem.init();
		
**Get a list of switches**

	List<HWSwitch> switches = hwsystem.getSwitches();

**Use switches**

Now you can find a switch with a certain name or number and turn it on or off, or ask for the status.
