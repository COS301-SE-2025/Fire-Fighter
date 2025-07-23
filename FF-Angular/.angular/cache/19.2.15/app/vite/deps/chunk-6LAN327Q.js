import {
  LIFECYCLE_DID_ENTER,
  LIFECYCLE_DID_LEAVE,
  LIFECYCLE_WILL_ENTER,
  LIFECYCLE_WILL_LEAVE,
  LIFECYCLE_WILL_UNLOAD,
  createAnimation
} from "./chunk-HPCN2MB7.js";
import {
  getMode,
  setMode
} from "./chunk-CKFPX5IG.js";
import {
  addEventListener,
  componentOnReady,
  focusVisibleElement,
  getElementRoot,
  removeEventListener
} from "./chunk-R4SJUBFW.js";
import {
  BACKDROP_NO_SCROLL
} from "./chunk-BF4AXLRD.js";
import {
  MENU_BACK_BUTTON_PRIORITY,
  OVERLAY_BACK_BUTTON_PRIORITY,
  shouldUseCloseWatcher
} from "./chunk-22S3O5CM.js";
import {
  doc
} from "./chunk-44IZU6OF.js";
import {
  config,
  configFromSession,
  configFromURL,
  printIonError,
  printIonWarning,
  saveConfig
} from "./chunk-CBIR4FRL.js";
import {
  ActivatedRoute,
  ChildrenOutletContexts,
  NavigationStart,
  PRIMARY_OUTLET,
  Router,
  RouterLink,
  UrlSerializer
} from "./chunk-376RXNOM.js";
import {
  NgControl
} from "./chunk-BPNPSZZ7.js";
import {
  Location,
  LocationStrategy
} from "./chunk-W7H6JUOX.js";
import {
  DOCUMENT
} from "./chunk-ROWOOGPI.js";
import {
  ApplicationRef,
  Attribute,
  ChangeDetectorRef,
  ContentChild,
  Directive,
  ElementRef,
  EnvironmentInjector,
  EventEmitter,
  HostListener,
  Inject,
  Injectable,
  InjectionToken,
  Injector,
  Input,
  NgZone,
  Optional,
  Output,
  SkipSelf,
  TemplateRef,
  ViewChild,
  ViewContainerRef,
  createComponent,
  inject,
  reflectComponentType,
  setClassMetadata,
  ɵɵNgOnChangesFeature,
  ɵɵcontentQuery,
  ɵɵdefineDirective,
  ɵɵdefineInjectable,
  ɵɵdirectiveInject,
  ɵɵinject,
  ɵɵinjectAttribute,
  ɵɵlistener,
  ɵɵloadQuery,
  ɵɵqueryRefresh,
  ɵɵviewQuery
} from "./chunk-GPKJZTAI.js";
import {
  fromEvent
} from "./chunk-D2ID5PW6.js";
import {
  BehaviorSubject,
  Subject,
  combineLatest,
  distinctUntilChanged,
  filter,
  of,
  switchMap
} from "./chunk-ZCKGGNWJ.js";
import {
  __decorate
} from "./chunk-S3YC363S.js";
import {
  __async,
  __objRest,
  __publicField,
  __spreadProps,
  __spreadValues
} from "./chunk-UQIXM5CJ.js";

// node_modules/@ionic/core/components/cubic-bezier.js
var getTimeGivenProgression = (p0, p1, p2, p3, progression) => {
  return solveCubicBezier(p0[1], p1[1], p2[1], p3[1], progression).map((tValue) => {
    return solveCubicParametricEquation(p0[0], p1[0], p2[0], p3[0], tValue);
  });
};
var solveCubicParametricEquation = (p0, p1, p2, p3, t) => {
  const partA = 3 * p1 * Math.pow(t - 1, 2);
  const partB = -3 * p2 * t + 3 * p2 + p3 * t;
  const partC = p0 * Math.pow(t - 1, 3);
  return t * (partA + t * partB) - partC;
};
var solveCubicBezier = (p0, p1, p2, p3, refPoint) => {
  p0 -= refPoint;
  p1 -= refPoint;
  p2 -= refPoint;
  p3 -= refPoint;
  const roots = solveCubicEquation(p3 - 3 * p2 + 3 * p1 - p0, 3 * p2 - 6 * p1 + 3 * p0, 3 * p1 - 3 * p0, p0);
  return roots.filter((root) => root >= 0 && root <= 1);
};
var solveQuadraticEquation = (a, b, c) => {
  const discriminant = b * b - 4 * a * c;
  if (discriminant < 0) {
    return [];
  } else {
    return [(-b + Math.sqrt(discriminant)) / (2 * a), (-b - Math.sqrt(discriminant)) / (2 * a)];
  }
};
var solveCubicEquation = (a, b, c, d) => {
  if (a === 0) {
    return solveQuadraticEquation(b, c, d);
  }
  b /= a;
  c /= a;
  d /= a;
  const p = (3 * c - b * b) / 3;
  const q = (2 * b * b * b - 9 * b * c + 27 * d) / 27;
  if (p === 0) {
    return [Math.pow(-q, 1 / 3)];
  } else if (q === 0) {
    return [Math.sqrt(-p), -Math.sqrt(-p)];
  }
  const discriminant = Math.pow(q / 2, 2) + Math.pow(p / 3, 3);
  if (discriminant === 0) {
    return [Math.pow(q / 2, 1 / 2) - b / 3];
  } else if (discriminant > 0) {
    return [Math.pow(-(q / 2) + Math.sqrt(discriminant), 1 / 3) - Math.pow(q / 2 + Math.sqrt(discriminant), 1 / 3) - b / 3];
  }
  const r = Math.sqrt(Math.pow(-(p / 3), 3));
  const phi = Math.acos(-(q / (2 * Math.sqrt(Math.pow(-(p / 3), 3)))));
  const s = 2 * Math.pow(r, 1 / 3);
  return [s * Math.cos(phi / 3) - b / 3, s * Math.cos((phi + 2 * Math.PI) / 3) - b / 3, s * Math.cos((phi + 4 * Math.PI) / 3) - b / 3];
};

// node_modules/@ionic/core/components/ionic-global.js
var getPlatforms = (win) => setupPlatforms(win);
var isPlatform = (winOrPlatform, platform) => {
  if (typeof winOrPlatform === "string") {
    platform = winOrPlatform;
    winOrPlatform = void 0;
  }
  return getPlatforms(winOrPlatform).includes(platform);
};
var setupPlatforms = (win = window) => {
  if (typeof win === "undefined") {
    return [];
  }
  win.Ionic = win.Ionic || {};
  let platforms = win.Ionic.platforms;
  if (platforms == null) {
    platforms = win.Ionic.platforms = detectPlatforms(win);
    platforms.forEach((p) => win.document.documentElement.classList.add(`plt-${p}`));
  }
  return platforms;
};
var detectPlatforms = (win) => {
  const customPlatformMethods = config.get("platform");
  return Object.keys(PLATFORMS_MAP).filter((p) => {
    const customMethod = customPlatformMethods === null || customPlatformMethods === void 0 ? void 0 : customPlatformMethods[p];
    return typeof customMethod === "function" ? customMethod(win) : PLATFORMS_MAP[p](win);
  });
};
var isMobileWeb = (win) => isMobile(win) && !isHybrid(win);
var isIpad = (win) => {
  if (testUserAgent(win, /iPad/i)) {
    return true;
  }
  if (testUserAgent(win, /Macintosh/i) && isMobile(win)) {
    return true;
  }
  return false;
};
var isIphone = (win) => testUserAgent(win, /iPhone/i);
var isIOS = (win) => testUserAgent(win, /iPhone|iPod/i) || isIpad(win);
var isAndroid = (win) => testUserAgent(win, /android|sink/i);
var isAndroidTablet = (win) => {
  return isAndroid(win) && !testUserAgent(win, /mobile/i);
};
var isPhablet = (win) => {
  const width = win.innerWidth;
  const height = win.innerHeight;
  const smallest = Math.min(width, height);
  const largest = Math.max(width, height);
  return smallest > 390 && smallest < 520 && largest > 620 && largest < 800;
};
var isTablet = (win) => {
  const width = win.innerWidth;
  const height = win.innerHeight;
  const smallest = Math.min(width, height);
  const largest = Math.max(width, height);
  return isIpad(win) || isAndroidTablet(win) || smallest > 460 && smallest < 820 && largest > 780 && largest < 1400;
};
var isMobile = (win) => matchMedia(win, "(any-pointer:coarse)");
var isDesktop = (win) => !isMobile(win);
var isHybrid = (win) => isCordova(win) || isCapacitorNative(win);
var isCordova = (win) => !!(win["cordova"] || win["phonegap"] || win["PhoneGap"]);
var isCapacitorNative = (win) => {
  const capacitor = win["Capacitor"];
  return !!((capacitor === null || capacitor === void 0 ? void 0 : capacitor.isNative) || (capacitor === null || capacitor === void 0 ? void 0 : capacitor.isNativePlatform) && !!capacitor.isNativePlatform());
};
var isElectron = (win) => testUserAgent(win, /electron/i);
var isPWA = (win) => {
  var _a5;
  return !!(((_a5 = win.matchMedia) === null || _a5 === void 0 ? void 0 : _a5.call(win, "(display-mode: standalone)").matches) || win.navigator.standalone);
};
var testUserAgent = (win, expr) => expr.test(win.navigator.userAgent);
var matchMedia = (win, query) => {
  var _a5;
  return (_a5 = win.matchMedia) === null || _a5 === void 0 ? void 0 : _a5.call(win, query).matches;
};
var PLATFORMS_MAP = {
  ipad: isIpad,
  iphone: isIphone,
  ios: isIOS,
  android: isAndroid,
  phablet: isPhablet,
  tablet: isTablet,
  cordova: isCordova,
  capacitor: isCapacitorNative,
  electron: isElectron,
  pwa: isPWA,
  mobile: isMobile,
  mobileweb: isMobileWeb,
  desktop: isDesktop,
  hybrid: isHybrid
};
var defaultMode;
var getIonMode = (ref) => {
  return ref && getMode(ref) || defaultMode;
};
var initialize = (userConfig = {}) => {
  if (typeof window === "undefined") {
    return;
  }
  const doc2 = window.document;
  const win = window;
  const Ionic = win.Ionic = win.Ionic || {};
  const configObj = Object.assign(Object.assign(Object.assign(Object.assign(Object.assign({}, configFromSession(win)), {
    persistConfig: false
  }), Ionic.config), configFromURL(win)), userConfig);
  config.reset(configObj);
  if (config.getBoolean("persistConfig")) {
    saveConfig(win, configObj);
  }
  setupPlatforms(win);
  Ionic.config = config;
  Ionic.mode = defaultMode = config.get("mode", doc2.documentElement.getAttribute("mode") || (isPlatform(win, "ios") ? "ios" : "md"));
  config.set("mode", defaultMode);
  doc2.documentElement.setAttribute("mode", defaultMode);
  doc2.documentElement.classList.add(defaultMode);
  if (config.getBoolean("_testing")) {
    config.set("animated", false);
  }
  const isIonicElement = (elm) => {
    var _a5;
    return (_a5 = elm.tagName) === null || _a5 === void 0 ? void 0 : _a5.startsWith("ION-");
  };
  const isAllowedIonicModeValue = (elmMode) => ["ios", "md"].includes(elmMode);
  setMode((elm) => {
    while (elm) {
      const elmMode = elm.mode || elm.getAttribute("mode");
      if (elmMode) {
        if (isAllowedIonicModeValue(elmMode)) {
          return elmMode;
        } else if (isIonicElement(elm)) {
          printIonWarning('Invalid ionic mode: "' + elmMode + '", expected: "ios" or "md"');
        }
      }
      elm = elm.parentElement;
    }
    return defaultMode;
  });
};

// node_modules/@ionic/core/components/config.js
var sanitizeDOMString = (untrustedString) => {
  try {
    if (untrustedString instanceof IonicSafeString) {
      return untrustedString.value;
    }
    if (!isSanitizerEnabled() || typeof untrustedString !== "string" || untrustedString === "") {
      return untrustedString;
    }
    if (untrustedString.includes("onload=")) {
      return "";
    }
    const documentFragment = document.createDocumentFragment();
    const workingDiv = document.createElement("div");
    documentFragment.appendChild(workingDiv);
    workingDiv.innerHTML = untrustedString;
    blockedTags.forEach((blockedTag) => {
      const getElementsToRemove = documentFragment.querySelectorAll(blockedTag);
      for (let elementIndex = getElementsToRemove.length - 1; elementIndex >= 0; elementIndex--) {
        const element = getElementsToRemove[elementIndex];
        if (element.parentNode) {
          element.parentNode.removeChild(element);
        } else {
          documentFragment.removeChild(element);
        }
        const childElements = getElementChildren(element);
        for (let childIndex = 0; childIndex < childElements.length; childIndex++) {
          sanitizeElement(childElements[childIndex]);
        }
      }
    });
    const dfChildren = getElementChildren(documentFragment);
    for (let childIndex = 0; childIndex < dfChildren.length; childIndex++) {
      sanitizeElement(dfChildren[childIndex]);
    }
    const fragmentDiv = document.createElement("div");
    fragmentDiv.appendChild(documentFragment);
    const getInnerDiv = fragmentDiv.querySelector("div");
    return getInnerDiv !== null ? getInnerDiv.innerHTML : fragmentDiv.innerHTML;
  } catch (err) {
    printIonError("sanitizeDOMString", err);
    return "";
  }
};
var sanitizeElement = (element) => {
  if (element.nodeType && element.nodeType !== 1) {
    return;
  }
  if (typeof NamedNodeMap !== "undefined" && !(element.attributes instanceof NamedNodeMap)) {
    element.remove();
    return;
  }
  for (let i = element.attributes.length - 1; i >= 0; i--) {
    const attribute = element.attributes.item(i);
    const attributeName = attribute.name;
    if (!allowedAttributes.includes(attributeName.toLowerCase())) {
      element.removeAttribute(attributeName);
      continue;
    }
    const attributeValue = attribute.value;
    const propertyValue = element[attributeName];
    if (attributeValue != null && attributeValue.toLowerCase().includes("javascript:") || propertyValue != null && propertyValue.toLowerCase().includes("javascript:")) {
      element.removeAttribute(attributeName);
    }
  }
  const childElements = getElementChildren(element);
  for (let i = 0; i < childElements.length; i++) {
    sanitizeElement(childElements[i]);
  }
};
var getElementChildren = (el) => {
  return el.children != null ? el.children : el.childNodes;
};
var isSanitizerEnabled = () => {
  var _a5;
  const win = window;
  const config2 = (_a5 = win === null || win === void 0 ? void 0 : win.Ionic) === null || _a5 === void 0 ? void 0 : _a5.config;
  if (config2) {
    if (config2.get) {
      return config2.get("sanitizerEnabled", true);
    } else {
      return config2.sanitizerEnabled === true || config2.sanitizerEnabled === void 0;
    }
  }
  return true;
};
var allowedAttributes = ["class", "id", "href", "src", "name", "slot"];
var blockedTags = ["script", "style", "iframe", "meta", "link", "object", "embed"];
var IonicSafeString = class {
  constructor(value) {
    this.value = value;
  }
};
var ENABLE_HTML_CONTENT_DEFAULT = false;

