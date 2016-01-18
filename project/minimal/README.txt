This folder contains the minimal number of components needed to create a functioning SFC environment, not counting the (IA) SFC and ILS-SFC modules.

To create an environment from a clean project:
-import all windows in "windows" folder

-import all templates from the "templates" folder

-import tags/clientTags.xml under "Client" in the Tag Browser

-import tags/udts.xml under the Data Types folder of your desired tag provider(s).
   (since tags are global, if you have done this oncee for another project you don't need to do it again--however, if you think the definitions may have changed, you should delete DataTypes/SFC before importing the latest ones).

-Follow the instructions in scripts/msgHandlerInstructions.txt

-Verify that IA's SFC module is installed in your Ignition environment

-From Eclipse, run the "BuildAndInstallModule" Ant script in the sfc-build project

-From Eclipse, run the "deploy" target in the ILS-Python project. Alternatively, download the ILS-SFC module from Google Drive and install it.

-Close any Ignition clients or Designers. Re-start the Ignition Gateway.

-Before starting any charts, go to View/External Interface Configuration and set databases and Tag Providers for Production mode, and for Isolation mode if you will be using it. Note: although initial defaults are shown in the combo boxes, these will not take effect unless you press OK.

-In all databases specified in step 9), run the DDL in database/createSfcTables.sql to create the Sfc schema

-After performing step 9), change the value of the [Client]/IsolationMode tag at least once so the tag change script will set the correct value for the database and tag provider.

-Save the project.

-ILS SFC charts need to be started in a special way, and charts involving window UIs will not work from the Designer. If you don't have a launcher window already defined, in the Designer mark the SFC/ExampleLauncher window as "Open on Startup" and use the button action scripts as models for your own application.