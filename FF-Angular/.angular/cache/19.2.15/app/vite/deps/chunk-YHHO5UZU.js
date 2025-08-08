import {
  hapticSelectionChanged,
  hapticSelectionEnd,
  hapticSelectionStart
} from "./chunk-OQILWA36.js";
import {
  createGesture
} from "./chunk-LRRXMVX2.js";
import {
  writeTask
} from "./chunk-G2JG4RXT.js";

// node_modules/@ionic/core/dist/esm/button-active-Bxcnevju.js
var createButtonActiveGesture = (el, isButton) => {
  let currentTouchedButton;
  let initialTouchedButton;
  const activateButtonAtPoint = (x, y, hapticFeedbackFn) => {
    if (typeof document === "undefined") {
      return;
    }
    const target = document.elementFromPoint(x, y);
    if (!target || !isButton(target) || target.disabled) {
      clearActiveButton();
      return;
    }
    if (target !== currentTouchedButton) {
      clearActiveButton();
      setActiveButton(target, hapticFeedbackFn);
    }
  };
  const setActiveButton = (button, hapticFeedbackFn) => {
    currentTouchedButton = button;
    if (!initialTouchedButton) {
      initialTouchedButton = currentTouchedButton;
    }
    const buttonToModify = currentTouchedButton;
    writeTask(() => buttonToModify.classList.add("ion-activated"));
    hapticFeedbackFn();
  };
  const clearActiveButton = (dispatchClick = false) => {
    if (!currentTouchedButton) {
      return;
    }
    const buttonToModify = currentTouchedButton;
    writeTask(() => buttonToModify.classList.remove("ion-activated"));
    if (dispatchClick && initialTouchedButton !== currentTouchedButton) {
      currentTouchedButton.click();
    }
    currentTouchedButton = void 0;
  };
  return createGesture({
    el,
    gestureName: "buttonActiveDrag",
    threshold: 0,
    onStart: (ev) => activateButtonAtPoint(ev.currentX, ev.currentY, hapticSelectionStart),
    onMove: (ev) => activateButtonAtPoint(ev.currentX, ev.currentY, hapticSelectionChanged),
    onEnd: () => {
      clearActiveButton(true);
      hapticSelectionEnd();
      initialTouchedButton = void 0;
    }
  });
};

export {
  createButtonActiveGesture
};
/*! Bundled license information:

@ionic/core/dist/esm/button-active-Bxcnevju.js:
  (*!
   * (C) Ionic http://ionicframework.com - MIT License
   *)
*/
//# sourceMappingURL=chunk-YHHO5UZU.js.map
