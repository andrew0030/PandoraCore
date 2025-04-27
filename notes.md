Iris compat:
- [Shadow map stuff](https://github.com/IrisShaders/Iris/blob/6c20dcd953f86b5f774abf0857ce9ecf28b44618/src/main/java/net/irisshaders/iris/pipeline/IrisRenderingPipeline.java#L743)

<hr/>

Jank:
- starting a game of chess creates triangles
- Investigate if spectator mode is broken (found on fabric). Basically looking diagonally and holding 'W' resulted in sideways movement instead of diagonal (maybe not a bug and just vanilla jank?).
- sometimes crashes when switching iris shaders
- crashes when disabling iris shaders
- does not properly reload when enabling iris shaders
- shader patcher does not like full F3+T with iris

<hr/>

Systems TODO:
- Config System
  - [ ] Value setters (needed behind the scenes to modify values ingame)
  - [ ] More annotations that can be used to control how certain entries should affect the game/menu
  - [ ] Add TranslatableComponent support for entry description and key
  - [ ] Maybe add some sort of wrapper for forge's config system
  - [ ] Maybe add support for custom Objects? (Probably dont need this tbh)
- NetworkSystem
  - Need to look into it more before I can add TODOs...
- Animation System
  - [ ] Copy all the code from Table Top Craft / Pandoras Creatures
  - [ ] Create better Animation State (make it a standalone class and add chainable methods to toggle which animations should start/stop)
  - [ ] Optimize Animation System
  - [ ] Add proper ghost model
  - [ ] Add better BlockEntity handling
  - [ ] Maybe add Item animations
  - [ ] Maybe add Player animations
- Click Location System (Abstract TTCs System)
  - Not sure yet how I want to make this...
- PaCo Screen
  - [ ] Add something into the "no mod selected" section (probably a general summary of found updates and such)
  - [ ] Replace PaCo's placeholder icon
  - [ ] Replace PaCo's placeholder background
  - [ ] Maybe add banner support
- PaCo Config Screen
  - [ ] Create config screen that can take any number of components and dynamically adjust to it
  - [ ] Hook mod config system into it
- Update Checker
  - [ ] Swap CompletableFuture system to an Update Checker Thread
  - [ ] Add automatic Modrinth update checker using modrinth's API (similar to what modmenu does)
- Registries
  - [x] Create PaCoRegistry to abstract all Object registration
  - [ ] Create more registry classes or a util class, to allow registering event based stuff (model layers, particles, e.t.c)
  - [ ] Maybe look into abstracting custom Registry registration
- Random Render Stuff
  - [ ] Create abstraction for BEWLERs
- Sounds
  - [x] Get sounds to play
  - [x] Figure out looped sounds
  - [x] ~~Maybe add some kind of system to adjust pitch/volume based on some value (e.g. distance to source).~~ One can use AbstractTickableSoundInstance to achieve this.
- Screen Shaker
  - [ ] Add a limit to how much the screen can shake
  - [ ] Maybe add a special override that allows bypassing the limit, might be handy for a few very select "shakes"
- Creative Tabs
  - [x] ~~Create builder pattern abstraction for creating creative tabs~~ The vanilla creative tab builder is fine (just needs a helper to create it more easily)
  - [x] Make a tool that allows inserting items into existing tabs
  - [x] Maybe add optional methods to "insert after" or "insert before"
- Shader Patcher
  - I dont understand enough about this system to write TODOs
