-------------------------------
mIDas - Block ID Changer *GOLD* V0.2.5
-------------------------------
by Pfaeff

*GOLD* 
by Havocx42

IMPORTANT NOTE:
===============

I am not responsible for any damage or data loss that may occur using this tool.


What is mIDas *GOLD*?
==============
mIDas *GOLD* is an update of mIDas.

mIDas is a very simple tool for Minecraft that allows you to change the IDs of certain blocks 
and items in your world files. You can for example replace all blocks of dirt with blocks of cobblestone 
if you want to. This tool is especially useful for modders and mod users, because it allows to 
exchange IDs during the development process. It also changes the IDs inside of chests and so on, 
(but not those inside your inventory, mIDas *GOLD* fixes player inventories) or those lying around. But this is already planned for future versions.

mIDas *GOLD* adds these major features:

*Blocks in player inventories are now changed.

*Blocks with id 127-255 are now supported.

*Blocks with id 255-4095 are now supported.

*The user defines multiple translations between any two block ids and they are all processed at once, this allows blocks to be switched without a 3rd block id being used.

*The ability to load a patch file containing Source and Target IDs for large jobs.

*Supports Anvil save format

*Allows changing damage values


Which minecraft versions are supported?
=======================================

This tool currently works with all minecraft versions that use the Anvil save format.


Installation:
=============

Extract mIDasGold.jar, IDNames.txt and the plugins directory into the same folder and doubleclick mIDasGold.jar to start.
If that doesn't work, use the console command "java -jar mIDasGold.jar".

Notice: Opening with "Right-click-> open-with -> Java" is known to cause problems.


Usage:
======

You need to select a savegame first (you can add external savgames using the "add savegame" button).
You can then add the ID translations that you want to the list. Choose a source ID then a target ID and click add translation. You can add as many translations as you want. When you have all your translations recorded click start.
If the ID you want is not in the dropdown list you can simply type it in.
If you want to specify a damage value in the source or target type it in in the format <ID>:<Damage>
Depending on the world size, this may take some time.
Note that this process can not be undone. If you exchange ID 15 with 0 for example and then change 0 back to 15, all blocks that were 0 before now get changed, too. 
It is recommended to do a manual backup.

To load a patch file click Load and point it to a txt file with the following layout

<source ID>:<source damage vlaue> -> <Target ID>:<target damage value>

example:
to change all blocks with ID of 1 to an ID of 112:
1 -> 112
or 112 to 18:
112 -> 18

To change all Blocks with an ID of 35 to an ID of 35 with a damage value of 1:
35 -> 35:1

To change all Blocks with an ID of 120 and a damage value of 12 to an ID of 19:
120:12 -> 19
or(both have the same effect)
120:12 -> 19:12

note: spaces are important


Damage Values:
==============

mIDas *GOLD* converts Damage values of items in inventories as you might expect. However damage values of placed blocks are stored in the "Data" field which has a maximum value of 15.
Blocks with damage values higher than 15 (such as some Redpower blocks) have their own methods to store that extra information, mIDas cannot change that information while the blocks are placed.


Sourcecode:
===========

The sourcecode for mIDas can be found on google code: http://code.google.com

The sourcecode for mIDas *GOLD* can be found at: http://code.google.com/p/midas-gold/


Additional notes:
=================

- I only tested this tool under Windows, so there might be problems on other systems. 
  Though it might work with some restrictions.
- ID names for the drop down can be customized in the IDNames.txt, These are STRICTLY informational, they have no effect.


Bugs/Suggestions
================

Found a bug or have a suggestion to make? Either mail it to havoc42@gmail.com
or use the bug tracker on google code: http://code.google.com/p/midas-gold/