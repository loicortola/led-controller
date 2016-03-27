local readcolor = function()
 local r, g, b
 if file.open("color.defaults", "r") then
  r = tonumber(file.readline());
  g = tonumber(file.readline());
  b = tonumber(file.readline());
 else
  r = 255
  g = 255
  b = 255
 end
 return { r = r, g = g, b = b }
end

local getcolor = function()
 return { r = pwm.getduty(5) / 4, g = pwm.getduty(6) / 4, b = pwm.getduty(2) / 4 }
end

local setcolor = function(r, g, b, dontsave)
 local duty

 duty = r * 4
 if duty < 20 then
  duty = 0
 elseif duty > 1000 then
  duty = 1023
 end
 pwm.setduty(5, duty)

 duty = g * 4
 if duty < 20 then
  duty = 0
 elseif duty > 1000 then
  duty = 1023
 end
 pwm.setduty(6, duty)

 duty = b * 4
 if duty < 20 then
  duty = 0
 elseif duty > 1000 then
  duty = 1023
 end
 pwm.setduty(2, duty)

 if dontsave == nil then
  -- Save changes to file
  file.open("led.defaults", "w+")
  file.writeline("0") --color mode
  file.flush()
  file.close()
  file.open("color.defaults", "w+")
  file.writeline(tostring(r))
  file.writeline(tostring(g))
  file.writeline(tostring(b))
  file.flush()
  file.close()
 end
end

local createloop = function(red, green, blue)
 local currentstate = 0

 return function()
  if currentstate == 0 then
   local v = pwm.getduty(2) / 4
   if v == blue then
    currentstate = 1
   elseif (v < 255) then
    pwm.setduty(2, (v + 1) * 4)
   end
  elseif currentstate == 1 then
   local v = pwm.getduty(5) / 4
   if (v == 0 or blue == 0) then
    currentstate = 2
   elseif (v > 0) then
    pwm.setduty(5, (v - 1) * 4)
   end
  elseif currentstate == 2 then
   local v = pwm.getduty(6) / 4
   if v == green then currentstate = 3
   elseif (v < 255) then
    pwm.setduty(6, (v + 1) * 4)
   end
  elseif currentstate == 3 then
   local v = pwm.getduty(2) / 4
   if (v == 0 or green == 0) then currentstate = 4
   elseif (v > 0) then
    pwm.setduty(2, (v - 1) * 4)
   end
  elseif currentstate == 4 then
   local v = pwm.getduty(5) / 4
   if v == red then currentstate = 5
   elseif (v < 255) then
    pwm.setduty(5, (v + 1) * 4)
   end
  elseif currentstate == 5 then
   local v = pwm.getduty(6) / 4
   if (v == 0 or red == 0) then currentstate = 0
   elseif (v > 0) then
    pwm.setduty(6, (v - 1) * 4)
   end
  end
 end
end

local createblinkloop = function(red, green, blue)
 local currentstate = 0

 return function()
  if currentstate == 1 then
   pwm.setduty(5, red)
   pwm.setduty(6, green)
   pwm.setduty(2, blue)
   currentstate = 0
  else
   pwm.setduty(5, 0)
   pwm.setduty(6, 0)
   pwm.setduty(2, 0)
   currentstate = 1
  end
 end
end

local readanimation = function()
 local r, g, b, looptime
 if file.open("animate.defaults", "r") then
  r = tonumber(file.readline())
  g = tonumber(file.readline())
  b = tonumber(file.readline())
  looptime = tonumber(file.readline())
 else
  r = 255
  g = 255
  b = 255
  looptime = 10000
 end
 return { r = r, g = g, b = b, looptime = looptime }
end

local stopanimation = function()
 local color = readcolor()
 -- Reset to previous color
 pwm.setduty(5, color.r)
 pwm.setduty(6, color.g)
 pwm.setduty(2, color.b)
 -- Stop Timer
 tmr.stop(0)
 -- Set fixed color to defaults
 file.open("led.defaults", "w+")
 file.writeline("0") --color mode
 file.flush()
 file.close()
end

local startanimation = function(r, g, b, looptime)
 -- Due to timer limitations, looptime cannot be under 8000
 if (looptime < 8000) then looptime = 8000
 elseif (looptime > 30000) then looptime = 30000
 end

 -- Maximum interval is looptime / (6 * 255)
 local steps = b * 2 + g * 2 + r * 2
 local interval = looptime / steps

 -- Initial state is red
 pwm.setduty(5, r)
 pwm.setduty(6, 0)
 pwm.setduty(2, 0)
 tmr.stop(0)
 tmr.alarm(0, interval, 1, createloop(r, g, b))

 -- Save changes to file
 file.open("led.defaults", "w+")
 file.writeline("1") --animate mode
 file.flush()
 file.close()
 file.open("animate.defaults", "w+")
 file.writeline(tostring(r))
 file.writeline(tostring(g))
 file.writeline(tostring(b))
 file.writeline(tostring(looptime))
 file.flush()
 file.close()

 createloop = nil
end

local startblink = function(r, g, b)
 local looptime = 1000

 -- Initial state is color
 pwm.setduty(5, r)
 pwm.setduty(6, g)
 pwm.setduty(2, b)
 tmr.stop(0)
 tmr.alarm(0, looptime, 1, createblinkloop(r, g, b))
end

local initpwm = function()
 -- Init
 local frequency = 250
 pwm.setup(5, frequency, 1023)
 pwm.start(5)
 pwm.setup(6, frequency, 1023)
 pwm.start(6)
 pwm.setup(2, frequency, 1023)
 pwm.start(2)
end

local loaddefaults = function()
 local mode, data
 if file.open("led.defaults", "r") then
  -- Read mode
  mode = tonumber(file.readline());
  if mode == 0 then
   data = readcolor()
   setcolor(data.r, data.g, data.b)
  elseif mode == 1 then
   data = readanimation()
   startanimation(data.r, data.g, data.b, data.looptime)
  end
 else
  setcolor(255, 255, 255)
 end
end

local switchonoff = function()
 local mode = 1
 if file.open("led.switch", "r") then
  -- Read mode
  mode = tonumber(file.readline());
  if mode == 0 then
   -- switch on
   loaddefaults()
   mode = 1
  elseif mode == 1 then
   -- switch off
   tmr.stop(0)
   setcolor(0, 0, 0, true)
   mode = 0
  end
 end

 -- Save changes to file
 file.open("led.switch", "w+")
 file.writeline(mode) --color mode
 file.flush()
 file.close()
end

local ison = function()
 if file.open("led.switch", "r") then
  return tonumber(file.readline()) == 1
 end
 return 0
end

return { initpwm = initpwm, loaddefaults = loaddefaults, switchonoff = switchonoff, ison = ison, startblink = startblink, readcolor = readcolor, readanimation = readanimation, getcolor = getcolor, setcolor = setcolor, stopanimation = stopanimation, startanimation = startanimation }