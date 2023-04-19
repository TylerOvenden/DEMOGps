# DEMOGps

a project in Java dealing with data used for a GPS in the form of NMEA strings, specifically GGA & GSV information. 
documentation on NMEA strings
http://aprs.gids.nl/nmea/
A textfile consisting of NMEA strings is read & uses the constructor to parse the strings into Position objects with all the attributes 
found in the string. Every position is stored in an array & getCurrentPosition is used to get a rolling average of the average position 
value for the last n positions with n being any number picked when the constructor is called. 
The class is designed to have the textfile be continously read & parsed utilizing threading with getCurrentPosition potentially being 
called by multiple instances at once. 
