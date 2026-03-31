const mongoose = require("mongoose");

const DemarcheSchema = new mongoose.Schema({
  titre: { type: String, required: true },
  description: { type: String, required: true },
  type: { type: String, required: true },
});

module.exports.schema = DemarcheSchema;
module.exports = mongoose.model("Demarche", DemarcheSchema);