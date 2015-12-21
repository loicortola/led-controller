local routes = {}
do
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
    handler = function(req, res)
        print("Request received: ", req.method, req.url)
          if routes[req.url] then
            routes[req.url][req.method](req, res)
          else
            res:statusCode(404)
            res:send()
          end
    end
end
return {handler = handler, get= get, post= post, put= put, delete= delete, options= options, head= head}