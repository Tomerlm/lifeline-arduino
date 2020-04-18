import * as functions from 'firebase-functions';

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

 const PATIENTS_COLLECTION = "patients";

exports.updatePatientParameters = functions.https.onRequest(async(req, res) => {
    const bpm = parseInt(req.query.bpm as any);
    const oxi = parseInt(req.query.oxi as any);
    const patientID = req.query.id;

    const writeResult = await admin.firestore().collection(PATIENTS_COLLECTION).doc(patientID).update({bpm: bpm, oxygenPercentage: oxi });
    res.json({result: writeResult});
});