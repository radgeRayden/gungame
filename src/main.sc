import radl.version-string
VERSION := (radl.version-string.git-version)
run-stage;

using import radl.strfmt
using import glm print String struct
import bottle
import .assets

@@ 'on bottle.configure
fn (cfg)
    cfg.window.title = f"gun game (${VERSION})"
    cfg.gpu.msaa-samples = 4
    # cfg.filesystem.root = ".."

@@ 'on bottle.load
fn ()
    renderer-info := (bottle.gpu.get-info)
    print f"gun game version: ${VERSION}"
    print f"bottle version: ${VERSION}"
    print renderer-info.APIString
    print renderer-info.GPUString

    assets.load-all-resources;
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
        raising noreturn
        bottle.run;
        0
