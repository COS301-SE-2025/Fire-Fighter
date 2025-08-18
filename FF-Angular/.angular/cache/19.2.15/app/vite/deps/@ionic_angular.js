import {
  menuController
} from "./chunk-2XQIGATA.js";
import {
  getTimeGivenProgression
} from "./chunk-VBX4H6XT.js";
import {
  IonicSafeString,
  setupConfig
} from "./chunk-YGZFVFEN.js";
import {
  actionSheetController,
  alertController,
  loadingController,
  modalController,
  pickerController,
  popoverController,
  toastController
} from "./chunk-UI7FBOKE.js";
import "./chunk-QNMG4E3J.js";
import {
  openURL
} from "./chunk-RC5HTYUZ.js";
import "./chunk-Q26ZJW7V.js";
import {
  iosTransitionAnimation
} from "./chunk-NLLJKDV4.js";
import {
  mdTransitionAnimation
} from "./chunk-QV7JBB25.js";
import {
  getIonPageElement
} from "./chunk-Y5T75T4T.js";
import {
  createAnimation
} from "./chunk-AFFMJNWB.js";
import "./chunk-AM533ZC5.js";
import "./chunk-MLQTL6XH.js";
import {
  bootstrapLazy,
  getPlatforms,
  globalScripts,
  isPlatform
} from "./chunk-T44VOFML.js";
import {
  createGesture
} from "./chunk-LRRXMVX2.js";
import "./chunk-OWX7YSRF.js";
import {
  AngularDelegate,
  Config,
  ConfigToken,
  DomController,
  IonBackButton,
  IonModal,
  IonNav,
  IonPopover,
  IonRouterOutlet,
  IonTabs,
  IonicRouteStrategy,
  MenuController,
  NavController,
  NavParams,
  OverlayBaseController,
  Platform,
  RouterLinkDelegateDirective,
  RouterLinkWithHrefDelegateDirective,
  ValueAccessor,
  provideComponentInputBinding,
  raf,
  setIonicClasses
} from "./chunk-OHM4I5F7.js";
import "./chunk-C3TCO22T.js";
import "./chunk-5DPEQSKI.js";
import "./chunk-HPCN2MB7.js";
import "./chunk-CKFPX5IG.js";
import "./chunk-R4SJUBFW.js";
import "./chunk-BF4AXLRD.js";
import "./chunk-22S3O5CM.js";
import "./chunk-44IZU6OF.js";
import "./chunk-CBIR4FRL.js";
import {
  MaxValidator,
  MinValidator,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR
} from "./chunk-BPNPSZZ7.js";
import {
  ActivatedRoute,
  Router
} from "./chunk-XSYUW5RI.js";
import "./chunk-47P2WZ2Y.js";
import "./chunk-LBMKVY6G.js";
import "./chunk-QDJMIV5P.js";
import {
  CommonModule,
  Location,
  NgIf,
  NgTemplateOutlet
} from "./chunk-W7H6JUOX.js";
import {
  DOCUMENT
} from "./chunk-ROWOOGPI.js";
import {
  APP_INITIALIZER,
  Attribute,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ContentChild,
  ContentChildren,
  Directive,
  ElementRef,
  EnvironmentInjector,
  HostListener,
  Injectable,
  Injector,
  NgModule,
  NgZone,
  Optional,
  SkipSelf,
  ViewChild,
  ViewContainerRef,
  forwardRef,
  inject,
  setClassMetadata,
  ɵɵInheritDefinitionFeature,
  ɵɵProvidersFeature,
  ɵɵadvance,
  ɵɵattribute,
  ɵɵcontentQuery,
  ɵɵdefineComponent,
  ɵɵdefineDirective,
  ɵɵdefineInjectable,
  ɵɵdefineInjector,
  ɵɵdefineNgModule,
  ɵɵdirectiveInject,
  ɵɵelementContainer,
  ɵɵelementContainerEnd,
  ɵɵelementContainerStart,
  ɵɵelementEnd,
  ɵɵelementStart,
  ɵɵgetCurrentView,
  ɵɵgetInheritedFactory,
  ɵɵinject,
  ɵɵinjectAttribute,
  ɵɵlistener,
  ɵɵloadQuery,
  ɵɵnextContext,
  ɵɵprojection,
  ɵɵprojectionDef,
  ɵɵproperty,
  ɵɵqueryRefresh,
  ɵɵresetView,
  ɵɵrestoreView,
  ɵɵtemplate,
  ɵɵviewQuery
} from "./chunk-GPKJZTAI.js";
import {
  fromEvent
} from "./chunk-D2ID5PW6.js";
import "./chunk-J64O5EGZ.js";
import "./chunk-ZCKGGNWJ.js";
import {
  __decorate
} from "./chunk-S3YC363S.js";
import {
  __async,
  __publicField,
  __spreadProps,
  __spreadValues
} from "./chunk-UQIXM5CJ.js";

// node_modules/@ionic/core/dist/esm/index.js
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

