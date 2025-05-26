import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { UserRequestsPage } from './user-requests.page';

const routes: Routes = [
  {
    path: '',
    component: UserRequestsPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserRequestsPageRoutingModule {}
