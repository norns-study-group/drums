-- drums

graphics = include("lib/graphics")
Lattice = include("lib/lattice")
GridKeys = include("lib/gridkeys")

local TRACK_COUNT = 7

local g = grid.connect()
local gridKeys = {}
local grid_dirty = false

function init()
  graphics.init()
  screen_dirty = true
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
<<<<<<< Updated upstream
=======
  tracks = {}
  for i = 1, TRACK_COUNT do
    local track = Track:new()
    track:set_pattern(fn.make_random_pattern())
    tracks[i] = track
  end
  tracks[1]:select()

  init_gridKeys()

>>>>>>> Stashed changes
  graphics_clock_id = clock.run(graphics_loop)
end



function do_drum_thing()
  print("do drum thing")
end

function increment_measure()
  measure = measure + 1
  screen_dirty = true
end

function redraw()
  graphics:setup()
  graphics:rect(1, 1, 7, 64, 15)
  graphics:text_rotate(7, 62, "DRUMS", -90, 0)
  graphics:text(64, 32, measure, 15)
  graphics:teardown()
end

function graphics_loop()
  while true do
    clock.sleep(1/15)
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
  print(k, z, "???")
  screen_dirty = true
end

function enc(e, d)
  print(e, d, "???")
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