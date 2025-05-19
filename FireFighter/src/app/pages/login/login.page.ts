// src/app/pages/login/login.page.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule, NgClass } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IonContent } from '@ionic/angular/standalone';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgClass,
    IonContent
  ]
})
export class LoginPage implements OnInit {
  loginForm!: FormGroup;
  isSubmitting = false;
  errorMsg: string | null = null;

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    // we'll use this later for validation, but right now it's just for form state
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    // stub: we're not doing real auth yet
    if (this.loginForm.valid) {
      this.isSubmitting = true;
      // Simulate API call
      setTimeout(() => {
        console.log('Would submit', this.loginForm.value);
        this.isSubmitting = false;
      }, 1500);
    }
  }
}