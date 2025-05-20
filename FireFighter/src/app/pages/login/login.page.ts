import { Component, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { IonContent } from '@ionic/angular/standalone';
import { CommonModule } from '@angular/common';

declare const Clerk: any;

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  standalone: true,
  imports: [IonContent, CommonModule],
})
export class LoginPage implements AfterViewInit {
  @ViewChild('signInContainer', { static: true }) signInContainer!: ElementRef<HTMLDivElement>;
  loaded = false;

  async ngAfterViewInit() {
    await Clerk.load();
    this.loaded = true;
    Clerk.mountSignIn(this.signInContainer.nativeElement);
  }
}