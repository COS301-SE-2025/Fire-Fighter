import {
  deleteApp,
  getApp,
  getApps,
  initializeApp,
  initializeServerApp,
  onLog,
  registerVersion,
  setLogLevel
} from "./chunk-4KMPP3WQ.js";
import {
  EnvironmentInjector,
  Inject,
  Injectable,
  InjectionToken,
  Injector,
  NgModule,
  NgZone,
  Optional,
  PLATFORM_ID,
  PendingTasks,
  VERSION,
  Version,
  assertInInjectionContext,
  inject,
  isDevMode,
  makeEnvironmentProviders,
  runInInjectionContext,
  setClassMetadata,
  ɵɵdefineInjectable,
  ɵɵdefineInjector,
  ɵɵdefineNgModule,
  ɵɵinject
} from "./chunk-GPKJZTAI.js";
import {
  queueScheduler
} from "./chunk-D2ID5PW6.js";
import {
  Observable,
  asyncScheduler,
  concatMap,
  distinct,
  from,
  observeOn,
  subscribeOn,
  timer
} from "./chunk-ZCKGGNWJ.js";
import {
  __publicField
} from "./chunk-UQIXM5CJ.js";

// node_modules/firebase/app/dist/esm/index.esm.js
var name = "firebase";
var version = "11.9.1";
registerVersion(name, version, "app");

// node_modules/@angular/core/fesm2022/rxjs-interop.mjs
function pendingUntilEvent(injector) {
  if (injector === void 0) {
    assertInInjectionContext(pendingUntilEvent);
    injector = inject(Injector);
  }
  const taskService = injector.get(PendingTasks);
  return (sourceObservable) => {
    return new Observable((originalSubscriber) => {
      const removeTask = taskService.add();
      let cleanedUp = false;
      function cleanupTask() {
        if (cleanedUp) {
          return;
        }
        removeTask();
        cleanedUp = true;
      }
      const innerSubscription = sourceObservable.subscribe({
        next: (v) => {
          originalSubscriber.next(v);
          cleanupTask();
        },
        complete: () => {
          originalSubscriber.complete();
          cleanupTask();
        },
        error: (e) => {
          originalSubscriber.error(e);
          cleanupTask();
        }
      });
      innerSubscription.add(() => {
        originalSubscriber.unsubscribe();
        cleanupTask();
      });
      return innerSubscription;
    });
  };
}

