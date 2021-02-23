#pragma once 

#include <osgViewer/GraphicsWindow>
#include <osgDB/Registry>

#ifndef ANDROID
USE_GRAPICSWINDOW_IMPLEMENTATION(IOS)
#endif


//osg plugins
USE_OSGPLUGIN(OpenFlight)
USE_OSGPLUGIN(obj)
USE_OSGPLUGIN(shp)
USE_OSGPLUGIN(ive)

//depreceated osg format
USE_OSGPLUGIN(osg)
USE_DOTOSGWRAPPER_LIBRARY(osg)


USE_OSGPLUGIN(osg2)
USE_SERIALIZER_WRAPPER_LIBRARY(osg)
USE_SERIALIZER_WRAPPER_LIBRARY(osgAnimation)

USE_OSGPLUGIN(rot)
USE_OSGPLUGIN(scale)
USE_OSGPLUGIN(trans)


//image files
#ifndef ANDROID
USE_OSGPLUGIN(tiff)
USE_OSGPLUGIN(imageio)
#else
USE_OSGPLUGIN(png)
USE_OSGPLUGIN(tiff)
USE_OSGPLUGIN(jpeg)
#endif
USE_OSGPLUGIN(curl)
USE_OSGPLUGIN(freetype)


USE_OSGPLUGIN(kml)
USE_OSGPLUGIN(osgearth_sky_simple)
USE_OSGPLUGIN(osgearth_sky_gl)
USE_OSGPLUGIN(osgearth_feature_wfs)
USE_OSGPLUGIN(osgearth_feature_tfs)
USE_OSGPLUGIN(osgearth_tms)
USE_OSGPLUGIN(osgearth_wms)
USE_OSGPLUGIN(osgearth_xyz)
USE_OSGPLUGIN(osgearth_label_annotation)
USE_OSGPLUGIN(osgearth_mask_feature)
USE_OSGPLUGIN(osgearth_model_feature_geom)
USE_OSGPLUGIN(osgearth_feature_ogr)
USE_OSGPLUGIN(osgearth_model_feature_stencil)
USE_OSGPLUGIN(osgearth_vdatum_egm2008)
USE_OSGPLUGIN(osgearth_model_simple)
USE_OSGPLUGIN(osgearth_engine_mp)
USE_OSGPLUGIN(osgearth_engine_byo)
USE_OSGPLUGIN(osgearth_vdatum_egm96)
USE_OSGPLUGIN(osgearth_debug)
USE_OSGPLUGIN(osgearth_vdatum_egm84)
USE_OSGPLUGIN(osgearth_tileservice)
USE_OSGPLUGIN(osgearth_yahoo)
USE_OSGPLUGIN(osgearth_arcgis_map_cache)
USE_OSGPLUGIN(osgearth_tilecache)
USE_OSGPLUGIN(osgearth_wcs)
USE_OSGPLUGIN(osgearth_gdal)
USE_OSGPLUGIN(earth)
USE_OSGPLUGIN(osgearth_cache_filesystem)
USE_OSGPLUGIN(osgearth_arcgis)
USE_OSGPLUGIN(osgearth_osg)

