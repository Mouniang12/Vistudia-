const express = require("express");
const router = express.Router();
const ctrl = require("../Controllers/documentController");

router.get("/", ctrl.getMesDocuments);
router.post("/", ctrl.ajouterDocument);
router.put("/:docId", ctrl.modifierDocument);
router.delete("/:docId", ctrl.supprimerDocument);

module.exports = router;
