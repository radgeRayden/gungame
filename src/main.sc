import radl.version-string
VERSION := (radl.version-string.git-version) as string
run-stage;

using import radl.strfmt
using import glm print String struct
import bottle
using import .assets

global player :
    struct PlayerCharacter plain
        direction : vec2
        position : vec2
        speed : f32 = 250:f32

@@ 'on bottle.configure
fn (cfg)
    cfg.window.title = f"gun game (${VERSION})"
    cfg.gpu.msaa-samples = 4

@@ 'on bottle.load
fn ()
    print f"gun game version: ${VERSION}"
    print f"bottle version: ${(bottle.get-version)}"

    renderer-info := (bottle.gpu.get-info)
    print renderer-info.APIString
    print renderer-info.GPUString

    load-all-resources;

    input-layer := (bottle.input.new-layer)

    'define-axis input-layer "moveH"
    'map-action input-layer "moveH"
        fn (value)
            mag := abs value
            if (mag != 0 and mag < 0.1)
                return; # deadzone hack
            elseif (mag < 0.5)
                player.direction.x = 0
            else
                player.direction.x = value

    'define-axis input-layer "moveV"
    'map-action input-layer "moveV"
        fn (value)
            mag := abs value
            if (mag != 0 and mag < 0.1)
                return; # deadzone hack
            elseif (mag < 0.5)
                player.direction.y = 0
            else
                player.direction.y = -value

    using bottle.enums
    'bind-to-axis input-layer "moveH" ControllerAxis.LeftX
    'bind-to-axis input-layer "moveH" ControllerButton.Left -1:f32
    'bind-to-axis input-layer "moveH" ControllerButton.Right 1:f32
    'bind-to-axis input-layer "moveV" ControllerAxis.LeftY
    'bind-to-axis input-layer "moveV" ControllerButton.Down 1:f32
    'bind-to-axis input-layer "moveV" ControllerButton.Up -1:f32
    ()

@@ 'on bottle.key-pressed
fn (key)
    using bottle.enums
    if (key == KeyboardKey.Escape)
        bottle.quit!;

@@ 'on bottle.update
fn (dt)
    dir := ((length player.direction) > 0) (normalize player.direction) (vec2)
    player.position += dir * player.speed * (f32 dt)

@@ 'on bottle.render
fn ()
    from bottle let plonk
    let atlas =
        try ('get game-resources.sprite-sheets S"assets/sprites/characters.png")
        else (assert false)

    size := (('get-quad-size atlas) * 8)
    position := floor (player.position + (size / 2))
    plonk.sprite atlas position size 0:f32 ('get-quad atlas 5 2)
    # collision
    plonk.circle-line position (size.x / 2)
        color = (vec4 0 1 0 1)

sugar-if main-module?
    bottle.run;
else
    fn main (argc argv)
        raising noreturn
        bottle.run;
        0
