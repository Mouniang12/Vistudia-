const User = require("../Models/User");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const crypto = require("crypto");
const { sendVerificationEmail, sendResetPasswordEmail } = require("../Services/emailService");

exports.updateProfile = async (req, res) => {
  try {
    const token = req.cookies.token;
    if (!token) return res.status(401).json({ message: "Non authentifié" });

    const decoded = jwt.verify(token, process.env.JWT_SECRET);

    const {
      prenom, nom, telephone,
      nationalite, dateNaissance,
      bio, paysOrigine, paysDestination
    } = req.body;

    const user = await User.findByIdAndUpdate(
      decoded.id,
      {
        prenom, nom, telephone,
        nationalite, dateNaissance,
        bio, paysOrigine, paysDestination
      },
      { new: true }
    ).select("-password");

    res.json({ message: "Profil mis à jour !", user });
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

exports.changePassword = async (req, res) => {
  try {
    const token = req.cookies.token;
    if (!token) return res.status(401).json({ message: "Non authentifié" });

    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    const { ancienPassword, nouveauPassword } = req.body;

    const user = await User.findById(decoded.id);
    const valide = await bcrypt.compare(ancienPassword, user.password);

    if (!valide) {
      return res.status(400).json({ message: "Ancien mot de passe incorrect" });
    }

    const salt = await bcrypt.genSalt(10);
    user.password = await bcrypt.hash(nouveauPassword, salt);
    await user.save();

    res.json({ message: "Mot de passe modifié !" });
  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

exports.registerUser = async (req, res) => {
  try {
    const { prenom, nom, email, password, paysOrigine } = req.body;

    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(400).json({ message: "Utilisateur déjà existant" });
    }

    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);

    // Générer un token de vérification unique
    const verificationToken = crypto.randomBytes(32).toString("hex");

    const user = new User({
      prenom,
      nom,
      email,
      password: hashedPassword,
      isVerified: false,
      verificationToken,
      paysOrigine: paysOrigine || ""
    });

    await user.save();

    // Envoyer l'email de confirmation
    await sendVerificationEmail(email, verificationToken);

    res.status(201).json({
      message: "Compte créé ! Vérifiez votre email pour activer votre compte."
    });

  } catch (error) {
    res.status(500).json({ message: "Erreur serveur", error: error.message });
  }
};

exports.verifyEmail = async (req, res) => {
  try {
    const { token } = req.params;

    const user = await User.findOne({ verificationToken: token });

    if (!user) {
      return res.status(400).send("<h2>Lien invalide ou expiré.</h2>");
    }

    user.isVerified = true;
    user.verificationToken = undefined;
    await user.save();

    res.send("<h2>✅ Email confirmé ! Vous pouvez maintenant vous connecter.</h2>");

  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

exports.loginUser = async (req, res) => {
  try {
    const { email, password, rememberMe } = req.body;

    const user = await User.findOne({ email });
    if (!user) {
      return res.status(400).json({ message: "Utilisateur non trouvé" });
    }

    if (!user.isVerified) {
      return res.status(403).json({ message: "Veuillez confirmer votre email avant de vous connecter." });
    }

    const validPassword = await bcrypt.compare(password, user.password);
    if (!validPassword) {
      return res.status(400).json({ message: "Mot de passe incorrect" });
    }

    // Durée selon "se souvenir de moi"
    const expiresIn = rememberMe ? "30d" : "1d";
    const maxAge = rememberMe
      ? 30 * 24 * 60 * 60 * 1000  // 30 jours
      : 24 * 60 * 60 * 1000;       // 1 jour

    const token = jwt.sign(
      { id: user._id },
      process.env.JWT_SECRET,
      { expiresIn }
    );

    res.cookie("token", token, {
      httpOnly: true,
      secure: false,
      sameSite: "lax",
      maxAge
    });

    res.json({
      message: "Connexion réussie",
      user: { id: user._id, prenom: user.prenom, email: user.email }
    });

  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

exports.getProfile = async (req, res) => {
  try {
    const token = req.cookies.token;
    if (!token) {
      return res.status(401).json({ message: "Non authentifié" });
    }

    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    const user = await User.findById(decoded.id).select("-password");
    res.json(user);

  } catch (error) {
    res.status(401).json({ message: "Token invalide" });
  }
};

exports.logoutUser = (req, res) => {
  res.clearCookie("token");
  res.json({ message: "Déconnexion réussie" });
};

exports.forgotPassword = async (req, res) => {
  try {
    const { email } = req.body;

    const user = await User.findOne({ email });
    if (!user) {
      return res.status(404).json({ message: "Aucun compte associé à cet email" });
    }

    const resetToken = crypto.randomBytes(32).toString("hex");
    user.resetPasswordToken = resetToken;
    user.resetPasswordExpires = Date.now() + 3600000; // 1 heure
    await user.save();

    await sendResetPasswordEmail(email, resetToken);

    res.json({ message: "Email de réinitialisation envoyé !" });

  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};

exports.resetPassword = async (req, res) => {
  try {
    const { token } = req.params;
    const { password } = req.body;

    const user = await User.findOne({
      resetPasswordToken: token,
      resetPasswordExpires: { $gt: Date.now() }
    });

    if (!user) {
      return res.status(400).send("<h2>Lien invalide ou expiré.</h2>");
    }

    const salt = await bcrypt.genSalt(10);
    user.password = await bcrypt.hash(password, salt);
    user.resetPasswordToken = undefined;
    user.resetPasswordExpires = undefined;
    await user.save();

    res.send(`
      <h2>✅ Mot de passe réinitialisé avec succès !</h2>
      <p>Vous pouvez maintenant vous connecter avec votre nouveau mot de passe.</p>
    `);

  } catch (error) {
    res.status(500).json({ message: "Erreur serveur" });
  }
};