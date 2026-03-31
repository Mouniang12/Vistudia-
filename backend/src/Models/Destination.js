const mongoose = require("mongoose");
const demarcheSchema = require("./Demarche").schema;



const DestinationSchema = new mongoose.Schema({
  nom: { type: String, required: true },
  pays: { type: String, required: true },
  demarches: [demarcheSchema]
});

module.exports = mongoose.model("Destination", DestinationSchema);