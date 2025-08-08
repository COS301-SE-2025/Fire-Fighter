import {
  __async
} from "./chunk-UQIXM5CJ.js";

// node_modules/@ionic/core/dist/esm/theme-DiVJyqlX.js
var hostContext = (selector, el) => {
  return el.closest(selector) !== null;
};
var createColorClasses = (color, cssClassMap) => {
  return typeof color === "string" && color.length > 0 ? Object.assign({
    "ion-color": true,
    [`ion-color-${color}`]: true
  }, cssClassMap) : cssClassMap;
};
var getClassList = (classes) => {
  if (classes !== void 0) {
    const array = Array.isArray(classes) ? classes : classes.split(" ");
    return array.filter((c) => c != null).map((c) => c.trim()).filter((c) => c !== "");
  }
  return [];
};
var getClassMap = (classes) => {
  const map = {};
  getClassList(classes).forEach((c) => map[c] = true);
  return map;
};
var SCHEME = /^[a-z][a-z0-9+\-.]*:/;
var openURL = (url, ev, direction, animation) => __async(null, null, function* () {
  if (url != null && url[0] !== "#" && !SCHEME.test(url)) {
    const router = document.querySelector("ion-router");
    if (router) {
      if (ev != null) {
        ev.preventDefault();
      }
      return router.push(url, direction, animation);
    }
  }
  return false;
});

export {
  hostContext,
  createColorClasses,
  getClassMap,
  openURL
};
/*! Bundled license information:

@ionic/core/dist/esm/theme-DiVJyqlX.js:
  (*!
   * (C) Ionic http://ionicframework.com - MIT License
   *)
*/
//# sourceMappingURL=chunk-RC5HTYUZ.js.map
