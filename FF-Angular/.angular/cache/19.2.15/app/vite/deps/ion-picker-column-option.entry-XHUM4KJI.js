import {
  createColorClasses
} from "./chunk-RC5HTYUZ.js";
import {
  inheritAttributes
} from "./chunk-UHAXTLK2.js";
import {
  Host,
  getElement,
  getIonMode,
  h,
  registerInstance
} from "./chunk-G2JG4RXT.js";
import "./chunk-UQIXM5CJ.js";

// node_modules/@ionic/core/dist/esm/ion-picker-column-option.entry.js
var pickerColumnOptionIosCss = "button{padding-left:0;padding-right:0;padding-top:0;padding-bottom:0;margin-left:0;margin-right:0;margin-top:0;margin-bottom:0;width:100%;height:34px;border:0px;outline:none;background:transparent;color:inherit;font-family:var(--ion-font-family, inherit);font-size:inherit;line-height:34px;text-align:inherit;text-overflow:ellipsis;white-space:nowrap;cursor:pointer;overflow:hidden}:host(.option-disabled){opacity:0.4}:host(.option-disabled) button{cursor:default}";
var pickerColumnOptionMdCss = "button{padding-left:0;padding-right:0;padding-top:0;padding-bottom:0;margin-left:0;margin-right:0;margin-top:0;margin-bottom:0;width:100%;height:34px;border:0px;outline:none;background:transparent;color:inherit;font-family:var(--ion-font-family, inherit);font-size:inherit;line-height:34px;text-align:inherit;text-overflow:ellipsis;white-space:nowrap;cursor:pointer;overflow:hidden}:host(.option-disabled){opacity:0.4}:host(.option-disabled) button{cursor:default}:host(.option-active){color:var(--ion-color-base)}";
var PickerColumnOption = class {
  constructor(hostRef) {
    registerInstance(this, hostRef);
    this.pickerColumn = null;
    this.ariaLabel = null;
    this.disabled = false;
    this.color = "primary";
  }
  /**
   * The aria-label of the option has changed after the
   * first render and needs to be updated within the component.
   *
   * @param ariaLbl The new aria-label value.
   */
  onAriaLabelChange(ariaLbl) {
    this.ariaLabel = ariaLbl;
  }
  componentWillLoad() {
    const inheritedAttributes = inheritAttributes(this.el, ["aria-label"]);
    this.ariaLabel = inheritedAttributes["aria-label"] || null;
  }
  connectedCallback() {
    this.pickerColumn = this.el.closest("ion-picker-column");
  }
  disconnectedCallback() {
    this.pickerColumn = null;
  }
  /**
   * The column options can load at any time
   * so the options needs to tell the
   * parent picker column when it is loaded
   * so the picker column can ensure it is
   * centered in the view.
   *
   * We intentionally run this for every
   * option. If we only ran this from
   * the selected option then if the newly
   * loaded options were not selected then
   * scrollActiveItemIntoView would not be called.
   */
  componentDidLoad() {
    const {
      pickerColumn
    } = this;
    if (pickerColumn !== null) {
      pickerColumn.scrollActiveItemIntoView();
    }
  }
  /**
   * When an option is clicked, update the
   * parent picker column value. This
   * component will handle centering the option
   * in the column view.
   */
  onClick() {
    const {
      pickerColumn
    } = this;
    if (pickerColumn !== null) {
      pickerColumn.setValue(this.value);
    }
  }
  render() {
    const {
      color,
      disabled,
      ariaLabel
    } = this;
    const mode = getIonMode(this);
    return h(Host, {
      key: "f816729941aabcb31ddfdce3ffe2e2139030d715",
      class: createColorClasses(color, {
        [mode]: true,
        ["option-disabled"]: disabled
      })
    }, h("button", {
      key: "48dff7833bb60fc8331cd353a0885e6affa683d1",
      tabindex: "-1",
      "aria-label": ariaLabel,
      disabled,
      onClick: () => this.onClick()
    }, h("slot", {
      key: "f9224d0e7b7aa6c05b29abfdcfe0f30ad6ee3141"
    })));
  }
  get el() {
    return getElement(this);
  }
  static get watchers() {
    return {
      "aria-label": ["onAriaLabelChange"]
    };
  }
};
PickerColumnOption.style = {
  ios: pickerColumnOptionIosCss,
  md: pickerColumnOptionMdCss
};
export {
  PickerColumnOption as ion_picker_column_option
};
/*! Bundled license information:

@ionic/core/dist/esm/ion-picker-column-option.entry.js:
  (*!
   * (C) Ionic http://ionicframework.com - MIT License
   *)
*/
//# sourceMappingURL=ion-picker-column-option.entry-XHUM4KJI.js.map
