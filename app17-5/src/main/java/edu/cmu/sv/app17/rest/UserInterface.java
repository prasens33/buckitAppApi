package edu.cmu.sv.app17.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.cmu.sv.app17.exceptions.APPBadRequestException;
import edu.cmu.sv.app17.exceptions.APPInternalServerException;
import edu.cmu.sv.app17.exceptions.APPNotFoundException;
import edu.cmu.sv.app17.models.Challenge;
import edu.cmu.sv.app17.models.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;


@Path("users")
public class UserInterface {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> challengeCollection;
    private ObjectWriter ow;


    public UserInterface() {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase database = mongoClient.getDatabase("buckitDB");

        this.collection = database.getCollection("users");
        this.challengeCollection = database.getCollection("challenges");
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    public ArrayList<User> getAll() {

        ArrayList<User> userList = new ArrayList<User>();

        FindIterable<Document> results = collection.find();
        if (results == null) {
            return  userList;
        }
        for (Document item : results) {
            User user = new User(
                    item.getString("firstName"),
                    item.getString("lastName"),
                    item.getString("emailAddress"),
                    item.getString("profilePictureLink")
            );
            user.setId(item.getObjectId("_id").toString());
            userList.add(user);
        }
        return userList;
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public User getOne(@PathParam("id") String id) {
        BasicDBObject query = new BasicDBObject();
        try {
            query.put("_id", new ObjectId(id));
            Document item = collection.find(query).first();
            if (item == null) {
                throw new APPNotFoundException(0, "No such challenge, my friend");
            }
            User user = new User(
                    item.getString("firstName"),
                    item.getString("lastName"),
                    item.getString("emailAddress"),
                    item.getString("profilePictureLink")
            );
            user.setId(item.getObjectId("_id").toString());
            return user;

        } catch(APPNotFoundException e) {
            throw new APPNotFoundException(0,"No such challenge");
        } catch(IllegalArgumentException e) {
            throw new APPBadRequestException(45,"Doesn't look like MongoDB ID");
        }  catch(Exception e) {
            throw new APPInternalServerException(99,"Something happened, pinch me!");
        }


    }

    @GET
    @Path("{id}/challenges")
    @Produces({MediaType.APPLICATION_JSON})
    public ArrayList<Challenge> getCarsForDriver(@PathParam("id") String id) {

        ArrayList<Challenge> challengeList = new ArrayList<Challenge>();

        try {
            BasicDBObject query = new BasicDBObject();
            query.put("userId", id);

            FindIterable<Document> results = challengeCollection.find(query);
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


    @POST
    @Path("{id}/challenges")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public Object create(@PathParam("id") String id, Object request) {
        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
        if (!json.has("challengeName"))
            throw new APPBadRequestException(55,"missing challengeName");
        if (!json.has("challengeDescription"))
            throw new APPBadRequestException(55,"missing challengeDescription");
        if (!json.has("challengeImageLink"))
            throw new APPBadRequestException(55,"missing challengeImageLink");

        Document doc = new Document("challengeName", json.getString("challengeName"))
                .append("challengeDescription", json.getString("challengeDescription"))
                .append("challengeImageLink", json.getString("challengeImageLink"))
                .append("userId", id);
        challengeCollection.insertOne(doc);
        return request;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public Object create(Object request) {

        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
        }
        catch (JsonProcessingException e) {
            throw new APPBadRequestException(33, e.getMessage());
        }
            if (!json.has("firstName") ) {
                throw new APPBadRequestException(55,"missing firstName");
            }
            // You need to add all other fields
            Document doc = new Document("firstName", json.getString("firstName"))
                    .append("lastName", json.getString("lastName"))
                    .append("emailAddress", json.getString("emailAddress"))
                    .append("profilePictureLink", json.getString("profilePictureLink"));
            collection.insertOne(doc);

        return request;
    }



}
