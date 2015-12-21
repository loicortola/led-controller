local HttpServer = {}
do 
    
    
    -- start a web server.  Return the web server object
    function HttpServer:createServer(cfg)
      print("Launching web-server")
      -- define member variables
      local config = cfg
      local routes = {}
      local server = require("http").createServer(config.port, function (req, res)
          print("Request received: ", req.method, req.url)
          if routes[req.url] then
            routes[req.url][req.method](req, res)
          else
            res:addHeader("Content-Type", "application/json; charset=utf-8")
            res:send("{url: \"" .. req.url .. "\", message:\"Not Found.\"}", 404)
          end
        end)
    
      local addHandler = function(url, method, callback)
        if not (routes[url]) then
            routes[url] = {}
        end
        routes[url][method] = callback
      end
      local get = function(url, callback)
        addHandler(url, 'GET', callback)
      end
      local post = function(url, callback)
        addHandler(url, 'POST', callback)
      end
      local put = function(url, callback)
        addHandler(url, 'PUT', callback)
      end
      local delete = function(url, callback)
        addHandler(url, 'DELETE', callback)
      end
      local options = function(url, callback)
        addHandler(url, 'OPTIONS', callback)
      end
      local head = function(url, callback)
        addHandler(url, 'HEAD', callback)
      end
      print("Server listening on port "..tostring(config.port))
      -- return functions
      return {server= server, router= {get= get, post= post, put= put, delete= delete, options= options, head= head}}
    end
end

return HttpServer