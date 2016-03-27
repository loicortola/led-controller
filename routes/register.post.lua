return function(req, res)
 local success, json = pcall(cjson.decode, req.body)
 if not success then
  error(json)
 end
 -- Decode json object from body
 print("SSID is " .. json.ssid)
 print("Key is " .. json.key)
 print("Password is " .. json.password)
 local wificontroller = loadfile("service/wificontroller.lc")()
 wificontroller.savesettings({ssid = json.ssid, key = json.key, password = json.password})
 res:send()
end