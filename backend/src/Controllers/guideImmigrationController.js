const GuideImmigration = require("../Models/GuideImmigration");

// Récupérer tous les pays d'origine disponibles
exports.getPaysOrigine = async (req, res) => {
  try {
    const pays = await GuideImmigration.distinct("paysOrigine");
    res.json(pays);
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

// Récupérer les destinations disponibles pour un pays d'origine
exports.getDestinations = async (req, res) => {
  try {
    const { paysOrigine } = req.params;
    const destinations = await GuideImmigration.find(
      { paysOrigine },
      "paysDestination titre description dureeTotal coutTotal"
    );
    res.json(destinations);
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

// Récupérer le guide détaillé
exports.getGuide = async (req, res) => {
  try {
    const { guideId } = req.params;
    const guide = await GuideImmigration.findById(guideId);
    if (!guide) return res.status(404).json({ message: "Guide non trouvé" });
    res.json(guide);
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

// Seed
exports.seedGuides = async (req, res) => {
  try {
    await GuideImmigration.deleteMany();

    const guides = [
      {
        paysOrigine: "Sénégal",
        paysDestination: "Canada",
        titre: "Guide immigration Sénégal → Canada",
        description: "Guide complet pour les étudiants sénégalais souhaitant étudier au Canada.",
        dureeTotal: "3 à 6 mois",
        coutTotal: "500$ à 1500$ CAD",
        etapes: [
          {
            ordre: 1,
            emoji: "🎓",
            titre: "Obtenir une lettre d'admission",
            description: "Faites une demande d'admission dans une université canadienne reconnue. Une fois accepté, vous recevrez une lettre d'admission officielle nécessaire pour votre demande de visa.",
            duree: "1 à 6 mois",
            cout: "100$ à 200$ CAD (frais de candidature)",
            documents: [
              "Relevés de notes officiels",
              "Diplômes certifiés",
              "Lettre de motivation",
              "Lettres de recommandation",
              "Preuve de niveau de langue (IELTS/TOEFL ou DELF/DALF)"
            ]
          },
          {
            ordre: 2,
            emoji: "📋",
            titre: "Préparer le dossier de permis d'études",
            description: "Rassemblez tous les documents nécessaires pour votre demande de permis d'études auprès d'Immigration, Réfugiés et Citoyenneté Canada (IRCC).",
            duree: "2 à 4 semaines",
            cout: "Gratuit",
            documents: [
              "Lettre d'admission de l'université",
              "Passeport valide (minimum 1 an)",
              "Photos passeport récentes",
              "Preuve de ressources financières (10 000$ CAD minimum)",
              "Relevés bancaires des 6 derniers mois",
              "Extrait de casier judiciaire",
              "Certificat médical (si requis)"
            ]
          },
          {
            ordre: 3,
            emoji: "💻",
            titre: "Soumettre la demande de permis d'études",
            description: "Soumettez votre demande en ligne sur le portail IRCC ou en personne au Centre de Réception des Demandes de Visa (CRDV) de Dakar.",
            duree: "8 à 16 semaines",
            cout: "150$ CAD",
            documents: [
              "Formulaire IMM 1294 complété",
              "Formulaire IMM 5645 (informations familiales)",
              "Tous les documents préparés à l'étape 2"
            ]
          },
          {
            ordre: 4,
            emoji: "🏥",
            titre: "Examen médical",
            description: "Passez un examen médical auprès d'un médecin désigné par IRCC au Sénégal. Cet examen est souvent requis pour les ressortissants sénégalais.",
            duree: "1 à 2 semaines",
            cout: "150$ à 300$ USD",
            documents: [
              "Convocation médicale d'IRCC",
              "Passeport",
              "Photos passeport"
            ]
          },
          {
            ordre: 5,
            emoji: "✅",
            titre: "Recevoir le permis d'études",
            description: "Si votre demande est approuvée, vous recevrez une lettre d'introduction (LOI). Le permis d'études officiel sera émis à votre arrivée au Canada à la frontière.",
            duree: "Variable",
            cout: "Gratuit",
            documents: [
              "Lettre d'introduction (LOI)",
              "Passeport avec visa (si requis)"
            ]
          },
          {
            ordre: 6,
            emoji: "✈️",
            titre: "Préparer le voyage",
            description: "Réservez vos billets d'avion, trouvez un logement temporaire à l'arrivée et préparez vos effets personnels pour votre nouvelle vie au Canada.",
            duree: "2 à 4 semaines",
            cout: "800$ à 2000$ CAD (billet aller)",
            documents: [
              "Billet d'avion",
              "Preuve de logement temporaire",
              "Argent liquide pour les premiers jours"
            ]
          },
          {
            ordre: 7,
            emoji: "🛬",
            titre: "Arrivée au Canada",
            description: "À votre arrivée, présentez-vous à l'agent des services frontaliers qui émettra votre permis d'études officiel. Conservez précieusement ce document.",
            duree: "1 jour",
            cout: "Gratuit",
            documents: [
              "Lettre d'introduction (LOI)",
              "Passeport",
              "Lettre d'admission universitaire",
              "Preuve de ressources financières"
            ]
          },
          {
            ordre: 8,
            emoji: "🏦",
            titre: "Démarches à l'arrivée",
            description: "Une fois installé, effectuez les démarches essentielles : ouverture d'un compte bancaire, obtention du NAS, inscription à l'assurance maladie provinciale.",
            duree: "2 à 4 semaines",
            cout: "Variable",
            documents: [
              "Permis d'études",
              "Passeport",
              "Preuve d'adresse au Canada",
              "Lettre d'admission universitaire"
            ]
          }
        ]
      },
      {
        paysOrigine: "Sénégal",
        paysDestination: "France",
        titre: "Guide immigration Sénégal → France",
        description: "Guide complet pour les étudiants sénégalais souhaitant étudier en France via Campus France.",
        dureeTotal: "4 à 8 mois",
        coutTotal: "200€ à 500€",
        etapes: [
          {
            ordre: 1,
            emoji: "🎓",
            titre: "S'inscrire sur Campus France",
            description: "Créez un dossier sur la plateforme Etudes en France (EEF) gérée par Campus France Sénégal à Dakar. Cette étape est obligatoire pour tous les étudiants sénégalais souhaitant étudier en France.",
            duree: "2 à 4 semaines",
            cout: "100€",
            documents: [
              "Relevés de notes du Baccalauréat",
              "Relevés de notes universitaires",
              "CV",
              "Lettre de motivation",
              "Copies de diplômes"
            ]
          },
          {
            ordre: 2,
            emoji: "🏫",
            titre: "Candidater aux universités françaises",
            description: "Postulez aux établissements français via la plateforme Parcoursup (licence) ou directement auprès des universités (master/doctorat). Obtenez une ou plusieurs admissions.",
            duree: "1 à 3 mois",
            cout: "Gratuit à 200€",
            documents: [
              "Dossier Campus France validé",
              "Relevés de notes traduits",
              "Lettres de recommandation",
              "Projet de motivation"
            ]
          },
          {
            ordre: 3,
            emoji: "🤝",
            titre: "Entretien Campus France",
            description: "Passez un entretien au bureau Campus France de Dakar. Cet entretien évalue votre projet d'études et votre motivation. Préparez-le soigneusement.",
            duree: "1 jour",
            cout: "Inclus dans les frais Campus France",
            documents: [
              "Convocation à l'entretien",
              "Dossier Campus France complet",
              "Lettre d'admission française"
            ]
          },
          {
            ordre: 4,
            emoji: "📝",
            titre: "Demande de visa étudiant long séjour",
            description: "Après validation de Campus France, déposez votre demande de visa étudiant (visa D) auprès du Consulat de France à Dakar ou via VFS Global.",
            duree: "3 à 8 semaines",
            cout: "99€",
            documents: [
              "Formulaire de demande de visa",
              "Passeport valide",
              "Photos d'identité",
              "Attestation Campus France",
              "Lettre d'admission université française",
              "Justificatif de ressources (615€/mois minimum)",
              "Justificatif de logement en France",
              "Assurance maladie"
            ]
          },
          {
            ordre: 5,
            emoji: "✈️",
            titre: "Préparer le départ",
            description: "Réservez votre billet d'avion, confirmez votre logement (résidence CROUS, foyer étudiant ou appartement privé) et préparez vos affaires.",
            duree: "2 à 4 semaines",
            cout: "400€ à 1200€ (billet aller)",
            documents: [
              "Visa étudiant",
              "Billet d'avion",
              "Confirmation de logement"
            ]
          },
          {
            ordre: 6,
            emoji: "🛬",
            titre: "Arrivée en France",
            description: "À votre arrivée, vous devez valider votre visa long séjour valant titre de séjour (VLS-TS) sur le site de l'OFII dans les 3 mois suivant votre arrivée.",
            duree: "3 mois maximum",
            cout: "200€ (taxe OFII)",
            documents: [
              "Passeport avec visa",
              "Formulaire OFII",
              "Justificatif de domicile en France"
            ]
          },
          {
            ordre: 7,
            emoji: "🏥",
            titre: "S'inscrire à la Sécurité Sociale",
            description: "Inscrivez-vous à la Sécurité Sociale étudiante via votre université pour bénéficier de la couverture maladie. Demandez également votre carte Vitale.",
            duree: "2 à 6 semaines",
            cout: "Gratuit",
            documents: [
              "Carte d'étudiant",
              "Justificatif de domicile",
              "RIB (relevé d'identité bancaire)",
              "Passeport"
            ]
          },
          {
            ordre: 8,
            emoji: "💰",
            titre: "Demande CAF",
            description: "Faites une demande d'Aide Personnalisée au Logement (APL) auprès de la CAF pour réduire votre loyer. Cette aide peut représenter plusieurs centaines d'euros par mois.",
            duree: "1 à 2 mois",
            cout: "Gratuit",
            documents: [
              "Contrat de location",
              "RIB",
              "Carte d'étudiant",
              "Avis d'imposition ou attestation de ressources"
            ]
          }
        ]
      },
      {
        paysOrigine: "Maroc",
        paysDestination: "Canada",
        titre: "Guide immigration Maroc → Canada",
        description: "Guide complet pour les étudiants marocains souhaitant étudier au Canada.",
        dureeTotal: "3 à 5 mois",
        coutTotal: "600$ à 1800$ CAD",
        etapes: [
          {
            ordre: 1,
            emoji: "🎓",
            titre: "Obtenir une admission universitaire",
            description: "Postulez dans des universités canadiennes reconnues. Le Québec est très populaire pour les étudiants francophones marocains.",
            duree: "1 à 4 mois",
            cout: "100$ à 250$ CAD",
            documents: [
              "Relevés de notes traduits et apostillés",
              "Diplômes certifiés",
              "Lettre de motivation",
              "CV académique",
              "DELF/DALF ou IELTS selon la langue d'enseignement"
            ]
          },
          {
            ordre: 2,
            emoji: "📋",
            titre: "Demande de permis d'études",
            description: "Soumettez votre demande de permis d'études en ligne sur le portail IRCC avec tous les documents requis.",
            duree: "8 à 14 semaines",
            cout: "150$ CAD",
            documents: [
              "Lettre d'admission",
              "Passeport valide",
              "Preuve financière (10 000$ CAD)",
              "Relevés bancaires",
              "Extrait de casier judiciaire",
              "Photos passeport"
            ]
          },
          {
            ordre: 3,
            emoji: "🏥",
            titre: "Examen médical",
            description: "Passez un examen médical auprès d'un médecin désigné par IRCC au Maroc.",
            duree: "1 semaine",
            cout: "150$ à 250$ USD",
            documents: ["Convocation médicale", "Passeport", "Photos"]
          },
          {
            ordre: 4,
            emoji: "✈️",
            titre: "Voyage et arrivée",
            description: "Préparez votre voyage et effectuez les formalités d'entrée au Canada.",
            duree: "1 à 2 semaines",
            cout: "600$ à 1500$ CAD",
            documents: [
              "LOI (Lettre d'Introduction)",
              "Passeport",
              "Lettre d'admission"
            ]
          }
        ]
      },
      {
        paysOrigine: "Côte d'Ivoire",
        paysDestination: "France",
        titre: "Guide immigration Côte d'Ivoire → France",
        description: "Guide pour les étudiants ivoiriens souhaitant poursuivre leurs études en France.",
        dureeTotal: "4 à 7 mois",
        coutTotal: "300€ à 600€",
        etapes: [
          {
            ordre: 1,
            emoji: "🎓",
            titre: "Inscription sur Campus France",
            description: "Créez votre dossier sur la plateforme Etudes en France via Campus France Côte d'Ivoire à Abidjan.",
            duree: "3 à 5 semaines",
            cout: "100€",
            documents: [
              "Relevés de notes",
              "Diplômes",
              "CV",
              "Lettre de motivation"
            ]
          },
          {
            ordre: 2,
            emoji: "🏫",
            titre: "Candidature et admission",
            description: "Postulez aux établissements français et obtenez une lettre d'admission.",
            duree: "1 à 3 mois",
            cout: "Gratuit à 150€",
            documents: [
              "Dossier Campus France",
              "Relevés de notes traduits",
              "Lettres de recommandation"
            ]
          },
          {
            ordre: 3,
            emoji: "🤝",
            titre: "Entretien Campus France",
            description: "Passez l'entretien de motivation au bureau Campus France d'Abidjan.",
            duree: "1 jour",
            cout: "Inclus",
            documents: ["Dossier complet", "Admission française"]
          },
          {
            ordre: 4,
            emoji: "📝",
            titre: "Visa étudiant",
            description: "Déposez votre demande de visa long séjour étudiant au Consulat de France à Abidjan.",
            duree: "3 à 6 semaines",
            cout: "99€",
            documents: [
              "Formulaire visa",
              "Passeport",
              "Attestation Campus France",
              "Admission université",
              "Justificatifs financiers",
              "Logement en France"
            ]
          },
          {
            ordre: 5,
            emoji: "🛬",
            titre: "Arrivée et validation OFII",
            description: "Validez votre visa à l'OFII dans les 3 mois suivant votre arrivée en France.",
            duree: "3 mois maximum",
            cout: "200€",
            documents: ["Passeport", "Formulaire OFII", "Justificatif domicile"]
          }
        ]
      }
    ];

    await GuideImmigration.insertMany(guides);
    res.json({ message: `${guides.length} guides créés !` });

  } catch (error) {
    res.status(500).json({ message: "Erreur serveur", error: error.message });
  }
};