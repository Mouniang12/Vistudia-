const Document = require("../Models/Document");
const jwt = require("jsonwebtoken");

const getUserId = (req) => {
  try {
    const token = req.cookies.token;
    if (!token) return null;
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    return decoded.id;
  } catch (e) {
    return null;
  }
};

exports.getMesDocuments = async (req, res) => {
  try {
    const userId = getUserId(req);
    if (!userId) return res.status(401).json({ message: "Non authentifié" });

    const documents = await Document.find({ userId }).sort({ dateExpiration: 1 });
    res.json(documents);
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur", error: error.message });
  }
};

exports.ajouterDocument = async (req, res) => {
  try {
    const userId = getUserId(req);
    if (!userId) return res.status(401).json({ message: "Non authentifié" });

    const { titre, description, dateExpiration } = req.body;

    if (!titre || !dateExpiration) {
      return res.status(400).json({ message: "Titre et date d'expiration requis" });
    }

    // Éviter les doublons
    const existe = await Document.findOne({
      userId,
      titre,
      dateExpiration: new Date(dateExpiration)
    });
    if (existe) {
      return res.json({ message: "Document déjà existant", document: existe });
    }

    const doc = new Document({
      userId,
      titre,
      description: description || "",
      dateExpiration: new Date(dateExpiration)
    });

    await doc.save();
    res.status(201).json({ message: "Document ajouté !", document: doc });
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur", error: error.message });
  }
};

exports.modifierDocument = async (req, res) => {
  try {
    const userId = getUserId(req);
    if (!userId) return res.status(401).json({ message: "Non authentifié" });

    const { docId } = req.params;
    const { titre, description, dateExpiration } = req.body;

    const doc = await Document.findOneAndUpdate(
      { _id: docId, userId },
      { titre, description, dateExpiration: new Date(dateExpiration) },
      { new: true }
    );

    if (!doc) return res.status(404).json({ message: "Document non trouvé" });
    res.json({ message: "Document modifié !", document: doc });
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur", error: error.message });
  }
};

exports.supprimerDocument = async (req, res) => {
  try {
    const userId = getUserId(req);
    if (!userId) return res.status(401).json({ message: "Non authentifié" });

    const { docId } = req.params;
    await Document.findOneAndDelete({ _id: docId, userId });
    res.json({ message: "Document supprimé" });
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur", error: error.message });
  }
};
