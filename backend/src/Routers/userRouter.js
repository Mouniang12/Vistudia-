const express = require("express");
const router = express.Router();
const userController = require("../Controllers/userController");
const path = require("path");


router.post("/register", userController.registerUser);
router.post("/login", userController.loginUser);
router.get("/profile", userController.getProfile);
router.post("/logout", userController.logoutUser);
router.get("/verify/:token", userController.verifyEmail);
router.post("/forgot-password", userController.forgotPassword);
router.get("/reset-password/:token", (req, res) => {
  res.sendFile(path.join(__dirname, "../views/reset-password.html"));
});

router.post("/reset-password/:token", userController.resetPassword);

module.exports = router;