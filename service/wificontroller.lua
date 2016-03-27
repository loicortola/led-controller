local lcprefix = "led-"

local readcredentials = function()
 local ssid, key, password
 if file.open("wifi.settings", "r") then
  ssid = string.gsub(file.readline(), "\n", "");
  key = string.gsub(file.readline(), "\n", "");
  password = string.gsub(file.readline(), "\n", "");
  return { ssid = ssid, key = key, password = password }
 end
 return nil
end

local setupAP = function()
 print("Setting up Access Point")
 wifi.setmode(wifi.STATIONAP)
 local cfg = {}
 local mac = wifi.ap.getmac()
 print("Mac is: " .. mac)
 cfg.ssid = lcprefix .. mac
 cfg.password = "dummypass"
 cfg.auth = wifi.AUTH_OPEN
 cfg.channel = 10
 -- Init config
 wifi.ap.config(cfg)
 -- Init AP IP
 cfg = {
  ip = "10.0.0.1",
  netmask = "255.255.255.0",
  gateway = "10.0.0.1"
 }
 wifi.ap.setip(cfg)
 -- Init AP DHCP config
 cfg = {}
 cfg.start = "10.0.0.2"
 wifi.ap.dhcp.config(cfg)
 wifi.ap.dhcp.start()
end

local savesettings = function(conf)
 -- Save changes to file
 file.open("wifi.settings", "w+")
 file.writeline(conf.ssid)
 file.writeline(conf.key)
 file.writeline(conf.password)
 file.flush()
 file.close()
end

return { setupAP = setupAP, readcredentials = readcredentials, savesettings = savesettings }