var SensorTag = require('sensortag');
var pushDelay = 3000;
var opcuaAddress = process.argv[2]; //"opc.tcp://localhost:4840";

var data =
    {
        "pressure": 0,
        "humidity": 0,
        "humidity_t": 0,       
        // "light": 0,             
        ts: new Date().getTime()
    };

SensorTag.discover(function (st) {
    st.on('disconnect', function () {
        console.log('sensor disconnected!');
        process.exit(0);
    });
    function update(field, value) {
        if (data[field] != value) {
            console.log("update: " + field + "=" + value);
            data[field] = value;
            var ts = new Date().getTime();
            if (ts - pushDelay > data.ts) {
                data.ts = ts;

            }
        }
    }
    function init() {
        console.log("connect to and setup sensortag " + st.id);
        st.connectAndSetUp(setup);

    }
    function setup() {
        console.log("enable humidity sensor ...");
        st.enableHumidity(humidityC);
        console.log("enable barometric pressure sensor ...");
        st.enableBarometricPressure(pressureC);
        console.log("enable luxometer ...");
        st.enableLuxometer(luxC);

    }
    function humidityC() {
        st.notifyHumidity(function () {
            st.on('humidityChange', function (temperature, humidity) {
	      var ts = new Date().getTime();
	      if (ts - pushDelay > data.ts) {
		  data.ts = ts;
		  sendOPCUA('uawrite -u "'+opcuaAddress +'" -n "ns=1;i=2004" ' +temperature);
		  console.log("Temperature: "+temperature);
		  sendOPCUA('uawrite -u "'+opcuaAddress +'" -n "ns=1;i=2003" ' +humidity);
		  console.log("Humidity: "+humidity);
	      }
            });
        });
    }
    function pressureC() {
        st.notifyBarometricPressure(function () {
            st.on('barometricPressureChange', function (pressure) {
	      var ts = new Date().getTime();
	      if (ts - pushDelay > data.ts) {
		data.ts = ts;
                sendOPCUA('uawrite -u "'+opcuaAddress +'" -n "ns=1;i=2006" ' +pressure);
		console.log("Pressure: "+pressure);
	      }
            });
        });
    }
    function luxC() {
        st.notifyLuxometer(function () {
            st.on('luxometerChange', function (lux) {
	      var ts = new Date().getTime();
	      if (ts - pushDelay > data.ts) {
		data.ts = ts;
	        sendOPCUA('uawrite -u "'+opcuaAddress +'" -n "ns=1;i=2005" ' +lux);
		console.log("Light: "+lux);
	      }
            });
        });
    }
    function sendOPCUA(bashCommand) {
      var exec = require('child_process').exec;

      var child = exec(bashCommand, function(error, stdout, stderr) {
	if (error) console.log(error);
	process.stdout.write(stdout);
	process.stderr.write(stderr);
      });
    }
    init();
});


function mqttSend(data) {
    var payload = {
        "d": data
    }
    var msg =  JSON.stringify(payload);
    mqttClient.publish('iot-2/evt/status/fmt/json',msg, function () {
    });
    console.log('data sent: '+msg);
};
