print("Welcome to Loic's super MCU")
print("Connecting to Wifi")

uart.setup(0, 9600, 8, 0, 1, 0)
wifi.setmode(wifi.STATION)
wifi.sta.config("DarthVader", "loicortola")
wifi.sta.connect()
 tmr.alarm(1, 1000, 1, function()
  if wifi.sta.getip()== nil then
  print("IP unavailable, Waiting...")
 else
  tmr.stop(1)
 print("MAC address is: " .. wifi.ap.getmac())
 print("IP is "..wifi.sta.getip())
 dofile ("server.lua")
 end
 end)