// node_modules/@ionic/core/components/index5.js
var baseAnimation = (isIos) => {
  return createAnimation().duration(isIos ? 400 : 300);
};
var menuOverlayAnimation = (menu) => {
  let closedX;
  let openedX;
  const width = menu.width + 8;
  const menuAnimation = createAnimation();
  const backdropAnimation = createAnimation();
  if (menu.isEndSide) {
    closedX = width + "px";
    openedX = "0px";
  } else {
    closedX = -width + "px";
    openedX = "0px";
  }
  menuAnimation.addElement(menu.menuInnerEl).fromTo("transform", `translateX(${closedX})`, `translateX(${openedX})`);
  const mode = getIonMode(menu);
  const isIos = mode === "ios";
  const opacity = isIos ? 0.2 : 0.25;
  backdropAnimation.addElement(menu.backdropEl).fromTo("opacity", 0.01, opacity);
  return baseAnimation(isIos).addAnimation([menuAnimation, backdropAnimation]);
};
var menuPushAnimation = (menu) => {
  let contentOpenedX;
  let menuClosedX;
  const mode = getIonMode(menu);
  const width = menu.width;
  if (menu.isEndSide) {
    contentOpenedX = -width + "px";
    menuClosedX = width + "px";
  } else {
    contentOpenedX = width + "px";
    menuClosedX = -width + "px";
  }
  const menuAnimation = createAnimation().addElement(menu.menuInnerEl).fromTo("transform", `translateX(${menuClosedX})`, "translateX(0px)");
  const contentAnimation = createAnimation().addElement(menu.contentEl).fromTo("transform", "translateX(0px)", `translateX(${contentOpenedX})`);
  const backdropAnimation = createAnimation().addElement(menu.backdropEl).fromTo("opacity", 0.01, 0.32);
  return baseAnimation(mode === "ios").addAnimation([menuAnimation, contentAnimation, backdropAnimation]);
};
var menuRevealAnimation = (menu) => {
  const mode = getIonMode(menu);
  const openedX = menu.width * (menu.isEndSide ? -1 : 1) + "px";
  const contentOpen = createAnimation().addElement(menu.contentEl).fromTo("transform", "translateX(0px)", `translateX(${openedX})`);
  return baseAnimation(mode === "ios").addAnimation(contentOpen);
};
var createMenuController = () => {
  const menuAnimations = /* @__PURE__ */ new Map();
  const menus = [];
  const open = (menu) => __async(null, null, function* () {
    const menuEl = yield get(menu, true);
    if (menuEl) {
      return menuEl.open();
    }
    return false;
  });
  const close = (menu) => __async(null, null, function* () {
    const menuEl = yield menu !== void 0 ? get(menu, true) : getOpen();
    if (menuEl !== void 0) {
      return menuEl.close();
    }
    return false;
  });
  const toggle = (menu) => __async(null, null, function* () {
    const menuEl = yield get(menu, true);
    if (menuEl) {
      return menuEl.toggle();
    }
    return false;
  });
  const enable = (shouldEnable, menu) => __async(null, null, function* () {
    const menuEl = yield get(menu);
    if (menuEl) {
      menuEl.disabled = !shouldEnable;
    }
    return menuEl;
  });
  const swipeGesture = (shouldEnable, menu) => __async(null, null, function* () {
    const menuEl = yield get(menu);
    if (menuEl) {
      menuEl.swipeGesture = shouldEnable;
    }
    return menuEl;
  });
  const isOpen = (menu) => __async(null, null, function* () {
    if (menu != null) {
      const menuEl = yield get(menu);
      return menuEl !== void 0 && menuEl.isOpen();
    } else {
      const menuEl = yield getOpen();
      return menuEl !== void 0;
    }
  });
  const isEnabled = (menu) => __async(null, null, function* () {
    const menuEl = yield get(menu);
    if (menuEl) {
      return !menuEl.disabled;
    }
    return false;
  });
  const get = (menu, logOnMultipleSideMenus = false) => __async(null, null, function* () {
    yield waitUntilReady();
    if (menu === "start" || menu === "end") {
      const menuRefs = menus.filter((m) => m.side === menu && !m.disabled);
      if (menuRefs.length >= 1) {
        if (menuRefs.length > 1 && logOnMultipleSideMenus) {
          printIonWarning(`menuController queried for a menu on the "${menu}" side, but ${menuRefs.length} menus were found. The first menu reference will be used. If this is not the behavior you want then pass the ID of the menu instead of its side.`, menuRefs.map((m) => m.el));
        }
        return menuRefs[0].el;
      }
      const sideMenuRefs = menus.filter((m) => m.side === menu);
      if (sideMenuRefs.length >= 1) {
        if (sideMenuRefs.length > 1 && logOnMultipleSideMenus) {
          printIonWarning(`menuController queried for a menu on the "${menu}" side, but ${sideMenuRefs.length} menus were found. The first menu reference will be used. If this is not the behavior you want then pass the ID of the menu instead of its side.`, sideMenuRefs.map((m) => m.el));
        }
        return sideMenuRefs[0].el;
      }
    } else if (menu != null) {
      return find((m) => m.menuId === menu);
    }
    const menuEl = find((m) => !m.disabled);
    if (menuEl) {
      return menuEl;
    }
    return menus.length > 0 ? menus[0].el : void 0;
  });
  const getOpen = () => __async(null, null, function* () {
    yield waitUntilReady();
    return _getOpenSync();
  });
  const getMenus = () => __async(null, null, function* () {
    yield waitUntilReady();
    return getMenusSync();
  });
  const isAnimating = () => __async(null, null, function* () {
    yield waitUntilReady();
    return isAnimatingSync();
  });
  const registerAnimation = (name, animation) => {
    menuAnimations.set(name, animation);
  };
  const _register = (menu) => {
    if (menus.indexOf(menu) < 0) {
      menus.push(menu);
    }
  };
  const _unregister = (menu) => {
    const index = menus.indexOf(menu);
    if (index > -1) {
      menus.splice(index, 1);
    }
  };
  const _setOpen = (menu, shouldOpen, animated, role) => __async(null, null, function* () {
    if (isAnimatingSync()) {
      return false;
    }
    if (shouldOpen) {
      const openedMenu = yield getOpen();
      if (openedMenu && menu.el !== openedMenu) {
        yield openedMenu.setOpen(false, false);
      }
    }
    return menu._setOpen(shouldOpen, animated, role);
  });
  const _createAnimation = (type, menuCmp) => {
    const animationBuilder = menuAnimations.get(type);
    if (!animationBuilder) {
      throw new Error("animation not registered");
    }
    const animation = animationBuilder(menuCmp);
    return animation;
  };
  const _getOpenSync = () => {
    return find((m) => m._isOpen);
  };
  const getMenusSync = () => {
    return menus.map((menu) => menu.el);
  };
  const isAnimatingSync = () => {
    return menus.some((menu) => menu.isAnimating);
  };
  const find = (predicate) => {
    const instance = menus.find(predicate);
    if (instance !== void 0) {
      return instance.el;
    }
    return void 0;
  };
  const waitUntilReady = () => {
    return Promise.all(Array.from(document.querySelectorAll("ion-menu")).map((menu) => new Promise((resolve) => componentOnReady(menu, resolve))));
  };
  registerAnimation("reveal", menuRevealAnimation);
  registerAnimation("push", menuPushAnimation);
  registerAnimation("overlay", menuOverlayAnimation);
  doc === null || doc === void 0 ? void 0 : doc.addEventListener("ionBackButton", (ev) => {
    const openMenu = _getOpenSync();
    if (openMenu) {
      ev.detail.register(MENU_BACK_BUTTON_PRIORITY, () => {
        return openMenu.close();
      });
    }
  });
  return {
    registerAnimation,
    get,
    getMenus,
    getOpen,
    isEnabled,
    swipeGesture,
    isAnimating,
    isOpen,
    enable,
    toggle,
    close,
    open,
    _getOpenSync,
    _createAnimation,
    _register,
    _unregister,
    _setOpen
  };
};
var menuController = createMenuController();

// node_modules/@ionic/core/components/framework-delegate.js
var attachComponent = (delegate, container, component, cssClasses, componentProps, inline) => __async(null, null, function* () {
  var _a5;
  if (delegate) {
    return delegate.attachViewToDom(container, component, componentProps, cssClasses);
  }
  if (!inline && typeof component !== "string" && !(component instanceof HTMLElement)) {
    throw new Error("framework delegate is missing");
  }
  const el = typeof component === "string" ? (_a5 = container.ownerDocument) === null || _a5 === void 0 ? void 0 : _a5.createElement(component) : component;
  if (cssClasses) {
    cssClasses.forEach((c) => el.classList.add(c));
  }
  if (componentProps) {
    Object.assign(el, componentProps);
  }
  container.appendChild(el);
  yield new Promise((resolve) => componentOnReady(el, resolve));
  return el;
});
var detachComponent = (delegate, element) => {
  if (element) {
    if (delegate) {
      const container = element.parentElement;
      return delegate.removeViewFromDom(container, element);
    }
    element.remove();
  }
  return Promise.resolve();
};
var CoreDelegate = () => {
  let BaseComponent;
  let Reference;
  const attachViewToDom = (_0, _1, ..._2) => __async(null, [_0, _1, ..._2], function* (parentElement, userComponent, userComponentProps = {}, cssClasses = []) {
    var _a5, _b;
    BaseComponent = parentElement;
    let ChildComponent;
    if (userComponent) {
      const el = typeof userComponent === "string" ? (_a5 = BaseComponent.ownerDocument) === null || _a5 === void 0 ? void 0 : _a5.createElement(userComponent) : userComponent;
      cssClasses.forEach((c) => el.classList.add(c));
      Object.assign(el, userComponentProps);
      BaseComponent.appendChild(el);
      ChildComponent = el;
      yield new Promise((resolve) => componentOnReady(el, resolve));
    } else if (BaseComponent.children.length > 0 && (BaseComponent.tagName === "ION-MODAL" || BaseComponent.tagName === "ION-POPOVER")) {
      const root = ChildComponent = BaseComponent.children[0];
      if (!root.classList.contains("ion-delegate-host")) {
        const el = (_b = BaseComponent.ownerDocument) === null || _b === void 0 ? void 0 : _b.createElement("div");
        el.classList.add("ion-delegate-host");
        cssClasses.forEach((c) => el.classList.add(c));
        el.append(...BaseComponent.children);
        BaseComponent.appendChild(el);
        ChildComponent = el;
      }
    }
    const app = document.querySelector("ion-app") || document.body;
    Reference = document.createComment("ionic teleport");
    BaseComponent.parentNode.insertBefore(Reference, BaseComponent);
    app.appendChild(BaseComponent);
    return ChildComponent !== null && ChildComponent !== void 0 ? ChildComponent : BaseComponent;
  });
  const removeViewFromDom = () => {
    if (BaseComponent && Reference) {
      Reference.parentNode.insertBefore(BaseComponent, Reference);
      Reference.remove();
    }
    return Promise.resolve();
  };
  return {
    attachViewToDom,
    removeViewFromDom
  };
};

