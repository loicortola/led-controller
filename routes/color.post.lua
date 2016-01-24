return function(req, res)
 tmr.stop(0)
 local r = -1
 local g = -1
 local b = -1
 
 if req.params.red and req.params.green and req.params.blue then
  r = tonumber(req.params.red);
  g = tonumber(req.params.green);
  b = tonumber(req.params.blue);
  local loader = loadfile("ledcontroller.lc")
  local ledcontroller = loader()

  ledcontroller.setcolor(r, g, b)
 end
 
 
 res:addheader("Content-Type", "application/json; charset=utf-8")
 res:send()
end