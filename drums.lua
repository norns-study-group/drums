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

engine.name = "Drums"

local TRACK_COUNT = 7

local g = grid.connect()
local gridKeys = {}
local grid_dirty = false

function init()
  graphics.init()
  screen_dirty = true
  selected_track = 1
  measure = 0
  lattice = Lattice:new()
  whole_notes = lattice:new_pattern{
    division = 1,
    callback = function(t)
      increment_measure()
      do_drum_thing()
    end
  }
  lattice:start()

  tracks = {}
  for i = 1, TRACK_COUNT do
    local track = Track:new()
    track:set_pattern(fn.make_random_pattern())
    tracks[i] = track
  end
  tracks[1]:select()

  init_gridKeys()

  graphics_clock_id = clock.run(graphics_loop)
  
-- this is pretty silly. probably attach to some menu thing or something
--   engine.pick_synth(0, "ez")
--   engine.pick_synth(0, "sinfb-kick")
  engine.pick_synth(0, "trig-test")
--   engine.map_param(0, "vel", (1 + math.random()) / 2)
end



function do_drum_thing()
-- this is also pretty silly. probably attach to some sequncer or something
  engine.map_param(0, "pan", math.random())
  engine.map_param(0, "slap", math.random())
  engine.map_param(0, "vel", (1 + math.random()) / 2)
  engine.trigger(0)
--   print("do drum thing")
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