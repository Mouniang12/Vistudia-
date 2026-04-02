const mongoose = require("mongoose");

const UserSchema = new mongoose.Schema({
  prenom: { type: String, required: true },
  nom: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  isVerified: { type: Boolean, default: false },
  verificationToken: { type: String },
  resetPasswordToken: { type: String },
  resetPasswordExpires: { type: Date },

  telephone: { type: String, default: "" },
  nationalite: { type: String, default: "" },
  dateNaissance: { type: Date, default: null },
  bio: { type: String, default: "" },
  paysOrigine: { type: String, default: "" },
  paysDestination: { type: String, default: "" },
  dateCreation: { type: Date, default: Date.now }
});

module.exports = mongoose.model("User", UserSchema);