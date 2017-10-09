package edu.cmu.sv.app17.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import edu.cmu.sv.app17.exceptions.APPBadRequestException;
import edu.cmu.sv.app17.exceptions.APPInternalServerException;
import edu.cmu.sv.app17.exceptions.APPNotFoundException;
import edu.cmu.sv.app17.helpers.PATCH;
import edu.cmu.sv.app17.models.Car;
import edu.cmu.sv.app17.models.Challenge;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;



@Path("challenges")
public class ChallengeInterface {

    private MongoCollection<Document> collection = null;
    private ObjectWriter ow;

    public ChallengeInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("buckitDB");
        collection = database.getCollection("challenges");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public ArrayList<Challenge> getAll() {

        ArrayList<Challenge> challengeList = new ArrayList<Challenge>();

        try {
            FindIterable<Document> results = collection.find();
            for (Document item : results) {
                String challengeName = item.getString("challengeName");
                Challenge challenge = new Challenge(
                        challengeName,
                        item.getString("challengeDescription"),
                        item.getString("challengeImageLink"),
                        item.getString("userId")
                );
                challenge.setId(item.getObjectId("_id").toString());
                challengeList.add(challenge);
            }
            return challengeList;

        } catch(Exception e) {
            System.out.println("EXCEPTION!!!!");
            e.printStackTrace();
            throw new APPInternalServerException(99,e.getMessage());
        }

    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Challenge getOne(@PathParam("id") String id) {


        BasicDBObject query = new BasicDBObject();

        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No such challenge, my friend");
            }
            Challenge challenge  = new Challenge(
                    item.getString("challengeName"),
                    item.getString("challengeDescription"),
                    item.getString("challengeImageLink"),
                    item.getString("userId")
            );
            challenge.setId(item.getObjectId("_id").toString());
            return challenge;

        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }


    }

/*
    @PATCH
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public Object update(@PathParam("id") String id, Object request) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }

        try {

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            Document doc = new Document();
            if (json.has("make"))
                doc.append("make",json.getString("make"));
            if (json.has("model"))
                doc.append("model",json.getString("model"));
            if (json.has("color"))
                doc.append("color",json.getString("color"));
            if (json.has("size"))
                doc.append("size",json.getString("size"));
            if (json.has("year"))
                doc.append("year",json.getInt("year"));
            if (json.has("odometer"))
                doc.append("odometer",json.getString("odometer"));
            Document set = new Document("$set", doc);
            collection.updateOne(query,set);

        } catch(JSONException e) {
            System.out.println("Failed to create a document");

        }
        return request;
    }*/

/*
    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Object delete(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        DeleteResult deleteResult = collection.deleteOne(query);
        if (deleteResult.getDeletedCount() < 1)
            throw new APPNotFoundException(66,"Could not delete");

        return new JSONObject();
    }*/
}
