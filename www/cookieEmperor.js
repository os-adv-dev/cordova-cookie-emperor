/**
 * CookieEmperor
 * @constructor
 */
function CookieEmperor(pluginName) {
    this.version = 1;
    this.pluginName = pluginName;
}

/**
 * returns cookie value
 * @param url
 * @param cookieName
 * @param successCallback
 * @param errorCallback
 */
CookieEmperor.prototype.getCookie = function(url, cookieName, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, this.pluginName, 'getCookieValue', [url, cookieName]);
};

/**
 * sets cookie
 * @param cookie 
 * @param successCallback
 * @param errorCallback
 */
CookieEmperor.prototype.setCookie = function (cookie, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback,  this.pluginName, 'setCookieValue', [cookie]);
};

/**
 * clears all cookies
 * @param successCallback
 * @param errorCallback
 */
CookieEmperor.prototype.clearAll = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback,  this.pluginName, 'clearCookies', []);
};

/**
 * export default CookieEmperor
 * @type {CookieEmperor}
 */
module.exports = new CookieEmperor('CookieEmperor');
