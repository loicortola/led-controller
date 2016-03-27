return function(req, res)
 res:send()
 local r = 0
 local g = 0
 local b = 0
 -- Loop time in milli-seconds
 local looptime = 20000
 if req.params.red then
  r = tonumber(req.params.red)
 end
 if req.params.green then
  g = tonumber(req.params.green)
 end
 if req.params.blue then
  b = tonumber(req.params.blue)
 end
 if req.params.loopTime then
  looptime = tonumber(req.params.loopTime)
 end
 
 local loader = loadfile("service/ledcontroller.lc")
 local ledcontroller = loader()
 
 ledcontroller.startanimation(r, g, b, looptime)
 
end