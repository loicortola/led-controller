local addheader = function(req, name, value)
 local h = req.headers
 h[name] = value
end

local createrequest = function(conn, line)
 print(line)
 -- Here, we parse the first line
 -- NB: just version 1.1 assumed
 local _, method, url, querystring, url_no_qs, params
 -- parse method and url
 _, _, method, url = line:find("^([A-Z]+) (.-) HTTP/1.1$")
 -- parse querystring
 _, _, url_no_qs, querystring = url:find("([^%s]+)%?([^%s]+)")

 if url_no_qs then
  url = url_no_qs
 end
 -- parse params
 params = {}
 if querystring then
  for name, value in string.gfind(querystring, "([^&=]+)=([^&=]+)") do
   params[name] = value
  end
 end
 -- Return request object
 return {
  conn = conn,
  method = method,
  body = "",
  headers = {},
  addheader = addheader,
  url = url,
  params = params
 }
end

return createrequest
