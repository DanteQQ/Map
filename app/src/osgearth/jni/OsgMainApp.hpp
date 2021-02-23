#ifndef OSGMAINAPP_HPP_
#define OSGMAINAPP_HPP_

#include <android/log.h>
#include <iostream>
#include <cstdlib>
#include <math.h>
#include <string>
#include <osgViewer/Viewer>
#include "osgPlugins.h"
#include "Scene.h"


class OsgMainApp{
	private:
		osg::ref_ptr<Scene> _scene;
		int _bufferWidth;
		int _bufferHeight;
		std::string _dataPath;
		std::string _packagePath;
		bool _initialized;

		//events for each touch phase, these are cleared each frame
		osg::ref_ptr<osgGA::GUIEventAdapter> _frameTouchBeganEvents;
		osg::ref_ptr<osgGA::GUIEventAdapter> _frameTouchMovedEvents;
		osg::ref_ptr<osgGA::GUIEventAdapter> _frameTouchEndedEvents;
		
	public:
		void initOsgWindow(int x,int y,int width,int height);
		void draw();
		void touchZoomEvent(double delta);
		void setDataPath(std::string dataPath, std::string packagePath);
		void setCoords(double lat, double lon, double alt);
        void setPitchHeading(double pitch, double heading);
        void changePitchHeading(double pitch, double heading);
        void setCitiesData(int position, double lat, double lon, std::string name);
        void setEarthFilePath(std::string path);
};


#endif /* OSGMAINAPP_HPP_ */
