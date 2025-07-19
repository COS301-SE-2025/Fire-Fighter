import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { VersionService } from '../../services/version.service';
import { trigger, state, style, transition, animate } from '@angular/animations';

@Component({
  selector: 'app-help',
  templateUrl: './help.page.html',
  styleUrls: ['./help.page.scss'],
  standalone: true,
  imports: [IonContent, CommonModule, FormsModule, NavbarComponent],
  animations: [
    trigger('modalBackdrop', [
      state('hidden', style({
        opacity: 0
      })),
      state('visible', style({
        opacity: 1
      })),
      transition('hidden => visible', [
        animate('200ms ease-out')
      ]),
      transition('visible => hidden', [
        animate('150ms ease-in')
      ])
    ]),
    trigger('modalPanel', [
      state('hidden', style({
        opacity: 0,
        transform: 'scale(0.95) translateY(-10px)'
      })),
      state('visible', style({
        opacity: 1,
        transform: 'scale(1) translateY(0)'
      })),
      transition('hidden => visible', [
        animate('250ms cubic-bezier(0.34, 1.56, 0.64, 1)')
      ]),
      transition('visible => hidden', [
        animate('200ms cubic-bezier(0.25, 0.46, 0.45, 0.94)')
      ])
    ])
  ]
})
export class HelpPage implements OnInit {

  appVersion: string = '';
  isReleaseNotesModalOpen: boolean = false;
  modalAnimationState: string = 'hidden';

  constructor(private versionService: VersionService) { }

  ngOnInit() {
    this.appVersion = this.versionService.getFormattedVersion();
  }

  openReleaseNotesModal() {
    this.isReleaseNotesModalOpen = true;
    // Small delay to ensure the modal is rendered before animating
    setTimeout(() => {
      this.modalAnimationState = 'visible';
    }, 10);
  }

  closeReleaseNotesModal() {
    this.modalAnimationState = 'hidden';
    // Wait for animation to complete before hiding the modal
    setTimeout(() => {
      this.isReleaseNotesModalOpen = false;
    }, 200);
  }

}
