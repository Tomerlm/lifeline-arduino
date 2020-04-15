const https = require('https')

const loop = () => {
    setInterval(async () =>{
        console.log("updating...")
        await updatePatient()
    }, 1000)
}

loop()


const updatePatient = () => {
    let bpm = randInRange(60, 120);
    let oxi = randInRange(70, 100);
    let id = 'FzGgf7uPQBdMUKfofbvc'
    let url = `https://us-central1-lifeline-aa56b.cloudfunctions.net/updatePatientParameters?id=${id}&bpm=${bpm}&oxi=${oxi}`
    https.get(url, (resp) => {
      let data = '';
    
      // A chunk of data has been recieved.
      resp.on('data', (chunk) => {
        data += chunk;
      });
    
      // The whole response has been received. Print out the result.
      resp.on('end', () => {
        console.log(JSON.parse(data));
      });
    
    }).on("error", (err) => {
      console.log("Error: " + err.message);
    });
}

function randInRange(min, max) {  
    return Math.floor(
      Math.random() * (max - min) + min
    )
  }

