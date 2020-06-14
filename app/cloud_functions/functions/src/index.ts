import * as functions from "firebase-functions";

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require("firebase-admin");
admin.initializeApp();

const PATIENTS_COLLECTION = "patients";
const USERS_COLLECTION = "users";
const CONNECTIONS_COLLECTION = "connections";
const SENSORS_COLLECTION = "sensors";

exports.getConnectionDetails = functions.https.onRequest(async (req, res) => {
  const ardID = req.query.arduinoID as string;
  let data: any;
  await admin
    .firestore()
    .collection(CONNECTIONS_COLLECTION)
    .doc(ardID)
    .get()
    .then((doc: any) => {
      if (!doc.exists) {
        console.log("No such document!");
      } else {
        console.log("Document data:", doc.data());
        data = doc.data();
        res.json({ data });
      }
    })
    .catch((err: any) => {
      console.log("Error getting document", err);
      res.json({ err });
    });
});

exports.getPatient = functions.https.onRequest(async (req, res) => {
  const id = req.query.patientID as string;
  let data: any;
  await admin
    .firestore()
    .collection(PATIENTS_COLLECTION)
    .doc(id)
    .get()
    .then((doc: any) => {
      if (!doc.exists) {
        console.log("No such document!");
      } else {
        console.log("Document data:", doc.data());
        data = doc.data();
        res.json({ data });
      }
    })
    .catch((err: any) => {
      console.log("Error getting document", err);
      res.json({ err });
    });
});

exports.updatePatientParameters = functions.https.onRequest(
  async (req, res) => {
    const bpm = parseInt(req.query.bpm as any);
    const oxi = parseInt(req.query.oxi as any);
    const patientID = req.query.id;

    const writeResult = await admin
      .firestore()
      .collection(PATIENTS_COLLECTION)
      .doc(patientID)
      .update({ bpm: bpm, oxygenPercentage: oxi });
    res.json({ result: writeResult });
  }
);

exports.updateSensor = functions.https.onRequest(async (req, res) => {
  const bpm = parseInt(req.query.bpm as any);
  const oxi = parseInt(req.query.oxi as any);
  const sensorID = "sensor";

  const writeResult = await admin
    .firestore()
    .collection(SENSORS_COLLECTION)
    .doc(sensorID)
    .update({ bpm: bpm, oxygenPercentage: oxi });
  res.json({ result: writeResult });
});

exports.createHospitals = functions.https.onRequest(async (req, res) => {
  /*
        private String email;
    private boolean isEms;
    private String username;
    private String hospitalName = null;
    private String arduinoID = null;
    private String currentPatient;*/
  const hospitals = [
    {
      email: "rambam@email.com",
      username: "Dr. Yossi",
      hospitalName: "Rambam",
      isEms: false,
    },
    {
      email: "carmel@email.com",
      username: "Dr. Yossi",
      hospitalName: "Carmel",
      isEms: false,
    },
  ];
  let docRef;
  let id;
  for (let hospital of hospitals) {
    docRef = admin.firestore().collection(USERS_COLLECTION).doc();
    id = docRef.id;
    await admin.firestore().collection(USERS_COLLECTION).doc(id).set(hospital);
  }
  res.json({ result: "Created Hospitals" });
});

exports.updatePatients = functions.https.onRequest(async (req, res) => {
  let patientsRef = await admin.firestore().collection(PATIENTS_COLLECTION);
  let query = patientsRef
    .get()
    .then((snapshot: any) => {
      if (snapshot.empty) {
        console.log("No matching documents.");
        return;
      }

      snapshot.forEach((doc: any) => {
        admin
          .firestore()
          .collection(PATIENTS_COLLECTION)
          .doc(doc.id)
          .update({ treatmentStatus: "COMPLETED" });
      });
      res.json({ result: "updated patients" });
    })
    .catch((err: any) => {
      console.log("Error getting documents", err);
      console.log(query);
    });
});

exports.setAllCompleted = functions.firestore
  .document(`${PATIENTS_COLLECTION}/{patientId}`)
  .onCreate(async (change, context) => {
    let patientsRef = await admin.firestore().collection(PATIENTS_COLLECTION);
    let newId = context.params.patientId;
    let query = patientsRef
      .get()
      .then((snapshot: any) => {
        if (snapshot.empty) {
          console.log("No matching documents.");
          return;
        }

        snapshot.forEach((doc: any) => {
          if (doc.id !== newId) {
            console.log(`updating: ${doc.id}`);
            admin
              .firestore()
              .collection(PATIENTS_COLLECTION)
              .doc(doc.id)
              .update({ treatmentStatus: "COMPLETED" });
          }
        });
      })
      .catch((err: any) => {
        console.log("Error getting documents", err);
        console.log(query);
      });
  });
