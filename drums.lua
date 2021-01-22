-- k1: exit   e1: ???
--
--         e2: y     e3: x
--    k2: change   k3: ???
--
fn = include("lib/functions")
graphics = include("lib/graphics")
Track = include("lib/track")
Lattice = include("lib/lattice")
GridKeys = include("lib/gridkeys")


local TRACK_COUNT = 7

local g = grid.connect()
local gridKeys = {}
local grid_dirty = false


function init()
engine.load("Drums", no_really_init)
  graphics.init()
  screen_dirty = true
  selected_track = 1
  measure = 0
  lattice = Lattice:new()
  whole_notes = lattice:new_pattern{
    division = 0.05,
    callback = function(t)
      increment_measure()
      do_drum_thing()
    end
}

  pew = 3
  pewpew = lattice:new_pattern{
    division = 0.40,
    callback = do_pewpew
  }
  
  heavy_metal = lattice:new_pattern{
      division = 0.2,
      callback = function ()
          engine.map_param(5, "decay", math.random())
          engine.map_param(pew, "dewey", 0.8 + 0.15 * math.random())
          engine.trigger(5)
      end
  }

  tracks = {}
  for i = 1, TRACK_COUNT do
    local track = Track:new()
    track:set_pattern(fn.make_random_pattern())
    tracks[i] = track
  end
  tracks[1]:select()

  init_gridKeys()

  graphics_clock_id = clock.run(graphics_loop)
end

function no_really_init()
  setup_drums()
  lattice:start()
end

function setup_drums()
  engine.pick_synth(0, "whip-snare")
  engine.pick_synth(1, "sinfb-kick")
  engine.pick_synth(2, "noisehat")

  engine.pick_synth(3, "ez")
  engine.pick_synth(4, "ez")
  
  engine.pick_synth(5, "metallic")
  
  engine.add_forward(3, 2)
  engine.add_forward(4, 2)
end



function do_drum_thing()
-- this is also pretty silly. probably attach to some sequncer or something
  

  if math.random() > 0.5 then
      if math.random() > 0.6 then
        engine.map_param(1, "pan", util.linlin(0,1,-1,1,math.random()))
        engine.map_param(1, "decay", util.linlin(0,1,0.3,1.0,math.random()))
        engine.map_param(1, "vel", math.random() * 0.6)

        engine.trigger(1)
        -- engine.pick_synth(0, "whip-snare")
      elseif math.random() > 0.2 then
        engine.map_param(2, "vel", (1 + math.random()) / 4)
        engine.map_param(2, "attack", math.random() * 0.05)
        engine.map_param(2, "decay", math.random() * 0.5)
        engine.trigger(2)
      else
        engine.map_param(0, "pan", util.linlin(0,1,-1,1,math.random()))
      -- engine.map_param(0, "slap", util.linlin(0,1,0.5,4,math.random()))
      -- engine.map_param(0, "slap", 5)

        engine.map_param(0, "decay", 0.05)
        engine.map_param(0, "heft", util.linlin(0,1,0.8,2,math.random()))
        engine.map_param(0, "vel", 1)
        engine.map_param(0, "dewey", 0.2 * math.random())
        engine.map_param(0, "system", 0.6 * math.random())

        engine.trigger(0)
        -- engine.pick_synth(0, "sinfb-kick")
      end
  end
  
  -- engine.trigger(0)
--   print("do drum thing")
end

function do_pewpew()
    print("PEW" .. pew)
    engine.set_param(pew, "hz", 
    (pew == 3 and 220 or 330) + math.random() * 3)
    engine.set_param(pew, "pan", pew == 3 and -1 or 1)
    
    engine.map_param(pew, "dewey", 0.7 + 0.25 * math.random())
    
    engine.map_param(pew, "slap", math.random() / 5)
    
    engine.trigger(pew)
    pew = 7 - pew
    if math.random() < 0.2 then
      local new_div = math.random() / 2 + 0.1
      print("WATCH OUT we got a new pewpew " .. new_div)
      pewpew.division = new_div
    end
end

function increment_measure()
  measure = measure + 1
  screen_dirty = true
end

function redraw()
  graphics:setup()
  graphics:draw_title()
  graphics:draw_tracks()
  graphics:teardown()
end

function graphics_loop()
  while true do
    clock.sleep(1/15)
    graphics:modulate()
    if screen_dirty then
      redraw()
      screen_dirty = false
    end

    -- animates gridKeys and redraws grid if dirty
    if gridKeys:animate() or grid_dirty then
      grid_redraw()
    end
  end
end

function key(k, z)
  if z == 0 then return end
  if k == 2 then
    fn.get_selected_track():change()
  elseif k == 3 then

  end
  screen_dirty = true
end

function enc(e, d)
  if e == 2 then
    fn.select_track(d)
  elseif e == 3 then
    fn.select_track_index(d)
  end
  screen_dirty = true
end

function cleanup()
  lattice:destroy()
  clock.cancel(graphics_clock_id)
end

-- GRID --
function init_gridKeys()
  -- setup gridkeys to play drums with grid
  gridKeys = GridKeys.new(16,8)
  gridKeys.vertical_offset = 0
  gridKeys.sound_mode = 3 -- kit mode
  gridKeys.layout_mode = 3 -- kit layout
  gridKeys.note_on = grid_note_on
  gridKeys.note_off = grid_note_off
  gridKeys.key_pushed = grid_key_pushed

  for i = 1, TRACK_COUNT do
    gridKeys.kit_has_sample[i] = 1
  end

  grid_dirty = true
end

g.key = function(x,y,z)
  grid_dirty = false
  
  grid_dirty = gridKeys:grid_key(x,y,z)

  if grid_dirty then
      grid_redraw()
  end
end

function grid_redraw()
  g:all(0)

  gridKeys:draw_grid(g)

  g:refresh()
end

function grid_note_on(gKeys, noteNum, vel)
  vel = vel or 100 

  if gKeys.sound_mode == 1 then -- play internal note, could be used to play drum track chromatically or in-scale
  elseif gKeys.sound_mode == 2 then -- midi out
  elseif gKeys.sound_mode == 3 then -- internal kit
    local track_id = noteNum + 1

    local noteInScale = gKeys:is_note_in_scale(noteNum)

    print("Play Drum "..track_id.." In scale: "..(noteInScale and "true" or "false"))
  end
end

function grid_note_off(gKeys, noteNum)
  print("Note Off: " .. noteNum)

  if gKeys.sound_mode == 1 then
  elseif gKeys.sound_mode == 2 then
  elseif gKeys.sound_mode == 3 then -- internal kit
  end
end

function grid_key_pushed(gKeys, noteNum, vel)
  if gKeys.sound_mode == 3 then -- internal kit
      -- print("Change selected drum: "..noteNum)
      local track_id = noteNum + 1

      fn.select_track_by_id(track_id)
  end
end
-- END GRID --