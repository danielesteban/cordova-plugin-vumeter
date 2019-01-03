const argscheck = require('cordova/argscheck');
const exec = require('cordova/exec');

function VUMeter () {

}

VUMeter.prototype.getMeasurement = function (successCallback, errorCallback) {
    argscheck.checkArgs('fF', 'VUMeter.getMeasurement', arguments);
    exec(successCallback, errorCallback, 'VUMeter', 'getMeasurement', []);
};

module.exports = new VUMeter();
