return function(req, res)
 local loader = loadfile("service/ledcontroller.lc")
 local ledcontroller = loader()
 res:addheader("Content-Type", "application/json; charset=utf-8")
 -- Load state
 if ledcontroller.ison() then
 res:send("true")
 else
  res:send("false")
 end
end