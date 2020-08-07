/*
 * -----------------------------------------------------------------------------
 *  Copyright (C) 2020 University of Dundee. All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * ------------------------------------------------------------------------------
 */

/*
 * This Groovy script uses the plugin Trackmater to analyse images, the generated tracks are
 * saved to OMERO.
 * In this script, we show:
 *  - How to connect to OMERO
 *  - How to open an image using Bio-Formats
 *  - How to create a trackmate model via its API
 *  - How to convert the ROIs created by Trackmate into OMERO ROIs
 * More details about restricted privileges can be found at
 * https://docs.openmicroscopy.org/latest/omero/sysadmins/restricted-admins.html
 * Use this script in the Scripting Dialog of Fiji (File > New > Script).
 * Select Groovy as language in the Scripting Dialog.
 * Error handling is omitted to ease the reading of the script but this
 * should be added if used in production to make sure the services are closed
 * Information can be found at
 * https://docs.openmicroscopy.org/latest/omero5/developers/Java.html
 */
 
#@ String(label="Username") USERNAME
#@ String(label="Password", style='password') PASSWORD
#@ String(label="Host", value='wss://workshop.openmicroscopy.org/omero-ws') HOST
#@ String(label="Port", value=443) PORT
#@ Integer(label="Image ID", value=28629) image_id


// Import-start
import java.awt.Color
import java.util.ArrayList

import omero.gateway.Gateway
import omero.gateway.LoginCredentials
import omero.gateway.SecurityContext
import omero.gateway.facility.BrowseFacility
import omero.gateway.facility.ROIFacility
import omero.gateway.model.EllipseData
import omero.gateway.model.PolylineData
import omero.gateway.model.ROIData
import omero.log.SimpleLogger
import omero.model.PolylineI
import static omero.rtypes.rstring

import ij.IJ

import fiji.plugin.trackmate.Spot
import fiji.plugin.trackmate.Settings
import fiji.plugin.trackmate.Model
import fiji.plugin.trackmate.SelectionModel
import fiji.plugin.trackmate.TrackMate
import fiji.plugin.trackmate.detection.DetectorKeys
import fiji.plugin.trackmate.detection.DogDetectorFactory
import fiji.plugin.trackmate.tracking.sparselap.SparseLAPTrackerFactory
import fiji.plugin.trackmate.tracking.LAPUtils
import fiji.plugin.trackmate.visualization.hyperstack.HyperStackDisplayer
import fiji.plugin.trackmate.features.spot.SpotContrastAndSNRAnalyzerFactory
import fiji.plugin.trackmate.features.spot.SpotIntensityAnalyzerFactory
import fiji.plugin.trackmate.features.track.TrackSpeedStatisticsAnalyzer
// Import-end

def connect_to_omero() {
    "Connect to OMERO"

    credentials = new LoginCredentials()
    credentials.getServer().setHostname(HOST)
    credentials.getUser().setUsername(USERNAME.trim())
    credentials.getUser().setPassword(PASSWORD.trim())
    simpleLogger = new SimpleLogger()
    gateway = new Gateway(simpleLogger)
    gateway.connect(credentials)
    return gateway
}

def disconnect(gateway) {
    gateway.disconnect()
}

// Load-Image
def get_image(gateway, image_id) {
    "Retrieve the image"
    browse = gateway.getFacility(BrowseFacility)
    user = gateway.getLoggedInUser()
    ctx = new SecurityContext(user.getGroupId())
    return browse.getImage(ctx, image_id)
}

// Open-Image
def open_image_plus(host, port, username, password, group_id, image_id) {
    "Open the image using the Bio-Formats Importer"

    StringBuilder options = new StringBuilder()
    options.append("location=[OMERO] open=[omero:server=")
    options.append(host)
    options.append("\nuser=")
    options.append(username.trim())
    options.append("\nport=")
    options.append(port)
    options.append("\npass=")
    options.append(password.trim())
    options.append("\ngroupID=")
    options.append(group_id)
    options.append("\niid=")
    options.append(image_id)
    options.append("] ")
    options.append("windowless=true view=Hyperstack ")
    IJ.runPlugIn("loci.plugins.LociImporter", options.toString())
}

