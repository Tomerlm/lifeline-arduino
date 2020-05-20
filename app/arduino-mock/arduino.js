const https = require("https");

const updatePatient = (id) => {
  let bpm = randInRange(60, 120);
  let oxi = randInRange(70, 100);
  let url = `https://us-central1-lifeline-aa56b.cloudfunctions.net/updatePatientParameters?id=${id}&bpm=${bpm}&oxi=${oxi}`;
  https
    .get(url, (resp) => {
      let data = "";

      // A chunk of data has been recieved.
      resp.on("data", (chunk) => {
        data += chunk;
      });

      // The whole response has been received. Print out the result.
      resp.on("end", () => {
        // console.log(JSON.parse(data));
      });
    })
    .on("error", (err) => {
      console.log("Error: " + err.message);
    });
};

const getPatientIdFromConnection = async (arduinoID, callback) => {
  let url = `https://us-central1-lifeline-aa56b.cloudfunctions.net/getConnectionDetails?arduinoID=${arduinoID}`;
  https
    .get(url, (resp) => {
      let data = "";

      // A chunk of data has been recieved.
      resp.on("data", (chunk) => {
        data += chunk;
      });

      // The whole response has been received. Print out the result.
      resp.on("end", () => {
        //console.log(JSON.parse(data));
        callback(JSON.parse(data));
      });
    })
    .on("error", (err) => {
      console.log("Error: " + err.message);
    });
};

function randInRange(min, max) {
  return Math.floor(Math.random() * (max - min) + min);
}

const loop = () => {
  getPatientIdFromConnection("a", (data) => {
    let connection = data.data;
    setInterval(async () => {
      getPatientIdFromConnection("a", async (data) => {
        connection = data.data;
        if (connection.manual) {
          console.log("ems is manualy controlling lifeline...");
          console.log(`respiration rate ${connection.rpm}`);
        } else {
          console.log("automatic control of lifeline");
        }
        console.log("updating vitals...");
        await updatePatient(connection.patientID);
      });
    }, 10000);
  });
};

loop();
