// OSG Earth built using scripts from https://github.com/thahemp
package org.osgearth.TerrainRender;

public class osgNativeLib {
    public static native void 		init(int width, int height);
    public static native void 		step();
    public static native void       touchZoomEvent(double delta);
    public static native void 		setDataFilePath(String dataPath, String packagePath);
    public static native void       setCoords(double lat, double lon, double alt);
    public static native void       setPitchHeading(double pitch, double heading);
    public static native void       changePitchHeading(double pitch, double heading);
    public static native void       setCitiesData(int position, double lat, double lon, String name);
    public static native void 		setEarthFilePath(String path);
}