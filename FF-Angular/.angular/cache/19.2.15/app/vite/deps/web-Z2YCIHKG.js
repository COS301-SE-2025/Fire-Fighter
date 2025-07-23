import {
  EmailAuthProvider,
  FacebookAuthProvider,
  GithubAuthProvider,
  GoogleAuthProvider,
  OAuthCredential,
  OAuthProvider,
  RecaptchaVerifier,
  TwitterAuthProvider,
  applyActionCode,
  browserLocalPersistence,
  browserSessionPersistence,
  confirmPasswordReset,
  connectAuthEmulator,
  createUserWithEmailAndPassword,
  deleteUser,
  fetchSignInMethodsForEmail,
  getAdditionalUserInfo,
  getAuth,
  getRedirectResult,
  inMemoryPersistence,
  indexedDBLocalPersistence,
  isSignInWithEmailLink,
  linkWithCredential,
  linkWithPhoneNumber,
  linkWithPopup,
  linkWithRedirect,
  reload,
  revokeAccessToken,
  sendEmailVerification,
  sendPasswordResetEmail,
  sendSignInLinkToEmail,
  setPersistence,
  signInAnonymously,
  signInWithCustomToken,
  signInWithEmailAndPassword,
  signInWithEmailLink,
  signInWithPhoneNumber,
  signInWithPopup,
  signInWithRedirect,
  unlink,
  updateEmail,
  updatePassword,
  updateProfile,
  verifyBeforeUpdateEmail
} from "./chunk-7OYTDY67.js";
import "./chunk-4KMPP3WQ.js";
import "./chunk-S3YC363S.js";
import {
  Persistence,
  ProviderId
} from "./chunk-GL7DITFT.js";
import {
  WebPlugin
} from "./chunk-IAKSN5QI.js";
import {
  __async
} from "./chunk-UQIXM5CJ.js";

