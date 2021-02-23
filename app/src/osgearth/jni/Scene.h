
#pragma once

#include "osgPlugins.h"
#include <osgViewer/Viewer>
#include <osgDB/FileUtils>
#include <osgEarth/MapNode>


typedef void UIView;

class Scene : public osg::Referenced
{
public:
    Scene();
    
    void init(const std::string& file, osg::Vec2 viewSize, UIView* view=0);
    
    void frame();
    
    //return the view
    osgViewer::Viewer* getViewer() {
        return _viewer.get();
    }

	osgEarth::MapNode *getMapNode() {
		return _mapNode;
	}
    
    void setDataPath(std::string dataPath, std::string packagePath);
    
protected:
    virtual ~Scene();

    void initDemo(const std::string& file);
    
protected:
    osg::ref_ptr<osgViewer::Viewer> _viewer;
	osgEarth::MapNode* _mapNode;

    std::string _dataPath;
    std::string _packagePath;
};


