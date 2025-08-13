import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
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
export class MetricsPage implements OnInit, OnDestroy, AfterViewInit {
  private themeObserver?: MutationObserver;
  private lineChart?: ApexCharts;
  private pieChart?: ApexCharts;
  private barChart?: ApexCharts;

  constructor() { }

  private getThemeColors() {
    const isDark = document.documentElement.classList.contains('dark');
    return {
      text: isDark ? '#F9FAFB' : '#111827',
      textSecondary: isDark ? '#9CA3AF' : '#6B7280'
    };
  }

  ngOnInit() {
    // Listen for theme changes
    this.themeObserver = new MutationObserver((mutations) => {
      mutations.forEach((mutation) => {
        if (mutation.type === 'attributes' && mutation.attributeName === 'class') {
          // Theme changed, refresh charts
          setTimeout(() => {
            this.refreshChartsForTheme();
          }, 50);
        }
      });
    });

    this.themeObserver.observe(document.documentElement, {
      attributes: true,
      attributeFilter: ['class']
    });
  }

  ngAfterViewInit() {
    // Initialize all charts after view is fully loaded
    setTimeout(() => {
      this.initializeChart();
      this.initializePieChart();
      this.initializeBarChart();
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
      if (this.lineChart) {
        this.lineChart.destroy();
      }
      this.lineChart = new ApexCharts(chartElement, options);
      this.lineChart.render();
    }
  }

  private initializePieChart() {
    const themeColors = this.getThemeColors();
    
    const getChartOptions = () => {
      return {
        series: [47.1, 47.1, 5.8], // Percentages: Opened (48/102), Closed (48/102), Revoked (6/102)
        colors: ["#1A56DB", "#7E3AF2", "#DC2626"], // Blue, Purple, Red - matching line chart
        chart: {
          height: 420,
          width: "100%",
          type: "pie",
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
            const counts = [48, 48, 6]; // Actual ticket counts
            const value = series[seriesIndex];
            const label = labels[seriesIndex];
            const count = counts[seriesIndex];
            
            let tooltipHTML = `<div class="bg-gray-900 dark:bg-gray-800 text-white dark:text-gray-200 p-3 rounded-lg shadow-lg border border-gray-700">`;
            tooltipHTML += `<div class="font-semibold mb-2">Status Distribution</div>`;
            tooltipHTML += `<div style="color: ${colors[seriesIndex]}">`;
            tooltipHTML += `<span class="inline-block w-2 h-2 rounded-full mr-2" style="background-color: ${colors[seriesIndex]}"></span>`;
            tooltipHTML += `${label}: ${count} tickets (${value.toFixed(1)}%)`;
            tooltipHTML += `</div>`;
            tooltipHTML += `<div class="text-xs text-gray-400 mt-1">Total: 102 tickets</div>`;
            tooltipHTML += `</div>`;
            return tooltipHTML;
          }
        },
        stroke: {
          colors: ["white"],
          lineCap: "",
        },
        plotOptions: {
          pie: {
            labels: {
              show: true,
            },
            size: "100%",
            dataLabels: {
              offset: -25
            }
          },
        },
        labels: ["Opened", "Closed", "Revoked"],
        dataLabels: {
          enabled: true,
          style: {
            fontFamily: "Inter, sans-serif",
          },
        },
        legend: {
          position: "bottom",
          fontFamily: "Inter, sans-serif",
          fontSize: '14px',
          labels: {
            colors: [themeColors.text],
            useSeriesColors: false
          },
          formatter: function(seriesName: string, opts: any) {
            const counts = [48, 48, 6]; // Actual ticket counts
            const count = counts[opts.seriesIndex];
            const percentage = opts.w.globals.series[opts.seriesIndex].toFixed(1);
            return `${seriesName}: ${count} tickets (${percentage}%)`;
          },
          markers: {
            width: 12,
            height: 12,
            radius: 2,
          },
          itemMargin: {
            horizontal: 15,
            vertical: 8
          }
        },
        yaxis: {
          labels: {
            formatter: function (value: any) {
              return value + "%"
            },
          },
        },
        xaxis: {
          labels: {
            formatter: function (value: any) {
              return value  + "%"
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

    const chartElement = document.getElementById("status-pie-chart");
    if (chartElement && typeof ApexCharts !== 'undefined') {
      if (this.pieChart) {
        this.pieChart.destroy();
      }
      this.pieChart = new ApexCharts(chartElement, getChartOptions());
      this.pieChart.render();
    }
  }

  refreshChartsForTheme() {
    // Re-initialize charts when theme changes
    this.initializeChart();
    this.initializePieChart();
    this.initializeBarChart();
  }

  doRefresh(event: any) {
    // Refresh metrics data here
    setTimeout(() => {
      event.target.complete();
    }, 1000);
  }

  private initializeBarChart() {
    const themeColors = this.getThemeColors();
    
    // Mock data for requests by reason chart - matching the provided image
    const options = {
      colors: ["#1A56DB"],
      series: [
        {
          name: "Requests",
          color: "#1A56DB",
          data: [
            { x: "Incident Response", y: 13 },
            { x: "Patch Deployment", y: 11 },
            { x: "Database Access", y: 9 },
            { x: "Account Recovery", y: 7 },
            { x: "Release Hotfix", y: 5 },
            { x: "Other", y: 3 },
          ],
        },
      ],
      chart: {
        type: "bar",
        height: "320px",
        fontFamily: "Inter, sans-serif",
        toolbar: {
          show: false,
        },
      },
      plotOptions: {
        bar: {
          horizontal: false,
          columnWidth: "70%",
          borderRadiusApplication: "end",
          borderRadius: 8,
        },
      },
      tooltip: {
        enabled: true,
        shared: false,
        intersect: false,
        style: {
          fontSize: '12px',
          fontFamily: 'Inter, sans-serif',
        },
        custom: function({series, seriesIndex, dataPointIndex, w}: {series: any, seriesIndex: any, dataPointIndex: any, w: any}) {
          const categories = ['Incident Response', 'Patch Deployment', 'Database Access', 'Account Recovery', 'Release Hotfix', 'Other'];
          const requestType = categories[dataPointIndex];
          const count = series[seriesIndex][dataPointIndex];
          
          let tooltipHTML = `<div class="bg-gray-900 dark:bg-gray-800 text-white dark:text-gray-200 p-3 rounded-lg shadow-lg border border-gray-700">`;
          tooltipHTML += `<div class="font-semibold mb-2">Request Type</div>`;
          tooltipHTML += `<div class="text-blue-400">`;
          tooltipHTML += `<span class="inline-block w-2 h-2 bg-blue-500 rounded-full mr-2"></span>`;
          tooltipHTML += `${requestType}: ${count} requests`;
          tooltipHTML += `</div>`;
          tooltipHTML += `<div class="text-xs text-gray-400 mt-1">This Month</div>`;
          tooltipHTML += `</div>`;
          return tooltipHTML;
        }
      },
      states: {
        hover: {
          filter: {
            type: "darken",
            value: 1,
          },
        },
      },
      stroke: {
        show: true,
        width: 0,
        colors: ["transparent"],
      },
      grid: {
        show: true,
        strokeDashArray: 4,
        borderColor: themeColors.textSecondary,
        padding: {
          left: 2,
          right: 2,
          top: -14
        },
        yaxis: {
          lines: {
            show: true
          }
        },
        xaxis: {
          lines: {
            show: false
          }
        },
      },
      dataLabels: {
        enabled: false,
      },
      legend: {
        show: false,
      },
      xaxis: {
        floating: false,
        labels: {
          show: true,
          style: {
            fontFamily: "Inter, sans-serif",
            fontSize: '11px',
            colors: themeColors.textSecondary
          }
        },
        axisBorder: {
          show: false,
        },
        axisTicks: {
          show: false,
        },
      },
      yaxis: {
        show: true,
        title: {
          text: 'Number of Requests',
          style: {
            fontFamily: "Inter, sans-serif",
            fontSize: '12px',
            fontWeight: 500,
            color: themeColors.textSecondary
          }
        },
        labels: {
          show: true,
          style: {
            fontFamily: "Inter, sans-serif",
            fontSize: '11px',
            colors: themeColors.textSecondary
          }
        },
        axisBorder: {
          show: false,
        },
        axisTicks: {
          show: false,
        },
      },
      fill: {
        opacity: 1,
      },
    };

    const chartElement = document.getElementById('requests-bar-chart');
    if (chartElement && typeof ApexCharts !== 'undefined') {
      // Clear any existing chart
      chartElement.innerHTML = '';
      
      this.barChart = new ApexCharts(chartElement, options);
      this.barChart.render().then(() => {
        console.log('Bar chart rendered successfully');
      }).catch((error: any) => {
        console.error('Error rendering bar chart:', error);
      });
    } else {
      console.error('Bar chart element not found or ApexCharts not loaded');
    }
  }

  ngOnDestroy() {
    // Clean up theme observer
    if (this.themeObserver) {
      this.themeObserver.disconnect();
    }
    
    // Clean up charts
    if (this.lineChart) {
      this.lineChart.destroy();
    }
    if (this.pieChart) {
      this.pieChart.destroy();
    }
    if (this.barChart) {
      this.barChart.destroy();
    }
  }

}