import { Component, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonApp, IonRouterOutlet } from '@ionic/angular/standalone';

declare const Clerk: any;

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ CommonModule, IonApp, IonRouterOutlet ],
  templateUrl: 'app.component.html',
})
export class AppComponent implements AfterViewInit {
  @ViewChild('signInContainer', { static: true })
  signInContainer!: ElementRef<HTMLDivElement>;

  @ViewChild('userBtnContainer', { static: true })
  userBtnContainer!: ElementRef<HTMLDivElement>;

  loaded = false;
  hasUser = false;

  async ngAfterViewInit() {
    await Clerk.load();      // initialize Clerk
    this.loaded = true;      // un-hide the containers

    if (Clerk.user) {
      this.hasUser = true;
      Clerk.mountUserButton(this.userBtnContainer.nativeElement);
    } else {
      Clerk.mountSignIn(this.signInContainer.nativeElement);
    }
  }
}