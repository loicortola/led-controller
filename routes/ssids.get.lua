local get = function(req, res)
 local result = "<select name=\"ssid\" id=\"ssid\">"
 tmr.wdclr()
 wifi.sta.getap(function(aps)
  tmr.wdclr()
  for ssid, v in pairs(aps) do
   local authmode, rssi, bssid, channel = string.match(v, "([^,]+),([^,]+),([^,]+),([^,]+)")
   result = result .. "<option value=\"" .. ssid .. "\"> " .. ssid .. "</option>"
  end
  result = result .. "</select>"
  tmr.wdclr()
  res:addheader("Content-Type", "text/html; charset=utf-8")
  res:send(result)
 end)
 
end
return get