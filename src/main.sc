import bottle
import radl.version-string
using import print
using import radl.strfmt
using import String

VERSION := (radl.version-string.git-version)
run-stage;

@@ 'on bottle.configure
fn (cfg)
    cfg.window.title = f"gun game (${VERSION})"

@@ 'on bottle.load
fn ()
    import C.stdio

    renderer-info := (bottle.gpu.get-info)
    print f"gun game version: ${VERSION}"
    print f"bottle version: ${(bottle.get-version)}"
    print renderer-info.APIString
    print renderer-info.GPUString
    ()

@@ 'on bottle.key-pressed
fn (key)
    using bottle.enums
    if (key == KeyboardKey.Escape)
        bottle.quit!;

@@ 'on bottle.update
fn (dt)

@@ 'on bottle.render
fn ()

sugar-if main-module?
    bottle.run;
else
    fn main (argc argv)
        bottle.run;
        0
