print("Server.lua : " .. tostring(node.heap()))
do
 local espress = require 'espress'
 local port = 80
 print("Server.lua after http : " .. tostring(node.heap()))

 pwm.setup(1, 250, 1023)
 pwm.start(1)
 pwm.setup(2, 250, 1023)
 pwm.start(2)
 pwm.setup(3, 250, 1023)
 pwm.start(3)

 local server = espress.createserver(port)
 server:use("auth_api_key.lua", {apikey = "1234", includes = "/api"})
 server:use("routes_auto.lua")
end
