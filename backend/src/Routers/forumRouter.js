const express = require('express');
const router = express.Router();
const forumController = require('../Controllers/forumController');

router.get('/salons', forumController.getSalons);
router.post('/salons', forumController.createSalon);
router.get('/salons/:salonId/messages', forumController.getMessages);
router.post('/salons/:salonId/messages', forumController.postMessage);

module.exports = router;