// node_modules/@capacitor-firebase/authentication/dist/esm/web.js
var FirebaseAuthenticationWeb = class _FirebaseAuthenticationWeb extends WebPlugin {
  constructor() {
    super();
    this.lastConfirmationResult = /* @__PURE__ */ new Map();
    const auth = getAuth();
    auth.onAuthStateChanged((user) => this.handleAuthStateChange(user));
    auth.onIdTokenChanged((user) => void this.handleIdTokenChange(user));
  }
  applyActionCode(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      return applyActionCode(auth, options.oobCode);
    });
  }
  createUserWithEmailAndPassword(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      const userCredential = yield createUserWithEmailAndPassword(auth, options.email, options.password);
      return this.createSignInResult(userCredential, null);
    });
  }
  confirmPasswordReset(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      return confirmPasswordReset(auth, options.oobCode, options.newPassword);
    });
  }
  confirmVerificationCode(options) {
    return __async(this, null, function* () {
      const {
        verificationCode,
        verificationId
      } = options;
      const confirmationResult = this.lastConfirmationResult.get(verificationId);
      if (!confirmationResult) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_CONFIRMATION_RESULT_MISSING);
      }
      const userCredential = yield confirmationResult.confirm(verificationCode);
      return this.createSignInResult(userCredential, null);
    });
  }
  deleteUser() {
    return __async(this, null, function* () {
      const auth = getAuth();
      const currentUser = auth.currentUser;
      if (!currentUser) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_NO_USER_SIGNED_IN);
      }
      return deleteUser(currentUser);
    });
  }
  fetchSignInMethodsForEmail(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      const signInMethods = yield fetchSignInMethodsForEmail(auth, options.email);
      return {
        signInMethods
      };
    });
  }
  getPendingAuthResult() {
    return __async(this, null, function* () {
      this.throwNotAvailableError();
    });
  }
  getCurrentUser() {
    return __async(this, null, function* () {
      const auth = getAuth();
      const userResult = this.createUserResult(auth.currentUser);
      const result = {
        user: userResult
      };
      return result;
    });
  }
  getIdToken(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      if (!auth.currentUser) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_NO_USER_SIGNED_IN);
      }
      const idToken = yield auth.currentUser.getIdToken(options === null || options === void 0 ? void 0 : options.forceRefresh);
      const result = {
        token: idToken || ""
      };
      return result;
    });
  }
  getRedirectResult() {
    return __async(this, null, function* () {
      const auth = getAuth();
      const userCredential = yield getRedirectResult(auth);
      const authCredential = userCredential ? OAuthProvider.credentialFromResult(userCredential) : null;
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  getTenantId() {
    return __async(this, null, function* () {
      const auth = getAuth();
      return {
        tenantId: auth.tenantId
      };
    });
  }
  isSignInWithEmailLink(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      return {
        isSignInWithEmailLink: isSignInWithEmailLink(auth, options.emailLink)
      };
    });
  }
  linkWithApple(options) {
    return __async(this, null, function* () {
      const provider = new OAuthProvider(ProviderId.APPLE);
      this.applySignInOptions(options || {}, provider);
      const userCredential = yield this.linkCurrentUserWithPopupOrRedirect(provider, options === null || options === void 0 ? void 0 : options.mode);
      const authCredential = OAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  linkWithEmailAndPassword(options) {
    return __async(this, null, function* () {
      const authCredential = EmailAuthProvider.credential(options.email, options.password);
      const userCredential = yield this.linkCurrentUserWithCredential(authCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  linkWithEmailLink(options) {
    return __async(this, null, function* () {
      const authCredential = EmailAuthProvider.credentialWithLink(options.email, options.emailLink);
      const userCredential = yield this.linkCurrentUserWithCredential(authCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  linkWithFacebook(options) {
    return __async(this, null, function* () {
      const provider = new FacebookAuthProvider();
      this.applySignInOptions(options || {}, provider);
      const userCredential = yield this.linkCurrentUserWithPopupOrRedirect(provider, options === null || options === void 0 ? void 0 : options.mode);
      const authCredential = FacebookAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  linkWithGameCenter() {
    return __async(this, null, function* () {
      this.throwNotAvailableError();
    });
  }
  linkWithGithub(options) {
    return __async(this, null, function* () {
      const provider = new GithubAuthProvider();
      this.applySignInOptions(options || {}, provider);
      const userCredential = yield this.linkCurrentUserWithPopupOrRedirect(provider, options === null || options === void 0 ? void 0 : options.mode);
      const authCredential = GithubAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  linkWithGoogle(options) {
    return __async(this, null, function* () {
      const provider = new GoogleAuthProvider();
      this.applySignInOptions(options || {}, provider);
      const userCredential = yield this.linkCurrentUserWithPopupOrRedirect(provider, options === null || options === void 0 ? void 0 : options.mode);
      const authCredential = GoogleAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  linkWithMicrosoft(options) {
    return __async(this, null, function* () {
      const provider = new OAuthProvider(ProviderId.MICROSOFT);
      this.applySignInOptions(options || {}, provider);
      const userCredential = yield this.linkCurrentUserWithPopupOrRedirect(provider, options === null || options === void 0 ? void 0 : options.mode);
      const authCredential = OAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  linkWithOpenIdConnect(options) {
    return __async(this, null, function* () {
      const provider = new OAuthProvider(options.providerId);
      this.applySignInOptions(options, provider);
      const userCredential = yield this.linkCurrentUserWithPopupOrRedirect(provider, options.mode);
      const authCredential = OAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  linkWithPhoneNumber(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      const currentUser = auth.currentUser;
      if (!currentUser) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_NO_USER_SIGNED_IN);
      }
      if (!options.phoneNumber) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_PHONE_NUMBER_MISSING);
      }
      if (!options.recaptchaVerifier || !(options.recaptchaVerifier instanceof RecaptchaVerifier)) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_RECAPTCHA_VERIFIER_MISSING);
      }
      try {
        const confirmationResult = yield linkWithPhoneNumber(currentUser, options.phoneNumber, options.recaptchaVerifier);
        const {
          verificationId
        } = confirmationResult;
        this.lastConfirmationResult.set(verificationId, confirmationResult);
        const event = {
          verificationId
        };
        this.notifyListeners(_FirebaseAuthenticationWeb.PHONE_CODE_SENT_EVENT, event);
      } catch (error) {
        const event = {
          message: this.getErrorMessage(error)
        };
        this.notifyListeners(_FirebaseAuthenticationWeb.PHONE_VERIFICATION_FAILED_EVENT, event);
      }
    });
  }
  linkWithPlayGames() {
    return __async(this, null, function* () {
      this.throwNotAvailableError();
    });
  }
  linkWithTwitter(options) {
    return __async(this, null, function* () {
      const provider = new TwitterAuthProvider();
      this.applySignInOptions(options || {}, provider);
      const userCredential = yield this.linkCurrentUserWithPopupOrRedirect(provider, options === null || options === void 0 ? void 0 : options.mode);
      const authCredential = TwitterAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  linkWithYahoo(options) {
    return __async(this, null, function* () {
      const provider = new OAuthProvider(ProviderId.YAHOO);
      this.applySignInOptions(options || {}, provider);
      const userCredential = yield this.linkCurrentUserWithPopupOrRedirect(provider, options === null || options === void 0 ? void 0 : options.mode);
      const authCredential = OAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  reload() {
    return __async(this, null, function* () {
      const auth = getAuth();
      const currentUser = auth.currentUser;
      if (!currentUser) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_NO_USER_SIGNED_IN);
      }
      return reload(currentUser);
    });
  }
  revokeAccessToken(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      return revokeAccessToken(auth, options.token);
    });
  }
  sendEmailVerification(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      const currentUser = auth.currentUser;
      if (!currentUser) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_NO_USER_SIGNED_IN);
      }
      return sendEmailVerification(currentUser, options === null || options === void 0 ? void 0 : options.actionCodeSettings);
    });
  }
  sendPasswordResetEmail(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      return sendPasswordResetEmail(auth, options.email, options.actionCodeSettings);
    });
  }
  sendSignInLinkToEmail(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      return sendSignInLinkToEmail(auth, options.email, options.actionCodeSettings);
    });
  }
  setLanguageCode(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      auth.languageCode = options.languageCode;
    });
  }
  setPersistence(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      switch (options.persistence) {
        case Persistence.BrowserLocal:
          yield setPersistence(auth, browserLocalPersistence);
          break;
        case Persistence.BrowserSession:
          yield setPersistence(auth, browserSessionPersistence);
          break;
        case Persistence.IndexedDbLocal:
          yield setPersistence(auth, indexedDBLocalPersistence);
          break;
        case Persistence.InMemory:
          yield setPersistence(auth, inMemoryPersistence);
          break;
      }
    });
  }
  setTenantId(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      auth.tenantId = options.tenantId;
    });
  }
  signInAnonymously() {
    return __async(this, null, function* () {
      const auth = getAuth();
      const userCredential = yield signInAnonymously(auth);
      return this.createSignInResult(userCredential, null);
    });
  }
  signInWithApple(options) {
    return __async(this, null, function* () {
      const provider = new OAuthProvider(ProviderId.APPLE);
      this.applySignInOptions(options || {}, provider);
      const userCredential = yield this.signInWithPopupOrRedirect(provider, options === null || options === void 0 ? void 0 : options.mode);
      const authCredential = OAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  signInWithCustomToken(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      const userCredential = yield signInWithCustomToken(auth, options.token);
      return this.createSignInResult(userCredential, null);
    });
  }
  signInWithEmailAndPassword(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      const userCredential = yield signInWithEmailAndPassword(auth, options.email, options.password);
      return this.createSignInResult(userCredential, null);
    });
  }
  signInWithEmailLink(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      const userCredential = yield signInWithEmailLink(auth, options.email, options.emailLink);
      return this.createSignInResult(userCredential, null);
    });
  }
  signInWithFacebook(options) {
    return __async(this, null, function* () {
      const provider = new FacebookAuthProvider();
      this.applySignInOptions(options || {}, provider);
      const userCredential = yield this.signInWithPopupOrRedirect(provider, options === null || options === void 0 ? void 0 : options.mode);
      const authCredential = FacebookAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  signInWithGithub(options) {
    return __async(this, null, function* () {
      const provider = new GithubAuthProvider();
      this.applySignInOptions(options || {}, provider);
      const userCredential = yield this.signInWithPopupOrRedirect(provider, options === null || options === void 0 ? void 0 : options.mode);
      const authCredential = GithubAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  signInWithGoogle(options) {
    return __async(this, null, function* () {
      const provider = new GoogleAuthProvider();
      this.applySignInOptions(options || {}, provider);
      const userCredential = yield this.signInWithPopupOrRedirect(provider, options === null || options === void 0 ? void 0 : options.mode);
      const authCredential = GoogleAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  signInWithMicrosoft(options) {
    return __async(this, null, function* () {
      const provider = new OAuthProvider(ProviderId.MICROSOFT);
      this.applySignInOptions(options || {}, provider);
      const userCredential = yield this.signInWithPopupOrRedirect(provider, options === null || options === void 0 ? void 0 : options.mode);
      const authCredential = OAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  signInWithOpenIdConnect(options) {
    return __async(this, null, function* () {
      const provider = new OAuthProvider(options.providerId);
      this.applySignInOptions(options, provider);
      const userCredential = yield this.signInWithPopupOrRedirect(provider, options.mode);
      const authCredential = OAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  signInWithPhoneNumber(options) {
    return __async(this, null, function* () {
      if (!options.phoneNumber) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_PHONE_NUMBER_MISSING);
      }
      if (!options.recaptchaVerifier || !(options.recaptchaVerifier instanceof RecaptchaVerifier)) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_RECAPTCHA_VERIFIER_MISSING);
      }
      const auth = getAuth();
      try {
        const confirmationResult = yield signInWithPhoneNumber(auth, options.phoneNumber, options.recaptchaVerifier);
        const {
          verificationId
        } = confirmationResult;
        this.lastConfirmationResult.set(verificationId, confirmationResult);
        const event = {
          verificationId
        };
        this.notifyListeners(_FirebaseAuthenticationWeb.PHONE_CODE_SENT_EVENT, event);
      } catch (error) {
        const event = {
          message: this.getErrorMessage(error)
        };
        this.notifyListeners(_FirebaseAuthenticationWeb.PHONE_VERIFICATION_FAILED_EVENT, event);
      }
    });
  }
  signInWithPlayGames() {
    return __async(this, null, function* () {
      this.throwNotAvailableError();
    });
  }
  signInWithGameCenter() {
    return __async(this, null, function* () {
      this.throwNotAvailableError();
    });
  }
  signInWithTwitter(options) {
    return __async(this, null, function* () {
      const provider = new TwitterAuthProvider();
      this.applySignInOptions(options || {}, provider);
      const userCredential = yield this.signInWithPopupOrRedirect(provider, options === null || options === void 0 ? void 0 : options.mode);
      const authCredential = TwitterAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  signInWithYahoo(options) {
    return __async(this, null, function* () {
      const provider = new OAuthProvider(ProviderId.YAHOO);
      this.applySignInOptions(options || {}, provider);
      const userCredential = yield this.signInWithPopupOrRedirect(provider, options === null || options === void 0 ? void 0 : options.mode);
      const authCredential = OAuthProvider.credentialFromResult(userCredential);
      return this.createSignInResult(userCredential, authCredential);
    });
  }
  signOut() {
    return __async(this, null, function* () {
      const auth = getAuth();
      yield auth.signOut();
    });
  }
  unlink(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      if (!auth.currentUser) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_NO_USER_SIGNED_IN);
      }
      const user = yield unlink(auth.currentUser, options.providerId);
      const userResult = this.createUserResult(user);
      const result = {
        user: userResult
      };
      return result;
    });
  }
  updateEmail(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      const currentUser = auth.currentUser;
      if (!currentUser) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_NO_USER_SIGNED_IN);
      }
      return updateEmail(currentUser, options.newEmail);
    });
  }
  updatePassword(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      const currentUser = auth.currentUser;
      if (!currentUser) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_NO_USER_SIGNED_IN);
      }
      return updatePassword(currentUser, options.newPassword);
    });
  }
  updateProfile(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      const currentUser = auth.currentUser;
      if (!currentUser) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_NO_USER_SIGNED_IN);
      }
      return updateProfile(currentUser, {
        displayName: options.displayName,
        photoURL: options.photoUrl
      });
    });
  }
  useAppLanguage() {
    return __async(this, null, function* () {
      const auth = getAuth();
      auth.useDeviceLanguage();
    });
  }
  useEmulator(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      const port = options.port || 9099;
      const scheme = options.scheme || "http";
      if (options.host.includes("://")) {
        connectAuthEmulator(auth, `${options.host}:${port}`);
      } else {
        connectAuthEmulator(auth, `${scheme}://${options.host}:${port}`);
      }
    });
  }
  verifyBeforeUpdateEmail(options) {
    return __async(this, null, function* () {
      const auth = getAuth();
      const currentUser = auth.currentUser;
      if (!currentUser) {
        throw new Error(_FirebaseAuthenticationWeb.ERROR_NO_USER_SIGNED_IN);
      }
      return verifyBeforeUpdateEmail(currentUser, options === null || options === void 0 ? void 0 : options.newEmail, options === null || options === void 0 ? void 0 : options.actionCodeSettings);
    });
  }
  handleAuthStateChange(user) {
    const userResult = this.createUserResult(user);
    const change = {
      user: userResult
    };
    this.notifyListeners(_FirebaseAuthenticationWeb.AUTH_STATE_CHANGE_EVENT, change, true);
  }
  handleIdTokenChange(user) {
    return __async(this, null, function* () {
      if (!user) {
        return;
      }
      const idToken = yield user.getIdToken(false);
      const result = {
        token: idToken
      };
      this.notifyListeners(_FirebaseAuthenticationWeb.ID_TOKEN_CHANGE_EVENT, result, true);
    });
  }
  applySignInOptions(options, provider) {
    if (options.customParameters) {
      const customParameters = {};
      options.customParameters.map((parameter) => {
        customParameters[parameter.key] = parameter.value;
      });
      provider.setCustomParameters(customParameters);
    }
    if (options.scopes) {
      for (const scope of options.scopes) {
        provider.addScope(scope);
      }
    }
  }
  signInWithPopupOrRedirect(provider, mode) {
    const auth = getAuth();
    if (mode === "redirect") {
      return signInWithRedirect(auth, provider);
    } else {
      return signInWithPopup(auth, provider);
    }
  }
  linkCurrentUserWithPopupOrRedirect(provider, mode) {
    const auth = getAuth();
    if (!auth.currentUser) {
      throw new Error(_FirebaseAuthenticationWeb.ERROR_NO_USER_SIGNED_IN);
    }
    if (mode === "redirect") {
      return linkWithRedirect(auth.currentUser, provider);
    } else {
      return linkWithPopup(auth.currentUser, provider);
    }
  }
  linkCurrentUserWithCredential(credential) {
    const auth = getAuth();
    if (!auth.currentUser) {
      throw new Error(_FirebaseAuthenticationWeb.ERROR_NO_USER_SIGNED_IN);
    }
    return linkWithCredential(auth.currentUser, credential);
  }
  requestAppTrackingTransparencyPermission() {
    this.throwNotAvailableError();
  }
  checkAppTrackingTransparencyPermission() {
    this.throwNotAvailableError();
  }
  createSignInResult(userCredential, authCredential) {
    const userResult = this.createUserResult((userCredential === null || userCredential === void 0 ? void 0 : userCredential.user) || null);
    const credentialResult = this.createCredentialResult(authCredential);
    const additionalUserInfoResult = this.createAdditionalUserInfoResult(userCredential);
    const result = {
      user: userResult,
      credential: credentialResult,
      additionalUserInfo: additionalUserInfoResult
    };
    return result;
  }
  createCredentialResult(credential) {
    if (!credential) {
      return null;
    }
    const result = {
      providerId: credential.providerId
    };
    if (credential instanceof OAuthCredential) {
      result.accessToken = credential.accessToken;
      result.idToken = credential.idToken;
      result.secret = credential.secret;
    }
    return result;
  }
  createUserResult(user) {
    if (!user) {
      return null;
    }
    const result = {
      displayName: user.displayName,
      email: user.email,
      emailVerified: user.emailVerified,
      isAnonymous: user.isAnonymous,
      metadata: this.createUserMetadataResult(user.metadata),
      phoneNumber: user.phoneNumber,
      photoUrl: user.photoURL,
      providerData: this.createUserProviderDataResult(user.providerData),
      providerId: user.providerId,
      tenantId: user.tenantId,
      uid: user.uid
    };
    return result;
  }
  createUserMetadataResult(metadata) {
    const result = {};
    if (metadata.creationTime) {
      result.creationTime = Date.parse(metadata.creationTime);
    }
    if (metadata.lastSignInTime) {
      result.lastSignInTime = Date.parse(metadata.lastSignInTime);
    }
    return result;
  }
  createUserProviderDataResult(providerData) {
    return providerData.map((data) => ({
      displayName: data.displayName,
      email: data.email,
      phoneNumber: data.phoneNumber,
      photoUrl: data.photoURL,
      providerId: data.providerId,
      uid: data.uid
    }));
  }
  createAdditionalUserInfoResult(credential) {
    if (!credential) {
      return null;
    }
    const additionalUserInfo = getAdditionalUserInfo(credential);
    if (!additionalUserInfo) {
      return null;
    }
    const {
      isNewUser,
      profile,
      providerId,
      username
    } = additionalUserInfo;
    const result = {
      isNewUser
    };
    if (providerId !== null) {
      result.providerId = providerId;
    }
    if (profile !== null) {
      result.profile = profile;
    }
    if (username !== null && username !== void 0) {
      result.username = username;
    }
    return result;
  }
  getErrorMessage(error) {
    if (error instanceof Object && "message" in error && typeof error["message"] === "string") {
      return error["message"];
    }
    return JSON.stringify(error);
  }
  throwNotAvailableError() {
    throw new Error("Not available on web.");
  }
};
FirebaseAuthenticationWeb.AUTH_STATE_CHANGE_EVENT = "authStateChange";
FirebaseAuthenticationWeb.ID_TOKEN_CHANGE_EVENT = "idTokenChange";
FirebaseAuthenticationWeb.PHONE_CODE_SENT_EVENT = "phoneCodeSent";
FirebaseAuthenticationWeb.PHONE_VERIFICATION_FAILED_EVENT = "phoneVerificationFailed";
FirebaseAuthenticationWeb.ERROR_NO_USER_SIGNED_IN = "No user is signed in.";
FirebaseAuthenticationWeb.ERROR_PHONE_NUMBER_MISSING = "phoneNumber must be provided.";
FirebaseAuthenticationWeb.ERROR_RECAPTCHA_VERIFIER_MISSING = "recaptchaVerifier must be provided and must be an instance of RecaptchaVerifier.";
FirebaseAuthenticationWeb.ERROR_CONFIRMATION_RESULT_MISSING = "No confirmation result with this verification id was found.";
export {
  FirebaseAuthenticationWeb
};
//# sourceMappingURL=web-Z2YCIHKG.js.map
