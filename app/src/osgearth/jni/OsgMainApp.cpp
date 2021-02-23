#include "OsgMainApp.hpp"

#include <osgEarthUtil/EarthManipulator>
#include <osgEarth/MapNode>
#include <osgEarth/Viewpoint>


//create scene graph and start TCP/UDP sockets for communicating with the server

//init window
void OsgMainApp::initOsgWindow(int x,int y,int width,int height)
{
    _scene = new Scene();
    _scene->setDataPath(_dataPath, _packagePath);
    _scene->init("", osg::Vec2(width, height), 0);
    _bufferWidth = width;
    _bufferHeight = height;
}

//called from Java app on frame render
void OsgMainApp::draw()
{
    _scene->frame();
    
    //clear events for next frame
    _frameTouchBeganEvents = NULL;
    _frameTouchMovedEvents = NULL;
    _frameTouchEndedEvents = NULL;
}

//zoom function, using delta from 2 finger input
static bool flipy = true;
void OsgMainApp::touchZoomEvent(double delta)
{
	osgEarth::Util::EarthManipulator* manip = dynamic_cast<osgEarth::Util::EarthManipulator*>(_scene->getViewer()->getCameraManipulator());
	manip->zoom(0.0, delta);
}
//set data paths
void OsgMainApp::setDataPath(std::string dataPath, std::string packagePath)
{
	_dataPath = dataPath;
	_packagePath = packagePath;
}

double latitude = 0.0;
double longitude = 0.0;
double altitude = 0.0;
//set initial coordinates
void OsgMainApp::setCoords(double lat, double lon, double alt)
{
    latitude = lat;
    longitude = lon;
    altitude = alt;
}
double mPitch = 0.0;
double mHeading = 0.0;
//set initial azimuth and pitch of phone
void OsgMainApp::setPitchHeading(double pitch, double heading)
{
    mPitch = pitch;
    mHeading = heading;
}
//change azimuth and pitch of phone
void OsgMainApp::changePitchHeading(double pitch, double heading)
{
    osgEarth::Util::EarthManipulator* manip = dynamic_cast<osgEarth::Util::EarthManipulator*>(_scene->getViewer()->getCameraManipulator());
    manip->rotate(heading, pitch);
}
double latArray[200];
double lonArray[200];
std::string nameArray[200];
int pos = 0;
//save data of geopoints from Overpass API
void OsgMainApp::setCitiesData(int position, double lat, double lon, std::string name)
{
    latArray[position] = lat;
    lonArray[position] = lon;
    nameArray[position] = name;
    pos = position;
}
std::string myPath;
//set earthfile path
void OsgMainApp::setEarthFilePath(std::string path)
{
    myPath = path;
}