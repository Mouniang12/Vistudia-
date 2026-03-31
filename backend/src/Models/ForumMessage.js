const mongoose = require('mongoose');

const messageSchema = new mongoose.Schema({
    salonId: { type: mongoose.Schema.Types.ObjectId, ref: 'ForumSalon', required: true },
    auteur: { type: String, required: true },
    contenu: { type: String, required: true },
    dateEnvoi: { type: Date, default: Date.now }
});

module.exports = mongoose.model('ForumMessage', messageSchema);