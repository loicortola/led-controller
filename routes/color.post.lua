do
 local post = function(req, res)
  if req.params.red then
   local duty = 1023 - tonumber(req.params.red) * 4;
   if duty < 10 then
    duty = 0
   end
   print("Duty is " .. tostring(duty))
   pwm.setduty(1, duty)
  end
  if req.params.green then
   local duty = 1023 - tonumber(req.params.green) * 4;
   if duty < 10 then
    duty = 0
   end
   print("Duty is " .. tostring(duty))
   pwm.setduty(2, duty)
  end
  if req.params["blue"] then
   local duty = 1023 - tonumber(req.params["blue"]) * 4;
   if duty < 10 then
    duty = 0
   end
   print("Duty is " .. tostring(duty))
   pwm.setduty(3, duty)
  end
  res:addheader("Content-Type", "application/json; charset=utf-8")
  res:send("{red:" .. 255 - pwm.getduty(1) / 4 .. ", green:" .. 255 - pwm.getduty(2) / 4 .. ", blue:" .. 255 - pwm.getduty(3) / 4 .. "}")
 end
 return post
end