const express = require('express');
const cors = require('cors');

const app = express();
const port = 3001;

//Middleware
app.use(cors());
app.use(express.json());

// Test endpoint
app.get('/test', (req: any, res: any) => {
  res.json({ message: 'Server is running!' });
});

app.listen(port, () => {
  console.log(`Mock ticket database running on port ${port}`);
});

export {};