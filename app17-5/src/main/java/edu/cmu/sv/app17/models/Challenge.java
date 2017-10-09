package edu.cmu.sv.app17.models;

import java.awt.image.BufferedImage;

public class Challenge {
    String id = null;
    String challenegName, challengeDescription,challengeImageLink;

    String userId;

    public Challenge(String challenegName, String challengeDescription, String challengeImageLink, String userId) {
        this.challenegName = challenegName;
        this.challengeDescription = challengeDescription;
        this.challengeImageLink = challengeImageLink;
        this.userId = userId;

    }
    public void setId(String id) {
        this.id = id;
    }
}



