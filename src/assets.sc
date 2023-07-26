using import Array enum Map print Rc String struct
import bottle
using bottle.types

enum AtlasAspect plain
    Grid
    Packed

struct SpriteAtlasRecord
    path : String
    aspect : AtlasAspect = AtlasAspect.Grid
    columns : i32
    rows : i32

struct AssetMetadata
    sprites : (Array SpriteAtlasRecord)

global current-root : String
global asset-metadata : AssetMetadata
vvv bind matching-context
do
    using import .record-matcher
    sugar set-root (path)
        current-root = String (path as string)
        ()

    sugar atlas (body...)
        'append asset-metadata.sprites
            record-matcher SpriteAtlasRecord body...
        ()

    indirect-let list-handler-symbol = list-handler
    local-scope;
run-stage;

fn load-asset-metadata ()
    try
        assets := FileStream "assets/assets.sln" FileMode.Read
        txt := 'read-all-string assets
        try
            metadata := (sc_parse_from_string (txt as string))
            sc_expand metadata '() matching-context
        except (ex)
            print
                'dump ex
            assert false "not able to parse asset metadata"
    else
        assert false "unable to load asset metadata"
    asset-metadata

struct SpriteAtlas
    aspect : AtlasAspect
    columns : i32
    rows : i32
    texture : Texture

struct GameResources
    sprite-sheets : (Map String (Rc SpriteAtlas))

global game-resources : GameResources

fn load-atlas (metadata)
    fullpath := current-root .. "/" .. metadata.path
    try
        imgdata := bottle.asset.load-image fullpath
        texture := Texture imgdata
        'set game-resources.sprite-sheets fullpath
            Rc.wrap
                SpriteAtlas
                    copy metadata.aspect
                    copy metadata.columns
                    copy metadata.rows
                    texture
    else (print "unable to load sprite atlas:" fullpath)

# this looks unnecessarily "decoupled", I plan to expand this stuff later and it doesn't make things
# too bad to do it this way right away.
fn load-all-resources ()
    asset-metadata := (load-asset-metadata)
    for sprite-record in asset-metadata.sprites
        load-atlas sprite-record

do
    let load-all-resources game-resources
    local-scope;
