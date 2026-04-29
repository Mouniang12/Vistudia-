const Checklist = require("../Models/Checklist");
const Destination = require("../Models/Destination");
const UserDestination = require("../Models/UserDestination");
const jwt = require("jsonwebtoken");
const crypto = require("crypto");

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

// Ajouter une destination à l'user
exports.ajouterDestination = async (req, res) => {
  try {
    const userId = getUserId(req);
    if (!userId) return res.status(401).json({ message: "Non authentifié" });

    const { destinationId } = req.body;

    const existe = await UserDestination.findOne({ userId, destinationId });
    if (existe) {
      return res.status(400).json({ message: "Destination déjà ajoutée" });
    }

    const userDest = new UserDestination({ userId, destinationId });
    await userDest.save();

    res.status(201).json({ message: "Destination ajoutée !" });
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

exports.supprimerDestination = async (req, res) => {
  try {
    const userId = getUserId(req);
    if (!userId) return res.status(401).json({ message: "Non authentifié" });

    const { destinationId } = req.params;

    await UserDestination.deleteOne({ userId, destinationId });

    await Checklist.deleteOne({ userId, destinationId });

    res.json({ message: "Destination et checklist supprimées" });
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

// Récupérer les destinations de l'user
exports.getMesDestinations = async (req, res) => {
  try {
    const userId = getUserId(req);
    if (!userId) return res.status(401).json({ message: "Non authentifié" });

    const userDestinations = await UserDestination.find({ userId })
      .populate("destinationId");

    res.json(userDestinations.map(ud => ud.destinationId));
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

// Toutes les destinations disponibles
exports.getDestinations = async (req, res) => {
  try {
    const destinations = await Destination.find();
    res.json(destinations);
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

// Checklist d'une destination
exports.getChecklist = async (req, res) => {
  try {
    const userId = getUserId(req);
    if (!userId) return res.status(401).json({ message: "Non authentifié" });

    const { destinationId } = req.params;

    const destination = await Destination.findById(destinationId);
    if (!destination) {
      return res.status(404).json({ message: "Destination non trouvée" });
    }

    const checklist = await Checklist.findOne({ userId, destinationId });
    const demarchesEffectuees = checklist
      ? checklist.demarchesEffectuees.map(d => d.demarcheId)
      : [];

    const demarches = destination.demarches.map(d => ({
      id: d._id,
      titre: d.titre,
      description: d.description,
      type: d.type,
      faite: demarchesEffectuees.includes(d._id.toString())
    }));

    res.json({
      destination: destination.nom,
      demarches: demarches.filter(d => !d.faite)
    });
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

// Cocher une démarche
exports.cocherDemarche = async (req, res) => {
  try {
    const userId = getUserId(req);
    if (!userId) return res.status(401).json({ message: "Non authentifié" });

    const { destinationId, demarcheId } = req.params;
    const { dateExpiration } = req.body;

    const destination = await Destination.findById(destinationId);
    const demarche = destination.demarches.id(demarcheId);

    if (!demarche) {
      return res.status(404).json({ message: "Démarche non trouvée" });
    }

    let checklist = await Checklist.findOne({ userId, destinationId });
    if (!checklist) {
      checklist = new Checklist({ userId, destinationId, demarchesEffectuees: [] });
    }

    const dejaEffectuee = checklist.demarchesEffectuees
      .some(d => d.demarcheId === demarcheId);

    if (!dejaEffectuee) {
      checklist.demarchesEffectuees.push({
        demarcheId,
        titreDemarche: demarche.titre,
        destinationNom: destination.nom,
        dateEffectuee: new Date(),
        dateExpiration: dateExpiration ? new Date(dateExpiration) : null
      });
      await checklist.save();

      // Auto-créer le document si type document et date fournie
      if (demarche.type === "document" && dateExpiration) {
        const Document = require("../Models/Document");
        const existe = await Document.findOne({
          userId,
          titre: demarche.titre,
          dateExpiration: new Date(dateExpiration)
        });

        if (!existe) {
          const doc = new Document({
            userId,
            titre: demarche.titre,
            description: `${destination.nom} — ${demarche.description}`,
            dateExpiration: new Date(dateExpiration)
          });
          await doc.save();
        }
      }
    }

    res.json({ message: "Démarche cochée !" });
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};  

// Historique
exports.getHistorique = async (req, res) => {
  try {
    const userId = getUserId(req);
    if (!userId) return res.status(401).json({ message: "Non authentifié" });

    const checklists = await Checklist.find({ userId });

    const historique = checklists.flatMap(c =>
      c.demarchesEffectuees.map(d => ({
        titreDemarche: d.titreDemarche,
        destinationNom: d.destinationNom,
        dateEffectuee: d.dateEffectuee,
        dateExpiration: d.dateExpiration
      }))
    ).sort((a, b) => new Date(b.dateEffectuee) - new Date(a.dateEffectuee));

    res.json(historique);
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

// Générer un lien de partage
exports.genererPartage = async (req, res) => {
  try {
    const userId = getUserId(req);
    if (!userId) return res.status(401).json({ message: "Non authentifié" });

    const { destinationId, mode } = req.body;

    if (!["lecture", "edition"].includes(mode)) {
      return res.status(400).json({ message: "Mode invalide" });
    }

    let checklist = await Checklist.findOne({ userId, destinationId });
    if (!checklist) {
      checklist = new Checklist({ userId, destinationId, demarchesEffectuees: [] });
    }

    const token = crypto.randomBytes(32).toString("hex");
    checklist.partageToken = token;
    checklist.modePartage = mode;
    await checklist.save();

    res.json({
      token,
      lien: `${process.env.APP_URL || "http://localhost:3000"}/api/checklist/partage/${token}`,
      mode
    });
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

// Accéder à une checklist partagée
exports.getChecklistPartage = async (req, res) => {
  try {
    const { token } = req.params;

    const checklist = await Checklist.findOne({ partageToken: token })
      .populate("destinationId")
      .populate("userId", "prenom nom");

    if (!checklist) {
      return res.status(404).json({ message: "Lien invalide ou expiré" });
    }

    const destination = checklist.destinationId;
    const demarchesEffectuees = checklist.demarchesEffectuees.map(d => d.demarcheId);

    const demarches = destination.demarches.map(d => ({
      id: d._id,
      titre: d.titre,
      description: d.description,
      type: d.type,
      faite: demarchesEffectuees.includes(d._id.toString())
    }));

    res.json({
      destination: destination.nom,
      proprietaire: `${checklist.userId.prenom} ${checklist.userId.nom}`,
      mode: checklist.modePartage,
      demarches,
      demarchesEffectuees: checklist.demarchesEffectuees
    });
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

// Cocher une démarche via partage en mode édition
exports.cocherDemarchePartage = async (req, res) => {
  try {
    const { token, demarcheId } = req.params;
    const { dateExpiration } = req.body;

    const checklist = await Checklist.findOne({ partageToken: token })
      .populate("destinationId");

    if (!checklist) {
      return res.status(404).json({ message: "Lien invalide" });
    }

    if (checklist.modePartage !== "edition") {
      return res.status(403).json({ message: "Ce lien est en lecture seule" });
    }

    const destination = checklist.destinationId;
    const demarche = destination.demarches.id(demarcheId);

    if (!demarche) {
      return res.status(404).json({ message: "Démarche non trouvée" });
    }

    const dejaEffectuee = checklist.demarchesEffectuees
      .some(d => d.demarcheId === demarcheId);

    if (!dejaEffectuee) {
      checklist.demarchesEffectuees.push({
        demarcheId,
        titreDemarche: demarche.titre,
        destinationNom: destination.nom,
        dateEffectuee: new Date(),
        dateExpiration: dateExpiration ? new Date(dateExpiration) : null
      });
      await checklist.save();
    }

    res.json({ message: "Démarche cochée !" });
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

// Seed
exports.seedDestinations = async (req, res) => {
  try {
    await Destination.deleteMany();

    const destinations = [
      {
        nom: "Canada",
        pays: "Canada",
        demarches: [
          { titre: "Permis d'études", description: "Demande de permis d'études", type: "document" },
          { titre: "Passeport", description: "Vérifier validité du passeport", type: "document" },
          { titre: "Assurance maladie", description: "Souscrire une assurance santé", type: "document" },
          { titre: "Inscription université", description: "Confirmer votre inscription", type: "demarche" },
          { titre: "Logement", description: "Trouver un logement sur place", type: "demarche" },
          { titre: "NAS", description: "Obtenir votre numéro d'assurance sociale", type: "demarche" }
        ]
      },
      {
        nom: "France",
        pays: "France",
        demarches: [
          { titre: "Visa long séjour", description: "Demande de visa via Campus France", type: "document" },
          { titre: "Passeport", description: "Vérifier validité du passeport", type: "document" },
          { titre: "Titre de séjour", description: "Valider votre visa à l'OFII", type: "document" },
          { titre: "Inscription Campus France", description: "Passer par la procédure CEF", type: "demarche" },
          { titre: "Logement CROUS", description: "Faire une demande de logement CROUS", type: "demarche" },
          { titre: "CAF", description: "Faire une demande d'aide au logement", type: "demarche" }
        ]
      },
      {
        nom: "Belgique",
        pays: "Belgique",
        demarches: [
          { titre: "Visa étudiant", description: "Demande de visa type D", type: "document" },
          { titre: "Passeport", description: "Vérifier validité du passeport", type: "document" },
          { titre: "Inscription commune", description: "S'inscrire à la commune", type: "demarche" },
          { titre: "Mutuelle", description: "S'affilier à une mutuelle belge", type: "demarche" },
          { titre: "Transport", description: "Obtenir la carte étudiant STIB/TEC", type: "demarche" }
        ]
      }
    ];

    await Destination.insertMany(destinations);
    res.json({ message: "Destinations créées !" });
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};