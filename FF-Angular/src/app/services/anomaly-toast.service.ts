import { Injectable } from '@angular/core';
import { ToastController } from '@ionic/angular';

// Anomaly type messages
const ANOMALY_MESSAGES: { [key: string]: { title: string; message: string } } = {
  'FREQUENT_REQUESTS': {
    title: '⚡ Activity Anomaly Detected',
    message: 'Excessive request frequency detected on your profile. Administrator has been notified. Please contact them to avoid miscommunication.'
  },
  'DORMANT_USER_ACTIVITY': {
    title: '🔄 Account Anomaly Detected',
    message: 'Dormant account sudden activity detected on your profile. Administrator has been notified. Please contact them to avoid miscommunication.'
  },
  'OFF_HOURS_ACTIVITY': {
    title: '⏰ Time Anomaly Detected',
    message: 'Off-hours system access detected on your profile. Administrator has been notified. Please contact them to avoid miscommunication.'
  },
  'MULTIPLE_LOCATION_ACCESS': {
    title: '🚨 Location Anomaly Detected',
    message: 'Multiple location access detected on your profile. Administrator has been notified. Please contact them to avoid miscommunication.'
  },
  'UNUSUAL_WORKING_HOURS': {
    title: '⏰ Time Anomaly Detected',
    message: 'Unusual working hours detected on your profile. Administrator has been notified. Please contact them to avoid miscommunication.'
  },
  'RAPID_SUCCESSIVE_LOGINS': {
    title: '🔄 Login Anomaly Detected',
    message: 'Rapid successive logins detected on your profile. Administrator has been notified. Please contact them to avoid miscommunication.'
  },
  'SUSPICIOUS_IP_ACCESS': {
    title: '🌐 IP Anomaly Detected',
    message: 'Suspicious IP access detected on your profile. Administrator has been notified. Please contact them to avoid miscommunication.'
  },
  'UNUSUAL_DEVICE_ACCESS': {
    title: '📱 Device Anomaly Detected',
    message: 'Unusual device access detected on your profile. Administrator has been notified. Please contact them to avoid miscommunication.'
  },
  'HIGH_FREQUENCY_ACTIONS': {
    title: '⚡ Activity Anomaly Detected',
    message: 'High frequency actions detected on your profile. Administrator has been notified. Please contact them to avoid miscommunication.'
  }
};

// Group change messages
const GROUP_CHANGE_MESSAGES: { [key: string]: { title: string; message: string } } = {
  'HIGH': {
    title: '🔐 Security Level Upgraded',
    message: 'You can now access highly sensitive data. Administrator has been notified of this change.'
  },
  'MEDIUM': {
    title: '🔒 Security Level Updated',
    message: 'You can now access sensitive data. Administrator has been notified of this change.'
  },
  'LOW': {
    title: '📋 Access Level Updated',
    message: 'Your access level has been updated. Administrator has been notified of this change.'
  }
};

@Injectable({
  providedIn: 'root'
})
export class AnomalyToastService {

  constructor(private toastController: ToastController) {}

  async showAnomalyToast(anomalyType: string): Promise<void> {
    const anomalyInfo = ANOMALY_MESSAGES[anomalyType];
    
    const message = anomalyInfo ? anomalyInfo.message : 
      'Anomalous behavior detected on your profile. Administrator has been notified. Please contact them to avoid miscommunication.';
    
    const header = anomalyInfo ? anomalyInfo.title : '🚨 Anomaly Detected';

    const toast = await this.toastController.create({
      header: header,
      message: message,
      duration: 8000,
      position: 'top',
      color: 'warning',
      buttons: [
        {
          text: 'Dismiss',
          role: 'cancel'
        }
      ],
      cssClass: 'anomaly-toast'
    });

    await toast.present();
  }

  async showGroupChangeToast(securityLevel: string): Promise<void> {
    const groupInfo = GROUP_CHANGE_MESSAGES[securityLevel] || GROUP_CHANGE_MESSAGES['LOW'];
    
    let color: string;
    let cssClass: string;
    
    switch (securityLevel) {
      case 'HIGH':
        color = 'danger';
        cssClass = 'group-change-toast high-security';
        break;
      case 'MEDIUM':
        color = 'warning';
        cssClass = 'group-change-toast medium-security';
        break;
      default:
        color = 'success';
        cssClass = 'group-change-toast low-security';
        break;
    }

    const toast = await this.toastController.create({
      header: groupInfo.title,
      message: groupInfo.message,
      duration: 6000,
      position: 'top',
      color: color,
      buttons: [
        {
          text: 'OK',
          role: 'cancel'
        }
      ],
      cssClass: cssClass
    });

    await toast.present();
  }
}