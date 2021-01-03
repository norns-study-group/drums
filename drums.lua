-- k1: exit   e1: ???
--
--         e2: y     e3: x
--    k2: change   k3: ???
--
fn = include("lib/functions")
graphics = include("lib/graphics")
Track = include("lib/track")
Lattice = include("lib/lattice")

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
  for i = 1, 7 do
    local track = Track:new()
    track:set_pattern(fn.make_random_pattern())
    tracks[i] = track
  end
  tracks[1]:select()
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