require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const path = require("path");
const cookieParser = require("cookie-parser");

const userRouter = require("./Routers/userRouter");
const checklistRouter = require("./Routers/checklistRouter");
const guideImmigrationRouter = require("./Routers/guideImmigrationRouter");
const documentRouter = require("./Routers/documentRouter");
const forumRouter = require("./Routers/forumRouter");


mongoose.connect(process.env.DB_CONNECTION_ID)
    .then(() => console.log('MongoDB connecte !'))
    .catch(err => console.error('Erreur MongoDB :', err.message));

const app = express();

app.use((req, res, next) => {
    const origin = req.headers.origin;
    // Accepte localhost (web) et toutes les requêtes sans origine (app mobile)
    if (!origin || origin === 'http://localhost:4200' || origin === 'http://localhost:3000') {
        res.setHeader('Access-Control-Allow-Origin', origin || '*');
    } else {
        res.setHeader('Access-Control-Allow-Origin', '*');
    }
    res.setHeader('Access-Control-Allow-Credentials', 'true');
    res.setHeader('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content, Accept, Content-Type, Authorization');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, PATCH, OPTIONS');
    if (req.method === 'OPTIONS') {
        return res.sendStatus(204);
    }
    next();
});

app.use(express.json());
app.use(cookieParser());

app.use("/api/users", userRouter);
app.use("/api/checklist", checklistRouter);
app.use("/api/immigration", guideImmigrationRouter);
app.use("/api/documents", documentRouter);
app.use("/api/forum", forumRouter);


app.use(express.static(path.join(__dirname, '../public')));

module.exports = app;
