const ForumSalon = require('../Models/ForumSalon');
const ForumMessage = require('../Models/ForumMessage');

exports.getSalons = async (req, res) => {
    try {
        const salons = await ForumSalon.find();
        res.status(200).json(salons);
    } catch (err) { res.status(500).json({ error: err.message }); }
};

exports.createSalon = async (req, res) => {
    try {
        const nouveauSalon = new ForumSalon(req.body);
        await nouveauSalon.save();
        res.status(201).json(nouveauSalon);
    } catch (err) { res.status(400).json({ error: err.message }); }
};

exports.getMessages = async (req, res) => {
    try {
        const messages = await ForumMessage.find({ salonId: req.params.salonId }).sort({ dateEnvoi: 1 });
        res.status(200).json(messages);
    } catch (err) { res.status(500).json({ error: err.message }); }
};

exports.postMessage = async (req, res) => {
    try {
        const message = new ForumMessage({
            salonId: req.params.salonId,
            auteur: req.body.auteur || "Invité",
            contenu: req.body.contenu
        });
        await message.save();
        res.status(201).json(message);
    } catch (err) { res.status(400).json({ error: err.message }); }
};