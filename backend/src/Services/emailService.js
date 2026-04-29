const nodemailer = require("nodemailer");

const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user: process.env.EMAIL_USER,
    pass: process.env.EMAIL_PASS
  }
});

exports.sendVerificationEmail = async (email, token) => {
  const url = `http://localhost:3000/api/users/verify/${token}`;

  try {
    await transporter.sendMail({
      from: `"Vistudia" <${process.env.EMAIL_USER}>`,
      to: email,
      subject: "Confirmez votre adresse email",
      html: `
        <h2>Bienvenue sur Vistudia !</h2>
        <p>Cliquez sur le lien ci-dessous pour confirmer votre email :</p>
        <a href="${url}" style="
          background-color: #FF6600;
          color: white;
          padding: 12px 24px;
          text-decoration: none;
          border-radius: 8px;
        ">Confirmer mon email</a>
        <p>Ce lien expire dans 24h.</p>
      `
    });
    console.log("✅ Email envoyé à :", email);
  } catch (error) {
    console.error("❌ Erreur envoi email :", error.message);
    console.log("\n=======================================================");
    console.log(`📧 Simulation d'envoi d'email à : ${email}`);
    console.log(`🔗 LIEN DE VÉRIFICATION : ${url}`);
    console.log("=======================================================\n");
  }
};


exports.sendResetPasswordEmail = async (email, token) => {
  const url = `http://localhost:3000/api/users/reset-password/${token}`;

  console.log("\n=======================================================");
  console.log(`🔑 Simulation d'email de RESET pour : ${email}`);
  console.log(`🔗 LIEN DE RESET : ${url}`);
  console.log("=======================================================\n");

  try {
    await transporter.sendMail({
      from: `"Vistudia" <${process.env.EMAIL_USER}>`,
      to: email,
      subject: "Réinitialisation de votre mot de passe",
      html: `
        <h2>Réinitialisation du mot de passe</h2>
        <p>Vous avez demandé à réinitialiser votre mot de passe.</p>
        <p>Cliquez sur le bouton ci-dessous. Ce lien expire dans <b>1 heure</b>.</p>
        <a href="${url}" style="
          background-color: #FF6600;
          color: white;
          padding: 12px 24px;
          text-decoration: none;
          border-radius: 8px;
          display: inline-block;
          margin-top: 12px;
        ">Réinitialiser mon mot de passe</a>
        <p style="color: gray; margin-top: 16px;">
          Si vous n'avez pas fait cette demande, ignorez cet email.
        </p>
      `
    });
    console.log("✅ Email reset envoyé à :", email);
  } catch (error) {
    console.error("❌ Erreur envoi email reset :", error.message);
    console.log("\n=======================================================");
    console.log(`📧 Simulation d'envoi d'email à : ${email}`);
    console.log(`🔗 LIEN DE VÉRIFICATION : ${url}`);
    console.log("=======================================================\n");
  }
};