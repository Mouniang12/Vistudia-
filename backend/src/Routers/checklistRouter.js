const express = require("express");
const router = express.Router();
const ctrl = require("../Controllers/checklistController");
const path = require("path");

router.get("/destinations", ctrl.getDestinations);
router.get("/seed", ctrl.seedDestinations);
router.get("/historique/all", ctrl.getHistorique);
router.get("/mes-destinations", ctrl.getMesDestinations);
router.post("/mes-destinations", ctrl.ajouterDestination);
router.delete("/mes-destinations/:destinationId", ctrl.supprimerDestination);
router.post("/partage", ctrl.genererPartage);

router.get("/partage/:token", (req, res) => {
  res.sendFile(path.join(__dirname, "../views/checklist-partage.html"));
});

router.get("/partage-data/:token", ctrl.getChecklistPartage);
router.post("/partage/:token/:demarcheId", ctrl.cocherDemarchePartage);

router.get("/:destinationId", ctrl.getChecklist);
router.post("/:destinationId/:demarcheId", ctrl.cocherDemarche);

module.exports = router;