// node_modules/@angular/fire/fesm2022/angular-fire.mjs
var VERSION2 = new Version("ANGULARFIRE2_VERSION");
function ɵgetDefaultInstanceOf(identifier, provided, defaultApp) {
  if (provided) {
    if (provided.length === 1) {
      return provided[0];
    }
    const providedUsingDefaultApp = provided.filter((it) => it.app === defaultApp);
    if (providedUsingDefaultApp.length === 1) {
      return providedUsingDefaultApp[0];
    }
  }
  const defaultAppWithContainer = defaultApp;
  const provider = defaultAppWithContainer.container.getProvider(identifier);
  return provider.getImmediate({
    optional: true
  });
}
var ɵgetAllInstancesOf = (identifier, app) => {
  const apps = app ? [app] : getApps();
  const instances = [];
  apps.forEach((app2) => {
    const provider = app2.container.getProvider(identifier);
    provider.instances.forEach((instance) => {
      if (!instances.includes(instance)) {
        instances.push(instance);
      }
    });
  });
  return instances;
};
var LogLevel;
(function(LogLevel2) {
  LogLevel2[LogLevel2["SILENT"] = 0] = "SILENT";
  LogLevel2[LogLevel2["WARN"] = 1] = "WARN";
  LogLevel2[LogLevel2["VERBOSE"] = 2] = "VERBOSE";
})(LogLevel || (LogLevel = {}));
var currentLogLevel = isDevMode() && typeof Zone !== "undefined" ? LogLevel.WARN : LogLevel.SILENT;
var ɵZoneScheduler = class {
  zone;
  delegate;
  constructor(zone, delegate = queueScheduler) {
    this.zone = zone;
    this.delegate = delegate;
  }
  now() {
    return this.delegate.now();
  }
  schedule(work, delay, state) {
    const targetZone = this.zone;
    const workInZone = function(state2) {
      if (targetZone) {
        targetZone.runGuarded(() => {
          work.apply(this, [state2]);
        });
      } else {
        work.apply(this, [state2]);
      }
    };
    return this.delegate.schedule(workInZone, delay, state);
  }
};
var _ɵAngularFireSchedulers = class _ɵAngularFireSchedulers {
  outsideAngular;
  insideAngular;
  constructor() {
    const ngZone = inject(NgZone);
    this.outsideAngular = ngZone.runOutsideAngular(() => new ɵZoneScheduler(typeof Zone === "undefined" ? void 0 : Zone.current));
    this.insideAngular = ngZone.run(() => new ɵZoneScheduler(typeof Zone === "undefined" ? void 0 : Zone.current, asyncScheduler));
  }
};
__publicField(_ɵAngularFireSchedulers, "ɵfac", function ɵAngularFireSchedulers_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _ɵAngularFireSchedulers)();
});
__publicField(_ɵAngularFireSchedulers, "ɵprov", ɵɵdefineInjectable({
  token: _ɵAngularFireSchedulers,
  factory: _ɵAngularFireSchedulers.ɵfac,
  providedIn: "root"
}));
var ɵAngularFireSchedulers = _ɵAngularFireSchedulers;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(ɵAngularFireSchedulers, [{
    type: Injectable,
    args: [{
      providedIn: "root"
    }]
  }], () => [], null);
})();
var alreadyWarned = false;
function warnOutsideInjectionContext(original, logLevel) {
  if (!alreadyWarned && (currentLogLevel > LogLevel.SILENT || isDevMode())) {
    alreadyWarned = true;
    console.warn("Calling Firebase APIs outside of an Injection context may destabilize your application leading to subtle change-detection and hydration bugs. Find more at https://github.com/angular/angularfire/blob/main/docs/zones.md");
  }
  if (currentLogLevel >= logLevel) {
    console.warn(`Firebase API called outside injection context: ${original.name}`);
  }
}
function runOutsideAngular(fn) {
  const ngZone = inject(NgZone, {
    optional: true
  });
  if (!ngZone) {
    return fn();
  }
  return ngZone.runOutsideAngular(() => fn());
}
function run(fn) {
  const ngZone = inject(NgZone, {
    optional: true
  });
  if (!ngZone) {
    return fn();
  }
  return ngZone.run(() => fn());
}
var zoneWrapFn = (it, taskDone, injector) => {
  return (...args) => {
    if (taskDone) {
      setTimeout(taskDone, 0);
    }
    return runInInjectionContext(injector, () => run(() => it.apply(void 0, args)));
  };
};
var ɵzoneWrap = (it, blockUntilFirst, logLevel) => {
  logLevel || (logLevel = blockUntilFirst ? LogLevel.WARN : LogLevel.VERBOSE);
  return function() {
    let taskDone;
    const _arguments = arguments;
    let schedulers;
    let pendingTasks;
    let injector;
    try {
      schedulers = inject(ɵAngularFireSchedulers);
      pendingTasks = inject(PendingTasks);
      injector = inject(EnvironmentInjector);
    } catch (e) {
      warnOutsideInjectionContext(it, logLevel);
      return it.apply(this, _arguments);
    }
    for (let i = 0; i < arguments.length; i++) {
      if (typeof _arguments[i] === "function") {
        if (blockUntilFirst) {
          taskDone || (taskDone = run(() => pendingTasks.add()));
        }
        _arguments[i] = zoneWrapFn(_arguments[i], taskDone, injector);
      }
    }
    const ret = runOutsideAngular(() => it.apply(this, _arguments));
    if (!blockUntilFirst) {
      if (ret instanceof Observable) {
        return ret.pipe(subscribeOn(schedulers.outsideAngular), observeOn(schedulers.insideAngular));
      } else {
        return run(() => ret);
      }
    }
    if (ret instanceof Observable) {
      return ret.pipe(subscribeOn(schedulers.outsideAngular), observeOn(schedulers.insideAngular), pendingUntilEvent(injector));
    } else if (ret instanceof Promise) {
      return run(() => new Promise((resolve, reject) => {
        pendingTasks.run(() => ret).then((it2) => runInInjectionContext(injector, () => run(() => resolve(it2))), (reason) => runInInjectionContext(injector, () => run(() => reject(reason))));
      }));
    } else if (typeof ret === "function" && taskDone) {
      return function() {
        setTimeout(taskDone, 0);
        return ret.apply(this, arguments);
      };
    } else {
      return run(() => ret);
    }
  };
};

