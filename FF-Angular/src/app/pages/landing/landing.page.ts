import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { IonContent } from '@ionic/angular/standalone';
import { ThemeService } from '../../services/theme.service';

@Component({
  selector: 'app-landing',
  templateUrl: './landing.page.html',
  styleUrls: ['./landing.page.scss'],
  standalone: true,
  imports: [IonContent, CommonModule, FormsModule]
})
export class LandingPage implements OnInit {

  constructor(
    private router: Router,
    private themeService: ThemeService
  ) { }

  ngOnInit() {
    // Always set status bar to dark for landing page
    this.themeService.setStatusBarDark();
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
  }

}
