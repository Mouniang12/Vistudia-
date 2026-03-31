const mongoose = require("mongoose");

const DemarcheEffectueeSchema = new mongoose.Schema({
  demarcheId: { type: String, required: true },
  titreDemarche: { type: String, required: true },
  destinationNom: { type: String, required: true },
  dateEffectuee: { type: Date, default: Date.now },
  dateExpiration: { type: Date, default: null }
});

const ChecklistSchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "User",
    required: true
  },
  destinationId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Destination",
    required: true
  },
  demarchesEffectuees: [DemarcheEffectueeSchema],
  partageToken: { type: String, default: null },
  modePartage: { type: String, enum: ["lecture", "edition", null], default: null }
});

module.exports = mongoose.model("Checklist", ChecklistSchema);