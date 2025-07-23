import {
  isRTL
} from "./chunk-JQCJAYTB.js";
import {
  checkmarkOutline,
  ellipseOutline,
  removeOutline
} from "./chunk-N4VCU7WS.js";
import {
  hapticSelection
} from "./chunk-OQILWA36.js";
import "./chunk-I7KKGUX3.js";
import {
  createColorClasses,
  hostContext
} from "./chunk-RC5HTYUZ.js";
import {
  inheritAriaAttributes,
  renderHiddenInput
} from "./chunk-UHAXTLK2.js";
import "./chunk-AM533ZC5.js";
import {
  Host,
  config,
  createEvent,
  getElement,
  getIonMode,
  h,
  isPlatform,
  registerInstance
} from "./chunk-G2JG4RXT.js";
import {
  __async
} from "./chunk-UQIXM5CJ.js";

// node_modules/@ionic/core/dist/esm/ion-toggle.entry.js
var toggleIosCss = ":host{-webkit-box-sizing:content-box !important;box-sizing:content-box !important;display:inline-block;position:relative;max-width:100%;cursor:pointer;-webkit-user-select:none;-moz-user-select:none;-ms-user-select:none;user-select:none;z-index:2}:host(.in-item){-ms-flex:1 1 0px;flex:1 1 0;width:100%;height:100%}:host([slot=start]),:host([slot=end]){-ms-flex:initial;flex:initial;width:auto}:host(.ion-focused) input{border:2px solid #5e9ed6}:host(.toggle-disabled){pointer-events:none}input{display:none}.toggle-wrapper{display:-ms-flexbox;display:flex;position:relative;-ms-flex-positive:1;flex-grow:1;-ms-flex-align:center;align-items:center;-ms-flex-pack:justify;justify-content:space-between;height:inherit;-webkit-transition:background-color 15ms linear;transition:background-color 15ms linear;cursor:inherit}.label-text-wrapper{text-overflow:ellipsis;white-space:nowrap;overflow:hidden}:host(.in-item) .label-text-wrapper{margin-top:10px;margin-bottom:10px}:host(.in-item.toggle-label-placement-stacked) .label-text-wrapper{margin-top:10px;margin-bottom:16px}:host(.in-item.toggle-label-placement-stacked) .native-wrapper{margin-bottom:10px}.label-text-wrapper-hidden{display:none}.native-wrapper{display:-ms-flexbox;display:flex;-ms-flex-align:center;align-items:center}.toggle-bottom{padding-top:4px;display:-ms-flexbox;display:flex;-ms-flex-pack:justify;justify-content:space-between;font-size:0.75rem;white-space:normal}:host(.toggle-label-placement-stacked) .toggle-bottom{font-size:1rem}.toggle-bottom .error-text{display:none;color:var(--ion-color-danger, #c5000f)}.toggle-bottom .helper-text{display:block;color:var(--ion-color-step-700, var(--ion-text-color-step-300, #4d4d4d))}:host(.ion-touched.ion-invalid) .toggle-bottom .error-text{display:block}:host(.ion-touched.ion-invalid) .toggle-bottom .helper-text{display:none}:host(.toggle-label-placement-start) .toggle-wrapper{-ms-flex-direction:row;flex-direction:row}:host(.toggle-label-placement-start) .label-text-wrapper{-webkit-margin-start:0;margin-inline-start:0;-webkit-margin-end:16px;margin-inline-end:16px}:host(.toggle-label-placement-end) .toggle-wrapper{-ms-flex-direction:row-reverse;flex-direction:row-reverse;-ms-flex-pack:start;justify-content:start}:host(.toggle-label-placement-end) .label-text-wrapper{-webkit-margin-start:16px;margin-inline-start:16px;-webkit-margin-end:0;margin-inline-end:0}:host(.toggle-label-placement-fixed) .label-text-wrapper{-webkit-margin-start:0;margin-inline-start:0;-webkit-margin-end:16px;margin-inline-end:16px}:host(.toggle-label-placement-fixed) .label-text-wrapper{-ms-flex:0 0 100px;flex:0 0 100px;width:100px;min-width:100px;max-width:200px}:host(.toggle-label-placement-stacked) .toggle-wrapper{-ms-flex-direction:column;flex-direction:column;text-align:center}:host(.toggle-label-placement-stacked) .label-text-wrapper{-webkit-transform:scale(0.75);transform:scale(0.75);margin-left:0;margin-right:0;margin-bottom:16px;max-width:calc(100% / 0.75)}:host(.toggle-label-placement-stacked.toggle-alignment-start) .label-text-wrapper{-webkit-transform-origin:left top;transform-origin:left top}:host-context([dir=rtl]):host(.toggle-label-placement-stacked.toggle-alignment-start) .label-text-wrapper,:host-context([dir=rtl]).toggle-label-placement-stacked.toggle-alignment-start .label-text-wrapper{-webkit-transform-origin:right top;transform-origin:right top}@supports selector(:dir(rtl)){:host(.toggle-label-placement-stacked.toggle-alignment-start:dir(rtl)) .label-text-wrapper{-webkit-transform-origin:right top;transform-origin:right top}}:host(.toggle-label-placement-stacked.toggle-alignment-center) .label-text-wrapper{-webkit-transform-origin:center top;transform-origin:center top}:host-context([dir=rtl]):host(.toggle-label-placement-stacked.toggle-alignment-center) .label-text-wrapper,:host-context([dir=rtl]).toggle-label-placement-stacked.toggle-alignment-center .label-text-wrapper{-webkit-transform-origin:calc(100% - center) top;transform-origin:calc(100% - center) top}@supports selector(:dir(rtl)){:host(.toggle-label-placement-stacked.toggle-alignment-center:dir(rtl)) .label-text-wrapper{-webkit-transform-origin:calc(100% - center) top;transform-origin:calc(100% - center) top}}:host(.toggle-justify-space-between) .toggle-wrapper{-ms-flex-pack:justify;justify-content:space-between}:host(.toggle-justify-start) .toggle-wrapper{-ms-flex-pack:start;justify-content:start}:host(.toggle-justify-end) .toggle-wrapper{-ms-flex-pack:end;justify-content:end}:host(.toggle-alignment-start) .toggle-wrapper{-ms-flex-align:start;align-items:start}:host(.toggle-alignment-center) .toggle-wrapper{-ms-flex-align:center;align-items:center}:host(.toggle-justify-space-between),:host(.toggle-justify-start),:host(.toggle-justify-end),:host(.toggle-alignment-start),:host(.toggle-alignment-center){display:block}.toggle-icon-wrapper{display:-ms-flexbox;display:flex;position:relative;-ms-flex-align:center;align-items:center;width:100%;height:100%;-webkit-transition:var(--handle-transition);transition:var(--handle-transition);will-change:transform}.toggle-icon{border-radius:var(--border-radius);display:block;position:relative;width:100%;height:100%;background:var(--track-background);overflow:inherit}:host(.toggle-checked) .toggle-icon{background:var(--track-background-checked)}.toggle-inner{border-radius:var(--handle-border-radius);position:absolute;left:var(--handle-spacing);width:var(--handle-width);height:var(--handle-height);max-height:var(--handle-max-height);-webkit-transition:var(--handle-transition);transition:var(--handle-transition);background:var(--handle-background);-webkit-box-shadow:var(--handle-box-shadow);box-shadow:var(--handle-box-shadow);contain:strict}:host(.toggle-ltr) .toggle-inner{left:var(--handle-spacing)}:host(.toggle-rtl) .toggle-inner{right:var(--handle-spacing)}:host(.toggle-ltr.toggle-checked) .toggle-icon-wrapper{-webkit-transform:translate3d(calc(100% - var(--handle-width)), 0, 0);transform:translate3d(calc(100% - var(--handle-width)), 0, 0)}:host(.toggle-rtl.toggle-checked) .toggle-icon-wrapper{-webkit-transform:translate3d(calc(-100% + var(--handle-width)), 0, 0);transform:translate3d(calc(-100% + var(--handle-width)), 0, 0)}:host(.toggle-checked) .toggle-inner{background:var(--handle-background-checked)}:host(.toggle-ltr.toggle-checked) .toggle-inner{-webkit-transform:translate3d(calc(var(--handle-spacing) * -2), 0, 0);transform:translate3d(calc(var(--handle-spacing) * -2), 0, 0)}:host(.toggle-rtl.toggle-checked) .toggle-inner{-webkit-transform:translate3d(calc(var(--handle-spacing) * 2), 0, 0);transform:translate3d(calc(var(--handle-spacing) * 2), 0, 0)}:host{--track-background:rgba(var(--ion-text-color-rgb, 0, 0, 0), 0.088);--track-background-checked:var(--ion-color-primary, #0054e9);--border-radius:15.5px;--handle-background:#ffffff;--handle-background-checked:#ffffff;--handle-border-radius:25.5px;--handle-box-shadow:0 3px 4px rgba(0, 0, 0, 0.06), 0 3px 8px rgba(0, 0, 0, 0.06);--handle-height:calc(31px - (2px * 2));--handle-max-height:calc(100% - var(--handle-spacing) * 2);--handle-width:calc(31px - (2px * 2));--handle-spacing:2px;--handle-transition:transform 300ms, width 120ms ease-in-out 80ms, left 110ms ease-in-out 80ms, right 110ms ease-in-out 80ms}.native-wrapper .toggle-icon{width:51px;height:31px;overflow:hidden}:host(.ion-color.toggle-checked) .toggle-icon{background:var(--ion-color-base)}:host(.toggle-activated) .toggle-switch-icon{opacity:0}.toggle-icon{-webkit-transform:translate3d(0, 0, 0);transform:translate3d(0, 0, 0);-webkit-transition:background-color 300ms;transition:background-color 300ms}.toggle-inner{will-change:transform}.toggle-switch-icon{position:absolute;top:50%;width:11px;height:11px;-webkit-transform:translateY(-50%);transform:translateY(-50%);-webkit-transition:opacity 300ms, color 300ms;transition:opacity 300ms, color 300ms}.toggle-switch-icon{position:absolute;color:var(--ion-color-dark, #222428)}:host(.toggle-ltr) .toggle-switch-icon{right:6px}:host(.toggle-rtl) .toggle-switch-icon{right:initial;left:6px;}:host(.toggle-checked) .toggle-switch-icon.toggle-switch-icon-checked{color:var(--ion-color-contrast, #fff)}:host(.toggle-checked) .toggle-switch-icon:not(.toggle-switch-icon-checked){opacity:0}.toggle-switch-icon-checked{position:absolute;width:15px;height:15px;-webkit-transform:translateY(-50%) rotate(90deg);transform:translateY(-50%) rotate(90deg)}:host(.toggle-ltr) .toggle-switch-icon-checked{right:initial;left:4px;}:host(.toggle-rtl) .toggle-switch-icon-checked{right:4px}:host(.toggle-activated) .toggle-icon::before,:host(.toggle-checked) .toggle-icon::before{-webkit-transform:scale3d(0, 0, 0);transform:scale3d(0, 0, 0)}:host(.toggle-activated.toggle-checked) .toggle-inner::before{-webkit-transform:scale3d(0, 0, 0);transform:scale3d(0, 0, 0)}:host(.toggle-activated) .toggle-inner{width:calc(var(--handle-width) + 6px)}:host(.toggle-ltr.toggle-activated.toggle-checked) .toggle-icon-wrapper{-webkit-transform:translate3d(calc(100% - var(--handle-width) - 6px), 0, 0);transform:translate3d(calc(100% - var(--handle-width) - 6px), 0, 0)}:host(.toggle-rtl.toggle-activated.toggle-checked) .toggle-icon-wrapper{-webkit-transform:translate3d(calc(-100% + var(--handle-width) + 6px), 0, 0);transform:translate3d(calc(-100% + var(--handle-width) + 6px), 0, 0)}:host(.toggle-disabled){opacity:0.3}";
var toggleMdCss = ":host{-webkit-box-sizing:content-box !important;box-sizing:content-box !important;display:inline-block;position:relative;max-width:100%;cursor:pointer;-webkit-user-select:none;-moz-user-select:none;-ms-user-select:none;user-select:none;z-index:2}:host(.in-item){-ms-flex:1 1 0px;flex:1 1 0;width:100%;height:100%}:host([slot=start]),:host([slot=end]){-ms-flex:initial;flex:initial;width:auto}:host(.ion-focused) input{border:2px solid #5e9ed6}:host(.toggle-disabled){pointer-events:none}input{display:none}.toggle-wrapper{display:-ms-flexbox;display:flex;position:relative;-ms-flex-positive:1;flex-grow:1;-ms-flex-align:center;align-items:center;-ms-flex-pack:justify;justify-content:space-between;height:inherit;-webkit-transition:background-color 15ms linear;transition:background-color 15ms linear;cursor:inherit}.label-text-wrapper{text-overflow:ellipsis;white-space:nowrap;overflow:hidden}:host(.in-item) .label-text-wrapper{margin-top:10px;margin-bottom:10px}:host(.in-item.toggle-label-placement-stacked) .label-text-wrapper{margin-top:10px;margin-bottom:16px}:host(.in-item.toggle-label-placement-stacked) .native-wrapper{margin-bottom:10px}.label-text-wrapper-hidden{display:none}.native-wrapper{display:-ms-flexbox;display:flex;-ms-flex-align:center;align-items:center}.toggle-bottom{padding-top:4px;display:-ms-flexbox;display:flex;-ms-flex-pack:justify;justify-content:space-between;font-size:0.75rem;white-space:normal}:host(.toggle-label-placement-stacked) .toggle-bottom{font-size:1rem}.toggle-bottom .error-text{display:none;color:var(--ion-color-danger, #c5000f)}.toggle-bottom .helper-text{display:block;color:var(--ion-color-step-700, var(--ion-text-color-step-300, #4d4d4d))}:host(.ion-touched.ion-invalid) .toggle-bottom .error-text{display:block}:host(.ion-touched.ion-invalid) .toggle-bottom .helper-text{display:none}:host(.toggle-label-placement-start) .toggle-wrapper{-ms-flex-direction:row;flex-direction:row}:host(.toggle-label-placement-start) .label-text-wrapper{-webkit-margin-start:0;margin-inline-start:0;-webkit-margin-end:16px;margin-inline-end:16px}:host(.toggle-label-placement-end) .toggle-wrapper{-ms-flex-direction:row-reverse;flex-direction:row-reverse;-ms-flex-pack:start;justify-content:start}:host(.toggle-label-placement-end) .label-text-wrapper{-webkit-margin-start:16px;margin-inline-start:16px;-webkit-margin-end:0;margin-inline-end:0}:host(.toggle-label-placement-fixed) .label-text-wrapper{-webkit-margin-start:0;margin-inline-start:0;-webkit-margin-end:16px;margin-inline-end:16px}:host(.toggle-label-placement-fixed) .label-text-wrapper{-ms-flex:0 0 100px;flex:0 0 100px;width:100px;min-width:100px;max-width:200px}:host(.toggle-label-placement-stacked) .toggle-wrapper{-ms-flex-direction:column;flex-direction:column;text-align:center}:host(.toggle-label-placement-stacked) .label-text-wrapper{-webkit-transform:scale(0.75);transform:scale(0.75);margin-left:0;margin-right:0;margin-bottom:16px;max-width:calc(100% / 0.75)}:host(.toggle-label-placement-stacked.toggle-alignment-start) .label-text-wrapper{-webkit-transform-origin:left top;transform-origin:left top}:host-context([dir=rtl]):host(.toggle-label-placement-stacked.toggle-alignment-start) .label-text-wrapper,:host-context([dir=rtl]).toggle-label-placement-stacked.toggle-alignment-start .label-text-wrapper{-webkit-transform-origin:right top;transform-origin:right top}@supports selector(:dir(rtl)){:host(.toggle-label-placement-stacked.toggle-alignment-start:dir(rtl)) .label-text-wrapper{-webkit-transform-origin:right top;transform-origin:right top}}:host(.toggle-label-placement-stacked.toggle-alignment-center) .label-text-wrapper{-webkit-transform-origin:center top;transform-origin:center top}:host-context([dir=rtl]):host(.toggle-label-placement-stacked.toggle-alignment-center) .label-text-wrapper,:host-context([dir=rtl]).toggle-label-placement-stacked.toggle-alignment-center .label-text-wrapper{-webkit-transform-origin:calc(100% - center) top;transform-origin:calc(100% - center) top}@supports selector(:dir(rtl)){:host(.toggle-label-placement-stacked.toggle-alignment-center:dir(rtl)) .label-text-wrapper{-webkit-transform-origin:calc(100% - center) top;transform-origin:calc(100% - center) top}}:host(.toggle-justify-space-between) .toggle-wrapper{-ms-flex-pack:justify;justify-content:space-between}:host(.toggle-justify-start) .toggle-wrapper{-ms-flex-pack:start;justify-content:start}:host(.toggle-justify-end) .toggle-wrapper{-ms-flex-pack:end;justify-content:end}:host(.toggle-alignment-start) .toggle-wrapper{-ms-flex-align:start;align-items:start}:host(.toggle-alignment-center) .toggle-wrapper{-ms-flex-align:center;align-items:center}:host(.toggle-justify-space-between),:host(.toggle-justify-start),:host(.toggle-justify-end),:host(.toggle-alignment-start),:host(.toggle-alignment-center){display:block}.toggle-icon-wrapper{display:-ms-flexbox;display:flex;position:relative;-ms-flex-align:center;align-items:center;width:100%;height:100%;-webkit-transition:var(--handle-transition);transition:var(--handle-transition);will-change:transform}.toggle-icon{border-radius:var(--border-radius);display:block;position:relative;width:100%;height:100%;background:var(--track-background);overflow:inherit}:host(.toggle-checked) .toggle-icon{background:var(--track-background-checked)}.toggle-inner{border-radius:var(--handle-border-radius);position:absolute;left:var(--handle-spacing);width:var(--handle-width);height:var(--handle-height);max-height:var(--handle-max-height);-webkit-transition:var(--handle-transition);transition:var(--handle-transition);background:var(--handle-background);-webkit-box-shadow:var(--handle-box-shadow);box-shadow:var(--handle-box-shadow);contain:strict}:host(.toggle-ltr) .toggle-inner{left:var(--handle-spacing)}:host(.toggle-rtl) .toggle-inner{right:var(--handle-spacing)}:host(.toggle-ltr.toggle-checked) .toggle-icon-wrapper{-webkit-transform:translate3d(calc(100% - var(--handle-width)), 0, 0);transform:translate3d(calc(100% - var(--handle-width)), 0, 0)}:host(.toggle-rtl.toggle-checked) .toggle-icon-wrapper{-webkit-transform:translate3d(calc(-100% + var(--handle-width)), 0, 0);transform:translate3d(calc(-100% + var(--handle-width)), 0, 0)}:host(.toggle-checked) .toggle-inner{background:var(--handle-background-checked)}:host(.toggle-ltr.toggle-checked) .toggle-inner{-webkit-transform:translate3d(calc(var(--handle-spacing) * -2), 0, 0);transform:translate3d(calc(var(--handle-spacing) * -2), 0, 0)}:host(.toggle-rtl.toggle-checked) .toggle-inner{-webkit-transform:translate3d(calc(var(--handle-spacing) * 2), 0, 0);transform:translate3d(calc(var(--handle-spacing) * 2), 0, 0)}:host{--track-background:rgba(var(--ion-text-color-rgb, 0, 0, 0), 0.39);--track-background-checked:rgba(var(--ion-color-primary-rgb, 0, 84, 233), 0.5);--border-radius:14px;--handle-background:#ffffff;--handle-background-checked:var(--ion-color-primary, #0054e9);--handle-border-radius:50%;--handle-box-shadow:0 3px 1px -2px rgba(0, 0, 0, 0.2), 0 2px 2px 0 rgba(0, 0, 0, 0.14), 0 1px 5px 0 rgba(0, 0, 0, 0.12);--handle-width:20px;--handle-height:20px;--handle-max-height:calc(100% + 6px);--handle-spacing:0;--handle-transition:transform 160ms cubic-bezier(0.4, 0, 0.2, 1), background-color 160ms cubic-bezier(0.4, 0, 0.2, 1)}.native-wrapper .toggle-icon{width:36px;height:14px}:host(.ion-color.toggle-checked) .toggle-icon{background:rgba(var(--ion-color-base-rgb), 0.5)}:host(.ion-color.toggle-checked) .toggle-inner{background:var(--ion-color-base)}:host(.toggle-checked) .toggle-inner{color:var(--ion-color-contrast, #fff)}.toggle-icon{-webkit-transition:background-color 160ms;transition:background-color 160ms}.toggle-inner{will-change:background-color, transform;display:-ms-flexbox;display:flex;-ms-flex-align:center;align-items:center;-ms-flex-pack:center;justify-content:center;color:#000}.toggle-inner .toggle-switch-icon{-webkit-padding-start:1px;padding-inline-start:1px;-webkit-padding-end:1px;padding-inline-end:1px;padding-top:1px;padding-bottom:1px;width:100%;height:100%}:host(.toggle-disabled){opacity:0.38}";
var Toggle = class {
  constructor(hostRef) {
    registerInstance(this, hostRef);
    this.ionChange = createEvent(this, "ionChange", 7);
    this.ionFocus = createEvent(this, "ionFocus", 7);
    this.ionBlur = createEvent(this, "ionBlur", 7);
    this.inputId = `ion-tg-${toggleIds++}`;
    this.inputLabelId = `${this.inputId}-lbl`;
    this.helperTextId = `${this.inputId}-helper-text`;
    this.errorTextId = `${this.inputId}-error-text`;
    this.lastDrag = 0;
    this.inheritedAttributes = {};
    this.didLoad = false;
    this.activated = false;
    this.name = this.inputId;
    this.checked = false;
    this.disabled = false;
    this.value = "on";
    this.enableOnOffLabels = config.get("toggleOnOffLabels");
    this.labelPlacement = "start";
    this.required = false;
    this.setupGesture = () => __async(this, null, function* () {
      const {
        toggleTrack
      } = this;
      if (toggleTrack) {
        this.gesture = (yield import("./index-CfgBF1SE-C6AOLKAZ.js")).createGesture({
          el: toggleTrack,
          gestureName: "toggle",
          gesturePriority: 100,
          threshold: 5,
          passive: false,
          onStart: () => this.onStart(),
          onMove: (ev) => this.onMove(ev),
          onEnd: (ev) => this.onEnd(ev)
        });
        this.disabledChanged();
      }
    });
    this.onKeyDown = (ev) => {
      if (ev.key === " ") {
        ev.preventDefault();
        if (!this.disabled) {
          this.toggleChecked();
        }
      }
    };
    this.onClick = (ev) => {
      const enableHaptics = isPlatform("ios");
      if (this.disabled) {
        return;
      }
      ev.preventDefault();
      if (this.lastDrag + 300 < Date.now()) {
        this.toggleChecked();
        enableHaptics && hapticSelection();
      }
    };
    this.onDivLabelClick = (ev) => {
      ev.stopPropagation();
    };
    this.onFocus = () => {
      this.ionFocus.emit();
    };
    this.onBlur = () => {
      this.ionBlur.emit();
    };
    this.getSwitchLabelIcon = (mode, checked) => {
      if (mode === "md") {
        return checked ? checkmarkOutline : removeOutline;
      }
      return checked ? removeOutline : ellipseOutline;
    };
  }
  disabledChanged() {
    if (this.gesture) {
      this.gesture.enable(!this.disabled);
    }
  }
  toggleChecked() {
    const {
      checked,
      value
    } = this;
    const isNowChecked = !checked;
    this.checked = isNowChecked;
    this.setFocus();
    this.ionChange.emit({
      checked: isNowChecked,
      value
    });
  }
  connectedCallback() {
    return __async(this, null, function* () {
      if (this.didLoad) {
        this.setupGesture();
      }
    });
  }
  componentDidLoad() {
    this.setupGesture();
    this.didLoad = true;
  }
  disconnectedCallback() {
    if (this.gesture) {
      this.gesture.destroy();
      this.gesture = void 0;
    }
  }
  componentWillLoad() {
    this.inheritedAttributes = Object.assign({}, inheritAriaAttributes(this.el));
  }
  onStart() {
    this.activated = true;
    this.setFocus();
  }
  onMove(detail) {
    if (shouldToggle(isRTL(this.el), this.checked, detail.deltaX, -10)) {
      this.toggleChecked();
      hapticSelection();
    }
  }
  onEnd(ev) {
    this.activated = false;
    this.lastDrag = Date.now();
    ev.event.preventDefault();
    ev.event.stopImmediatePropagation();
  }
  getValue() {
    return this.value || "";
  }
  setFocus() {
    if (this.focusEl) {
      this.focusEl.focus();
    }
  }
  renderOnOffSwitchLabels(mode, checked) {
    const icon = this.getSwitchLabelIcon(mode, checked);
    return h("ion-icon", {
      class: {
        "toggle-switch-icon": true,
        "toggle-switch-icon-checked": checked
      },
      icon,
      "aria-hidden": "true"
    });
  }
  renderToggleControl() {
    const mode = getIonMode(this);
    const {
      enableOnOffLabels,
      checked
    } = this;
    return h("div", {
      class: "toggle-icon",
      part: "track",
      ref: (el) => this.toggleTrack = el
    }, enableOnOffLabels && mode === "ios" && [this.renderOnOffSwitchLabels(mode, true), this.renderOnOffSwitchLabels(mode, false)], h("div", {
      class: "toggle-icon-wrapper"
    }, h("div", {
      class: "toggle-inner",
      part: "handle"
    }, enableOnOffLabels && mode === "md" && this.renderOnOffSwitchLabels(mode, checked))));
  }
  get hasLabel() {
    return this.el.textContent !== "";
  }
  getHintTextID() {
    const {
      el,
      helperText,
      errorText,
      helperTextId,
      errorTextId
    } = this;
    if (el.classList.contains("ion-touched") && el.classList.contains("ion-invalid") && errorText) {
      return errorTextId;
    }
    if (helperText) {
      return helperTextId;
    }
    return void 0;
  }
  /**
   * Responsible for rendering helper text and error text.
   * This element should only be rendered if hint text is set.
   */
  renderHintText() {
    const {
      helperText,
      errorText,
      helperTextId,
      errorTextId
    } = this;
    const hasHintText = !!helperText || !!errorText;
    if (!hasHintText) {
      return;
    }
    return h("div", {
      class: "toggle-bottom"
    }, h("div", {
      id: helperTextId,
      class: "helper-text",
      part: "supporting-text helper-text"
    }, helperText), h("div", {
      id: errorTextId,
      class: "error-text",
      part: "supporting-text error-text"
    }, errorText));
  }
  render() {
    const {
      activated,
      alignment,
      checked,
      color,
      disabled,
      el,
      errorTextId,
      hasLabel,
      inheritedAttributes,
      inputId,
      inputLabelId,
      justify,
      labelPlacement,
      name,
      required
    } = this;
    const mode = getIonMode(this);
    const value = this.getValue();
    const rtl = isRTL(el) ? "rtl" : "ltr";
    renderHiddenInput(true, el, name, checked ? value : "", disabled);
    return h(Host, {
      key: "21037ea2e8326f58c84becadde475f007f931924",
      role: "switch",
      "aria-checked": `${checked}`,
      "aria-describedby": this.getHintTextID(),
      "aria-invalid": this.getHintTextID() === errorTextId,
      onClick: this.onClick,
      "aria-labelledby": hasLabel ? inputLabelId : null,
      "aria-label": inheritedAttributes["aria-label"] || null,
      "aria-disabled": disabled ? "true" : null,
      tabindex: disabled ? void 0 : 0,
      onKeyDown: this.onKeyDown,
      class: createColorClasses(color, {
        [mode]: true,
        "in-item": hostContext("ion-item", el),
        "toggle-activated": activated,
        "toggle-checked": checked,
        "toggle-disabled": disabled,
        [`toggle-justify-${justify}`]: justify !== void 0,
        [`toggle-alignment-${alignment}`]: alignment !== void 0,
        [`toggle-label-placement-${labelPlacement}`]: true,
        [`toggle-${rtl}`]: true
      })
    }, h("label", {
      key: "4d153679d118d01286f6633d1c19558a97745ff6",
      class: "toggle-wrapper",
      htmlFor: inputId
    }, h("input", Object.assign({
      key: "0dfcd4df15b8d41bec5ff5f8912503afbb7bec53",
      type: "checkbox",
      role: "switch",
      "aria-checked": `${checked}`,
      checked,
      disabled,
      id: inputId,
      onFocus: () => this.onFocus(),
      onBlur: () => this.onBlur(),
      ref: (focusEl) => this.focusEl = focusEl,
      required
    }, inheritedAttributes)), h("div", {
      key: "ffed3a07ba2ab70e5b232e6041bc3b6b34be8331",
      class: {
        "label-text-wrapper": true,
        "label-text-wrapper-hidden": !hasLabel
      },
      part: "label",
      id: inputLabelId,
      onClick: this.onDivLabelClick
    }, h("slot", {
      key: "d88e1e3dcdd8293f6b61f237cd7a0511dcbce300"
    }), this.renderHintText()), h("div", {
      key: "0e924225f5f0caf3c88738acb6c557bd8c1b68f6",
      class: "native-wrapper"
    }, this.renderToggleControl())));
  }
  get el() {
    return getElement(this);
  }
  static get watchers() {
    return {
      "disabled": ["disabledChanged"]
    };
  }
};
var shouldToggle = (rtl, checked, deltaX, margin) => {
  if (checked) {
    return !rtl && margin > deltaX || rtl && 10 < deltaX;
  } else {
    return !rtl && 10 < deltaX || rtl && margin > deltaX;
  }
};
var toggleIds = 0;
Toggle.style = {
  ios: toggleIosCss,
  md: toggleMdCss
};
export {
  Toggle as ion_toggle
};
/*! Bundled license information:

@ionic/core/dist/esm/ion-toggle.entry.js:
  (*!
   * (C) Ionic http://ionicframework.com - MIT License
   *)
*/
//# sourceMappingURL=ion-toggle.entry-NAQHMSVX.js.map
