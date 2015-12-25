do
 local get = function(req, res)
  res:addheader("Content-Type", "application/json; charset=utf-8")
  res:send("{red:" .. 255 - pwm.getduty(1) / 4 .. ", green:" .. 255 - pwm.getduty(2) / 4 .. ", blue:" .. 255 - pwm.getduty(3) / 4 .. "}")
 end
 return get
end