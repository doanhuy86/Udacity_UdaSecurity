module com.udacity.catpoint.security {
    requires java.desktop;
    requires com.miglayout.swing;
    requires com.google.common;
    requires com.google.gson;
    requires java.prefs;
    requires com.udacity.catpoint.Image;
    opens com.udacity.catpoint.Security.data to com.google.gson;

}

