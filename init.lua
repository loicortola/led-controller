wifi.setmode(wifi.STATION)
wifi.sta.config("DarthVader", "loicortola")
wifi.sta.connect()
print("---------------------------")
print("Welcome to Loic's super MCU")
print("Connecting to Wifi")
tmr.alarm(1, 1000, 1, function()
 if wifi.sta.getip() == nil then
  print("IP unavailable, Waiting...")
 else
  tmr.stop(1)
  print("MAC address is: " .. wifi.ap.getmac())
  print("IP is " .. wifi.sta.getip())
  -- Init
  local frequency = 250
  pwm.setup(5, frequency, 0)
  pwm.start(5)
  pwm.setup(6, frequency, 0)
  pwm.start(6)
  pwm.setup(2, frequency, 0)
  pwm.start(2)
  
  -- Set to normal mode
  local mode, data
  local loader = loadfile("ledcontroller.lc")
  local ledcontroller = loader()
  
  if file.open("led.defaults", "r") then
   mode = tonumber(file.readline());
   if mode == 0 then
    data = ledcontroller.readcolor()
    ledcontroller.setcolor(data.r, data.g, data.b)
   elseif mode == 1 then
    data = ledcontroller.readanimation()
    ledcontroller.startanimation(data.r, data.g, data.b, data.looptime)
   end
  else
   ledcontroller.setcolor(255, 255, 255)
  end
  loader = nil
  ledcontroller = nil
  
  --dofile("server.lua")
 end
end)
