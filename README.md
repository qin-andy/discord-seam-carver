
# Smohbot: a content aware image scaling Discord bot

Smohbot is a content aware image manipulation (seam carving) application written in Java, packaged
into a Discord bot using [JDA](https://github.com/DV8FromTheWorld/JDA)! 

As opposed to scaling or cropping, seam carving identifies and removes the "least important" portions of an
image first when reducing image size.

## Features
- Content aware image scaling (seam carving) for message image attachments 
  - Supports scaling along both axes
  - Specify either flat pixel cuts or percentage scaling
  - Implements multiple seam carving scaling strategies
- Interacts with users within the Discord client via text
- Detailed info and error messages to guide users on how to use commands
- Features artwork of a friendly cat mascot to respond to commands!

## Examples

<div>
  <img src="https://github.com/qin-andy/Smohbot/blob/assets/src/main/resources/assets/carve_demo_user.jpg" height="250">
  <img src="https://github.com/qin-andy/Smohbot/blob/assets/src/main/resources/assets/carve_demo_smohbot.JPG" height="250">
</div>
 - The Great Wave off Kanagawa, Hokusai

<br>
<div>
  <img src="https://github.com/qin-andy/Smohbot/blob/assets/src/main/resources/assets/seattle_demo1.png" height="220">
  <img src="https://github.com/qin-andy/Smohbot/blob/assets/src/main/resources/assets/seattle_demo2.PNG" height="220">
</div>

<br>
<div>
  <img src="https://github.com/qin-andy/Smohbot/blob/assets/src/main/resources/assets/goose_demo1.png" height="220">
  <img src="https://github.com/qin-andy/Smohbot/blob/assets/src/main/resources/assets/goose_demo2.png" height="220">
</div>
 - Space Needle and Geese images are my own

## Installation Guide
 - [Git](https://git-scm.com/)
 - [Gradle](https://gradle.org/)
 - [Java 8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)

  1. Create or use an existing bot application through [Discord's Developer Portal](https://discord.com/developers/applications) and store the unique token and add it to the desired server. Note that Smohbot requires send and recieve messages with attachments.
  2. Clone the repository using git
      ```
      cd /projectpath
      git clone https://github.com/qin-andy/Smohbot.git
      ```
  3. ``cd`` into the newly created directory and use ``gradle install`` to collect dependencies
  4. Run ``gradle shadow`` or ``gradlew shadow`` to generate the fat jar in ``build/libs/smohbot-shadowjar.jar``
  5. Launch Smohbot using ``java -jar <smohbot jar location>`` and enter the token to associate it with the Discord application created in step 1
  
## Command Guide
Call Smohbot on any channel on the server!
 - ``!info``: help command, details command usage
 - ``!carve x y``: default seam carving command for dimensions x and y
 - ``!fcarve x y``: experimental forwards energy seam carving command

<img src="https://github.com/qin-andy/Smohbot/blob/assets/src/main/resources/assets/info_demo.jpg" height="500">

## Use Notes
 - Images are automatically compressed to fit within a 1000x1000 square to reduce seam carving time for larger images. This size can be changed or removed in the ModularCarver class's constant field ``MAX_SIZE``.
 - Smohbot requires permission to download, read, and write image files
 - 
## Algorithm Details
The implmentation of the "backward energy" seam carving algorithm (!carve) is built on the work of Shai Avidan and Ariel Shamir which was based on [an inital 2007 paper introducing seamcarving](https://dl.acm.org/doi/10.1145/1276377.1276390) as well as [their incredibly informative video](https://www.youtube.com/watch?v=6NcIJXTlugc) on the subject

This project was also inpsired by assignments by [Princeton](https://www.cs.princeton.edu/courses/archive/fall14/cos226/assignments/seamCarving.html), [Stanford](http://nifty.stanford.edu/2015/hug-seam-carving/), and [University of Washington](https://courses.cs.washington.edu/courses/cse373/20sp/projects/seamcarving/); However, my implementation of the algorithm is built from scratch and structured differently. For example, I found a 20 times speed increase in directly accessing raster data buffers for both reading and writing images as opposed to the using the Picture (or BufferedImage) class described in assignment specficiations.

The "forwards energy" algorithm (!fcarve) is built off of a [follow up paper](https://dl.acm.org/doi/10.1145/1360612.1360615) by Michael Rubinstein and the original authors, which reduces image artifacts by considering the energy of pixels made adjacent after a scene removal. Avik Das's [incredible article](https://avikdas.com/2019/07/29/improved-seam-carving-with-forward-energy.html) summarizing the algorithm was a great help.

<div>
  <img src="https://github.com/qin-andy/Smohbot/blob/assets/src/main/resources/assets/hokusai_demo2.PNG" height="300">
  <img src="https://github.com/qin-andy/Smohbot/blob/assets/src/main/resources/assets/hokusai_demo3.PNG" height="300">
</div>

A comparison between backwards energy (left) and and forwards energy (right). Note the smoothness on the crest of the wave on the forwards energy image versus the rough choppiness on the left.

The general framework for the seam carving algorithm is as follows:
  1. Extract RGB information from picture
  2. Calculate an energy value for each pixel to construct an "energy map"
  3. Modelling the energy map as a weighted directed acyclic graph which each pixel having an edge to 3 pixels on the row below it and with their energies as edge weights, find the shortest path from any pixels on the top row to any pixel on the bottom, the "lowest energy seam"
  4. Remove every pixel along that path, i.e. seam carve
  5. Repeat steps 2-4 until the image is the desired size, then reconstruct it based on the resulting RGB information

Forwards and backwards energy mapping are interchangeable strategies for steps 2 and 3.

