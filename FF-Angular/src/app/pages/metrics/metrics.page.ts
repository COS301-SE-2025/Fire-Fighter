import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent, IonRefresher, IonRefresherContent } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  selector: 'app-metrics',
  templateUrl: './metrics.page.html',
  styleUrls: ['./metrics.page.scss'],
  standalone: true,
  imports: [IonContent, IonRefresher, IonRefresherContent, CommonModule, FormsModule, NavbarComponent]
})
export class MetricsPage implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  doRefresh(event: any) {
    // Refresh metrics data here
    setTimeout(() => {
      event.target.complete();
    }, 1000);
  }

}
