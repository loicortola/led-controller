do
 local espress = require 'espress'
 local port = 80
 print("Heap after http : " .. tostring(node.heap()))
 
 local server = espress.createserver(port)
 server:use("auth_api_key.lc", {apikey = "799f7400-1612-4994-b84d-19e205f9eff9"})
 server:use("routes_custom.lc")
end