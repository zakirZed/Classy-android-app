const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotificationToTopic = functions.firestore.document('Classes/{cid}/{Routine}/{rid}').onUpdate(async (event) => {
    //let docID = event.after.id;
    const Topic = event.after.get('classId');
    const title = event.after.get('status');
    const subName = event.after.get('subjectName');
    const classDate = event.after.get('docId');
    const flag = event.after.get('inList');
    const shiftedDate = event.after.get('shiftedDate');
   if(shiftedDate!==null&&!flag){
        let message = {
            notification: {
                title: 'Class '+title,
                body: 'Your class, '+subName+' is '+title+' from '+classDate+' '+shiftedDate+'. Please take a look.',
            },
            topic: Topic,
        };
        let response = await admin.messaging().send(message);
        console.log(response);
    }else if((title.localeCompare("Cancelled")===0)&&!flag){
        let message = {
            notification: {
                title: 'Class '+title,
                body: 'Your class, '+subName+', '+classDate+' is '+title+'. Please take a look.',
            },
            topic: Topic,
        };
        let response = await admin.messaging().send(message);
        console.log(response);
    }else if((title.localeCompare("Live")===0)&&!flag){
        let message = {
            notification: {
                title: 'Class is '+title+' now',
                body: 'Your class, '+subName+', '+classDate+' is '+title+'. Please join class.',
            },
            topic: Topic,
        };
        let response = await admin.messaging().send(message);
        console.log(response);
    }
    

   
});