// node_modules/@angular/fire/fesm2022/angular-fire-app.mjs
var FirebaseApp = class {
  constructor(app) {
    return app;
  }
};
var FirebaseApps = class {
  constructor() {
    return getApps();
  }
};
var firebaseApp$ = timer(0, 300).pipe(concatMap(() => from(getApps())), distinct());
function defaultFirebaseAppFactory(provided) {
  if (provided && provided.length === 1) {
    return provided[0];
  }
  return new FirebaseApp(getApp());
}
var PROVIDED_FIREBASE_APPS = new InjectionToken("angularfire2._apps");
var DEFAULT_FIREBASE_APP_PROVIDER = {
  provide: FirebaseApp,
  useFactory: defaultFirebaseAppFactory,
  deps: [[new Optional(), PROVIDED_FIREBASE_APPS]]
};
var FIREBASE_APPS_PROVIDER = {
  provide: FirebaseApps,
  deps: [[new Optional(), PROVIDED_FIREBASE_APPS]]
};
function firebaseAppFactory(fn) {
  return (zone, injector) => {
    const platformId = injector.get(PLATFORM_ID);
    registerVersion("angularfire", VERSION2.full, "core");
    registerVersion("angularfire", VERSION2.full, "app");
    registerVersion("angular", VERSION.full, platformId.toString());
    const app = zone.runOutsideAngular(() => fn(injector));
    return new FirebaseApp(app);
  };
}
var _FirebaseAppModule = class _FirebaseAppModule {
  // eslint-disable-next-line @typescript-eslint/ban-types
  constructor(platformId) {
    registerVersion("angularfire", VERSION2.full, "core");
    registerVersion("angularfire", VERSION2.full, "app");
    registerVersion("angular", VERSION.full, platformId.toString());
  }
};
__publicField(_FirebaseAppModule, "ɵfac", function FirebaseAppModule_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _FirebaseAppModule)(ɵɵinject(PLATFORM_ID));
});
__publicField(_FirebaseAppModule, "ɵmod", ɵɵdefineNgModule({
  type: _FirebaseAppModule
}));
__publicField(_FirebaseAppModule, "ɵinj", ɵɵdefineInjector({
  providers: [DEFAULT_FIREBASE_APP_PROVIDER, FIREBASE_APPS_PROVIDER]
}));
var FirebaseAppModule = _FirebaseAppModule;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(FirebaseAppModule, [{
    type: NgModule,
    args: [{
      providers: [DEFAULT_FIREBASE_APP_PROVIDER, FIREBASE_APPS_PROVIDER]
    }]
  }], () => [{
    type: Object,
    decorators: [{
      type: Inject,
      args: [PLATFORM_ID]
    }]
  }], null);
})();
function provideFirebaseApp(fn, ...deps) {
  return makeEnvironmentProviders([DEFAULT_FIREBASE_APP_PROVIDER, FIREBASE_APPS_PROVIDER, {
    provide: PROVIDED_FIREBASE_APPS,
    useFactory: firebaseAppFactory(fn),
    multi: true,
    deps: [NgZone, Injector, ɵAngularFireSchedulers, ...deps]
  }]);
}
var deleteApp2 = ɵzoneWrap(deleteApp, true);
var getApp2 = ɵzoneWrap(getApp, true);
var getApps2 = ɵzoneWrap(getApps, true);
var initializeApp2 = ɵzoneWrap(initializeApp, true);
var initializeServerApp2 = ɵzoneWrap(initializeServerApp, true);
var onLog2 = ɵzoneWrap(onLog, true);
var registerVersion2 = ɵzoneWrap(registerVersion, true);
var setLogLevel2 = ɵzoneWrap(setLogLevel, true);

export {
  VERSION2 as VERSION,
  ɵgetDefaultInstanceOf,
  ɵgetAllInstancesOf,
  ɵAngularFireSchedulers,
  ɵzoneWrap,
  FirebaseApp,
  FirebaseApps,
  firebaseApp$,
  FirebaseAppModule,
  provideFirebaseApp,
  deleteApp2 as deleteApp,
  getApp2 as getApp,
  getApps2 as getApps,
  initializeApp2 as initializeApp,
  initializeServerApp2 as initializeServerApp,
  onLog2 as onLog,
  registerVersion2 as registerVersion,
  setLogLevel2 as setLogLevel
};
/*! Bundled license information:

firebase/app/dist/esm/index.esm.js:
  (**
   * @license
   * Copyright 2020 Google LLC
   *
   * Licensed under the Apache License, Version 2.0 (the "License");
   * you may not use this file except in compliance with the License.
   * You may obtain a copy of the License at
   *
   *   http://www.apache.org/licenses/LICENSE-2.0
   *
   * Unless required by applicable law or agreed to in writing, software
   * distributed under the License is distributed on an "AS IS" BASIS,
   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   * See the License for the specific language governing permissions and
   * limitations under the License.
   *)

@angular/core/fesm2022/rxjs-interop.mjs:
  (**
   * @license Angular v19.2.14
   * (c) 2010-2025 Google LLC. https://angular.io/
   * License: MIT
   *)
*/
//# sourceMappingURL=chunk-M6QXVEDM.js.map
