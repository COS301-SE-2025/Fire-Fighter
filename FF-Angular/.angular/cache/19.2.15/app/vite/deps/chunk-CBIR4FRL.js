// node_modules/@ionic/core/components/index4.js
var Config = class {
  constructor() {
    this.m = /* @__PURE__ */ new Map();
  }
  reset(configObj) {
    this.m = new Map(Object.entries(configObj));
  }
  get(key, fallback) {
    const value = this.m.get(key);
    return value !== void 0 ? value : fallback;
  }
  getBoolean(key, fallback = false) {
    const val = this.m.get(key);
    if (val === void 0) {
      return fallback;
    }
    if (typeof val === "string") {
      return val === "true";
    }
    return !!val;
  }
  getNumber(key, fallback) {
    const val = parseFloat(this.m.get(key));
    return isNaN(val) ? fallback !== void 0 ? fallback : NaN : val;
  }
  set(key, value) {
    this.m.set(key, value);
  }
};
var config = new Config();
var configFromSession = (win) => {
  try {
    const configStr = win.sessionStorage.getItem(IONIC_SESSION_KEY);
    return configStr !== null ? JSON.parse(configStr) : {};
  } catch (e) {
    return {};
  }
};
var saveConfig = (win, c) => {
  try {
    win.sessionStorage.setItem(IONIC_SESSION_KEY, JSON.stringify(c));
  } catch (e) {
    return;
  }
};
var configFromURL = (win) => {
  const configObj = {};
  win.location.search.slice(1).split("&").map((entry) => entry.split("=")).map(([key, value]) => {
    try {
      return [decodeURIComponent(key), decodeURIComponent(value)];
    } catch (e) {
      return ["", ""];
    }
  }).filter(([key]) => startsWith(key, IONIC_PREFIX)).map(([key, value]) => [key.slice(IONIC_PREFIX.length), value]).forEach(([key, value]) => {
    configObj[key] = value;
  });
  return configObj;
};
var startsWith = (input, search) => {
  return input.substr(0, search.length) === search;
};
var IONIC_PREFIX = "ionic:";
var IONIC_SESSION_KEY = "ionic-persist-config";
var LogLevel;
(function(LogLevel2) {
  LogLevel2["OFF"] = "OFF";
  LogLevel2["ERROR"] = "ERROR";
  LogLevel2["WARN"] = "WARN";
})(LogLevel || (LogLevel = {}));
var printIonWarning = (message, ...params) => {
  const logLevel = config.get("logLevel", LogLevel.WARN);
  if ([LogLevel.WARN].includes(logLevel)) {
    return console.warn(`[Ionic Warning]: ${message}`, ...params);
  }
};
var printIonError = (message, ...params) => {
  const logLevel = config.get("logLevel", LogLevel.ERROR);
  if ([LogLevel.ERROR, LogLevel.WARN].includes(logLevel)) {
    return console.error(`[Ionic Error]: ${message}`, ...params);
  }
};
var printRequiredElementError = (el, ...targetSelectors) => {
  return console.error(`<${el.tagName.toLowerCase()}> must be used inside ${targetSelectors.join(" or ")}.`);
};

export {
  config,
  configFromSession,
  saveConfig,
  configFromURL,
  printIonWarning,
  printIonError,
  printRequiredElementError
};
/*! Bundled license information:

@ionic/core/components/index4.js:
  (*!
   * (C) Ionic http://ionicframework.com - MIT License
   *)
*/
//# sourceMappingURL=chunk-CBIR4FRL.js.map
