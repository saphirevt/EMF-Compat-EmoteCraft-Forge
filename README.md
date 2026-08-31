# EMF Compat: EmoteCraft (Forge)
This is a port of the EMF Compat: Emotecraft mod for Minecraft 1.21.1 NeoForge by joaosant05 to Minecraft 1.20.1 Forge.

This mod allows to apply bending from bendy-lib to the cuboids of the player model from EMF (and from resource packs, for example, Fresh Animations: Player Extension).
![Bending with Fresh Animations](https://cdn.modrinth.com/data/EROrh6Sf/images/1804b5b5199a6745a0ad38eb902e030138c64d47.gif)

The original bendy-lib can only bend the limbs of the vanilla player model during Emotecraft emotion playback, which is why it doesn’t work with custom player models, for example, FA:PE, during emotions, limbs cannot bend.

## Requirements
This mod requires:
* Entity Model Features
* Player Animator
* EmoteCraft
* Bandy Lib

## Notes
* _Custom player models with many boxes on a limb may not bend properly. I added a check to the code that if an element has more than 3 boxes, that limb will not bend._
* _Also, the elements of your custom model that will be bent must have a box UV map, because when an element with a per‑face UV map is bent, the textures will be displayed incorrectly._
* _This mod most likely won’t receive updates, as I ported it for my own modpack to play with friends (at most, I might fix the texture issues on the armor layer when it bends)._

## Credits
* The original mod was created by [joaosant05](https://github.com/joaosant05/emf-compat-emotecraft)
