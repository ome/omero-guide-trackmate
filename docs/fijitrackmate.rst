**Analyze OMERO timelapse images using the Fiji-TrackMate plugin**
==================================================================

In this example we open images from a dataset in OMERO in Fiji and use TrackMate plugin of Fiji to analyze them. For more details about TrackMate, go to \ https://imagej.net/TrackMate\ .

**Description:**
----------------

First, we will show how to use TrackMate manually. The manual steps are essential to determine the suitable parameters to analyze the images. Note that when using TrackMate manually, the generated tracks cannot be saved as ROIs into OMERO.server.

Second, using a scripting workflow, we will show:

-  How to connect to OMERO using the JAVA API.

-  How to load all the images within a given Project.

-  How to load the images in ImageJ using Bio-Formats

-  How to analyze the images using TrackMate

-  How to save the tracks as polygons ROIs in OMERO

-  How to collect the measurement associated to the tracks and

   -  Save them as an OMERO.table and link the table to the Project.

   -  Save them as a CSV file and link it to the Project.

**Setup:**
----------

-  Install Fiji on the local machine with the OMERO.insight-ij plugin. The installation instructions can be found at [LINK TO BE ADDED].

**Resources:**
--------------

-  For the manual workflow, an artificial track image \ https://samples.fiji.sc/FakeTracks.tif

-  For the scripting workflow, the timelapse data available at \ https://downloads.openmicroscopy.org/images/DV/iain/438CTR/

-  Script: \ https://github.com/ome/training-scripts/blob/master/practical/jython/tracker.jy

Step-by-Step:
-------------

**Manual workflow**
~~~~~~~~~~~~~~~~~~~

Each of you can work on your own data. We will use the manual workflow to explain the steps of the script below.

1. Launch Fiji

2. Go to Plugins > OMERO > Connect to OMERO

3. Enter the username and password provided.

4. Browse the Dataset artificial-trackmate

5. Double-click on the FakeTracks.tif Image to open it in Fiji.

   a. Make sure it is opened using the Hyperstack viewer. This option depends on what you did last time when using your Fiji.

   b. In case you are not sure, Open Plugins > Bio-Formats > Bio-Formats importer and then select any image on your local computer to open it in Fiji. In the popup dialog, in the top left dropdown menu

      .. image:: images/Trackmate1.png

   c. Select Hyperstack and click OK. Then, close and re-open the FakeTracks.tif Image from OMERO.

6. Go to Plugins > Tracking > TrackMate

7. Click Next in the first dialog that pops up.

8. A new dialog pop ups indicating to select a detector. Select the DoG detector. Click Next.

   d. Set the Estimated blob diameter to 5.0

   e. Set the Threshold to 5.0

      .. image:: images/Trackmate2.png

9.  Click the Preview button to find spots. 4 spots should be found.

10. Click the Next button several times until you get to the Select a view dialog.

11. Select the HyperStack Displayer view

    .. image:: images/Trackmate3.png

12. Click the Next button twice until you get to the Select a tracker window. Select the LAP Tracker

    .. image:: images/Trackmate4.png

13. Click the Next button several times until a dialog with three tabs pops up

14. Select the Tracks tab to display the Tracks.

    .. image:: images/Trackmate5.png

15. Click Next until you get to Select an action dialog.

    f. Select the option Capture overlay

    g. Click Execute.

       .. image:: images/Trackmate6.png

16. Click OK in the following dialog, leaving the defaults (the whole stack will be captured).

17. New image appears. Select it. This new Image can then be imported as an OME-TIFF using Plugins > OMERO > Save Image(s) to OMERO.


**Scripting workflow**
~~~~~~~~~~~~~~~~~~~~~~

The scripting workflow saves the ROIs to the server on the original images, it does not generate a new OMERO Image. The OMERO.insight-ij plugin installed above includes everything needed to connect to OMERO using the Script Editor of Fiji

1.  In your browser, go to the server address provided.

2.  Enter the username and password provided.

3.  Make sure you are working with your own data.

4.  Create a Project: trackmate\ .

5.  Drag and Drop the Dataset dv-iain-multiTZ into the Project: trackmate. Note the ID of the Project.

6.  Launch Fiji, open File > New > Script... and select Python as language.

7.  Copy the content of the script \ https://raw.githubusercontent.com/ome/training-scripts/master/practical/jython/tracker.jy\  into the script window of Fiji.

8.  Edit the credentials to connect to the server and change the ID of the Project in the script window of Fiji, replacing it with the ID of your Project trackmate.

9.  Study the script step-by-step.

10. Click Run.

11. After the script has finished, go to the images in the webclient and open the second image in OMERO.iviewer.

12. Click on the ROI tab and observe that you now have ROIs under which there are Shapes. Each ROI is a collection of shapes. The ROI corresponds to a Track in Trackmate. There is always one polyline shape in each ROI which represents the track. The other, elliptical shapes in the same ROI represent the tracked spots.

    .. image:: images/Trackmate7.png

13. Play the timelapse video in OMERO.iviewer.

14. Go to the Info tab, and in the Open with: line click on OMERO.figure. In OMERO.figure, add the Tracks and ellipses to the panel by selecting the appropriate ROIs in the Labels tab of OMERO.figure.

    .. image:: images/Trackmate8.png

