
#include "Scene.h"

#include <osgEarth/MapNode>
#include <osgEarthUtil/Sky>
#include <osgEarthUtil/EarthManipulator>
#include <osgEarthDrivers/tms/TMSOptions>
#include <osgEarthDrivers/tilecache/TileCacheOptions>
#include <osg/PositionAttitudeTransform>
#include <osg/CameraView>
#include <osgEarthDrivers/gdal/GDALOptions>
#include <osgEarthAnnotation/PlaceNode>
#include <osgEarthAnnotation/LabelNode>
#include <osgEarthAnnotation/ImageOverlay>
#include <osgEarthAnnotation/CircleNode>
#include <osgEarthAnnotation/RectangleNode>
#include <osgEarthAnnotation/EllipseNode>
#include <osgEarthAnnotation/LocalGeometryNode>
#include <osgEarthAnnotation/FeatureNode>
#include <osgEarthAnnotation/ModelNode>
#include <osgEarthUtil/Controls>
#include <osgViewer/Viewer>

#include <osgDB/ReadFile>
#include <osgDB/FileUtils>
#include <osgGA/MultiTouchTrackballManipulator>


using namespace osgEarth;
using namespace osgEarth::Annotation;
using namespace osgEarth::Features;
using namespace osgEarth::Util;
using namespace osgEarth::Drivers;
using namespace osgEarth::Util::Controls;

void Scene::initDemo(const std::string &file)
{
    extern std::string myPath;
	std::string filepath = myPath;
	osg::Node* node = osgDB::readNodeFile(filepath);
	//create mapnode
	osg::ref_ptr<osgEarth::Util::MapNode> _mapNode = osgEarth::Util::MapNode::findMapNode(node);

    //set manipulator to viewer
    _viewer->setCameraManipulator(new osgEarth::Util::EarthManipulator());
	osgEarth::Util::EarthManipulator* manip = dynamic_cast<osgEarth::Util::EarthManipulator*>(_viewer->getCameraManipulator());
	osgEarth::Util::EarthManipulator::ActionOptions options;

    //label settings
    osgEarth::Util::Controls::ControlCanvas* cs = ControlCanvas::getOrCreate( _viewer );
    VBox* center = new VBox();

    LabelControl* label = new LabelControl( "osgEarth Controls Toolkit" );
    label->setFontSize( 24.0f );
    center->addControl( label );

    cs->addControl( center );

    //manipulator settings
	manip->getSettings()->setMouseSensitivity(0.30);
	manip->getSettings()->setThrowingEnabled(true);
	manip->getSettings()->setThrowDecayRate(0.1);
	manip->getSettings()->setArcViewpointTransitions(false);
	manip->getSettings()->bindTwist(osgEarth::Util::EarthManipulator::ACTION_NULL, options);
	manip->getSettings()->bindMultiDrag(osgEarth::Util::EarthManipulator::ACTION_NULL, options);
	//set min max pitch to enable phone pointing up
	manip->getSettings()->setMinMaxPitch(-90.0, 90.0);

    extern double latitude;
    extern double longitude;
    extern double altitude;
    extern double mPitch;
    extern double mHeading;

    //set camera on position given by GPS
    manip->setHomeViewpoint(osgEarth::Viewpoint(
        "Home",
        longitude, latitude, altitude + 25.0,   // longitude, latitude, altitude
        mHeading, mPitch, -altitude - 50.0)); // heading, pitch, range

    //create skynode
	osgEarth::Util::SkyOptions sOptions;
	sOptions.ambient() = 1.0;
	osgEarth::Util::SkyNode* sky = osgEarth::Util::SkyNode::create(sOptions, _mapNode);
	sky->attach(_viewer, 0);
	sky->addChild(_mapNode);

    //create label group
    osg::Group* labelGroup = new osg::Group();
    MapNode::get(_mapNode)->addChild( labelGroup );

    Style pin;

    //lat/long SRS for specifying points
    const SpatialReference* geoSRS = _mapNode->getMapSRS()->getGeographicSRS();

    extern double latArray[];
    extern double lonArray[];
    extern std::string nameArray[];
    extern int pos;

    //place nodes from OverPass
    for (int i = 0; i < pos; i++)
    {
        labelGroup->addChild( new PlaceNode(_mapNode, GeoPoint(geoSRS, lonArray[i], latArray[i]), nameArray[i], pin));
    }
    //set scene data to viewer
	_viewer->setSceneData(_mapNode.get());

    _viewer->realize();
}
