# kuxn
Experiments with emulating the [Uxn personal computing environment](https://100r.co/site/uxn.html) created by [100R](https://100r.co/site/about_us.html) using Kotlin, built live at https://twitch.tv/sebi_io!


## Notes from the stream

### Dev plan

- How does the stack work?
- Implement minimal set of instructions (`LIT`, `ADD`, `DEO`)
- Build immutable version of the `UxnMachine` using [kx.collections.immutable](https://github.com/Kotlin/kotlinx.collections.immutable)

### Important resources

- [Uxn main page](https://100r.co/site/uxn.html)
- [Varvara](https://wiki.xxiivv.com/site/varvara.html), the "computer" surrounding the Uxn CPU
- [Uxn tutorial, multi-part](https://compudanzas.net/uxn_tutorial_day_1.html)
- [CPU instruction tests](https://github.com/DeltaF1/uxn-instruction-tests)

### Minimal viable Varvara
Device Address `0x18` (Console Write)