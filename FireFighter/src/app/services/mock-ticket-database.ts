import { Ticket } from './ticket.service';

import express from 'express';
import cors from 'cors';

const app = express();
const port = 3001;

//Middleware
app.use(cors());
app.use(express.json());



app.listen(port, () => {


  console.log(`Mock ticket database running on port ${port}`);

})