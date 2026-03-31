const mongoose = require("mongoose");

const DocumentSchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "User",
    required: true
  },
  titre: { type: String, required: true },
  description: { type: String, default: "" },
  dateExpiration: { type: Date, required: true },
  notificationEnvoyee: { type: Boolean, default: false },
  dateAjout: { type: Date, default: Date.now }
});

module.exports = mongoose.model("Document", DocumentSchema);
