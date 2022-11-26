# kuxn
Experiments with emulating the [Uxn personal computing environment](https://100r.co/site/uxn.html) created by [100R](https://100r.co/site/about_us.html) using Kotlin, built live at https://twitch.tv/sebi_io!


## Notes from the stream

### Dev plan

- [x] How does the stack work?: The stack is separate from the main memory of the Uxn machine, and is 256 bytes in size, can't be addressed randomly, is fully managed by the CPU.
- [x] Implement minimal set of instructions (`LIT`, `ADD`, `DEO`, `BRK`)
- [x] Successfully run a first simple program / ROM
- Basic screen support
  - [ ] `DEO2` instruction
  - [ ] Screen visualization
  - [ ] Pixel support
  - [ ] Sprite support
    - [ ] 1bpp
    - [ ] 2bpp
- Basic system support
  - [ ] System colors


- Build immutable version of the `UxnMachine` using [kx.collections.immutable](https://github.com/Kotlin/kotlinx.collections.immutable)

### Important resources

- [Uxn main page](https://100r.co/site/uxn.html)
- [Varvara](https://wiki.xxiivv.com/site/varvara.html), the "computer" surrounding the Uxn CPU
- [Uxn tutorial, multi-part](https://compudanzas.net/uxn_tutorial_day_1.html)
- [CPU instruction tests](https://github.com/DeltaF1/uxn-instruction-tests)

### Minimal viable Varvara
Device Address `0x18` (Console Write)