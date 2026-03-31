const mongoose = require("mongoose");

const UserDestinationSchema = new mongoose.Schema({
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
  dateAjout: { type: Date, default: Date.now }
});

module.exports = mongoose.model("UserDestination", UserDestinationSchema);