// node_modules/@ionic/core/dist/esm/loader.js
var defineCustomElements = (win, options) => __async(null, null, function* () {
  if (typeof window === "undefined") return void 0;
  yield globalScripts();
  return bootstrapLazy(JSON.parse('[["ion-menu_3",[[33,"ion-menu-button",{"color":[513],"disabled":[4],"menu":[1],"autoHide":[4,"auto-hide"],"type":[1],"visible":[32]},[[16,"ionMenuChange","visibilityChanged"],[16,"ionSplitPaneVisible","visibilityChanged"]]],[33,"ion-menu",{"contentId":[513,"content-id"],"menuId":[513,"menu-id"],"type":[1025],"disabled":[1028],"side":[513],"swipeGesture":[4,"swipe-gesture"],"maxEdgeStart":[2,"max-edge-start"],"isPaneVisible":[32],"isEndSide":[32],"isOpen":[64],"isActive":[64],"open":[64],"close":[64],"toggle":[64],"setOpen":[64]},[[16,"ionSplitPaneVisible","onSplitPaneChanged"],[2,"click","onBackdropClick"]],{"type":["typeChanged"],"disabled":["disabledChanged"],"side":["sideChanged"],"swipeGesture":["swipeGestureChanged"]}],[1,"ion-menu-toggle",{"menu":[1],"autoHide":[4,"auto-hide"],"visible":[32]},[[16,"ionMenuChange","visibilityChanged"],[16,"ionSplitPaneVisible","visibilityChanged"]]]]],["ion-input-password-toggle",[[33,"ion-input-password-toggle",{"color":[513],"showIcon":[1,"show-icon"],"hideIcon":[1,"hide-icon"],"type":[1025]},null,{"type":["onTypeChange"]}]]],["ion-fab_3",[[33,"ion-fab-button",{"color":[513],"activated":[4],"disabled":[4],"download":[1],"href":[1],"rel":[1],"routerDirection":[1,"router-direction"],"routerAnimation":[16,"router-animation"],"target":[1],"show":[4],"translucent":[4],"type":[1],"size":[1],"closeIcon":[1,"close-icon"]}],[1,"ion-fab",{"horizontal":[1],"vertical":[1],"edge":[4],"activated":[1028],"close":[64],"toggle":[64]},null,{"activated":["activatedChanged"]}],[1,"ion-fab-list",{"activated":[4],"side":[1]},null,{"activated":["activatedChanged"]}]]],["ion-refresher_2",[[0,"ion-refresher-content",{"pullingIcon":[1025,"pulling-icon"],"pullingText":[1,"pulling-text"],"refreshingSpinner":[1025,"refreshing-spinner"],"refreshingText":[1,"refreshing-text"]}],[32,"ion-refresher",{"pullMin":[2,"pull-min"],"pullMax":[2,"pull-max"],"closeDuration":[1,"close-duration"],"snapbackDuration":[1,"snapback-duration"],"pullFactor":[2,"pull-factor"],"disabled":[4],"nativeRefresher":[32],"state":[32],"complete":[64],"cancel":[64],"getProgress":[64]},null,{"disabled":["disabledChanged"]}]]],["ion-back-button",[[33,"ion-back-button",{"color":[513],"defaultHref":[1025,"default-href"],"disabled":[516],"icon":[1],"text":[1],"type":[1],"routerAnimation":[16,"router-animation"]}]]],["ion-toast",[[33,"ion-toast",{"overlayIndex":[2,"overlay-index"],"delegate":[16],"hasController":[4,"has-controller"],"color":[513],"enterAnimation":[16,"enter-animation"],"leaveAnimation":[16,"leave-animation"],"cssClass":[1,"css-class"],"duration":[2],"header":[1],"layout":[1],"message":[1],"keyboardClose":[4,"keyboard-close"],"position":[1],"positionAnchor":[1,"position-anchor"],"buttons":[16],"translucent":[4],"animated":[4],"icon":[1],"htmlAttributes":[16,"html-attributes"],"swipeGesture":[1,"swipe-gesture"],"isOpen":[4,"is-open"],"trigger":[1],"revealContentToScreenReader":[32],"present":[64],"dismiss":[64],"onDidDismiss":[64],"onWillDismiss":[64]},null,{"swipeGesture":["swipeGestureChanged"],"isOpen":["onIsOpenChange"],"trigger":["triggerChanged"]}]]],["ion-card_5",[[33,"ion-card",{"color":[513],"button":[4],"type":[1],"disabled":[4],"download":[1],"href":[1],"rel":[1],"routerDirection":[1,"router-direction"],"routerAnimation":[16,"router-animation"],"target":[1]}],[32,"ion-card-content"],[33,"ion-card-header",{"color":[513],"translucent":[4]}],[33,"ion-card-subtitle",{"color":[513]}],[33,"ion-card-title",{"color":[513]}]]],["ion-item-option_3",[[33,"ion-item-option",{"color":[513],"disabled":[4],"download":[1],"expandable":[4],"href":[1],"rel":[1],"target":[1],"type":[1]}],[32,"ion-item-options",{"side":[1],"fireSwipeEvent":[64]}],[0,"ion-item-sliding",{"disabled":[4],"state":[32],"getOpenAmount":[64],"getSlidingRatio":[64],"open":[64],"close":[64],"closeOpened":[64]},null,{"disabled":["disabledChanged"]}]]],["ion-accordion_2",[[49,"ion-accordion",{"value":[1],"disabled":[4],"readonly":[4],"toggleIcon":[1,"toggle-icon"],"toggleIconSlot":[1,"toggle-icon-slot"],"state":[32],"isNext":[32],"isPrevious":[32]},null,{"value":["valueChanged"]}],[33,"ion-accordion-group",{"animated":[4],"multiple":[4],"value":[1025],"disabled":[4],"readonly":[4],"expand":[1],"requestAccordionToggle":[64],"getAccordions":[64]},[[0,"keydown","onKeydown"]],{"value":["valueChanged"],"disabled":["disabledChanged"],"readonly":["readonlyChanged"]}]]],["ion-infinite-scroll_2",[[32,"ion-infinite-scroll-content",{"loadingSpinner":[1025,"loading-spinner"],"loadingText":[1,"loading-text"]}],[0,"ion-infinite-scroll",{"threshold":[1],"disabled":[4],"position":[1],"isLoading":[32],"complete":[64]},null,{"threshold":["thresholdChanged"],"disabled":["disabledChanged"]}]]],["ion-reorder_2",[[33,"ion-reorder",null,[[2,"click","onClick"]]],[0,"ion-reorder-group",{"disabled":[4],"state":[32],"complete":[64]},null,{"disabled":["disabledChanged"]}]]],["ion-segment_2",[[33,"ion-segment-button",{"contentId":[513,"content-id"],"disabled":[1028],"layout":[1],"type":[1],"value":[8],"checked":[32],"setFocus":[64]},null,{"value":["valueChanged"]}],[33,"ion-segment",{"color":[513],"disabled":[4],"scrollable":[4],"swipeGesture":[4,"swipe-gesture"],"value":[1032],"selectOnFocus":[4,"select-on-focus"],"activated":[32]},[[16,"ionSegmentViewScroll","handleSegmentViewScroll"],[0,"keydown","onKeyDown"]],{"color":["colorChanged"],"swipeGesture":["swipeGestureChanged"],"value":["valueChanged"],"disabled":["disabledChanged"]}]]],["ion-chip",[[33,"ion-chip",{"color":[513],"outline":[4],"disabled":[4]}]]],["ion-input",[[38,"ion-input",{"color":[513],"autocapitalize":[1],"autocomplete":[1],"autocorrect":[1],"autofocus":[4],"clearInput":[4,"clear-input"],"clearInputIcon":[1,"clear-input-icon"],"clearOnEdit":[4,"clear-on-edit"],"counter":[4],"counterFormatter":[16,"counter-formatter"],"debounce":[2],"disabled":[516],"enterkeyhint":[1],"errorText":[1,"error-text"],"fill":[1],"inputmode":[1],"helperText":[1,"helper-text"],"label":[1],"labelPlacement":[1,"label-placement"],"max":[8],"maxlength":[2],"min":[8],"minlength":[2],"multiple":[4],"name":[1],"pattern":[1],"placeholder":[1],"readonly":[516],"required":[4],"shape":[1],"spellcheck":[4],"step":[1],"type":[1],"value":[1032],"hasFocus":[32],"setFocus":[64],"getInputElement":[64]},[[2,"click","onClickCapture"]],{"debounce":["debounceChanged"],"type":["onTypeChange"],"value":["valueChanged"],"dir":["onDirChanged"]}]]],["ion-searchbar",[[34,"ion-searchbar",{"color":[513],"animated":[4],"autocapitalize":[1],"autocomplete":[1],"autocorrect":[1],"cancelButtonIcon":[1,"cancel-button-icon"],"cancelButtonText":[1,"cancel-button-text"],"clearIcon":[1,"clear-icon"],"debounce":[2],"disabled":[4],"inputmode":[1],"enterkeyhint":[1],"maxlength":[2],"minlength":[2],"name":[1],"placeholder":[1],"searchIcon":[1,"search-icon"],"showCancelButton":[1,"show-cancel-button"],"showClearButton":[1,"show-clear-button"],"spellcheck":[4],"type":[1],"value":[1025],"focused":[32],"noAnimate":[32],"setFocus":[64],"getInputElement":[64]},null,{"lang":["onLangChanged"],"dir":["onDirChanged"],"debounce":["debounceChanged"],"value":["valueChanged"],"showCancelButton":["showCancelButtonChanged"]}]]],["ion-toggle",[[33,"ion-toggle",{"color":[513],"name":[1],"checked":[1028],"disabled":[4],"errorText":[1,"error-text"],"helperText":[1,"helper-text"],"value":[1],"enableOnOffLabels":[4,"enable-on-off-labels"],"labelPlacement":[1,"label-placement"],"justify":[1],"alignment":[1],"required":[4],"activated":[32]},null,{"disabled":["disabledChanged"]}]]],["ion-nav_2",[[1,"ion-nav",{"delegate":[16],"swipeGesture":[1028,"swipe-gesture"],"animated":[4],"animation":[16],"rootParams":[16,"root-params"],"root":[1],"push":[64],"insert":[64],"insertPages":[64],"pop":[64],"popTo":[64],"popToRoot":[64],"removeIndex":[64],"setRoot":[64],"setPages":[64],"setRouteId":[64],"getRouteId":[64],"getActive":[64],"getByIndex":[64],"canGoBack":[64],"getPrevious":[64],"getLength":[64]},null,{"swipeGesture":["swipeGestureChanged"],"root":["rootChanged"]}],[0,"ion-nav-link",{"component":[1],"componentProps":[16,"component-props"],"routerDirection":[1,"router-direction"],"routerAnimation":[16,"router-animation"]}]]],["ion-tab_2",[[1,"ion-tab",{"active":[1028],"delegate":[16],"tab":[1],"component":[1],"setActive":[64]},null,{"active":["changeActive"]}],[1,"ion-tabs",{"useRouter":[1028,"use-router"],"selectedTab":[32],"select":[64],"getTab":[64],"getSelected":[64],"setRouteId":[64],"getRouteId":[64]}]]],["ion-textarea",[[38,"ion-textarea",{"color":[513],"autocapitalize":[1],"autofocus":[4],"clearOnEdit":[4,"clear-on-edit"],"debounce":[2],"disabled":[4],"fill":[1],"inputmode":[1],"enterkeyhint":[1],"maxlength":[2],"minlength":[2],"name":[1],"placeholder":[1],"readonly":[4],"required":[4],"spellcheck":[4],"cols":[514],"rows":[2],"wrap":[1],"autoGrow":[516,"auto-grow"],"value":[1025],"counter":[4],"counterFormatter":[16,"counter-formatter"],"errorText":[1,"error-text"],"helperText":[1,"helper-text"],"label":[1],"labelPlacement":[1,"label-placement"],"shape":[1],"hasFocus":[32],"setFocus":[64],"getInputElement":[64]},[[2,"click","onClickCapture"]],{"debounce":["debounceChanged"],"value":["valueChanged"],"dir":["onDirChanged"]}]]],["ion-backdrop",[[33,"ion-backdrop",{"visible":[4],"tappable":[4],"stopPropagation":[4,"stop-propagation"]},[[2,"click","onMouseDown"]]]]],["ion-loading",[[34,"ion-loading",{"overlayIndex":[2,"overlay-index"],"delegate":[16],"hasController":[4,"has-controller"],"keyboardClose":[4,"keyboard-close"],"enterAnimation":[16,"enter-animation"],"leaveAnimation":[16,"leave-animation"],"message":[1],"cssClass":[1,"css-class"],"duration":[2],"backdropDismiss":[4,"backdrop-dismiss"],"showBackdrop":[4,"show-backdrop"],"spinner":[1025],"translucent":[4],"animated":[4],"htmlAttributes":[16,"html-attributes"],"isOpen":[4,"is-open"],"trigger":[1],"present":[64],"dismiss":[64],"onDidDismiss":[64],"onWillDismiss":[64]},null,{"isOpen":["onIsOpenChange"],"trigger":["triggerChanged"]}]]],["ion-breadcrumb_2",[[33,"ion-breadcrumb",{"collapsed":[4],"last":[4],"showCollapsedIndicator":[4,"show-collapsed-indicator"],"color":[1],"active":[4],"disabled":[4],"download":[1],"href":[1],"rel":[1],"separator":[4],"target":[1],"routerDirection":[1,"router-direction"],"routerAnimation":[16,"router-animation"]}],[33,"ion-breadcrumbs",{"color":[513],"maxItems":[2,"max-items"],"itemsBeforeCollapse":[2,"items-before-collapse"],"itemsAfterCollapse":[2,"items-after-collapse"],"collapsed":[32],"activeChanged":[32]},[[0,"collapsedClick","onCollapsedClick"]],{"maxItems":["maxItemsChanged"],"itemsBeforeCollapse":["maxItemsChanged"],"itemsAfterCollapse":["maxItemsChanged"]}]]],["ion-tab-bar_2",[[33,"ion-tab-button",{"disabled":[4],"download":[1],"href":[1],"rel":[1],"layout":[1025],"selected":[1028],"tab":[1],"target":[1]},[[8,"ionTabBarChanged","onTabBarChanged"]]],[33,"ion-tab-bar",{"color":[513],"selectedTab":[1,"selected-tab"],"translucent":[4],"keyboardVisible":[32]},null,{"selectedTab":["selectedTabChanged"]}]]],["ion-datetime-button",[[33,"ion-datetime-button",{"color":[513],"disabled":[516],"datetime":[1],"datetimePresentation":[32],"dateText":[32],"timeText":[32],"datetimeActive":[32],"selectedButton":[32]}]]],["ion-route_4",[[0,"ion-route",{"url":[1],"component":[1],"componentProps":[16,"component-props"],"beforeLeave":[16,"before-leave"],"beforeEnter":[16,"before-enter"]},null,{"url":["onUpdate"],"component":["onUpdate"],"componentProps":["onComponentProps"]}],[0,"ion-route-redirect",{"from":[1],"to":[1]},null,{"from":["propDidChange"],"to":["propDidChange"]}],[0,"ion-router",{"root":[1],"useHash":[4,"use-hash"],"canTransition":[64],"push":[64],"back":[64],"printDebug":[64],"navChanged":[64]},[[8,"popstate","onPopState"],[4,"ionBackButton","onBackButton"]]],[1,"ion-router-link",{"color":[513],"href":[1],"rel":[1],"routerDirection":[1,"router-direction"],"routerAnimation":[16,"router-animation"],"target":[1]}]]],["ion-avatar_3",[[33,"ion-avatar"],[33,"ion-badge",{"color":[513]}],[1,"ion-thumbnail"]]],["ion-col_3",[[1,"ion-col",{"offset":[1],"offsetXs":[1,"offset-xs"],"offsetSm":[1,"offset-sm"],"offsetMd":[1,"offset-md"],"offsetLg":[1,"offset-lg"],"offsetXl":[1,"offset-xl"],"pull":[1],"pullXs":[1,"pull-xs"],"pullSm":[1,"pull-sm"],"pullMd":[1,"pull-md"],"pullLg":[1,"pull-lg"],"pullXl":[1,"pull-xl"],"push":[1],"pushXs":[1,"push-xs"],"pushSm":[1,"push-sm"],"pushMd":[1,"push-md"],"pushLg":[1,"push-lg"],"pushXl":[1,"push-xl"],"size":[1],"sizeXs":[1,"size-xs"],"sizeSm":[1,"size-sm"],"sizeMd":[1,"size-md"],"sizeLg":[1,"size-lg"],"sizeXl":[1,"size-xl"]},[[9,"resize","onResize"]]],[1,"ion-grid",{"fixed":[4]}],[1,"ion-row"]]],["ion-img",[[1,"ion-img",{"alt":[1],"src":[1],"loadSrc":[32],"loadError":[32]},null,{"src":["srcChanged"]}]]],["ion-input-otp",[[38,"ion-input-otp",{"autocapitalize":[1],"color":[513],"disabled":[516],"fill":[1],"inputmode":[1],"length":[2],"pattern":[1],"readonly":[516],"separators":[1],"shape":[1],"size":[1],"type":[1],"value":[1032],"inputValues":[32],"hasFocus":[32],"setFocus":[64]},null,{"value":["valueChanged"],"separators":["processSeparators"],"length":["processSeparators"]}]]],["ion-progress-bar",[[33,"ion-progress-bar",{"type":[1],"reversed":[4],"value":[2],"buffer":[2],"color":[513]}]]],["ion-range",[[33,"ion-range",{"color":[513],"debounce":[2],"name":[1],"label":[1],"dualKnobs":[4,"dual-knobs"],"min":[2],"max":[2],"pin":[4],"pinFormatter":[16,"pin-formatter"],"snaps":[4],"step":[2],"ticks":[4],"activeBarStart":[1026,"active-bar-start"],"disabled":[4],"value":[1026],"labelPlacement":[1,"label-placement"],"ratioA":[32],"ratioB":[32],"pressedKnob":[32]},null,{"debounce":["debounceChanged"],"min":["minChanged"],"max":["maxChanged"],"step":["stepChanged"],"activeBarStart":["activeBarStartChanged"],"disabled":["disabledChanged"],"value":["valueChanged"]}]]],["ion-segment-content",[[1,"ion-segment-content"]]],["ion-segment-view",[[33,"ion-segment-view",{"disabled":[4],"isManualScroll":[32],"setContent":[64]},[[1,"scroll","handleScroll"],[1,"touchstart","handleScrollStart"],[1,"touchend","handleTouchEnd"]]]]],["ion-split-pane",[[33,"ion-split-pane",{"contentId":[513,"content-id"],"disabled":[4],"when":[8],"visible":[32],"isVisible":[64]},null,{"visible":["visibleChanged"],"disabled":["updateState"],"when":["updateState"]}]]],["ion-text",[[1,"ion-text",{"color":[513]}]]],["ion-select-modal",[[34,"ion-select-modal",{"header":[1],"multiple":[4],"options":[16]}]]],["ion-datetime_3",[[33,"ion-datetime",{"color":[1],"name":[1],"disabled":[4],"formatOptions":[16,"format-options"],"readonly":[4],"isDateEnabled":[16,"is-date-enabled"],"showAdjacentDays":[4,"show-adjacent-days"],"min":[1025],"max":[1025],"presentation":[1],"cancelText":[1,"cancel-text"],"doneText":[1,"done-text"],"clearText":[1,"clear-text"],"yearValues":[8,"year-values"],"monthValues":[8,"month-values"],"dayValues":[8,"day-values"],"hourValues":[8,"hour-values"],"minuteValues":[8,"minute-values"],"locale":[1],"firstDayOfWeek":[2,"first-day-of-week"],"titleSelectedDatesFormatter":[16,"title-selected-dates-formatter"],"multiple":[4],"highlightedDates":[16,"highlighted-dates"],"value":[1025],"showDefaultTitle":[4,"show-default-title"],"showDefaultButtons":[4,"show-default-buttons"],"showClearButton":[4,"show-clear-button"],"showDefaultTimeLabel":[4,"show-default-time-label"],"hourCycle":[1,"hour-cycle"],"size":[1],"preferWheel":[4,"prefer-wheel"],"showMonthAndYear":[32],"activeParts":[32],"workingParts":[32],"isTimePopoverOpen":[32],"forceRenderDate":[32],"confirm":[64],"reset":[64],"cancel":[64]},null,{"formatOptions":["formatOptionsChanged"],"disabled":["disabledChanged"],"min":["minChanged"],"max":["maxChanged"],"presentation":["presentationChanged"],"yearValues":["yearValuesChanged"],"monthValues":["monthValuesChanged"],"dayValues":["dayValuesChanged"],"hourValues":["hourValuesChanged"],"minuteValues":["minuteValuesChanged"],"value":["valueChanged"]}],[34,"ion-picker-legacy",{"overlayIndex":[2,"overlay-index"],"delegate":[16],"hasController":[4,"has-controller"],"keyboardClose":[4,"keyboard-close"],"enterAnimation":[16,"enter-animation"],"leaveAnimation":[16,"leave-animation"],"buttons":[16],"columns":[16],"cssClass":[1,"css-class"],"duration":[2],"showBackdrop":[4,"show-backdrop"],"backdropDismiss":[4,"backdrop-dismiss"],"animated":[4],"htmlAttributes":[16,"html-attributes"],"isOpen":[4,"is-open"],"trigger":[1],"presented":[32],"present":[64],"dismiss":[64],"onDidDismiss":[64],"onWillDismiss":[64],"getColumn":[64]},null,{"isOpen":["onIsOpenChange"],"trigger":["triggerChanged"]}],[32,"ion-picker-legacy-column",{"col":[16]},null,{"col":["colChanged"]}]]],["ion-action-sheet",[[34,"ion-action-sheet",{"overlayIndex":[2,"overlay-index"],"delegate":[16],"hasController":[4,"has-controller"],"keyboardClose":[4,"keyboard-close"],"enterAnimation":[16,"enter-animation"],"leaveAnimation":[16,"leave-animation"],"buttons":[16],"cssClass":[1,"css-class"],"backdropDismiss":[4,"backdrop-dismiss"],"header":[1],"subHeader":[1,"sub-header"],"translucent":[4],"animated":[4],"htmlAttributes":[16,"html-attributes"],"isOpen":[4,"is-open"],"trigger":[1],"present":[64],"dismiss":[64],"onDidDismiss":[64],"onWillDismiss":[64]},null,{"isOpen":["onIsOpenChange"],"trigger":["triggerChanged"]}]]],["ion-alert",[[34,"ion-alert",{"overlayIndex":[2,"overlay-index"],"delegate":[16],"hasController":[4,"has-controller"],"keyboardClose":[4,"keyboard-close"],"enterAnimation":[16,"enter-animation"],"leaveAnimation":[16,"leave-animation"],"cssClass":[1,"css-class"],"header":[1],"subHeader":[1,"sub-header"],"message":[1],"buttons":[16],"inputs":[1040],"backdropDismiss":[4,"backdrop-dismiss"],"translucent":[4],"animated":[4],"htmlAttributes":[16,"html-attributes"],"isOpen":[4,"is-open"],"trigger":[1],"present":[64],"dismiss":[64],"onDidDismiss":[64],"onWillDismiss":[64]},[[4,"keydown","onKeydown"]],{"isOpen":["onIsOpenChange"],"trigger":["triggerChanged"],"buttons":["buttonsChanged"],"inputs":["inputsChanged"]}]]],["ion-modal",[[33,"ion-modal",{"hasController":[4,"has-controller"],"overlayIndex":[2,"overlay-index"],"delegate":[16],"keyboardClose":[4,"keyboard-close"],"enterAnimation":[16,"enter-animation"],"leaveAnimation":[16,"leave-animation"],"breakpoints":[16],"expandToScroll":[4,"expand-to-scroll"],"initialBreakpoint":[2,"initial-breakpoint"],"backdropBreakpoint":[2,"backdrop-breakpoint"],"handle":[4],"handleBehavior":[1,"handle-behavior"],"component":[1],"componentProps":[16,"component-props"],"cssClass":[1,"css-class"],"backdropDismiss":[4,"backdrop-dismiss"],"showBackdrop":[4,"show-backdrop"],"animated":[4],"presentingElement":[16,"presenting-element"],"htmlAttributes":[16,"html-attributes"],"isOpen":[4,"is-open"],"trigger":[1],"keepContentsMounted":[4,"keep-contents-mounted"],"focusTrap":[4,"focus-trap"],"canDismiss":[4,"can-dismiss"],"presented":[32],"present":[64],"dismiss":[64],"onDidDismiss":[64],"onWillDismiss":[64],"setCurrentBreakpoint":[64],"getCurrentBreakpoint":[64]},null,{"isOpen":["onIsOpenChange"],"trigger":["triggerChanged"]}]]],["ion-picker",[[33,"ion-picker",{"exitInputMode":[64]},[[1,"touchstart","preventTouchStartPropagation"]]]]],["ion-picker-column",[[1,"ion-picker-column",{"disabled":[4],"value":[1032],"color":[513],"numericInput":[4,"numeric-input"],"ariaLabel":[32],"isActive":[32],"scrollActiveItemIntoView":[64],"setValue":[64],"setFocus":[64]},null,{"aria-label":["ariaLabelChanged"],"value":["valueChange"]}]]],["ion-picker-column-option",[[33,"ion-picker-column-option",{"disabled":[4],"value":[8],"color":[513],"ariaLabel":[32]},null,{"aria-label":["onAriaLabelChange"]}]]],["ion-popover",[[33,"ion-popover",{"hasController":[4,"has-controller"],"delegate":[16],"overlayIndex":[2,"overlay-index"],"enterAnimation":[16,"enter-animation"],"leaveAnimation":[16,"leave-animation"],"component":[1],"componentProps":[16,"component-props"],"keyboardClose":[4,"keyboard-close"],"cssClass":[1,"css-class"],"backdropDismiss":[4,"backdrop-dismiss"],"event":[8],"showBackdrop":[4,"show-backdrop"],"translucent":[4],"animated":[4],"htmlAttributes":[16,"html-attributes"],"triggerAction":[1,"trigger-action"],"trigger":[1],"size":[1],"dismissOnSelect":[4,"dismiss-on-select"],"reference":[1],"side":[1],"alignment":[1025],"arrow":[4],"isOpen":[4,"is-open"],"keyboardEvents":[4,"keyboard-events"],"focusTrap":[4,"focus-trap"],"keepContentsMounted":[4,"keep-contents-mounted"],"presented":[32],"presentFromTrigger":[64],"present":[64],"dismiss":[64],"getParentPopover":[64],"onDidDismiss":[64],"onWillDismiss":[64]},null,{"trigger":["onTriggerChange"],"triggerAction":["onTriggerChange"],"isOpen":["onIsOpenChange"]}]]],["ion-checkbox",[[33,"ion-checkbox",{"color":[513],"name":[1],"checked":[1028],"indeterminate":[1028],"disabled":[4],"errorText":[1,"error-text"],"helperText":[1,"helper-text"],"value":[8],"labelPlacement":[1,"label-placement"],"justify":[1],"alignment":[1],"required":[4],"setFocus":[64]}]]],["ion-item_8",[[33,"ion-item-divider",{"color":[513],"sticky":[4]}],[32,"ion-item-group"],[33,"ion-note",{"color":[513]}],[1,"ion-skeleton-text",{"animated":[4]}],[38,"ion-label",{"color":[513],"position":[1],"noAnimate":[32]},null,{"color":["colorChanged"],"position":["positionChanged"]}],[33,"ion-list-header",{"color":[513],"lines":[1]}],[33,"ion-item",{"color":[513],"button":[4],"detail":[4],"detailIcon":[1,"detail-icon"],"disabled":[516],"download":[1],"href":[1],"rel":[1],"lines":[1],"routerAnimation":[16,"router-animation"],"routerDirection":[1,"router-direction"],"target":[1],"type":[1],"multipleInputs":[32],"focusable":[32]},[[0,"ionColor","labelColorChanged"],[0,"ionStyle","itemStyle"]],{"button":["buttonChanged"]}],[32,"ion-list",{"lines":[1],"inset":[4],"closeSlidingItems":[64]}]]],["ion-app_8",[[0,"ion-app",{"setFocus":[64]}],[36,"ion-footer",{"collapse":[1],"translucent":[4],"keyboardVisible":[32]}],[1,"ion-router-outlet",{"mode":[1025],"delegate":[16],"animated":[4],"animation":[16],"swipeHandler":[16,"swipe-handler"],"commit":[64],"setRouteId":[64],"getRouteId":[64]},null,{"swipeHandler":["swipeHandlerChanged"]}],[1,"ion-content",{"color":[513],"fullscreen":[4],"fixedSlotPlacement":[1,"fixed-slot-placement"],"forceOverscroll":[1028,"force-overscroll"],"scrollX":[4,"scroll-x"],"scrollY":[4,"scroll-y"],"scrollEvents":[4,"scroll-events"],"getScrollElement":[64],"getBackgroundElement":[64],"scrollToTop":[64],"scrollToBottom":[64],"scrollByPoint":[64],"scrollToPoint":[64]},[[9,"resize","onResize"]]],[36,"ion-header",{"collapse":[1],"translucent":[4]}],[33,"ion-title",{"color":[513],"size":[1]},null,{"size":["sizeChanged"]}],[33,"ion-toolbar",{"color":[513]},[[0,"ionStyle","childrenStyle"]]],[38,"ion-buttons",{"collapse":[4]}]]],["ion-select_3",[[33,"ion-select",{"cancelText":[1,"cancel-text"],"color":[513],"compareWith":[1,"compare-with"],"disabled":[4],"fill":[1],"errorText":[1,"error-text"],"helperText":[1,"helper-text"],"interface":[1],"interfaceOptions":[8,"interface-options"],"justify":[1],"label":[1],"labelPlacement":[1,"label-placement"],"multiple":[4],"name":[1],"okText":[1,"ok-text"],"placeholder":[1],"selectedText":[1,"selected-text"],"toggleIcon":[1,"toggle-icon"],"expandedIcon":[1,"expanded-icon"],"shape":[1],"value":[1032],"required":[4],"isExpanded":[32],"hasFocus":[32],"open":[64]},null,{"disabled":["styleChanged"],"isExpanded":["styleChanged"],"placeholder":["styleChanged"],"value":["styleChanged"]}],[1,"ion-select-option",{"disabled":[4],"value":[8]}],[34,"ion-select-popover",{"header":[1],"subHeader":[1,"sub-header"],"message":[1],"multiple":[4],"options":[16]}]]],["ion-spinner",[[1,"ion-spinner",{"color":[513],"duration":[2],"name":[1],"paused":[4]}]]],["ion-radio_2",[[33,"ion-radio",{"color":[513],"name":[1],"disabled":[4],"value":[8],"labelPlacement":[1,"label-placement"],"justify":[1],"alignment":[1],"checked":[32],"buttonTabindex":[32],"setFocus":[64],"setButtonTabindex":[64]},null,{"value":["valueChanged"]}],[36,"ion-radio-group",{"allowEmptySelection":[4,"allow-empty-selection"],"compareWith":[1,"compare-with"],"name":[1],"value":[1032],"helperText":[1,"helper-text"],"errorText":[1,"error-text"],"setFocus":[64]},[[4,"keydown","onKeydown"]],{"value":["valueChanged"]}]]],["ion-ripple-effect",[[1,"ion-ripple-effect",{"type":[1],"addRipple":[64]}]]],["ion-button_2",[[33,"ion-button",{"color":[513],"buttonType":[1025,"button-type"],"disabled":[516],"expand":[513],"fill":[1537],"routerDirection":[1,"router-direction"],"routerAnimation":[16,"router-animation"],"download":[1],"href":[1],"rel":[1],"shape":[513],"size":[513],"strong":[4],"target":[1],"type":[1],"form":[1],"isCircle":[32]},null,{"disabled":["disabledChanged"],"aria-checked":["onAriaChanged"],"aria-label":["onAriaChanged"]}],[1,"ion-icon",{"mode":[1025],"color":[1],"ios":[1],"md":[1],"flipRtl":[4,"flip-rtl"],"name":[513],"src":[1],"icon":[8],"size":[1],"lazy":[4],"sanitize":[4],"svgContent":[32],"isVisible":[32]},null,{"name":["loadIcon"],"src":["loadIcon"],"icon":["loadIcon"],"ios":["loadIcon"],"md":["loadIcon"]}]]]]'), options);
});

// node_modules/@ionic/core/loader/index.js
(function() {
  if ("undefined" !== typeof window && void 0 !== window.Reflect && void 0 !== window.customElements) {
    var a = HTMLElement;
    window.HTMLElement = function() {
      return Reflect.construct(a, [], this.constructor);
    };
    HTMLElement.prototype = a.prototype;
    HTMLElement.prototype.constructor = HTMLElement;
    Object.setPrototypeOf(HTMLElement, a);
  }
})();

