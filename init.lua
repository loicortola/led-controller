local wificontroller = loadfile("service/wificontroller.lc")() 
local ledcontroller = loadfile("service/ledcontroller.lc")()

-- Default conf for wifi
wifi.ap.dhcp.stop()
wifi.sleeptype(wifi.NONE_SLEEP)
wifi.setphymode(wifi.PHYMODE_N)

-- Init PWM
ledcontroller.initpwm()
-- Load default colors
ledcontroller.loaddefaults()

local error = function()
 -- Too many attempts: Failed
 tmr.stop(1)
 ledcontroller.stopanimation()
 -- Blink red
 ledcontroller.startblink(255, 0, 0)
 -- Remove settings
 file.remove("wifi.settings")
end

if file.open("wifi.settings", "r") then
 -- Wifi settings exist. Start system
 local cred = wificontroller.readcredentials()
 
 -- Set mode Station
 wifi.setmode(wifi.STATION)
 print("------------------------------")
 print("LED CONTROLLER by Loic Ortola")
 print("------------------------------")
 print("Connect attempt to SSID: '" .. cred.ssid .. "'")
 success, res = pcall(wifi.sta.config, cred.ssid, cred.key)
 if success then
  wifi.sta.connect()
 else
  error()
  return
 end
 local attempts = 0
 ledcontroller.startblink(255, 165, 0)
 tmr.alarm(1, 1000, 1, function()
  attempts = attempts + 1
  if wifi.sta.getip() == nil then
   print("IP unavailable, Waiting...")
  else
   tmr.stop(1)
   print("MAC address is: " .. wifi.ap.getmac())
   print("IP is " .. wifi.sta.getip())
   ledcontroller.stopanimation()
   -- Load default colors
   ledcontroller.loaddefaults()
   -- Register Multicast DNS
   mdns.register("led-" .. node.chipid(), "http", 80)
   dofile("server.lc")
  end
  if attempts > 30 then
   error()
  end
 end)
else
 print("No wifi settings. Setup Mode")
 -- Initial color code is blue => waiting for setup
 wificontroller.setupAP()
 dofile("server_setup.lc")
 ledcontroller.startblink(0, 0, 255)
end