<img height="64px" src="https://github.com/benjwarner/stacked/blob/master/src/main/resources/webapp/stacked-off-white.png"/>

StackedOff is a stack exchange site indexer and search engine.  It's
intended use is for people who wish to access a Stack Exchange Network site, 
e.g. stackoverflow.com, but do not have a reliable internet service.  
StackedOff uses the 'stack dump' data files made public by the Stack Exchange Network.

#Installation
* Download the latest zip version from <a href="https://github.com/benjwarner/stacked">here</a>, and unzip into your desired location.
* Ensure you have a version of a Java JRE installed which is <a href="https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html">version 8</a> or higher.
* Ensure your JAVA_HOME environment variable is pointing to this java home directory.

#Aquiring StackExchange Data Dumps
Get the 'BitTorrent Infohash' from <a href="https://meta.stackexchange.com/questions/224873/all-stack-exchange-data-dumps">here</a>.
Use your preferred BitTorrent (e.g. uTorrent) client to download a, or part of a Data Dump.
Note: Most BitTorrent clients allow you to pick and choose which files _within_ a Torrent that you
wish to download.  You will probably want to limit the files that you download, as some of them can be 
quite large.

**Important**: You must include in your download the Sites.xml file that is present in every data dump.

**Important**: Once downloaded, do NOT unzip the 7z site files.  Stacked Off can only read from the archived site files.

#Running Stacked Off
* Call <unzipped-location>/bin/stacked (if running on Linux/MacOS)
* Call <unzipped-location>/bin/stacked.bat (if running on Windows)

Launch a browser pointing at http://localhost and you should see the StackedOff GUI.

##Changing the port that Stacked Off uses
By default Stacked Off launches on port 80.
To change this, edit the file in your home directory .stackedoff/app.properties, and add a port setting, e.g.:

`port=8080`

Re-run Stacked Off.

##Acknowledgments
The guys at <a href="https://stackexchange.com/">stackexchange.com</a>.  Who not only revolutionized the 
technical Q&A space, but also, in the spirit of 'openness' admirably continue to allow free access to all of their
Q&A data for all of their sites.
