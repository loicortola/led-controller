do
 local espress = require 'espress'
 local port = 80
 print("Heap after http : " .. tostring(node.heap()))
 
 local server = espress.createserver()
 local wificontroller = loadfile("service/wificontroller.lc")()
 server:use("auth_api_key.lc", {apikey = wificontroller.readcredentials().password, excludes = "/health"})
 server:use("routes_custom.lc")
 server:listen(80)
end