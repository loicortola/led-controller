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
  print(node.heap())
  -- dofile("server.lua")
 end
end)
