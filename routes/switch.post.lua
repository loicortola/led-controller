return function(req, res)
 local loader = loadfile("service/ledcontroller.lc")
 local ledcontroller = loader()
 -- Load color
 ledcontroller.switchonoff()
 res:addheader("Content-Type", "application/json; charset=utf-8")
 res:send()
end