"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var express = require('express');
var cors = require('cors');
var app = express();
var port = 3001;
//Middleware
app.use(cors());
app.use(express.json());
// Test endpoint
app.get('/test', function (req, res) {
    res.json({ message: 'Server is running!' });
});
app.listen(port, function () {
    console.log("Mock ticket database running on port ".concat(port));
});
