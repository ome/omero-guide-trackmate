Analyze OMERO timelapse images using the TrackMate API
======================================================

In this example we open an image stored in an OMERO server and use the `TrackMate <https://imagej.net/TrackMate>`_ API to analyze it.
One of the advantages of this approach over the User interface workflow is that the generated track **can** be saved as
OMERO ROIs.

Description
-----------

First, we will show how to use the TrackMate API and the Scripting editor of Fiji.

We will show:

-  How to connect to OMERO using the JAVA API.

-  How to retrieve an image.

-  How to open the image using Bio-Formats.

-  How to create a TrackMate model using its API.

-  How to save the tracks as polygon ROIs in OMERO.


Setup
-----

- Install Fiji on the local machine with the OMERO.insight-ij plugin. The installation instructions can be found at `here <https://omero-guides.readthedocs.io/en/latest/fiji/docs/installation.html>`_.

Resources
---------

-  We will use a timelapse image available at `DV/iain/438CTR <https://downloads.openmicroscopy.org/images/DV/iain/438CTR/>`_

-  Script: Groovy script for tracking timelapse images - :download:`tracking.groovy <../scripts/tracking.groovy>`.

Step-by-Step
------------

In this section, we go through the steps required to analyze the data.
The script used in this document is :download:`tracking.groovy <../scripts/tracking.groovy>`.

One advantage of the scripting approach is that we can save the generated tracks as 
ROIs in an OMERO server.

#. Launch Fiji.

#. Open ``File > New > Script...``.

#. Select ``Groovy`` as a language.

#. Copy the content of :download:`tracking.groovy <../scripts/tracking.groovy>` in the text area.

#. A dialog will pop up. Enter the credentials to connect to the server and select an Image.

#. Click Run.

#. Go back to OMERO.web to visualize the tracks. Double-click on the image in OMERO.web to open it in OMERO.iviewer.

#. Click on the ROI tab and observe that you now have ROIs under which there are Shapes. Each ROI is a collection of shapes. The ROI corresponds to a track in Trackmate. There is always one polyline shape in each ROI which represents the track. The other, elliptical shapes in the same ROI represent the tracked spots.

    .. image:: images/Trackmate7.png

#. Play the timelapse video in OMERO.iviewer.

#. Go to the Info tab, and in the Open with: line click on OMERO.figure. In OMERO.figure, add the Tracks and ellipses to the panel by selecting the appropriate ROIs in the Labels tab of OMERO.figure.

    .. image:: images/Trackmate8.png


Script's description
~~~~~~~~~~~~~~~~~~~~


**Import** packages needed:

.. literalinclude:: ../scripts/tracking.groovy
    :start-after: // Import-start
    :end-before: // Import-end

**Connect to the server**. It is also important to close the connection again
to clear up potential resources held on the server. This is done in the 
``disconnect`` method:

.. literalinclude:: ../scripts/tracking.groovy
    :start-after: // Import-end
    :end-before: // Load-Image

**Load** the image from the server:

.. literalinclude:: ../scripts/tracking.groovy
    :start-after: // Load-Image
    :end-before: // Open-Image

**Read** the binary data using Bio-Formats:

.. literalinclude:: ../scripts/tracking.groovy
    :start-after: // Open-Image
    :end-before: // Create-Tracker

**Create** a tracking model using the TrackMate API:

.. literalinclude:: ../scripts/tracking.groovy
    :start-after: // Create-Tracker
    :end-before: // Convert-Tracks

**Convert** the tracks into OMERO ROIs:

.. literalinclude:: ../scripts/tracking.groovy
    :start-after: // Convert-Tracks
    :end-before: // Save-ROIs

**Save** the converted tracks back to the OMERO server:

.. literalinclude:: ../scripts/tracking.groovy
    :start-after: // Save-ROIs
    :end-before: // Main

In order the use the methods implemented above in a proper standalone script,
**Wrap it all up**:

.. literalinclude:: ../scripts/tracking.groovy
    :start-after: // Main