// node_modules/@ionic/core/components/overlays.js
var focusableQueryString = '[tabindex]:not([tabindex^="-"]):not([hidden]):not([disabled]), input:not([type=hidden]):not([tabindex^="-"]):not([hidden]):not([disabled]), textarea:not([tabindex^="-"]):not([hidden]):not([disabled]), button:not([tabindex^="-"]):not([hidden]):not([disabled]), select:not([tabindex^="-"]):not([hidden]):not([disabled]), ion-checkbox:not([tabindex^="-"]):not([hidden]):not([disabled]), ion-radio:not([tabindex^="-"]):not([hidden]):not([disabled]), .ion-focusable:not([tabindex^="-"]):not([hidden]):not([disabled]), .ion-focusable[disabled="false"]:not([tabindex^="-"]):not([hidden])';
var focusFirstDescendant = (ref, fallbackElement) => {
  const firstInput = ref.querySelector(focusableQueryString);
  focusElementInContext(firstInput, fallbackElement !== null && fallbackElement !== void 0 ? fallbackElement : ref);
};
var focusLastDescendant = (ref, fallbackElement) => {
  const inputs = Array.from(ref.querySelectorAll(focusableQueryString));
  const lastInput = inputs.length > 0 ? inputs[inputs.length - 1] : null;
  focusElementInContext(lastInput, fallbackElement !== null && fallbackElement !== void 0 ? fallbackElement : ref);
};
var focusElementInContext = (hostToFocus, fallbackElement) => {
  let elementToFocus = hostToFocus;
  const shadowRoot = hostToFocus === null || hostToFocus === void 0 ? void 0 : hostToFocus.shadowRoot;
  if (shadowRoot) {
    elementToFocus = shadowRoot.querySelector(focusableQueryString) || hostToFocus;
  }
  if (elementToFocus) {
    const radioGroup = elementToFocus.closest("ion-radio-group");
    if (radioGroup) {
      radioGroup.setFocus();
    } else {
      focusVisibleElement(elementToFocus);
    }
  } else {
    fallbackElement.focus();
  }
};
var lastOverlayIndex = 0;
var lastId = 0;
var activeAnimations = /* @__PURE__ */ new WeakMap();
var createController = (tagName) => {
  return {
    create(options) {
      return createOverlay(tagName, options);
    },
    dismiss(data, role, id) {
      return dismissOverlay(document, data, role, tagName, id);
    },
    getTop() {
      return __async(this, null, function* () {
        return getPresentedOverlay(document, tagName);
      });
    }
  };
};
var alertController = createController("ion-alert");
var actionSheetController = createController("ion-action-sheet");
var loadingController = createController("ion-loading");
var modalController = createController("ion-modal");
var pickerController = createController("ion-picker-legacy");
var popoverController = createController("ion-popover");
var toastController = createController("ion-toast");
var prepareOverlay = (el) => {
  if (typeof document !== "undefined") {
    connectListeners(document);
  }
  const overlayIndex = lastOverlayIndex++;
  el.overlayIndex = overlayIndex;
};
var setOverlayId = (el) => {
  if (!el.hasAttribute("id")) {
    el.id = `ion-overlay-${++lastId}`;
  }
  return el.id;
};
var createOverlay = (tagName, opts) => {
  if (typeof window !== "undefined" && typeof window.customElements !== "undefined") {
    return window.customElements.whenDefined(tagName).then(() => {
      const element = document.createElement(tagName);
      element.classList.add("overlay-hidden");
      Object.assign(element, Object.assign(Object.assign({}, opts), {
        hasController: true
      }));
      getAppRoot(document).appendChild(element);
      return new Promise((resolve) => componentOnReady(element, resolve));
    });
  }
  return Promise.resolve();
};
var isOverlayHidden = (overlay) => overlay.classList.contains("overlay-hidden");
var focusElementInOverlay = (hostToFocus, overlay) => {
  let elementToFocus = hostToFocus;
  const shadowRoot = hostToFocus === null || hostToFocus === void 0 ? void 0 : hostToFocus.shadowRoot;
  if (shadowRoot) {
    elementToFocus = shadowRoot.querySelector(focusableQueryString) || hostToFocus;
  }
  if (elementToFocus) {
    focusVisibleElement(elementToFocus);
  } else {
    overlay.focus();
  }
};
var trapKeyboardFocus = (ev, doc2) => {
  const lastOverlay = getPresentedOverlay(doc2, "ion-alert,ion-action-sheet,ion-loading,ion-modal,ion-picker-legacy,ion-popover");
  const target = ev.target;
  if (!lastOverlay || !target) {
    return;
  }
  if (lastOverlay.classList.contains(FOCUS_TRAP_DISABLE_CLASS)) {
    return;
  }
  const trapScopedFocus = () => {
    if (lastOverlay === target) {
      lastOverlay.lastFocus = void 0;
    } else if (target.tagName === "ION-TOAST") {
      focusElementInOverlay(lastOverlay.lastFocus, lastOverlay);
    } else {
      const overlayRoot = getElementRoot(lastOverlay);
      if (!overlayRoot.contains(target)) {
        return;
      }
      const overlayWrapper = overlayRoot.querySelector(".ion-overlay-wrapper");
      if (!overlayWrapper) {
        return;
      }
      if (overlayWrapper.contains(target) || target === overlayRoot.querySelector("ion-backdrop")) {
        lastOverlay.lastFocus = target;
      } else {
        const lastFocus = lastOverlay.lastFocus;
        focusFirstDescendant(overlayWrapper, lastOverlay);
        if (lastFocus === doc2.activeElement) {
          focusLastDescendant(overlayWrapper, lastOverlay);
        }
        lastOverlay.lastFocus = doc2.activeElement;
      }
    }
  };
  const trapShadowFocus = () => {
    if (lastOverlay.contains(target)) {
      lastOverlay.lastFocus = target;
    } else if (target.tagName === "ION-TOAST") {
      focusElementInOverlay(lastOverlay.lastFocus, lastOverlay);
    } else {
      const lastFocus = lastOverlay.lastFocus;
      focusFirstDescendant(lastOverlay);
      if (lastFocus === doc2.activeElement) {
        focusLastDescendant(lastOverlay);
      }
      lastOverlay.lastFocus = doc2.activeElement;
    }
  };
  if (lastOverlay.shadowRoot) {
    trapShadowFocus();
  } else {
    trapScopedFocus();
  }
};
var connectListeners = (doc2) => {
  if (lastOverlayIndex === 0) {
    lastOverlayIndex = 1;
    doc2.addEventListener("focus", (ev) => {
      trapKeyboardFocus(ev, doc2);
    }, true);
    doc2.addEventListener("ionBackButton", (ev) => {
      const lastOverlay = getPresentedOverlay(doc2);
      if (lastOverlay === null || lastOverlay === void 0 ? void 0 : lastOverlay.backdropDismiss) {
        ev.detail.register(OVERLAY_BACK_BUTTON_PRIORITY, () => {
          lastOverlay.dismiss(void 0, BACKDROP);
        });
      }
    });
    if (!shouldUseCloseWatcher()) {
      doc2.addEventListener("keydown", (ev) => {
        if (ev.key === "Escape") {
          const lastOverlay = getPresentedOverlay(doc2);
          if (lastOverlay === null || lastOverlay === void 0 ? void 0 : lastOverlay.backdropDismiss) {
            lastOverlay.dismiss(void 0, BACKDROP);
          }
        }
      });
    }
  }
};
var dismissOverlay = (doc2, data, role, overlayTag, id) => {
  const overlay = getPresentedOverlay(doc2, overlayTag, id);
  if (!overlay) {
    return Promise.reject("overlay does not exist");
  }
  return overlay.dismiss(data, role);
};
var getOverlays = (doc2, selector) => {
  if (selector === void 0) {
    selector = "ion-alert,ion-action-sheet,ion-loading,ion-modal,ion-picker-legacy,ion-popover,ion-toast";
  }
  return Array.from(doc2.querySelectorAll(selector)).filter((c) => c.overlayIndex > 0);
};
var getPresentedOverlays = (doc2, overlayTag) => {
  return getOverlays(doc2, overlayTag).filter((o) => !isOverlayHidden(o));
};
var getPresentedOverlay = (doc2, overlayTag, id) => {
  const overlays = getPresentedOverlays(doc2, overlayTag);
  return id === void 0 ? overlays[overlays.length - 1] : overlays.find((o) => o.id === id);
};
var setRootAriaHidden = (hidden = false) => {
  const root = getAppRoot(document);
  const viewContainer = root.querySelector("ion-router-outlet, ion-nav, #ion-view-container-root");
  if (!viewContainer) {
    return;
  }
  if (hidden) {
    viewContainer.setAttribute("aria-hidden", "true");
  } else {
    viewContainer.removeAttribute("aria-hidden");
  }
};
var present = (overlay, name, iosEnterAnimation, mdEnterAnimation, opts) => __async(null, null, function* () {
  var _a5, _b;
  if (overlay.presented) {
    return;
  }
  if (overlay.el.tagName !== "ION-TOAST") {
    setRootAriaHidden(true);
    document.body.classList.add(BACKDROP_NO_SCROLL);
  }
  hideUnderlyingOverlaysFromScreenReaders(overlay.el);
  hideAnimatingOverlayFromScreenReaders(overlay.el);
  overlay.presented = true;
  overlay.willPresent.emit();
  (_a5 = overlay.willPresentShorthand) === null || _a5 === void 0 ? void 0 : _a5.emit();
  const mode = getIonMode(overlay);
  const animationBuilder = overlay.enterAnimation ? overlay.enterAnimation : config.get(name, mode === "ios" ? iosEnterAnimation : mdEnterAnimation);
  const completed = yield overlayAnimation(overlay, animationBuilder, overlay.el, opts);
  if (completed) {
    overlay.didPresent.emit();
    (_b = overlay.didPresentShorthand) === null || _b === void 0 ? void 0 : _b.emit();
  }
  if (overlay.el.tagName !== "ION-TOAST") {
    restoreElementFocus(overlay.el);
  }
  if (overlay.keyboardClose && (document.activeElement === null || !overlay.el.contains(document.activeElement))) {
    overlay.el.focus();
  }
  overlay.el.removeAttribute("aria-hidden");
});
var restoreElementFocus = (overlayEl) => __async(null, null, function* () {
  let previousElement = document.activeElement;
  if (!previousElement) {
    return;
  }
  const shadowRoot = previousElement === null || previousElement === void 0 ? void 0 : previousElement.shadowRoot;
  if (shadowRoot) {
    previousElement = shadowRoot.querySelector(focusableQueryString) || previousElement;
  }
  yield overlayEl.onDidDismiss();
  if (document.activeElement === null || document.activeElement === document.body) {
    previousElement.focus();
  }
});
var dismiss = (overlay, data, role, name, iosLeaveAnimation, mdLeaveAnimation, opts) => __async(null, null, function* () {
  var _a5, _b;
  if (!overlay.presented) {
    return false;
  }
  const presentedOverlays = doc !== void 0 ? getPresentedOverlays(doc) : [];
  const overlaysNotToast = presentedOverlays.filter((o) => o.tagName !== "ION-TOAST");
  const lastOverlayNotToast = overlaysNotToast.length === 1 && overlaysNotToast[0].id === overlay.el.id;
  if (lastOverlayNotToast) {
    setRootAriaHidden(false);
    document.body.classList.remove(BACKDROP_NO_SCROLL);
  }
  overlay.presented = false;
  try {
    hideAnimatingOverlayFromScreenReaders(overlay.el);
    overlay.el.style.setProperty("pointer-events", "none");
    overlay.willDismiss.emit({
      data,
      role
    });
    (_a5 = overlay.willDismissShorthand) === null || _a5 === void 0 ? void 0 : _a5.emit({
      data,
      role
    });
    const mode = getIonMode(overlay);
    const animationBuilder = overlay.leaveAnimation ? overlay.leaveAnimation : config.get(name, mode === "ios" ? iosLeaveAnimation : mdLeaveAnimation);
    if (role !== GESTURE) {
      yield overlayAnimation(overlay, animationBuilder, overlay.el, opts);
    }
    overlay.didDismiss.emit({
      data,
      role
    });
    (_b = overlay.didDismissShorthand) === null || _b === void 0 ? void 0 : _b.emit({
      data,
      role
    });
    const animations = activeAnimations.get(overlay) || [];
    animations.forEach((ani) => ani.destroy());
    activeAnimations.delete(overlay);
    overlay.el.classList.add("overlay-hidden");
    overlay.el.style.removeProperty("pointer-events");
    if (overlay.el.lastFocus !== void 0) {
      overlay.el.lastFocus = void 0;
    }
  } catch (err) {
    printIonError(`[${overlay.el.tagName.toLowerCase()}] - `, err);
  }
  overlay.el.remove();
  revealOverlaysToScreenReaders();
  return true;
});
var getAppRoot = (doc2) => {
  return doc2.querySelector("ion-app") || doc2.body;
};
var overlayAnimation = (overlay, animationBuilder, baseEl, opts) => __async(null, null, function* () {
  baseEl.classList.remove("overlay-hidden");
  const aniRoot = overlay.el;
  const animation = animationBuilder(aniRoot, opts);
  if (!overlay.animated || !config.getBoolean("animated", true)) {
    animation.duration(0);
  }
  if (overlay.keyboardClose) {
    animation.beforeAddWrite(() => {
      const activeElement = baseEl.ownerDocument.activeElement;
      if (activeElement === null || activeElement === void 0 ? void 0 : activeElement.matches("input,ion-input, ion-textarea")) {
        activeElement.blur();
      }
    });
  }
  const activeAni = activeAnimations.get(overlay) || [];
  activeAnimations.set(overlay, [...activeAni, animation]);
  yield animation.play();
  return true;
});
var eventMethod = (element, eventName) => {
  let resolve;
  const promise = new Promise((r) => resolve = r);
  onceEvent(element, eventName, (event) => {
    resolve(event.detail);
  });
  return promise;
};
var onceEvent = (element, eventName, callback) => {
  const handler = (ev) => {
    removeEventListener(element, eventName, handler);
    callback(ev);
  };
  addEventListener(element, eventName, handler);
};
var isCancel = (role) => {
  return role === "cancel" || role === BACKDROP;
};
var defaultGate = (h) => h();
var safeCall = (handler, arg) => {
  if (typeof handler === "function") {
    const jmp = config.get("_zoneGate", defaultGate);
    return jmp(() => {
      try {
        return handler(arg);
      } catch (e) {
        throw e;
      }
    });
  }
  return void 0;
};
var BACKDROP = "backdrop";
var GESTURE = "gesture";
var OVERLAY_GESTURE_PRIORITY = 39;
var createDelegateController = (ref) => {
  let inline = false;
  let workingDelegate;
  const coreDelegate = CoreDelegate();
  const getDelegate = (force = false) => {
    if (workingDelegate && !force) {
      return {
        delegate: workingDelegate,
        inline
      };
    }
    const {
      el,
      hasController,
      delegate
    } = ref;
    const parentEl = el.parentNode;
    inline = parentEl !== null && !hasController;
    workingDelegate = inline ? delegate || coreDelegate : delegate;
    return {
      inline,
      delegate: workingDelegate
    };
  };
  const attachViewToDom = (component) => __async(null, null, function* () {
    const {
      delegate
    } = getDelegate(true);
    if (delegate) {
      return yield delegate.attachViewToDom(ref.el, component);
    }
    const {
      hasController
    } = ref;
    if (hasController && component !== void 0) {
      throw new Error("framework delegate is missing");
    }
    return null;
  });
  const removeViewFromDom = () => {
    const {
      delegate
    } = getDelegate();
    if (delegate && ref.el !== void 0) {
      delegate.removeViewFromDom(ref.el.parentElement, ref.el);
    }
  };
  return {
    attachViewToDom,
    removeViewFromDom
  };
};
var createTriggerController = () => {
  let destroyTriggerInteraction;
  const removeClickListener = () => {
    if (destroyTriggerInteraction) {
      destroyTriggerInteraction();
      destroyTriggerInteraction = void 0;
    }
  };
  const addClickListener = (el, trigger) => {
    removeClickListener();
    const triggerEl = trigger !== void 0 ? document.getElementById(trigger) : null;
    if (!triggerEl) {
      printIonWarning(`[${el.tagName.toLowerCase()}] - A trigger element with the ID "${trigger}" was not found in the DOM. The trigger element must be in the DOM when the "trigger" property is set on an overlay component.`, el);
      return;
    }
    const configureTriggerInteraction = (targetEl, overlayEl) => {
      const openOverlay = () => {
        overlayEl.present();
      };
      targetEl.addEventListener("click", openOverlay);
      return () => {
        targetEl.removeEventListener("click", openOverlay);
      };
    };
    destroyTriggerInteraction = configureTriggerInteraction(triggerEl, el);
  };
  return {
    addClickListener,
    removeClickListener
  };
};
var hideAnimatingOverlayFromScreenReaders = (overlay) => {
  if (doc === void 0) return;
  if (isPlatform("android")) {
    overlay.setAttribute("aria-hidden", "true");
  }
};
var hideUnderlyingOverlaysFromScreenReaders = (newTopMostOverlay) => {
  var _a5;
  if (doc === void 0) return;
  const overlays = getPresentedOverlays(doc);
  for (let i = overlays.length - 1; i >= 0; i--) {
    const presentedOverlay = overlays[i];
    const nextPresentedOverlay = (_a5 = overlays[i + 1]) !== null && _a5 !== void 0 ? _a5 : newTopMostOverlay;
    if (nextPresentedOverlay.hasAttribute("aria-hidden") || nextPresentedOverlay.tagName !== "ION-TOAST") {
      presentedOverlay.setAttribute("aria-hidden", "true");
    }
  }
};
var revealOverlaysToScreenReaders = () => {
  if (doc === void 0) return;
  const overlays = getPresentedOverlays(doc);
  for (let i = overlays.length - 1; i >= 0; i--) {
    const currentOverlay = overlays[i];
    currentOverlay.removeAttribute("aria-hidden");
    if (currentOverlay.tagName !== "ION-TOAST") {
      break;
    }
  }
};
var FOCUS_TRAP_DISABLE_CLASS = "ion-disable-focus-trap";

// node_modules/@ionic/core/components/theme.js
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

// node_modules/@ionic/core/components/index.js
var IonicSlides = (opts) => {
  const {
    swiper,
    extendParams
  } = opts;
  const slidesParams = {
    effect: void 0,
    direction: "horizontal",
    initialSlide: 0,
    loop: false,
    parallax: false,
    slidesPerView: 1,
    spaceBetween: 0,
    speed: 300,
    slidesPerColumn: 1,
    slidesPerColumnFill: "column",
    slidesPerGroup: 1,
    centeredSlides: false,
    slidesOffsetBefore: 0,
    slidesOffsetAfter: 0,
    touchEventsTarget: "container",
    freeMode: false,
    freeModeMomentum: true,
    freeModeMomentumRatio: 1,
    freeModeMomentumBounce: true,
    freeModeMomentumBounceRatio: 1,
    freeModeMomentumVelocityRatio: 1,
    freeModeSticky: false,
    freeModeMinimumVelocity: 0.02,
    autoHeight: false,
    setWrapperSize: false,
    zoom: {
      maxRatio: 3,
      minRatio: 1,
      toggle: false
    },
    touchRatio: 1,
    touchAngle: 45,
    simulateTouch: true,
    touchStartPreventDefault: false,
    shortSwipes: true,
    longSwipes: true,
    longSwipesRatio: 0.5,
    longSwipesMs: 300,
    followFinger: true,
    threshold: 0,
    touchMoveStopPropagation: true,
    touchReleaseOnEdges: false,
    iOSEdgeSwipeDetection: false,
    iOSEdgeSwipeThreshold: 20,
    resistance: true,
    resistanceRatio: 0.85,
    watchSlidesProgress: false,
    watchSlidesVisibility: false,
    preventClicks: true,
    preventClicksPropagation: true,
    slideToClickedSlide: false,
    loopAdditionalSlides: 0,
    noSwiping: true,
    runCallbacksOnInit: true,
    coverflowEffect: {
      rotate: 50,
      stretch: 0,
      depth: 100,
      modifier: 1,
      slideShadows: true
    },
    flipEffect: {
      slideShadows: true,
      limitRotation: true
    },
    cubeEffect: {
      slideShadows: true,
      shadow: true,
      shadowOffset: 20,
      shadowScale: 0.94
    },
    fadeEffect: {
      crossFade: false
    },
    a11y: {
      prevSlideMessage: "Previous slide",
      nextSlideMessage: "Next slide",
      firstSlideMessage: "This is the first slide",
      lastSlideMessage: "This is the last slide"
    }
  };
  if (swiper.pagination) {
    slidesParams.pagination = {
      type: "bullets",
      clickable: false,
      hideOnClick: false
    };
  }
  if (swiper.scrollbar) {
    slidesParams.scrollbar = {
      hide: true
    };
  }
  extendParams(slidesParams);
};

