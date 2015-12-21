local HttpServer = require 'httpserver'
do
    local port=80
    pwm.setup(1, 250, 1023)
    pwm.start(1)
    pwm.setup(2, 250, 1023)
    pwm.start(2)
    pwm.setup(3, 250, 1023)
    pwm.start(3)
    
    local server = HttpServer:createServer({port = port})
    server.router.post("/color", function(req, res)
        if request.query["red"] then
            local duty = 1023 - tonumber(request.query["red"]) * 4;
            if duty < 10 then
                duty = 0
            end
            print("Duty is " .. tostring(duty))
            pwm.setduty(1, duty)
        end
        if request.query["green"] then
            local duty = 1023 - tonumber(request.query["green"]) * 4;
            if duty < 10 then
                duty = 0
            end
            print("Duty is " .. tostring(duty))
            pwm.setduty(2, duty)
        end
        if request.query["blue"] then
            local duty = 1023 - tonumber(request.query["blue"]) * 4;
            if duty < 10 then
                duty = 0
            end
            print("Duty is " .. tostring(duty))
            pwm.setduty(3, duty)
        end
        res:addHeader("Content-Type", "application/json; charset=utf-8")
        res:send("{red:" .. 255 - pwm.getduty(1) / 4 .. ", green:" .. 255 - pwm.getduty(2) / 4 .. ", blue:" .. 255 - pwm.getduty(3) / 4 .. "}")
    end)
    
    server.router.get("/color", function(req, res)
        res:addHeader("Content-Type", "application/json; charset=utf-8")
        res:send("{red:" .. 255 - pwm.getduty(1) / 4 .. ", green:" .. 255 - pwm.getduty(2) / 4 .. ", blue:" .. 255 - pwm.getduty(3) / 4 .. "}")
    end)
    
    server.router.post("/animate", function(req, res)
        if request.query["from"] then
            local duty = 1023 - tonumber(request.query["red"]) * 4;
            if duty < 10 then
                duty = 0
            end
            print("Duty is " .. tostring(duty))
            pwm.setduty(1, duty)
        end
        if request.query["to"] then
            local duty = 1023 - tonumber(request.query["green"]) * 4;
            if duty < 10 then
                duty = 0
            end
            print("Duty is " .. tostring(duty))
            pwm.setduty(2, duty)
        end
        res:addHeader("Content-Type", "application/json; charset=utf-8")
        res:send("{red:" .. 255 - pwm.getduty(1) / 4 .. ", green:" .. 255 - pwm.getduty(2) / 4 .. ", blue:" .. 255 - pwm.getduty(3) / 4 .. "}")
    end)
end
