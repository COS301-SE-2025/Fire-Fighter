# TicketService (Angular)

This Angular service provides an interface for interacting with a ticket management API. It allows for creating, retrieving, and updating support or request tickets.

## 📁 File Location

`src/app/services/ticket.service.ts`

---

## ✅ Features

- Create new ticket requests.
- Retrieve all tickets for the current user.
- Get individual ticket details.
- Update the status of a ticket.

---

## 🛠️ Technologies Used

- **Angular**
- **TypeScript**
- **HttpClient** from `@angular/common/http`
- **RxJS** for reactive programming
- **Environment-based API configuration**

---

## 🔗 API Endpoints

All endpoints are prefixed by the `apiUrl` defined in your environment configuration (e.g., `environment.apiUrl`).

| Method | Endpoint              | Description                 |
|--------|-----------------------|-----------------------------|
| POST   | `/tickets`            | Create a new ticket         |
| GET    | `/tickets`            | Get all user tickets        |
| GET    | `/tickets/:id`        | Get a specific ticket       |
| PATCH  | `/tickets/:id`        | Update the status of ticket |

---

## 📦 Interface

```
export interface Ticket {
  id: string;
  status: 'Pending' | 'Approved' | 'Completed' | 'Rejected';
  timeAgo: string;
  reason: string;
  requestDate: string;
  userId: string;
}
```

### Create Ticket Payload
When creating a ticket, you must omit `id`, `status`, and `timeAgo` fields:

```
Omit<Ticket, 'id' | 'status' | 'timeAgo'>
```

---

## 📌 Usage Example

### Import the service

```
import { TicketService } from './services/ticket.service';
```

### Create a Ticket

```
this.ticketService.createTicket({
  reason: 'Request for leave',
  requestDate: '2025-06-03',
  userId: 'user123'
}).subscribe(ticket => {
  console.log('Created ticket:', ticket);
});
```

### Get All Tickets

```
this.ticketService.getTickets().subscribe(tickets => {
  console.log(tickets);
});
```

### Get Ticket by ID

```
this.ticketService.getTicket('ticket123').subscribe(ticket => {
  console.log(ticket);
});
```

### Update Ticket Status

```
this.ticketService.updateTicketStatus('ticket123', 'Approved').subscribe(updated => {
  console.log(updated);
});
```

---

## 🌍 Environment Configuration

Ensure your `environment.ts` (or `environment.prod.ts`) includes:

```
export const environment = {
  production: false,
  apiUrl: 'https://your-api-domain.com/api'
};
```

---

## 🧪 Testing

To unit test this service, use Angular's `HttpTestingController` from `@angular/common/http/testing`.

---

## 📄 License

MIT — free to use for personal and commercial projects.