module com.udacity.catpoint.Image {
    requires org.slf4j;
    requires software.amazon.awssdk.auth;
    requires software.amazon.awssdk.core;
    requires software.amazon.awssdk.services.rekognition;
    requires software.amazon.awssdk.regions;
    requires java.desktop;
    exports com.udacity.catpoint.Image;
}