// Create-Tracker
def create_tracker(imp) {
    "Create the trackmate model for the specified ImagePlus object"
    // Instantiate model object
    model = new Model()

    // Prepare settings object
    settings = new Settings()
    settings.setFrom(imp)
    // Configure detector
    settings.detectorFactory = new DogDetectorFactory()
    settings.detectorSettings.put(DetectorKeys.KEY_DO_SUBPIXEL_LOCALIZATION, true)
    settings.detectorSettings.put(DetectorKeys.KEY_RADIUS, new Double(2.5))
    settings.detectorSettings.put(DetectorKeys.KEY_TARGET_CHANNEL, 1)
    settings.detectorSettings.put(DetectorKeys.KEY_THRESHOLD, new Double(5.0))
    settings.detectorSettings.put(DetectorKeys.KEY_DO_MEDIAN_FILTERING, false)
    // Configure tracker
    settings.trackerFactory = new SparseLAPTrackerFactory()
    settings.trackerSettings = LAPUtils.getDefaultLAPSettingsMap()
    settings.trackerSettings['LINKING_MAX_DISTANCE'] = new Double(10.0)
    settings.trackerSettings['GAP_CLOSING_MAX_DISTANCE'] = new Double(10.0)
    settings.trackerSettings['MAX_FRAME_GAP'] = 3

    // Add the analyzers for some spot features
    settings.addSpotAnalyzerFactory(new SpotIntensityAnalyzerFactory())
    settings.addSpotAnalyzerFactory(new SpotContrastAndSNRAnalyzerFactory())

    // Add an analyzer for some track features, such as the track mean speed.
    settings.addTrackAnalyzer(new TrackSpeedStatisticsAnalyzer())
    settings.initialSpotFilterValue = 1

    // Instantiate trackmate
    trackmate = new TrackMate(model, settings)
    ok = trackmate.checkInput()
    if (!ok) {
        print(str(trackmate.getErrorMessage()))
        return null
    }

    ok = trackmate.process()
    if (!ok) {
        print(str(trackmate.getErrorMessage()))
        return null
    }
    // Display the results on top of the image
    selectionModel = new SelectionModel(model)
    displayer = new HyperStackDisplayer(model, selectionModel, imp)
    displayer.render()
    displayer.refresh()
    // The feature model, that stores edge and track features.
    fm = model.getFeatureModel()
    space_units = model.getSpaceUnits()
    time_units = model.getTimeUnits()
    return model
}


// Convert-Tracks
def convert_tracks(model, dx, dy) {
    "Convert the tracks into OMERO objects"
    rois = new ArrayList()
    tracks = model.getTrackModel().trackIDs(true)
    tracks.each() { track_id ->
        track = model.getTrackModel().trackSpots(track_id)
        roi = new ROIData()
        rois.add(roi)
        points = ""
        track.each() { spot ->
            sid = spot.ID()
            // Fetch spot features directly from spot.
            x = spot.getFeature('POSITION_X')/dx
            y = spot.getFeature('POSITION_Y')/dy
            r = spot.getFeature('RADIUS')
            z = spot.getFeature('POSITION_Z')
            t = spot.getFeature('FRAME')
            // Save spot as Point in OMERO
            ellipse = new EllipseData(x, y, r, r)
            ellipse.setZ((int) z)
            ellipse.setT((int) t)
            // set trackmate track ID and spot ID for later
            ellipse.setText(track_id+':'+sid)
            // set a default color
            settings = ellipse.getShapeSettings()
            settings.setStroke(Color.RED)
            roi.addShapeData(ellipse)
            points = points + x + ',' + y + ' '
        }
        // Save the track
        points = points.trim()
        polyline = new PolylineI()
        polyline.setPoints(rstring(points))
        pl = new PolylineData(polyline)
        // set a default color
        settings = pl.getShapeSettings()
        settings.setStroke(Color.YELLOW)
        roi.addShapeData(pl)
    }
    return rois
}

// Save-ROIs
def save_rois(gateway, rois, image_id) {
    roi_facility = gateway.getFacility(ROIFacility)
    user = gateway.getLoggedInUser()
    ctx = new SecurityContext(user.getGroupId())
    results = roi_facility.saveROIs(ctx, image_id, user.getId(), rois)
}

// Main part of the analysis
gateway = connect_to_omero()
exp = gateway.getLoggedInUser()
group_id = exp.getGroupId()

image = get_image(gateway, image_id)
open_image_plus(HOST, PORT, USERNAME, PASSWORD, group_id, image_id)
imp = IJ.getImage()
dx = imp.getCalibration().pixelWidth
dy = imp.getCalibration().pixelHeight
trackmate_model = create_tracker(imp)
if (trackmate_model == null) {
	print("unable to create the trackmate model")
} else {
	omero_rois = convert_tracks(trackmate_model, dx, dy)
    save_rois(gateway, omero_rois, image_id)
    print("done")
}
disconnect(gateway)
