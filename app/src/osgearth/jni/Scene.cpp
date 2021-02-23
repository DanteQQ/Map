
#include "Scene.h"
#include <osgEarth/AndroidCapabilities>
#include <osgEarth/Registry>

Scene::Scene()
    :osg::Referenced()
{
    
}

Scene::~Scene()
{
    
}

void Scene::init(const std::string& file, osg::Vec2 viewSize, UIView* view)
{
	OSG_ALWAYS << "Initializing scene graph" << std::endl;

    osgEarth::Registry::instance()->setCapabilities(new osgEarth::AndroidCapabilities());

    //setup viewer
	_viewer = new osgViewer::Viewer();
    _viewer->setUpViewerAsEmbeddedInWindow(0, 0, viewSize.x(), viewSize.y());
    _viewer->getCamera()->setViewport(new osg::Viewport(0, 0, viewSize.x(), viewSize.y()));
    _viewer->getCamera()->setClearMask(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    _viewer->getCamera()->setClearColor(osg::Vec4(0.0f,1.0f,0.0f,1.0f));
    _viewer->getCamera()->setProjectionMatrixAsPerspective(45.0f,(float)viewSize.x()/viewSize.y(), 0.1, 1000.0);
    _viewer->getCamera()->setNearFarRatio(0.00001);
    _viewer->setThreadingModel(osgViewer::ViewerBase::SingleThreaded);
    _viewer->getDatabasePager()->setIncrementalCompileOperation(new osgUtil::IncrementalCompileOperation());
    _viewer->getDatabasePager()->setDoPreCompile( true );
    _viewer->getDatabasePager()->setTargetMaximumNumberOfPageLOD(0);
    _viewer->getDatabasePager()->setUnrefImageDataAfterApplyPolicy(true,true);

    this->initDemo(file);
}
//draw frame
void Scene::frame()
{
	_viewer->frame();
}

void Scene::setDataPath(std::string dataPath, std::string packagePath)
{
	_dataPath = dataPath;
	_packagePath = packagePath;
}

