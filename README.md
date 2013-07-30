ColorCauldron
=============
A simple image manipulation program in Java using AWT and the Marvin Image Processing Framework (http://marvinproject.sourceforge.net); it only allows a few rather non-standard operations 
but is still fun to play with.  Operations include:
1. Drag and drop images to parent view panes via J2SE's AWT package (sorry, Android)
2. Juxtapose color channel values randomly in parent 1 (left side parent) on a per-pixel basis
3. 'Shade-stack' parent images either wrapping (binary pixel color values added without
   bounds, leaving values over 255 to be interpreted as Two's Complement and thus wrap around to
   low values) or not wrapping (same as above but leaving values greater than 255 at 255).  This
   creates a neat little composite color merge of two images; at the moment it is required that
   each parent have equivalent dimensions, but this could be changed fairly easily.  One would
   need to introduce OpenGL style texture interpolation modes like GL_CLAMP so that the program can decide
   how to reconcile different dimensions.
4. Invert colors of the first parent image
5. Saturate hues of the first parent image (reduce all color channel values besides those of the chosen chanel to zero)
6. Convert RGB images to gray-scale
