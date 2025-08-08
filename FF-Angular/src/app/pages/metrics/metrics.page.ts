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
    // Initialize charts after view is ready
    setTimeout(() => {
      this.initializeChart();
      this.initializeDonutChart();
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

  private initializeDonutChart() {
    const getChartOptions = () => {
      return {
        series: [48, 48, 6], // Opened, Closed, Revoked (matching current month data)
        colors: ["#1A56DB", "#7E3AF2", "#DC2626"], // Blue, Purple, Red - matching line chart
        chart: {
          height: 320,
          width: "100%",
          type: "donut",
          fontFamily: "Inter, sans-serif"
        },
        tooltip: {
          enabled: true,
          style: {
            fontSize: '12px',
            fontFamily: 'Inter, sans-serif',
          },
          custom: function({series, seriesIndex, dataPointIndex, w}: {series: any, seriesIndex: any, dataPointIndex: any, w: any}) {
            const labels = ['Opened', 'Closed', 'Revoked'];
            const colors = ['#1A56DB', '#7E3AF2', '#DC2626'];
            const value = series[seriesIndex];
            const label = labels[seriesIndex];
            const total = series.reduce((sum: number, val: number) => sum + val, 0);
            const percentage = ((value / total) * 100).toFixed(1);
            
            let tooltipHTML = `<div class="bg-gray-900 dark:bg-gray-800 text-white dark:text-gray-200 p-3 rounded-lg shadow-lg border border-gray-700">`;
            tooltipHTML += `<div class="font-semibold mb-2">Status Distribution</div>`;
            tooltipHTML += `<div style="color: ${colors[seriesIndex]}">`;
            tooltipHTML += `<span class="inline-block w-2 h-2 rounded-full mr-2" style="background-color: ${colors[seriesIndex]}"></span>`;
            tooltipHTML += `${label}: ${value} (${percentage}%)`;
            tooltipHTML += `</div>`;
            tooltipHTML += `<div class="text-xs text-gray-400 mt-1">Total: ${total} tickets</div>`;
            tooltipHTML += `</div>`;
            return tooltipHTML;
          }
        },
        stroke: {
          colors: ["transparent"],
          lineCap: "",
        },
        plotOptions: {
          pie: {
            donut: {
              labels: {
                show: true,
                name: {
                  show: true,
                  fontFamily: "Inter, sans-serif",
                  offsetY: 20,
                },
                total: {
                  showAlways: true,
                  show: true,
                  label: "Total tickets",
                  fontFamily: "Inter, sans-serif",
                  formatter: function (w: any) {
                    const sum = w.globals.seriesTotals.reduce((a: any, b: any) => {
                      return a + b
                    }, 0)
                    return sum.toString()
                  },
                },
                value: {
                  show: true,
                  fontFamily: "Inter, sans-serif",
                  offsetY: -20,
                  formatter: function (value: any) {
                    return value.toString()
                  },
                },
              },
              size: "80%",
            },
          },
        },
        grid: {
          padding: {
            top: -2,
          },
        },
        labels: ["Opened", "Closed", "Revoked"],
        dataLabels: {
          enabled: false,
        },
        legend: {
          position: "bottom",
          fontFamily: "Inter, sans-serif",
        },
        yaxis: {
          labels: {
            formatter: function (value: any) {
              return value.toString()
            },
          },
        },
        xaxis: {
          labels: {
            formatter: function (value: any) {
              return value.toString()
            },
          },
          axisTicks: {
            show: false,
          },
          axisBorder: {
            show: false,
          },
        },
      }
    }

    const chartElement = document.getElementById("status-donut-chart");
    if (chartElement && typeof ApexCharts !== 'undefined') {
      const chart = new ApexCharts(chartElement, getChartOptions());
      chart.render();

      // Get all the checkboxes by their class name
      const checkboxes = document.querySelectorAll('#status-filters input[type="checkbox"]');

      // Function to handle the checkbox change event
      const handleCheckboxChange = (event: any, chart: any) => {
        const checkbox = event.target;
        const checkedBoxes = document.querySelectorAll('#status-filters input[type="checkbox"]:checked');
        
        if (checkedBoxes.length === 0) {
          // If no checkboxes are checked, show all data
          chart.updateSeries([48, 48, 6]);
        } else {
          // Show only selected data
          let series = [0, 0, 0];
          checkedBoxes.forEach((cb: any) => {
            switch(cb.value) {
              case 'opened':
                series[0] = 48;
                break;
              case 'closed':
                series[1] = 48;
                break;
              case 'revoked':
                series[2] = 6;
                break;
            }
          });
          chart.updateSeries(series);
        }
      }

      // Attach the event listener to each checkbox
      checkboxes.forEach((checkbox) => {
        checkbox.addEventListener('change', (event) => handleCheckboxChange(event, chart));
      });
    }
  }

  doRefresh(event: any) {
    // Refresh metrics data here
    setTimeout(() => {
      event.target.complete();
    }, 1000);
  }

}