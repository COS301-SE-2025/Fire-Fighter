import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent, IonRefresher, IonRefresherContent } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import ApexCharts from 'apexcharts';

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
    // Initialize chart after view is ready
    setTimeout(() => {
      this.initializeChart();
    }, 100);
  }

  private initializeChart() {
    const options = {
      chart: {
        height: 350,
        width: "100%",
        type: "line",
        fontFamily: "Inter, sans-serif",
        dropShadow: {
          enabled: false,
        },
        toolbar: {
          show: false,
        },
        background: 'transparent'
      },
      tooltip: {
        enabled: true,
        shared: true,
        intersect: false,
        style: {
          fontSize: '12px',
          fontFamily: 'Inter, sans-serif',
        },
        theme: 'dark',
        custom: function({series, seriesIndex, dataPointIndex, w}: {series: any, seriesIndex: any, dataPointIndex: any, w: any}) {
          const categories = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
          const month = categories[dataPointIndex];
          let tooltipHTML = `<div class="bg-gray-900 dark:bg-gray-800 text-white dark:text-gray-200 p-3 rounded-lg shadow-lg border border-gray-700">`;
          tooltipHTML += `<div class="font-semibold mb-2">${month}</div>`;
          tooltipHTML += `<div class="text-blue-400"><span class="inline-block w-2 h-2 bg-blue-500 rounded-full mr-2"></span>Opened: ${series[0][dataPointIndex]}</div>`;
          tooltipHTML += `<div class="text-purple-400"><span class="inline-block w-2 h-2 bg-purple-500 rounded-full mr-2"></span>Closed: ${series[1][dataPointIndex]}</div>`;
          tooltipHTML += `<div class="text-red-400"><span class="inline-block w-2 h-2 bg-red-500 rounded-full mr-2"></span>Revoked: ${series[2][dataPointIndex]}</div>`;
          tooltipHTML += `</div>`;
          return tooltipHTML;
        }
      },
      dataLabels: {
        enabled: false,
      },
      stroke: {
        width: 3,
        curve: 'smooth'
      },
      grid: {
        show: true,
        strokeDashArray: 4,
        borderColor: '#374151',
        padding: {
          left: 10,
          right: 10,
          top: -26
        },
      },
      series: [
        {
          name: "Tickets Opened",
          data: [75, 64, 68, 72, 58, 65, 48, 52, 60, 58, 66, 48],
          color: "#1A56DB",
        },
        {
          name: "Tickets Closed", 
          data: [42, 57, 45, 38, 52, 41, 48, 45, 38, 47, 52, 48],
          color: "#7E3AF2",
        },
        {
          name: "Tickets Revoked",
          data: [8, 12, 6, 4, 9, 7, 0, 3, 5, 2, 8, 0],
          color: "#DC2626",
        },
      ],
      legend: {
        show: false
      },
      xaxis: {
        categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
        labels: {
          show: true,
          style: {
            fontFamily: "Inter, sans-serif",
            colors: '#6B7280',
            fontSize: '12px'
          }
        },
        axisBorder: {
          show: false,
        },
        axisTicks: {
          show: false,
        },
        crosshairs: {
          show: true,
          position: 'back',
          stroke: {
            color: '#374151',
            width: 1,
            dashArray: 3,
          },
        },
        tooltip: {
          enabled: false,
        },
      },
      yaxis: {
        show: false,
        crosshairs: {
          show: true,
          position: 'back',
          stroke: {
            color: '#374151',
            width: 1,
            dashArray: 3,
          },
        },
      },
      responsive: [{
        breakpoint: 768,
        options: {
          chart: {
            height: 300
          }
        }
      }]
    };

    const chartElement = document.getElementById("tickets-trend-chart");
    if (chartElement) {
      const chart = new ApexCharts(chartElement, options);
      chart.render();
    }
  }

  doRefresh(event: any) {
    // Refresh metrics data here
    setTimeout(() => {
      event.target.complete();
    }, 1000);
  }

}