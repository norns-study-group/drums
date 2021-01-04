local fn = {}

function fn.get_selected_track()
  return tracks[selected_track]
end

function fn.select_track(d)
  fn.get_selected_track():deselect()
  selected_track = util.clamp(selected_track + d, 1, #tracks)
  fn.get_selected_track():select()
  fn.get_selected_track():set_selected_index(1)
end

function fn.select_track_by_id(id)
  if selected_track == id then return end
  fn.get_selected_track():deselect()
  selected_track = util.clamp(id, 1, #tracks)
  fn.get_selected_track():select()
  fn.get_selected_track():set_selected_index(1)
end

function fn.select_track_index(d)
  fn.get_selected_track():set_selected_index(fn.get_selected_track():get_selected_index() + d)
end

function fn.make_random_pattern(n)
  local n = n ~= nil and n or math.random(3, 16)
  local out = {}
  for i = 1, n do
    table.insert(out, math.random(1, 2) == 1)
  end
  return out
end

function rerun()
  norns.script.load(norns.state.script)
end

function r()
  norns.script.load(norns.state.script)
end

return fn