// node_modules/@ionic/angular/fesm2022/ionic-angular-common.mjs
var _c0 = ["tabsInner"];
var MenuController = class {
  menuController;
  constructor(menuController2) {
    this.menuController = menuController2;
  }
  /**
   * Programmatically open the Menu.
   * @param [menuId]  Optionally get the menu by its id, or side.
   * @return returns a promise when the menu is fully opened
   */
  open(menuId) {
    return this.menuController.open(menuId);
  }
  /**
   * Programmatically close the Menu. If no `menuId` is given as the first
   * argument then it'll close any menu which is open. If a `menuId`
   * is given then it'll close that exact menu.
   * @param [menuId]  Optionally get the menu by its id, or side.
   * @return returns a promise when the menu is fully closed
   */
  close(menuId) {
    return this.menuController.close(menuId);
  }
  /**
   * Toggle the menu. If it's closed, it will open, and if opened, it
   * will close.
   * @param [menuId]  Optionally get the menu by its id, or side.
   * @return returns a promise when the menu has been toggled
   */
  toggle(menuId) {
    return this.menuController.toggle(menuId);
  }
  /**
   * Used to enable or disable a menu. For example, there could be multiple
   * left menus, but only one of them should be able to be opened at the same
   * time. If there are multiple menus on the same side, then enabling one menu
   * will also automatically disable all the others that are on the same side.
   * @param [menuId]  Optionally get the menu by its id, or side.
   * @return Returns the instance of the menu, which is useful for chaining.
   */
  enable(shouldEnable, menuId) {
    return this.menuController.enable(shouldEnable, menuId);
  }
  /**
   * Used to enable or disable the ability to swipe open the menu.
   * @param shouldEnable  True if it should be swipe-able, false if not.
   * @param [menuId]  Optionally get the menu by its id, or side.
   * @return Returns the instance of the menu, which is useful for chaining.
   */
  swipeGesture(shouldEnable, menuId) {
    return this.menuController.swipeGesture(shouldEnable, menuId);
  }
  /**
   * @param [menuId] Optionally get the menu by its id, or side.
   * @return Returns true if the specified menu is currently open, otherwise false.
   * If the menuId is not specified, it returns true if ANY menu is currenly open.
   */
  isOpen(menuId) {
    return this.menuController.isOpen(menuId);
  }
  /**
   * @param [menuId]  Optionally get the menu by its id, or side.
   * @return Returns true if the menu is currently enabled, otherwise false.
   */
  isEnabled(menuId) {
    return this.menuController.isEnabled(menuId);
  }
  /**
   * Used to get a menu instance. If a `menuId` is not provided then it'll
   * return the first menu found. If a `menuId` is `left` or `right`, then
   * it'll return the enabled menu on that side. Otherwise, if a `menuId` is
   * provided, then it'll try to find the menu using the menu's `id`
   * property. If a menu is not found then it'll return `null`.
   * @param [menuId]  Optionally get the menu by its id, or side.
   * @return Returns the instance of the menu if found, otherwise `null`.
   */
  get(menuId) {
    return this.menuController.get(menuId);
  }
  /**
   * @return Returns the instance of the menu already opened, otherwise `null`.
   */
  getOpen() {
    return this.menuController.getOpen();
  }
  /**
   * @return Returns an array of all menu instances.
   */
  getMenus() {
    return this.menuController.getMenus();
  }
  registerAnimation(name, animation) {
    return this.menuController.registerAnimation(name, animation);
  }
  isAnimating() {
    return this.menuController.isAnimating();
  }
  _getOpenSync() {
    return this.menuController._getOpenSync();
  }
  _createAnimation(type, menuCmp) {
    return this.menuController._createAnimation(type, menuCmp);
  }
  _register(menu) {
    return this.menuController._register(menu);
  }
  _unregister(menu) {
    return this.menuController._unregister(menu);
  }
  _setOpen(menu, shouldOpen, animated) {
    return this.menuController._setOpen(menu, shouldOpen, animated);
  }
};
var _DomController = class _DomController {
  /**
   * Schedules a task to run during the READ phase of the next frame.
   * This task should only read the DOM, but never modify it.
   */
  read(cb) {
    getQueue().read(cb);
  }
  /**
   * Schedules a task to run during the WRITE phase of the next frame.
   * This task should write the DOM, but never READ it.
   */
  write(cb) {
    getQueue().write(cb);
  }
};
/** @nocollapse */
__publicField(_DomController, "ɵfac", function DomController_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _DomController)();
});
/** @nocollapse */
__publicField(_DomController, "ɵprov", ɵɵdefineInjectable({
  token: _DomController,
  factory: _DomController.ɵfac,
  providedIn: "root"
}));
var DomController = _DomController;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(DomController, [{
    type: Injectable,
    args: [{
      providedIn: "root"
    }]
  }], null, null);
})();
var getQueue = () => {
  const win = typeof window !== "undefined" ? window : null;
  if (win != null) {
    const Ionic = win.Ionic;
    if (Ionic == null ? void 0 : Ionic.queue) {
      return Ionic.queue;
    }
    return {
      read: (cb) => win.requestAnimationFrame(cb),
      write: (cb) => win.requestAnimationFrame(cb)
    };
  }
  return {
    read: (cb) => cb(),
    write: (cb) => cb()
  };
};
var _Platform = class _Platform {
  doc;
  _readyPromise;
  win;
  /**
   * @hidden
   */
  backButton = new Subject();
  /**
   * The keyboardDidShow event emits when the
   * on-screen keyboard is presented.
   */
  keyboardDidShow = new Subject();
  /**
   * The keyboardDidHide event emits when the
   * on-screen keyboard is hidden.
   */
  keyboardDidHide = new Subject();
  /**
   * The pause event emits when the native platform puts the application
   * into the background, typically when the user switches to a different
   * application. This event would emit when a Cordova app is put into
   * the background, however, it would not fire on a standard web browser.
   */
  pause = new Subject();
  /**
   * The resume event emits when the native platform pulls the application
   * out from the background. This event would emit when a Cordova app comes
   * out from the background, however, it would not fire on a standard web browser.
   */
  resume = new Subject();
  /**
   * The resize event emits when the browser window has changed dimensions. This
   * could be from a browser window being physically resized, or from a device
   * changing orientation.
   */
  resize = new Subject();
  constructor(doc2, zone) {
    this.doc = doc2;
    zone.run(() => {
      var _a5;
      this.win = doc2.defaultView;
      this.backButton.subscribeWithPriority = function(priority, callback) {
        return this.subscribe((ev) => {
          return ev.register(priority, (processNextHandler) => zone.run(() => callback(processNextHandler)));
        });
      };
      proxyEvent(this.pause, doc2, "pause", zone);
      proxyEvent(this.resume, doc2, "resume", zone);
      proxyEvent(this.backButton, doc2, "ionBackButton", zone);
      proxyEvent(this.resize, this.win, "resize", zone);
      proxyEvent(this.keyboardDidShow, this.win, "ionKeyboardDidShow", zone);
      proxyEvent(this.keyboardDidHide, this.win, "ionKeyboardDidHide", zone);
      let readyResolve;
      this._readyPromise = new Promise((res) => {
        readyResolve = res;
      });
      if ((_a5 = this.win) == null ? void 0 : _a5["cordova"]) {
        doc2.addEventListener("deviceready", () => {
          readyResolve("cordova");
        }, {
          once: true
        });
      } else {
        readyResolve("dom");
      }
    });
  }
  /**
   * @returns returns true/false based on platform.
   * @description
   * Depending on the platform the user is on, `is(platformName)` will
   * return `true` or `false`. Note that the same app can return `true`
   * for more than one platform name. For example, an app running from
   * an iPad would return `true` for the platform names: `mobile`,
   * `ios`, `ipad`, and `tablet`. Additionally, if the app was running
   * from Cordova then `cordova` would be true, and if it was running
   * from a web browser on the iPad then `mobileweb` would be `true`.
   *
   * ```
   * import { Platform } from 'ionic-angular';
   *
   * @Component({...})
   * export MyPage {
   *   constructor(public platform: Platform) {
   *     if (this.platform.is('ios')) {
   *       // This will only print when on iOS
   *       console.log('I am an iOS device!');
   *     }
   *   }
   * }
   * ```
   *
   * | Platform Name   | Description                        |
   * |-----------------|------------------------------------|
   * | android         | on a device running Android.       |
   * | capacitor       | on a device running Capacitor.     |
   * | cordova         | on a device running Cordova.       |
   * | ios             | on a device running iOS.           |
   * | ipad            | on an iPad device.                 |
   * | iphone          | on an iPhone device.               |
   * | phablet         | on a phablet device.               |
   * | tablet          | on a tablet device.                |
   * | electron        | in Electron on a desktop device.   |
   * | pwa             | as a PWA app.                      |
   * | mobile          | on a mobile device.                |
   * | mobileweb       | on a mobile device in a browser.   |
   * | desktop         | on a desktop device.               |
   * | hybrid          | is a cordova or capacitor app.     |
   *
   */
  is(platformName) {
    return isPlatform(this.win, platformName);
  }
  /**
   * @returns the array of platforms
   * @description
   * Depending on what device you are on, `platforms` can return multiple values.
   * Each possible value is a hierarchy of platforms. For example, on an iPhone,
   * it would return `mobile`, `ios`, and `iphone`.
   *
   * ```
   * import { Platform } from 'ionic-angular';
   *
   * @Component({...})
   * export MyPage {
   *   constructor(public platform: Platform) {
   *     // This will print an array of the current platforms
   *     console.log(this.platform.platforms());
   *   }
   * }
   * ```
   */
  platforms() {
    return getPlatforms(this.win);
  }
  /**
   * Returns a promise when the platform is ready and native functionality
   * can be called. If the app is running from within a web browser, then
   * the promise will resolve when the DOM is ready. When the app is running
   * from an application engine such as Cordova, then the promise will
   * resolve when Cordova triggers the `deviceready` event.
   *
   * The resolved value is the `readySource`, which states which platform
   * ready was used. For example, when Cordova is ready, the resolved ready
   * source is `cordova`. The default ready source value will be `dom`. The
   * `readySource` is useful if different logic should run depending on the
   * platform the app is running from. For example, only Cordova can execute
   * the status bar plugin, so the web should not run status bar plugin logic.
   *
   * ```
   * import { Component } from '@angular/core';
   * import { Platform } from 'ionic-angular';
   *
   * @Component({...})
   * export MyApp {
   *   constructor(public platform: Platform) {
   *     this.platform.ready().then((readySource) => {
   *       console.log('Platform ready from', readySource);
   *       // Platform now ready, execute any required native code
   *     });
   *   }
   * }
   * ```
   */
  ready() {
    return this._readyPromise;
  }
  /**
   * Returns if this app is using right-to-left language direction or not.
   * We recommend the app's `index.html` file already has the correct `dir`
   * attribute value set, such as `<html dir="ltr">` or `<html dir="rtl">`.
   * [W3C: Structural markup and right-to-left text in HTML](http://www.w3.org/International/questions/qa-html-dir)
   */
  get isRTL() {
    return this.doc.dir === "rtl";
  }
  /**
   * Get the query string parameter
   */
  getQueryParam(key) {
    return readQueryParam(this.win.location.href, key);
  }
  /**
   * Returns `true` if the app is in landscape mode.
   */
  isLandscape() {
    return !this.isPortrait();
  }
  /**
   * Returns `true` if the app is in portrait mode.
   */
  isPortrait() {
    var _a5, _b;
    return (_b = (_a5 = this.win).matchMedia) == null ? void 0 : _b.call(_a5, "(orientation: portrait)").matches;
  }
  testUserAgent(expression) {
    const nav = this.win.navigator;
    return !!((nav == null ? void 0 : nav.userAgent) && nav.userAgent.indexOf(expression) >= 0);
  }
  /**
   * Get the current url.
   */
  url() {
    return this.win.location.href;
  }
  /**
   * Gets the width of the platform's viewport using `window.innerWidth`.
   */
  width() {
    return this.win.innerWidth;
  }
  /**
   * Gets the height of the platform's viewport using `window.innerHeight`.
   */
  height() {
    return this.win.innerHeight;
  }
};
/** @nocollapse */
__publicField(_Platform, "ɵfac", function Platform_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _Platform)(ɵɵinject(DOCUMENT), ɵɵinject(NgZone));
});
/** @nocollapse */
__publicField(_Platform, "ɵprov", ɵɵdefineInjectable({
  token: _Platform,
  factory: _Platform.ɵfac,
  providedIn: "root"
}));
var Platform = _Platform;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(Platform, [{
    type: Injectable,
    args: [{
      providedIn: "root"
    }]
  }], function() {
    return [{
      type: void 0,
      decorators: [{
        type: Inject,
        args: [DOCUMENT]
      }]
    }, {
      type: NgZone
    }];
  }, null);
})();
var readQueryParam = (url, key) => {
  key = key.replace(/[[\]\\]/g, "\\$&");
  const regex = new RegExp("[\\?&]" + key + "=([^&#]*)");
  const results = regex.exec(url);
  return results ? decodeURIComponent(results[1].replace(/\+/g, " ")) : null;
};
var proxyEvent = (emitter, el, eventName, zone) => {
  if (el) {
    el.addEventListener(eventName, (ev) => {
      zone.run(() => {
        const value = ev != null ? ev.detail : void 0;
        emitter.next(value);
      });
    });
  }
};
var _NavController = class _NavController {
  location;
  serializer;
  router;
  topOutlet;
  direction = DEFAULT_DIRECTION;
  animated = DEFAULT_ANIMATED;
  animationBuilder;
  guessDirection = "forward";
  guessAnimation;
  lastNavId = -1;
  constructor(platform, location, serializer, router) {
    this.location = location;
    this.serializer = serializer;
    this.router = router;
    if (router) {
      router.events.subscribe((ev) => {
        if (ev instanceof NavigationStart) {
          const id = ev.restoredState ? ev.restoredState.navigationId : ev.id;
          this.guessDirection = this.guessAnimation = id < this.lastNavId ? "back" : "forward";
          this.lastNavId = this.guessDirection === "forward" ? ev.id : id;
        }
      });
    }
    platform.backButton.subscribeWithPriority(0, (processNextHandler) => {
      this.pop();
      processNextHandler();
    });
  }
  /**
   * This method uses Angular's [Router](https://angular.io/api/router/Router) under the hood,
   * it's equivalent to calling `this.router.navigateByUrl()`, but it's explicit about the **direction** of the transition.
   *
   * Going **forward** means that a new page is going to be pushed to the stack of the outlet (ion-router-outlet),
   * and that it will show a "forward" animation by default.
   *
   * Navigating forward can also be triggered in a declarative manner by using the `[routerDirection]` directive:
   *
   * ```html
   * <a routerLink="/path/to/page" routerDirection="forward">Link</a>
   * ```
   */
  navigateForward(url, options = {}) {
    this.setDirection("forward", options.animated, options.animationDirection, options.animation);
    return this.navigate(url, options);
  }
  /**
   * This method uses Angular's [Router](https://angular.io/api/router/Router) under the hood,
   * it's equivalent to calling:
   *
   * ```ts
   * this.navController.setDirection('back');
   * this.router.navigateByUrl(path);
   * ```
   *
   * Going **back** means that all the pages in the stack until the navigated page is found will be popped,
   * and that it will show a "back" animation by default.
   *
   * Navigating back can also be triggered in a declarative manner by using the `[routerDirection]` directive:
   *
   * ```html
   * <a routerLink="/path/to/page" routerDirection="back">Link</a>
   * ```
   */
  navigateBack(url, options = {}) {
    this.setDirection("back", options.animated, options.animationDirection, options.animation);
    return this.navigate(url, options);
  }
  /**
   * This method uses Angular's [Router](https://angular.io/api/router/Router) under the hood,
   * it's equivalent to calling:
   *
   * ```ts
   * this.navController.setDirection('root');
   * this.router.navigateByUrl(path);
   * ```
   *
   * Going **root** means that all existing pages in the stack will be removed,
   * and the navigated page will become the single page in the stack.
   *
   * Navigating root can also be triggered in a declarative manner by using the `[routerDirection]` directive:
   *
   * ```html
   * <a routerLink="/path/to/page" routerDirection="root">Link</a>
   * ```
   */
  navigateRoot(url, options = {}) {
    this.setDirection("root", options.animated, options.animationDirection, options.animation);
    return this.navigate(url, options);
  }
  /**
   * Same as [Location](https://angular.io/api/common/Location)'s back() method.
   * It will use the standard `window.history.back()` under the hood, but featuring a `back` animation
   * by default.
   */
  back(options = {
    animated: true,
    animationDirection: "back"
  }) {
    this.setDirection("back", options.animated, options.animationDirection, options.animation);
    return this.location.back();
  }
  /**
   * This methods goes back in the context of Ionic's stack navigation.
   *
   * It recursively finds the top active `ion-router-outlet` and calls `pop()`.
   * This is the recommended way to go back when you are using `ion-router-outlet`.
   *
   * Resolves to `true` if it was able to pop.
   */
  pop() {
    return __async(this, null, function* () {
      let outlet = this.topOutlet;
      while (outlet) {
        if (yield outlet.pop()) {
          return true;
        } else {
          outlet = outlet.parentOutlet;
        }
      }
      return false;
    });
  }
  /**
   * This methods specifies the direction of the next navigation performed by the Angular router.
   *
   * `setDirection()` does not trigger any transition, it just sets some flags to be consumed by `ion-router-outlet`.
   *
   * It's recommended to use `navigateForward()`, `navigateBack()` and `navigateRoot()` instead of `setDirection()`.
   */
  setDirection(direction, animated, animationDirection, animationBuilder) {
    this.direction = direction;
    this.animated = getAnimation(direction, animated, animationDirection);
    this.animationBuilder = animationBuilder;
  }
  /**
   * @internal
   */
  setTopOutlet(outlet) {
    this.topOutlet = outlet;
  }
  /**
   * @internal
   */
  consumeTransition() {
    let direction = "root";
    let animation;
    const animationBuilder = this.animationBuilder;
    if (this.direction === "auto") {
      direction = this.guessDirection;
      animation = this.guessAnimation;
    } else {
      animation = this.animated;
      direction = this.direction;
    }
    this.direction = DEFAULT_DIRECTION;
    this.animated = DEFAULT_ANIMATED;
    this.animationBuilder = void 0;
    return {
      direction,
      animation,
      animationBuilder
    };
  }
  navigate(url, options) {
    if (Array.isArray(url)) {
      return this.router.navigate(url, options);
    } else {
      const urlTree = this.serializer.parse(url.toString());
      if (options.queryParams !== void 0) {
        urlTree.queryParams = __spreadValues({}, options.queryParams);
      }
      if (options.fragment !== void 0) {
        urlTree.fragment = options.fragment;
      }
      return this.router.navigateByUrl(urlTree, options);
    }
  }
};
/** @nocollapse */
__publicField(_NavController, "ɵfac", function NavController_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _NavController)(ɵɵinject(Platform), ɵɵinject(Location), ɵɵinject(UrlSerializer), ɵɵinject(Router, 8));
});
/** @nocollapse */
__publicField(_NavController, "ɵprov", ɵɵdefineInjectable({
  token: _NavController,
  factory: _NavController.ɵfac,
  providedIn: "root"
}));
var NavController = _NavController;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NavController, [{
    type: Injectable,
    args: [{
      providedIn: "root"
    }]
  }], function() {
    return [{
      type: Platform
    }, {
      type: Location
    }, {
      type: UrlSerializer
    }, {
      type: Router,
      decorators: [{
        type: Optional
      }]
    }];
  }, null);
})();
var getAnimation = (direction, animated, animationDirection) => {
  if (animated === false) {
    return void 0;
  }
  if (animationDirection !== void 0) {
    return animationDirection;
  }
  if (direction === "forward" || direction === "back") {
    return direction;
  } else if (direction === "root" && animated === true) {
    return "forward";
  }
  return void 0;
};
var DEFAULT_DIRECTION = "auto";
var DEFAULT_ANIMATED = void 0;
var _Config = class _Config {
  get(key, fallback) {
    const c = getConfig();
    if (c) {
      return c.get(key, fallback);
    }
    return null;
  }
  getBoolean(key, fallback) {
    const c = getConfig();
    if (c) {
      return c.getBoolean(key, fallback);
    }
    return false;
  }
  getNumber(key, fallback) {
    const c = getConfig();
    if (c) {
      return c.getNumber(key, fallback);
    }
    return 0;
  }
};
/** @nocollapse */
__publicField(_Config, "ɵfac", function Config_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _Config)();
});
/** @nocollapse */
__publicField(_Config, "ɵprov", ɵɵdefineInjectable({
  token: _Config,
  factory: _Config.ɵfac,
  providedIn: "root"
}));
var Config = _Config;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(Config, [{
    type: Injectable,
    args: [{
      providedIn: "root"
    }]
  }], null, null);
})();
var ConfigToken = new InjectionToken("USERCONFIG");
var getConfig = () => {
  if (typeof window !== "undefined") {
    const Ionic = window.Ionic;
    if (Ionic == null ? void 0 : Ionic.config) {
      return Ionic.config;
    }
  }
  return null;
};
var NavParams = class {
  data;
  constructor(data = {}) {
    this.data = data;
    console.warn(`[Ionic Warning]: NavParams has been deprecated in favor of using Angular's input API. Developers should migrate to either the @Input decorator or the Signals-based input API.`);
  }
  /**
   * Get the value of a nav-parameter for the current view
   *
   * ```ts
   * import { NavParams } from 'ionic-angular';
   *
   * export class MyClass{
   *  constructor(public navParams: NavParams){
   *    // userParams is an object we have in our nav-parameters
   *    this.navParams.get('userParams');
   *  }
   * }
   * ```
   *
   * @param param Which param you want to look up
   */
  get(param) {
    return this.data[param];
  }
};
var _AngularDelegate = class _AngularDelegate {
  zone = inject(NgZone);
  applicationRef = inject(ApplicationRef);
  config = inject(ConfigToken);
  create(environmentInjector, injector, elementReferenceKey) {
    var _a5;
    return new AngularFrameworkDelegate(environmentInjector, injector, this.applicationRef, this.zone, elementReferenceKey, (_a5 = this.config.useSetInputAPI) != null ? _a5 : false);
  }
};
/** @nocollapse */
__publicField(_AngularDelegate, "ɵfac", function AngularDelegate_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _AngularDelegate)();
});
/** @nocollapse */
__publicField(_AngularDelegate, "ɵprov", ɵɵdefineInjectable({
  token: _AngularDelegate,
  factory: _AngularDelegate.ɵfac
}));
var AngularDelegate = _AngularDelegate;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(AngularDelegate, [{
    type: Injectable
  }], null, null);
})();
var AngularFrameworkDelegate = class {
  environmentInjector;
  injector;
  applicationRef;
  zone;
  elementReferenceKey;
  enableSignalsSupport;
  elRefMap = /* @__PURE__ */ new WeakMap();
  elEventsMap = /* @__PURE__ */ new WeakMap();
  constructor(environmentInjector, injector, applicationRef, zone, elementReferenceKey, enableSignalsSupport) {
    this.environmentInjector = environmentInjector;
    this.injector = injector;
    this.applicationRef = applicationRef;
    this.zone = zone;
    this.elementReferenceKey = elementReferenceKey;
    this.enableSignalsSupport = enableSignalsSupport;
  }
  attachViewToDom(container, component, params, cssClasses) {
    return this.zone.run(() => {
      return new Promise((resolve) => {
        const componentProps = __spreadValues({}, params);
        if (this.elementReferenceKey !== void 0) {
          componentProps[this.elementReferenceKey] = container;
        }
        const el = attachView(this.zone, this.environmentInjector, this.injector, this.applicationRef, this.elRefMap, this.elEventsMap, container, component, componentProps, cssClasses, this.elementReferenceKey, this.enableSignalsSupport);
        resolve(el);
      });
    });
  }
  removeViewFromDom(_container, component) {
    return this.zone.run(() => {
      return new Promise((resolve) => {
        const componentRef = this.elRefMap.get(component);
        if (componentRef) {
          componentRef.destroy();
          this.elRefMap.delete(component);
          const unbindEvents = this.elEventsMap.get(component);
          if (unbindEvents) {
            unbindEvents();
            this.elEventsMap.delete(component);
          }
        }
        resolve();
      });
    });
  }
};
var attachView = (zone, environmentInjector, injector, applicationRef, elRefMap, elEventsMap, container, component, params, cssClasses, elementReferenceKey, enableSignalsSupport) => {
  const childInjector = Injector.create({
    providers: getProviders(params),
    parent: injector
  });
  const componentRef = createComponent(component, {
    environmentInjector,
    elementInjector: childInjector
  });
  const instance = componentRef.instance;
  const hostElement = componentRef.location.nativeElement;
  if (params) {
    if (elementReferenceKey && instance[elementReferenceKey] !== void 0) {
      console.error(`[Ionic Error]: ${elementReferenceKey} is a reserved property when using ${container.tagName.toLowerCase()}. Rename or remove the "${elementReferenceKey}" property from ${component.name}.`);
    }
    if (enableSignalsSupport === true && componentRef.setInput !== void 0) {
      const _a5 = params, {
        modal,
        popover
      } = _a5, otherParams = __objRest(_a5, [
        "modal",
        "popover"
      ]);
      for (const key in otherParams) {
        componentRef.setInput(key, otherParams[key]);
      }
      if (modal !== void 0) {
        Object.assign(instance, {
          modal
        });
      }
      if (popover !== void 0) {
        Object.assign(instance, {
          popover
        });
      }
    } else {
      Object.assign(instance, params);
    }
  }
  if (cssClasses) {
    for (const cssClass of cssClasses) {
      hostElement.classList.add(cssClass);
    }
  }
  const unbindEvents = bindLifecycleEvents(zone, instance, hostElement);
  container.appendChild(hostElement);
  applicationRef.attachView(componentRef.hostView);
  elRefMap.set(hostElement, componentRef);
  elEventsMap.set(hostElement, unbindEvents);
  return hostElement;
};
var LIFECYCLES = [LIFECYCLE_WILL_ENTER, LIFECYCLE_DID_ENTER, LIFECYCLE_WILL_LEAVE, LIFECYCLE_DID_LEAVE, LIFECYCLE_WILL_UNLOAD];
var bindLifecycleEvents = (zone, instance, element) => {
  return zone.run(() => {
    const unregisters = LIFECYCLES.filter((eventName) => typeof instance[eventName] === "function").map((eventName) => {
      const handler = (ev) => instance[eventName](ev.detail);
      element.addEventListener(eventName, handler);
      return () => element.removeEventListener(eventName, handler);
    });
    return () => unregisters.forEach((fn) => fn());
  });
};
var NavParamsToken = new InjectionToken("NavParamsToken");
var getProviders = (params) => {
  return [{
    provide: NavParamsToken,
    useValue: params
  }, {
    provide: NavParams,
    useFactory: provideNavParamsInjectable,
    deps: [NavParamsToken]
  }];
};
var provideNavParamsInjectable = (params) => {
  return new NavParams(params);
};
var proxyInputs = (Cmp, inputs) => {
  const Prototype = Cmp.prototype;
  inputs.forEach((item) => {
    Object.defineProperty(Prototype, item, {
      get() {
        return this.el[item];
      },
      set(val) {
        this.z.runOutsideAngular(() => this.el[item] = val);
      }
    });
  });
};
var proxyMethods = (Cmp, methods) => {
  const Prototype = Cmp.prototype;
  methods.forEach((methodName) => {
    Prototype[methodName] = function() {
      const args = arguments;
      return this.z.runOutsideAngular(() => this.el[methodName].apply(this.el, args));
    };
  });
};
var proxyOutputs = (instance, el, events) => {
  events.forEach((eventName) => instance[eventName] = fromEvent(el, eventName));
};
function ProxyCmp(opts) {
  const decorator = function(cls) {
    const {
      defineCustomElementFn,
      inputs,
      methods
    } = opts;
    if (defineCustomElementFn !== void 0) {
      defineCustomElementFn();
    }
    if (inputs) {
      proxyInputs(cls, inputs);
    }
    if (methods) {
      proxyMethods(cls, methods);
    }
    return cls;
  };
  return decorator;
}
var POPOVER_INPUTS = ["alignment", "animated", "arrow", "keepContentsMounted", "backdropDismiss", "cssClass", "dismissOnSelect", "enterAnimation", "event", "focusTrap", "isOpen", "keyboardClose", "leaveAnimation", "mode", "showBackdrop", "translucent", "trigger", "triggerAction", "reference", "size", "side"];
var POPOVER_METHODS = ["present", "dismiss", "onDidDismiss", "onWillDismiss"];
var _a;
var IonPopover = (_a = class {
  z;
  // TODO(FW-2827): type
  template;
  isCmpOpen = false;
  el;
  constructor(c, r, z) {
    this.z = z;
    this.el = r.nativeElement;
    this.el.addEventListener("ionMount", () => {
      this.isCmpOpen = true;
      c.detectChanges();
    });
    this.el.addEventListener("didDismiss", () => {
      this.isCmpOpen = false;
      c.detectChanges();
    });
    proxyOutputs(this, this.el, ["ionPopoverDidPresent", "ionPopoverWillPresent", "ionPopoverWillDismiss", "ionPopoverDidDismiss", "didPresent", "willPresent", "willDismiss", "didDismiss"]);
  }
}, /** @nocollapse */
__publicField(_a, "ɵfac", function IonPopover_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a, "ɵdir", ɵɵdefineDirective({
  type: _a,
  selectors: [["ion-popover"]],
  contentQueries: function IonPopover_ContentQueries(rf, ctx, dirIndex) {
    if (rf & 1) {
      ɵɵcontentQuery(dirIndex, TemplateRef, 5);
    }
    if (rf & 2) {
      let _t;
      ɵɵqueryRefresh(_t = ɵɵloadQuery()) && (ctx.template = _t.first);
    }
  },
  inputs: {
    alignment: "alignment",
    animated: "animated",
    arrow: "arrow",
    keepContentsMounted: "keepContentsMounted",
    backdropDismiss: "backdropDismiss",
    cssClass: "cssClass",
    dismissOnSelect: "dismissOnSelect",
    enterAnimation: "enterAnimation",
    event: "event",
    focusTrap: "focusTrap",
    isOpen: "isOpen",
    keyboardClose: "keyboardClose",
    leaveAnimation: "leaveAnimation",
    mode: "mode",
    showBackdrop: "showBackdrop",
    translucent: "translucent",
    trigger: "trigger",
    triggerAction: "triggerAction",
    reference: "reference",
    size: "size",
    side: "side"
  },
  standalone: false
})), _a);
IonPopover = __decorate([
  ProxyCmp({
    inputs: POPOVER_INPUTS,
    methods: POPOVER_METHODS
  })
  /**
   * @Component extends from @Directive
   * so by defining the inputs here we
   * do not need to re-define them for the
   * lazy loaded popover.
   */
], IonPopover);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonPopover, [{
    type: Directive,
    args: [{
      selector: "ion-popover",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: POPOVER_INPUTS
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, {
    template: [{
      type: ContentChild,
      args: [TemplateRef, {
        static: false
      }]
    }]
  });
})();
var MODAL_INPUTS = ["animated", "keepContentsMounted", "backdropBreakpoint", "backdropDismiss", "breakpoints", "canDismiss", "cssClass", "enterAnimation", "expandToScroll", "event", "focusTrap", "handle", "handleBehavior", "initialBreakpoint", "isOpen", "keyboardClose", "leaveAnimation", "mode", "presentingElement", "showBackdrop", "translucent", "trigger"];
var MODAL_METHODS = ["present", "dismiss", "onDidDismiss", "onWillDismiss", "setCurrentBreakpoint", "getCurrentBreakpoint"];
var _a2;
var IonModal = (_a2 = class {
  z;
  // TODO(FW-2827): type
  template;
  isCmpOpen = false;
  el;
  constructor(c, r, z) {
    this.z = z;
    this.el = r.nativeElement;
    this.el.addEventListener("ionMount", () => {
      this.isCmpOpen = true;
      c.detectChanges();
    });
    this.el.addEventListener("didDismiss", () => {
      this.isCmpOpen = false;
      c.detectChanges();
    });
    proxyOutputs(this, this.el, ["ionModalDidPresent", "ionModalWillPresent", "ionModalWillDismiss", "ionModalDidDismiss", "ionBreakpointDidChange", "didPresent", "willPresent", "willDismiss", "didDismiss"]);
  }
}, /** @nocollapse */
__publicField(_a2, "ɵfac", function IonModal_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a2)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a2, "ɵdir", ɵɵdefineDirective({
  type: _a2,
  selectors: [["ion-modal"]],
  contentQueries: function IonModal_ContentQueries(rf, ctx, dirIndex) {
    if (rf & 1) {
      ɵɵcontentQuery(dirIndex, TemplateRef, 5);
    }
    if (rf & 2) {
      let _t;
      ɵɵqueryRefresh(_t = ɵɵloadQuery()) && (ctx.template = _t.first);
    }
  },
  inputs: {
    animated: "animated",
    keepContentsMounted: "keepContentsMounted",
    backdropBreakpoint: "backdropBreakpoint",
    backdropDismiss: "backdropDismiss",
    breakpoints: "breakpoints",
    canDismiss: "canDismiss",
    cssClass: "cssClass",
    enterAnimation: "enterAnimation",
    expandToScroll: "expandToScroll",
    event: "event",
    focusTrap: "focusTrap",
    handle: "handle",
    handleBehavior: "handleBehavior",
    initialBreakpoint: "initialBreakpoint",
    isOpen: "isOpen",
    keyboardClose: "keyboardClose",
    leaveAnimation: "leaveAnimation",
    mode: "mode",
    presentingElement: "presentingElement",
    showBackdrop: "showBackdrop",
    translucent: "translucent",
    trigger: "trigger"
  },
  standalone: false
})), _a2);
IonModal = __decorate([
  ProxyCmp({
    inputs: MODAL_INPUTS,
    methods: MODAL_METHODS
  })
  /**
   * @Component extends from @Directive
   * so by defining the inputs here we
   * do not need to re-define them for the
   * lazy loaded popover.
   */
], IonModal);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonModal, [{
    type: Directive,
    args: [{
      selector: "ion-modal",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: MODAL_INPUTS
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, {
    template: [{
      type: ContentChild,
      args: [TemplateRef, {
        static: false
      }]
    }]
  });
})();
var insertView = (views, view, direction) => {
  if (direction === "root") {
    return setRoot(views, view);
  } else if (direction === "forward") {
    return setForward(views, view);
  } else {
    return setBack(views, view);
  }
};
var setRoot = (views, view) => {
  views = views.filter((v) => v.stackId !== view.stackId);
  views.push(view);
  return views;
};
var setForward = (views, view) => {
  const index = views.indexOf(view);
  if (index >= 0) {
    views = views.filter((v) => v.stackId !== view.stackId || v.id <= view.id);
  } else {
    views.push(view);
  }
  return views;
};
var setBack = (views, view) => {
  const index = views.indexOf(view);
  if (index >= 0) {
    return views.filter((v) => v.stackId !== view.stackId || v.id <= view.id);
  } else {
    return setRoot(views, view);
  }
};
var getUrl = (router, activatedRoute) => {
  const urlTree = router.createUrlTree(["."], {
    relativeTo: activatedRoute
  });
  return router.serializeUrl(urlTree);
};
var isTabSwitch = (enteringView, leavingView) => {
  if (!leavingView) {
    return true;
  }
  return enteringView.stackId !== leavingView.stackId;
};
var computeStackId = (prefixUrl, url) => {
  if (!prefixUrl) {
    return void 0;
  }
  const segments = toSegments(url);
  for (let i = 0; i < segments.length; i++) {
    if (i >= prefixUrl.length) {
      return segments[i];
    }
    if (segments[i] !== prefixUrl[i]) {
      return void 0;
    }
  }
  return void 0;
};
var toSegments = (path) => {
  return path.split("/").map((s) => s.trim()).filter((s) => s !== "");
};
var destroyView = (view) => {
  if (view) {
    view.ref.destroy();
    view.unlistenEvents();
  }
};
var StackController = class {
  containerEl;
  router;
  navCtrl;
  zone;
  location;
  views = [];
  runningTask;
  skipTransition = false;
  tabsPrefix;
  activeView;
  nextId = 0;
  constructor(tabsPrefix, containerEl, router, navCtrl, zone, location) {
    this.containerEl = containerEl;
    this.router = router;
    this.navCtrl = navCtrl;
    this.zone = zone;
    this.location = location;
    this.tabsPrefix = tabsPrefix !== void 0 ? toSegments(tabsPrefix) : void 0;
  }
  createView(ref, activatedRoute) {
    var _a5;
    const url = getUrl(this.router, activatedRoute);
    const element = (_a5 = ref == null ? void 0 : ref.location) == null ? void 0 : _a5.nativeElement;
    const unlistenEvents = bindLifecycleEvents(this.zone, ref.instance, element);
    return {
      id: this.nextId++,
      stackId: computeStackId(this.tabsPrefix, url),
      unlistenEvents,
      element,
      ref,
      url
    };
  }
  getExistingView(activatedRoute) {
    const activatedUrlKey = getUrl(this.router, activatedRoute);
    const view = this.views.find((vw) => vw.url === activatedUrlKey);
    if (view) {
      view.ref.changeDetectorRef.reattach();
    }
    return view;
  }
  setActive(enteringView) {
    var _a5, _b;
    const consumeResult = this.navCtrl.consumeTransition();
    let {
      direction,
      animation,
      animationBuilder
    } = consumeResult;
    const leavingView = this.activeView;
    const tabSwitch = isTabSwitch(enteringView, leavingView);
    if (tabSwitch) {
      direction = "back";
      animation = void 0;
    }
    const viewsSnapshot = this.views.slice();
    let currentNavigation;
    const router = this.router;
    if (router.getCurrentNavigation) {
      currentNavigation = router.getCurrentNavigation();
    } else if ((_a5 = router.navigations) == null ? void 0 : _a5.value) {
      currentNavigation = router.navigations.value;
    }
    if ((_b = currentNavigation == null ? void 0 : currentNavigation.extras) == null ? void 0 : _b.replaceUrl) {
      if (this.views.length > 0) {
        this.views.splice(-1, 1);
      }
    }
    const reused = this.views.includes(enteringView);
    const views = this.insertView(enteringView, direction);
    if (!reused) {
      enteringView.ref.changeDetectorRef.detectChanges();
    }
    const customAnimation = enteringView.animationBuilder;
    if (animationBuilder === void 0 && direction === "back" && !tabSwitch && customAnimation !== void 0) {
      animationBuilder = customAnimation;
    }
    if (leavingView) {
      leavingView.animationBuilder = animationBuilder;
    }
    return this.zone.runOutsideAngular(() => {
      return this.wait(() => {
        if (leavingView) {
          leavingView.ref.changeDetectorRef.detach();
        }
        enteringView.ref.changeDetectorRef.reattach();
        return this.transition(enteringView, leavingView, animation, this.canGoBack(1), false, animationBuilder).then(() => cleanupAsync(enteringView, views, viewsSnapshot, this.location, this.zone)).then(() => ({
          enteringView,
          direction,
          animation,
          tabSwitch
        }));
      });
    });
  }
  canGoBack(deep, stackId = this.getActiveStackId()) {
    return this.getStack(stackId).length > deep;
  }
  pop(deep, stackId = this.getActiveStackId()) {
    return this.zone.run(() => {
      var _a5, _b;
      const views = this.getStack(stackId);
      if (views.length <= deep) {
        return Promise.resolve(false);
      }
      const view = views[views.length - deep - 1];
      let url = view.url;
      const viewSavedData = view.savedData;
      if (viewSavedData) {
        const primaryOutlet = viewSavedData.get("primary");
        if ((_b = (_a5 = primaryOutlet == null ? void 0 : primaryOutlet.route) == null ? void 0 : _a5._routerState) == null ? void 0 : _b.snapshot.url) {
          url = primaryOutlet.route._routerState.snapshot.url;
        }
      }
      const {
        animationBuilder
      } = this.navCtrl.consumeTransition();
      return this.navCtrl.navigateBack(url, __spreadProps(__spreadValues({}, view.savedExtras), {
        animation: animationBuilder
      })).then(() => true);
    });
  }
  startBackTransition() {
    const leavingView = this.activeView;
    if (leavingView) {
      const views = this.getStack(leavingView.stackId);
      const enteringView = views[views.length - 2];
      const customAnimation = enteringView.animationBuilder;
      return this.wait(() => {
        return this.transition(
          enteringView,
          // entering view
          leavingView,
          // leaving view
          "back",
          this.canGoBack(2),
          true,
          customAnimation
        );
      });
    }
    return Promise.resolve();
  }
  endBackTransition(shouldComplete) {
    if (shouldComplete) {
      this.skipTransition = true;
      this.pop(1);
    } else if (this.activeView) {
      cleanup(this.activeView, this.views, this.views, this.location, this.zone);
    }
  }
  getLastUrl(stackId) {
    const views = this.getStack(stackId);
    return views.length > 0 ? views[views.length - 1] : void 0;
  }
  /**
   * @internal
   */
  getRootUrl(stackId) {
    const views = this.getStack(stackId);
    return views.length > 0 ? views[0] : void 0;
  }
  getActiveStackId() {
    return this.activeView ? this.activeView.stackId : void 0;
  }
  /**
   * @internal
   */
  getActiveView() {
    return this.activeView;
  }
  hasRunningTask() {
    return this.runningTask !== void 0;
  }
  destroy() {
    this.containerEl = void 0;
    this.views.forEach(destroyView);
    this.activeView = void 0;
    this.views = [];
  }
  getStack(stackId) {
    return this.views.filter((v) => v.stackId === stackId);
  }
  insertView(enteringView, direction) {
    this.activeView = enteringView;
    this.views = insertView(this.views, enteringView, direction);
    return this.views.slice();
  }
  transition(enteringView, leavingView, direction, showGoBack, progressAnimation, animationBuilder) {
    if (this.skipTransition) {
      this.skipTransition = false;
      return Promise.resolve(false);
    }
    if (leavingView === enteringView) {
      return Promise.resolve(false);
    }
    const enteringEl = enteringView ? enteringView.element : void 0;
    const leavingEl = leavingView ? leavingView.element : void 0;
    const containerEl = this.containerEl;
    if (enteringEl && enteringEl !== leavingEl) {
      enteringEl.classList.add("ion-page");
      enteringEl.classList.add("ion-page-invisible");
      if (containerEl.commit) {
        return containerEl.commit(enteringEl, leavingEl, {
          duration: direction === void 0 ? 0 : void 0,
          direction,
          showGoBack,
          progressAnimation,
          animationBuilder
        });
      }
    }
    return Promise.resolve(false);
  }
  wait(task) {
    return __async(this, null, function* () {
      if (this.runningTask !== void 0) {
        yield this.runningTask;
        this.runningTask = void 0;
      }
      const promise = this.runningTask = task();
      promise.finally(() => this.runningTask = void 0);
      return promise;
    });
  }
};
var cleanupAsync = (activeRoute, views, viewsSnapshot, location, zone) => {
  if (typeof requestAnimationFrame === "function") {
    return new Promise((resolve) => {
      requestAnimationFrame(() => {
        cleanup(activeRoute, views, viewsSnapshot, location, zone);
        resolve();
      });
    });
  }
  return Promise.resolve();
};
var cleanup = (activeRoute, views, viewsSnapshot, location, zone) => {
  zone.run(() => viewsSnapshot.filter((view) => !views.includes(view)).forEach(destroyView));
  views.forEach((view) => {
    const locationWithoutParams = location.path().split("?")[0];
    const locationWithoutFragment = locationWithoutParams.split("#")[0];
    if (view !== activeRoute && view.url !== locationWithoutFragment) {
      const element = view.element;
      element.setAttribute("aria-hidden", "true");
      element.classList.add("ion-page-hidden");
      view.ref.changeDetectorRef.detach();
    }
  });
};
var _IonRouterOutlet = class _IonRouterOutlet {
  parentOutlet;
  nativeEl;
  activatedView = null;
  tabsPrefix;
  _swipeGesture;
  stackCtrl;
  // Maintain map of activated route proxies for each component instance
  proxyMap = /* @__PURE__ */ new WeakMap();
  // Keep the latest activated route in a subject for the proxy routes to switch map to
  currentActivatedRoute$ = new BehaviorSubject(null);
  activated = null;
  /** @internal */
  get activatedComponentRef() {
    return this.activated;
  }
  _activatedRoute = null;
  /**
   * The name of the outlet
   */
  name = PRIMARY_OUTLET;
  /** @internal */
  stackWillChange = new EventEmitter();
  /** @internal */
  stackDidChange = new EventEmitter();
  // eslint-disable-next-line @angular-eslint/no-output-rename
  activateEvents = new EventEmitter();
  // eslint-disable-next-line @angular-eslint/no-output-rename
  deactivateEvents = new EventEmitter();
  parentContexts = inject(ChildrenOutletContexts);
  location = inject(ViewContainerRef);
  environmentInjector = inject(EnvironmentInjector);
  inputBinder = inject(INPUT_BINDER, {
    optional: true
  });
  /** @nodoc */
  supportsBindingToComponentInputs = true;
  // Ionic providers
  config = inject(Config);
  navCtrl = inject(NavController);
  set animation(animation) {
    this.nativeEl.animation = animation;
  }
  set animated(animated) {
    this.nativeEl.animated = animated;
  }
  set swipeGesture(swipe) {
    this._swipeGesture = swipe;
    this.nativeEl.swipeHandler = swipe ? {
      canStart: () => this.stackCtrl.canGoBack(1) && !this.stackCtrl.hasRunningTask(),
      onStart: () => this.stackCtrl.startBackTransition(),
      onEnd: (shouldContinue) => this.stackCtrl.endBackTransition(shouldContinue)
    } : void 0;
  }
  constructor(name, tabs, commonLocation, elementRef, router, zone, activatedRoute, parentOutlet) {
    this.parentOutlet = parentOutlet;
    this.nativeEl = elementRef.nativeElement;
    this.name = name || PRIMARY_OUTLET;
    this.tabsPrefix = tabs === "true" ? getUrl(router, activatedRoute) : void 0;
    this.stackCtrl = new StackController(this.tabsPrefix, this.nativeEl, router, this.navCtrl, zone, commonLocation);
    this.parentContexts.onChildOutletCreated(this.name, this);
  }
  ngOnDestroy() {
    var _a5;
    this.stackCtrl.destroy();
    (_a5 = this.inputBinder) == null ? void 0 : _a5.unsubscribeFromRouteData(this);
  }
  getContext() {
    return this.parentContexts.getContext(this.name);
  }
  ngOnInit() {
    this.initializeOutletWithName();
  }
  // Note: Ionic deviates from the Angular Router implementation here
  initializeOutletWithName() {
    if (!this.activated) {
      const context = this.getContext();
      if (context == null ? void 0 : context.route) {
        this.activateWith(context.route, context.injector);
      }
    }
    new Promise((resolve) => componentOnReady(this.nativeEl, resolve)).then(() => {
      if (this._swipeGesture === void 0) {
        this.swipeGesture = this.config.getBoolean("swipeBackEnabled", this.nativeEl.mode === "ios");
      }
    });
  }
  get isActivated() {
    return !!this.activated;
  }
  get component() {
    if (!this.activated) {
      throw new Error("Outlet is not activated");
    }
    return this.activated.instance;
  }
  get activatedRoute() {
    if (!this.activated) {
      throw new Error("Outlet is not activated");
    }
    return this._activatedRoute;
  }
  get activatedRouteData() {
    if (this._activatedRoute) {
      return this._activatedRoute.snapshot.data;
    }
    return {};
  }
  /**
   * Called when the `RouteReuseStrategy` instructs to detach the subtree
   */
  detach() {
    throw new Error("incompatible reuse strategy");
  }
  /**
   * Called when the `RouteReuseStrategy` instructs to re-attach a previously detached subtree
   */
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  attach(_ref, _activatedRoute) {
    throw new Error("incompatible reuse strategy");
  }
  deactivate() {
    if (this.activated) {
      if (this.activatedView) {
        const context = this.getContext();
        this.activatedView.savedData = new Map(context.children["contexts"]);
        const primaryOutlet = this.activatedView.savedData.get("primary");
        if (primaryOutlet && context.route) {
          primaryOutlet.route = __spreadValues({}, context.route);
        }
        this.activatedView.savedExtras = {};
        if (context.route) {
          const contextSnapshot = context.route.snapshot;
          this.activatedView.savedExtras.queryParams = contextSnapshot.queryParams;
          this.activatedView.savedExtras.fragment = contextSnapshot.fragment;
        }
      }
      const c = this.component;
      this.activatedView = null;
      this.activated = null;
      this._activatedRoute = null;
      this.deactivateEvents.emit(c);
    }
  }
  activateWith(activatedRoute, environmentInjector) {
    var _a5, _b;
    if (this.isActivated) {
      throw new Error("Cannot activate an already activated outlet");
    }
    this._activatedRoute = activatedRoute;
    let cmpRef;
    let enteringView = this.stackCtrl.getExistingView(activatedRoute);
    if (enteringView) {
      cmpRef = this.activated = enteringView.ref;
      const saved = enteringView.savedData;
      if (saved) {
        const context = this.getContext();
        context.children["contexts"] = saved;
      }
      this.updateActivatedRouteProxy(cmpRef.instance, activatedRoute);
    } else {
      const snapshot = activatedRoute._futureSnapshot;
      const childContexts = this.parentContexts.getOrCreateContext(this.name).children;
      const component$ = new BehaviorSubject(null);
      const activatedRouteProxy = this.createActivatedRouteProxy(component$, activatedRoute);
      const injector = new OutletInjector(activatedRouteProxy, childContexts, this.location.injector);
      const component = (_a5 = snapshot.routeConfig.component) != null ? _a5 : snapshot.component;
      cmpRef = this.activated = this.outletContent.createComponent(component, {
        index: this.outletContent.length,
        injector,
        environmentInjector: environmentInjector != null ? environmentInjector : this.environmentInjector
      });
      component$.next(cmpRef.instance);
      enteringView = this.stackCtrl.createView(this.activated, activatedRoute);
      this.proxyMap.set(cmpRef.instance, activatedRouteProxy);
      this.currentActivatedRoute$.next({
        component: cmpRef.instance,
        activatedRoute
      });
    }
    (_b = this.inputBinder) == null ? void 0 : _b.bindActivatedRouteToOutletComponent(this);
    this.activatedView = enteringView;
    this.navCtrl.setTopOutlet(this);
    const leavingView = this.stackCtrl.getActiveView();
    this.stackWillChange.emit({
      enteringView,
      tabSwitch: isTabSwitch(enteringView, leavingView)
    });
    this.stackCtrl.setActive(enteringView).then((data) => {
      this.activateEvents.emit(cmpRef.instance);
      this.stackDidChange.emit(data);
    });
  }
  /**
   * Returns `true` if there are pages in the stack to go back.
   */
  canGoBack(deep = 1, stackId) {
    return this.stackCtrl.canGoBack(deep, stackId);
  }
  /**
   * Resolves to `true` if it the outlet was able to sucessfully pop the last N pages.
   */
  pop(deep = 1, stackId) {
    return this.stackCtrl.pop(deep, stackId);
  }
  /**
   * Returns the URL of the active page of each stack.
   */
  getLastUrl(stackId) {
    const active = this.stackCtrl.getLastUrl(stackId);
    return active ? active.url : void 0;
  }
  /**
   * Returns the RouteView of the active page of each stack.
   * @internal
   */
  getLastRouteView(stackId) {
    return this.stackCtrl.getLastUrl(stackId);
  }
  /**
   * Returns the root view in the tab stack.
   * @internal
   */
  getRootView(stackId) {
    return this.stackCtrl.getRootUrl(stackId);
  }
  /**
   * Returns the active stack ID. In the context of ion-tabs, it means the active tab.
   */
  getActiveStackId() {
    return this.stackCtrl.getActiveStackId();
  }
  /**
   * Since the activated route can change over the life time of a component in an ion router outlet, we create
   * a proxy so that we can update the values over time as a user navigates back to components already in the stack.
   */
  createActivatedRouteProxy(component$, activatedRoute) {
    const proxy = new ActivatedRoute();
    proxy._futureSnapshot = activatedRoute._futureSnapshot;
    proxy._routerState = activatedRoute._routerState;
    proxy.snapshot = activatedRoute.snapshot;
    proxy.outlet = activatedRoute.outlet;
    proxy.component = activatedRoute.component;
    proxy._paramMap = this.proxyObservable(component$, "paramMap");
    proxy._queryParamMap = this.proxyObservable(component$, "queryParamMap");
    proxy.url = this.proxyObservable(component$, "url");
    proxy.params = this.proxyObservable(component$, "params");
    proxy.queryParams = this.proxyObservable(component$, "queryParams");
    proxy.fragment = this.proxyObservable(component$, "fragment");
    proxy.data = this.proxyObservable(component$, "data");
    return proxy;
  }
  /**
   * Create a wrapped observable that will switch to the latest activated route matched by the given component
   */
  proxyObservable(component$, path) {
    return component$.pipe(
      // First wait until the component instance is pushed
      filter((component) => !!component),
      switchMap((component) => this.currentActivatedRoute$.pipe(filter((current) => current !== null && current.component === component), switchMap((current) => current && current.activatedRoute[path]), distinctUntilChanged()))
    );
  }
  /**
   * Updates the activated route proxy for the given component to the new incoming router state
   */
  updateActivatedRouteProxy(component, activatedRoute) {
    const proxy = this.proxyMap.get(component);
    if (!proxy) {
      throw new Error(`Could not find activated route proxy for view`);
    }
    proxy._futureSnapshot = activatedRoute._futureSnapshot;
    proxy._routerState = activatedRoute._routerState;
    proxy.snapshot = activatedRoute.snapshot;
    proxy.outlet = activatedRoute.outlet;
    proxy.component = activatedRoute.component;
    this.currentActivatedRoute$.next({
      component,
      activatedRoute
    });
  }
};
/** @nocollapse */
__publicField(_IonRouterOutlet, "ɵfac", function IonRouterOutlet_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _IonRouterOutlet)(ɵɵinjectAttribute("name"), ɵɵinjectAttribute("tabs"), ɵɵdirectiveInject(Location), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(Router), ɵɵdirectiveInject(NgZone), ɵɵdirectiveInject(ActivatedRoute), ɵɵdirectiveInject(_IonRouterOutlet, 12));
});
/** @nocollapse */
__publicField(_IonRouterOutlet, "ɵdir", ɵɵdefineDirective({
  type: _IonRouterOutlet,
  selectors: [["ion-router-outlet"]],
  inputs: {
    animated: "animated",
    animation: "animation",
    mode: "mode",
    swipeGesture: "swipeGesture",
    name: "name"
  },
  outputs: {
    stackWillChange: "stackWillChange",
    stackDidChange: "stackDidChange",
    activateEvents: "activate",
    deactivateEvents: "deactivate"
  },
  exportAs: ["outlet"],
  standalone: false
}));
var IonRouterOutlet = _IonRouterOutlet;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonRouterOutlet, [{
    type: Directive,
    args: [{
      selector: "ion-router-outlet",
      exportAs: "outlet",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["animated", "animation", "mode", "swipeGesture"]
    }]
  }], function() {
    return [{
      type: void 0,
      decorators: [{
        type: Attribute,
        args: ["name"]
      }]
    }, {
      type: void 0,
      decorators: [{
        type: Optional
      }, {
        type: Attribute,
        args: ["tabs"]
      }]
    }, {
      type: Location
    }, {
      type: ElementRef
    }, {
      type: Router
    }, {
      type: NgZone
    }, {
      type: ActivatedRoute
    }, {
      type: IonRouterOutlet,
      decorators: [{
        type: SkipSelf
      }, {
        type: Optional
      }]
    }];
  }, {
    name: [{
      type: Input
    }],
    stackWillChange: [{
      type: Output
    }],
    stackDidChange: [{
      type: Output
    }],
    activateEvents: [{
      type: Output,
      args: ["activate"]
    }],
    deactivateEvents: [{
      type: Output,
      args: ["deactivate"]
    }]
  });
})();
var OutletInjector = class {
  route;
  childContexts;
  parent;
  constructor(route, childContexts, parent) {
    this.route = route;
    this.childContexts = childContexts;
    this.parent = parent;
  }
  get(token, notFoundValue) {
    if (token === ActivatedRoute) {
      return this.route;
    }
    if (token === ChildrenOutletContexts) {
      return this.childContexts;
    }
    return this.parent.get(token, notFoundValue);
  }
};
var INPUT_BINDER = new InjectionToken("");
var _RoutedComponentInputBinder = class _RoutedComponentInputBinder {
  outletDataSubscriptions = /* @__PURE__ */ new Map();
  bindActivatedRouteToOutletComponent(outlet) {
    this.unsubscribeFromRouteData(outlet);
    this.subscribeToRouteData(outlet);
  }
  unsubscribeFromRouteData(outlet) {
    var _a5;
    (_a5 = this.outletDataSubscriptions.get(outlet)) == null ? void 0 : _a5.unsubscribe();
    this.outletDataSubscriptions.delete(outlet);
  }
  subscribeToRouteData(outlet) {
    const {
      activatedRoute
    } = outlet;
    const dataSubscription = combineLatest([activatedRoute.queryParams, activatedRoute.params, activatedRoute.data]).pipe(switchMap(([queryParams, params, data], index) => {
      data = __spreadValues(__spreadValues(__spreadValues({}, queryParams), params), data);
      if (index === 0) {
        return of(data);
      }
      return Promise.resolve(data);
    })).subscribe((data) => {
      if (!outlet.isActivated || !outlet.activatedComponentRef || outlet.activatedRoute !== activatedRoute || activatedRoute.component === null) {
        this.unsubscribeFromRouteData(outlet);
        return;
      }
      const mirror = reflectComponentType(activatedRoute.component);
      if (!mirror) {
        this.unsubscribeFromRouteData(outlet);
        return;
      }
      for (const {
        templateName
      } of mirror.inputs) {
        outlet.activatedComponentRef.setInput(templateName, data[templateName]);
      }
    });
    this.outletDataSubscriptions.set(outlet, dataSubscription);
  }
};
/** @nocollapse */
__publicField(_RoutedComponentInputBinder, "ɵfac", function RoutedComponentInputBinder_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _RoutedComponentInputBinder)();
});
/** @nocollapse */
__publicField(_RoutedComponentInputBinder, "ɵprov", ɵɵdefineInjectable({
  token: _RoutedComponentInputBinder,
  factory: _RoutedComponentInputBinder.ɵfac
}));
var RoutedComponentInputBinder = _RoutedComponentInputBinder;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(RoutedComponentInputBinder, [{
    type: Injectable
  }], null, null);
})();
var provideComponentInputBinding = () => {
  return {
    provide: INPUT_BINDER,
    useFactory: componentInputBindingFactory,
    deps: [Router]
  };
};
function componentInputBindingFactory(router) {
  if (router == null ? void 0 : router.componentInputBindingEnabled) {
    return new RoutedComponentInputBinder();
  }
  return null;
}
var BACK_BUTTON_INPUTS = ["color", "defaultHref", "disabled", "icon", "mode", "routerAnimation", "text", "type"];
var _a3;
var IonBackButton = (_a3 = class {
  routerOutlet;
  navCtrl;
  config;
  r;
  z;
  el;
  constructor(routerOutlet, navCtrl, config2, r, z, c) {
    this.routerOutlet = routerOutlet;
    this.navCtrl = navCtrl;
    this.config = config2;
    this.r = r;
    this.z = z;
    c.detach();
    this.el = this.r.nativeElement;
  }
  /**
   * @internal
   */
  onClick(ev) {
    var _a5;
    const defaultHref = this.defaultHref || this.config.get("backButtonDefaultHref");
    if ((_a5 = this.routerOutlet) == null ? void 0 : _a5.canGoBack()) {
      this.navCtrl.setDirection("back", void 0, void 0, this.routerAnimation);
      this.routerOutlet.pop();
      ev.preventDefault();
    } else if (defaultHref != null) {
      this.navCtrl.navigateBack(defaultHref, {
        animation: this.routerAnimation
      });
      ev.preventDefault();
    }
  }
}, /** @nocollapse */
__publicField(_a3, "ɵfac", function IonBackButton_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a3)(ɵɵdirectiveInject(IonRouterOutlet, 8), ɵɵdirectiveInject(NavController), ɵɵdirectiveInject(Config), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone), ɵɵdirectiveInject(ChangeDetectorRef));
}), /** @nocollapse */
__publicField(_a3, "ɵdir", ɵɵdefineDirective({
  type: _a3,
  hostBindings: function IonBackButton_HostBindings(rf, ctx) {
    if (rf & 1) {
      ɵɵlistener("click", function IonBackButton_click_HostBindingHandler($event) {
        return ctx.onClick($event);
      });
    }
  },
  inputs: {
    color: "color",
    defaultHref: "defaultHref",
    disabled: "disabled",
    icon: "icon",
    mode: "mode",
    routerAnimation: "routerAnimation",
    text: "text",
    type: "type"
  },
  standalone: false
})), _a3);
IonBackButton = __decorate([ProxyCmp({
  inputs: BACK_BUTTON_INPUTS
})], IonBackButton);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonBackButton, [{
    type: Directive,
    args: [{
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: BACK_BUTTON_INPUTS
    }]
  }], function() {
    return [{
      type: IonRouterOutlet,
      decorators: [{
        type: Optional
      }]
    }, {
      type: NavController
    }, {
      type: Config
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }, {
      type: ChangeDetectorRef
    }];
  }, {
    onClick: [{
      type: HostListener,
      args: ["click", ["$event"]]
    }]
  });
})();
var _RouterLinkDelegateDirective = class _RouterLinkDelegateDirective {
  locationStrategy;
  navCtrl;
  elementRef;
  router;
  routerLink;
  routerDirection = "forward";
  routerAnimation;
  constructor(locationStrategy, navCtrl, elementRef, router, routerLink) {
    this.locationStrategy = locationStrategy;
    this.navCtrl = navCtrl;
    this.elementRef = elementRef;
    this.router = router;
    this.routerLink = routerLink;
  }
  ngOnInit() {
    this.updateTargetUrlAndHref();
    this.updateTabindex();
  }
  ngOnChanges() {
    this.updateTargetUrlAndHref();
  }
  /**
   * The `tabindex` is set to `0` by default on the host element when
   * the `routerLink` directive is used. This causes issues with Ionic
   * components that wrap an `a` or `button` element, such as `ion-item`.
   * See issue https://github.com/angular/angular/issues/28345
   *
   * This method removes the `tabindex` attribute from the host element
   * to allow the Ionic component to manage the focus state correctly.
   */
  updateTabindex() {
    const ionicComponents = ["ION-BACK-BUTTON", "ION-BREADCRUMB", "ION-BUTTON", "ION-CARD", "ION-FAB-BUTTON", "ION-ITEM", "ION-ITEM-OPTION", "ION-MENU-BUTTON", "ION-SEGMENT-BUTTON", "ION-TAB-BUTTON"];
    const hostElement = this.elementRef.nativeElement;
    if (ionicComponents.includes(hostElement.tagName)) {
      if (hostElement.getAttribute("tabindex") === "0") {
        hostElement.removeAttribute("tabindex");
      }
    }
  }
  updateTargetUrlAndHref() {
    var _a5;
    if ((_a5 = this.routerLink) == null ? void 0 : _a5.urlTree) {
      const href = this.locationStrategy.prepareExternalUrl(this.router.serializeUrl(this.routerLink.urlTree));
      this.elementRef.nativeElement.href = href;
    }
  }
  /**
   * @internal
   */
  onClick(ev) {
    this.navCtrl.setDirection(this.routerDirection, void 0, void 0, this.routerAnimation);
    ev.preventDefault();
  }
};
/** @nocollapse */
__publicField(_RouterLinkDelegateDirective, "ɵfac", function RouterLinkDelegateDirective_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _RouterLinkDelegateDirective)(ɵɵdirectiveInject(LocationStrategy), ɵɵdirectiveInject(NavController), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(Router), ɵɵdirectiveInject(RouterLink, 8));
});
/** @nocollapse */
__publicField(_RouterLinkDelegateDirective, "ɵdir", ɵɵdefineDirective({
  type: _RouterLinkDelegateDirective,
  selectors: [["", "routerLink", "", 5, "a", 5, "area"]],
  hostBindings: function RouterLinkDelegateDirective_HostBindings(rf, ctx) {
    if (rf & 1) {
      ɵɵlistener("click", function RouterLinkDelegateDirective_click_HostBindingHandler($event) {
        return ctx.onClick($event);
      });
    }
  },
  inputs: {
    routerDirection: "routerDirection",
    routerAnimation: "routerAnimation"
  },
  standalone: false,
  features: [ɵɵNgOnChangesFeature]
}));
var RouterLinkDelegateDirective = _RouterLinkDelegateDirective;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(RouterLinkDelegateDirective, [{
    type: Directive,
    args: [{
      selector: ":not(a):not(area)[routerLink]"
    }]
  }], function() {
    return [{
      type: LocationStrategy
    }, {
      type: NavController
    }, {
      type: ElementRef
    }, {
      type: Router
    }, {
      type: RouterLink,
      decorators: [{
        type: Optional
      }]
    }];
  }, {
    routerDirection: [{
      type: Input
    }],
    routerAnimation: [{
      type: Input
    }],
    onClick: [{
      type: HostListener,
      args: ["click", ["$event"]]
    }]
  });
})();
var _RouterLinkWithHrefDelegateDirective = class _RouterLinkWithHrefDelegateDirective {
  locationStrategy;
  navCtrl;
  elementRef;
  router;
  routerLink;
  routerDirection = "forward";
  routerAnimation;
  constructor(locationStrategy, navCtrl, elementRef, router, routerLink) {
    this.locationStrategy = locationStrategy;
    this.navCtrl = navCtrl;
    this.elementRef = elementRef;
    this.router = router;
    this.routerLink = routerLink;
  }
  ngOnInit() {
    this.updateTargetUrlAndHref();
  }
  ngOnChanges() {
    this.updateTargetUrlAndHref();
  }
  updateTargetUrlAndHref() {
    var _a5;
    if ((_a5 = this.routerLink) == null ? void 0 : _a5.urlTree) {
      const href = this.locationStrategy.prepareExternalUrl(this.router.serializeUrl(this.routerLink.urlTree));
      this.elementRef.nativeElement.href = href;
    }
  }
  /**
   * @internal
   */
  onClick() {
    this.navCtrl.setDirection(this.routerDirection, void 0, void 0, this.routerAnimation);
  }
};
/** @nocollapse */
__publicField(_RouterLinkWithHrefDelegateDirective, "ɵfac", function RouterLinkWithHrefDelegateDirective_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _RouterLinkWithHrefDelegateDirective)(ɵɵdirectiveInject(LocationStrategy), ɵɵdirectiveInject(NavController), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(Router), ɵɵdirectiveInject(RouterLink, 8));
});
/** @nocollapse */
__publicField(_RouterLinkWithHrefDelegateDirective, "ɵdir", ɵɵdefineDirective({
  type: _RouterLinkWithHrefDelegateDirective,
  selectors: [["a", "routerLink", ""], ["area", "routerLink", ""]],
  hostBindings: function RouterLinkWithHrefDelegateDirective_HostBindings(rf, ctx) {
    if (rf & 1) {
      ɵɵlistener("click", function RouterLinkWithHrefDelegateDirective_click_HostBindingHandler() {
        return ctx.onClick();
      });
    }
  },
  inputs: {
    routerDirection: "routerDirection",
    routerAnimation: "routerAnimation"
  },
  standalone: false,
  features: [ɵɵNgOnChangesFeature]
}));
var RouterLinkWithHrefDelegateDirective = _RouterLinkWithHrefDelegateDirective;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(RouterLinkWithHrefDelegateDirective, [{
    type: Directive,
    args: [{
      selector: "a[routerLink],area[routerLink]"
    }]
  }], function() {
    return [{
      type: LocationStrategy
    }, {
      type: NavController
    }, {
      type: ElementRef
    }, {
      type: Router
    }, {
      type: RouterLink,
      decorators: [{
        type: Optional
      }]
    }];
  }, {
    routerDirection: [{
      type: Input
    }],
    routerAnimation: [{
      type: Input
    }],
    onClick: [{
      type: HostListener,
      args: ["click"]
    }]
  });
})();
var NAV_INPUTS = ["animated", "animation", "root", "rootParams", "swipeGesture"];
var NAV_METHODS = ["push", "insert", "insertPages", "pop", "popTo", "popToRoot", "removeIndex", "setRoot", "setPages", "getActive", "getByIndex", "canGoBack", "getPrevious"];
var _a4;
var IonNav = (_a4 = class {
  z;
  el;
  constructor(ref, environmentInjector, injector, angularDelegate, z, c) {
    this.z = z;
    c.detach();
    this.el = ref.nativeElement;
    ref.nativeElement.delegate = angularDelegate.create(environmentInjector, injector);
    proxyOutputs(this, this.el, ["ionNavDidChange", "ionNavWillChange"]);
  }
}, /** @nocollapse */
__publicField(_a4, "ɵfac", function IonNav_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a4)(ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(EnvironmentInjector), ɵɵdirectiveInject(Injector), ɵɵdirectiveInject(AngularDelegate), ɵɵdirectiveInject(NgZone), ɵɵdirectiveInject(ChangeDetectorRef));
}), /** @nocollapse */
__publicField(_a4, "ɵdir", ɵɵdefineDirective({
  type: _a4,
  inputs: {
    animated: "animated",
    animation: "animation",
    root: "root",
    rootParams: "rootParams",
    swipeGesture: "swipeGesture"
  },
  standalone: false
})), _a4);
IonNav = __decorate([ProxyCmp({
  inputs: NAV_INPUTS,
  methods: NAV_METHODS
})], IonNav);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonNav, [{
    type: Directive,
    args: [{
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: NAV_INPUTS
    }]
  }], function() {
    return [{
      type: ElementRef
    }, {
      type: EnvironmentInjector
    }, {
      type: Injector
    }, {
      type: AngularDelegate
    }, {
      type: NgZone
    }, {
      type: ChangeDetectorRef
    }];
  }, null);
})();
var _IonTabs = class _IonTabs {
  navCtrl;
  tabsInner;
  /**
   * Emitted before the tab view is changed.
   */
  ionTabsWillChange = new EventEmitter();
  /**
   * Emitted after the tab view is changed.
   */
  ionTabsDidChange = new EventEmitter();
  tabBarSlot = "bottom";
  hasTab = false;
  selectedTab;
  leavingTab;
  constructor(navCtrl) {
    this.navCtrl = navCtrl;
  }
  ngAfterViewInit() {
    const firstTab = this.tabs.length > 0 ? this.tabs.first : void 0;
    if (firstTab) {
      this.hasTab = true;
      this.setActiveTab(firstTab.tab);
      this.tabSwitch();
    }
  }
  ngAfterContentInit() {
    this.detectSlotChanges();
  }
  ngAfterContentChecked() {
    this.detectSlotChanges();
  }
  /**
   * @internal
   */
  onStackWillChange({
    enteringView,
    tabSwitch
  }) {
    const stackId = enteringView.stackId;
    if (tabSwitch && stackId !== void 0) {
      this.ionTabsWillChange.emit({
        tab: stackId
      });
    }
  }
  /**
   * @internal
   */
  onStackDidChange({
    enteringView,
    tabSwitch
  }) {
    const stackId = enteringView.stackId;
    if (tabSwitch && stackId !== void 0) {
      if (this.tabBar) {
        this.tabBar.selectedTab = stackId;
      }
      this.ionTabsDidChange.emit({
        tab: stackId
      });
    }
  }
  /**
   * When a tab button is clicked, there are several scenarios:
   * 1. If the selected tab is currently active (the tab button has been clicked
   *    again), then it should go to the root view for that tab.
   *
   *   a. Get the saved root view from the router outlet. If the saved root view
   *      matches the tabRootUrl, set the route view to this view including the
   *      navigation extras.
   *   b. If the saved root view from the router outlet does
   *      not match, navigate to the tabRootUrl. No navigation extras are
   *      included.
   *
   * 2. If the current tab tab is not currently selected, get the last route
   *    view from the router outlet.
   *
   *   a. If the last route view exists, navigate to that view including any
   *      navigation extras
   *   b. If the last route view doesn't exist, then navigate
   *      to the default tabRootUrl
   */
  select(tabOrEvent) {
    const isTabString = typeof tabOrEvent === "string";
    const tab = isTabString ? tabOrEvent : tabOrEvent.detail.tab;
    if (this.hasTab) {
      this.setActiveTab(tab);
      this.tabSwitch();
      return;
    }
    const alreadySelected = this.outlet.getActiveStackId() === tab;
    const tabRootUrl = `${this.outlet.tabsPrefix}/${tab}`;
    if (!isTabString) {
      tabOrEvent.stopPropagation();
    }
    if (alreadySelected) {
      const activeStackId = this.outlet.getActiveStackId();
      const activeView = this.outlet.getLastRouteView(activeStackId);
      if ((activeView == null ? void 0 : activeView.url) === tabRootUrl) {
        return;
      }
      const rootView = this.outlet.getRootView(tab);
      const navigationExtras = rootView && tabRootUrl === rootView.url && rootView.savedExtras;
      return this.navCtrl.navigateRoot(tabRootUrl, __spreadProps(__spreadValues({}, navigationExtras), {
        animated: true,
        animationDirection: "back"
      }));
    } else {
      const lastRoute = this.outlet.getLastRouteView(tab);
      const url = (lastRoute == null ? void 0 : lastRoute.url) || tabRootUrl;
      const navigationExtras = lastRoute == null ? void 0 : lastRoute.savedExtras;
      return this.navCtrl.navigateRoot(url, __spreadProps(__spreadValues({}, navigationExtras), {
        animated: true,
        animationDirection: "back"
      }));
    }
  }
  setActiveTab(tab) {
    const tabs = this.tabs;
    const selectedTab = tabs.find((t) => t.tab === tab);
    if (!selectedTab) {
      console.error(`[Ionic Error]: Tab with id: "${tab}" does not exist`);
      return;
    }
    this.leavingTab = this.selectedTab;
    this.selectedTab = selectedTab;
    this.ionTabsWillChange.emit({
      tab
    });
    selectedTab.el.active = true;
  }
  tabSwitch() {
    const {
      selectedTab,
      leavingTab
    } = this;
    if (this.tabBar && selectedTab) {
      this.tabBar.selectedTab = selectedTab.tab;
    }
    if ((leavingTab == null ? void 0 : leavingTab.tab) !== (selectedTab == null ? void 0 : selectedTab.tab)) {
      if (leavingTab == null ? void 0 : leavingTab.el) {
        leavingTab.el.active = false;
      }
    }
    if (selectedTab) {
      this.ionTabsDidChange.emit({
        tab: selectedTab.tab
      });
    }
  }
  getSelected() {
    var _a5;
    if (this.hasTab) {
      return (_a5 = this.selectedTab) == null ? void 0 : _a5.tab;
    }
    return this.outlet.getActiveStackId();
  }
  /**
   * Detects changes to the slot attribute of the tab bar.
   *
   * If the slot attribute has changed, then the tab bar
   * should be relocated to the new slot position.
   */
  detectSlotChanges() {
    this.tabBars.forEach((tabBar) => {
      const currentSlot = tabBar.el.getAttribute("slot");
      if (currentSlot !== this.tabBarSlot) {
        this.tabBarSlot = currentSlot;
        this.relocateTabBar();
      }
    });
  }
  /**
   * Relocates the tab bar to the new slot position.
   */
  relocateTabBar() {
    const tabBar = this.tabBar.el;
    if (this.tabBarSlot === "top") {
      this.tabsInner.nativeElement.before(tabBar);
    } else {
      this.tabsInner.nativeElement.after(tabBar);
    }
  }
};
/** @nocollapse */
__publicField(_IonTabs, "ɵfac", function IonTabs_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _IonTabs)(ɵɵdirectiveInject(NavController));
});
/** @nocollapse */
__publicField(_IonTabs, "ɵdir", ɵɵdefineDirective({
  type: _IonTabs,
  selectors: [["ion-tabs"]],
  viewQuery: function IonTabs_Query(rf, ctx) {
    if (rf & 1) {
      ɵɵviewQuery(_c0, 7, ElementRef);
    }
    if (rf & 2) {
      let _t;
      ɵɵqueryRefresh(_t = ɵɵloadQuery()) && (ctx.tabsInner = _t.first);
    }
  },
  hostBindings: function IonTabs_HostBindings(rf, ctx) {
    if (rf & 1) {
      ɵɵlistener("ionTabButtonClick", function IonTabs_ionTabButtonClick_HostBindingHandler($event) {
        return ctx.select($event);
      });
    }
  },
  outputs: {
    ionTabsWillChange: "ionTabsWillChange",
    ionTabsDidChange: "ionTabsDidChange"
  },
  standalone: false
}));
var IonTabs = _IonTabs;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonTabs, [{
    type: Directive,
    args: [{
      selector: "ion-tabs"
    }]
  }], function() {
    return [{
      type: NavController
    }];
  }, {
    tabsInner: [{
      type: ViewChild,
      args: ["tabsInner", {
        read: ElementRef,
        static: true
      }]
    }],
    ionTabsWillChange: [{
      type: Output
    }],
    ionTabsDidChange: [{
      type: Output
    }],
    select: [{
      type: HostListener,
      args: ["ionTabButtonClick", ["$event"]]
    }]
  });
})();
var raf = (h) => {
  if (typeof __zone_symbol__requestAnimationFrame === "function") {
    return __zone_symbol__requestAnimationFrame(h);
  }
  if (typeof requestAnimationFrame === "function") {
    return requestAnimationFrame(h);
  }
  return setTimeout(h);
};
var _ValueAccessor = class _ValueAccessor {
  injector;
  elementRef;
  onChange = () => {
  };
  onTouched = () => {
  };
  lastValue;
  statusChanges;
  constructor(injector, elementRef) {
    this.injector = injector;
    this.elementRef = elementRef;
  }
  writeValue(value) {
    this.elementRef.nativeElement.value = this.lastValue = value;
    setIonicClasses(this.elementRef);
  }
  /**
   * Notifies the ControlValueAccessor of a change in the value of the control.
   *
   * This is called by each of the ValueAccessor directives when we want to update
   * the status and validity of the form control. For example with text components this
   * is called when the ionInput event is fired. For select components this is called
   * when the ionChange event is fired.
   *
   * This also updates the Ionic form status classes on the element.
   *
   * @param el The component element.
   * @param value The new value of the control.
   */
  handleValueChange(el, value) {
    if (el === this.elementRef.nativeElement) {
      if (value !== this.lastValue) {
        this.lastValue = value;
        this.onChange(value);
      }
      setIonicClasses(this.elementRef);
    }
  }
  _handleBlurEvent(el) {
    if (el === this.elementRef.nativeElement) {
      this.onTouched();
      setIonicClasses(this.elementRef);
    } else if (el.closest("ion-radio-group") === this.elementRef.nativeElement) {
      this.onTouched();
    }
  }
  registerOnChange(fn) {
    this.onChange = fn;
  }
  registerOnTouched(fn) {
    this.onTouched = fn;
  }
  setDisabledState(isDisabled) {
    this.elementRef.nativeElement.disabled = isDisabled;
  }
  ngOnDestroy() {
    if (this.statusChanges) {
      this.statusChanges.unsubscribe();
    }
  }
  ngAfterViewInit() {
    let ngControl;
    try {
      ngControl = this.injector.get(NgControl);
    } catch {
    }
    if (!ngControl) {
      return;
    }
    if (ngControl.statusChanges) {
      this.statusChanges = ngControl.statusChanges.subscribe(() => setIonicClasses(this.elementRef));
    }
    const formControl = ngControl.control;
    if (formControl) {
      const methodsToPatch = ["markAsTouched", "markAllAsTouched", "markAsUntouched", "markAsDirty", "markAsPristine"];
      methodsToPatch.forEach((method) => {
        if (typeof formControl[method] !== "undefined") {
          const oldFn = formControl[method].bind(formControl);
          formControl[method] = (...params) => {
            oldFn(...params);
            setIonicClasses(this.elementRef);
          };
        }
      });
    }
  }
};
/** @nocollapse */
__publicField(_ValueAccessor, "ɵfac", function ValueAccessor_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _ValueAccessor)(ɵɵdirectiveInject(Injector), ɵɵdirectiveInject(ElementRef));
});
/** @nocollapse */
__publicField(_ValueAccessor, "ɵdir", ɵɵdefineDirective({
  type: _ValueAccessor,
  hostBindings: function ValueAccessor_HostBindings(rf, ctx) {
    if (rf & 1) {
      ɵɵlistener("ionBlur", function ValueAccessor_ionBlur_HostBindingHandler($event) {
        return ctx._handleBlurEvent($event.target);
      });
    }
  },
  standalone: false
}));
var ValueAccessor = _ValueAccessor;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(ValueAccessor, [{
    type: Directive
  }], function() {
    return [{
      type: Injector
    }, {
      type: ElementRef
    }];
  }, {
    _handleBlurEvent: [{
      type: HostListener,
      args: ["ionBlur", ["$event.target"]]
    }]
  });
})();
var setIonicClasses = (element) => {
  raf(() => {
    const input = element.nativeElement;
    const hasValue = input.value != null && input.value.toString().length > 0;
    const classes = getClasses(input);
    setClasses(input, classes);
    const item = input.closest("ion-item");
    if (item) {
      if (hasValue) {
        setClasses(item, [...classes, "item-has-value"]);
      } else {
        setClasses(item, classes);
      }
    }
  });
};
var getClasses = (element) => {
  const classList = element.classList;
  const classes = [];
  for (let i = 0; i < classList.length; i++) {
    const item = classList.item(i);
    if (item !== null && startsWith(item, "ng-")) {
      classes.push(`ion-${item.substring(3)}`);
    }
  }
  return classes;
};
var setClasses = (element, classes) => {
  const classList = element.classList;
  classList.remove("ion-valid", "ion-invalid", "ion-touched", "ion-untouched", "ion-dirty", "ion-pristine");
  classList.add(...classes);
};
var startsWith = (input, search) => {
  return input.substring(0, search.length) === search;
};
var IonicRouteStrategy = class {
  /**
   * Whether the given route should detach for later reuse.
   */
  shouldDetach(_route) {
    return false;
  }
  /**
   * Returns `false`, meaning the route (and its subtree) is never reattached
   */
  shouldAttach(_route) {
    return false;
  }
  /**
   * A no-op; the route is never stored since this strategy never detaches routes for later re-use.
   */
  store(_route, _detachedTree) {
    return;
  }
  /**
   * Returns `null` because this strategy does not store routes for later re-use.
   */
  retrieve(_route) {
    return null;
  }
  /**
   * Determines if a route should be reused.
   * This strategy returns `true` when the future route config and
   * current route config are identical and all route parameters are identical.
   */
  shouldReuseRoute(future, curr) {
    if (future.routeConfig !== curr.routeConfig) {
      return false;
    }
    const futureParams = future.params;
    const currentParams = curr.params;
    const keysA = Object.keys(futureParams);
    const keysB = Object.keys(currentParams);
    if (keysA.length !== keysB.length) {
      return false;
    }
    for (const key of keysA) {
      if (currentParams[key] !== futureParams[key]) {
        return false;
      }
    }
    return true;
  }
};
var OverlayBaseController = class {
  ctrl;
  constructor(ctrl) {
    this.ctrl = ctrl;
  }
  /**
   * Creates a new overlay
   */
  create(opts) {
    return this.ctrl.create(opts || {});
  }
  /**
   * When `id` is not provided, it dismisses the top overlay.
   */
  dismiss(data, role, id) {
    return this.ctrl.dismiss(data, role, id);
  }
  /**
   * Returns the top overlay.
   */
  getTop() {
    return this.ctrl.getTop();
  }
};

