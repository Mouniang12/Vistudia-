const express = require("express");
const router = express.Router();
const ctrl = require("../Controllers/guideImmigrationController");

router.get("/seed", ctrl.seedGuides);
router.get("/pays-origine", ctrl.getPaysOrigine);
router.get("/destinations/:paysOrigine", ctrl.getDestinations);
router.get("/guide/:guideId", ctrl.getGuide);

module.exports = router;