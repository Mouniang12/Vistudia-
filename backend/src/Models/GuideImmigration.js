const mongoose = require("mongoose");

const EtapeSchema = new mongoose.Schema({
  ordre: { type: Number, required: true },
  titre: { type: String, required: true },
  description: { type: String, required: true },
  emoji: { type: String, default: "📌" },
  duree: { type: String },
  cout: { type: String },
  documents: [{ type: String }]
});

const GuideImmigrationSchema = new mongoose.Schema({
  paysOrigine: { type: String, required: true },
  paysDestination: { type: String, required: true },
  titre: { type: String, required: true },
  description: { type: String, required: true },
  dureeTotal: { type: String, required: true },
  coutTotal: { type: String, required: true },
  etapes: [EtapeSchema]
});

module.exports = mongoose.model("GuideImmigration", GuideImmigrationSchema);