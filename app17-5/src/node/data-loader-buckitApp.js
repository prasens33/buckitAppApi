var MongoClient = require('mongodb').MongoClient;

var dbConnection = null;

var lockCount = 0;



function getDbConnection(callback){
    MongoClient.connect("mongodb://localhost/buckitDB", function(err, db){
        if(err){
            console.log("Unable to connect to Mongodb");
        }else{
            dbConnection = db;
            callback();
        }
    });
};

function closeConnection() {
    if (dbConnection)
        dbConnection.close();

}

getDbConnection(function(){
    dbConnection.dropDatabase(function(err,doc){
        if (err)
            console.log("Could not drop database");
        else
            addUser();
    });
});

function addUser() {
    d = [{
        "firstName":    "Kevin",
        "lastName":     "Peet",
        "emailAddress":  "kevin@peet.com",
        "profilePictureLink" : "http://www.whatsappstatus77.in/wp-content/uploads/2015/07/awesome-boys-profile-photos-pics-for-facebook-wall-whatsapp-dp.jpg"
    },
        {
            "firstName":    "Robert",
            "lastName":     "Redford",
            "emailAddress":        "robert@redford.com",
            "profilePictureLink" : "http://sguru.org/wp-content/uploads/2017/04/girl-profile-picture-for-facebook-1-1.jpg"
        }];

    var users = dbConnection.collection('users');
    users.insertOne(d[0], function(err,doc){
        if (err){
            console.log("Could not add user 1");
        }
        else {
            addChallengestoUser0(doc.ops[0]._id.toString());
        }
    })
    users.insertOne(d[1], function(err,doc){
        if (err){
            console.log("Could not add user 1");
        }
        else {
            addChallengestoUser1(doc.ops[0]._id.toString());
        }
    })
}

function addChallengestoUser0(userId) {
    c = [{
        "challengeName" : "SelfieChallenge",
        "challengeDescription" : "Take selfie.",
        "challengeImageLink" : "https://bestcellphonespyapps.com/wp-content/uploads/2017/06/woman-taking-selfie.jpg",
        "userId" : userId
    },{
        "challengeName" : "FlyHigh",
        "challengeDescription" : "Sky diving challenge.",
        "challengeImageLink" : "https://img.grouponcdn.com/deal/9cPfwCFsgGxMTyNckks7QH/tennessee_skydiving_llc-2048x1229.jpg/v1/c700x420.jpg",
        "userId" : userId
    },{
        "challengeName" : "CodeChallenge",
        "challengeDescription" : "Write a code",
        "challengeImageLink" : "https://warroom.securestate.com/wp-content/uploads/2016/10/coding.jpg",
        "userId" : userId
    }];
    c.forEach(function(challenge){
        var challenges = dbConnection.collection('challenges');
        challenges.insertOne(challenge);
    })

}

function addChallengestoUser1(userId) {
    c = [{
        "challengeName" : "Fun challenge",
        "challengeDescription" : "Play ps4",
        "challengeImageLink" : "http://www.spokeslabs.com/jstone/ps4_images/ps4-hrdware-large18.jpg",
        "userId" : userId
    },{
        "challengeName" : "Scary Challenge",
        "challengeDescription" : "Watch a horror movie.",
        "challengeImageLink" : "https://cdn.empireonline.com/jpg/80/0/0/1000/563/0/north/0/0/0/0/0/c/features/57dc2b120c6437272f5f2ad4/Scream.jpg",
        "userId" : userId
    }];
    c.forEach(function(challenge){
        var challenges = dbConnection.collection('challenges');
        challenges.insertOne(challenge);
    })

}

setTimeout(closeConnection,5000);