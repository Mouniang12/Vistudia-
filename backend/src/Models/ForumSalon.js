const mongoose = require('mongoose');

const salonSchema = new mongoose.Schema({
    nom: { type: String, required: true },
    description: { type: String },
    dateCreation: { type: Date, default: Date.now }
});

module.exports = mongoose.model('ForumSalon', salonSchema);