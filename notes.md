Iris compat:
- [Shadow map stuff](https://github.com/IrisShaders/Iris/blob/6c20dcd953f86b5f774abf0857ce9ecf28b44618/src/main/java/net/irisshaders/iris/pipeline/IrisRenderingPipeline.java#L743)

Jank:
- starting a game of chess creates triangles
- Investigate if spectator mode is broken (found on fabric). Basically looking diagonally and holding 'W' resulted in sideways movement instead of diagonal (maybe not a bug and just vanilla jank?).

Systems TODO:
- Config System
- NetworkSystem
- Animation System
- Click Location System (Abstract TTCs System)
