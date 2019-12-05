package de.rouvenkruse.cordova.plugins.cookieemperor;

import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CookieEmperor extends CordovaPlugin {

    private static final String ACTION_GET_COOKIE_VALUE = "getCookieValue";
    private static final String ACTION_SET_COOKIE_VALUE = "setCookieValue";
    private static final String ACTION_CLEAR_COOKIES = "clearCookies";

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (ACTION_GET_COOKIE_VALUE.equals(action)) {
            return this.getCookie(args, callbackContext);
        } else if (ACTION_SET_COOKIE_VALUE.equals(action)) {
            return this.setCookie(args, callbackContext);
        } else if (ACTION_CLEAR_COOKIES.equals(action)) {
            CookieManager cookieManager = CookieManager.getInstance();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                cookieManager.removeAllCookies(value -> {
                });
                cookieManager.flush();
            } else {
                cookieManager.removeAllCookie();
                cookieManager.removeSessionCookie();
            }

            callbackContext.success();
        }

        callbackContext.error("Invalid action");
        return false;

    }

    /**
     * Sets cookie value under given key
     *
     * @param args            the arguments
     * @param callbackContext the callbackContext
     * @return boolean the True if success and false if some an error
     */
    private boolean setCookie(JSONArray args, final CallbackContext callbackContext) throws JSONException {

        JSONObject jsonObject = args.getJSONObject(0);
        if (jsonObject == null) {
            callbackContext.error("The object can not be null");
            return false;
        } else {
            String name = jsonObject.getString("name");
            String value = jsonObject.getString("value");
            String path = jsonObject.getString("path");
            String sessionCookie = jsonObject.getString("domain");
            String expire = jsonObject.getString("expire");

            cordova
                    .getThreadPool()
                    .execute(() -> {

                        try {
                            CookieSyncManager.createInstance(cordova.getContext());
                            CookieManager cookieManager = CookieManager.getInstance();
                            cookieManager.setAcceptCookie(true);

                            if (sessionCookie != null) {
                                // delete old cookies
                                cookieManager.removeSessionCookie();
                            }

                            cookieManager.setCookie(sessionCookie, value + getCookieValue(path, name, expire));
                            CookieSyncManager.createInstance(this.cordova.getContext());

                            PluginResult res = new PluginResult(PluginResult.Status.OK, "Successfully added cookie");
                            callbackContext.sendPluginResult(res);
                        } catch (Exception e) {
                            callbackContext.error(e.getMessage());
                        }
                    });
            return true;
        }
    }

    /**
     * Returns cookie under given key
     *
     * @param args            the arguments
     * @param callbackContext the callbackContext
     * @return Json Object
     */
    private boolean getCookie(JSONArray args, final CallbackContext callbackContext) {
        try {
            final String url = args.getString(0);

            cordova
                    .getThreadPool()
                    .execute(() -> {
                        try {
                            String cookies = CookieManager.getInstance().getCookie(url);
                            String cookieValue = "";

                            for (String cookie : cookies.split(";")) {
                                cookieValue = cookie.split("=")[0].trim();
                                break;
                            }

                            JSONObject json = null;

                            if (!cookieValue.equals("")) {
                                json = new JSONObject("{cookieValue:\"" + cookieValue + "\"}");
                            }

                            if (json != null) {
                                PluginResult res = new PluginResult(PluginResult.Status.OK, json);
                                callbackContext.sendPluginResult(res);
                            } else {
                                callbackContext.error("Cookie not found!");
                            }
                        } catch (Exception e) {
                            callbackContext.error(e.getMessage());
                        }
                    });

            return true;
        } catch (JSONException e) {
            callbackContext.error("JSON parsing error");
        }

        return false;
    }

    private String getCookieValue(String name, String path, String expires) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(";name=").append(name).append(";");
        stringBuilder.append("path=").append(path).append(";");

        if (expires != null && expires.length() > 0)
            stringBuilder.append("expires=").append(expires).append(";");

        return stringBuilder.toString();
    }
}