export {
  getTimeGivenProgression,
  getPlatforms,
  isPlatform,
  getIonMode,
  initialize,
  sanitizeDOMString,
  IonicSafeString,
  ENABLE_HTML_CONTENT_DEFAULT,
  hostContext,
  createColorClasses,
  getClassMap,
  openURL,
  menuController,
  attachComponent,
  detachComponent,
  CoreDelegate,
  focusFirstDescendant,
  focusLastDescendant,
  alertController,
  actionSheetController,
  loadingController,
  modalController,
  pickerController,
  popoverController,
  toastController,
  prepareOverlay,
  setOverlayId,
  getPresentedOverlay,
  present,
  dismiss,
  eventMethod,
  isCancel,
  safeCall,
  BACKDROP,
  GESTURE,
  OVERLAY_GESTURE_PRIORITY,
  createDelegateController,
  createTriggerController,
  FOCUS_TRAP_DISABLE_CLASS,
  IonicSlides,
  MenuController,
  DomController,
  Platform,
  NavController,
  Config,
  ConfigToken,
  NavParams,
  AngularDelegate,
  ProxyCmp,
  IonPopover,
  IonModal,
  IonRouterOutlet,
  provideComponentInputBinding,
  IonBackButton,
  RouterLinkDelegateDirective,
  RouterLinkWithHrefDelegateDirective,
  IonNav,
  IonTabs,
  raf,
  ValueAccessor,
  setIonicClasses,
  IonicRouteStrategy,
  OverlayBaseController
};
/*! Bundled license information:

@ionic/core/components/cubic-bezier.js:
@ionic/core/components/ionic-global.js:
@ionic/core/components/config.js:
@ionic/core/components/index5.js:
@ionic/core/components/framework-delegate.js:
@ionic/core/components/overlays.js:
@ionic/core/components/theme.js:
@ionic/core/components/index.js:
  (*!
   * (C) Ionic http://ionicframework.com - MIT License
   *)
*/
//# sourceMappingURL=chunk-6LAN327Q.js.map
