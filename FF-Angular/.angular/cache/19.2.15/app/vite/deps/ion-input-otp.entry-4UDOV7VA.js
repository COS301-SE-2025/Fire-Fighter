import {
  isRTL
} from "./chunk-JQCJAYTB.js";
import {
  createColorClasses
} from "./chunk-RC5HTYUZ.js";
import {
  inheritAriaAttributes
} from "./chunk-UHAXTLK2.js";
import {
  Fragment,
  Host,
  createEvent,
  getElement,
  getIonMode,
  h,
  printIonWarning,
  registerInstance
} from "./chunk-G2JG4RXT.js";
import {
  __async
} from "./chunk-UQIXM5CJ.js";

// node_modules/@ionic/core/dist/esm/ion-input-otp.entry.js
var inputOtpIosCss = ".sc-ion-input-otp-ios-h{--margin-top:0;--margin-end:0;--margin-bottom:0;--margin-start:0;--padding-top:16px;--padding-end:0;--padding-bottom:16px;--padding-start:0;--color:initial;--min-width:40px;--separator-width:8px;--separator-height:var(--separator-width);--separator-border-radius:999px;--separator-color:var(--ion-color-step-150, var(--ion-background-color-step-150, #d9d9d9));--highlight-color-focused:var(--ion-color-primary, #0054e9);--highlight-color-valid:var(--ion-color-success, #2dd55b);--highlight-color-invalid:var(--ion-color-danger, #c5000f);--highlight-color:var(--highlight-color-focused);display:block;position:relative;font-size:0.875rem}.input-otp-group.sc-ion-input-otp-ios{-webkit-margin-start:var(--margin-start);margin-inline-start:var(--margin-start);-webkit-margin-end:var(--margin-end);margin-inline-end:var(--margin-end);margin-top:var(--margin-top);margin-bottom:var(--margin-bottom);-webkit-padding-start:var(--padding-start);padding-inline-start:var(--padding-start);-webkit-padding-end:var(--padding-end);padding-inline-end:var(--padding-end);padding-top:var(--padding-top);padding-bottom:var(--padding-bottom);display:-ms-flexbox;display:flex;-ms-flex-align:center;align-items:center;-ms-flex-pack:center;justify-content:center}.native-wrapper.sc-ion-input-otp-ios{display:-ms-flexbox;display:flex;-ms-flex-align:center;align-items:center;-ms-flex-pack:center;justify-content:center;min-width:var(--min-width)}.native-input.sc-ion-input-otp-ios{border-radius:var(--border-radius);width:var(--width);min-width:inherit;height:var(--height);border-width:var(--border-width);border-style:solid;border-color:var(--border-color);background:var(--background);color:var(--color);font-size:inherit;text-align:center;-webkit-appearance:none;-moz-appearance:none;appearance:none}.has-focus.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios{caret-color:var(--highlight-color)}.input-otp-description.sc-ion-input-otp-ios{color:var(--ion-color-step-700, var(--ion-text-color-step-300, #4d4d4d));font-size:0.75rem;line-height:1.25rem;text-align:center}.input-otp-description-hidden.sc-ion-input-otp-ios{display:none}.input-otp-separator.sc-ion-input-otp-ios{border-radius:var(--separator-border-radius);-ms-flex-negative:0;flex-shrink:0;width:var(--separator-width);height:var(--separator-height);background:var(--separator-color)}.input-otp-size-small.sc-ion-input-otp-ios-h{--width:40px;--height:40px}.input-otp-size-small.sc-ion-input-otp-ios-h .input-otp-group.sc-ion-input-otp-ios{gap:8px}.input-otp-size-medium.sc-ion-input-otp-ios-h{--width:48px;--height:48px}.input-otp-size-large.sc-ion-input-otp-ios-h{--width:56px;--height:56px}.input-otp-size-medium.sc-ion-input-otp-ios-h .input-otp-group.sc-ion-input-otp-ios,.input-otp-size-large.sc-ion-input-otp-ios-h .input-otp-group.sc-ion-input-otp-ios{gap:12px}.input-otp-shape-round.sc-ion-input-otp-ios-h{--border-radius:16px}.input-otp-shape-soft.sc-ion-input-otp-ios-h{--border-radius:8px}.input-otp-shape-rectangular.sc-ion-input-otp-ios-h{--border-radius:0}.input-otp-fill-outline.sc-ion-input-otp-ios-h{--background:none}.input-otp-fill-solid.sc-ion-input-otp-ios-h{--border-color:var(--ion-color-step-50, var(--ion-background-color-step-50, #f2f2f2));--background:var(--ion-color-step-50, var(--ion-background-color-step-50, #f2f2f2))}.input-otp-disabled.sc-ion-input-otp-ios-h{--color:var(--ion-color-step-350, var(--ion-text-color-step-650, #a6a6a6))}.input-otp-fill-outline.input-otp-disabled.sc-ion-input-otp-ios-h{--background:var(--ion-color-step-50, var(--ion-background-color-step-50, #f2f2f2));--border-color:var(--ion-color-step-100, var(--ion-background-color-step-100, #e6e6e6))}.input-otp-disabled.sc-ion-input-otp-ios-h,.input-otp-disabled.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios:disabled{cursor:not-allowed}.has-focus.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios:focus{--border-color:var(--highlight-color);outline:none}.input-otp-fill-outline.input-otp-readonly.sc-ion-input-otp-ios-h{--background:var(--ion-color-step-50, var(--ion-background-color-step-50, #f2f2f2))}.input-otp-fill-solid.input-otp-disabled.sc-ion-input-otp-ios-h,.input-otp-fill-solid.input-otp-readonly.sc-ion-input-otp-ios-h{--border-color:var(--ion-color-step-100, var(--ion-background-color-step-100, #e6e6e6));--background:var(--ion-color-step-100, var(--ion-background-color-step-100, #e6e6e6))}.ion-touched.ion-invalid.sc-ion-input-otp-ios-h{--highlight-color:var(--highlight-color-invalid)}.ion-valid.sc-ion-input-otp-ios-h{--highlight-color:var(--highlight-color-valid)}.has-focus.ion-valid.sc-ion-input-otp-ios-h,.ion-touched.ion-invalid.sc-ion-input-otp-ios-h{--border-color:var(--highlight-color)}.ion-color.sc-ion-input-otp-ios-h{--highlight-color-focused:var(--ion-color-base)}.input-otp-fill-outline.ion-color.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios,.input-otp-fill-solid.ion-color.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios:focus{border-color:rgba(var(--ion-color-base-rgb), 0.6)}.input-otp-fill-outline.ion-color.ion-invalid.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios,.input-otp-fill-solid.ion-color.ion-invalid.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios,.input-otp-fill-outline.ion-color.has-focus.ion-invalid.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios,.input-otp-fill-solid.ion-color.has-focus.ion-invalid.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios{border-color:var(--ion-color-danger, #c5000f)}.input-otp-fill-outline.ion-color.ion-valid.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios,.input-otp-fill-solid.ion-color.ion-valid.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios,.input-otp-fill-outline.ion-color.has-focus.ion-valid.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios,.input-otp-fill-solid.ion-color.has-focus.ion-valid.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios{border-color:var(--ion-color-success, #2dd55b)}.input-otp-fill-outline.input-otp-disabled.ion-color.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios{border-color:rgba(var(--ion-color-base-rgb), 0.3)}.sc-ion-input-otp-ios-h{--border-width:0.55px}.has-focus.sc-ion-input-otp-ios-h .native-input.sc-ion-input-otp-ios:focus{--border-width:1px}.input-otp-fill-outline.sc-ion-input-otp-ios-h{--border-color:var(--ion-item-border-color, var(--ion-border-color, var(--ion-color-step-250, var(--ion-background-color-step-250, #c8c7cc))))}";
var inputOtpMdCss = ".sc-ion-input-otp-md-h{--margin-top:0;--margin-end:0;--margin-bottom:0;--margin-start:0;--padding-top:16px;--padding-end:0;--padding-bottom:16px;--padding-start:0;--color:initial;--min-width:40px;--separator-width:8px;--separator-height:var(--separator-width);--separator-border-radius:999px;--separator-color:var(--ion-color-step-150, var(--ion-background-color-step-150, #d9d9d9));--highlight-color-focused:var(--ion-color-primary, #0054e9);--highlight-color-valid:var(--ion-color-success, #2dd55b);--highlight-color-invalid:var(--ion-color-danger, #c5000f);--highlight-color:var(--highlight-color-focused);display:block;position:relative;font-size:0.875rem}.input-otp-group.sc-ion-input-otp-md{-webkit-margin-start:var(--margin-start);margin-inline-start:var(--margin-start);-webkit-margin-end:var(--margin-end);margin-inline-end:var(--margin-end);margin-top:var(--margin-top);margin-bottom:var(--margin-bottom);-webkit-padding-start:var(--padding-start);padding-inline-start:var(--padding-start);-webkit-padding-end:var(--padding-end);padding-inline-end:var(--padding-end);padding-top:var(--padding-top);padding-bottom:var(--padding-bottom);display:-ms-flexbox;display:flex;-ms-flex-align:center;align-items:center;-ms-flex-pack:center;justify-content:center}.native-wrapper.sc-ion-input-otp-md{display:-ms-flexbox;display:flex;-ms-flex-align:center;align-items:center;-ms-flex-pack:center;justify-content:center;min-width:var(--min-width)}.native-input.sc-ion-input-otp-md{border-radius:var(--border-radius);width:var(--width);min-width:inherit;height:var(--height);border-width:var(--border-width);border-style:solid;border-color:var(--border-color);background:var(--background);color:var(--color);font-size:inherit;text-align:center;-webkit-appearance:none;-moz-appearance:none;appearance:none}.has-focus.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md{caret-color:var(--highlight-color)}.input-otp-description.sc-ion-input-otp-md{color:var(--ion-color-step-700, var(--ion-text-color-step-300, #4d4d4d));font-size:0.75rem;line-height:1.25rem;text-align:center}.input-otp-description-hidden.sc-ion-input-otp-md{display:none}.input-otp-separator.sc-ion-input-otp-md{border-radius:var(--separator-border-radius);-ms-flex-negative:0;flex-shrink:0;width:var(--separator-width);height:var(--separator-height);background:var(--separator-color)}.input-otp-size-small.sc-ion-input-otp-md-h{--width:40px;--height:40px}.input-otp-size-small.sc-ion-input-otp-md-h .input-otp-group.sc-ion-input-otp-md{gap:8px}.input-otp-size-medium.sc-ion-input-otp-md-h{--width:48px;--height:48px}.input-otp-size-large.sc-ion-input-otp-md-h{--width:56px;--height:56px}.input-otp-size-medium.sc-ion-input-otp-md-h .input-otp-group.sc-ion-input-otp-md,.input-otp-size-large.sc-ion-input-otp-md-h .input-otp-group.sc-ion-input-otp-md{gap:12px}.input-otp-shape-round.sc-ion-input-otp-md-h{--border-radius:16px}.input-otp-shape-soft.sc-ion-input-otp-md-h{--border-radius:8px}.input-otp-shape-rectangular.sc-ion-input-otp-md-h{--border-radius:0}.input-otp-fill-outline.sc-ion-input-otp-md-h{--background:none}.input-otp-fill-solid.sc-ion-input-otp-md-h{--border-color:var(--ion-color-step-50, var(--ion-background-color-step-50, #f2f2f2));--background:var(--ion-color-step-50, var(--ion-background-color-step-50, #f2f2f2))}.input-otp-disabled.sc-ion-input-otp-md-h{--color:var(--ion-color-step-350, var(--ion-text-color-step-650, #a6a6a6))}.input-otp-fill-outline.input-otp-disabled.sc-ion-input-otp-md-h{--background:var(--ion-color-step-50, var(--ion-background-color-step-50, #f2f2f2));--border-color:var(--ion-color-step-100, var(--ion-background-color-step-100, #e6e6e6))}.input-otp-disabled.sc-ion-input-otp-md-h,.input-otp-disabled.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md:disabled{cursor:not-allowed}.has-focus.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md:focus{--border-color:var(--highlight-color);outline:none}.input-otp-fill-outline.input-otp-readonly.sc-ion-input-otp-md-h{--background:var(--ion-color-step-50, var(--ion-background-color-step-50, #f2f2f2))}.input-otp-fill-solid.input-otp-disabled.sc-ion-input-otp-md-h,.input-otp-fill-solid.input-otp-readonly.sc-ion-input-otp-md-h{--border-color:var(--ion-color-step-100, var(--ion-background-color-step-100, #e6e6e6));--background:var(--ion-color-step-100, var(--ion-background-color-step-100, #e6e6e6))}.ion-touched.ion-invalid.sc-ion-input-otp-md-h{--highlight-color:var(--highlight-color-invalid)}.ion-valid.sc-ion-input-otp-md-h{--highlight-color:var(--highlight-color-valid)}.has-focus.ion-valid.sc-ion-input-otp-md-h,.ion-touched.ion-invalid.sc-ion-input-otp-md-h{--border-color:var(--highlight-color)}.ion-color.sc-ion-input-otp-md-h{--highlight-color-focused:var(--ion-color-base)}.input-otp-fill-outline.ion-color.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md,.input-otp-fill-solid.ion-color.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md:focus{border-color:rgba(var(--ion-color-base-rgb), 0.6)}.input-otp-fill-outline.ion-color.ion-invalid.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md,.input-otp-fill-solid.ion-color.ion-invalid.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md,.input-otp-fill-outline.ion-color.has-focus.ion-invalid.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md,.input-otp-fill-solid.ion-color.has-focus.ion-invalid.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md{border-color:var(--ion-color-danger, #c5000f)}.input-otp-fill-outline.ion-color.ion-valid.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md,.input-otp-fill-solid.ion-color.ion-valid.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md,.input-otp-fill-outline.ion-color.has-focus.ion-valid.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md,.input-otp-fill-solid.ion-color.has-focus.ion-valid.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md{border-color:var(--ion-color-success, #2dd55b)}.input-otp-fill-outline.input-otp-disabled.ion-color.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md{border-color:rgba(var(--ion-color-base-rgb), 0.3)}.sc-ion-input-otp-md-h{--border-width:1px}.has-focus.sc-ion-input-otp-md-h .native-input.sc-ion-input-otp-md:focus{--border-width:2px}.input-otp-fill-outline.sc-ion-input-otp-md-h{--border-color:var(--ion-color-step-300, var(--ion-background-color-step-300, #b3b3b3))}";
var InputOTP = class {
  constructor(hostRef) {
    registerInstance(this, hostRef);
    this.ionInput = createEvent(this, "ionInput", 7);
    this.ionChange = createEvent(this, "ionChange", 7);
    this.ionComplete = createEvent(this, "ionComplete", 7);
    this.ionBlur = createEvent(this, "ionBlur", 7);
    this.ionFocus = createEvent(this, "ionFocus", 7);
    this.inheritedAttributes = {};
    this.inputRefs = [];
    this.inputId = `ion-input-otp-${inputIds++}`;
    this.parsedSeparators = [];
    this.isKeyboardNavigation = false;
    this.inputValues = [];
    this.hasFocus = false;
    this.autocapitalize = "off";
    this.disabled = false;
    this.fill = "outline";
    this.length = 4;
    this.readonly = false;
    this.shape = "round";
    this.size = "medium";
    this.type = "number";
    this.value = "";
    this.onFocus = (index) => (event) => {
      var _a;
      const {
        inputRefs
      } = this;
      if (!this.hasFocus) {
        this.ionFocus.emit(event);
        this.focusedValue = this.value;
      }
      this.hasFocus = true;
      let finalIndex = index;
      if (!this.isKeyboardNavigation) {
        const targetIndex = this.inputValues[index] ? index : this.getFirstEmptyIndex();
        finalIndex = targetIndex === -1 ? this.length - 1 : targetIndex;
        (_a = this.inputRefs[finalIndex]) === null || _a === void 0 ? void 0 : _a.focus();
      }
      inputRefs.forEach((input, i) => {
        input.tabIndex = i === finalIndex ? 0 : -1;
      });
      this.isKeyboardNavigation = false;
    };
    this.onBlur = (event) => {
      const {
        inputRefs
      } = this;
      const relatedTarget = event.relatedTarget;
      const isInternalFocus = relatedTarget != null && inputRefs.includes(relatedTarget);
      if (!isInternalFocus) {
        this.hasFocus = false;
        this.updateTabIndexes();
        this.ionBlur.emit(event);
        if (this.focusedValue !== this.value) {
          this.emitIonChange(event);
        }
      }
    };
    this.onKeyDown = (index) => (event) => {
      const {
        length
      } = this;
      const rtl = isRTL(this.el);
      const input = event.target;
      const metaShortcuts = ["a", "c", "v", "x", "r", "z", "y"];
      const isTextSelection = input.selectionStart !== input.selectionEnd;
      if (isTextSelection || (event.metaKey || event.ctrlKey) && metaShortcuts.includes(event.key.toLowerCase())) {
        return;
      }
      if (event.key === "Backspace") {
        if (this.inputValues[index]) {
          for (let i = index; i < length - 1; i++) {
            this.inputValues[i] = this.inputValues[i + 1];
          }
          this.inputValues[length - 1] = "";
          for (let i = 0; i < length; i++) {
            this.inputRefs[i].value = this.inputValues[i] || "";
          }
          this.updateValue(event);
          event.preventDefault();
        } else if (!this.inputValues[index] && index > 0) {
          this.focusPrevious(index);
        }
      } else if (event.key === "ArrowLeft" || event.key === "ArrowRight") {
        this.isKeyboardNavigation = true;
        event.preventDefault();
        const isLeft = event.key === "ArrowLeft";
        const shouldMoveNext = isLeft && rtl || !isLeft && !rtl;
        if (shouldMoveNext) {
          if (this.inputValues[index] && index < length - 1) {
            this.focusNext(index);
          }
        } else {
          this.focusPrevious(index);
        }
      } else if (event.key === "Tab") {
        this.isKeyboardNavigation = true;
        return;
      }
      if (this.inputValues[index] && this.validKeyPattern.test(event.key)) {
        if (!this.inputValues[length - 1]) {
          for (let i = length - 1; i > index; i--) {
            this.inputValues[i] = this.inputValues[i - 1];
            this.inputRefs[i].value = this.inputValues[i] || "";
          }
        }
        this.inputValues[index] = event.key;
        this.inputRefs[index].value = event.key;
        this.updateValue(event);
        event.preventDefault();
      }
    };
    this.onInput = (index) => (event) => {
      const {
        length,
        validKeyPattern
      } = this;
      const value = event.target.value;
      if (value.length > 1) {
        const validChars = value.split("").filter((char) => validKeyPattern.test(char)).slice(0, length);
        if (validChars.length === 0) {
          requestAnimationFrame(() => {
            this.inputRefs.forEach((input) => {
              input.value = "";
            });
          });
        }
        this.value = validChars.join("");
        this.updateValue(event);
        setTimeout(() => {
          var _a;
          const nextIndex = validChars.length < length ? validChars.length : length - 1;
          (_a = this.inputRefs[nextIndex]) === null || _a === void 0 ? void 0 : _a.focus();
        }, 20);
        return;
      }
      if (value.length > 0 && !validKeyPattern.test(value)) {
        this.inputRefs[index].value = "";
        this.inputValues[index] = "";
        return;
      }
      this.inputValues[index] = value;
      this.updateValue(event);
      if (value.length > 0) {
        this.focusNext(index);
      }
    };
    this.onPaste = (event) => {
      var _a, _b, _c;
      const {
        inputRefs,
        length,
        validKeyPattern
      } = this;
      event.preventDefault();
      const pastedText = (_a = event.clipboardData) === null || _a === void 0 ? void 0 : _a.getData("text");
      if (!pastedText) {
        this.emitIonInput(event);
        return;
      }
      const validChars = pastedText.split("").filter((char) => validKeyPattern.test(char)).slice(0, length);
      validChars.forEach((char, index) => {
        if (index < length) {
          this.inputRefs[index].value = char;
          this.inputValues[index] = char;
        }
      });
      this.value = validChars.join("");
      this.updateValue(event);
      const nextEmptyIndex = validChars.length;
      if (nextEmptyIndex < length) {
        (_b = inputRefs[nextEmptyIndex]) === null || _b === void 0 ? void 0 : _b.focus();
      } else {
        (_c = inputRefs[length - 1]) === null || _c === void 0 ? void 0 : _c.focus();
      }
    };
  }
  /**
   * Sets focus to an input box.
   * @param index - The index of the input box to focus (0-based).
   * If provided and the input box has a value, the input box at that index will be focused.
   * Otherwise, the first empty input box or the last input if all are filled will be focused.
   */
  setFocus(index) {
    return __async(this, null, function* () {
      var _a, _b;
      if (typeof index === "number") {
        const validIndex = Math.max(0, Math.min(index, this.length - 1));
        (_a = this.inputRefs[validIndex]) === null || _a === void 0 ? void 0 : _a.focus();
      } else {
        const tabbableIndex = this.getTabbableIndex();
        (_b = this.inputRefs[tabbableIndex]) === null || _b === void 0 ? void 0 : _b.focus();
      }
    });
  }
  valueChanged() {
    this.initializeValues();
    this.updateTabIndexes();
  }
  /**
   * Processes the separators prop into an array of numbers.
   *
   * If the separators prop is not provided, returns an empty array.
   * If the separators prop is 'all', returns an array of all valid positions (1 to length-1).
   * If the separators prop is an array, returns it as is.
   * If the separators prop is a string, splits it by commas and parses each part as a number.
   *
   * If the separators are greater than the input length, it will warn and ignore the separators.
   */
  processSeparators() {
    const {
      separators,
      length
    } = this;
    if (separators === void 0) {
      this.parsedSeparators = [];
      return;
    }
    if (typeof separators === "string" && separators !== "all") {
      const isValidFormat = /^(\d+)(,\d+)*$/.test(separators);
      if (!isValidFormat) {
        printIonWarning(`[ion-input-otp] - Invalid separators format. Expected a comma-separated list of numbers, an array of numbers, or "all". Received: ${separators}`, this.el);
        this.parsedSeparators = [];
        return;
      }
    }
    let separatorValues;
    if (separators === "all") {
      separatorValues = Array.from({
        length: length - 1
      }, (_, i) => i + 1);
    } else if (Array.isArray(separators)) {
      separatorValues = separators;
    } else {
      separatorValues = separators.split(",").map((pos) => parseInt(pos, 10)).filter((pos) => !isNaN(pos));
    }
    const duplicates = separatorValues.filter((pos, index) => separatorValues.indexOf(pos) !== index);
    if (duplicates.length > 0) {
      printIonWarning(`[ion-input-otp] - Duplicate separator positions are not allowed. Received: ${separators}`, this.el);
    }
    const invalidSeparators = separatorValues.filter((pos) => pos > length);
    if (invalidSeparators.length > 0) {
      printIonWarning(`[ion-input-otp] - The following separator positions are greater than the input length (${length}): ${invalidSeparators.join(", ")}. These separators will be ignored.`, this.el);
    }
    this.parsedSeparators = separatorValues.filter((pos) => pos <= length);
  }
  componentWillLoad() {
    this.inheritedAttributes = inheritAriaAttributes(this.el);
    this.processSeparators();
    this.initializeValues();
  }
  componentDidLoad() {
    this.updateTabIndexes();
  }
  /**
   * Get the regex pattern for allowed characters.
   * If a pattern is provided, use it to create a regex pattern
   * Otherwise, use the default regex pattern based on type
   */
  get validKeyPattern() {
    return new RegExp(`^${this.getPattern()}$`, "u");
  }
  /**
   * Gets the string pattern to pass to the input element
   * and use in the regex for allowed characters.
   */
  getPattern() {
    const {
      pattern,
      type
    } = this;
    if (pattern) {
      return pattern;
    }
    return type === "number" ? "[\\p{N}]" : "[\\p{L}\\p{N}]";
  }
  /**
   * Get the default value for inputmode.
   * If inputmode is provided, use it.
   * Otherwise, use the default inputmode based on type
   */
  getInputmode() {
    const {
      inputmode
    } = this;
    if (inputmode) {
      return inputmode;
    }
    if (this.type == "number") {
      return "numeric";
    } else {
      return "text";
    }
  }
  /**
   * Initializes the input values array based on the current value prop.
   * This splits the value into individual characters and validates them against
   * the allowed pattern. The values are then used as the values in the native
   * input boxes and the value of the input group is updated.
   */
  initializeValues() {
    this.inputValues = Array(this.length).fill("");
    if (this.value == null || String(this.value).length === 0) {
      return;
    }
    const chars = String(this.value).split("").slice(0, this.length);
    chars.forEach((char, index) => {
      if (this.validKeyPattern.test(char)) {
        this.inputValues[index] = char;
      }
    });
    this.value = this.inputValues.join("");
  }
  /**
   * Updates the value of the input group.
   * This updates the value of the input group and emits an `ionChange` event.
   * If all of the input boxes are filled, it emits an `ionComplete` event.
   */
  updateValue(event) {
    const {
      inputValues,
      length
    } = this;
    const newValue = inputValues.join("");
    this.value = newValue;
    this.emitIonInput(event);
    if (newValue.length === length) {
      this.ionComplete.emit({
        value: newValue
      });
    }
  }
  /**
   * Emits an `ionChange` event.
   * This API should be called for user committed changes.
   * This API should not be used for external value changes.
   */
  emitIonChange(event) {
    const {
      value
    } = this;
    const newValue = value == null ? value : value.toString();
    this.ionChange.emit({
      value: newValue,
      event
    });
  }
  /**
   * Emits an `ionInput` event.
   * This is used to emit the input value when the user types,
   * backspaces, or pastes.
   */
  emitIonInput(event) {
    const {
      value
    } = this;
    const newValue = value == null ? value : value.toString();
    this.ionInput.emit({
      value: newValue,
      event
    });
  }
  /**
   * Focuses the next input box.
   */
  focusNext(currentIndex) {
    var _a;
    const {
      inputRefs,
      length
    } = this;
    if (currentIndex < length - 1) {
      (_a = inputRefs[currentIndex + 1]) === null || _a === void 0 ? void 0 : _a.focus();
    }
  }
  /**
   * Focuses the previous input box.
   */
  focusPrevious(currentIndex) {
    var _a;
    const {
      inputRefs
    } = this;
    if (currentIndex > 0) {
      (_a = inputRefs[currentIndex - 1]) === null || _a === void 0 ? void 0 : _a.focus();
    }
  }
  /**
   * Searches through the input values and returns the index
   * of the first empty input.
   * Returns -1 if all inputs are filled.
   */
  getFirstEmptyIndex() {
    var _a;
    const {
      inputValues,
      length
    } = this;
    const values = Array.from({
      length
    }, (_, i) => inputValues[i] || "");
    return (_a = values.findIndex((value) => !value || value === "")) !== null && _a !== void 0 ? _a : -1;
  }
  /**
   * Returns the index of the input that should be tabbed to.
   * If all inputs are filled, returns the last input's index.
   * Otherwise, returns the index of the first empty input.
   */
  getTabbableIndex() {
    const {
      length
    } = this;
    const firstEmptyIndex = this.getFirstEmptyIndex();
    return firstEmptyIndex === -1 ? length - 1 : firstEmptyIndex;
  }
  /**
   * Updates the tabIndexes for the input boxes.
   * This is used to ensure that the correct input is
   * focused when the user navigates using the tab key.
   */
  updateTabIndexes() {
    const {
      inputRefs,
      inputValues,
      length
    } = this;
    let firstEmptyIndex = -1;
    for (let i = 0; i < length; i++) {
      if (!inputValues[i] || inputValues[i] === "") {
        firstEmptyIndex = i;
        break;
      }
    }
    inputRefs.forEach((input, index) => {
      const shouldBeTabbable = firstEmptyIndex === -1 ? index === length - 1 : firstEmptyIndex === index;
      input.tabIndex = shouldBeTabbable ? 0 : -1;
      const isEmpty = !inputValues[index] || inputValues[index] === "";
      input.setAttribute("aria-hidden", isEmpty && !shouldBeTabbable ? "true" : "false");
    });
  }
  /**
   * Determines if a separator should be shown for a given index by
   * checking if the index is included in the parsed separators array.
   */
  showSeparator(index) {
    const {
      length
    } = this;
    return this.parsedSeparators.includes(index + 1) && index < length - 1;
  }
  render() {
    var _a, _b;
    const {
      autocapitalize,
      color,
      disabled,
      el,
      fill,
      hasFocus,
      inheritedAttributes,
      inputId,
      inputRefs,
      inputValues,
      length,
      readonly,
      shape,
      size
    } = this;
    const mode = getIonMode(this);
    const inputmode = this.getInputmode();
    const tabbableIndex = this.getTabbableIndex();
    const pattern = this.getPattern();
    const hasDescription = ((_b = (_a = el.querySelector(".input-otp-description")) === null || _a === void 0 ? void 0 : _a.textContent) === null || _b === void 0 ? void 0 : _b.trim()) !== "";
    return h(Host, {
      key: "df8fca036cedea0812185a02e3b655d7d76285e0",
      class: createColorClasses(color, {
        [mode]: true,
        "has-focus": hasFocus,
        [`input-otp-size-${size}`]: true,
        [`input-otp-shape-${shape}`]: true,
        [`input-otp-fill-${fill}`]: true,
        "input-otp-disabled": disabled,
        "input-otp-readonly": readonly
      })
    }, h("div", Object.assign({
      key: "831be3f939cf037f0eb8d7e37e0afd4ef9a3c2c5",
      role: "group",
      "aria-label": "One-time password input",
      class: "input-otp-group"
    }, inheritedAttributes), Array.from({
      length
    }).map((_, index) => h(Fragment, null, h("div", {
      class: "native-wrapper"
    }, h("input", {
      class: "native-input",
      id: `${inputId}-${index}`,
      "aria-label": `Input ${index + 1} of ${length}`,
      type: "text",
      autoCapitalize: autocapitalize,
      inputmode,
      pattern,
      disabled,
      readOnly: readonly,
      tabIndex: index === tabbableIndex ? 0 : -1,
      value: inputValues[index] || "",
      autocomplete: "one-time-code",
      ref: (el2) => inputRefs[index] = el2,
      onInput: this.onInput(index),
      onBlur: this.onBlur,
      onFocus: this.onFocus(index),
      onKeyDown: this.onKeyDown(index),
      onPaste: this.onPaste
    })), this.showSeparator(index) && h("div", {
      class: "input-otp-separator"
    })))), h("div", {
      key: "5311fedc34f7af3efd5f69e5a3d768055119c4f1",
      class: {
        "input-otp-description": true,
        "input-otp-description-hidden": !hasDescription
      }
    }, h("slot", {
      key: "9e8afa2f7fa76c3092582dc27770fdf565a1b9ba"
    })));
  }
  get el() {
    return getElement(this);
  }
  static get watchers() {
    return {
      "value": ["valueChanged"],
      "separators": ["processSeparators"],
      "length": ["processSeparators"]
    };
  }
};
var inputIds = 0;
InputOTP.style = {
  ios: inputOtpIosCss,
  md: inputOtpMdCss
};
export {
  InputOTP as ion_input_otp
};
/*! Bundled license information:

@ionic/core/dist/esm/ion-input-otp.entry.js:
  (*!
   * (C) Ionic http://ionicframework.com - MIT License
   *)
*/
//# sourceMappingURL=ion-input-otp.entry-4UDOV7VA.js.map