// node_modules/@ionic/angular/fesm2022/ionic-angular.mjs
var _c0 = ["*"];
var _c1 = ["outletContent"];
var _c2 = ["outlet"];
var _c3 = [[["", "slot", "top"]], "*", [["ion-tab"]]];
var _c4 = ["[slot=top]", "*", "ion-tab"];
function IonTabs_ion_router_outlet_3_Template(rf, ctx) {
  if (rf & 1) {
    const _r1 = ɵɵgetCurrentView();
    ɵɵelementStart(0, "ion-router-outlet", 5, 1);
    ɵɵlistener("stackWillChange", function IonTabs_ion_router_outlet_3_Template_ion_router_outlet_stackWillChange_0_listener($event) {
      ɵɵrestoreView(_r1);
      const ctx_r1 = ɵɵnextContext();
      return ɵɵresetView(ctx_r1.onStackWillChange($event));
    })("stackDidChange", function IonTabs_ion_router_outlet_3_Template_ion_router_outlet_stackDidChange_0_listener($event) {
      ɵɵrestoreView(_r1);
      const ctx_r1 = ɵɵnextContext();
      return ɵɵresetView(ctx_r1.onStackDidChange($event));
    });
    ɵɵelementEnd();
  }
}
function IonTabs_ng_content_4_Template(rf, ctx) {
  if (rf & 1) {
    ɵɵprojection(0, 2, ["*ngIf", "tabs.length > 0"]);
  }
}
function IonModal_div_0_Template(rf, ctx) {
  if (rf & 1) {
    ɵɵelementStart(0, "div", 1);
    ɵɵelementContainer(1, 2);
    ɵɵelementEnd();
  }
  if (rf & 2) {
    const ctx_r0 = ɵɵnextContext();
    ɵɵadvance();
    ɵɵproperty("ngTemplateOutlet", ctx_r0.template);
  }
}
function IonPopover_ng_container_0_Template(rf, ctx) {
  if (rf & 1) {
    ɵɵelementContainer(0, 1);
  }
  if (rf & 2) {
    const ctx_r0 = ɵɵnextContext();
    ɵɵproperty("ngTemplateOutlet", ctx_r0.template);
  }
}
var _BooleanValueAccessorDirective = class _BooleanValueAccessorDirective extends ValueAccessor {
  constructor(injector, el) {
    super(injector, el);
  }
  writeValue(value) {
    this.elementRef.nativeElement.checked = this.lastValue = value;
    setIonicClasses(this.elementRef);
  }
  _handleIonChange(el) {
    this.handleValueChange(el, el.checked);
  }
};
/** @nocollapse */
__publicField(_BooleanValueAccessorDirective, "ɵfac", function BooleanValueAccessorDirective_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _BooleanValueAccessorDirective)(ɵɵdirectiveInject(Injector), ɵɵdirectiveInject(ElementRef));
});
/** @nocollapse */
__publicField(_BooleanValueAccessorDirective, "ɵdir", ɵɵdefineDirective({
  type: _BooleanValueAccessorDirective,
  selectors: [["ion-checkbox"], ["ion-toggle"]],
  hostBindings: function BooleanValueAccessorDirective_HostBindings(rf, ctx) {
    if (rf & 1) {
      ɵɵlistener("ionChange", function BooleanValueAccessorDirective_ionChange_HostBindingHandler($event) {
        return ctx._handleIonChange($event.target);
      });
    }
  },
  standalone: false,
  features: [ɵɵProvidersFeature([{
    provide: NG_VALUE_ACCESSOR,
    useExisting: _BooleanValueAccessorDirective,
    multi: true
  }]), ɵɵInheritDefinitionFeature]
}));
var BooleanValueAccessorDirective = _BooleanValueAccessorDirective;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(BooleanValueAccessorDirective, [{
    type: Directive,
    args: [{
      selector: "ion-checkbox,ion-toggle",
      providers: [{
        provide: NG_VALUE_ACCESSOR,
        useExisting: BooleanValueAccessorDirective,
        multi: true
      }]
    }]
  }], function() {
    return [{
      type: Injector
    }, {
      type: ElementRef
    }];
  }, {
    _handleIonChange: [{
      type: HostListener,
      args: ["ionChange", ["$event.target"]]
    }]
  });
})();
var _NumericValueAccessorDirective = class _NumericValueAccessorDirective extends ValueAccessor {
  el;
  constructor(injector, el) {
    super(injector, el);
    this.el = el;
  }
  handleInputEvent(el) {
    this.handleValueChange(el, el.value);
  }
  registerOnChange(fn) {
    if (this.el.nativeElement.tagName === "ION-INPUT" || this.el.nativeElement.tagName === "ION-INPUT-OTP") {
      super.registerOnChange((value) => {
        fn(value === "" ? null : parseFloat(value));
      });
    } else {
      super.registerOnChange(fn);
    }
  }
};
/** @nocollapse */
__publicField(_NumericValueAccessorDirective, "ɵfac", function NumericValueAccessorDirective_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _NumericValueAccessorDirective)(ɵɵdirectiveInject(Injector), ɵɵdirectiveInject(ElementRef));
});
/** @nocollapse */
__publicField(_NumericValueAccessorDirective, "ɵdir", ɵɵdefineDirective({
  type: _NumericValueAccessorDirective,
  selectors: [["ion-input", "type", "number"], ["ion-input-otp", 3, "type", "text"], ["ion-range"]],
  hostBindings: function NumericValueAccessorDirective_HostBindings(rf, ctx) {
    if (rf & 1) {
      ɵɵlistener("ionInput", function NumericValueAccessorDirective_ionInput_HostBindingHandler($event) {
        return ctx.handleInputEvent($event.target);
      });
    }
  },
  standalone: false,
  features: [ɵɵProvidersFeature([{
    provide: NG_VALUE_ACCESSOR,
    useExisting: _NumericValueAccessorDirective,
    multi: true
  }]), ɵɵInheritDefinitionFeature]
}));
var NumericValueAccessorDirective = _NumericValueAccessorDirective;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(NumericValueAccessorDirective, [{
    type: Directive,
    args: [{
      selector: "ion-input[type=number],ion-input-otp:not([type=text]),ion-range",
      providers: [{
        provide: NG_VALUE_ACCESSOR,
        useExisting: NumericValueAccessorDirective,
        multi: true
      }]
    }]
  }], function() {
    return [{
      type: Injector
    }, {
      type: ElementRef
    }];
  }, {
    handleInputEvent: [{
      type: HostListener,
      args: ["ionInput", ["$event.target"]]
    }]
  });
})();
var _SelectValueAccessorDirective = class _SelectValueAccessorDirective extends ValueAccessor {
  constructor(injector, el) {
    super(injector, el);
  }
  _handleChangeEvent(el) {
    this.handleValueChange(el, el.value);
  }
};
/** @nocollapse */
__publicField(_SelectValueAccessorDirective, "ɵfac", function SelectValueAccessorDirective_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _SelectValueAccessorDirective)(ɵɵdirectiveInject(Injector), ɵɵdirectiveInject(ElementRef));
});
/** @nocollapse */
__publicField(_SelectValueAccessorDirective, "ɵdir", ɵɵdefineDirective({
  type: _SelectValueAccessorDirective,
  selectors: [["ion-select"], ["ion-radio-group"], ["ion-segment"], ["ion-datetime"]],
  hostBindings: function SelectValueAccessorDirective_HostBindings(rf, ctx) {
    if (rf & 1) {
      ɵɵlistener("ionChange", function SelectValueAccessorDirective_ionChange_HostBindingHandler($event) {
        return ctx._handleChangeEvent($event.target);
      });
    }
  },
  standalone: false,
  features: [ɵɵProvidersFeature([{
    provide: NG_VALUE_ACCESSOR,
    useExisting: _SelectValueAccessorDirective,
    multi: true
  }]), ɵɵInheritDefinitionFeature]
}));
var SelectValueAccessorDirective = _SelectValueAccessorDirective;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(SelectValueAccessorDirective, [{
    type: Directive,
    args: [{
      /* tslint:disable-next-line:directive-selector */
      selector: "ion-select, ion-radio-group, ion-segment, ion-datetime",
      providers: [{
        provide: NG_VALUE_ACCESSOR,
        useExisting: SelectValueAccessorDirective,
        multi: true
      }]
    }]
  }], function() {
    return [{
      type: Injector
    }, {
      type: ElementRef
    }];
  }, {
    _handleChangeEvent: [{
      type: HostListener,
      args: ["ionChange", ["$event.target"]]
    }]
  });
})();
var _TextValueAccessorDirective = class _TextValueAccessorDirective extends ValueAccessor {
  constructor(injector, el) {
    super(injector, el);
  }
  _handleInputEvent(el) {
    this.handleValueChange(el, el.value);
  }
};
/** @nocollapse */
__publicField(_TextValueAccessorDirective, "ɵfac", function TextValueAccessorDirective_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _TextValueAccessorDirective)(ɵɵdirectiveInject(Injector), ɵɵdirectiveInject(ElementRef));
});
/** @nocollapse */
__publicField(_TextValueAccessorDirective, "ɵdir", ɵɵdefineDirective({
  type: _TextValueAccessorDirective,
  selectors: [["ion-input", 3, "type", "number"], ["ion-input-otp", "type", "text"], ["ion-textarea"], ["ion-searchbar"]],
  hostBindings: function TextValueAccessorDirective_HostBindings(rf, ctx) {
    if (rf & 1) {
      ɵɵlistener("ionInput", function TextValueAccessorDirective_ionInput_HostBindingHandler($event) {
        return ctx._handleInputEvent($event.target);
      });
    }
  },
  standalone: false,
  features: [ɵɵProvidersFeature([{
    provide: NG_VALUE_ACCESSOR,
    useExisting: _TextValueAccessorDirective,
    multi: true
  }]), ɵɵInheritDefinitionFeature]
}));
var TextValueAccessorDirective = _TextValueAccessorDirective;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(TextValueAccessorDirective, [{
    type: Directive,
    args: [{
      selector: "ion-input:not([type=number]),ion-input-otp[type=text],ion-textarea,ion-searchbar",
      providers: [{
        provide: NG_VALUE_ACCESSOR,
        useExisting: TextValueAccessorDirective,
        multi: true
      }]
    }]
  }], function() {
    return [{
      type: Injector
    }, {
      type: ElementRef
    }];
  }, {
    _handleInputEvent: [{
      type: HostListener,
      args: ["ionInput", ["$event.target"]]
    }]
  });
})();
var proxyInputs = (Cmp, inputs) => {
  const Prototype = Cmp.prototype;
  inputs.forEach((item) => {
    Object.defineProperty(Prototype, item, {
      get() {
        return this.el[item];
      },
      set(val) {
        this.z.runOutsideAngular(() => this.el[item] = val);
      },
      /**
       * In the event that proxyInputs is called
       * multiple times re-defining these inputs
       * will cause an error to be thrown. As a result
       * we set configurable: true to indicate these
       * properties can be changed.
       */
      configurable: true
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
var _a;
var IonAccordion = (_a = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a, "ɵfac", function IonAccordion_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a, "ɵcmp", ɵɵdefineComponent({
  type: _a,
  selectors: [["ion-accordion"]],
  inputs: {
    disabled: "disabled",
    mode: "mode",
    readonly: "readonly",
    toggleIcon: "toggleIcon",
    toggleIconSlot: "toggleIconSlot",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonAccordion_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a);
IonAccordion = __decorate([ProxyCmp({
  inputs: ["disabled", "mode", "readonly", "toggleIcon", "toggleIconSlot", "value"]
})], IonAccordion);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonAccordion, [{
    type: Component,
    args: [{
      selector: "ion-accordion",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["disabled", "mode", "readonly", "toggleIcon", "toggleIconSlot", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a2;
var IonAccordionGroup = (_a2 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionChange"]);
  }
}, /** @nocollapse */
__publicField(_a2, "ɵfac", function IonAccordionGroup_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a2)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a2, "ɵcmp", ɵɵdefineComponent({
  type: _a2,
  selectors: [["ion-accordion-group"]],
  inputs: {
    animated: "animated",
    disabled: "disabled",
    expand: "expand",
    mode: "mode",
    multiple: "multiple",
    readonly: "readonly",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonAccordionGroup_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a2);
IonAccordionGroup = __decorate([ProxyCmp({
  inputs: ["animated", "disabled", "expand", "mode", "multiple", "readonly", "value"]
})], IonAccordionGroup);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonAccordionGroup, [{
    type: Component,
    args: [{
      selector: "ion-accordion-group",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["animated", "disabled", "expand", "mode", "multiple", "readonly", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a3;
var IonActionSheet = (_a3 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionActionSheetDidPresent", "ionActionSheetWillPresent", "ionActionSheetWillDismiss", "ionActionSheetDidDismiss", "didPresent", "willPresent", "willDismiss", "didDismiss"]);
  }
}, /** @nocollapse */
__publicField(_a3, "ɵfac", function IonActionSheet_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a3)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a3, "ɵcmp", ɵɵdefineComponent({
  type: _a3,
  selectors: [["ion-action-sheet"]],
  inputs: {
    animated: "animated",
    backdropDismiss: "backdropDismiss",
    buttons: "buttons",
    cssClass: "cssClass",
    enterAnimation: "enterAnimation",
    header: "header",
    htmlAttributes: "htmlAttributes",
    isOpen: "isOpen",
    keyboardClose: "keyboardClose",
    leaveAnimation: "leaveAnimation",
    mode: "mode",
    subHeader: "subHeader",
    translucent: "translucent",
    trigger: "trigger"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonActionSheet_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a3);
IonActionSheet = __decorate([ProxyCmp({
  inputs: ["animated", "backdropDismiss", "buttons", "cssClass", "enterAnimation", "header", "htmlAttributes", "isOpen", "keyboardClose", "leaveAnimation", "mode", "subHeader", "translucent", "trigger"],
  methods: ["present", "dismiss", "onDidDismiss", "onWillDismiss"]
})], IonActionSheet);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonActionSheet, [{
    type: Component,
    args: [{
      selector: "ion-action-sheet",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["animated", "backdropDismiss", "buttons", "cssClass", "enterAnimation", "header", "htmlAttributes", "isOpen", "keyboardClose", "leaveAnimation", "mode", "subHeader", "translucent", "trigger"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a4;
var IonAlert = (_a4 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionAlertDidPresent", "ionAlertWillPresent", "ionAlertWillDismiss", "ionAlertDidDismiss", "didPresent", "willPresent", "willDismiss", "didDismiss"]);
  }
}, /** @nocollapse */
__publicField(_a4, "ɵfac", function IonAlert_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a4)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a4, "ɵcmp", ɵɵdefineComponent({
  type: _a4,
  selectors: [["ion-alert"]],
  inputs: {
    animated: "animated",
    backdropDismiss: "backdropDismiss",
    buttons: "buttons",
    cssClass: "cssClass",
    enterAnimation: "enterAnimation",
    header: "header",
    htmlAttributes: "htmlAttributes",
    inputs: "inputs",
    isOpen: "isOpen",
    keyboardClose: "keyboardClose",
    leaveAnimation: "leaveAnimation",
    message: "message",
    mode: "mode",
    subHeader: "subHeader",
    translucent: "translucent",
    trigger: "trigger"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonAlert_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a4);
IonAlert = __decorate([ProxyCmp({
  inputs: ["animated", "backdropDismiss", "buttons", "cssClass", "enterAnimation", "header", "htmlAttributes", "inputs", "isOpen", "keyboardClose", "leaveAnimation", "message", "mode", "subHeader", "translucent", "trigger"],
  methods: ["present", "dismiss", "onDidDismiss", "onWillDismiss"]
})], IonAlert);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonAlert, [{
    type: Component,
    args: [{
      selector: "ion-alert",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["animated", "backdropDismiss", "buttons", "cssClass", "enterAnimation", "header", "htmlAttributes", "inputs", "isOpen", "keyboardClose", "leaveAnimation", "message", "mode", "subHeader", "translucent", "trigger"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a5;
var IonApp = (_a5 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a5, "ɵfac", function IonApp_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a5)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a5, "ɵcmp", ɵɵdefineComponent({
  type: _a5,
  selectors: [["ion-app"]],
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonApp_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a5);
IonApp = __decorate([ProxyCmp({
  methods: ["setFocus"]
})], IonApp);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonApp, [{
    type: Component,
    args: [{
      selector: "ion-app",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: []
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a6;
var IonAvatar = (_a6 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a6, "ɵfac", function IonAvatar_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a6)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a6, "ɵcmp", ɵɵdefineComponent({
  type: _a6,
  selectors: [["ion-avatar"]],
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonAvatar_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a6);
IonAvatar = __decorate([ProxyCmp({})], IonAvatar);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonAvatar, [{
    type: Component,
    args: [{
      selector: "ion-avatar",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: []
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a7;
var IonBackdrop = (_a7 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionBackdropTap"]);
  }
}, /** @nocollapse */
__publicField(_a7, "ɵfac", function IonBackdrop_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a7)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a7, "ɵcmp", ɵɵdefineComponent({
  type: _a7,
  selectors: [["ion-backdrop"]],
  inputs: {
    stopPropagation: "stopPropagation",
    tappable: "tappable",
    visible: "visible"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonBackdrop_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a7);
IonBackdrop = __decorate([ProxyCmp({
  inputs: ["stopPropagation", "tappable", "visible"]
})], IonBackdrop);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonBackdrop, [{
    type: Component,
    args: [{
      selector: "ion-backdrop",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["stopPropagation", "tappable", "visible"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a8;
var IonBadge = (_a8 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a8, "ɵfac", function IonBadge_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a8)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a8, "ɵcmp", ɵɵdefineComponent({
  type: _a8,
  selectors: [["ion-badge"]],
  inputs: {
    color: "color",
    mode: "mode"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonBadge_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a8);
IonBadge = __decorate([ProxyCmp({
  inputs: ["color", "mode"]
})], IonBadge);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonBadge, [{
    type: Component,
    args: [{
      selector: "ion-badge",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "mode"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a9;
var IonBreadcrumb = (_a9 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionFocus", "ionBlur"]);
  }
}, /** @nocollapse */
__publicField(_a9, "ɵfac", function IonBreadcrumb_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a9)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a9, "ɵcmp", ɵɵdefineComponent({
  type: _a9,
  selectors: [["ion-breadcrumb"]],
  inputs: {
    active: "active",
    color: "color",
    disabled: "disabled",
    download: "download",
    href: "href",
    mode: "mode",
    rel: "rel",
    routerAnimation: "routerAnimation",
    routerDirection: "routerDirection",
    separator: "separator",
    target: "target"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonBreadcrumb_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a9);
IonBreadcrumb = __decorate([ProxyCmp({
  inputs: ["active", "color", "disabled", "download", "href", "mode", "rel", "routerAnimation", "routerDirection", "separator", "target"]
})], IonBreadcrumb);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonBreadcrumb, [{
    type: Component,
    args: [{
      selector: "ion-breadcrumb",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["active", "color", "disabled", "download", "href", "mode", "rel", "routerAnimation", "routerDirection", "separator", "target"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a10;
var IonBreadcrumbs = (_a10 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionCollapsedClick"]);
  }
}, /** @nocollapse */
__publicField(_a10, "ɵfac", function IonBreadcrumbs_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a10)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a10, "ɵcmp", ɵɵdefineComponent({
  type: _a10,
  selectors: [["ion-breadcrumbs"]],
  inputs: {
    color: "color",
    itemsAfterCollapse: "itemsAfterCollapse",
    itemsBeforeCollapse: "itemsBeforeCollapse",
    maxItems: "maxItems",
    mode: "mode"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonBreadcrumbs_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a10);
IonBreadcrumbs = __decorate([ProxyCmp({
  inputs: ["color", "itemsAfterCollapse", "itemsBeforeCollapse", "maxItems", "mode"]
})], IonBreadcrumbs);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonBreadcrumbs, [{
    type: Component,
    args: [{
      selector: "ion-breadcrumbs",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "itemsAfterCollapse", "itemsBeforeCollapse", "maxItems", "mode"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a11;
var IonButton = (_a11 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionFocus", "ionBlur"]);
  }
}, /** @nocollapse */
__publicField(_a11, "ɵfac", function IonButton_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a11)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a11, "ɵcmp", ɵɵdefineComponent({
  type: _a11,
  selectors: [["ion-button"]],
  inputs: {
    buttonType: "buttonType",
    color: "color",
    disabled: "disabled",
    download: "download",
    expand: "expand",
    fill: "fill",
    form: "form",
    href: "href",
    mode: "mode",
    rel: "rel",
    routerAnimation: "routerAnimation",
    routerDirection: "routerDirection",
    shape: "shape",
    size: "size",
    strong: "strong",
    target: "target",
    type: "type"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonButton_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a11);
IonButton = __decorate([ProxyCmp({
  inputs: ["buttonType", "color", "disabled", "download", "expand", "fill", "form", "href", "mode", "rel", "routerAnimation", "routerDirection", "shape", "size", "strong", "target", "type"]
})], IonButton);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonButton, [{
    type: Component,
    args: [{
      selector: "ion-button",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["buttonType", "color", "disabled", "download", "expand", "fill", "form", "href", "mode", "rel", "routerAnimation", "routerDirection", "shape", "size", "strong", "target", "type"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a12;
var IonButtons = (_a12 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a12, "ɵfac", function IonButtons_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a12)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a12, "ɵcmp", ɵɵdefineComponent({
  type: _a12,
  selectors: [["ion-buttons"]],
  inputs: {
    collapse: "collapse"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonButtons_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a12);
IonButtons = __decorate([ProxyCmp({
  inputs: ["collapse"]
})], IonButtons);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonButtons, [{
    type: Component,
    args: [{
      selector: "ion-buttons",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["collapse"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a13;
var IonCard = (_a13 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a13, "ɵfac", function IonCard_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a13)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a13, "ɵcmp", ɵɵdefineComponent({
  type: _a13,
  selectors: [["ion-card"]],
  inputs: {
    button: "button",
    color: "color",
    disabled: "disabled",
    download: "download",
    href: "href",
    mode: "mode",
    rel: "rel",
    routerAnimation: "routerAnimation",
    routerDirection: "routerDirection",
    target: "target",
    type: "type"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonCard_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a13);
IonCard = __decorate([ProxyCmp({
  inputs: ["button", "color", "disabled", "download", "href", "mode", "rel", "routerAnimation", "routerDirection", "target", "type"]
})], IonCard);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonCard, [{
    type: Component,
    args: [{
      selector: "ion-card",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["button", "color", "disabled", "download", "href", "mode", "rel", "routerAnimation", "routerDirection", "target", "type"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a14;
var IonCardContent = (_a14 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a14, "ɵfac", function IonCardContent_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a14)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a14, "ɵcmp", ɵɵdefineComponent({
  type: _a14,
  selectors: [["ion-card-content"]],
  inputs: {
    mode: "mode"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonCardContent_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a14);
IonCardContent = __decorate([ProxyCmp({
  inputs: ["mode"]
})], IonCardContent);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonCardContent, [{
    type: Component,
    args: [{
      selector: "ion-card-content",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["mode"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a15;
var IonCardHeader = (_a15 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a15, "ɵfac", function IonCardHeader_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a15)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a15, "ɵcmp", ɵɵdefineComponent({
  type: _a15,
  selectors: [["ion-card-header"]],
  inputs: {
    color: "color",
    mode: "mode",
    translucent: "translucent"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonCardHeader_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a15);
IonCardHeader = __decorate([ProxyCmp({
  inputs: ["color", "mode", "translucent"]
})], IonCardHeader);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonCardHeader, [{
    type: Component,
    args: [{
      selector: "ion-card-header",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "mode", "translucent"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a16;
var IonCardSubtitle = (_a16 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a16, "ɵfac", function IonCardSubtitle_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a16)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a16, "ɵcmp", ɵɵdefineComponent({
  type: _a16,
  selectors: [["ion-card-subtitle"]],
  inputs: {
    color: "color",
    mode: "mode"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonCardSubtitle_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a16);
IonCardSubtitle = __decorate([ProxyCmp({
  inputs: ["color", "mode"]
})], IonCardSubtitle);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonCardSubtitle, [{
    type: Component,
    args: [{
      selector: "ion-card-subtitle",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "mode"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a17;
var IonCardTitle = (_a17 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a17, "ɵfac", function IonCardTitle_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a17)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a17, "ɵcmp", ɵɵdefineComponent({
  type: _a17,
  selectors: [["ion-card-title"]],
  inputs: {
    color: "color",
    mode: "mode"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonCardTitle_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a17);
IonCardTitle = __decorate([ProxyCmp({
  inputs: ["color", "mode"]
})], IonCardTitle);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonCardTitle, [{
    type: Component,
    args: [{
      selector: "ion-card-title",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "mode"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a18;
var IonCheckbox = (_a18 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionChange", "ionFocus", "ionBlur"]);
  }
}, /** @nocollapse */
__publicField(_a18, "ɵfac", function IonCheckbox_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a18)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a18, "ɵcmp", ɵɵdefineComponent({
  type: _a18,
  selectors: [["ion-checkbox"]],
  inputs: {
    alignment: "alignment",
    checked: "checked",
    color: "color",
    disabled: "disabled",
    errorText: "errorText",
    helperText: "helperText",
    indeterminate: "indeterminate",
    justify: "justify",
    labelPlacement: "labelPlacement",
    mode: "mode",
    name: "name",
    required: "required",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonCheckbox_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a18);
IonCheckbox = __decorate([ProxyCmp({
  inputs: ["alignment", "checked", "color", "disabled", "errorText", "helperText", "indeterminate", "justify", "labelPlacement", "mode", "name", "required", "value"]
})], IonCheckbox);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonCheckbox, [{
    type: Component,
    args: [{
      selector: "ion-checkbox",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["alignment", "checked", "color", "disabled", "errorText", "helperText", "indeterminate", "justify", "labelPlacement", "mode", "name", "required", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a19;
var IonChip = (_a19 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a19, "ɵfac", function IonChip_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a19)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a19, "ɵcmp", ɵɵdefineComponent({
  type: _a19,
  selectors: [["ion-chip"]],
  inputs: {
    color: "color",
    disabled: "disabled",
    mode: "mode",
    outline: "outline"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonChip_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a19);
IonChip = __decorate([ProxyCmp({
  inputs: ["color", "disabled", "mode", "outline"]
})], IonChip);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonChip, [{
    type: Component,
    args: [{
      selector: "ion-chip",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "disabled", "mode", "outline"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a20;
var IonCol = (_a20 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a20, "ɵfac", function IonCol_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a20)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a20, "ɵcmp", ɵɵdefineComponent({
  type: _a20,
  selectors: [["ion-col"]],
  inputs: {
    offset: "offset",
    offsetLg: "offsetLg",
    offsetMd: "offsetMd",
    offsetSm: "offsetSm",
    offsetXl: "offsetXl",
    offsetXs: "offsetXs",
    pull: "pull",
    pullLg: "pullLg",
    pullMd: "pullMd",
    pullSm: "pullSm",
    pullXl: "pullXl",
    pullXs: "pullXs",
    push: "push",
    pushLg: "pushLg",
    pushMd: "pushMd",
    pushSm: "pushSm",
    pushXl: "pushXl",
    pushXs: "pushXs",
    size: "size",
    sizeLg: "sizeLg",
    sizeMd: "sizeMd",
    sizeSm: "sizeSm",
    sizeXl: "sizeXl",
    sizeXs: "sizeXs"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonCol_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a20);
IonCol = __decorate([ProxyCmp({
  inputs: ["offset", "offsetLg", "offsetMd", "offsetSm", "offsetXl", "offsetXs", "pull", "pullLg", "pullMd", "pullSm", "pullXl", "pullXs", "push", "pushLg", "pushMd", "pushSm", "pushXl", "pushXs", "size", "sizeLg", "sizeMd", "sizeSm", "sizeXl", "sizeXs"]
})], IonCol);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonCol, [{
    type: Component,
    args: [{
      selector: "ion-col",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["offset", "offsetLg", "offsetMd", "offsetSm", "offsetXl", "offsetXs", "pull", "pullLg", "pullMd", "pullSm", "pullXl", "pullXs", "push", "pushLg", "pushMd", "pushSm", "pushXl", "pushXs", "size", "sizeLg", "sizeMd", "sizeSm", "sizeXl", "sizeXs"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a21;
var IonContent = (_a21 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionScrollStart", "ionScroll", "ionScrollEnd"]);
  }
}, /** @nocollapse */
__publicField(_a21, "ɵfac", function IonContent_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a21)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a21, "ɵcmp", ɵɵdefineComponent({
  type: _a21,
  selectors: [["ion-content"]],
  inputs: {
    color: "color",
    fixedSlotPlacement: "fixedSlotPlacement",
    forceOverscroll: "forceOverscroll",
    fullscreen: "fullscreen",
    scrollEvents: "scrollEvents",
    scrollX: "scrollX",
    scrollY: "scrollY"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonContent_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a21);
IonContent = __decorate([ProxyCmp({
  inputs: ["color", "fixedSlotPlacement", "forceOverscroll", "fullscreen", "scrollEvents", "scrollX", "scrollY"],
  methods: ["getScrollElement", "scrollToTop", "scrollToBottom", "scrollByPoint", "scrollToPoint"]
})], IonContent);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonContent, [{
    type: Component,
    args: [{
      selector: "ion-content",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "fixedSlotPlacement", "forceOverscroll", "fullscreen", "scrollEvents", "scrollX", "scrollY"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a22;
var IonDatetime = (_a22 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionCancel", "ionChange", "ionFocus", "ionBlur"]);
  }
}, /** @nocollapse */
__publicField(_a22, "ɵfac", function IonDatetime_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a22)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a22, "ɵcmp", ɵɵdefineComponent({
  type: _a22,
  selectors: [["ion-datetime"]],
  inputs: {
    cancelText: "cancelText",
    clearText: "clearText",
    color: "color",
    dayValues: "dayValues",
    disabled: "disabled",
    doneText: "doneText",
    firstDayOfWeek: "firstDayOfWeek",
    formatOptions: "formatOptions",
    highlightedDates: "highlightedDates",
    hourCycle: "hourCycle",
    hourValues: "hourValues",
    isDateEnabled: "isDateEnabled",
    locale: "locale",
    max: "max",
    min: "min",
    minuteValues: "minuteValues",
    mode: "mode",
    monthValues: "monthValues",
    multiple: "multiple",
    name: "name",
    preferWheel: "preferWheel",
    presentation: "presentation",
    readonly: "readonly",
    showAdjacentDays: "showAdjacentDays",
    showClearButton: "showClearButton",
    showDefaultButtons: "showDefaultButtons",
    showDefaultTimeLabel: "showDefaultTimeLabel",
    showDefaultTitle: "showDefaultTitle",
    size: "size",
    titleSelectedDatesFormatter: "titleSelectedDatesFormatter",
    value: "value",
    yearValues: "yearValues"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonDatetime_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a22);
IonDatetime = __decorate([ProxyCmp({
  inputs: ["cancelText", "clearText", "color", "dayValues", "disabled", "doneText", "firstDayOfWeek", "formatOptions", "highlightedDates", "hourCycle", "hourValues", "isDateEnabled", "locale", "max", "min", "minuteValues", "mode", "monthValues", "multiple", "name", "preferWheel", "presentation", "readonly", "showAdjacentDays", "showClearButton", "showDefaultButtons", "showDefaultTimeLabel", "showDefaultTitle", "size", "titleSelectedDatesFormatter", "value", "yearValues"],
  methods: ["confirm", "reset", "cancel"]
})], IonDatetime);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonDatetime, [{
    type: Component,
    args: [{
      selector: "ion-datetime",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["cancelText", "clearText", "color", "dayValues", "disabled", "doneText", "firstDayOfWeek", "formatOptions", "highlightedDates", "hourCycle", "hourValues", "isDateEnabled", "locale", "max", "min", "minuteValues", "mode", "monthValues", "multiple", "name", "preferWheel", "presentation", "readonly", "showAdjacentDays", "showClearButton", "showDefaultButtons", "showDefaultTimeLabel", "showDefaultTitle", "size", "titleSelectedDatesFormatter", "value", "yearValues"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a23;
var IonDatetimeButton = (_a23 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a23, "ɵfac", function IonDatetimeButton_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a23)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a23, "ɵcmp", ɵɵdefineComponent({
  type: _a23,
  selectors: [["ion-datetime-button"]],
  inputs: {
    color: "color",
    datetime: "datetime",
    disabled: "disabled",
    mode: "mode"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonDatetimeButton_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a23);
IonDatetimeButton = __decorate([ProxyCmp({
  inputs: ["color", "datetime", "disabled", "mode"]
})], IonDatetimeButton);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonDatetimeButton, [{
    type: Component,
    args: [{
      selector: "ion-datetime-button",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "datetime", "disabled", "mode"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a24;
var IonFab = (_a24 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a24, "ɵfac", function IonFab_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a24)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a24, "ɵcmp", ɵɵdefineComponent({
  type: _a24,
  selectors: [["ion-fab"]],
  inputs: {
    activated: "activated",
    edge: "edge",
    horizontal: "horizontal",
    vertical: "vertical"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonFab_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a24);
IonFab = __decorate([ProxyCmp({
  inputs: ["activated", "edge", "horizontal", "vertical"],
  methods: ["close"]
})], IonFab);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonFab, [{
    type: Component,
    args: [{
      selector: "ion-fab",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["activated", "edge", "horizontal", "vertical"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a25;
var IonFabButton = (_a25 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionFocus", "ionBlur"]);
  }
}, /** @nocollapse */
__publicField(_a25, "ɵfac", function IonFabButton_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a25)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a25, "ɵcmp", ɵɵdefineComponent({
  type: _a25,
  selectors: [["ion-fab-button"]],
  inputs: {
    activated: "activated",
    closeIcon: "closeIcon",
    color: "color",
    disabled: "disabled",
    download: "download",
    href: "href",
    mode: "mode",
    rel: "rel",
    routerAnimation: "routerAnimation",
    routerDirection: "routerDirection",
    show: "show",
    size: "size",
    target: "target",
    translucent: "translucent",
    type: "type"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonFabButton_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a25);
IonFabButton = __decorate([ProxyCmp({
  inputs: ["activated", "closeIcon", "color", "disabled", "download", "href", "mode", "rel", "routerAnimation", "routerDirection", "show", "size", "target", "translucent", "type"]
})], IonFabButton);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonFabButton, [{
    type: Component,
    args: [{
      selector: "ion-fab-button",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["activated", "closeIcon", "color", "disabled", "download", "href", "mode", "rel", "routerAnimation", "routerDirection", "show", "size", "target", "translucent", "type"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a26;
var IonFabList = (_a26 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a26, "ɵfac", function IonFabList_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a26)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a26, "ɵcmp", ɵɵdefineComponent({
  type: _a26,
  selectors: [["ion-fab-list"]],
  inputs: {
    activated: "activated",
    side: "side"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonFabList_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a26);
IonFabList = __decorate([ProxyCmp({
  inputs: ["activated", "side"]
})], IonFabList);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonFabList, [{
    type: Component,
    args: [{
      selector: "ion-fab-list",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["activated", "side"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a27;
var IonFooter = (_a27 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a27, "ɵfac", function IonFooter_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a27)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a27, "ɵcmp", ɵɵdefineComponent({
  type: _a27,
  selectors: [["ion-footer"]],
  inputs: {
    collapse: "collapse",
    mode: "mode",
    translucent: "translucent"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonFooter_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a27);
IonFooter = __decorate([ProxyCmp({
  inputs: ["collapse", "mode", "translucent"]
})], IonFooter);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonFooter, [{
    type: Component,
    args: [{
      selector: "ion-footer",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["collapse", "mode", "translucent"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a28;
var IonGrid = (_a28 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a28, "ɵfac", function IonGrid_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a28)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a28, "ɵcmp", ɵɵdefineComponent({
  type: _a28,
  selectors: [["ion-grid"]],
  inputs: {
    fixed: "fixed"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonGrid_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a28);
IonGrid = __decorate([ProxyCmp({
  inputs: ["fixed"]
})], IonGrid);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonGrid, [{
    type: Component,
    args: [{
      selector: "ion-grid",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["fixed"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a29;
var IonHeader = (_a29 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a29, "ɵfac", function IonHeader_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a29)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a29, "ɵcmp", ɵɵdefineComponent({
  type: _a29,
  selectors: [["ion-header"]],
  inputs: {
    collapse: "collapse",
    mode: "mode",
    translucent: "translucent"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonHeader_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a29);
IonHeader = __decorate([ProxyCmp({
  inputs: ["collapse", "mode", "translucent"]
})], IonHeader);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonHeader, [{
    type: Component,
    args: [{
      selector: "ion-header",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["collapse", "mode", "translucent"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a30;
var IonIcon = (_a30 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a30, "ɵfac", function IonIcon_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a30)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a30, "ɵcmp", ɵɵdefineComponent({
  type: _a30,
  selectors: [["ion-icon"]],
  inputs: {
    color: "color",
    flipRtl: "flipRtl",
    icon: "icon",
    ios: "ios",
    lazy: "lazy",
    md: "md",
    mode: "mode",
    name: "name",
    sanitize: "sanitize",
    size: "size",
    src: "src"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonIcon_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a30);
IonIcon = __decorate([ProxyCmp({
  inputs: ["color", "flipRtl", "icon", "ios", "lazy", "md", "mode", "name", "sanitize", "size", "src"]
})], IonIcon);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonIcon, [{
    type: Component,
    args: [{
      selector: "ion-icon",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "flipRtl", "icon", "ios", "lazy", "md", "mode", "name", "sanitize", "size", "src"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a31;
var IonImg = (_a31 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionImgWillLoad", "ionImgDidLoad", "ionError"]);
  }
}, /** @nocollapse */
__publicField(_a31, "ɵfac", function IonImg_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a31)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a31, "ɵcmp", ɵɵdefineComponent({
  type: _a31,
  selectors: [["ion-img"]],
  inputs: {
    alt: "alt",
    src: "src"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonImg_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a31);
IonImg = __decorate([ProxyCmp({
  inputs: ["alt", "src"]
})], IonImg);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonImg, [{
    type: Component,
    args: [{
      selector: "ion-img",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["alt", "src"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a32;
var IonInfiniteScroll = (_a32 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionInfinite"]);
  }
}, /** @nocollapse */
__publicField(_a32, "ɵfac", function IonInfiniteScroll_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a32)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a32, "ɵcmp", ɵɵdefineComponent({
  type: _a32,
  selectors: [["ion-infinite-scroll"]],
  inputs: {
    disabled: "disabled",
    position: "position",
    threshold: "threshold"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonInfiniteScroll_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a32);
IonInfiniteScroll = __decorate([ProxyCmp({
  inputs: ["disabled", "position", "threshold"],
  methods: ["complete"]
})], IonInfiniteScroll);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonInfiniteScroll, [{
    type: Component,
    args: [{
      selector: "ion-infinite-scroll",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["disabled", "position", "threshold"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a33;
var IonInfiniteScrollContent = (_a33 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a33, "ɵfac", function IonInfiniteScrollContent_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a33)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a33, "ɵcmp", ɵɵdefineComponent({
  type: _a33,
  selectors: [["ion-infinite-scroll-content"]],
  inputs: {
    loadingSpinner: "loadingSpinner",
    loadingText: "loadingText"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonInfiniteScrollContent_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a33);
IonInfiniteScrollContent = __decorate([ProxyCmp({
  inputs: ["loadingSpinner", "loadingText"]
})], IonInfiniteScrollContent);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonInfiniteScrollContent, [{
    type: Component,
    args: [{
      selector: "ion-infinite-scroll-content",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["loadingSpinner", "loadingText"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a34;
var IonInput = (_a34 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionInput", "ionChange", "ionBlur", "ionFocus"]);
  }
}, /** @nocollapse */
__publicField(_a34, "ɵfac", function IonInput_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a34)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a34, "ɵcmp", ɵɵdefineComponent({
  type: _a34,
  selectors: [["ion-input"]],
  inputs: {
    autocapitalize: "autocapitalize",
    autocomplete: "autocomplete",
    autocorrect: "autocorrect",
    autofocus: "autofocus",
    clearInput: "clearInput",
    clearInputIcon: "clearInputIcon",
    clearOnEdit: "clearOnEdit",
    color: "color",
    counter: "counter",
    counterFormatter: "counterFormatter",
    debounce: "debounce",
    disabled: "disabled",
    enterkeyhint: "enterkeyhint",
    errorText: "errorText",
    fill: "fill",
    helperText: "helperText",
    inputmode: "inputmode",
    label: "label",
    labelPlacement: "labelPlacement",
    max: "max",
    maxlength: "maxlength",
    min: "min",
    minlength: "minlength",
    mode: "mode",
    multiple: "multiple",
    name: "name",
    pattern: "pattern",
    placeholder: "placeholder",
    readonly: "readonly",
    required: "required",
    shape: "shape",
    spellcheck: "spellcheck",
    step: "step",
    type: "type",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonInput_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a34);
IonInput = __decorate([ProxyCmp({
  inputs: ["autocapitalize", "autocomplete", "autocorrect", "autofocus", "clearInput", "clearInputIcon", "clearOnEdit", "color", "counter", "counterFormatter", "debounce", "disabled", "enterkeyhint", "errorText", "fill", "helperText", "inputmode", "label", "labelPlacement", "max", "maxlength", "min", "minlength", "mode", "multiple", "name", "pattern", "placeholder", "readonly", "required", "shape", "spellcheck", "step", "type", "value"],
  methods: ["setFocus", "getInputElement"]
})], IonInput);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonInput, [{
    type: Component,
    args: [{
      selector: "ion-input",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["autocapitalize", "autocomplete", "autocorrect", "autofocus", "clearInput", "clearInputIcon", "clearOnEdit", "color", "counter", "counterFormatter", "debounce", "disabled", "enterkeyhint", "errorText", "fill", "helperText", "inputmode", "label", "labelPlacement", "max", "maxlength", "min", "minlength", "mode", "multiple", "name", "pattern", "placeholder", "readonly", "required", "shape", "spellcheck", "step", "type", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a35;
var IonInputOtp = (_a35 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionInput", "ionChange", "ionComplete", "ionBlur", "ionFocus"]);
  }
}, /** @nocollapse */
__publicField(_a35, "ɵfac", function IonInputOtp_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a35)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a35, "ɵcmp", ɵɵdefineComponent({
  type: _a35,
  selectors: [["ion-input-otp"]],
  inputs: {
    autocapitalize: "autocapitalize",
    color: "color",
    disabled: "disabled",
    fill: "fill",
    inputmode: "inputmode",
    length: "length",
    pattern: "pattern",
    readonly: "readonly",
    separators: "separators",
    shape: "shape",
    size: "size",
    type: "type",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonInputOtp_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a35);
IonInputOtp = __decorate([ProxyCmp({
  inputs: ["autocapitalize", "color", "disabled", "fill", "inputmode", "length", "pattern", "readonly", "separators", "shape", "size", "type", "value"],
  methods: ["setFocus"]
})], IonInputOtp);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonInputOtp, [{
    type: Component,
    args: [{
      selector: "ion-input-otp",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["autocapitalize", "color", "disabled", "fill", "inputmode", "length", "pattern", "readonly", "separators", "shape", "size", "type", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a36;
var IonInputPasswordToggle = (_a36 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a36, "ɵfac", function IonInputPasswordToggle_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a36)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a36, "ɵcmp", ɵɵdefineComponent({
  type: _a36,
  selectors: [["ion-input-password-toggle"]],
  inputs: {
    color: "color",
    hideIcon: "hideIcon",
    mode: "mode",
    showIcon: "showIcon"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonInputPasswordToggle_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a36);
IonInputPasswordToggle = __decorate([ProxyCmp({
  inputs: ["color", "hideIcon", "mode", "showIcon"]
})], IonInputPasswordToggle);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonInputPasswordToggle, [{
    type: Component,
    args: [{
      selector: "ion-input-password-toggle",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "hideIcon", "mode", "showIcon"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a37;
var IonItem = (_a37 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a37, "ɵfac", function IonItem_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a37)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a37, "ɵcmp", ɵɵdefineComponent({
  type: _a37,
  selectors: [["ion-item"]],
  inputs: {
    button: "button",
    color: "color",
    detail: "detail",
    detailIcon: "detailIcon",
    disabled: "disabled",
    download: "download",
    href: "href",
    lines: "lines",
    mode: "mode",
    rel: "rel",
    routerAnimation: "routerAnimation",
    routerDirection: "routerDirection",
    target: "target",
    type: "type"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonItem_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a37);
IonItem = __decorate([ProxyCmp({
  inputs: ["button", "color", "detail", "detailIcon", "disabled", "download", "href", "lines", "mode", "rel", "routerAnimation", "routerDirection", "target", "type"]
})], IonItem);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonItem, [{
    type: Component,
    args: [{
      selector: "ion-item",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["button", "color", "detail", "detailIcon", "disabled", "download", "href", "lines", "mode", "rel", "routerAnimation", "routerDirection", "target", "type"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a38;
var IonItemDivider = (_a38 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a38, "ɵfac", function IonItemDivider_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a38)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a38, "ɵcmp", ɵɵdefineComponent({
  type: _a38,
  selectors: [["ion-item-divider"]],
  inputs: {
    color: "color",
    mode: "mode",
    sticky: "sticky"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonItemDivider_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a38);
IonItemDivider = __decorate([ProxyCmp({
  inputs: ["color", "mode", "sticky"]
})], IonItemDivider);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonItemDivider, [{
    type: Component,
    args: [{
      selector: "ion-item-divider",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "mode", "sticky"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a39;
var IonItemGroup = (_a39 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a39, "ɵfac", function IonItemGroup_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a39)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a39, "ɵcmp", ɵɵdefineComponent({
  type: _a39,
  selectors: [["ion-item-group"]],
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonItemGroup_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a39);
IonItemGroup = __decorate([ProxyCmp({})], IonItemGroup);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonItemGroup, [{
    type: Component,
    args: [{
      selector: "ion-item-group",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: []
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a40;
var IonItemOption = (_a40 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a40, "ɵfac", function IonItemOption_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a40)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a40, "ɵcmp", ɵɵdefineComponent({
  type: _a40,
  selectors: [["ion-item-option"]],
  inputs: {
    color: "color",
    disabled: "disabled",
    download: "download",
    expandable: "expandable",
    href: "href",
    mode: "mode",
    rel: "rel",
    target: "target",
    type: "type"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonItemOption_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a40);
IonItemOption = __decorate([ProxyCmp({
  inputs: ["color", "disabled", "download", "expandable", "href", "mode", "rel", "target", "type"]
})], IonItemOption);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonItemOption, [{
    type: Component,
    args: [{
      selector: "ion-item-option",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "disabled", "download", "expandable", "href", "mode", "rel", "target", "type"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a41;
var IonItemOptions = (_a41 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionSwipe"]);
  }
}, /** @nocollapse */
__publicField(_a41, "ɵfac", function IonItemOptions_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a41)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a41, "ɵcmp", ɵɵdefineComponent({
  type: _a41,
  selectors: [["ion-item-options"]],
  inputs: {
    side: "side"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonItemOptions_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a41);
IonItemOptions = __decorate([ProxyCmp({
  inputs: ["side"]
})], IonItemOptions);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonItemOptions, [{
    type: Component,
    args: [{
      selector: "ion-item-options",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["side"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a42;
var IonItemSliding = (_a42 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionDrag"]);
  }
}, /** @nocollapse */
__publicField(_a42, "ɵfac", function IonItemSliding_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a42)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a42, "ɵcmp", ɵɵdefineComponent({
  type: _a42,
  selectors: [["ion-item-sliding"]],
  inputs: {
    disabled: "disabled"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonItemSliding_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a42);
IonItemSliding = __decorate([ProxyCmp({
  inputs: ["disabled"],
  methods: ["getOpenAmount", "getSlidingRatio", "open", "close", "closeOpened"]
})], IonItemSliding);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonItemSliding, [{
    type: Component,
    args: [{
      selector: "ion-item-sliding",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["disabled"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a43;
var IonLabel = (_a43 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a43, "ɵfac", function IonLabel_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a43)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a43, "ɵcmp", ɵɵdefineComponent({
  type: _a43,
  selectors: [["ion-label"]],
  inputs: {
    color: "color",
    mode: "mode",
    position: "position"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonLabel_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a43);
IonLabel = __decorate([ProxyCmp({
  inputs: ["color", "mode", "position"]
})], IonLabel);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonLabel, [{
    type: Component,
    args: [{
      selector: "ion-label",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "mode", "position"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a44;
var IonList = (_a44 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a44, "ɵfac", function IonList_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a44)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a44, "ɵcmp", ɵɵdefineComponent({
  type: _a44,
  selectors: [["ion-list"]],
  inputs: {
    inset: "inset",
    lines: "lines",
    mode: "mode"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonList_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a44);
IonList = __decorate([ProxyCmp({
  inputs: ["inset", "lines", "mode"],
  methods: ["closeSlidingItems"]
})], IonList);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonList, [{
    type: Component,
    args: [{
      selector: "ion-list",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["inset", "lines", "mode"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a45;
var IonListHeader = (_a45 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a45, "ɵfac", function IonListHeader_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a45)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a45, "ɵcmp", ɵɵdefineComponent({
  type: _a45,
  selectors: [["ion-list-header"]],
  inputs: {
    color: "color",
    lines: "lines",
    mode: "mode"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonListHeader_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a45);
IonListHeader = __decorate([ProxyCmp({
  inputs: ["color", "lines", "mode"]
})], IonListHeader);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonListHeader, [{
    type: Component,
    args: [{
      selector: "ion-list-header",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "lines", "mode"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a46;
var IonLoading = (_a46 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionLoadingDidPresent", "ionLoadingWillPresent", "ionLoadingWillDismiss", "ionLoadingDidDismiss", "didPresent", "willPresent", "willDismiss", "didDismiss"]);
  }
}, /** @nocollapse */
__publicField(_a46, "ɵfac", function IonLoading_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a46)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a46, "ɵcmp", ɵɵdefineComponent({
  type: _a46,
  selectors: [["ion-loading"]],
  inputs: {
    animated: "animated",
    backdropDismiss: "backdropDismiss",
    cssClass: "cssClass",
    duration: "duration",
    enterAnimation: "enterAnimation",
    htmlAttributes: "htmlAttributes",
    isOpen: "isOpen",
    keyboardClose: "keyboardClose",
    leaveAnimation: "leaveAnimation",
    message: "message",
    mode: "mode",
    showBackdrop: "showBackdrop",
    spinner: "spinner",
    translucent: "translucent",
    trigger: "trigger"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonLoading_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a46);
IonLoading = __decorate([ProxyCmp({
  inputs: ["animated", "backdropDismiss", "cssClass", "duration", "enterAnimation", "htmlAttributes", "isOpen", "keyboardClose", "leaveAnimation", "message", "mode", "showBackdrop", "spinner", "translucent", "trigger"],
  methods: ["present", "dismiss", "onDidDismiss", "onWillDismiss"]
})], IonLoading);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonLoading, [{
    type: Component,
    args: [{
      selector: "ion-loading",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["animated", "backdropDismiss", "cssClass", "duration", "enterAnimation", "htmlAttributes", "isOpen", "keyboardClose", "leaveAnimation", "message", "mode", "showBackdrop", "spinner", "translucent", "trigger"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a47;
var IonMenu = (_a47 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionWillOpen", "ionWillClose", "ionDidOpen", "ionDidClose"]);
  }
}, /** @nocollapse */
__publicField(_a47, "ɵfac", function IonMenu_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a47)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a47, "ɵcmp", ɵɵdefineComponent({
  type: _a47,
  selectors: [["ion-menu"]],
  inputs: {
    contentId: "contentId",
    disabled: "disabled",
    maxEdgeStart: "maxEdgeStart",
    menuId: "menuId",
    side: "side",
    swipeGesture: "swipeGesture",
    type: "type"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonMenu_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a47);
IonMenu = __decorate([ProxyCmp({
  inputs: ["contentId", "disabled", "maxEdgeStart", "menuId", "side", "swipeGesture", "type"],
  methods: ["isOpen", "isActive", "open", "close", "toggle", "setOpen"]
})], IonMenu);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonMenu, [{
    type: Component,
    args: [{
      selector: "ion-menu",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["contentId", "disabled", "maxEdgeStart", "menuId", "side", "swipeGesture", "type"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a48;
var IonMenuButton = (_a48 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a48, "ɵfac", function IonMenuButton_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a48)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a48, "ɵcmp", ɵɵdefineComponent({
  type: _a48,
  selectors: [["ion-menu-button"]],
  inputs: {
    autoHide: "autoHide",
    color: "color",
    disabled: "disabled",
    menu: "menu",
    mode: "mode",
    type: "type"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonMenuButton_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a48);
IonMenuButton = __decorate([ProxyCmp({
  inputs: ["autoHide", "color", "disabled", "menu", "mode", "type"]
})], IonMenuButton);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonMenuButton, [{
    type: Component,
    args: [{
      selector: "ion-menu-button",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["autoHide", "color", "disabled", "menu", "mode", "type"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a49;
var IonMenuToggle = (_a49 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a49, "ɵfac", function IonMenuToggle_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a49)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a49, "ɵcmp", ɵɵdefineComponent({
  type: _a49,
  selectors: [["ion-menu-toggle"]],
  inputs: {
    autoHide: "autoHide",
    menu: "menu"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonMenuToggle_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a49);
IonMenuToggle = __decorate([ProxyCmp({
  inputs: ["autoHide", "menu"]
})], IonMenuToggle);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonMenuToggle, [{
    type: Component,
    args: [{
      selector: "ion-menu-toggle",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["autoHide", "menu"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a50;
var IonNavLink = (_a50 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a50, "ɵfac", function IonNavLink_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a50)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a50, "ɵcmp", ɵɵdefineComponent({
  type: _a50,
  selectors: [["ion-nav-link"]],
  inputs: {
    component: "component",
    componentProps: "componentProps",
    routerAnimation: "routerAnimation",
    routerDirection: "routerDirection"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonNavLink_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a50);
IonNavLink = __decorate([ProxyCmp({
  inputs: ["component", "componentProps", "routerAnimation", "routerDirection"]
})], IonNavLink);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonNavLink, [{
    type: Component,
    args: [{
      selector: "ion-nav-link",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["component", "componentProps", "routerAnimation", "routerDirection"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a51;
var IonNote = (_a51 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a51, "ɵfac", function IonNote_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a51)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a51, "ɵcmp", ɵɵdefineComponent({
  type: _a51,
  selectors: [["ion-note"]],
  inputs: {
    color: "color",
    mode: "mode"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonNote_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a51);
IonNote = __decorate([ProxyCmp({
  inputs: ["color", "mode"]
})], IonNote);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonNote, [{
    type: Component,
    args: [{
      selector: "ion-note",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "mode"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a52;
var IonPicker = (_a52 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a52, "ɵfac", function IonPicker_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a52)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a52, "ɵcmp", ɵɵdefineComponent({
  type: _a52,
  selectors: [["ion-picker"]],
  inputs: {
    mode: "mode"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonPicker_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a52);
IonPicker = __decorate([ProxyCmp({
  inputs: ["mode"]
})], IonPicker);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonPicker, [{
    type: Component,
    args: [{
      selector: "ion-picker",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["mode"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a53;
var IonPickerColumn = (_a53 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionChange"]);
  }
}, /** @nocollapse */
__publicField(_a53, "ɵfac", function IonPickerColumn_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a53)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a53, "ɵcmp", ɵɵdefineComponent({
  type: _a53,
  selectors: [["ion-picker-column"]],
  inputs: {
    color: "color",
    disabled: "disabled",
    mode: "mode",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonPickerColumn_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a53);
IonPickerColumn = __decorate([ProxyCmp({
  inputs: ["color", "disabled", "mode", "value"],
  methods: ["setFocus"]
})], IonPickerColumn);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonPickerColumn, [{
    type: Component,
    args: [{
      selector: "ion-picker-column",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "disabled", "mode", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a54;
var IonPickerColumnOption = (_a54 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a54, "ɵfac", function IonPickerColumnOption_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a54)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a54, "ɵcmp", ɵɵdefineComponent({
  type: _a54,
  selectors: [["ion-picker-column-option"]],
  inputs: {
    color: "color",
    disabled: "disabled",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonPickerColumnOption_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a54);
IonPickerColumnOption = __decorate([ProxyCmp({
  inputs: ["color", "disabled", "value"]
})], IonPickerColumnOption);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonPickerColumnOption, [{
    type: Component,
    args: [{
      selector: "ion-picker-column-option",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "disabled", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a55;
var IonPickerLegacy = (_a55 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionPickerDidPresent", "ionPickerWillPresent", "ionPickerWillDismiss", "ionPickerDidDismiss", "didPresent", "willPresent", "willDismiss", "didDismiss"]);
  }
}, /** @nocollapse */
__publicField(_a55, "ɵfac", function IonPickerLegacy_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a55)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a55, "ɵcmp", ɵɵdefineComponent({
  type: _a55,
  selectors: [["ion-picker-legacy"]],
  inputs: {
    animated: "animated",
    backdropDismiss: "backdropDismiss",
    buttons: "buttons",
    columns: "columns",
    cssClass: "cssClass",
    duration: "duration",
    enterAnimation: "enterAnimation",
    htmlAttributes: "htmlAttributes",
    isOpen: "isOpen",
    keyboardClose: "keyboardClose",
    leaveAnimation: "leaveAnimation",
    mode: "mode",
    showBackdrop: "showBackdrop",
    trigger: "trigger"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonPickerLegacy_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a55);
IonPickerLegacy = __decorate([ProxyCmp({
  inputs: ["animated", "backdropDismiss", "buttons", "columns", "cssClass", "duration", "enterAnimation", "htmlAttributes", "isOpen", "keyboardClose", "leaveAnimation", "mode", "showBackdrop", "trigger"],
  methods: ["present", "dismiss", "onDidDismiss", "onWillDismiss", "getColumn"]
})], IonPickerLegacy);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonPickerLegacy, [{
    type: Component,
    args: [{
      selector: "ion-picker-legacy",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["animated", "backdropDismiss", "buttons", "columns", "cssClass", "duration", "enterAnimation", "htmlAttributes", "isOpen", "keyboardClose", "leaveAnimation", "mode", "showBackdrop", "trigger"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a56;
var IonProgressBar = (_a56 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a56, "ɵfac", function IonProgressBar_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a56)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a56, "ɵcmp", ɵɵdefineComponent({
  type: _a56,
  selectors: [["ion-progress-bar"]],
  inputs: {
    buffer: "buffer",
    color: "color",
    mode: "mode",
    reversed: "reversed",
    type: "type",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonProgressBar_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a56);
IonProgressBar = __decorate([ProxyCmp({
  inputs: ["buffer", "color", "mode", "reversed", "type", "value"]
})], IonProgressBar);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonProgressBar, [{
    type: Component,
    args: [{
      selector: "ion-progress-bar",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["buffer", "color", "mode", "reversed", "type", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a57;
var IonRadio = (_a57 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionFocus", "ionBlur"]);
  }
}, /** @nocollapse */
__publicField(_a57, "ɵfac", function IonRadio_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a57)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a57, "ɵcmp", ɵɵdefineComponent({
  type: _a57,
  selectors: [["ion-radio"]],
  inputs: {
    alignment: "alignment",
    color: "color",
    disabled: "disabled",
    justify: "justify",
    labelPlacement: "labelPlacement",
    mode: "mode",
    name: "name",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonRadio_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a57);
IonRadio = __decorate([ProxyCmp({
  inputs: ["alignment", "color", "disabled", "justify", "labelPlacement", "mode", "name", "value"]
})], IonRadio);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonRadio, [{
    type: Component,
    args: [{
      selector: "ion-radio",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["alignment", "color", "disabled", "justify", "labelPlacement", "mode", "name", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a58;
var IonRadioGroup = (_a58 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionChange"]);
  }
}, /** @nocollapse */
__publicField(_a58, "ɵfac", function IonRadioGroup_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a58)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a58, "ɵcmp", ɵɵdefineComponent({
  type: _a58,
  selectors: [["ion-radio-group"]],
  inputs: {
    allowEmptySelection: "allowEmptySelection",
    compareWith: "compareWith",
    errorText: "errorText",
    helperText: "helperText",
    name: "name",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonRadioGroup_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a58);
IonRadioGroup = __decorate([ProxyCmp({
  inputs: ["allowEmptySelection", "compareWith", "errorText", "helperText", "name", "value"]
})], IonRadioGroup);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonRadioGroup, [{
    type: Component,
    args: [{
      selector: "ion-radio-group",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["allowEmptySelection", "compareWith", "errorText", "helperText", "name", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a59;
var IonRange = (_a59 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionChange", "ionInput", "ionFocus", "ionBlur", "ionKnobMoveStart", "ionKnobMoveEnd"]);
  }
}, /** @nocollapse */
__publicField(_a59, "ɵfac", function IonRange_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a59)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a59, "ɵcmp", ɵɵdefineComponent({
  type: _a59,
  selectors: [["ion-range"]],
  inputs: {
    activeBarStart: "activeBarStart",
    color: "color",
    debounce: "debounce",
    disabled: "disabled",
    dualKnobs: "dualKnobs",
    label: "label",
    labelPlacement: "labelPlacement",
    max: "max",
    min: "min",
    mode: "mode",
    name: "name",
    pin: "pin",
    pinFormatter: "pinFormatter",
    snaps: "snaps",
    step: "step",
    ticks: "ticks",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonRange_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a59);
IonRange = __decorate([ProxyCmp({
  inputs: ["activeBarStart", "color", "debounce", "disabled", "dualKnobs", "label", "labelPlacement", "max", "min", "mode", "name", "pin", "pinFormatter", "snaps", "step", "ticks", "value"]
})], IonRange);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonRange, [{
    type: Component,
    args: [{
      selector: "ion-range",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["activeBarStart", "color", "debounce", "disabled", "dualKnobs", "label", "labelPlacement", "max", "min", "mode", "name", "pin", "pinFormatter", "snaps", "step", "ticks", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a60;
var IonRefresher = (_a60 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionRefresh", "ionPull", "ionStart"]);
  }
}, /** @nocollapse */
__publicField(_a60, "ɵfac", function IonRefresher_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a60)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a60, "ɵcmp", ɵɵdefineComponent({
  type: _a60,
  selectors: [["ion-refresher"]],
  inputs: {
    closeDuration: "closeDuration",
    disabled: "disabled",
    mode: "mode",
    pullFactor: "pullFactor",
    pullMax: "pullMax",
    pullMin: "pullMin",
    snapbackDuration: "snapbackDuration"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonRefresher_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a60);
IonRefresher = __decorate([ProxyCmp({
  inputs: ["closeDuration", "disabled", "mode", "pullFactor", "pullMax", "pullMin", "snapbackDuration"],
  methods: ["complete", "cancel", "getProgress"]
})], IonRefresher);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonRefresher, [{
    type: Component,
    args: [{
      selector: "ion-refresher",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["closeDuration", "disabled", "mode", "pullFactor", "pullMax", "pullMin", "snapbackDuration"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a61;
var IonRefresherContent = (_a61 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a61, "ɵfac", function IonRefresherContent_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a61)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a61, "ɵcmp", ɵɵdefineComponent({
  type: _a61,
  selectors: [["ion-refresher-content"]],
  inputs: {
    pullingIcon: "pullingIcon",
    pullingText: "pullingText",
    refreshingSpinner: "refreshingSpinner",
    refreshingText: "refreshingText"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonRefresherContent_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a61);
IonRefresherContent = __decorate([ProxyCmp({
  inputs: ["pullingIcon", "pullingText", "refreshingSpinner", "refreshingText"]
})], IonRefresherContent);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonRefresherContent, [{
    type: Component,
    args: [{
      selector: "ion-refresher-content",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["pullingIcon", "pullingText", "refreshingSpinner", "refreshingText"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a62;
var IonReorder = (_a62 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a62, "ɵfac", function IonReorder_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a62)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a62, "ɵcmp", ɵɵdefineComponent({
  type: _a62,
  selectors: [["ion-reorder"]],
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonReorder_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a62);
IonReorder = __decorate([ProxyCmp({})], IonReorder);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonReorder, [{
    type: Component,
    args: [{
      selector: "ion-reorder",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: []
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a63;
var IonReorderGroup = (_a63 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionItemReorder"]);
  }
}, /** @nocollapse */
__publicField(_a63, "ɵfac", function IonReorderGroup_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a63)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a63, "ɵcmp", ɵɵdefineComponent({
  type: _a63,
  selectors: [["ion-reorder-group"]],
  inputs: {
    disabled: "disabled"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonReorderGroup_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a63);
IonReorderGroup = __decorate([ProxyCmp({
  inputs: ["disabled"],
  methods: ["complete"]
})], IonReorderGroup);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonReorderGroup, [{
    type: Component,
    args: [{
      selector: "ion-reorder-group",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["disabled"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a64;
var IonRippleEffect = (_a64 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a64, "ɵfac", function IonRippleEffect_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a64)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a64, "ɵcmp", ɵɵdefineComponent({
  type: _a64,
  selectors: [["ion-ripple-effect"]],
  inputs: {
    type: "type"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonRippleEffect_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a64);
IonRippleEffect = __decorate([ProxyCmp({
  inputs: ["type"],
  methods: ["addRipple"]
})], IonRippleEffect);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonRippleEffect, [{
    type: Component,
    args: [{
      selector: "ion-ripple-effect",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["type"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a65;
var IonRow = (_a65 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a65, "ɵfac", function IonRow_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a65)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a65, "ɵcmp", ɵɵdefineComponent({
  type: _a65,
  selectors: [["ion-row"]],
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonRow_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a65);
IonRow = __decorate([ProxyCmp({})], IonRow);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonRow, [{
    type: Component,
    args: [{
      selector: "ion-row",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: []
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a66;
var IonSearchbar = (_a66 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionInput", "ionChange", "ionCancel", "ionClear", "ionBlur", "ionFocus"]);
  }
}, /** @nocollapse */
__publicField(_a66, "ɵfac", function IonSearchbar_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a66)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a66, "ɵcmp", ɵɵdefineComponent({
  type: _a66,
  selectors: [["ion-searchbar"]],
  inputs: {
    animated: "animated",
    autocapitalize: "autocapitalize",
    autocomplete: "autocomplete",
    autocorrect: "autocorrect",
    cancelButtonIcon: "cancelButtonIcon",
    cancelButtonText: "cancelButtonText",
    clearIcon: "clearIcon",
    color: "color",
    debounce: "debounce",
    disabled: "disabled",
    enterkeyhint: "enterkeyhint",
    inputmode: "inputmode",
    maxlength: "maxlength",
    minlength: "minlength",
    mode: "mode",
    name: "name",
    placeholder: "placeholder",
    searchIcon: "searchIcon",
    showCancelButton: "showCancelButton",
    showClearButton: "showClearButton",
    spellcheck: "spellcheck",
    type: "type",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonSearchbar_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a66);
IonSearchbar = __decorate([ProxyCmp({
  inputs: ["animated", "autocapitalize", "autocomplete", "autocorrect", "cancelButtonIcon", "cancelButtonText", "clearIcon", "color", "debounce", "disabled", "enterkeyhint", "inputmode", "maxlength", "minlength", "mode", "name", "placeholder", "searchIcon", "showCancelButton", "showClearButton", "spellcheck", "type", "value"],
  methods: ["setFocus", "getInputElement"]
})], IonSearchbar);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonSearchbar, [{
    type: Component,
    args: [{
      selector: "ion-searchbar",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["animated", "autocapitalize", "autocomplete", "autocorrect", "cancelButtonIcon", "cancelButtonText", "clearIcon", "color", "debounce", "disabled", "enterkeyhint", "inputmode", "maxlength", "minlength", "mode", "name", "placeholder", "searchIcon", "showCancelButton", "showClearButton", "spellcheck", "type", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a67;
var IonSegment = (_a67 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionChange"]);
  }
}, /** @nocollapse */
__publicField(_a67, "ɵfac", function IonSegment_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a67)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a67, "ɵcmp", ɵɵdefineComponent({
  type: _a67,
  selectors: [["ion-segment"]],
  inputs: {
    color: "color",
    disabled: "disabled",
    mode: "mode",
    scrollable: "scrollable",
    selectOnFocus: "selectOnFocus",
    swipeGesture: "swipeGesture",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonSegment_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a67);
IonSegment = __decorate([ProxyCmp({
  inputs: ["color", "disabled", "mode", "scrollable", "selectOnFocus", "swipeGesture", "value"]
})], IonSegment);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonSegment, [{
    type: Component,
    args: [{
      selector: "ion-segment",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "disabled", "mode", "scrollable", "selectOnFocus", "swipeGesture", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a68;
var IonSegmentButton = (_a68 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a68, "ɵfac", function IonSegmentButton_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a68)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a68, "ɵcmp", ɵɵdefineComponent({
  type: _a68,
  selectors: [["ion-segment-button"]],
  inputs: {
    contentId: "contentId",
    disabled: "disabled",
    layout: "layout",
    mode: "mode",
    type: "type",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonSegmentButton_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a68);
IonSegmentButton = __decorate([ProxyCmp({
  inputs: ["contentId", "disabled", "layout", "mode", "type", "value"]
})], IonSegmentButton);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonSegmentButton, [{
    type: Component,
    args: [{
      selector: "ion-segment-button",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["contentId", "disabled", "layout", "mode", "type", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a69;
var IonSegmentContent = (_a69 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a69, "ɵfac", function IonSegmentContent_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a69)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a69, "ɵcmp", ɵɵdefineComponent({
  type: _a69,
  selectors: [["ion-segment-content"]],
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonSegmentContent_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a69);
IonSegmentContent = __decorate([ProxyCmp({})], IonSegmentContent);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonSegmentContent, [{
    type: Component,
    args: [{
      selector: "ion-segment-content",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: []
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a70;
var IonSegmentView = (_a70 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionSegmentViewScroll"]);
  }
}, /** @nocollapse */
__publicField(_a70, "ɵfac", function IonSegmentView_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a70)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a70, "ɵcmp", ɵɵdefineComponent({
  type: _a70,
  selectors: [["ion-segment-view"]],
  inputs: {
    disabled: "disabled"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonSegmentView_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a70);
IonSegmentView = __decorate([ProxyCmp({
  inputs: ["disabled"]
})], IonSegmentView);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonSegmentView, [{
    type: Component,
    args: [{
      selector: "ion-segment-view",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["disabled"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a71;
var IonSelect = (_a71 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionChange", "ionCancel", "ionDismiss", "ionFocus", "ionBlur"]);
  }
}, /** @nocollapse */
__publicField(_a71, "ɵfac", function IonSelect_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a71)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a71, "ɵcmp", ɵɵdefineComponent({
  type: _a71,
  selectors: [["ion-select"]],
  inputs: {
    cancelText: "cancelText",
    color: "color",
    compareWith: "compareWith",
    disabled: "disabled",
    errorText: "errorText",
    expandedIcon: "expandedIcon",
    fill: "fill",
    helperText: "helperText",
    interface: "interface",
    interfaceOptions: "interfaceOptions",
    justify: "justify",
    label: "label",
    labelPlacement: "labelPlacement",
    mode: "mode",
    multiple: "multiple",
    name: "name",
    okText: "okText",
    placeholder: "placeholder",
    required: "required",
    selectedText: "selectedText",
    shape: "shape",
    toggleIcon: "toggleIcon",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonSelect_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a71);
IonSelect = __decorate([ProxyCmp({
  inputs: ["cancelText", "color", "compareWith", "disabled", "errorText", "expandedIcon", "fill", "helperText", "interface", "interfaceOptions", "justify", "label", "labelPlacement", "mode", "multiple", "name", "okText", "placeholder", "required", "selectedText", "shape", "toggleIcon", "value"],
  methods: ["open"]
})], IonSelect);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonSelect, [{
    type: Component,
    args: [{
      selector: "ion-select",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["cancelText", "color", "compareWith", "disabled", "errorText", "expandedIcon", "fill", "helperText", "interface", "interfaceOptions", "justify", "label", "labelPlacement", "mode", "multiple", "name", "okText", "placeholder", "required", "selectedText", "shape", "toggleIcon", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a72;
var IonSelectModal = (_a72 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a72, "ɵfac", function IonSelectModal_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a72)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a72, "ɵcmp", ɵɵdefineComponent({
  type: _a72,
  selectors: [["ion-select-modal"]],
  inputs: {
    header: "header",
    multiple: "multiple",
    options: "options"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonSelectModal_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a72);
IonSelectModal = __decorate([ProxyCmp({
  inputs: ["header", "multiple", "options"]
})], IonSelectModal);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonSelectModal, [{
    type: Component,
    args: [{
      selector: "ion-select-modal",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["header", "multiple", "options"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a73;
var IonSelectOption = (_a73 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a73, "ɵfac", function IonSelectOption_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a73)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a73, "ɵcmp", ɵɵdefineComponent({
  type: _a73,
  selectors: [["ion-select-option"]],
  inputs: {
    disabled: "disabled",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonSelectOption_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a73);
IonSelectOption = __decorate([ProxyCmp({
  inputs: ["disabled", "value"]
})], IonSelectOption);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonSelectOption, [{
    type: Component,
    args: [{
      selector: "ion-select-option",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["disabled", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a74;
var IonSkeletonText = (_a74 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a74, "ɵfac", function IonSkeletonText_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a74)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a74, "ɵcmp", ɵɵdefineComponent({
  type: _a74,
  selectors: [["ion-skeleton-text"]],
  inputs: {
    animated: "animated"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonSkeletonText_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a74);
IonSkeletonText = __decorate([ProxyCmp({
  inputs: ["animated"]
})], IonSkeletonText);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonSkeletonText, [{
    type: Component,
    args: [{
      selector: "ion-skeleton-text",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["animated"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a75;
var IonSpinner = (_a75 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a75, "ɵfac", function IonSpinner_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a75)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a75, "ɵcmp", ɵɵdefineComponent({
  type: _a75,
  selectors: [["ion-spinner"]],
  inputs: {
    color: "color",
    duration: "duration",
    name: "name",
    paused: "paused"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonSpinner_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a75);
IonSpinner = __decorate([ProxyCmp({
  inputs: ["color", "duration", "name", "paused"]
})], IonSpinner);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonSpinner, [{
    type: Component,
    args: [{
      selector: "ion-spinner",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "duration", "name", "paused"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a76;
var IonSplitPane = (_a76 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionSplitPaneVisible"]);
  }
}, /** @nocollapse */
__publicField(_a76, "ɵfac", function IonSplitPane_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a76)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a76, "ɵcmp", ɵɵdefineComponent({
  type: _a76,
  selectors: [["ion-split-pane"]],
  inputs: {
    contentId: "contentId",
    disabled: "disabled",
    when: "when"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonSplitPane_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a76);
IonSplitPane = __decorate([ProxyCmp({
  inputs: ["contentId", "disabled", "when"]
})], IonSplitPane);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonSplitPane, [{
    type: Component,
    args: [{
      selector: "ion-split-pane",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["contentId", "disabled", "when"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a77;
var IonTab = (_a77 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a77, "ɵfac", function IonTab_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a77)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a77, "ɵcmp", ɵɵdefineComponent({
  type: _a77,
  selectors: [["ion-tab"]],
  inputs: {
    component: "component",
    tab: "tab"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonTab_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a77);
IonTab = __decorate([ProxyCmp({
  inputs: ["component", "tab"],
  methods: ["setActive"]
})], IonTab);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonTab, [{
    type: Component,
    args: [{
      selector: "ion-tab",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["component", "tab"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a78;
var IonTabBar = (_a78 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a78, "ɵfac", function IonTabBar_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a78)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a78, "ɵcmp", ɵɵdefineComponent({
  type: _a78,
  selectors: [["ion-tab-bar"]],
  inputs: {
    color: "color",
    mode: "mode",
    selectedTab: "selectedTab",
    translucent: "translucent"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonTabBar_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a78);
IonTabBar = __decorate([ProxyCmp({
  inputs: ["color", "mode", "selectedTab", "translucent"]
})], IonTabBar);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonTabBar, [{
    type: Component,
    args: [{
      selector: "ion-tab-bar",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "mode", "selectedTab", "translucent"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a79;
var IonTabButton = (_a79 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a79, "ɵfac", function IonTabButton_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a79)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a79, "ɵcmp", ɵɵdefineComponent({
  type: _a79,
  selectors: [["ion-tab-button"]],
  inputs: {
    disabled: "disabled",
    download: "download",
    href: "href",
    layout: "layout",
    mode: "mode",
    rel: "rel",
    selected: "selected",
    tab: "tab",
    target: "target"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonTabButton_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a79);
IonTabButton = __decorate([ProxyCmp({
  inputs: ["disabled", "download", "href", "layout", "mode", "rel", "selected", "tab", "target"]
})], IonTabButton);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonTabButton, [{
    type: Component,
    args: [{
      selector: "ion-tab-button",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["disabled", "download", "href", "layout", "mode", "rel", "selected", "tab", "target"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a80;
var IonText = (_a80 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a80, "ɵfac", function IonText_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a80)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a80, "ɵcmp", ɵɵdefineComponent({
  type: _a80,
  selectors: [["ion-text"]],
  inputs: {
    color: "color",
    mode: "mode"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonText_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a80);
IonText = __decorate([ProxyCmp({
  inputs: ["color", "mode"]
})], IonText);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonText, [{
    type: Component,
    args: [{
      selector: "ion-text",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "mode"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a81;
var IonTextarea = (_a81 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionChange", "ionInput", "ionBlur", "ionFocus"]);
  }
}, /** @nocollapse */
__publicField(_a81, "ɵfac", function IonTextarea_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a81)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a81, "ɵcmp", ɵɵdefineComponent({
  type: _a81,
  selectors: [["ion-textarea"]],
  inputs: {
    autoGrow: "autoGrow",
    autocapitalize: "autocapitalize",
    autofocus: "autofocus",
    clearOnEdit: "clearOnEdit",
    color: "color",
    cols: "cols",
    counter: "counter",
    counterFormatter: "counterFormatter",
    debounce: "debounce",
    disabled: "disabled",
    enterkeyhint: "enterkeyhint",
    errorText: "errorText",
    fill: "fill",
    helperText: "helperText",
    inputmode: "inputmode",
    label: "label",
    labelPlacement: "labelPlacement",
    maxlength: "maxlength",
    minlength: "minlength",
    mode: "mode",
    name: "name",
    placeholder: "placeholder",
    readonly: "readonly",
    required: "required",
    rows: "rows",
    shape: "shape",
    spellcheck: "spellcheck",
    value: "value",
    wrap: "wrap"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonTextarea_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a81);
IonTextarea = __decorate([ProxyCmp({
  inputs: ["autoGrow", "autocapitalize", "autofocus", "clearOnEdit", "color", "cols", "counter", "counterFormatter", "debounce", "disabled", "enterkeyhint", "errorText", "fill", "helperText", "inputmode", "label", "labelPlacement", "maxlength", "minlength", "mode", "name", "placeholder", "readonly", "required", "rows", "shape", "spellcheck", "value", "wrap"],
  methods: ["setFocus", "getInputElement"]
})], IonTextarea);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonTextarea, [{
    type: Component,
    args: [{
      selector: "ion-textarea",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["autoGrow", "autocapitalize", "autofocus", "clearOnEdit", "color", "cols", "counter", "counterFormatter", "debounce", "disabled", "enterkeyhint", "errorText", "fill", "helperText", "inputmode", "label", "labelPlacement", "maxlength", "minlength", "mode", "name", "placeholder", "readonly", "required", "rows", "shape", "spellcheck", "value", "wrap"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a82;
var IonThumbnail = (_a82 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a82, "ɵfac", function IonThumbnail_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a82)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a82, "ɵcmp", ɵɵdefineComponent({
  type: _a82,
  selectors: [["ion-thumbnail"]],
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonThumbnail_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a82);
IonThumbnail = __decorate([ProxyCmp({})], IonThumbnail);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonThumbnail, [{
    type: Component,
    args: [{
      selector: "ion-thumbnail",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: []
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a83;
var IonTitle = (_a83 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a83, "ɵfac", function IonTitle_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a83)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a83, "ɵcmp", ɵɵdefineComponent({
  type: _a83,
  selectors: [["ion-title"]],
  inputs: {
    color: "color",
    size: "size"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonTitle_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a83);
IonTitle = __decorate([ProxyCmp({
  inputs: ["color", "size"]
})], IonTitle);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonTitle, [{
    type: Component,
    args: [{
      selector: "ion-title",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "size"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a84;
var IonToast = (_a84 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionToastDidPresent", "ionToastWillPresent", "ionToastWillDismiss", "ionToastDidDismiss", "didPresent", "willPresent", "willDismiss", "didDismiss"]);
  }
}, /** @nocollapse */
__publicField(_a84, "ɵfac", function IonToast_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a84)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a84, "ɵcmp", ɵɵdefineComponent({
  type: _a84,
  selectors: [["ion-toast"]],
  inputs: {
    animated: "animated",
    buttons: "buttons",
    color: "color",
    cssClass: "cssClass",
    duration: "duration",
    enterAnimation: "enterAnimation",
    header: "header",
    htmlAttributes: "htmlAttributes",
    icon: "icon",
    isOpen: "isOpen",
    keyboardClose: "keyboardClose",
    layout: "layout",
    leaveAnimation: "leaveAnimation",
    message: "message",
    mode: "mode",
    position: "position",
    positionAnchor: "positionAnchor",
    swipeGesture: "swipeGesture",
    translucent: "translucent",
    trigger: "trigger"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonToast_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a84);
IonToast = __decorate([ProxyCmp({
  inputs: ["animated", "buttons", "color", "cssClass", "duration", "enterAnimation", "header", "htmlAttributes", "icon", "isOpen", "keyboardClose", "layout", "leaveAnimation", "message", "mode", "position", "positionAnchor", "swipeGesture", "translucent", "trigger"],
  methods: ["present", "dismiss", "onDidDismiss", "onWillDismiss"]
})], IonToast);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonToast, [{
    type: Component,
    args: [{
      selector: "ion-toast",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["animated", "buttons", "color", "cssClass", "duration", "enterAnimation", "header", "htmlAttributes", "icon", "isOpen", "keyboardClose", "layout", "leaveAnimation", "message", "mode", "position", "positionAnchor", "swipeGesture", "translucent", "trigger"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a85;
var IonToggle = (_a85 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
    proxyOutputs(this, this.el, ["ionChange", "ionFocus", "ionBlur"]);
  }
}, /** @nocollapse */
__publicField(_a85, "ɵfac", function IonToggle_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a85)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a85, "ɵcmp", ɵɵdefineComponent({
  type: _a85,
  selectors: [["ion-toggle"]],
  inputs: {
    alignment: "alignment",
    checked: "checked",
    color: "color",
    disabled: "disabled",
    enableOnOffLabels: "enableOnOffLabels",
    errorText: "errorText",
    helperText: "helperText",
    justify: "justify",
    labelPlacement: "labelPlacement",
    mode: "mode",
    name: "name",
    required: "required",
    value: "value"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonToggle_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a85);
IonToggle = __decorate([ProxyCmp({
  inputs: ["alignment", "checked", "color", "disabled", "enableOnOffLabels", "errorText", "helperText", "justify", "labelPlacement", "mode", "name", "required", "value"]
})], IonToggle);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonToggle, [{
    type: Component,
    args: [{
      selector: "ion-toggle",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["alignment", "checked", "color", "disabled", "enableOnOffLabels", "errorText", "helperText", "justify", "labelPlacement", "mode", "name", "required", "value"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _a86;
var IonToolbar = (_a86 = class {
  z;
  el;
  constructor(c, r, z) {
    this.z = z;
    c.detach();
    this.el = r.nativeElement;
  }
}, /** @nocollapse */
__publicField(_a86, "ɵfac", function IonToolbar_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _a86)(ɵɵdirectiveInject(ChangeDetectorRef), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone));
}), /** @nocollapse */
__publicField(_a86, "ɵcmp", ɵɵdefineComponent({
  type: _a86,
  selectors: [["ion-toolbar"]],
  inputs: {
    color: "color",
    mode: "mode"
  },
  standalone: false,
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonToolbar_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
})), _a86);
IonToolbar = __decorate([ProxyCmp({
  inputs: ["color", "mode"]
})], IonToolbar);
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonToolbar, [{
    type: Component,
    args: [{
      selector: "ion-toolbar",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: "<ng-content></ng-content>",
      // eslint-disable-next-line @angular-eslint/no-inputs-metadata-property
      inputs: ["color", "mode"]
    }]
  }], function() {
    return [{
      type: ChangeDetectorRef
    }, {
      type: ElementRef
    }, {
      type: NgZone
    }];
  }, null);
})();
var _IonRouterOutlet = class _IonRouterOutlet extends IonRouterOutlet {
  parentOutlet;
  /**
   * `static: true` must be set so the query results are resolved
   * before change detection runs. Otherwise, the view container
   * ref will be ion-router-outlet instead of ng-container, and
   * the first view will be added as a sibling of ion-router-outlet
   * instead of a child.
   */
  outletContent;
  /**
   * We need to pass in the correct instance of IonRouterOutlet
   * otherwise parentOutlet will be null in a nested outlet context.
   * This results in APIs such as NavController.pop not working
   * in nested outlets because the parent outlet cannot be found.
   */
  constructor(name, tabs, commonLocation, elementRef, router, zone, activatedRoute, parentOutlet) {
    super(name, tabs, commonLocation, elementRef, router, zone, activatedRoute, parentOutlet);
    this.parentOutlet = parentOutlet;
  }
};
/** @nocollapse */
__publicField(_IonRouterOutlet, "ɵfac", function IonRouterOutlet_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _IonRouterOutlet)(ɵɵinjectAttribute("name"), ɵɵinjectAttribute("tabs"), ɵɵdirectiveInject(Location), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(Router), ɵɵdirectiveInject(NgZone), ɵɵdirectiveInject(ActivatedRoute), ɵɵdirectiveInject(_IonRouterOutlet, 12));
});
/** @nocollapse */
__publicField(_IonRouterOutlet, "ɵcmp", ɵɵdefineComponent({
  type: _IonRouterOutlet,
  selectors: [["ion-router-outlet"]],
  viewQuery: function IonRouterOutlet_Query(rf, ctx) {
    if (rf & 1) {
      ɵɵviewQuery(_c1, 7, ViewContainerRef);
    }
    if (rf & 2) {
      let _t;
      ɵɵqueryRefresh(_t = ɵɵloadQuery()) && (ctx.outletContent = _t.first);
    }
  },
  standalone: false,
  features: [ɵɵInheritDefinitionFeature],
  ngContentSelectors: _c0,
  decls: 3,
  vars: 0,
  consts: [["outletContent", ""]],
  template: function IonRouterOutlet_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵelementContainerStart(0, null, 0);
      ɵɵprojection(2);
      ɵɵelementContainerEnd();
    }
  },
  encapsulation: 2
}));
var IonRouterOutlet2 = _IonRouterOutlet;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonRouterOutlet2, [{
    type: Component,
    args: [{
      selector: "ion-router-outlet",
      template: "<ng-container #outletContent><ng-content></ng-content></ng-container>"
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
      type: IonRouterOutlet2,
      decorators: [{
        type: SkipSelf
      }, {
        type: Optional
      }]
    }];
  }, {
    outletContent: [{
      type: ViewChild,
      args: ["outletContent", {
        read: ViewContainerRef,
        static: true
      }]
    }]
  });
})();
var _IonTabs = class _IonTabs extends IonTabs {
  outlet;
  tabBar;
  tabBars;
  tabs;
};
/** @nocollapse */
__publicField(_IonTabs, "ɵfac", /* @__PURE__ */ (() => {
  let ɵIonTabs_BaseFactory;
  return function IonTabs_Factory(__ngFactoryType__) {
    return (ɵIonTabs_BaseFactory || (ɵIonTabs_BaseFactory = ɵɵgetInheritedFactory(_IonTabs)))(__ngFactoryType__ || _IonTabs);
  };
})());
/** @nocollapse */
__publicField(_IonTabs, "ɵcmp", ɵɵdefineComponent({
  type: _IonTabs,
  selectors: [["ion-tabs"]],
  contentQueries: function IonTabs_ContentQueries(rf, ctx, dirIndex) {
    if (rf & 1) {
      ɵɵcontentQuery(dirIndex, IonTabBar, 5);
      ɵɵcontentQuery(dirIndex, IonTabBar, 4);
      ɵɵcontentQuery(dirIndex, IonTab, 4);
    }
    if (rf & 2) {
      let _t;
      ɵɵqueryRefresh(_t = ɵɵloadQuery()) && (ctx.tabBar = _t.first);
      ɵɵqueryRefresh(_t = ɵɵloadQuery()) && (ctx.tabBars = _t);
      ɵɵqueryRefresh(_t = ɵɵloadQuery()) && (ctx.tabs = _t);
    }
  },
  viewQuery: function IonTabs_Query(rf, ctx) {
    if (rf & 1) {
      ɵɵviewQuery(_c2, 5, IonRouterOutlet2);
    }
    if (rf & 2) {
      let _t;
      ɵɵqueryRefresh(_t = ɵɵloadQuery()) && (ctx.outlet = _t.first);
    }
  },
  standalone: false,
  features: [ɵɵInheritDefinitionFeature],
  ngContentSelectors: _c4,
  decls: 6,
  vars: 2,
  consts: [["tabsInner", ""], ["outlet", ""], [1, "tabs-inner"], ["tabs", "true", 3, "stackWillChange", "stackDidChange", 4, "ngIf"], [4, "ngIf"], ["tabs", "true", 3, "stackWillChange", "stackDidChange"]],
  template: function IonTabs_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef(_c3);
      ɵɵprojection(0);
      ɵɵelementStart(1, "div", 2, 0);
      ɵɵtemplate(3, IonTabs_ion_router_outlet_3_Template, 2, 0, "ion-router-outlet", 3)(4, IonTabs_ng_content_4_Template, 1, 0, "ng-content", 4);
      ɵɵelementEnd();
      ɵɵprojection(5, 1);
    }
    if (rf & 2) {
      ɵɵadvance(3);
      ɵɵproperty("ngIf", ctx.tabs.length === 0);
      ɵɵadvance();
      ɵɵproperty("ngIf", ctx.tabs.length > 0);
    }
  },
  dependencies: [NgIf, IonRouterOutlet2],
  styles: ["[_nghost-%COMP%]{display:flex;position:absolute;inset:0;flex-direction:column;width:100%;height:100%;contain:layout size style}.tabs-inner[_ngcontent-%COMP%]{position:relative;flex:1;contain:layout size style}"]
}));
var IonTabs2 = _IonTabs;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonTabs2, [{
    type: Component,
    args: [{
      selector: "ion-tabs",
      template: `
    <ng-content select="[slot=top]"></ng-content>
    <div class="tabs-inner" #tabsInner>
      <ion-router-outlet
        *ngIf="tabs.length === 0"
        #outlet
        tabs="true"
        (stackWillChange)="onStackWillChange($event)"
        (stackDidChange)="onStackDidChange($event)"
      ></ion-router-outlet>
      <ng-content *ngIf="tabs.length > 0" select="ion-tab"></ng-content>
    </div>
    <ng-content></ng-content>
  `,
      styles: [":host{display:flex;position:absolute;inset:0;flex-direction:column;width:100%;height:100%;contain:layout size style}.tabs-inner{position:relative;flex:1;contain:layout size style}\n"]
    }]
  }], null, {
    outlet: [{
      type: ViewChild,
      args: ["outlet", {
        read: IonRouterOutlet2,
        static: false
      }]
    }],
    tabBar: [{
      type: ContentChild,
      args: [IonTabBar, {
        static: false
      }]
    }],
    tabBars: [{
      type: ContentChildren,
      args: [IonTabBar]
    }],
    tabs: [{
      type: ContentChildren,
      args: [IonTab]
    }]
  });
})();
var _IonBackButton = class _IonBackButton extends IonBackButton {
  constructor(routerOutlet, navCtrl, config, r, z, c) {
    super(routerOutlet, navCtrl, config, r, z, c);
  }
};
/** @nocollapse */
__publicField(_IonBackButton, "ɵfac", function IonBackButton_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _IonBackButton)(ɵɵdirectiveInject(IonRouterOutlet2, 8), ɵɵdirectiveInject(NavController), ɵɵdirectiveInject(Config), ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(NgZone), ɵɵdirectiveInject(ChangeDetectorRef));
});
/** @nocollapse */
__publicField(_IonBackButton, "ɵcmp", ɵɵdefineComponent({
  type: _IonBackButton,
  selectors: [["ion-back-button"]],
  standalone: false,
  features: [ɵɵInheritDefinitionFeature],
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonBackButton_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
}));
var IonBackButton2 = _IonBackButton;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonBackButton2, [{
    type: Component,
    args: [{
      selector: "ion-back-button",
      template: "<ng-content></ng-content>",
      changeDetection: ChangeDetectionStrategy.OnPush
    }]
  }], function() {
    return [{
      type: IonRouterOutlet2,
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
  }, null);
})();
var _IonNav = class _IonNav extends IonNav {
  constructor(ref, environmentInjector, injector, angularDelegate, z, c) {
    super(ref, environmentInjector, injector, angularDelegate, z, c);
  }
};
/** @nocollapse */
__publicField(_IonNav, "ɵfac", function IonNav_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _IonNav)(ɵɵdirectiveInject(ElementRef), ɵɵdirectiveInject(EnvironmentInjector), ɵɵdirectiveInject(Injector), ɵɵdirectiveInject(AngularDelegate), ɵɵdirectiveInject(NgZone), ɵɵdirectiveInject(ChangeDetectorRef));
});
/** @nocollapse */
__publicField(_IonNav, "ɵcmp", ɵɵdefineComponent({
  type: _IonNav,
  selectors: [["ion-nav"]],
  standalone: false,
  features: [ɵɵInheritDefinitionFeature],
  ngContentSelectors: _c0,
  decls: 1,
  vars: 0,
  template: function IonNav_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵprojectionDef();
      ɵɵprojection(0);
    }
  },
  encapsulation: 2,
  changeDetection: 0
}));
var IonNav2 = _IonNav;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonNav2, [{
    type: Component,
    args: [{
      selector: "ion-nav",
      template: "<ng-content></ng-content>",
      changeDetection: ChangeDetectionStrategy.OnPush
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
var _RouterLinkDelegateDirective = class _RouterLinkDelegateDirective extends RouterLinkDelegateDirective {
};
/** @nocollapse */
__publicField(_RouterLinkDelegateDirective, "ɵfac", /* @__PURE__ */ (() => {
  let ɵRouterLinkDelegateDirective_BaseFactory;
  return function RouterLinkDelegateDirective_Factory(__ngFactoryType__) {
    return (ɵRouterLinkDelegateDirective_BaseFactory || (ɵRouterLinkDelegateDirective_BaseFactory = ɵɵgetInheritedFactory(_RouterLinkDelegateDirective)))(__ngFactoryType__ || _RouterLinkDelegateDirective);
  };
})());
/** @nocollapse */
__publicField(_RouterLinkDelegateDirective, "ɵdir", ɵɵdefineDirective({
  type: _RouterLinkDelegateDirective,
  selectors: [["", "routerLink", "", 5, "a", 5, "area"]],
  standalone: false,
  features: [ɵɵInheritDefinitionFeature]
}));
var RouterLinkDelegateDirective2 = _RouterLinkDelegateDirective;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(RouterLinkDelegateDirective2, [{
    type: Directive,
    args: [{
      selector: ":not(a):not(area)[routerLink]"
    }]
  }], null, null);
})();
var _RouterLinkWithHrefDelegateDirective = class _RouterLinkWithHrefDelegateDirective extends RouterLinkWithHrefDelegateDirective {
};
/** @nocollapse */
__publicField(_RouterLinkWithHrefDelegateDirective, "ɵfac", /* @__PURE__ */ (() => {
  let ɵRouterLinkWithHrefDelegateDirective_BaseFactory;
  return function RouterLinkWithHrefDelegateDirective_Factory(__ngFactoryType__) {
    return (ɵRouterLinkWithHrefDelegateDirective_BaseFactory || (ɵRouterLinkWithHrefDelegateDirective_BaseFactory = ɵɵgetInheritedFactory(_RouterLinkWithHrefDelegateDirective)))(__ngFactoryType__ || _RouterLinkWithHrefDelegateDirective);
  };
})());
/** @nocollapse */
__publicField(_RouterLinkWithHrefDelegateDirective, "ɵdir", ɵɵdefineDirective({
  type: _RouterLinkWithHrefDelegateDirective,
  selectors: [["a", "routerLink", ""], ["area", "routerLink", ""]],
  standalone: false,
  features: [ɵɵInheritDefinitionFeature]
}));
var RouterLinkWithHrefDelegateDirective2 = _RouterLinkWithHrefDelegateDirective;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(RouterLinkWithHrefDelegateDirective2, [{
    type: Directive,
    args: [{
      selector: "a[routerLink],area[routerLink]"
    }]
  }], null, null);
})();
var _IonModal = class _IonModal extends IonModal {
};
/** @nocollapse */
__publicField(_IonModal, "ɵfac", /* @__PURE__ */ (() => {
  let ɵIonModal_BaseFactory;
  return function IonModal_Factory(__ngFactoryType__) {
    return (ɵIonModal_BaseFactory || (ɵIonModal_BaseFactory = ɵɵgetInheritedFactory(_IonModal)))(__ngFactoryType__ || _IonModal);
  };
})());
/** @nocollapse */
__publicField(_IonModal, "ɵcmp", ɵɵdefineComponent({
  type: _IonModal,
  selectors: [["ion-modal"]],
  standalone: false,
  features: [ɵɵInheritDefinitionFeature],
  decls: 1,
  vars: 1,
  consts: [["class", "ion-delegate-host ion-page", 4, "ngIf"], [1, "ion-delegate-host", "ion-page"], [3, "ngTemplateOutlet"]],
  template: function IonModal_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵtemplate(0, IonModal_div_0_Template, 2, 1, "div", 0);
    }
    if (rf & 2) {
      ɵɵproperty("ngIf", ctx.isCmpOpen || ctx.keepContentsMounted);
    }
  },
  dependencies: [NgIf, NgTemplateOutlet],
  encapsulation: 2,
  changeDetection: 0
}));
var IonModal2 = _IonModal;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonModal2, [{
    type: Component,
    args: [{
      selector: "ion-modal",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: `<div class="ion-delegate-host ion-page" *ngIf="isCmpOpen || keepContentsMounted">
    <ng-container [ngTemplateOutlet]="template"></ng-container>
  </div>`
    }]
  }], null, null);
})();
var _IonPopover = class _IonPopover extends IonPopover {
};
/** @nocollapse */
__publicField(_IonPopover, "ɵfac", /* @__PURE__ */ (() => {
  let ɵIonPopover_BaseFactory;
  return function IonPopover_Factory(__ngFactoryType__) {
    return (ɵIonPopover_BaseFactory || (ɵIonPopover_BaseFactory = ɵɵgetInheritedFactory(_IonPopover)))(__ngFactoryType__ || _IonPopover);
  };
})());
/** @nocollapse */
__publicField(_IonPopover, "ɵcmp", ɵɵdefineComponent({
  type: _IonPopover,
  selectors: [["ion-popover"]],
  standalone: false,
  features: [ɵɵInheritDefinitionFeature],
  decls: 1,
  vars: 1,
  consts: [[3, "ngTemplateOutlet", 4, "ngIf"], [3, "ngTemplateOutlet"]],
  template: function IonPopover_Template(rf, ctx) {
    if (rf & 1) {
      ɵɵtemplate(0, IonPopover_ng_container_0_Template, 1, 1, "ng-container", 0);
    }
    if (rf & 2) {
      ɵɵproperty("ngIf", ctx.isCmpOpen || ctx.keepContentsMounted);
    }
  },
  dependencies: [NgIf, NgTemplateOutlet],
  encapsulation: 2,
  changeDetection: 0
}));
var IonPopover2 = _IonPopover;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonPopover2, [{
    type: Component,
    args: [{
      selector: "ion-popover",
      changeDetection: ChangeDetectionStrategy.OnPush,
      template: `<ng-container [ngTemplateOutlet]="template" *ngIf="isCmpOpen || keepContentsMounted"></ng-container>`
    }]
  }], null, null);
})();
var ION_MAX_VALIDATOR = {
  provide: NG_VALIDATORS,
  useExisting: forwardRef(() => IonMaxValidator),
  multi: true
};
var _IonMaxValidator = class _IonMaxValidator extends MaxValidator {
};
/** @nocollapse */
__publicField(_IonMaxValidator, "ɵfac", /* @__PURE__ */ (() => {
  let ɵIonMaxValidator_BaseFactory;
  return function IonMaxValidator_Factory(__ngFactoryType__) {
    return (ɵIonMaxValidator_BaseFactory || (ɵIonMaxValidator_BaseFactory = ɵɵgetInheritedFactory(_IonMaxValidator)))(__ngFactoryType__ || _IonMaxValidator);
  };
})());
/** @nocollapse */
__publicField(_IonMaxValidator, "ɵdir", ɵɵdefineDirective({
  type: _IonMaxValidator,
  selectors: [["ion-input", "type", "number", "max", "", "formControlName", ""], ["ion-input", "type", "number", "max", "", "formControl", ""], ["ion-input", "type", "number", "max", "", "ngModel", ""]],
  hostVars: 1,
  hostBindings: function IonMaxValidator_HostBindings(rf, ctx) {
    if (rf & 2) {
      ɵɵattribute("max", ctx._enabled ? ctx.max : null);
    }
  },
  standalone: false,
  features: [ɵɵProvidersFeature([ION_MAX_VALIDATOR]), ɵɵInheritDefinitionFeature]
}));
var IonMaxValidator = _IonMaxValidator;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonMaxValidator, [{
    type: Directive,
    args: [{
      selector: "ion-input[type=number][max][formControlName],ion-input[type=number][max][formControl],ion-input[type=number][max][ngModel]",
      providers: [ION_MAX_VALIDATOR],
      // eslint-disable-next-line @angular-eslint/no-host-metadata-property
      host: {
        "[attr.max]": "_enabled ? max : null"
      }
    }]
  }], null, null);
})();
var ION_MIN_VALIDATOR = {
  provide: NG_VALIDATORS,
  useExisting: forwardRef(() => IonMinValidator),
  multi: true
};
var _IonMinValidator = class _IonMinValidator extends MinValidator {
};
/** @nocollapse */
__publicField(_IonMinValidator, "ɵfac", /* @__PURE__ */ (() => {
  let ɵIonMinValidator_BaseFactory;
  return function IonMinValidator_Factory(__ngFactoryType__) {
    return (ɵIonMinValidator_BaseFactory || (ɵIonMinValidator_BaseFactory = ɵɵgetInheritedFactory(_IonMinValidator)))(__ngFactoryType__ || _IonMinValidator);
  };
})());
/** @nocollapse */
__publicField(_IonMinValidator, "ɵdir", ɵɵdefineDirective({
  type: _IonMinValidator,
  selectors: [["ion-input", "type", "number", "min", "", "formControlName", ""], ["ion-input", "type", "number", "min", "", "formControl", ""], ["ion-input", "type", "number", "min", "", "ngModel", ""]],
  hostVars: 1,
  hostBindings: function IonMinValidator_HostBindings(rf, ctx) {
    if (rf & 2) {
      ɵɵattribute("min", ctx._enabled ? ctx.min : null);
    }
  },
  standalone: false,
  features: [ɵɵProvidersFeature([ION_MIN_VALIDATOR]), ɵɵInheritDefinitionFeature]
}));
var IonMinValidator = _IonMinValidator;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonMinValidator, [{
    type: Directive,
    args: [{
      selector: "ion-input[type=number][min][formControlName],ion-input[type=number][min][formControl],ion-input[type=number][min][ngModel]",
      providers: [ION_MIN_VALIDATOR],
      // eslint-disable-next-line @angular-eslint/no-host-metadata-property
      host: {
        "[attr.min]": "_enabled ? min : null"
      }
    }]
  }], null, null);
})();
var _AlertController = class _AlertController extends OverlayBaseController {
  constructor() {
    super(alertController);
  }
};
/** @nocollapse */
__publicField(_AlertController, "ɵfac", function AlertController_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _AlertController)();
});
/** @nocollapse */
__publicField(_AlertController, "ɵprov", ɵɵdefineInjectable({
  token: _AlertController,
  factory: _AlertController.ɵfac,
  providedIn: "root"
}));
var AlertController = _AlertController;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(AlertController, [{
    type: Injectable,
    args: [{
      providedIn: "root"
    }]
  }], function() {
    return [];
  }, null);
})();
var _AnimationController = class _AnimationController {
  /**
   * Create a new animation
   */
  create(animationId) {
    return createAnimation(animationId);
  }
  /**
   * EXPERIMENTAL
   *
   * Given a progression and a cubic bezier function,
   * this utility returns the time value(s) at which the
   * cubic bezier reaches the given time progression.
   *
   * If the cubic bezier never reaches the progression
   * the result will be an empty array.
   *
   * This is most useful for switching between easing curves
   * when doing a gesture animation (i.e. going from linear easing
   * during a drag, to another easing when `progressEnd` is called)
   */
  easingTime(p0, p1, p2, p3, progression) {
    return getTimeGivenProgression(p0, p1, p2, p3, progression);
  }
};
/** @nocollapse */
__publicField(_AnimationController, "ɵfac", function AnimationController_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _AnimationController)();
});
/** @nocollapse */
__publicField(_AnimationController, "ɵprov", ɵɵdefineInjectable({
  token: _AnimationController,
  factory: _AnimationController.ɵfac,
  providedIn: "root"
}));
var AnimationController = _AnimationController;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(AnimationController, [{
    type: Injectable,
    args: [{
      providedIn: "root"
    }]
  }], null, null);
})();
var _ActionSheetController = class _ActionSheetController extends OverlayBaseController {
  constructor() {
    super(actionSheetController);
  }
};
/** @nocollapse */
__publicField(_ActionSheetController, "ɵfac", function ActionSheetController_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _ActionSheetController)();
});
/** @nocollapse */
__publicField(_ActionSheetController, "ɵprov", ɵɵdefineInjectable({
  token: _ActionSheetController,
  factory: _ActionSheetController.ɵfac,
  providedIn: "root"
}));
var ActionSheetController = _ActionSheetController;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(ActionSheetController, [{
    type: Injectable,
    args: [{
      providedIn: "root"
    }]
  }], function() {
    return [];
  }, null);
})();
var _GestureController = class _GestureController {
  zone;
  constructor(zone) {
    this.zone = zone;
  }
  /**
   * Create a new gesture
   */
  create(opts, runInsideAngularZone = false) {
    if (runInsideAngularZone) {
      Object.getOwnPropertyNames(opts).forEach((key) => {
        if (typeof opts[key] === "function") {
          const fn = opts[key];
          opts[key] = (...props) => this.zone.run(() => fn(...props));
        }
      });
    }
    return createGesture(opts);
  }
};
/** @nocollapse */
__publicField(_GestureController, "ɵfac", function GestureController_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _GestureController)(ɵɵinject(NgZone));
});
/** @nocollapse */
__publicField(_GestureController, "ɵprov", ɵɵdefineInjectable({
  token: _GestureController,
  factory: _GestureController.ɵfac,
  providedIn: "root"
}));
var GestureController = _GestureController;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(GestureController, [{
    type: Injectable,
    args: [{
      providedIn: "root"
    }]
  }], function() {
    return [{
      type: NgZone
    }];
  }, null);
})();
var _LoadingController = class _LoadingController extends OverlayBaseController {
  constructor() {
    super(loadingController);
  }
};
/** @nocollapse */
__publicField(_LoadingController, "ɵfac", function LoadingController_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _LoadingController)();
});
/** @nocollapse */
__publicField(_LoadingController, "ɵprov", ɵɵdefineInjectable({
  token: _LoadingController,
  factory: _LoadingController.ɵfac,
  providedIn: "root"
}));
var LoadingController = _LoadingController;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(LoadingController, [{
    type: Injectable,
    args: [{
      providedIn: "root"
    }]
  }], function() {
    return [];
  }, null);
})();
var _MenuController = class _MenuController extends MenuController {
  constructor() {
    super(menuController);
  }
};
/** @nocollapse */
__publicField(_MenuController, "ɵfac", function MenuController_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _MenuController)();
});
/** @nocollapse */
__publicField(_MenuController, "ɵprov", ɵɵdefineInjectable({
  token: _MenuController,
  factory: _MenuController.ɵfac,
  providedIn: "root"
}));
var MenuController2 = _MenuController;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(MenuController2, [{
    type: Injectable,
    args: [{
      providedIn: "root"
    }]
  }], function() {
    return [];
  }, null);
})();
var _ModalController = class _ModalController extends OverlayBaseController {
  angularDelegate = inject(AngularDelegate);
  injector = inject(Injector);
  environmentInjector = inject(EnvironmentInjector);
  constructor() {
    super(modalController);
  }
  create(opts) {
    return super.create(__spreadProps(__spreadValues({}, opts), {
      delegate: this.angularDelegate.create(this.environmentInjector, this.injector, "modal")
    }));
  }
};
/** @nocollapse */
__publicField(_ModalController, "ɵfac", function ModalController_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _ModalController)();
});
/** @nocollapse */
__publicField(_ModalController, "ɵprov", ɵɵdefineInjectable({
  token: _ModalController,
  factory: _ModalController.ɵfac
}));
var ModalController = _ModalController;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(ModalController, [{
    type: Injectable
  }], function() {
    return [];
  }, null);
})();
var _PickerController = class _PickerController extends OverlayBaseController {
  constructor() {
    super(pickerController);
  }
};
/** @nocollapse */
__publicField(_PickerController, "ɵfac", function PickerController_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _PickerController)();
});
/** @nocollapse */
__publicField(_PickerController, "ɵprov", ɵɵdefineInjectable({
  token: _PickerController,
  factory: _PickerController.ɵfac,
  providedIn: "root"
}));
var PickerController = _PickerController;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(PickerController, [{
    type: Injectable,
    args: [{
      providedIn: "root"
    }]
  }], function() {
    return [];
  }, null);
})();
var PopoverController = class extends OverlayBaseController {
  angularDelegate = inject(AngularDelegate);
  injector = inject(Injector);
  environmentInjector = inject(EnvironmentInjector);
  constructor() {
    super(popoverController);
  }
  create(opts) {
    return super.create(__spreadProps(__spreadValues({}, opts), {
      delegate: this.angularDelegate.create(this.environmentInjector, this.injector, "popover")
    }));
  }
};
var _ToastController = class _ToastController extends OverlayBaseController {
  constructor() {
    super(toastController);
  }
};
/** @nocollapse */
__publicField(_ToastController, "ɵfac", function ToastController_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _ToastController)();
});
/** @nocollapse */
__publicField(_ToastController, "ɵprov", ɵɵdefineInjectable({
  token: _ToastController,
  factory: _ToastController.ɵfac,
  providedIn: "root"
}));
var ToastController = _ToastController;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(ToastController, [{
    type: Injectable,
    args: [{
      providedIn: "root"
    }]
  }], function() {
    return [];
  }, null);
})();
var appInitialize = (config, doc, zone) => {
  return () => {
    const win = doc.defaultView;
    if (win && typeof window !== "undefined") {
      setupConfig(__spreadProps(__spreadValues({}, config), {
        _zoneGate: (h) => zone.run(h)
      }));
      const aelFn = "__zone_symbol__addEventListener" in doc.body ? "__zone_symbol__addEventListener" : "addEventListener";
      return defineCustomElements(win, {
        exclude: ["ion-tabs"],
        syncQueue: true,
        raf,
        jmp: (h) => zone.runOutsideAngular(h),
        ael(elm, eventName, cb, opts) {
          elm[aelFn](eventName, cb, opts);
        },
        rel(elm, eventName, cb, opts) {
          elm.removeEventListener(eventName, cb, opts);
        }
      });
    }
  };
};
var DIRECTIVES = [IonAccordion, IonAccordionGroup, IonActionSheet, IonAlert, IonApp, IonAvatar, IonBackdrop, IonBadge, IonBreadcrumb, IonBreadcrumbs, IonButton, IonButtons, IonCard, IonCardContent, IonCardHeader, IonCardSubtitle, IonCardTitle, IonCheckbox, IonChip, IonCol, IonContent, IonDatetime, IonDatetimeButton, IonFab, IonFabButton, IonFabList, IonFooter, IonGrid, IonHeader, IonIcon, IonImg, IonInfiniteScroll, IonInfiniteScrollContent, IonInput, IonInputOtp, IonInputPasswordToggle, IonItem, IonItemDivider, IonItemGroup, IonItemOption, IonItemOptions, IonItemSliding, IonLabel, IonList, IonListHeader, IonLoading, IonMenu, IonMenuButton, IonMenuToggle, IonNavLink, IonNote, IonPicker, IonPickerColumn, IonPickerColumnOption, IonPickerLegacy, IonProgressBar, IonRadio, IonRadioGroup, IonRange, IonRefresher, IonRefresherContent, IonReorder, IonReorderGroup, IonRippleEffect, IonRow, IonSearchbar, IonSegment, IonSegmentButton, IonSegmentContent, IonSegmentView, IonSelect, IonSelectModal, IonSelectOption, IonSkeletonText, IonSpinner, IonSplitPane, IonTab, IonTabBar, IonTabButton, IonText, IonTextarea, IonThumbnail, IonTitle, IonToast, IonToggle, IonToolbar];
var DECLARATIONS = [
  // generated proxies
  ...DIRECTIVES,
  // manual proxies
  IonModal2,
  IonPopover2,
  // ngModel accessors
  BooleanValueAccessorDirective,
  NumericValueAccessorDirective,
  SelectValueAccessorDirective,
  TextValueAccessorDirective,
  // navigation
  IonTabs2,
  IonRouterOutlet2,
  IonBackButton2,
  IonNav2,
  RouterLinkDelegateDirective2,
  RouterLinkWithHrefDelegateDirective2,
  // validators
  IonMinValidator,
  IonMaxValidator
];
var _IonicModule = class _IonicModule {
  static forRoot(config = {}) {
    return {
      ngModule: _IonicModule,
      providers: [{
        provide: ConfigToken,
        useValue: config
      }, {
        provide: APP_INITIALIZER,
        useFactory: appInitialize,
        multi: true,
        deps: [ConfigToken, DOCUMENT, NgZone]
      }, AngularDelegate, provideComponentInputBinding()]
    };
  }
};
/** @nocollapse */
__publicField(_IonicModule, "ɵfac", function IonicModule_Factory(__ngFactoryType__) {
  return new (__ngFactoryType__ || _IonicModule)();
});
/** @nocollapse */
__publicField(_IonicModule, "ɵmod", ɵɵdefineNgModule({
  type: _IonicModule,
  declarations: [
    IonAccordion,
    IonAccordionGroup,
    IonActionSheet,
    IonAlert,
    IonApp,
    IonAvatar,
    IonBackdrop,
    IonBadge,
    IonBreadcrumb,
    IonBreadcrumbs,
    IonButton,
    IonButtons,
    IonCard,
    IonCardContent,
    IonCardHeader,
    IonCardSubtitle,
    IonCardTitle,
    IonCheckbox,
    IonChip,
    IonCol,
    IonContent,
    IonDatetime,
    IonDatetimeButton,
    IonFab,
    IonFabButton,
    IonFabList,
    IonFooter,
    IonGrid,
    IonHeader,
    IonIcon,
    IonImg,
    IonInfiniteScroll,
    IonInfiniteScrollContent,
    IonInput,
    IonInputOtp,
    IonInputPasswordToggle,
    IonItem,
    IonItemDivider,
    IonItemGroup,
    IonItemOption,
    IonItemOptions,
    IonItemSliding,
    IonLabel,
    IonList,
    IonListHeader,
    IonLoading,
    IonMenu,
    IonMenuButton,
    IonMenuToggle,
    IonNavLink,
    IonNote,
    IonPicker,
    IonPickerColumn,
    IonPickerColumnOption,
    IonPickerLegacy,
    IonProgressBar,
    IonRadio,
    IonRadioGroup,
    IonRange,
    IonRefresher,
    IonRefresherContent,
    IonReorder,
    IonReorderGroup,
    IonRippleEffect,
    IonRow,
    IonSearchbar,
    IonSegment,
    IonSegmentButton,
    IonSegmentContent,
    IonSegmentView,
    IonSelect,
    IonSelectModal,
    IonSelectOption,
    IonSkeletonText,
    IonSpinner,
    IonSplitPane,
    IonTab,
    IonTabBar,
    IonTabButton,
    IonText,
    IonTextarea,
    IonThumbnail,
    IonTitle,
    IonToast,
    IonToggle,
    IonToolbar,
    // manual proxies
    IonModal2,
    IonPopover2,
    // ngModel accessors
    BooleanValueAccessorDirective,
    NumericValueAccessorDirective,
    SelectValueAccessorDirective,
    TextValueAccessorDirective,
    // navigation
    IonTabs2,
    IonRouterOutlet2,
    IonBackButton2,
    IonNav2,
    RouterLinkDelegateDirective2,
    RouterLinkWithHrefDelegateDirective2,
    // validators
    IonMinValidator,
    IonMaxValidator
  ],
  imports: [CommonModule],
  exports: [
    IonAccordion,
    IonAccordionGroup,
    IonActionSheet,
    IonAlert,
    IonApp,
    IonAvatar,
    IonBackdrop,
    IonBadge,
    IonBreadcrumb,
    IonBreadcrumbs,
    IonButton,
    IonButtons,
    IonCard,
    IonCardContent,
    IonCardHeader,
    IonCardSubtitle,
    IonCardTitle,
    IonCheckbox,
    IonChip,
    IonCol,
    IonContent,
    IonDatetime,
    IonDatetimeButton,
    IonFab,
    IonFabButton,
    IonFabList,
    IonFooter,
    IonGrid,
    IonHeader,
    IonIcon,
    IonImg,
    IonInfiniteScroll,
    IonInfiniteScrollContent,
    IonInput,
    IonInputOtp,
    IonInputPasswordToggle,
    IonItem,
    IonItemDivider,
    IonItemGroup,
    IonItemOption,
    IonItemOptions,
    IonItemSliding,
    IonLabel,
    IonList,
    IonListHeader,
    IonLoading,
    IonMenu,
    IonMenuButton,
    IonMenuToggle,
    IonNavLink,
    IonNote,
    IonPicker,
    IonPickerColumn,
    IonPickerColumnOption,
    IonPickerLegacy,
    IonProgressBar,
    IonRadio,
    IonRadioGroup,
    IonRange,
    IonRefresher,
    IonRefresherContent,
    IonReorder,
    IonReorderGroup,
    IonRippleEffect,
    IonRow,
    IonSearchbar,
    IonSegment,
    IonSegmentButton,
    IonSegmentContent,
    IonSegmentView,
    IonSelect,
    IonSelectModal,
    IonSelectOption,
    IonSkeletonText,
    IonSpinner,
    IonSplitPane,
    IonTab,
    IonTabBar,
    IonTabButton,
    IonText,
    IonTextarea,
    IonThumbnail,
    IonTitle,
    IonToast,
    IonToggle,
    IonToolbar,
    // manual proxies
    IonModal2,
    IonPopover2,
    // ngModel accessors
    BooleanValueAccessorDirective,
    NumericValueAccessorDirective,
    SelectValueAccessorDirective,
    TextValueAccessorDirective,
    // navigation
    IonTabs2,
    IonRouterOutlet2,
    IonBackButton2,
    IonNav2,
    RouterLinkDelegateDirective2,
    RouterLinkWithHrefDelegateDirective2,
    // validators
    IonMinValidator,
    IonMaxValidator
  ]
}));
/** @nocollapse */
__publicField(_IonicModule, "ɵinj", ɵɵdefineInjector({
  providers: [ModalController, PopoverController],
  imports: [CommonModule]
}));
var IonicModule = _IonicModule;
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(IonicModule, [{
    type: NgModule,
    args: [{
      declarations: DECLARATIONS,
      exports: DECLARATIONS,
      providers: [ModalController, PopoverController],
      imports: [CommonModule]
    }]
  }], null, null);
})();
export {
  ActionSheetController,
  AlertController,
  AngularDelegate,
  AnimationController,
  BooleanValueAccessorDirective as BooleanValueAccessor,
  Config,
  DomController,
  GestureController,
  ION_MAX_VALIDATOR,
  ION_MIN_VALIDATOR,
  IonAccordion,
  IonAccordionGroup,
  IonActionSheet,
  IonAlert,
  IonApp,
  IonAvatar,
  IonBackButton2 as IonBackButton,
  IonBackdrop,
  IonBadge,
  IonBreadcrumb,
  IonBreadcrumbs,
  IonButton,
  IonButtons,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonCardSubtitle,
  IonCardTitle,
  IonCheckbox,
  IonChip,
  IonCol,
  IonContent,
  IonDatetime,
  IonDatetimeButton,
  IonFab,
  IonFabButton,
  IonFabList,
  IonFooter,
  IonGrid,
  IonHeader,
  IonIcon,
  IonImg,
  IonInfiniteScroll,
  IonInfiniteScrollContent,
  IonInput,
  IonInputOtp,
  IonInputPasswordToggle,
  IonItem,
  IonItemDivider,
  IonItemGroup,
  IonItemOption,
  IonItemOptions,
  IonItemSliding,
  IonLabel,
  IonList,
  IonListHeader,
  IonLoading,
  IonMaxValidator,
  IonMenu,
  IonMenuButton,
  IonMenuToggle,
  IonMinValidator,
  IonModal2 as IonModal,
  IonNav2 as IonNav,
  IonNavLink,
  IonNote,
  IonPicker,
  IonPickerColumn,
  IonPickerColumnOption,
  IonPickerLegacy,
  IonPopover2 as IonPopover,
  IonProgressBar,
  IonRadio,
  IonRadioGroup,
  IonRange,
  IonRefresher,
  IonRefresherContent,
  IonReorder,
  IonReorderGroup,
  IonRippleEffect,
  IonRouterOutlet2 as IonRouterOutlet,
  IonRow,
  IonSearchbar,
  IonSegment,
  IonSegmentButton,
  IonSegmentContent,
  IonSegmentView,
  IonSelect,
  IonSelectModal,
  IonSelectOption,
  IonSkeletonText,
  IonSpinner,
  IonSplitPane,
  IonTab,
  IonTabBar,
  IonTabButton,
  IonTabs2 as IonTabs,
  IonText,
  IonTextarea,
  IonThumbnail,
  IonTitle,
  IonToast,
  IonToggle,
  IonToolbar,
  IonicModule,
  IonicRouteStrategy,
  IonicSafeString,
  IonicSlides,
  LoadingController,
  MenuController2 as MenuController,
  ModalController,
  NavController,
  NavParams,
  NumericValueAccessorDirective as NumericValueAccessor,
  PickerController,
  Platform,
  PopoverController,
  RouterLinkDelegateDirective2 as RouterLinkDelegate,
  RouterLinkWithHrefDelegateDirective2 as RouterLinkWithHrefDelegate,
  SelectValueAccessorDirective as SelectValueAccessor,
  TextValueAccessorDirective as TextValueAccessor,
  ToastController,
  createAnimation,
  createGesture,
  getIonPageElement,
  getPlatforms,
  getTimeGivenProgression,
  iosTransitionAnimation,
  isPlatform,
  mdTransitionAnimation,
  openURL
};
/*! Bundled license information:

@ionic/core/dist/esm/index.js:
@ionic/core/dist/esm/loader.js:
@ionic/core/loader/index.js:
  (*!
   * (C) Ionic http://ionicframework.com - MIT License
   *)
*/
//# sourceMappingURL=@ionic_angular.js.map
