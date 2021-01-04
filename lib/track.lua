local Track = {}

function Track:new() 
  local t = setmetatable({}, { 
    __index = Track,
    __tostring = function(t) return t:to_string() end
  })
  t.pattern = {}
  t.pattern_string = ""
  t.length = 8
  t.division = 1
  t.selected = false   -- for ui, not sequencing
  t.selected_index = 1 -- ditto
  return t
end

function Track:get_selected_index()
  return self.selected_index
end

function Track:set_selected_index(n)
  self.selected_index = util.clamp(n, 1, self:get_length())
end

function Track:to_string()
  return self.pattern_string
end

function Track:set_selected_index(n)
  self.selected_index = util.clamp(n, 1, #self.pattern)
end

function Track:change()
  local new_pattern = self.pattern
  new_pattern[self:get_selected_index()] = not new_pattern[self:get_selected_index()]
  self:set_pattern(new_pattern)
end

function Track:set_divsion(n)
  self.division = n
end

function Track:set_pattern(pattern)
  self.length = #pattern
  self.pattern = {}
  self.pattern_string = ""
  for i = 1, #pattern do
    self.pattern[i] = pattern[i]
    self.pattern_string = self.pattern_string .. (pattern[i] and "x" or "-")
  end
end

function Track:set_division(n)
  self.division = n
end

function Track:select()
  self.selected = true
end

function Track:deselect()
  self.selected = false
end

function Track:is_selected()
  return self.selected
end

function Track:get_length()
  return self.length
end

return Track