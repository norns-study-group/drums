-- drums

graphics = include("lib/graphics")
Lattice = include("lib/lattice")

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
  end
end

function key(k, z)
  if z == 0 then return end
  print(k, z, "???")
  song.is_screen_dirty = true
end

function enc(e, d)
  print(e, d, "???")
  song.is_screen_dirty = true
end

function cleanup()
  lattice:destroy()
  clock.cancel(graphics_clock_id)
end