Analyze OMERO timelapse images using the TrackMate User Interface
=================================================================

In this example we open in Fiji an image stored in an OMERO server and use `TrackMate <https://imagej.net/TrackMate>`_ to analyze it.

Description
-----------

In this section, we show how to use TrackMate via its User Interface.
The manual steps are essential to determine the suitable parameters to analyze the images. Note that when using the TrackMate User Interface, the generated tracks **cannot** be saved as ROIs into OMERO.server.

Setup
-----

-  Install Fiji on the local machine with the OMERO.insight-ij plugin. The installation instructions can be found at `here <https://omero-guides.readthedocs.io/en/latest/fiji/docs/installation.html>`_.

Resources
---------

-  We use an artificial track image \ https://samples.fiji.sc/FakeTracks.tif.


Step-by-Step
------------

We will use the manual workflow to explain the steps of the script below.

#. Launch Fiji.

#. Go to *Plugins > OMERO > Connect* To OMERO. This will show a login
   screen where you can enter the name of the server to connect to,
   the username and password. The OMERO plugin will allow you to
   browse your data in a similar manner to OMERO.web.

#. In the OMERO login dialog, click the wrench icon\ |image1| and then
   add the server address in the dialog. By default, only "localhost"
   is listed. Click on the *plus* icon to add a new line to the list
   and type into the line the server address.

#. Click Apply.

   .. image:: images/manual2.png

#.  Enter your credentials and click *Login*.

#. Browse the Dataset `artificial-trackmate`.

#. Double-click on the `FakeTracks.tif` Image to open it in Fiji.

   #. Make sure it is opened using the ``Hyperstack`` viewer. This option depends on what you did last time when using your Fiji.

   #. In case you are not sure, Open ``Plugins > Bio-Formats > Bio-Formats importer`` and then select any image on your local computer to open it in Fiji. In the popup dialog, in the top left dropdown menu.

      .. image:: images/Trackmate1.png

   #. Select ``Hyperstack`` and click ``OK``. Then, close and re-open the FakeTracks.tif Image from OMERO.

#. Go to ``Plugins > Tracking > TrackMate``.

#. Click ``Next`` in the first dialog that pops up.

#. A new dialog pop ups indicating to select a detector. Select the ``DoG detector``. Click ``Next``.

   #. Set the ``Estimated blob diameter`` to ``5.0``

   #. Set the ``Threshold`` to ``5.0``

      .. image:: images/Trackmate2.png

#.  Click the ``Preview`` button to find spots. 4 spots should be found.

#. Click the ``Next`` button several times until you get to the ``Select a view`` dialog.

#. Select the ``HyperStack Displayer view``

    .. image:: images/Trackmate3.png

#. Click the ``Next`` button twice until you get to the ``Select a tracker`` window. Select the ``LAP Tracker``.

    .. image:: images/Trackmate4.png

#. Click the ``Next`` button several times until a dialog with three tabs pops up

#. Select the ``Tracks`` tab to display the ``Tracks``.

    .. image:: images/Trackmate5.png

#. Click ``Next`` until you get to ``Select an action`` dialog.

    #. Select the option Capture overlay

    #. Click Execute.

       .. image:: images/Trackmate6.png

#. Click ``OK`` in the following dialog, leaving the defaults (the whole stack will be captured).

#. New image appears. Select it. This new Image can then be imported as an OME-TIFF using ``Plugins > OMERO > Save Image(s) to OMERO``. The tracks will be part of the generated OME-TIFF and the timepoints will be captured as z-sections.


.. |image1| image:: images/manual1.png
   :width: 0.24105in
   :height: 0.24105in