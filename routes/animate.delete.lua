return function(req, res)
 -- Reset to previous color
 local loader = loadfile("ledcontroller.lc")
 local ledcontroller = loader()

 ledcontroller.stopanimation()
 res:send()
end