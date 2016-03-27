// Wrap AJAX Query into nice function
var request = function (url, method, callback, body) {
  var xhttp;
  if (window.XMLHttpRequest) {
    xhttp = new XMLHttpRequest();
  } else {
    // code for IE6, IE5
    xhttp = new ActiveXObject("Microsoft.XMLHTTP");
  }
  xhttp.onreadystatechange = function () {
    if (xhttp.readyState == 4) {
      if (xhttp.status == 200) {
        callback(null, xhttp.responseText);
      } else {
        callback({"statusCode": xhttp.status, "message": xhttp.responseText});
      }
    }
  };
  xhttp.open(method, url, true);
  xhttp.send(body);
};

// Called when form is posted
var postForm = function () {
  var body = {};
  body.ssid = document.getElementById("ssid").value;
  body.key = document.getElementById("key").value;
  body.password = document.getElementById("password").value;
  request("api/register", "POST", function (err, result) {
    if (err) {
      document.getElementById("bad-news").innerHTML = "An error occured during registration. Please check all fields are valid, and try again.<br/>Message:" + err.message;
    } else {
      document.getElementById("good-news").innerHTML = "Registration successful. Your device will now restart...";
      request("api/restart", "POST", function(err, result) {
        console.log("Device Restart: " + !err);
      })
    }
  }, JSON.stringify(body));
};

// Request SSIDS
request("api/ssids", "GET", function (err, result) {
  document.getElementById("wap").innerHTML = result;
  document.getElementById("submit").removeAttribute("disabled");
});