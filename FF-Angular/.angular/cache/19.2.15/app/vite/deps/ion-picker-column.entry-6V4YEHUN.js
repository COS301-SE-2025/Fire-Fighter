import {
  hapticSelectionChanged,
  hapticSelectionEnd,
  hapticSelectionStart
} from "./chunk-OQILWA36.js";
import "./chunk-I7KKGUX3.js";
import {
  createColorClasses
} from "./chunk-RC5HTYUZ.js";
import {
  getElementRoot,
  raf
} from "./chunk-UHAXTLK2.js";
import {
  doc
} from "./chunk-AM533ZC5.js";
import {
  Host,
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

// node_modules/@ionic/core/dist/esm/ion-picker-column.entry.js
var pickerColumnCss = ":host{display:-ms-flexbox;display:flex;position:relative;-ms-flex-align:center;align-items:center;-ms-flex-pack:center;justify-content:center;max-width:100%;height:200px;font-size:22px;text-align:center}.assistive-focusable{left:0;right:0;top:0;bottom:0;position:absolute;z-index:1;pointer-events:none}.assistive-focusable:focus{outline:none}.picker-opts{-webkit-padding-start:16px;padding-inline-start:16px;-webkit-padding-end:16px;padding-inline-end:16px;padding-top:0px;padding-bottom:0px;min-width:26px;max-height:200px;outline:none;text-align:inherit;-webkit-scroll-snap-type:y mandatory;-ms-scroll-snap-type:y mandatory;scroll-snap-type:y mandatory;overflow-x:hidden;overflow-y:scroll;scrollbar-width:none}.picker-item-empty{padding-left:0;padding-right:0;padding-top:0;padding-bottom:0;margin-left:0;margin-right:0;margin-top:0;margin-bottom:0;display:block;width:100%;height:34px;border:0px;outline:none;background:transparent;color:inherit;font-family:var(--ion-font-family, inherit);font-size:inherit;line-height:34px;text-align:inherit;text-overflow:ellipsis;white-space:nowrap;overflow:hidden}.picker-opts::-webkit-scrollbar{display:none}::slotted(ion-picker-column-option){display:block;scroll-snap-align:center}.picker-item-empty,:host(:not([disabled])) ::slotted(ion-picker-column-option.option-disabled){scroll-snap-align:none}::slotted([slot=prefix]),::slotted([slot=suffix]){max-width:200px;text-overflow:ellipsis;white-space:nowrap;overflow:hidden}::slotted([slot=prefix]){-webkit-padding-start:16px;padding-inline-start:16px;-webkit-padding-end:16px;padding-inline-end:16px;padding-top:0;padding-bottom:0;-ms-flex-pack:end;justify-content:end}::slotted([slot=suffix]){-webkit-padding-start:16px;padding-inline-start:16px;-webkit-padding-end:16px;padding-inline-end:16px;padding-top:0;padding-bottom:0;-ms-flex-pack:start;justify-content:start}:host(.picker-column-disabled) .picker-opts{overflow-y:hidden}:host(.picker-column-disabled) ::slotted(ion-picker-column-option){cursor:default;opacity:0.4;pointer-events:none}@media (any-hover: hover){:host(:focus) .picker-opts{outline:none;background:rgba(var(--ion-color-base-rgb), 0.2)}}";
var PickerColumn = class {
  constructor(hostRef) {
    registerInstance(this, hostRef);
    this.ionChange = createEvent(this, "ionChange", 7);
    this.isScrolling = false;
    this.isColumnVisible = false;
    this.canExitInputMode = true;
    this.updateValueTextOnScroll = false;
    this.ariaLabel = null;
    this.isActive = false;
    this.disabled = false;
    this.color = "primary";
    this.numericInput = false;
    this.centerPickerItemInView = (target, smooth = true, canExitInputMode = true) => {
      const {
        isColumnVisible,
        scrollEl
      } = this;
      if (isColumnVisible && scrollEl) {
        const top = target.offsetTop - 3 * target.clientHeight + target.clientHeight / 2;
        if (scrollEl.scrollTop !== top) {
          this.canExitInputMode = canExitInputMode;
          this.updateValueTextOnScroll = false;
          scrollEl.scroll({
            top,
            left: 0,
            behavior: smooth ? "smooth" : void 0
          });
        }
      }
    };
    this.setPickerItemActiveState = (item, isActive) => {
      if (isActive) {
        item.classList.add(PICKER_ITEM_ACTIVE_CLASS);
      } else {
        item.classList.remove(PICKER_ITEM_ACTIVE_CLASS);
      }
    };
    this.inputModeChange = (ev) => {
      if (!this.numericInput) {
        return;
      }
      const {
        useInputMode,
        inputModeColumn
      } = ev.detail;
      const isColumnActive = inputModeColumn === void 0 || inputModeColumn === this.el;
      if (!useInputMode || !isColumnActive) {
        this.setInputModeActive(false);
        return;
      }
      this.setInputModeActive(true);
    };
    this.setInputModeActive = (state) => {
      if (this.isScrolling) {
        this.scrollEndCallback = () => {
          this.isActive = state;
        };
        return;
      }
      this.isActive = state;
    };
    this.initializeScrollListener = () => {
      const enableHaptics = isPlatform("ios");
      const {
        el,
        scrollEl
      } = this;
      let timeout;
      let activeEl = this.activeItem;
      const scrollCallback = () => {
        raf(() => {
          var _a;
          if (!scrollEl) return;
          if (timeout) {
            clearTimeout(timeout);
            timeout = void 0;
          }
          if (!this.isScrolling) {
            enableHaptics && hapticSelectionStart();
            this.isScrolling = true;
          }
          const bbox = scrollEl.getBoundingClientRect();
          const centerX = bbox.x + bbox.width / 2;
          const centerY = bbox.y + bbox.height / 2;
          const rootNode = el.getRootNode();
          const hasParentShadow = rootNode instanceof ShadowRoot;
          const referenceNode = hasParentShadow ? rootNode : doc;
          if (referenceNode === void 0) {
            return;
          }
          const elementsAtPoint = referenceNode.elementsFromPoint(centerX, centerY);
          let newActiveElement = elementsAtPoint.find((el2) => el2.tagName === "ION-PICKER-COLUMN-OPTION");
          if (newActiveElement === void 0) {
            const fallbackActiveElement = referenceNode.elementFromPoint(centerX, centerY);
            if ((fallbackActiveElement === null || fallbackActiveElement === void 0 ? void 0 : fallbackActiveElement.tagName) === "ION-PICKER-COLUMN-OPTION") {
              newActiveElement = fallbackActiveElement;
            }
          }
          if (activeEl !== void 0) {
            this.setPickerItemActiveState(activeEl, false);
          }
          if (newActiveElement === void 0 || newActiveElement.disabled) {
            return;
          }
          if (newActiveElement !== activeEl) {
            enableHaptics && hapticSelectionChanged();
            if (this.canExitInputMode) {
              this.exitInputMode();
            }
          }
          activeEl = newActiveElement;
          this.setPickerItemActiveState(newActiveElement, true);
          if (this.updateValueTextOnScroll) {
            (_a = this.assistiveFocusable) === null || _a === void 0 ? void 0 : _a.setAttribute("aria-valuetext", this.getOptionValueText(newActiveElement));
          }
          timeout = setTimeout(() => {
            this.isScrolling = false;
            this.updateValueTextOnScroll = true;
            enableHaptics && hapticSelectionEnd();
            const {
              scrollEndCallback
            } = this;
            if (scrollEndCallback) {
              scrollEndCallback();
              this.scrollEndCallback = void 0;
            }
            this.canExitInputMode = true;
            this.setValue(newActiveElement.value);
          }, 250);
        });
      };
      raf(() => {
        if (!scrollEl) return;
        scrollEl.addEventListener("scroll", scrollCallback);
        this.destroyScrollListener = () => {
          scrollEl.removeEventListener("scroll", scrollCallback);
        };
      });
    };
    this.exitInputMode = () => {
      const {
        parentEl
      } = this;
      if (parentEl == null) return;
      parentEl.exitInputMode();
      this.el.classList.remove("picker-column-active");
    };
    this.findNextOption = (stride = 1) => {
      const {
        activeItem
      } = this;
      if (!activeItem) return null;
      let prevNode = activeItem;
      let node = activeItem.nextElementSibling;
      while (node != null) {
        if (stride > 0) {
          stride--;
        }
        if (node.tagName === "ION-PICKER-COLUMN-OPTION" && !node.disabled && stride === 0) {
          return node;
        }
        prevNode = node;
        node = node.nextElementSibling;
      }
      return prevNode;
    };
    this.findPreviousOption = (stride = 1) => {
      const {
        activeItem
      } = this;
      if (!activeItem) return null;
      let nextNode = activeItem;
      let node = activeItem.previousElementSibling;
      while (node != null) {
        if (stride > 0) {
          stride--;
        }
        if (node.tagName === "ION-PICKER-COLUMN-OPTION" && !node.disabled && stride === 0) {
          return node;
        }
        nextNode = node;
        node = node.previousElementSibling;
      }
      return nextNode;
    };
    this.onKeyDown = (ev) => {
      const mobile = isPlatform("mobile");
      let newOption = null;
      switch (ev.key) {
        case "ArrowDown":
          newOption = mobile ? this.findPreviousOption() : this.findNextOption();
          break;
        case "ArrowUp":
          newOption = mobile ? this.findNextOption() : this.findPreviousOption();
          break;
        case "PageUp":
          newOption = mobile ? this.findNextOption(5) : this.findPreviousOption(5);
          break;
        case "PageDown":
          newOption = mobile ? this.findPreviousOption(5) : this.findNextOption(5);
          break;
        case "Home":
          newOption = this.el.querySelector("ion-picker-column-option:first-of-type");
          break;
        case "End":
          newOption = this.el.querySelector("ion-picker-column-option:last-of-type");
          break;
      }
      if (newOption !== null) {
        this.setValue(newOption.value);
        ev.preventDefault();
      }
    };
    this.getOptionValueText = (el) => {
      var _a;
      return el ? (_a = el.getAttribute("aria-label")) !== null && _a !== void 0 ? _a : el.innerText : "";
    };
    this.renderAssistiveFocusable = () => {
      const {
        activeItem
      } = this;
      const valueText = this.getOptionValueText(activeItem);
      return h("div", {
        ref: (el) => this.assistiveFocusable = el,
        class: "assistive-focusable",
        role: "slider",
        tabindex: this.disabled ? void 0 : 0,
        "aria-label": this.ariaLabel,
        "aria-valuemin": 0,
        "aria-valuemax": 0,
        "aria-valuenow": 0,
        "aria-valuetext": valueText,
        "aria-orientation": "vertical",
        onKeyDown: (ev) => this.onKeyDown(ev)
      });
    };
  }
  ariaLabelChanged(newValue) {
    this.ariaLabel = newValue;
  }
  valueChange() {
    if (this.isColumnVisible) {
      this.scrollActiveItemIntoView(true);
    }
  }
  /**
   * Only setup scroll listeners
   * when the picker is visible, otherwise
   * the container will have a scroll
   * height of 0px.
   */
  componentWillLoad() {
    const parentEl = this.parentEl = this.el.closest("ion-picker");
    const visibleCallback = (entries) => {
      const ev = entries[entries.length - 1];
      if (ev.isIntersecting) {
        const {
          activeItem,
          el
        } = this;
        this.isColumnVisible = true;
        const oldActive = getElementRoot(el).querySelector(`.${PICKER_ITEM_ACTIVE_CLASS}`);
        if (oldActive) {
          this.setPickerItemActiveState(oldActive, false);
        }
        this.scrollActiveItemIntoView();
        if (activeItem) {
          this.setPickerItemActiveState(activeItem, true);
        }
        this.initializeScrollListener();
      } else {
        this.isColumnVisible = false;
        if (this.destroyScrollListener) {
          this.destroyScrollListener();
          this.destroyScrollListener = void 0;
        }
      }
    };
    new IntersectionObserver(visibleCallback, {
      threshold: 1e-3,
      root: this.parentEl
    }).observe(this.el);
    if (parentEl !== null) {
      parentEl.addEventListener("ionInputModeChange", (ev) => this.inputModeChange(ev));
    }
  }
  componentDidRender() {
    const {
      el,
      activeItem,
      isColumnVisible,
      value
    } = this;
    if (isColumnVisible && !activeItem) {
      const firstOption = el.querySelector("ion-picker-column-option");
      if (firstOption !== null && firstOption.value !== value) {
        this.setValue(firstOption.value);
      }
    }
  }
  /** @internal  */
  scrollActiveItemIntoView(smooth = false) {
    return __async(this, null, function* () {
      const activeEl = this.activeItem;
      if (activeEl) {
        this.centerPickerItemInView(activeEl, smooth, false);
      }
    });
  }
  /**
   * Sets the value prop and fires the ionChange event.
   * This is used when we need to fire ionChange from
   * user-generated events that cannot be caught with normal
   * input/change event listeners.
   * @internal
   */
  setValue(value) {
    return __async(this, null, function* () {
      if (this.disabled === true || this.value === value) {
        return;
      }
      this.value = value;
      this.ionChange.emit({
        value
      });
    });
  }
  /**
   * Sets focus on the scrollable container within the picker column.
   * Use this method instead of the global `pickerColumn.focus()`.
   */
  setFocus() {
    return __async(this, null, function* () {
      if (this.assistiveFocusable) {
        this.assistiveFocusable.focus();
      }
    });
  }
  connectedCallback() {
    var _a;
    this.ariaLabel = (_a = this.el.getAttribute("aria-label")) !== null && _a !== void 0 ? _a : "Select a value";
  }
  get activeItem() {
    const {
      value
    } = this;
    const options = Array.from(this.el.querySelectorAll("ion-picker-column-option"));
    return options.find((option) => {
      if (!this.disabled && option.disabled) {
        return false;
      }
      return option.value === value;
    });
  }
  render() {
    const {
      color,
      disabled,
      isActive,
      numericInput
    } = this;
    const mode = getIonMode(this);
    return h(Host, {
      key: "ea0280355b2f87895bf7dddd289ccf473aa759f3",
      class: createColorClasses(color, {
        [mode]: true,
        ["picker-column-active"]: isActive,
        ["picker-column-numeric-input"]: numericInput,
        ["picker-column-disabled"]: disabled
      })
    }, this.renderAssistiveFocusable(), h("slot", {
      key: "482992131cdeb85b1f61430d7fe1322a16345769",
      name: "prefix"
    }), h("div", {
      key: "43f7f80d621d411ef366b3ca1396299e8c9a0c97",
      "aria-hidden": "true",
      class: "picker-opts",
      ref: (el) => {
        this.scrollEl = el;
      },
      /**
       * When an element has an overlay scroll style and
       * a fixed height, Firefox will focus the scrollable
       * container if the content exceeds the container's
       * dimensions.
       *
       * This causes keyboard navigation to focus to this
       * element instead of going to the next element in
       * the tab order.
       *
       * The desired behavior is for the user to be able to
       * focus the assistive focusable element and tab to
       * the next element in the tab order. Instead of tabbing
       * to this element.
       *
       * To prevent this, we set the tabIndex to -1. This
       * will match the behavior of the other browsers.
       */
      tabIndex: -1
    }, h("div", {
      key: "13a9ee686132af32240710730765de4c0003a9e8",
      class: "picker-item-empty",
      "aria-hidden": "true"
    }, " "), h("div", {
      key: "dbccba4920833cfcebe9b0fc763458ec3053705a",
      class: "picker-item-empty",
      "aria-hidden": "true"
    }, " "), h("div", {
      key: "682b43f83a5ea2e46067457f3af118535e111edb",
      class: "picker-item-empty",
      "aria-hidden": "true"
    }, " "), h("slot", {
      key: "d27e1e1dc0504b2f4627a29912a05bb91e8e413a"
    }), h("div", {
      key: "61c948dbb9cf7469aed3018542bc0954211585ba",
      class: "picker-item-empty",
      "aria-hidden": "true"
    }, " "), h("div", {
      key: "cf46c277fbee65e35ff44ce0d53ce12aa9cbf9db",
      class: "picker-item-empty",
      "aria-hidden": "true"
    }, " "), h("div", {
      key: "bbc0e2d491d3f836ab849493ade2f7fa6ad9244e",
      class: "picker-item-empty",
      "aria-hidden": "true"
    }, " ")), h("slot", {
      key: "d25cbbe14b2914fe7b878d43b4e3f4a8c8177d24",
      name: "suffix"
    }));
  }
  get el() {
    return getElement(this);
  }
  static get watchers() {
    return {
      "aria-label": ["ariaLabelChanged"],
      "value": ["valueChange"]
    };
  }
};
var PICKER_ITEM_ACTIVE_CLASS = "option-active";
PickerColumn.style = pickerColumnCss;
export {
  PickerColumn as ion_picker_column
};
/*! Bundled license information:

@ionic/core/dist/esm/ion-picker-column.entry.js:
  (*!
   * (C) Ionic http://ionicframework.com - MIT License
   *)
*/
//# sourceMappingURL=ion-picker-column.entry-6V4YEHUN.js.map
