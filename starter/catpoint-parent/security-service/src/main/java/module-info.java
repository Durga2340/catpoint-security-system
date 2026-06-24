module security.service {

    requires image.service;

    requires java.desktop;
    requires java.prefs;

    requires miglayout.swing;

    requires com.google.gson;
    requires com.google.common;

    exports com.udacity.catpoint.application;
    exports com.udacity.catpoint.data;
    exports com.udacity.catpoint.security;

    opens com.udacity.catpoint.data to com.google.gson;
}